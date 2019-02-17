package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkPrimer implements IChunk {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ChunkPos pos;
   private boolean modified;
   private final AtomicInteger refCount = new AtomicInteger();
   private Biome[] biomes;
   private final Map<Heightmap.Type, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Type.class);
   private volatile ChunkStatus status = ChunkStatus.EMPTY;
   private final Map<BlockPos, TileEntity> tileEntities = Maps.newHashMap();
   private final Map<BlockPos, NBTTagCompound> deferredTileEntities = Maps.newHashMap();
   private final ChunkSection[] sections = new ChunkSection[16];
   private final List<NBTTagCompound> entities = Lists.newArrayList();
   private final List<BlockPos> lightPositions = Lists.newArrayList();
   private final ShortList[] packedPositions = new ShortList[16];
   private final Map<String, StructureStart> structureStartMap = Maps.newHashMap();
   private final Map<String, LongSet> structureReferenceMap = Maps.newHashMap();
   private final UpgradeData upgradeData;
   private final ChunkPrimerTickList<Block> pendingBlockTicks;
   private final ChunkPrimerTickList<Fluid> pendingFluidTicks;
   private long inhabitedTime;
   private final Map<GenerationStage.Carving, BitSet> carvingMasks = Maps.newHashMap();
   private boolean updateHeightmaps;

   public ChunkPrimer(int x, int z, UpgradeData p_i48699_3_) {
      this(new ChunkPos(x, z), p_i48699_3_);
   }

   public ChunkPrimer(ChunkPos p_i48700_1_, UpgradeData data) {
      this.pos = p_i48700_1_;
      this.upgradeData = data;
      this.pendingBlockTicks = new ChunkPrimerTickList<>((p_205332_0_) -> {
         return p_205332_0_ == null || p_205332_0_.getDefaultState().isAir();
      }, IRegistry.field_212618_g::getKey, IRegistry.field_212618_g::get, p_i48700_1_);
      this.pendingFluidTicks = new ChunkPrimerTickList<>((p_205766_0_) -> {
         return p_205766_0_ == null || p_205766_0_ == Fluids.EMPTY;
      }, IRegistry.field_212619_h::getKey, IRegistry.field_212619_h::get, p_i48700_1_);
   }

   public static ShortList getOrCreate(ShortList[] lists, int idx) {
      if (lists[idx] == null) {
         lists[idx] = new ShortArrayList();
      }

      return lists[idx];
   }

   @Nullable
   public IBlockState getBlockState(BlockPos pos) {
      int i = pos.getX();
      int j = pos.getY();
      int k = pos.getZ();
      if (j >= 0 && j < 256) {
         return this.sections[j >> 4] == Chunk.EMPTY_SECTION ? Blocks.AIR.getDefaultState() : this.sections[j >> 4].get(i & 15, j & 15, k & 15);
      } else {
         return Blocks.VOID_AIR.getDefaultState();
      }
   }

   public IFluidState getFluidState(BlockPos pos) {
      int i = pos.getX();
      int j = pos.getY();
      int k = pos.getZ();
      return j >= 0 && j < 256 && this.sections[j >> 4] != Chunk.EMPTY_SECTION ? this.sections[j >> 4].getFluidState(i & 15, j & 15, k & 15) : Fluids.EMPTY.getDefaultState();
   }

   public List<BlockPos> getLightBlockPositions() {
      return this.lightPositions;
   }

   public ShortList[] getPackedLightPositions() {
      ShortList[] ashortlist = new ShortList[16];

      for(BlockPos blockpos : this.lightPositions) {
         getOrCreate(ashortlist, blockpos.getY() >> 4).add(packToLocal(blockpos));
      }

      return ashortlist;
   }

   public void addLightValue(short packedPosition, int lightValue) {
      this.addLightPosition(unpackToWorld(packedPosition, lightValue, this.pos));
   }

   public void addLightPosition(BlockPos lightPos) {
      this.lightPositions.add(lightPos);
   }

   @Nullable
   public IBlockState setBlockState(BlockPos pos, IBlockState state, boolean isMoving) {
      int i = pos.getX();
      int j = pos.getY();
      int k = pos.getZ();
      if (j >= 0 && j < 256) {
         if (state.getLightValue() > 0) {
            this.lightPositions.add(new BlockPos((i & 15) + this.getPos().getXStart(), j, (k & 15) + this.getPos().getZStart()));
         }

         if (this.sections[j >> 4] == Chunk.EMPTY_SECTION) {
            if (state.getBlock() == Blocks.AIR) {
               return state;
            }

            this.sections[j >> 4] = new ChunkSection(j >> 4 << 4, this.hasSkylight());
         }

         IBlockState iblockstate = this.sections[j >> 4].get(i & 15, j & 15, k & 15);
         this.sections[j >> 4].set(i & 15, j & 15, k & 15, state);
         if (this.updateHeightmaps) {
            this.getOrCreateHeightmap(Heightmap.Type.MOTION_BLOCKING).update(i & 15, j, k & 15, state);
            this.getOrCreateHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES).update(i & 15, j, k & 15, state);
            this.getOrCreateHeightmap(Heightmap.Type.OCEAN_FLOOR).update(i & 15, j, k & 15, state);
            this.getOrCreateHeightmap(Heightmap.Type.WORLD_SURFACE).update(i & 15, j, k & 15, state);
         }

         return iblockstate;
      } else {
         return Blocks.VOID_AIR.getDefaultState();
      }
   }

   public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
      tileEntityIn.setPos(pos);
      this.tileEntities.put(pos, tileEntityIn);
   }

   public Set<BlockPos> getTileEntityPositions() {
      Set<BlockPos> set = Sets.newHashSet(this.deferredTileEntities.keySet());
      set.addAll(this.tileEntities.keySet());
      return set;
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos pos) {
      return this.tileEntities.get(pos);
   }

   public Map<BlockPos, TileEntity> getTileEntities() {
      return this.tileEntities;
   }

   public void addEntity(NBTTagCompound entityCompound) {
      this.entities.add(entityCompound);
   }

   /**
    * Adds an entity to the chunk.
    */
   public void addEntity(Entity entityIn) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      entityIn.writeUnlessPassenger(nbttagcompound);
      this.addEntity(nbttagcompound);
   }

   public List<NBTTagCompound> getEntities() {
      return this.entities;
   }

   public void setBiomes(Biome[] biomesIn) {
      this.biomes = biomesIn;
   }

   public Biome[] getBiomes() {
      return this.biomes;
   }

   public void setModified(boolean modified) {
      this.modified = modified;
   }

   public boolean isModified() {
      return this.modified;
   }

   public ChunkStatus getStatus() {
      return this.status;
   }

   public void setStatus(ChunkStatus status) {
      this.status = status;
      this.setModified(true);
   }

   public void setStatus(String status) {
      this.setStatus(ChunkStatus.getByName(status));
   }

   /**
    * Returns the ExtendedBlockStorage array for this Chunk.
    */
   public ChunkSection[] getSections() {
      return this.sections;
   }

   public int getLight(EnumLightType lightType, BlockPos pos, boolean hasSkylight) {
      int i = pos.getX() & 15;
      int j = pos.getY();
      int k = pos.getZ() & 15;
      int l = j >> 4;
      if (l >= 0 && l <= this.sections.length - 1) {
         ChunkSection chunksection = this.sections[l];
         if (chunksection == Chunk.EMPTY_SECTION) {
            return this.canSeeSky(pos) ? lightType.defaultLightValue : 0;
         } else if (lightType == EnumLightType.SKY) {
            return !hasSkylight ? 0 : chunksection.getSkyLight(i, j & 15, k);
         } else {
            return lightType == EnumLightType.BLOCK ? chunksection.getBlockLight(i, j & 15, k) : lightType.defaultLightValue;
         }
      } else {
         return 0;
      }
   }

   public int getLightSubtracted(BlockPos pos, int amount, boolean hasSkylight) {
      int i = pos.getX() & 15;
      int j = pos.getY();
      int k = pos.getZ() & 15;
      int l = j >> 4;
      if (l >= 0 && l <= this.sections.length - 1) {
         ChunkSection chunksection = this.sections[l];
         if (chunksection == Chunk.EMPTY_SECTION) {
            return this.hasSkylight() && amount < EnumLightType.SKY.defaultLightValue ? EnumLightType.SKY.defaultLightValue - amount : 0;
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

   public boolean canSeeSky(BlockPos pos) {
      int i = pos.getX() & 15;
      int j = pos.getY();
      int k = pos.getZ() & 15;
      return j >= this.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, i, k);
   }

   public void setChunkSections(ChunkSection[] chunkSections) {
      if (this.sections.length != chunkSections.length) {
         LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", chunkSections.length, this.sections.length);
      } else {
         System.arraycopy(chunkSections, 0, this.sections, 0, this.sections.length);
      }
   }

   public Set<Heightmap.Type> getHeightMapKeys() {
      return this.heightmaps.keySet();
   }

   @Nullable
   public Heightmap getHeightmap(Heightmap.Type p_201642_1_) {
      return this.heightmaps.get(p_201642_1_);
   }

   public void setHeightMap(Heightmap.Type type, long[] heightData) {
      this.getOrCreateHeightmap(type).setDataArray(heightData);
   }

   public void createHeightMap(Heightmap.Type... types) {
      for(Heightmap.Type heightmap$type : types) {
         this.getOrCreateHeightmap(heightmap$type);
      }

   }

   private Heightmap getOrCreateHeightmap(Heightmap.Type p_207902_1_) {
      return this.heightmaps.computeIfAbsent(p_207902_1_, (p_207903_1_) -> {
         Heightmap heightmap = new Heightmap(this, p_207903_1_);
         heightmap.generate();
         return heightmap;
      });
   }

   public int getTopBlockY(Heightmap.Type heightmapType, int x, int z) {
      Heightmap heightmap = this.heightmaps.get(heightmapType);
      if (heightmap == null) {
         this.createHeightMap(heightmapType);
         heightmap = this.heightmaps.get(heightmapType);
      }

      return heightmap.getHeight(x & 15, z & 15) - 1;
   }

   /**
    * Gets a {@link ChunkPos} representing the x and z coordinates of this chunk.
    */
   public ChunkPos getPos() {
      return this.pos;
   }

   public void setLastSaveTime(long saveTime) {
   }

   @Nullable
   public StructureStart getStructureStart(String stucture) {
      return this.structureStartMap.get(stucture);
   }

   public void putStructureStart(String structureIn, StructureStart structureStartIn) {
      this.structureStartMap.put(structureIn, structureStartIn);
      this.modified = true;
   }

   public Map<String, StructureStart> getStructureStarts() {
      return Collections.unmodifiableMap(this.structureStartMap);
   }

   public void setStructureStarts(Map<String, StructureStart> map) {
      this.structureStartMap.clear();
      this.structureStartMap.putAll(map);
      this.modified = true;
   }

   @Nullable
   public LongSet getStructureReferences(String structureIn) {
      return this.structureReferenceMap.computeIfAbsent(structureIn, (p_208302_0_) -> {
         return new LongOpenHashSet();
      });
   }

   public void addStructureReference(String strucutre, long reference) {
      this.structureReferenceMap.computeIfAbsent(strucutre, (p_201628_0_) -> {
         return new LongOpenHashSet();
      }).add(reference);
      this.modified = true;
   }

   public Map<String, LongSet> getStructureReferences() {
      return Collections.unmodifiableMap(this.structureReferenceMap);
   }

   public void setStructureReferences(Map<String, LongSet> map) {
      this.structureReferenceMap.clear();
      this.structureReferenceMap.putAll(map);
      this.modified = true;
   }

   public void setLightFor(EnumLightType light, boolean hasSkylight, BlockPos pos, int lightValue) {
      int i = pos.getX() & 15;
      int j = pos.getY();
      int k = pos.getZ() & 15;
      int l = j >> 4;
      if (l < 16 && l >= 0) {
         if (this.sections[l] == Chunk.EMPTY_SECTION) {
            if (lightValue == light.defaultLightValue) {
               return;
            }

            this.sections[l] = new ChunkSection(l << 4, this.hasSkylight());
         }

         if (light == EnumLightType.SKY) {
            if (hasSkylight) {
               this.sections[l].setSkyLight(i, j & 15, k, lightValue);
            }
         } else if (light == EnumLightType.BLOCK) {
            this.sections[l].setBlockLight(i, j & 15, k, lightValue);
         }

      }
   }

   public static short packToLocal(BlockPos p_201651_0_) {
      int i = p_201651_0_.getX();
      int j = p_201651_0_.getY();
      int k = p_201651_0_.getZ();
      int l = i & 15;
      int i1 = j & 15;
      int j1 = k & 15;
      return (short)(l | i1 << 4 | j1 << 8);
   }

   public static BlockPos unpackToWorld(short packedPos, int yOffset, ChunkPos chunkPosIn) {
      int i = (packedPos & 15) + (chunkPosIn.x << 4);
      int j = (packedPos >>> 4 & 15) + (yOffset << 4);
      int k = (packedPos >>> 8 & 15) + (chunkPosIn.z << 4);
      return new BlockPos(i, j, k);
   }

   public void markBlockForPostprocessing(BlockPos pos) {
      if (!World.isOutsideBuildHeight(pos)) {
         getOrCreate(this.packedPositions, pos.getY() >> 4).add(packToLocal(pos));
      }

   }

   public ShortList[] getPackedPositions() {
      return this.packedPositions;
   }

   public void func_201636_b(short packedPosition, int index) {
      getOrCreate(this.packedPositions, index).add(packedPosition);
   }

   public ChunkPrimerTickList<Block> getBlocksToBeTicked() {
      return this.pendingBlockTicks;
   }

   public ChunkPrimerTickList<Fluid> func_212247_j() {
      return this.pendingFluidTicks;
   }

   private boolean hasSkylight() {
      return true;
   }

   public UpgradeData getUpgradeData() {
      return this.upgradeData;
   }

   public void setInhabitedTime(long inhabitedTime) {
      this.inhabitedTime = inhabitedTime;
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void addTileEntity(NBTTagCompound nbt) {
      this.deferredTileEntities.put(new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z")), nbt);
   }

   public Map<BlockPos, NBTTagCompound> getDeferredTileEntities() {
      return Collections.unmodifiableMap(this.deferredTileEntities);
   }

   public NBTTagCompound getDeferredTileEntity(BlockPos pos) {
      return this.deferredTileEntities.get(pos);
   }

   public void removeTileEntity(BlockPos pos) {
      this.tileEntities.remove(pos);
      this.deferredTileEntities.remove(pos);
   }

   public BitSet getCarvingMask(GenerationStage.Carving type) {
      return this.carvingMasks.computeIfAbsent(type, (p_205761_0_) -> {
         return new BitSet(65536);
      });
   }

   public void setCarvingMask(GenerationStage.Carving type, BitSet mask) {
      this.carvingMasks.put(type, mask);
   }

   public void addRefCount(int p_205747_1_) {
      this.refCount.addAndGet(p_205747_1_);
   }

   public boolean isAlive() {
      return this.refCount.get() > 0;
   }

   public void setUpdateHeightmaps(boolean p_207739_1_) {
      this.updateHeightmaps = p_207739_1_;
   }
}