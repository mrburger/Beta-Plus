package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;

public class ChunkPrimerWrapper extends ChunkPrimer {
   private final IChunk chunk;

   public ChunkPrimerWrapper(IChunk p_i49380_1_) {
      super(p_i49380_1_.getPos(), UpgradeData.EMPTY);
      this.chunk = p_i49380_1_;
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos pos) {
      return this.chunk.getTileEntity(pos);
   }

   @Nullable
   public IBlockState getBlockState(BlockPos pos) {
      return this.chunk.getBlockState(pos);
   }

   public IFluidState getFluidState(BlockPos pos) {
      return this.chunk.getFluidState(pos);
   }

   public int getMaxLightLevel() {
      return this.chunk.getMaxLightLevel();
   }

   @Nullable
   public IBlockState setBlockState(BlockPos pos, IBlockState state, boolean isMoving) {
      return null;
   }

   public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
   }

   /**
    * Adds an entity to the chunk.
    */
   public void addEntity(Entity entityIn) {
   }

   public void setStatus(ChunkStatus status) {
   }

   /**
    * Returns the ExtendedBlockStorage array for this Chunk.
    */
   public ChunkSection[] getSections() {
      return this.chunk.getSections();
   }

   public int getLight(EnumLightType lightType, BlockPos pos, boolean hasSkylight) {
      return this.chunk.getLight(lightType, pos, hasSkylight);
   }

   public int getLightSubtracted(BlockPos pos, int amount, boolean hasSkylight) {
      return this.chunk.getLightSubtracted(pos, amount, hasSkylight);
   }

   public boolean canSeeSky(BlockPos pos) {
      return this.chunk.canSeeSky(pos);
   }

   public void setHeightMap(Heightmap.Type type, long[] heightData) {
   }

   private Heightmap.Type func_209532_c(Heightmap.Type p_209532_1_) {
      if (p_209532_1_ == Heightmap.Type.WORLD_SURFACE_WG) {
         return Heightmap.Type.WORLD_SURFACE;
      } else {
         return p_209532_1_ == Heightmap.Type.OCEAN_FLOOR_WG ? Heightmap.Type.OCEAN_FLOOR : p_209532_1_;
      }
   }

   public int getTopBlockY(Heightmap.Type heightmapType, int x, int z) {
      return this.chunk.getTopBlockY(this.func_209532_c(heightmapType), x, z);
   }

   /**
    * Gets a {@link ChunkPos} representing the x and z coordinates of this chunk.
    */
   public ChunkPos getPos() {
      return this.chunk.getPos();
   }

   public void setLastSaveTime(long saveTime) {
   }

   @Nullable
   public StructureStart getStructureStart(String stucture) {
      return this.chunk.getStructureStart(stucture);
   }

   public void putStructureStart(String structureIn, StructureStart structureStartIn) {
   }

   public Map<String, StructureStart> getStructureStarts() {
      return this.chunk.getStructureStarts();
   }

   public void setStructureStarts(Map<String, StructureStart> map) {
   }

   @Nullable
   public LongSet getStructureReferences(String structureIn) {
      return this.chunk.getStructureReferences(structureIn);
   }

   public void addStructureReference(String strucutre, long reference) {
   }

   public Map<String, LongSet> getStructureReferences() {
      return this.chunk.getStructureReferences();
   }

   public void setStructureReferences(Map<String, LongSet> map) {
   }

   public Biome[] getBiomes() {
      return this.chunk.getBiomes();
   }

   public void setModified(boolean modified) {
   }

   public boolean isModified() {
      return false;
   }

   public ChunkStatus getStatus() {
      return this.chunk.getStatus();
   }

   public void removeTileEntity(BlockPos pos) {
   }

   public void setLightFor(EnumLightType light, boolean hasSkylight, BlockPos pos, int lightValue) {
      this.chunk.setLightFor(light, hasSkylight, pos, lightValue);
   }

   public void markBlockForPostprocessing(BlockPos pos) {
   }

   public void addTileEntity(NBTTagCompound nbt) {
   }

   @Nullable
   public NBTTagCompound getDeferredTileEntity(BlockPos pos) {
      return this.chunk.getDeferredTileEntity(pos);
   }

   public void setBiomes(Biome[] biomesIn) {
   }

   public void createHeightMap(Heightmap.Type... types) {
   }

   public List<BlockPos> getLightBlockPositions() {
      return this.chunk.getLightBlockPositions();
   }

   public ChunkPrimerTickList<Block> getBlocksToBeTicked() {
      return new ChunkPrimerTickList<>((p_209219_0_) -> {
         return p_209219_0_.getDefaultState().isAir();
      }, IRegistry.field_212618_g::getKey, IRegistry.field_212618_g::get, this.getPos());
   }

   public ChunkPrimerTickList<Fluid> func_212247_j() {
      return new ChunkPrimerTickList<>((p_209218_0_) -> {
         return p_209218_0_ == Fluids.EMPTY;
      }, IRegistry.field_212619_h::getKey, IRegistry.field_212619_h::get, this.getPos());
   }

   public BitSet getCarvingMask(GenerationStage.Carving type) {
      return this.chunk.getCarvingMask(type);
   }

   public void setUpdateHeightmaps(boolean p_207739_1_) {
   }
}