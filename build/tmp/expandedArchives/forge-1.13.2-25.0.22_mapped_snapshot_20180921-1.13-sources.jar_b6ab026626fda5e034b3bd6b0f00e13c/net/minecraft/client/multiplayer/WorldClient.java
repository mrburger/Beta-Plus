package net.minecraft.client.multiplayer;

import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.particles.IParticleData;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EmptyTickList;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.GameType;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.SaveDataMemoryStorage;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorldClient extends World {
   /** The packets that need to be sent to the server. */
   private final NetHandlerPlayClient connection;
   /** The ChunkProviderClient instance */
   private ChunkProviderClient clientChunkProvider;
   /** Contains all entities for this client, both spawned and non-spawned. */
   private final Set<Entity> entityList = Sets.newHashSet();
   /**
    * Contains all entities for this client that were not spawned due to a non-present chunk. The game will attempt to
    * spawn up to 10 pending entities with each subsequent tick until the spawn queue is empty.
    */
   private final Set<Entity> entitySpawnQueue = Sets.newHashSet();
   private final Minecraft mc = Minecraft.getInstance();
   private final Set<ChunkPos> previousActiveChunkSet = Sets.newHashSet();
   private int ambienceTicks;
   protected Set<ChunkPos> visibleChunks;
   private Scoreboard scoreboard;

   public WorldClient(NetHandlerPlayClient p_i49845_1_, WorldSettings p_i49845_2_, DimensionType p_i49845_3_, EnumDifficulty p_i49845_4_, Profiler p_i49845_5_) {
      super(new SaveHandlerMP(), new SaveDataMemoryStorage(), new WorldInfo(p_i49845_2_, "MpServer"), p_i49845_3_.create(), p_i49845_5_, true);
      this.ambienceTicks = this.rand.nextInt(12000);
      this.visibleChunks = Sets.newHashSet();
      this.scoreboard = new Scoreboard();
      this.connection = p_i49845_1_;
      this.getWorldInfo().setDifficulty(p_i49845_4_);
      this.dimension.setWorld(this);
      this.setSpawnPoint(new BlockPos(8, 64, 8)); //Forge: Moved below registerWorld to prevent NPE in our redirect.
      this.chunkProvider = this.createChunkProvider();
      this.calculateInitialSkylight();
      this.calculateInitialWeather();
      this.initCapabilities();
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Load(this));
   }

   /**
    * Runs a single tick for the world
    */
   public void tick(BooleanSupplier p_72835_1_) {
      super.tick(p_72835_1_);
      this.setTotalWorldTime(this.getGameTime() + 1L);
      if (this.getGameRules().getBoolean("doDaylightCycle")) {
         this.setDayTime(this.getDayTime() + 1L);
      }

      this.profiler.startSection("reEntryProcessing");

      for(int i = 0; i < 10 && !this.entitySpawnQueue.isEmpty(); ++i) {
         Entity entity = this.entitySpawnQueue.iterator().next();
         this.entitySpawnQueue.remove(entity);
         if (!this.loadedEntityList.contains(entity)) {
            this.spawnEntity(entity);
         }
      }

      this.profiler.endStartSection("chunkCache");
      this.clientChunkProvider.tick(p_72835_1_);
      this.profiler.endStartSection("blocks");
      this.tickBlocks();
      this.profiler.endSection();
   }

   /**
    * Creates the chunk provider for this world. Called in the constructor. Retrieves provider from worldProvider?
    */
   protected IChunkProvider createChunkProvider() {
      this.clientChunkProvider = new ChunkProviderClient(this);
      return this.clientChunkProvider;
   }

   public boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
      return allowEmpty || this.getChunkProvider().provideChunk(x, z, true, false) != null;
   }

   protected void refreshVisibleChunks() {
      this.visibleChunks.clear();
      int i = this.mc.gameSettings.renderDistanceChunks;
      this.profiler.startSection("buildList");
      int j = MathHelper.floor(this.mc.player.posX / 16.0D);
      int k = MathHelper.floor(this.mc.player.posZ / 16.0D);

      for(int l = -i; l <= i; ++l) {
         for(int i1 = -i; i1 <= i; ++i1) {
            this.visibleChunks.add(new ChunkPos(l + j, i1 + k));
         }
      }

      this.profiler.endSection();
   }

   protected void tickBlocks() {
      this.refreshVisibleChunks();
      if (this.ambienceTicks > 0) {
         --this.ambienceTicks;
      }

      this.previousActiveChunkSet.retainAll(this.visibleChunks);
      if (this.previousActiveChunkSet.size() == this.visibleChunks.size()) {
         this.previousActiveChunkSet.clear();
      }

      int i = 0;

      for(ChunkPos chunkpos : this.visibleChunks) {
         if (!this.previousActiveChunkSet.contains(chunkpos)) {
            int j = chunkpos.x * 16;
            int k = chunkpos.z * 16;
            this.profiler.startSection("getChunk");
            Chunk chunk = this.getChunk(chunkpos.x, chunkpos.z);
            this.playMoodSoundAndCheckLight(j, k, chunk);
            this.profiler.endSection();
            this.previousActiveChunkSet.add(chunkpos);
            ++i;
            if (i >= 10) {
               return;
            }
         }
      }

   }

   /**
    * Called when an entity is spawned in the world. This includes players.
    */
   public boolean spawnEntity(Entity entityIn) {
      boolean flag = super.spawnEntity(entityIn);
      this.entityList.add(entityIn);
      if (flag) {
         if (entityIn instanceof EntityMinecart) {
            this.mc.getSoundHandler().play(new MovingSoundMinecart((EntityMinecart)entityIn));
         }
      } else {
         this.entitySpawnQueue.add(entityIn);
      }

      return flag;
   }

   /**
    * Schedule the entity for removal during the next tick. Marks the entity dead in anticipation.
    */
   public void removeEntity(Entity entityIn) {
      super.removeEntity(entityIn);
      this.entityList.remove(entityIn);
   }

   public void onEntityAdded(Entity entityIn) {
      super.onEntityAdded(entityIn);
      if (this.entitySpawnQueue.contains(entityIn)) {
         this.entitySpawnQueue.remove(entityIn);
      }

   }

   public void onEntityRemoved(Entity entityIn) {
      super.onEntityRemoved(entityIn);
      if (this.entityList.contains(entityIn)) {
         if (entityIn.isAlive()) {
            this.entitySpawnQueue.add(entityIn);
         } else {
            this.entityList.remove(entityIn);
         }
      }

   }

   /**
    * Add an ID to Entity mapping to entityHashSet
    */
   public void addEntityToWorld(int entityID, Entity entityToSpawn) {
      Entity entity = this.getEntityByID(entityID);
      if (entity != null) {
         this.removeEntity(entity);
      }

      this.entityList.add(entityToSpawn);
      entityToSpawn.setEntityId(entityID);
      if (!this.spawnEntity(entityToSpawn)) {
         this.entitySpawnQueue.add(entityToSpawn);
      }

      this.entitiesById.addKey(entityID, entityToSpawn);
   }

   /**
    * Returns the Entity with the given ID, or null if it doesn't exist in this World.
    */
   @Nullable
   public Entity getEntityByID(int id) {
      return (Entity)(id == this.mc.player.getEntityId() ? this.mc.player : super.getEntityByID(id));
   }

   public Entity removeEntityFromWorld(int entityID) {
      Entity entity = this.entitiesById.removeObject(entityID);
      if (entity != null) {
         this.entityList.remove(entity);
         this.removeEntity(entity);
      }

      return entity;
   }

   public void invalidateRegionAndSetBlock(BlockPos pos, IBlockState state) {
      this.setBlockState(pos, state, 19);
   }

   /**
    * If on MP, sends a quitting packet.
    */
   public void sendQuittingDisconnectingPacket() {
      this.connection.getNetworkManager().closeChannel(new TextComponentTranslation("multiplayer.status.quitting"));
   }

   /**
    * Updates all weather states.
    */
   protected void tickWeather() {
   }

   protected void playMoodSoundAndCheckLight(int x, int z, Chunk chunkIn) {
      super.playMoodSoundAndCheckLight(x, z, chunkIn);
      if (this.ambienceTicks == 0) {
         this.updateLCG = this.updateLCG * 3 + 1013904223;
         int i = this.updateLCG >> 2;
         int j = i & 15;
         int k = i >> 8 & 15;
         int l = i >> 16 & 255;
         BlockPos blockpos = new BlockPos(j + x, l, k + z);
         IBlockState iblockstate = chunkIn.getBlockState(blockpos);
         j = j + x;
         k = k + z;
         if (iblockstate.isAir() && this.getLightSubtracted(blockpos, 0) <= this.rand.nextInt(8) && this.getLightFor(EnumLightType.SKY, blockpos) <= 0) {
            double d0 = this.mc.player.getDistanceSq((double)j + 0.5D, (double)l + 0.5D, (double)k + 0.5D);
            if (this.mc.player != null && d0 > 4.0D && d0 < 256.0D) {
               this.playSound((double)j + 0.5D, (double)l + 0.5D, (double)k + 0.5D, SoundEvents.AMBIENT_CAVE, SoundCategory.AMBIENT, 0.7F, 0.8F + this.rand.nextFloat() * 0.2F, false);
               this.ambienceTicks = this.rand.nextInt(12000) + 6000;
            }
         }
      }

   }

   public void animateTick(int posX, int posY, int posZ) {
      int i = 32;
      Random random = new Random();
      ItemStack itemstack = this.mc.player.getHeldItemMainhand();
      boolean flag = this.mc.playerController.getCurrentGameType() == GameType.CREATIVE && !itemstack.isEmpty() && itemstack.getItem() == Blocks.BARRIER.asItem();
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int j = 0; j < 667; ++j) {
         this.animateTick(posX, posY, posZ, 16, random, flag, blockpos$mutableblockpos);
         this.animateTick(posX, posY, posZ, 32, random, flag, blockpos$mutableblockpos);
      }

   }

   public void animateTick(int x, int y, int z, int offset, Random random, boolean holdingBarrier, BlockPos.MutableBlockPos pos) {
      int i = x + this.rand.nextInt(offset) - this.rand.nextInt(offset);
      int j = y + this.rand.nextInt(offset) - this.rand.nextInt(offset);
      int k = z + this.rand.nextInt(offset) - this.rand.nextInt(offset);
      pos.setPos(i, j, k);
      IBlockState iblockstate = this.getBlockState(pos);
      iblockstate.getBlock().animateTick(iblockstate, this, pos, random);
      IFluidState ifluidstate = this.getFluidState(pos);
      if (!ifluidstate.isEmpty()) {
         ifluidstate.animateTick(this, pos, random);
         IParticleData iparticledata = ifluidstate.getDripParticleData();
         if (iparticledata != null && this.rand.nextInt(10) == 0) {
            boolean flag = iblockstate.getBlockFaceShape(this, pos, EnumFacing.DOWN) == BlockFaceShape.SOLID;
            BlockPos blockpos = pos.down();
            this.spawnFluidParticle(blockpos, this.getBlockState(blockpos), iparticledata, flag);
         }
      }

      if (holdingBarrier && iblockstate.getBlock() == Blocks.BARRIER) {
         this.spawnParticle(Particles.BARRIER, (double)((float)i + 0.5F), (double)((float)j + 0.5F), (double)((float)k + 0.5F), 0.0D, 0.0D, 0.0D);
      }

   }

   private void spawnFluidParticle(BlockPos blockPosIn, IBlockState blockStateIn, IParticleData particleDataIn, boolean shapeDownSolid) {
      if (blockStateIn.getFluidState().isEmpty()) {
         VoxelShape voxelshape = blockStateIn.getCollisionShape(this, blockPosIn);
         double d0 = voxelshape.getEnd(EnumFacing.Axis.Y);
         if (d0 < 1.0D) {
            if (shapeDownSolid) {
               this.spawnParticle((double)blockPosIn.getX(), (double)(blockPosIn.getX() + 1), (double)blockPosIn.getZ(), (double)(blockPosIn.getZ() + 1), (double)(blockPosIn.getY() + 1) - 0.05D, particleDataIn);
            }
         } else if (!blockStateIn.isIn(BlockTags.IMPERMEABLE)) {
            double d1 = voxelshape.getStart(EnumFacing.Axis.Y);
            if (d1 > 0.0D) {
               this.spawnParticle(blockPosIn, particleDataIn, voxelshape, (double)blockPosIn.getY() + d1 - 0.05D);
            } else {
               BlockPos blockpos = blockPosIn.down();
               IBlockState iblockstate = this.getBlockState(blockpos);
               VoxelShape voxelshape1 = iblockstate.getCollisionShape(this, blockpos);
               double d2 = voxelshape1.getEnd(EnumFacing.Axis.Y);
               if (d2 < 1.0D && iblockstate.getFluidState().isEmpty()) {
                  this.spawnParticle(blockPosIn, particleDataIn, voxelshape, (double)blockPosIn.getY() - 0.05D);
               }
            }
         }

      }
   }

   private void spawnParticle(BlockPos posIn, IParticleData particleDataIn, VoxelShape voxelShapeIn, double p_211835_4_) {
      this.spawnParticle((double)posIn.getX() + voxelShapeIn.getStart(EnumFacing.Axis.X), (double)posIn.getX() + voxelShapeIn.getEnd(EnumFacing.Axis.X), (double)posIn.getZ() + voxelShapeIn.getStart(EnumFacing.Axis.Z), (double)posIn.getZ() + voxelShapeIn.getEnd(EnumFacing.Axis.Z), p_211835_4_, particleDataIn);
   }

   private void spawnParticle(double p_211834_1_, double p_211834_3_, double p_211834_5_, double p_211834_7_, double p_211834_9_, IParticleData p_211834_11_) {
      this.spawnParticle(p_211834_11_, p_211834_1_ + (p_211834_3_ - p_211834_1_) * this.rand.nextDouble(), p_211834_9_, p_211834_5_ + (p_211834_7_ - p_211834_5_) * this.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
   }

   /**
    * also releases skins.
    */
   public void removeAllEntities() {
      this.loadedEntityList.removeAll(this.unloadedEntityList);

      for(int i = 0; i < this.unloadedEntityList.size(); ++i) {
         Entity entity = this.unloadedEntityList.get(i);
         int j = entity.chunkCoordX;
         int k = entity.chunkCoordZ;
         if (entity.addedToChunk && this.isChunkLoaded(j, k, true)) {
            this.getChunk(j, k).removeEntity(entity);
         }
      }

      for(int i1 = 0; i1 < this.unloadedEntityList.size(); ++i1) {
         this.onEntityRemoved(this.unloadedEntityList.get(i1));
      }

      this.unloadedEntityList.clear();

      for(int j1 = 0; j1 < this.loadedEntityList.size(); ++j1) {
         Entity entity1 = this.loadedEntityList.get(j1);
         Entity entity2 = entity1.getRidingEntity();
         if (entity2 != null) {
            if (!entity2.removed && entity2.isPassenger(entity1)) {
               continue;
            }

            entity1.stopRiding();
         }

         if (entity1.removed) {
            int k1 = entity1.chunkCoordX;
            int l = entity1.chunkCoordZ;
            if (entity1.addedToChunk && this.isChunkLoaded(k1, l, true)) {
               this.getChunk(k1, l).removeEntity(entity1);
            }

            this.loadedEntityList.remove(j1--);
            this.onEntityRemoved(entity1);
         }
      }

   }

   /**
    * Adds some basic stats of the world to the given crash report.
    */
   public CrashReportCategory fillCrashReport(CrashReport report) {
      CrashReportCategory crashreportcategory = super.fillCrashReport(report);
      crashreportcategory.addDetail("Forced entities", () -> {
         return this.entityList.size() + " total; " + this.entityList;
      });
      crashreportcategory.addDetail("Retry entities", () -> {
         return this.entitySpawnQueue.size() + " total; " + this.entitySpawnQueue;
      });
      crashreportcategory.addDetail("Server brand", () -> {
         return this.mc.player.getServerBrand();
      });
      crashreportcategory.addDetail("Server type", () -> {
         return this.mc.getIntegratedServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
      });
      return crashreportcategory;
   }

   public void playSound(@Nullable EntityPlayer player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
      if (player == this.mc.player) {
         this.playSound(x, y, z, soundIn, category, volume, pitch, false);
      }

   }

   public void playSound(BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay) {
      this.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, soundIn, category, volume, pitch, distanceDelay);
   }

   public void playSound(double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay) {
      double d0 = this.mc.getRenderViewEntity().getDistanceSq(x, y, z);
      SimpleSound simplesound = new SimpleSound(soundIn, category, volume, pitch, (float)x, (float)y, (float)z);
      if (distanceDelay && d0 > 100.0D) {
         double d1 = Math.sqrt(d0) / 40.0D;
         this.mc.getSoundHandler().playDelayed(simplesound, (int)(d1 * 20.0D));
      } else {
         this.mc.getSoundHandler().play(simplesound);
      }

   }

   public void makeFireworks(double x, double y, double z, double motionX, double motionY, double motionZ, @Nullable NBTTagCompound compound) {
      this.mc.particles.addEffect(new ParticleFirework.Starter(this, x, y, z, motionX, motionY, motionZ, this.mc.particles, compound));
   }

   public void sendPacketToServer(Packet<?> packetIn) {
      this.connection.sendPacket(packetIn);
   }

   public RecipeManager getRecipeManager() {
      return this.connection.getRecipeManager();
   }

   public void setScoreboard(Scoreboard scoreboardIn) {
      this.scoreboard = scoreboardIn;
   }

   /**
    * Sets the world time.
    */
   public void setDayTime(long time) {
      if (time < 0L) {
         time = -time;
         this.getGameRules().setOrCreateGameRule("doDaylightCycle", "false", (MinecraftServer)null);
      } else {
         this.getGameRules().setOrCreateGameRule("doDaylightCycle", "true", (MinecraftServer)null);
      }

      super.setDayTime(time);
   }

   public ITickList<Block> getPendingBlockTicks() {
      return EmptyTickList.get();
   }

   public ITickList<Fluid> getPendingFluidTicks() {
      return EmptyTickList.get();
   }

   /**
    * gets the world's chunk provider
    */
   public ChunkProviderClient getChunkProvider() {
      return (ChunkProviderClient)super.getChunkProvider();
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public NetworkTagManager getTags() {
      return this.connection.getTags();
   }
}