package net.minecraft.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.INpc;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.VillageCollection;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.BonusChestFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SessionLockException;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataCallableSave;
import net.minecraft.world.storage.WorldSavedDataStorage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldServer extends World implements IThreadListener, net.minecraftforge.common.extensions.IForgeWorldServer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final MinecraftServer server;
   /** The entity tracker for this server world. */
   private final EntityTracker entityTracker;
   /** The player chunk map for this server world. */
   private final PlayerChunkMap playerChunkMap;
   private final Map<UUID, Entity> entitiesByUuid = Maps.newHashMap();
   /** Whether level saving is disabled or not */
   public boolean disableLevelSaving;
   /** is false if there are no players */
   private boolean allPlayersSleeping;
   private int updateEntityTick;
   /** the teleporter to use when the entity is being transferred into the dimension */
   private final Teleporter worldTeleporter;
   private final WorldEntitySpawner entitySpawner = new WorldEntitySpawner();
   private final ServerTickList<Block> pendingBlockTicks = new ServerTickList<>(this, (p_205341_0_) -> {
      return p_205341_0_ == null || p_205341_0_.getDefaultState().isAir();
   }, IRegistry.field_212618_g::getKey, IRegistry.field_212618_g::get, this::tickBlock);
   private final ServerTickList<Fluid> pendingFluidTicks = new ServerTickList<>(this, (p_205774_0_) -> {
      return p_205774_0_ == null || p_205774_0_ == Fluids.EMPTY;
   }, IRegistry.field_212619_h::getKey, IRegistry.field_212619_h::get, this::tickFluid);
   protected final VillageSiege villageSiege = new VillageSiege(this);
   ObjectLinkedOpenHashSet<BlockEventData> blockEventQueue = new ObjectLinkedOpenHashSet<>();
   private boolean insideTick;
   /** Stores the recently processed (lighting) chunks */
   protected java.util.Set<ChunkPos> doneChunks = new java.util.HashSet<ChunkPos>();
   public List<Teleporter> customTeleporters = new java.util.ArrayList<Teleporter>();

   public WorldServer(MinecraftServer p_i49819_1_, ISaveHandler p_i49819_2_, WorldSavedDataStorage p_i49819_3_, WorldInfo p_i49819_4_, DimensionType p_i49819_5_, Profiler p_i49819_6_) {
      super(p_i49819_2_, p_i49819_3_, p_i49819_4_, p_i49819_5_.create(), p_i49819_6_, false);
      this.server = p_i49819_1_;
      this.entityTracker = new EntityTracker(this);
      this.playerChunkMap = new PlayerChunkMap(this);
      this.dimension.setWorld(this);
      this.chunkProvider = this.createChunkProvider();
      this.worldTeleporter = new Teleporter(this);
      this.calculateInitialSkylight();
      this.calculateInitialWeather();
      this.getWorldBorder().setSize(p_i49819_1_.getMaxWorldSize());
   }

   public WorldServer func_212251_i__() {
      String s = VillageCollection.fileNameForProvider(this.dimension);
      DimensionType key = getDimension().getType().isVanilla() ? DimensionType.OVERWORLD : getDimension().getType();
      VillageCollection villagecollection = this.func_212411_a(key, VillageCollection::new, s);
      if (villagecollection == null) {
         this.villageCollection = new VillageCollection(this);
         this.func_212409_a(key, s, this.villageCollection);
      } else {
         this.villageCollection = villagecollection;
         this.villageCollection.setWorldsForAll(this);
      }

      ScoreboardSaveData scoreboardsavedata = this.func_212411_a(getDimension().getType(), ScoreboardSaveData::new, "scoreboard");
      if (scoreboardsavedata == null) {
         scoreboardsavedata = new ScoreboardSaveData();
         this.func_212409_a(getDimension().getType(), "scoreboard", scoreboardsavedata);
      }

      scoreboardsavedata.setScoreboard(this.server.getWorldScoreboard());
      this.server.getWorldScoreboard().addDirtyRunnable(new WorldSavedDataCallableSave(scoreboardsavedata));
      this.getWorldBorder().setCenter(this.worldInfo.getBorderCenterX(), this.worldInfo.getBorderCenterZ());
      this.getWorldBorder().setDamageAmount(this.worldInfo.getBorderDamagePerBlock());
      this.getWorldBorder().setDamageBuffer(this.worldInfo.getBorderSafeZone());
      this.getWorldBorder().setWarningDistance(this.worldInfo.getBorderWarningDistance());
      this.getWorldBorder().setWarningTime(this.worldInfo.getBorderWarningTime());
      if (this.worldInfo.getBorderLerpTime() > 0L) {
         this.getWorldBorder().setTransition(this.worldInfo.getBorderSize(), this.worldInfo.getBorderLerpTarget(), this.worldInfo.getBorderLerpTime());
      } else {
         this.getWorldBorder().setTransition(this.worldInfo.getBorderSize());
      }

      this.initCapabilities();
      return this;
   }

   /**
    * Runs a single tick for the world
    */
   public void tick(BooleanSupplier p_72835_1_) {
      this.insideTick = true;
      super.tick(p_72835_1_);
      if (this.getWorldInfo().isHardcore() && this.getDifficulty() != EnumDifficulty.HARD) {
         this.getWorldInfo().setDifficulty(EnumDifficulty.HARD);
      }

      this.chunkProvider.getChunkGenerator().getBiomeProvider().tick();
      if (this.areAllPlayersAsleep()) {
         if (this.getGameRules().getBoolean("doDaylightCycle")) {
            long i = this.getDayTime() + 24000L;
            this.setDayTime(i - i % 24000L);
         }

         this.wakeAllPlayers();
      }

      this.profiler.startSection("spawner");
      if (this.getGameRules().getBoolean("doMobSpawning") && this.worldInfo.getTerrainType() != WorldType.DEBUG_ALL_BLOCK_STATES) {
         this.entitySpawner.findChunksForSpawning(this, this.spawnHostileMobs, this.spawnPeacefulMobs, this.worldInfo.getGameTime() % 400L == 0L);
         this.getChunkProvider().spawnMobs(this, this.spawnHostileMobs, this.spawnPeacefulMobs);
      }

      this.profiler.endStartSection("chunkSource");
      this.chunkProvider.tick(p_72835_1_);
      int j = this.calculateSkylightSubtracted(1.0F);
      if (j != this.getSkylightSubtracted()) {
         this.setSkylightSubtracted(j);
      }

      this.worldInfo.setWorldTotalTime(this.worldInfo.getGameTime() + 1L);
      if (this.getGameRules().getBoolean("doDaylightCycle")) {
         this.setDayTime(this.getDayTime() + 1L);
      }

      this.profiler.endStartSection("tickPending");
      this.tickPending();
      this.profiler.endStartSection("tickBlocks");
      this.tickBlocks();
      this.profiler.endStartSection("chunkMap");
      this.playerChunkMap.tick();
      this.profiler.endStartSection("village");
      this.villageCollection.tick();
      this.villageSiege.tick();
      this.profiler.endStartSection("portalForcer");
      this.worldTeleporter.removeStalePortalLocations(this.getGameTime());
      customTeleporters.forEach(t -> t.removeStalePortalLocations(getGameTime()));
      this.profiler.endSection();
      this.sendQueuedBlockEvents();
      this.insideTick = false;
   }

   public boolean isInsideTick() {
      return this.insideTick;
   }

   @Nullable
   public Biome.SpawnListEntry getSpawnListEntryForTypeAt(EnumCreatureType creatureType, BlockPos pos) {
      List<Biome.SpawnListEntry> list = this.getChunkProvider().getPossibleCreatures(creatureType, pos);
      list = net.minecraftforge.event.ForgeEventFactory.getPotentialSpawns(this, creatureType, pos, list);
      return list.isEmpty() ? null : WeightedRandom.getRandomItem(this.rand, list);
   }

   public boolean canCreatureTypeSpawnHere(EnumCreatureType creatureType, Biome.SpawnListEntry spawnListEntry, BlockPos pos) {
      List<Biome.SpawnListEntry> list = this.getChunkProvider().getPossibleCreatures(creatureType, pos);
      list = net.minecraftforge.event.ForgeEventFactory.getPotentialSpawns(this, creatureType, pos, list);
      return list != null && !list.isEmpty() ? list.contains(spawnListEntry) : false;
   }

   /**
    * Updates the flag that indicates whether or not all players in the world are sleeping.
    */
   public void updateAllPlayersSleepingFlag() {
      this.allPlayersSleeping = false;
      if (!this.playerEntities.isEmpty()) {
         int i = 0;
         int j = 0;

         for(EntityPlayer entityplayer : this.playerEntities) {
            if (entityplayer.isSpectator()) {
               ++i;
            } else if (entityplayer.isPlayerSleeping()) {
               ++j;
            }
         }

         this.allPlayersSleeping = j > 0 && j >= this.playerEntities.size() - i;
      }

   }

   public ServerScoreboard getScoreboard() {
      return this.server.getWorldScoreboard();
   }

   protected void wakeAllPlayers() {
      this.allPlayersSleeping = false;

      for(EntityPlayer entityplayer : this.playerEntities.stream().filter(EntityPlayer::isPlayerSleeping).collect(Collectors.toList())) {
         entityplayer.wakeUpPlayer(false, false, true);
      }

      if (this.getGameRules().getBoolean("doWeatherCycle")) {
         this.resetRainAndThunder();
      }

   }

   /**
    * Clears the current rain and thunder weather states.
    */
   private void resetRainAndThunder() {
      this.dimension.resetRainAndThunder();
   }

   /**
    * Checks if all players in this world are sleeping.
    */
   public boolean areAllPlayersAsleep() {
      if (this.allPlayersSleeping && !this.isRemote) {
         for(EntityPlayer entityplayer : this.playerEntities) {
            if (!entityplayer.isSpectator() && !entityplayer.isPlayerFullyAsleep()) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   /**
    * Sets a new spawn location by finding an uncovered block at a random (x,z) location in the chunk.
    */
   @OnlyIn(Dist.CLIENT)
   public void setInitialSpawnLocation() {
      if (this.worldInfo.getSpawnY() <= 0) {
         this.worldInfo.setSpawnY(this.getSeaLevel() + 1);
      }

      int i = this.worldInfo.getSpawnX();
      int j = this.worldInfo.getSpawnZ();
      int k = 0;

      while(this.getGroundAboveSeaLevel(new BlockPos(i, 0, j)).isAir(this, new BlockPos(i, 0, j))) {
         i += this.rand.nextInt(8) - this.rand.nextInt(8);
         j += this.rand.nextInt(8) - this.rand.nextInt(8);
         ++k;
         if (k == 10000) {
            break;
         }
      }

      this.worldInfo.setSpawnX(i);
      this.worldInfo.setSpawnZ(j);
   }

   public boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
      return this.isChunkLoaded(x, z);
   }

   public boolean isChunkLoaded(int p_201697_1_, int p_201697_2_) {
      return this.getChunkProvider().chunkExists(p_201697_1_, p_201697_2_);
   }

   protected void playerCheckLight() {
      this.profiler.startSection("playerCheckLight");
      if (!this.playerEntities.isEmpty()) {
         int i = this.rand.nextInt(this.playerEntities.size());
         EntityPlayer entityplayer = this.playerEntities.get(i);
         int j = MathHelper.floor(entityplayer.posX) + this.rand.nextInt(11) - 5;
         int k = MathHelper.floor(entityplayer.posY) + this.rand.nextInt(11) - 5;
         int l = MathHelper.floor(entityplayer.posZ) + this.rand.nextInt(11) - 5;
         this.checkLight(new BlockPos(j, k, l));
      }

      this.profiler.endSection();
   }

   protected void tickBlocks() {
      this.playerCheckLight();
      if (this.worldInfo.getTerrainType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
         Iterator<Chunk> iterator1 = this.playerChunkMap.getChunkIterator();

         while(iterator1.hasNext()) {
            iterator1.next().tick(false);
         }

      } else {
         int i = this.getGameRules().getInt("randomTickSpeed");
         boolean flag = this.isRaining();
         boolean flag1 = this.isThundering();
         this.profiler.startSection("pollingChunks");

         //Forge: Tick forced loaded chunks as well as normal chunks.
         Stream<Chunk> chunks = Stream.concat(
            java.util.stream.StreamSupport.stream(java.util.Spliterators.spliteratorUnknownSize(this.playerChunkMap.getChunkIterator(), 0), false),
            func_212412_ag().stream().map(l -> getChunk(ChunkPos.func_212578_a(l), ChunkPos.func_212579_b(l)))
         ).distinct();// We need distinct so we don't double tick chunks.

         for(Iterator<Chunk> iterator = chunks.iterator(); iterator.hasNext(); this.profiler.endSection()) {
            this.profiler.startSection("getChunk");
            Chunk chunk = iterator.next();
            int j = chunk.x * 16;
            int k = chunk.z * 16;
            this.profiler.endStartSection("checkNextLight");
            chunk.enqueueRelightChecks();
            this.profiler.endStartSection("tickChunk");
            chunk.tick(false);
            this.profiler.endStartSection("thunder");
            if (this.dimension.canDoLightning(chunk) && flag && flag1 && this.rand.nextInt(100000) == 0) {
               this.updateLCG = this.updateLCG * 3 + 1013904223;
               int l = this.updateLCG >> 2;
               BlockPos blockpos = this.adjustPosToNearbyEntity(new BlockPos(j + (l & 15), 0, k + (l >> 8 & 15)));
               if (this.isRainingAt(blockpos)) {
                  DifficultyInstance difficultyinstance = this.getDifficultyForLocation(blockpos);
                  boolean flag2 = this.getGameRules().getBoolean("doMobSpawning") && this.rand.nextDouble() < (double)difficultyinstance.getAdditionalDifficulty() * 0.01D;
                  if (flag2) {
                     EntitySkeletonHorse entityskeletonhorse = new EntitySkeletonHorse(this);
                     entityskeletonhorse.setTrap(true);
                     entityskeletonhorse.setGrowingAge(0);
                     entityskeletonhorse.setPosition((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
                     this.spawnEntity(entityskeletonhorse);
                  }

                  this.addWeatherEffect(new EntityLightningBolt(this, (double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D, flag2));
               }
            }

            this.profiler.endStartSection("iceandsnow");
            if (this.dimension.canDoRainSnowIce(chunk) && this.rand.nextInt(16) == 0) {
               this.updateLCG = this.updateLCG * 3 + 1013904223;
               int i2 = this.updateLCG >> 2;
               BlockPos blockpos1 = this.getHeight(Heightmap.Type.MOTION_BLOCKING, new BlockPos(j + (i2 & 15), 0, k + (i2 >> 8 & 15)));
               BlockPos blockpos2 = blockpos1.down();
               Biome biome = this.getBiome(blockpos1);
               if (this.isAreaLoaded(blockpos2, 1, false)) // Forge: check area to avoid loading neighbors in unloaded chunks
               if (biome.doesWaterFreeze(this, blockpos2)) {
                  this.setBlockState(blockpos2, Blocks.ICE.getDefaultState());
               }

               if (flag && biome.doesSnowGenerate(this, blockpos1)) {
                  this.setBlockState(blockpos1, Blocks.SNOW.getDefaultState());
               }

               if (flag && this.getBiome(blockpos2).getPrecipitation() == Biome.RainType.RAIN) {
                  this.getBlockState(blockpos2).getBlock().fillWithRain(this, blockpos2);
               }
            }

            this.profiler.endStartSection("tickBlocks");
            if (i > 0) {
               for(ChunkSection chunksection : chunk.getSections()) {
                  if (chunksection != Chunk.EMPTY_SECTION && chunksection.needsRandomTickAny()) {
                     for(int j2 = 0; j2 < i; ++j2) {
                        this.updateLCG = this.updateLCG * 3 + 1013904223;
                        int i1 = this.updateLCG >> 2;
                        int j1 = i1 & 15;
                        int k1 = i1 >> 8 & 15;
                        int l1 = i1 >> 16 & 15;
                        IBlockState iblockstate = chunksection.get(j1, l1, k1);
                        IFluidState ifluidstate = chunksection.getFluidState(j1, l1, k1);
                        this.profiler.startSection("randomTick");
                        if (iblockstate.needsRandomTick()) {
                           iblockstate.randomTick(this, new BlockPos(j1 + j, l1 + chunksection.getYLocation(), k1 + k), this.rand);
                        }

                        if (ifluidstate.getTickRandomly()) {
                           ifluidstate.randomTick(this, new BlockPos(j1 + j, l1 + chunksection.getYLocation(), k1 + k), this.rand);
                        }

                        this.profiler.endSection();
                     }
                  }
               }
            }
         }

         this.profiler.endSection();
      }
   }

   protected BlockPos adjustPosToNearbyEntity(BlockPos pos) {
      BlockPos blockpos = this.getHeight(Heightmap.Type.MOTION_BLOCKING, pos);
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(blockpos, new BlockPos(blockpos.getX(), this.getHeight(), blockpos.getZ()))).grow(3.0D);
      List<EntityLivingBase> list = this.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb, (p_210194_1_) -> {
         return p_210194_1_ != null && p_210194_1_.isAlive() && this.canSeeSky(p_210194_1_.getPosition());
      });
      if (!list.isEmpty()) {
         return list.get(this.rand.nextInt(list.size())).getPosition();
      } else {
         if (blockpos.getY() == -1) {
            blockpos = blockpos.up(2);
         }

         return blockpos;
      }
   }

   /**
    * Updates (and cleans up) entities and tile entities
    */
   public void tickEntities() {
      if (this.playerEntities.isEmpty() && func_212412_ag().isEmpty()) {
         if (this.updateEntityTick++ >= 300) {
            return;
         }
      } else {
         this.resetUpdateEntityTick();
      }

      this.dimension.tick();
      super.tickEntities();
   }

   protected void tickPlayers() {
      super.tickPlayers();
      this.profiler.endStartSection("players");

      for(int i = 0; i < this.playerEntities.size(); ++i) {
         Entity entity = this.playerEntities.get(i);
         Entity entity1 = entity.getRidingEntity();
         if (entity1 != null) {
            if (!entity1.removed && entity1.isPassenger(entity)) {
               continue;
            }

            entity.stopRiding();
         }

         this.profiler.startSection("tick");
         if (!entity.removed) {
            try {
               this.tickEntity(entity);
            } catch (Throwable throwable) {
               CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Ticking player");
               CrashReportCategory crashreportcategory = crashreport.makeCategory("Player being ticked");
               entity.fillCrashReport(crashreportcategory);
               throw new ReportedException(crashreport);
            }
         }

         this.profiler.endSection();
         this.profiler.startSection("remove");
         if (entity.removed) {
            int j = entity.chunkCoordX;
            int k = entity.chunkCoordZ;
            if (entity.addedToChunk && this.isChunkLoaded(j, k, true)) {
               this.getChunk(j, k).removeEntity(entity);
            }

            this.loadedEntityList.remove(entity);
            this.onEntityRemoved(entity);
         }

         this.profiler.endSection();
      }

   }

   /**
    * Resets the updateEntityTick field to 0
    */
   public void resetUpdateEntityTick() {
      this.updateEntityTick = 0;
   }

   /**
    * Runs through the list of updates to run and ticks them
    */
   public void tickPending() {
      if (this.worldInfo.getTerrainType() != WorldType.DEBUG_ALL_BLOCK_STATES) {
         this.pendingBlockTicks.tick();
         this.pendingFluidTicks.tick();
      }
   }

   private void tickFluid(NextTickListEntry<Fluid> fluidTickEntry) {
      IFluidState ifluidstate = this.getFluidState(fluidTickEntry.position);
      if (ifluidstate.getFluid() == fluidTickEntry.getTarget()) {
         ifluidstate.tick(this, fluidTickEntry.position);
      }

   }

   private void tickBlock(NextTickListEntry<Block> blockTickEntry) {
      IBlockState iblockstate = this.getBlockState(blockTickEntry.position);
      if (iblockstate.getBlock() == blockTickEntry.getTarget()) {
         iblockstate.tick(this, blockTickEntry.position, this.rand);
      }

   }

   /**
    * Updates the entity in the world if the chunk the entity is in is currently loaded or its forced to update.
    */
   public void tickEntity(Entity entityIn, boolean forceUpdate) {
      if (!this.canSpawnAnimals() && (entityIn instanceof EntityAnimal || entityIn instanceof EntityWaterMob)) {
         entityIn.remove();
      }

      if (!this.canSpawnNPCs() && entityIn instanceof INpc) {
         entityIn.remove();
      }

      super.tickEntity(entityIn, forceUpdate);
   }

   private boolean canSpawnNPCs() {
      return this.server.getCanSpawnNPCs();
   }

   private boolean canSpawnAnimals() {
      return this.server.getCanSpawnAnimals();
   }

   /**
    * Creates the chunk provider for this world. Called in the constructor. Retrieves provider from worldProvider?
    */
   protected IChunkProvider createChunkProvider() {
      IChunkLoader ichunkloader = this.saveHandler.getChunkLoader(this.dimension);
      return new ChunkProviderServer(this, ichunkloader, this.getWorldType().createChunkGenerator(this), this.server);
   }

   public boolean isBlockModifiable(EntityPlayer player, BlockPos pos) {
      return super.isBlockModifiable(player, pos);
   }

   @Override
   public boolean canMineBlockBody(EntityPlayer player, BlockPos pos) {
      return !this.server.isBlockProtected(this, pos, player) && this.getWorldBorder().contains(pos);
   }

   public void initialize(WorldSettings settings) {
      if (!this.worldInfo.isInitialized()) {
         try {
            this.createSpawnPosition(settings);
            if (this.worldInfo.getTerrainType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
               this.setDebugWorldSettings();
            }

            super.initialize(settings);
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception initializing level");

            try {
               this.fillCrashReport(crashreport);
            } catch (Throwable var5) {
               ;
            }

            throw new ReportedException(crashreport);
         }

         this.worldInfo.setServerInitialized(true);
      }

   }

   private void setDebugWorldSettings() {
      this.worldInfo.setMapFeaturesEnabled(false);
      this.worldInfo.setAllowCommands(true);
      this.worldInfo.setRaining(false);
      this.worldInfo.setThundering(false);
      this.worldInfo.setClearWeatherTime(1000000000);
      this.worldInfo.setDayTime(6000L);
      this.worldInfo.setGameType(GameType.SPECTATOR);
      this.worldInfo.setHardcore(false);
      this.worldInfo.setDifficulty(EnumDifficulty.PEACEFUL);
      this.worldInfo.setDifficultyLocked(true);
      this.getGameRules().setOrCreateGameRule("doDaylightCycle", "false", this.server);
   }

   /**
    * creates a spawn position at random within 256 blocks of 0,0
    */
   private void createSpawnPosition(WorldSettings settings) {
      if (!this.dimension.canRespawnHere()) {
         this.worldInfo.setSpawn(BlockPos.ORIGIN.up(this.chunkProvider.getChunkGenerator().getGroundHeight()));
      } else if (this.worldInfo.getTerrainType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
         this.worldInfo.setSpawn(BlockPos.ORIGIN.up());
      } else {
         if (net.minecraftforge.event.ForgeEventFactory.onCreateWorldSpawn(this, settings)) return;
         BiomeProvider biomeprovider = this.chunkProvider.getChunkGenerator().getBiomeProvider();
         List<Biome> list = biomeprovider.getBiomesToSpawnIn();
         Random random = new Random(this.getSeed());
         BlockPos blockpos = biomeprovider.findBiomePosition(0, 0, 256, list, random);
         ChunkPos chunkpos = blockpos == null ? new ChunkPos(0, 0) : new ChunkPos(blockpos);
         if (blockpos == null) {
            LOGGER.warn("Unable to find spawn biome");
         }

         boolean flag = false;

         for(Block block : BlockTags.VALID_SPAWN.getAllElements()) {
            if (biomeprovider.getSurfaceBlocks().contains(block.getDefaultState())) {
               flag = true;
               break;
            }
         }

         this.worldInfo.setSpawn(chunkpos.asBlockPos().add(8, this.chunkProvider.getChunkGenerator().getGroundHeight(), 8));
         int i1 = 0;
         int j1 = 0;
         int i = 0;
         int j = -1;
         int k = 32;

         for(int l = 0; l < 1024; ++l) {
            if (i1 > -16 && i1 <= 16 && j1 > -16 && j1 <= 16) {
               BlockPos blockpos1 = this.dimension.findSpawn(new ChunkPos(chunkpos.x + i1, chunkpos.z + j1), flag);
               if (blockpos1 != null) {
                  this.worldInfo.setSpawn(blockpos1);
                  break;
               }
            }

            if (i1 == j1 || i1 < 0 && i1 == -j1 || i1 > 0 && i1 == 1 - j1) {
               int k1 = i;
               i = -j;
               j = k1;
            }

            i1 += i;
            j1 += j;
         }

         if (settings.isBonusChestEnabled()) {
            this.createBonusChest();
         }

      }
   }

   /**
    * Creates the bonus chest in the world.
    */
   protected void createBonusChest() {
      BonusChestFeature bonuschestfeature = new BonusChestFeature();

      for(int i = 0; i < 10; ++i) {
         int j = this.worldInfo.getSpawnX() + this.rand.nextInt(6) - this.rand.nextInt(6);
         int k = this.worldInfo.getSpawnZ() + this.rand.nextInt(6) - this.rand.nextInt(6);
         BlockPos blockpos = this.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(j, 0, k)).up();
         if (bonuschestfeature.func_212245_a(this, this.chunkProvider.getChunkGenerator(), this.rand, blockpos, IFeatureConfig.NO_FEATURE_CONFIG)) {
            break;
         }
      }

   }

   /**
    * Returns null for anything other than the End
    */
   @Nullable
   public BlockPos getSpawnCoordinate() {
      return this.dimension.getSpawnCoordinate();
   }

   /**
    * Saves all chunks to disk while updating progress bar.
    */
   public void saveAllChunks(boolean all, @Nullable IProgressUpdate progressCallback) throws SessionLockException {
      ChunkProviderServer chunkproviderserver = this.getChunkProvider();
      if (chunkproviderserver.canSave()) {
         if (progressCallback != null) {
            progressCallback.displaySavingString(new TextComponentTranslation("menu.savingLevel"));
         }

         this.saveLevel();
         if (progressCallback != null) {
            progressCallback.displayLoadingString(new TextComponentTranslation("menu.savingChunks"));
         }

         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Save(this));
         chunkproviderserver.saveChunks(all);

         for(Chunk chunk : Lists.newArrayList(chunkproviderserver.getLoadedChunks())) {
            if (chunk != null && !this.playerChunkMap.contains(chunk.x, chunk.z)) {
               chunkproviderserver.queueUnload(chunk);
            }
         }

      }
   }

   /**
    * Flushes all pending chunks fully back to disk
    */
   public void flushToDisk() {
      ChunkProviderServer chunkproviderserver = this.getChunkProvider();
      if (chunkproviderserver.canSave()) {
         chunkproviderserver.flushToDisk();
      }
   }

   /**
    * Saves the chunks to disk.
    */
   protected void saveLevel() throws SessionLockException {
      this.checkSessionLock();

      for(WorldServer worldserver : this.server.func_212370_w()) {
         if (worldserver instanceof WorldServerMulti) {
            ((WorldServerMulti)worldserver).saveAdditionalData();
         }
      }

      this.worldInfo.setBorderSize(this.getWorldBorder().getDiameter());
      this.worldInfo.getBorderCenterX(this.getWorldBorder().getCenterX());
      this.worldInfo.getBorderCenterZ(this.getWorldBorder().getCenterZ());
      this.worldInfo.setBorderSafeZone(this.getWorldBorder().getDamageBuffer());
      this.worldInfo.setBorderDamagePerBlock(this.getWorldBorder().getDamageAmount());
      this.worldInfo.setBorderWarningDistance(this.getWorldBorder().getWarningDistance());
      this.worldInfo.setBorderWarningTime(this.getWorldBorder().getWarningTime());
      this.worldInfo.setBorderLerpTarget(this.getWorldBorder().getTargetSize());
      this.worldInfo.setBorderLerpTime(this.getWorldBorder().getTimeUntilTarget());
      this.worldInfo.setCustomBossEvents(this.server.getCustomBossEvents().write());
      this.saveHandler.saveWorldInfoWithPlayer(this.worldInfo, this.server.getPlayerList().getHostPlayerData());
      this.getMapStorage().saveAllData();
   }

   /**
    * Called when an entity is spawned in the world. This includes players.
    */
   public boolean spawnEntity(Entity entityIn) {
      return this.canAddEntity(entityIn) ? super.spawnEntity(entityIn) : false;
   }

   public void func_212420_a(Stream<Entity> p_212420_1_) {
      p_212420_1_.forEach((p_212421_1_) -> {
         if (this.canAddEntity(p_212421_1_) && !net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityJoinWorldEvent(p_212421_1_, this))) {
            this.loadedEntityList.add(p_212421_1_);
            this.onEntityAdded(p_212421_1_);
         }

      });
   }

   private boolean canAddEntity(Entity entityIn) {
      if (entityIn.removed) {
         LOGGER.warn("Tried to add entity {} but it was marked as removed already", (Object)EntityType.getId(entityIn.getType()));
         return false;
      } else {
         UUID uuid = entityIn.getUniqueID();
         if (this.entitiesByUuid.containsKey(uuid)) {
            Entity entity = this.entitiesByUuid.get(uuid);
            if (this.unloadedEntityList.contains(entity)) {
               this.unloadedEntityList.remove(entity);
            } else {
               if (!(entityIn instanceof EntityPlayer)) {
                  LOGGER.warn("Keeping entity {} that already exists with UUID {}", EntityType.getId(entity.getType()), uuid.toString());
                  return false;
               }

               LOGGER.warn("Force-added player with duplicate UUID {}", (Object)uuid.toString());
            }

            this.removeEntityDangerously(entity);
         }

         return true;
      }
   }

   public void onEntityAdded(Entity entityIn) {
      super.onEntityAdded(entityIn);
      this.entitiesById.addKey(entityIn.getEntityId(), entityIn);
      this.entitiesByUuid.put(entityIn.getUniqueID(), entityIn);
      Entity[] aentity = entityIn.getParts();
      if (aentity != null) {
         for(Entity entity : aentity) {
            this.entitiesById.addKey(entity.getEntityId(), entity);
         }
      }

   }

   public void onEntityRemoved(Entity entityIn) {
      super.onEntityRemoved(entityIn);
      this.entitiesById.removeObject(entityIn.getEntityId());
      this.entitiesByUuid.remove(entityIn.getUniqueID());
      Entity[] aentity = entityIn.getParts();
      if (aentity != null) {
         for(Entity entity : aentity) {
            this.entitiesById.removeObject(entity.getEntityId());
         }
      }

   }

   /**
    * adds a lightning bolt to the list of lightning bolts in this world.
    */
   public boolean addWeatherEffect(Entity entityIn) {
      if (super.addWeatherEffect(entityIn)) {
         this.server.getPlayerList().sendToAllNearExcept((EntityPlayer)null, entityIn.posX, entityIn.posY, entityIn.posZ, 512.0D, this.dimension.getType(), new SPacketSpawnGlobalEntity(entityIn));
         return true;
      } else {
         return false;
      }
   }

   /**
    * sends a Packet 38 (Entity Status) to all tracked players of that entity
    */
   public void setEntityState(Entity entityIn, byte state) {
      this.getEntityTracker().sendToTrackingAndSelf(entityIn, new SPacketEntityStatus(entityIn, state));
   }

   /**
    * gets the world's chunk provider
    */
   public ChunkProviderServer getChunkProvider() {
      return (ChunkProviderServer)super.getChunkProvider();
   }

   public Explosion createExplosion(@Nullable Entity entityIn, DamageSource damageSourceIn, double x, double y, double z, float strength, boolean causesFire, boolean damagesTerrain) {
      Explosion explosion = new Explosion(this, entityIn, x, y, z, strength, causesFire, damagesTerrain);
      if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(this, explosion)) return explosion;
      if (damageSourceIn != null) {
         explosion.setDamageSource(damageSourceIn);
      }

      explosion.doExplosionA();
      explosion.doExplosionB(false);
      if (!damagesTerrain) {
         explosion.clearAffectedBlockPositions();
      }

      for(EntityPlayer entityplayer : this.playerEntities) {
         if (entityplayer.getDistanceSq(x, y, z) < 4096.0D) {
            ((EntityPlayerMP)entityplayer).connection.sendPacket(new SPacketExplosion(x, y, z, strength, explosion.getAffectedBlockPositions(), explosion.getPlayerKnockbackMap().get(entityplayer)));
         }
      }

      return explosion;
   }

   public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam) {
      this.blockEventQueue.add(new BlockEventData(pos, blockIn, eventID, eventParam));
   }

   private void sendQueuedBlockEvents() {
      while(!this.blockEventQueue.isEmpty()) {
         BlockEventData blockeventdata = this.blockEventQueue.removeFirst();
         if (this.fireBlockEvent(blockeventdata)) {
            this.server.getPlayerList().sendToAllNearExcept((EntityPlayer)null, (double)blockeventdata.getPosition().getX(), (double)blockeventdata.getPosition().getY(), (double)blockeventdata.getPosition().getZ(), 64.0D, this.dimension.getType(), new SPacketBlockAction(blockeventdata.getPosition(), blockeventdata.getBlock(), blockeventdata.getEventID(), blockeventdata.getEventParameter()));
         }
      }

   }

   private boolean fireBlockEvent(BlockEventData event) {
      IBlockState iblockstate = this.getBlockState(event.getPosition());
      return iblockstate.getBlock() == event.getBlock() ? iblockstate.onBlockEventReceived(this, event.getPosition(), event.getEventID(), event.getEventParameter()) : false;
   }

   public void close() {
      this.saveHandler.flush();
      super.close();
   }

   /**
    * Updates all weather states.
    */
   protected void tickWeather() {
      boolean flag = this.isRaining();
      super.tickWeather();
      if (this.prevRainingStrength != this.rainingStrength) {
         this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SPacketChangeGameState(7, this.rainingStrength), this.dimension.getType());
      }

      if (this.prevThunderingStrength != this.thunderingStrength) {
         this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SPacketChangeGameState(8, this.thunderingStrength), this.dimension.getType());
      }

      /* The function in use here has been replaced in order to only send the weather info to players in the correct dimension,
       * rather than to all players on the server. This is what causes the client-side rain, as the
       * client believes that it has started raining locally, rather than in another dimension.
       */
      if (flag != this.isRaining()) {
         if (flag) {
            this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SPacketChangeGameState(2, 0.0F), this.dimension.getType());
         } else {
            this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SPacketChangeGameState(1, 0.0F), this.dimension.getType());
         }

         this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SPacketChangeGameState(7, this.rainingStrength), this.dimension.getType());
         this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SPacketChangeGameState(8, this.thunderingStrength), this.dimension.getType());
      }

   }

   public ServerTickList<Block> getPendingBlockTicks() {
      return this.pendingBlockTicks;
   }

   public ServerTickList<Fluid> getPendingFluidTicks() {
      return this.pendingFluidTicks;
   }

   @Nonnull
   public MinecraftServer getServer() {
      return this.server;
   }

   /**
    * Gets the entity tracker for this server world.
    */
   public EntityTracker getEntityTracker() {
      return this.entityTracker;
   }

   /**
    * Gets the player chunk map for this server world.
    */
   public PlayerChunkMap getPlayerChunkMap() {
      return this.playerChunkMap;
   }

   public Teleporter getDefaultTeleporter() {
      return this.worldTeleporter;
   }

   public TemplateManager getStructureTemplateManager() {
      return this.saveHandler.getStructureTemplateManager();
   }

   public <T extends IParticleData> int spawnParticle(T p_195598_1_, double p_195598_2_, double p_195598_4_, double p_195598_6_, int p_195598_8_, double p_195598_9_, double p_195598_11_, double p_195598_13_, double p_195598_15_) {
      SPacketParticles spacketparticles = new SPacketParticles(p_195598_1_, false, (float)p_195598_2_, (float)p_195598_4_, (float)p_195598_6_, (float)p_195598_9_, (float)p_195598_11_, (float)p_195598_13_, (float)p_195598_15_, p_195598_8_);
      int i = 0;

      for(int j = 0; j < this.playerEntities.size(); ++j) {
         EntityPlayerMP entityplayermp = (EntityPlayerMP)this.playerEntities.get(j);
         if (this.sendPacketWithinDistance(entityplayermp, false, p_195598_2_, p_195598_4_, p_195598_6_, spacketparticles)) {
            ++i;
         }
      }

      return i;
   }

   public <T extends IParticleData> boolean spawnParticle(EntityPlayerMP p_195600_1_, T p_195600_2_, boolean p_195600_3_, double p_195600_4_, double p_195600_6_, double p_195600_8_, int p_195600_10_, double p_195600_11_, double p_195600_13_, double p_195600_15_, double p_195600_17_) {
      Packet<?> packet = new SPacketParticles(p_195600_2_, p_195600_3_, (float)p_195600_4_, (float)p_195600_6_, (float)p_195600_8_, (float)p_195600_11_, (float)p_195600_13_, (float)p_195600_15_, (float)p_195600_17_, p_195600_10_);
      return this.sendPacketWithinDistance(p_195600_1_, p_195600_3_, p_195600_4_, p_195600_6_, p_195600_8_, packet);
   }

   private boolean sendPacketWithinDistance(EntityPlayerMP p_195601_1_, boolean p_195601_2_, double p_195601_3_, double p_195601_5_, double p_195601_7_, Packet<?> p_195601_9_) {
      if (p_195601_1_.getServerWorld() != this) {
         return false;
      } else {
         BlockPos blockpos = p_195601_1_.getPosition();
         double d0 = blockpos.distanceSq(p_195601_3_, p_195601_5_, p_195601_7_);
         if (!(d0 <= 1024.0D) && (!p_195601_2_ || !(d0 <= 262144.0D))) {
            return false;
         } else {
            p_195601_1_.connection.sendPacket(p_195601_9_);
            return true;
         }
      }
   }

   @Nullable
   public Entity getEntityFromUuid(UUID uuid) {
      return this.entitiesByUuid.get(uuid);
   }

   public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule) {
      return this.server.addScheduledTask(runnableToSchedule);
   }

   public boolean isCallingFromMinecraftThread() {
      return this.server.isCallingFromMinecraftThread();
   }

   @Nullable
   public BlockPos findNearestStructure(String name, BlockPos pos, int radius, boolean p_211157_4_) {
      return this.getChunkProvider().findNearestStructure(this, name, pos, radius, p_211157_4_);
   }

   public RecipeManager getRecipeManager() {
      return this.server.getRecipeManager();
   }

   public NetworkTagManager getTags() {
      return this.server.getNetworkTagManager();
   }

   public java.io.File getChunkSaveLocation() {
      return ((net.minecraft.world.chunk.storage.AnvilChunkLoader)getChunkProvider().chunkLoader).chunkSaveLocation;
   }
}