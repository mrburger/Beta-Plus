package net.minecraft.world.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.EmptyTickList;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.gen.ChunkGeneratorDebug;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk extends net.minecraftforge.common.capabilities.CapabilityProvider<Chunk> implements IChunk, net.minecraftforge.common.extensions.IForgeChunk {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ChunkSection EMPTY_SECTION = null;
   /**
    * Used to store block IDs, block MSBs, Sky-light maps, Block-light maps, and metadata. Each entry corresponds to a
    * logical segment of 16x16x16 blocks, stacked vertically.
    */
   private final ChunkSection[] sections = new ChunkSection[16];
   /** Contains a 16x16 mapping on the X/Z plane of the biome ID to which each colum belongs. */
   private final Biome[] blockBiomeArray;
   /** Which columns need their skylightMaps updated. */
   private final boolean[] updateSkylightColumns = new boolean[256];
   /** Tile entities to be deserialized and loaded in {@link #postProcess} */
   private final Map<BlockPos, NBTTagCompound> deferredTileEntities = Maps.newHashMap();
   /** Whether or not this Chunk is currently loaded into the World */
   private boolean loaded;
   /** Reference to the World object. */
   private final World world;
   private final Map<Heightmap.Type, Heightmap> heightMap = Maps.newEnumMap(Heightmap.Type.class);
   /** The x coordinate of the chunk. */
   public final int x;
   /** The z coordinate of the chunk. */
   public final int z;
   private boolean isGapLightingUpdated;
   private final UpgradeData upgradeData;
   /** A Map of ChunkPositions to TileEntities in this chunk */
   private final Map<BlockPos, TileEntity> tileEntities = Maps.newHashMap();
   /** Array of Lists containing the entities in this Chunk. Each List represents a 16 block subchunk. */
   private final ClassInheritanceMultiMap<Entity>[] entityLists;
   private final Map<String, StructureStart> structureStarts = Maps.newHashMap();
   private final Map<String, LongSet> structureReferences = Maps.newHashMap();
   private final ShortList[] packedBlockPositions = new ShortList[16];
   private final ITickList<Block> blocksToBeTicked;
   private final ITickList<Fluid> fluidsToBeTicked;
   private boolean ticked;
   /** Whether this Chunk has any Entities and thus requires saving on every tick */
   private boolean hasEntities;
   /** The time according to World.worldTime when this chunk was last saved */
   private long lastSaveTime;
   /** Set to true if the chunk has been modified and needs to be updated internally. */
   private boolean dirty;
   /** Lowest value in the heightmap. */
   private int heightMapMinimum;
   /** the cumulative number of ticks players have been in this chunk */
   private long inhabitedTime;
   /** Contains the current round-robin relight check index, and is implied as the relight check location as well. */
   private int queuedLightChecks = 4096;
   /** Queue containing the BlockPos of tile entities queued for creation */
   private final ConcurrentLinkedQueue<BlockPos> tileEntityPosQueue = Queues.newConcurrentLinkedQueue();
   private ChunkStatus status = ChunkStatus.EMPTY;
   private int neighborCount;
   private final AtomicInteger field_205757_F = new AtomicInteger();
   private final ChunkPos field_212816_F;

   @OnlyIn(Dist.CLIENT)
   public Chunk(World worldIn, int cx, int cz, Biome[] biomesIn) {
      this(worldIn, cx, cz, biomesIn, UpgradeData.EMPTY, EmptyTickList.get(), EmptyTickList.get(), 0L);
   }

   public Chunk(World worldIn, int cx, int cz, Biome[] biomesIn, UpgradeData upgradeDataIn, ITickList<Block> blocksToBeTickedIn, ITickList<Fluid> fluidsToBeTickedIn, long inhabitedTimeIn) {
      super(Chunk.class);
      this.entityLists = new ClassInheritanceMultiMap[16];
      this.world = worldIn;
      this.x = cx;
      this.z = cz;
      this.field_212816_F = new ChunkPos(cx, cz);
      this.upgradeData = upgradeDataIn;

      for(Heightmap.Type heightmap$type : Heightmap.Type.values()) {
         if (heightmap$type.getUsage() == Heightmap.Usage.LIVE_WORLD) {
            this.heightMap.put(heightmap$type, new Heightmap(this, heightmap$type));
         }
      }

      for(int i = 0; i < this.entityLists.length; ++i) {
         this.entityLists[i] = new ClassInheritanceMultiMap<>(Entity.class);
      }

      this.blockBiomeArray = biomesIn;
      this.blocksToBeTicked = blocksToBeTickedIn;
      this.fluidsToBeTicked = fluidsToBeTickedIn;
      this.inhabitedTime = inhabitedTimeIn;
      this.gatherCapabilities();
   }

   public Chunk(World worldIn, ChunkPrimer primer, int cx, int cz) {
      this(worldIn, cx, cz, primer.getBiomes(), primer.getUpgradeData(), primer.getBlocksToBeTicked(), primer.func_212247_j(), primer.getInhabitedTime());

      for(int i = 0; i < this.sections.length; ++i) {
         this.sections[i] = primer.getSections()[i];
      }

      for(NBTTagCompound nbttagcompound : primer.getEntities()) {
         AnvilChunkLoader.readChunkEntity(nbttagcompound, worldIn, this);
      }

      for(TileEntity tileentity : primer.getTileEntities().values()) {
         this.addTileEntity(tileentity);
      }

      this.deferredTileEntities.putAll(primer.getDeferredTileEntities());

      for(int j = 0; j < primer.getPackedPositions().length; ++j) {
         this.packedBlockPositions[j] = primer.getPackedPositions()[j];
      }

      this.setStructureStarts(primer.getStructureStarts());
      this.setStructureReferences(primer.getStructureReferences());

      for(Heightmap.Type heightmap$type : primer.getHeightMapKeys()) {
         if (heightmap$type.getUsage() == Heightmap.Usage.LIVE_WORLD) {
            this.heightMap.computeIfAbsent(heightmap$type, (p_205750_1_) -> {
               return new Heightmap(this, p_205750_1_);
            }).setDataArray(primer.getHeightmap(heightmap$type).getDataArray());
         }
      }

      this.dirty = true;
      this.setStatus(ChunkStatus.FULLCHUNK);
   }

   public Set<BlockPos> getTileEntitiesPos() {
      Set<BlockPos> set = Sets.newHashSet(this.deferredTileEntities.keySet());
      set.addAll(this.tileEntities.keySet());
      return set;
   }

   /**
    * Checks whether the chunk is at the X/Z location specified
    */
   public boolean isAtLocation(int x, int z) {
      return x == this.x && z == this.z;
   }

   /**
    * Returns the ExtendedBlockStorage array for this Chunk.
    */
   public ChunkSection[] getSections() {
      return this.sections;
   }

   /**
    * Generates the height map for a chunk from scratch
    */
   @OnlyIn(Dist.CLIENT)
   protected void generateHeightMap() {
      for(Heightmap heightmap : this.heightMap.values()) {
         heightmap.generate();
      }

      this.dirty = true;
   }

   /**
    * Generates the initial skylight map for the chunk upon generation or load.
    */
   public void generateSkylightMap() {
      int i = this.getTopFilledSegment();
      this.heightMapMinimum = Integer.MAX_VALUE;

      for(Heightmap heightmap : this.heightMap.values()) {
         heightmap.generate();
      }

      for(int i1 = 0; i1 < 16; ++i1) {
         for(int j1 = 0; j1 < 16; ++j1) {
            if (this.world.dimension.hasSkyLight()) {
               int j = 15;
               int k = i + 16 - 1;

               while(true) {
                  int l = this.getBlockLightOpacity(i1, k, j1);
                  if (l == 0 && j != 15) {
                     l = 1;
                  }

                  j -= l;
                  if (j > 0) {
                     ChunkSection chunksection = this.sections[k >> 4];
                     if (chunksection != EMPTY_SECTION) {
                        chunksection.setSkyLight(i1, k & 15, j1, j);
                        this.world.notifyLightSet(new BlockPos((this.x << 4) + i1, k, (this.z << 4) + j1));
                     }
                  }

                  --k;
                  if (k <= 0 || j <= 0) {
                     break;
                  }
               }
            }
         }
      }

      this.dirty = true;
   }

   /**
    * Propagates a given sky-visible block's light value downward and upward to neighboring blocks as necessary.
    */
   private void propagateSkylightOcclusion(int x, int z) {
      this.updateSkylightColumns[x + z * 16] = true;
      this.isGapLightingUpdated = true;
   }

   private void recheckGaps(boolean onlyOne) {
      this.world.profiler.startSection("recheckGaps");
      if (this.world.isAreaLoaded(new BlockPos(this.x * 16 + 8, 0, this.z * 16 + 8), 16)) {
         for(int i = 0; i < 16; ++i) {
            for(int j = 0; j < 16; ++j) {
               if (this.updateSkylightColumns[i + j * 16]) {
                  this.updateSkylightColumns[i + j * 16] = false;
                  int k = this.getTopBlockY(Heightmap.Type.LIGHT_BLOCKING, i, j);
                  int l = this.x * 16 + i;
                  int i1 = this.z * 16 + j;
                  int j1 = Integer.MAX_VALUE;

                  for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                     j1 = Math.min(j1, this.world.getChunksLowestHorizon(l + enumfacing.getXOffset(), i1 + enumfacing.getZOffset()));
                  }

                  this.checkSkylightNeighborHeight(l, i1, j1);

                  for(EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
                     this.checkSkylightNeighborHeight(l + enumfacing1.getXOffset(), i1 + enumfacing1.getZOffset(), k);
                  }

                  if (onlyOne) {
                     this.world.profiler.endSection();
                     return;
                  }
               }
            }
         }

         this.isGapLightingUpdated = false;
      }

      this.world.profiler.endSection();
   }

   /**
    * Checks the height of a block next to a sky-visible block and schedules a lighting update as necessary.
    */
   private void checkSkylightNeighborHeight(int x, int z, int maxValue) {
      int i = this.world.getHeight(Heightmap.Type.MOTION_BLOCKING, new BlockPos(x, 0, z)).getY();
      if (i > maxValue) {
         this.updateSkylightNeighborHeight(x, z, maxValue, i + 1);
      } else if (i < maxValue) {
         this.updateSkylightNeighborHeight(x, z, i, maxValue + 1);
      }

   }

   private void updateSkylightNeighborHeight(int x, int z, int startY, int endY) {
      if (endY > startY && this.world.isAreaLoaded(new BlockPos(x, 0, z), 16)) {
         for(int i = startY; i < endY; ++i) {
            this.world.checkLightFor(EnumLightType.SKY, new BlockPos(x, i, z));
         }

         this.dirty = true;
      }

   }

   /**
    * Initiates the recalculation of both the block-light and sky-light for a given block inside a chunk.
    */
   private void relightBlock(int x, int y, int z, IBlockState p_76615_4_) {
      Heightmap heightmap = this.heightMap.get(Heightmap.Type.LIGHT_BLOCKING);
      int i = heightmap.getHeight(x & 15, z & 15) & 255;
      if (heightmap.update(x, y, z, p_76615_4_)) {
         int j = heightmap.getHeight(x & 15, z & 15);
         int k = this.x * 16 + x;
         int l = this.z * 16 + z;
         this.world.markBlocksDirtyVertical(k, l, j, i);
         if (this.world.dimension.hasSkyLight()) {
            int i1 = Math.min(i, j);
            int j1 = Math.max(i, j);
            int k1 = j < i ? 15 : 0;

            for(int l1 = i1; l1 < j1; ++l1) {
               ChunkSection chunksection = this.sections[l1 >> 4];
               if (chunksection != EMPTY_SECTION) {
                  chunksection.setSkyLight(x, l1 & 15, z, k1);
                  this.world.notifyLightSet(new BlockPos((this.x << 4) + x, l1, (this.z << 4) + z));
               }
            }

            int l2 = 15;

            while(j > 0 && l2 > 0) {
               --j;
               int i3 = this.getBlockLightOpacity(x, j, z);
               i3 = i3 == 0 ? 1 : i3;
               l2 = l2 - i3;
               l2 = Math.max(0, l2);
               ChunkSection chunksection1 = this.sections[j >> 4];
               if (chunksection1 != EMPTY_SECTION) {
                  chunksection1.setSkyLight(x, j & 15, z, l2);
               }
            }
         }

         if (j < this.heightMapMinimum) {
            this.heightMapMinimum = j;
         }

         if (this.world.dimension.hasSkyLight()) {
            int i2 = heightmap.getHeight(x & 15, z & 15);
            int j2 = Math.min(i, i2);
            int k2 = Math.max(i, i2);

            for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
               this.updateSkylightNeighborHeight(k + enumfacing.getXOffset(), l + enumfacing.getZOffset(), j2, k2);
            }

            this.updateSkylightNeighborHeight(k, l, j2, k2);
         }

         this.dirty = true;
      }
   }

   private int getBlockLightOpacity(int x, int y, int z) {
      return this.getBlockState(x, y, z).getOpacity(this.world, new BlockPos(x, y, z));
   }

   public IBlockState getBlockState(BlockPos pos) {
      return this.getBlockState(pos.getX(), pos.getY(), pos.getZ());
   }

   public IBlockState getBlockState(int x, int y, int z) {
      if (this.world.getWorldType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
         IBlockState iblockstate = null;
         if (y == 60) {
            iblockstate = Blocks.BARRIER.getDefaultState();
         }

         if (y == 70) {
            iblockstate = ChunkGeneratorDebug.getBlockStateFor(x, z);
         }

         return iblockstate == null ? Blocks.AIR.getDefaultState() : iblockstate;
      } else {
         try {
            if (y >= 0 && y >> 4 < this.sections.length) {
               ChunkSection chunksection = this.sections[y >> 4];
               if (chunksection != EMPTY_SECTION) {
                  return chunksection.get(x & 15, y & 15, z & 15);
               }
            }

            return Blocks.AIR.getDefaultState();
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting block state");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being got");
            crashreportcategory.addDetail("Location", () -> {
               return CrashReportCategory.getCoordinateInfo(x, y, z);
            });
            throw new ReportedException(crashreport);
         }
      }
   }

   public IFluidState getFluidState(BlockPos pos) {
      return this.getFluidState(pos.getX(), pos.getY(), pos.getZ());
   }

   public IFluidState getFluidState(int bx, int by, int bz) {
      try {
         if (by >= 0 && by >> 4 < this.sections.length) {
            ChunkSection chunksection = this.sections[by >> 4];
            if (chunksection != EMPTY_SECTION) {
               return chunksection.getFluidState(bx & 15, by & 15, bz & 15);
            }
         }

         return Fluids.EMPTY.getDefaultState();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting fluid state");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being got");
         crashreportcategory.addDetail("Location", () -> {
            return CrashReportCategory.getCoordinateInfo(bx, by, bz);
         });
         throw new ReportedException(crashreport);
      }
   }

   @Nullable
   public IBlockState setBlockState(BlockPos pos, IBlockState state, boolean isMoving) {
      int i = pos.getX() & 15;
      int j = pos.getY();
      int k = pos.getZ() & 15;
      int l = this.heightMap.get(Heightmap.Type.LIGHT_BLOCKING).getHeight(i, k);
      IBlockState iblockstate = this.getBlockState(pos);
      if (iblockstate == state) {
         return null;
      } else {
         Block block = state.getBlock();
         Block block1 = iblockstate.getBlock();
         ChunkSection chunksection = this.sections[j >> 4];
         int j1 = iblockstate.getOpacity(this.world, pos); // Relocate old light value lookup here, so that it is called before TE is removed.
         boolean flag = false;
         if (chunksection == EMPTY_SECTION) {
            if (state.isAir()) {
               return null;
            }

            chunksection = new ChunkSection(j >> 4 << 4, this.world.dimension.hasSkyLight());
            this.sections[j >> 4] = chunksection;
            flag = j >= l;
         }

         chunksection.set(i, j & 15, k, state);
         this.heightMap.get(Heightmap.Type.MOTION_BLOCKING).update(i, j, k, state);
         this.heightMap.get(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES).update(i, j, k, state);
         this.heightMap.get(Heightmap.Type.OCEAN_FLOOR).update(i, j, k, state);
         this.heightMap.get(Heightmap.Type.WORLD_SURFACE).update(i, j, k, state);
         if (!this.world.isRemote) {
            iblockstate.onReplaced(this.world, pos, state, isMoving);
         } else if (block1 != block && iblockstate.hasTileEntity()) {
            this.world.removeTileEntity(pos);
         }

         if (chunksection.get(i, j & 15, k).getBlock() != block) {
            return null;
         } else {
            if (flag) {
               this.generateSkylightMap();
            } else {
               int i1 = state.getOpacity(this.world, pos);
               this.relightBlock(i, j, k, state);
               if (i1 != j1 && (i1 < j1 || this.getLightFor(EnumLightType.SKY, pos) > 0 || this.getLightFor(EnumLightType.BLOCK, pos) > 0)) {
                  this.propagateSkylightOcclusion(i, k);
               }
            }

            if (iblockstate.hasTileEntity()) {
               TileEntity tileentity = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
               if (tileentity != null) {
                  tileentity.updateContainingBlockInfo();
               }
            }

            if (!this.world.isRemote) {
               state.onBlockAdded(this.world, pos, iblockstate);
            }

            if (state.hasTileEntity()) {
               TileEntity tileentity1 = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
               if (tileentity1 == null) {
                  tileentity1 = state.createTileEntity(this.world);
                  this.world.setTileEntity(pos, tileentity1);
               } else {
                  tileentity1.updateContainingBlockInfo();
               }
            }

            this.dirty = true;
            return iblockstate;
         }
      }
   }

   public int getLightFor(EnumLightType type, BlockPos pos) {
      return this.getLight(type, pos, this.world.getDimension().hasSkyLight());
   }

   public int getLight(EnumLightType lightType, BlockPos pos, boolean hasSkylight) {
      int i = pos.getX() & 15;
      int j = pos.getY();
      int k = pos.getZ() & 15;
      int l = j >> 4;
      if (l >= 0 && l <= this.sections.length - 1) {
         ChunkSection chunksection = this.sections[l];
         if (chunksection == EMPTY_SECTION) {
            return this.canSeeSky(pos) ? lightType.defaultLightValue : 0;
         } else if (lightType == EnumLightType.SKY) {
            return !hasSkylight ? 0 : chunksection.getSkyLight(i, j & 15, k);
         } else {
            return lightType == EnumLightType.BLOCK ? chunksection.getBlockLight(i, j & 15, k) : lightType.defaultLightValue;
         }
      } else {
         return (lightType != EnumLightType.SKY || !hasSkylight) && lightType != EnumLightType.BLOCK ? 0 : lightType.defaultLightValue;
      }
   }

   public void setLightFor(EnumLightType type, BlockPos pos, int value) {
      this.setLightFor(type, this.world.getDimension().hasSkyLight(), pos, value);
   }

   public void setLightFor(EnumLightType light, boolean hasSkylight, BlockPos pos, int lightValue) {
      int i = pos.getX() & 15;
      int j = pos.getY();
      int k = pos.getZ() & 15;
      int l = j >> 4;
      if (l < 16 && l >= 0) {
         ChunkSection chunksection = this.sections[l];
         if (chunksection == EMPTY_SECTION) {
            if (lightValue == light.defaultLightValue) {
               return;
            }

            chunksection = new ChunkSection(l << 4, hasSkylight);
            this.sections[l] = chunksection;
            this.generateSkylightMap();
         }

         if (light == EnumLightType.SKY) {
            if (this.world.dimension.hasSkyLight()) {
               chunksection.setSkyLight(i, j & 15, k, lightValue);
            }
         } else if (light == EnumLightType.BLOCK) {
            chunksection.setBlockLight(i, j & 15, k, lightValue);
         }

         this.dirty = true;
      }
   }

   public int getLightSubtracted(BlockPos pos, int amount) {
      return this.getLightSubtracted(pos, amount, this.world.getDimension().hasSkyLight());
   }

   public int getLightSubtracted(BlockPos pos, int amount, boolean hasSkylight) {
      int i = pos.getX() & 15;
      int j = pos.getY();
      int k = pos.getZ() & 15;
      int l = j >> 4;
      if (l >= 0 && l <= this.sections.length - 1) {
         ChunkSection chunksection = this.sections[l];
         if (chunksection == EMPTY_SECTION) {
            return hasSkylight && amount < EnumLightType.SKY.defaultLightValue ? EnumLightType.SKY.defaultLightValue - amount : 0;
         } else {
            int i1 = hasSkylight ? chunksection.getSkyLight(i, j & 15, k) : 0;
            i1 = i1 - amount;
            int j1 = chunksection.getBlockLight(i, j & 15, k);
            if (j1 > i1) {
               i1 = j1;
            }

            return i1;
         }
      } else {
         return 0;
      }
   }

   /**
    * Adds an entity to the chunk.
    */
   public void addEntity(Entity entityIn) {
      this.hasEntities = true;
      int i = MathHelper.floor(entityIn.posX / 16.0D);
      int j = MathHelper.floor(entityIn.posZ / 16.0D);
      if (i != this.x || j != this.z) {
         LOGGER.warn("Wrong location! ({}, {}) should be ({}, {}), {}", i, j, this.x, this.z, entityIn);
         entityIn.remove();
      }

      int k = MathHelper.floor(entityIn.posY / 16.0D);
      if (k < 0) {
         k = 0;
      }

      if (k >= this.entityLists.length) {
         k = this.entityLists.length - 1;
      }

      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityEvent.EnteringChunk(entityIn, this.x, this.z, entityIn.chunkCoordX, entityIn.chunkCoordZ));
      entityIn.addedToChunk = true;
      entityIn.chunkCoordX = this.x;
      entityIn.chunkCoordY = k;
      entityIn.chunkCoordZ = this.z;
      this.entityLists[k].add(entityIn);
      this.markDirty(); // Forge - ensure chunks are marked to save after an entity add
   }

   public void setHeightmap(Heightmap.Type type, long[] data) {
      this.heightMap.get(type).setDataArray(data);
   }

   /**
    * removes entity using its y chunk coordinate as its index
    */
   public void removeEntity(Entity entityIn) {
      this.removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
   }

   /**
    * Removes entity at the specified index from the entity array.
    */
   public void removeEntityAtIndex(Entity entityIn, int index) {
      if (index < 0) {
         index = 0;
      }

      if (index >= this.entityLists.length) {
         index = this.entityLists.length - 1;
      }

      this.entityLists[index].remove(entityIn);
      this.markDirty(); // Forge - ensure chunks are marked to save after entity removals
   }

   public boolean canSeeSky(BlockPos pos) {
      int i = pos.getX() & 15;
      int j = pos.getY();
      int k = pos.getZ() & 15;
      return j >= this.heightMap.get(Heightmap.Type.LIGHT_BLOCKING).getHeight(i, k);
   }

   public int getTopBlockY(Heightmap.Type heightmapType, int x, int z) {
      return this.heightMap.get(heightmapType).getHeight(x & 15, z & 15) - 1;
   }

   @Nullable
   private TileEntity createNewTileEntity(BlockPos pos) {
      IBlockState iblockstate = this.getBlockState(pos);
      Block block = iblockstate.getBlock();
      return !iblockstate.hasTileEntity() ? null : iblockstate.createTileEntity(this.world);
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos pos) {
      return this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType creationMode) {
      TileEntity tileentity = this.tileEntities.get(pos);
      if (tileentity != null && tileentity.isRemoved()) {
         tileEntities.remove(pos);
         tileentity = null;
      }

      if (tileentity == null) {
         NBTTagCompound nbttagcompound = this.deferredTileEntities.remove(pos);
         if (nbttagcompound != null) {
            TileEntity tileentity1 = this.func_212815_a(pos, nbttagcompound);
            if (tileentity1 != null) {
               return tileentity1;
            }
         }
      }

      if (tileentity == null) {
         if (creationMode == Chunk.EnumCreateEntityType.IMMEDIATE) {
            tileentity = this.createNewTileEntity(pos);
            this.world.setTileEntity(pos, tileentity);
         } else if (creationMode == Chunk.EnumCreateEntityType.QUEUED) {
            this.tileEntityPosQueue.add(pos.toImmutable());
         }
      }

      return tileentity;
   }

   public void addTileEntity(TileEntity tileEntityIn) {
      this.addTileEntity(tileEntityIn.getPos(), tileEntityIn);
      if (this.loaded) {
         this.world.addTileEntity(tileEntityIn);
      }

   }

   public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
      if (tileEntityIn.getWorld() != this.world) //Forge don't call unless it's changed, could screw up bad mods.
      tileEntityIn.setWorld(this.world);
      tileEntityIn.setPos(pos);
      if (this.getBlockState(pos).hasTileEntity()) {
         if (this.tileEntities.containsKey(pos)) {
            this.tileEntities.get(pos).remove();
         }

         tileEntityIn.validate();
         this.tileEntities.put(pos.toImmutable(), tileEntityIn);
      }
   }

   public void addTileEntity(NBTTagCompound nbt) {
      this.deferredTileEntities.put(new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z")), nbt);
   }

   public void removeTileEntity(BlockPos pos) {
      if (this.loaded) {
         TileEntity tileentity = this.tileEntities.remove(pos);
         if (tileentity != null) {
            tileentity.remove();
         }
      }

   }

   /**
    * Called when this Chunk is loaded by the ChunkProvider
    */
   public void onLoad() {
      this.loaded = true;
      this.world.addTileEntities(this.tileEntities.values());

      for(ClassInheritanceMultiMap<Entity> classinheritancemultimap : this.entityLists) {
         this.world.func_212420_a(classinheritancemultimap.stream().filter((p_212383_0_) -> {
            return !(p_212383_0_ instanceof EntityPlayer);
         }));
      }

      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Load(this));
   }

   /**
    * Called when this Chunk is unloaded by the ChunkProvider
    */
   public void onUnload() {
      java.util.Arrays.stream(entityLists).forEach(multimap -> com.google.common.collect.Lists.newArrayList(multimap.getByClass(net.minecraft.entity.player.EntityPlayer.class)).forEach(player -> world.tickEntity(player, false))); // FORGE - Fix for MC-92916
      this.loaded = false;

      for(TileEntity tileentity : this.tileEntities.values()) {
         this.world.markTileEntityForRemoval(tileentity);
      }

      for(ClassInheritanceMultiMap<Entity> classinheritancemultimap : this.entityLists) {
         this.world.unloadEntities(classinheritancemultimap);
      }
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Unload(this));

   }

   /**
    * Sets the isModified flag for this Chunk
    */
   public void markDirty() {
      this.dirty = true;
   }

   /**
    * Fills the given list of all entities that intersect within the given bounding box that aren't the passed entity.
    */
   public void getEntitiesWithinAABBForEntity(@Nullable Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, Predicate<? super Entity> filter) {
      int i = MathHelper.floor((aabb.minY - net.minecraftforge.common.extensions.IForgeWorld.MAX_ENTITY_RADIUS) / 16.0D);
      int j = MathHelper.floor((aabb.maxY + net.minecraftforge.common.extensions.IForgeWorld.MAX_ENTITY_RADIUS) / 16.0D);
      i = MathHelper.clamp(i, 0, this.entityLists.length - 1);
      j = MathHelper.clamp(j, 0, this.entityLists.length - 1);

      for(int k = i; k <= j; ++k) {
         if (!this.entityLists[k].isEmpty()) {
            for(Entity entity : this.entityLists[k]) {
               if (entity.getBoundingBox().intersects(aabb) && entity != entityIn) {
                  if (filter == null || filter.test(entity)) {
                     listToFill.add(entity);
                  }

                  Entity[] aentity = entity.getParts();
                  if (aentity != null) {
                     for(Entity entity1 : aentity) {
                        if (entity1 != entityIn && entity1.getBoundingBox().intersects(aabb) && (filter == null || filter.test(entity1))) {
                           listToFill.add(entity1);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   /**
    * Gets all entities that can be assigned to the specified class.
    */
   public <T extends Entity> void getEntitiesOfTypeWithinAABB(Class<? extends T> entityClass, AxisAlignedBB aabb, List<T> listToFill, @Nullable Predicate<? super T> filter) {
      int i = MathHelper.floor((aabb.minY - net.minecraftforge.common.extensions.IForgeWorld.MAX_ENTITY_RADIUS) / 16.0D);
      int j = MathHelper.floor((aabb.maxY + net.minecraftforge.common.extensions.IForgeWorld.MAX_ENTITY_RADIUS) / 16.0D);
      i = MathHelper.clamp(i, 0, this.entityLists.length - 1);
      j = MathHelper.clamp(j, 0, this.entityLists.length - 1);

      for(int k = i; k <= j; ++k) {
         for(T t : this.entityLists[k].getByClass(entityClass)) {
            if (t.getBoundingBox().intersects(aabb) && (filter == null || filter.test(t))) {
               listToFill.add(t);
            }
         }
      }

   }

   /**
    * Returns true if this Chunk needs to be saved
    */
   public boolean needsSaving(boolean p_76601_1_) {
      if (p_76601_1_) {
         if (this.hasEntities && this.world.getGameTime() != this.lastSaveTime || this.dirty) {
            return true;
         }
      } else if (this.hasEntities && this.world.getGameTime() >= this.lastSaveTime + 600L) {
         return true;
      }

      return this.dirty;
   }

   public boolean isEmpty() {
      return false;
   }

   public void tick(boolean skipRecheckGaps) {
      if (this.isGapLightingUpdated && this.world.dimension.hasSkyLight() && !skipRecheckGaps) {
         this.recheckGaps(this.world.isRemote);
      }

      this.ticked = true;

      while(!this.tileEntityPosQueue.isEmpty()) {
         BlockPos blockpos = this.tileEntityPosQueue.poll();
         if (this.getTileEntity(blockpos, Chunk.EnumCreateEntityType.CHECK) == null && this.getBlockState(blockpos).hasTileEntity()) {
            TileEntity tileentity = this.createNewTileEntity(blockpos);
            this.world.setTileEntity(blockpos, tileentity);
            this.world.markBlockRangeForRenderUpdate(blockpos, blockpos);
         }
      }

   }

   public boolean isPopulated() {
      return this.status.isAtLeast(ChunkStatus.POSTPROCESSED);
   }

   public boolean wasTicked() {
      return this.ticked;
   }

   /**
    * Gets a {@link ChunkPos} representing the x and z coordinates of this chunk.
    */
   public ChunkPos getPos() {
      return this.field_212816_F;
   }

   /**
    * Returns whether the ExtendedBlockStorages containing levels (in blocks) from arg 1 to arg 2 are fully empty (true)
    * or not (false).
    */
   public boolean isEmptyBetween(int startY, int endY) {
      if (startY < 0) {
         startY = 0;
      }

      if (endY >= 256) {
         endY = 255;
      }

      for(int i = startY; i <= endY; i += 16) {
         ChunkSection chunksection = this.sections[i >> 4];
         if (chunksection != EMPTY_SECTION && !chunksection.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public void setSections(ChunkSection[] newStorageArrays) {
      if (this.sections.length != newStorageArrays.length) {
         LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", newStorageArrays.length, this.sections.length);
      } else {
         System.arraycopy(newStorageArrays, 0, this.sections, 0, this.sections.length);
      }
   }

   /**
    * Loads this chunk from the given buffer.
    *  
    * @see net.minecraft.network.play.server.SPacketChunkData#getReadBuffer()
    */
   @OnlyIn(Dist.CLIENT)
   public void read(PacketBuffer buf, int availableSections, boolean fullChunk) {
      for (TileEntity tileEntity : tileEntities.values()) {
         tileEntity.updateContainingBlockInfo();
         tileEntity.getBlockState();
      }
      if (fullChunk) {
         this.tileEntities.clear();
      } else {
         Iterator<BlockPos> iterator = this.tileEntities.keySet().iterator();

         while(iterator.hasNext()) {
            BlockPos blockpos = iterator.next();
            int i = blockpos.getY() >> 4;
            if ((availableSections & 1 << i) != 0) {
               iterator.remove();
            }
         }
      }

      boolean flag = this.world.dimension.hasSkyLight();

      for(int j = 0; j < this.sections.length; ++j) {
         ChunkSection chunksection = this.sections[j];
         if ((availableSections & 1 << j) == 0) {
            if (fullChunk && chunksection != EMPTY_SECTION) {
               this.sections[j] = EMPTY_SECTION;
            }
         } else {
            if (chunksection == EMPTY_SECTION) {
               chunksection = new ChunkSection(j << 4, flag);
               this.sections[j] = chunksection;
            }

            chunksection.getData().read(buf);
            buf.readBytes(chunksection.getBlockLight().getData());
            if (flag) {
               buf.readBytes(chunksection.getSkyLight().getData());
            }
         }
      }

      if (fullChunk) {
         for(int k = 0; k < this.blockBiomeArray.length; ++k) {
            this.blockBiomeArray[k] = IRegistry.field_212624_m.get(buf.readInt());
         }
      }

      for(int l = 0; l < this.sections.length; ++l) {
         if (this.sections[l] != EMPTY_SECTION && (availableSections & 1 << l) != 0) {
            this.sections[l].recalculateRefCounts();
         }
      }

      this.generateHeightMap();

      for(TileEntity tileentity : this.tileEntities.values()) {
         tileentity.updateContainingBlockInfo();
      }

   }

   public Biome getBiome(BlockPos pos) {
      int i = pos.getX() & 15;
      int j = pos.getZ() & 15;
      return this.blockBiomeArray[j << 4 | i];
   }

   public Biome[] getBiomes() {
      return this.blockBiomeArray;
   }

   /**
    * Resets the relight check index to 0 for this Chunk.
    */
   @OnlyIn(Dist.CLIENT)
   public void resetRelightChecks() {
      this.queuedLightChecks = 0;
   }

   /**
    * Called once-per-chunk-per-tick, and advances the round-robin relight check index by up to 8 blocks at a time. In a
    * worst-case scenario, can potentially take up to 25.6 seconds, calculated via (4096/8)/20, to re-check all blocks
    * in a chunk, which may explain lagging light updates on initial world generation.
    */
   public void enqueueRelightChecks() {
      if (this.queuedLightChecks < 4096) {
         BlockPos blockpos = new BlockPos(this.x << 4, 0, this.z << 4);

         for(int i = 0; i < 8; ++i) {
            if (this.queuedLightChecks >= 4096) {
               return;
            }

            int j = this.queuedLightChecks % 16;
            int k = this.queuedLightChecks / 16 % 16;
            int l = this.queuedLightChecks / 256;
            ++this.queuedLightChecks;

            for(int i1 = 0; i1 < 16; ++i1) {
               BlockPos blockpos1 = blockpos.add(k, (j << 4) + i1, l);
               boolean flag = i1 == 0 || i1 == 15 || k == 0 || k == 15 || l == 0 || l == 15;
               if (this.sections[j] == EMPTY_SECTION && flag || this.sections[j] != EMPTY_SECTION && this.sections[j].get(k, i1, l).isAir(world, blockpos1)) {
                  for(EnumFacing enumfacing : EnumFacing.values()) {
                     BlockPos blockpos2 = blockpos1.offset(enumfacing);
                     if (this.world.getBlockState(blockpos2).getLightValue(world, blockpos2) > 0) {
                        this.world.checkLight(blockpos2);
                     }
                  }

                  this.world.checkLight(blockpos1);
               }
            }
         }

      }
   }

   public boolean isLoaded() {
      return this.loaded;
   }

   @OnlyIn(Dist.CLIENT)
   public void markLoaded(boolean loaded) {
      this.loaded = loaded;
   }

   public World getWorld() {
      return this.world;
   }

   public Set<Heightmap.Type> getHeightmaps() {
      return this.heightMap.keySet();
   }

   public Heightmap getHeightmap(Heightmap.Type type) {
      return this.heightMap.get(type);
   }

   public Map<BlockPos, TileEntity> getTileEntityMap() {
      return this.tileEntities;
   }

   public ClassInheritanceMultiMap<Entity>[] getEntityLists() {
      return this.entityLists;
   }

   public NBTTagCompound getDeferredTileEntity(BlockPos pos) {
      return this.deferredTileEntities.get(pos);
   }

   public ITickList<Block> getBlocksToBeTicked() {
      return this.blocksToBeTicked;
   }

   public ITickList<Fluid> func_212247_j() {
      return this.fluidsToBeTicked;
   }

   public BitSet getCarvingMask(GenerationStage.Carving type) {
      throw new RuntimeException("Not yet implemented");
   }

   public void setModified(boolean modified) {
      this.dirty = modified;
   }

   public void setHasEntities(boolean hasEntitiesIn) {
      this.hasEntities = hasEntitiesIn;
   }

   public void setLastSaveTime(long saveTime) {
      this.lastSaveTime = saveTime;
   }

   @Nullable
   public StructureStart getStructureStart(String stucture) {
      return this.structureStarts.get(stucture);
   }

   public void putStructureStart(String structureIn, StructureStart structureStartIn) {
      this.structureStarts.put(structureIn, structureStartIn);
   }

   public Map<String, StructureStart> getStructureStarts() {
      return this.structureStarts;
   }

   public void setStructureStarts(Map<String, StructureStart> structureStartsIn) {
      this.structureStarts.clear();
      this.structureStarts.putAll(structureStartsIn);
   }

   @Nullable
   public LongSet getStructureReferences(String structureIn) {
      return this.structureReferences.computeIfAbsent(structureIn, (p_201603_0_) -> {
         return new LongOpenHashSet();
      });
   }

   public void addStructureReference(String strucutre, long reference) {
      this.structureReferences.computeIfAbsent(strucutre, (p_201598_0_) -> {
         return new LongOpenHashSet();
      }).add(reference);
   }

   public Map<String, LongSet> getStructureReferences() {
      return this.structureReferences;
   }

   public void setStructureReferences(Map<String, LongSet> p_201606_1_) {
      this.structureReferences.clear();
      this.structureReferences.putAll(p_201606_1_);
   }

   public int getLowestHeight() {
      return this.heightMapMinimum;
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void setInhabitedTime(long newInhabitedTime) {
      this.inhabitedTime = newInhabitedTime;
   }

   public void postProcess() {
      if (!this.status.isAtLeast(ChunkStatus.POSTPROCESSED) && this.neighborCount == 8) {
         ChunkPos chunkpos = this.getPos();

         for(int i = 0; i < this.packedBlockPositions.length; ++i) {
            if (this.packedBlockPositions[i] != null) {
               for(Short oshort : this.packedBlockPositions[i]) {
                  BlockPos blockpos = ChunkPrimer.unpackToWorld(oshort, i, chunkpos);
                  IBlockState iblockstate = this.world.getBlockState(blockpos);
                  IBlockState iblockstate1 = Block.getValidBlockForPosition(iblockstate, this.world, blockpos);
                  this.world.setBlockState(blockpos, iblockstate1, 20);
               }

               this.packedBlockPositions[i].clear();
            }
         }

         if (this.blocksToBeTicked instanceof ChunkPrimerTickList) {
            ((ChunkPrimerTickList<Block>)this.blocksToBeTicked).postProcess(this.world.getPendingBlockTicks(), (p_205323_1_) -> {
               return this.world.getBlockState(p_205323_1_).getBlock();
            });
         }

         if (this.fluidsToBeTicked instanceof ChunkPrimerTickList) {
            ((ChunkPrimerTickList<Fluid>)this.fluidsToBeTicked).postProcess(this.world.getPendingFluidTicks(), (p_205324_1_) -> {
               return this.world.getFluidState(p_205324_1_).getFluid();
            });
         }

         for(BlockPos blockpos1 : new HashSet<>(this.deferredTileEntities.keySet())) {
            this.getTileEntity(blockpos1);
         }

         this.deferredTileEntities.clear();
         this.setStatus(ChunkStatus.POSTPROCESSED);
         this.upgradeData.postProcessChunk(this);
      }
   }

   @Nullable
   private TileEntity func_212815_a(BlockPos p_212815_1_, NBTTagCompound p_212815_2_) {
      TileEntity tileentity;
      if ("DUMMY".equals(p_212815_2_.getString("id"))) {
         IBlockState state = this.getBlockState(p_212815_1_);
         if (state.hasTileEntity()) {
            tileentity = state.createTileEntity(this.world);
         } else {
            tileentity = null;
            LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", p_212815_1_, this.getBlockState(p_212815_1_));
         }
      } else {
         tileentity = TileEntity.create(p_212815_2_);
      }

      if (tileentity != null) {
         tileentity.setPos(p_212815_1_);
         this.addTileEntity(tileentity);
      } else {
         LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", this.getBlockState(p_212815_1_), p_212815_1_);
      }

      return tileentity;
   }

   public UpgradeData getUpgradeData() {
      return this.upgradeData;
   }

   public ShortList[] getPackedPositions() {
      return this.packedBlockPositions;
   }

   public void addPackedPos(short p_201610_1_, int p_201610_2_) {
      ChunkPrimer.getOrCreate(this.packedBlockPositions, p_201610_2_).add(p_201610_1_);
   }

   public ChunkStatus getStatus() {
      return this.status;
   }

   public void setStatus(ChunkStatus status) {
      this.status = status;
   }

   public void setStatus(String statusIn) {
      this.setStatus(ChunkStatus.getByName(statusIn));
   }

   public void neighborAdded() {
      ++this.neighborCount;
      if (this.neighborCount > 8) {
         throw new RuntimeException("Error while adding chunk to cache. Too many neighbors");
      } else {
         if (this.areAllNeighborsLoaded()) {
            ((IThreadListener)this.world).addScheduledTask(this::postProcess);
         }

      }
   }

   public void neighborRemoved() {
      --this.neighborCount;
      if (this.neighborCount < 0) {
         throw new RuntimeException("Error while removing chunk from cache. Not enough neighbors");
      }
   }

   public boolean areAllNeighborsLoaded() {
      return this.neighborCount == 8;
   }

   public static enum EnumCreateEntityType {
      IMMEDIATE,
      QUEUED,
      CHECK;
   }

   /**
    * <strong>FOR INTERNAL USE ONLY</strong>
    * <p>
    * Only public for use in {@link AnvilChunkLoader}.
    */
   @java.lang.Deprecated
   @javax.annotation.Nullable
   public final NBTTagCompound writeCapsToNBT() {
      return this.serializeCaps();
   }

   /**
    * <strong>FOR INTERNAL USE ONLY</strong>
    * <p>
    * Only public for use in {@link AnvilChunkLoader}.
    */
   @java.lang.Deprecated
   public final void readCapsFromNBT(NBTTagCompound tag) {
      this.deserializeCaps(tag);
   }

   @Override
   public World getWorldForge() {
      return getWorld();
   }
}