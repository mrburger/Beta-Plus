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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ITickList;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;
import org.apache.logging.log4j.LogManager;

public interface IChunk extends IBlockReader {
   @Nullable
   IBlockState setBlockState(BlockPos pos, IBlockState state, boolean isMoving);

   void addTileEntity(BlockPos pos, TileEntity tileEntityIn);

   /**
    * Adds an entity to the chunk.
    */
   void addEntity(Entity entityIn);

   void setStatus(ChunkStatus status);

   @Nullable
   default ChunkSection getLastExtendedBlockStorage() {
      ChunkSection[] achunksection = this.getSections();

      for(int i = achunksection.length - 1; i >= 0; --i) {
         if (achunksection[i] != Chunk.EMPTY_SECTION) {
            return achunksection[i];
         }
      }

      return null;
   }

   /**
    * Returns the topmost ExtendedBlockStorage instance for this Chunk that actually contains a block.
    */
   default int getTopFilledSegment() {
      ChunkSection chunksection = this.getLastExtendedBlockStorage();
      return chunksection == null ? 0 : chunksection.getYLocation();
   }

   /**
    * Returns the ExtendedBlockStorage array for this Chunk.
    */
   ChunkSection[] getSections();

   int getLight(EnumLightType lightType, BlockPos pos, boolean hasSkylight);

   int getLightSubtracted(BlockPos pos, int amount, boolean hasSkylight);

   boolean canSeeSky(BlockPos pos);

   int getTopBlockY(Heightmap.Type heightmapType, int x, int z);

   /**
    * Gets a {@link ChunkPos} representing the x and z coordinates of this chunk.
    */
   ChunkPos getPos();

   void setLastSaveTime(long saveTime);

   @Nullable
   StructureStart getStructureStart(String stucture);

   void putStructureStart(String structureIn, StructureStart structureStartIn);

   Map<String, StructureStart> getStructureStarts();

   @Nullable
   LongSet getStructureReferences(String structureIn);

   void addStructureReference(String strucutre, long reference);

   Map<String, LongSet> getStructureReferences();

   Biome[] getBiomes();

   ChunkStatus getStatus();

   void removeTileEntity(BlockPos pos);

   void setLightFor(EnumLightType light, boolean hasSkylight, BlockPos pos, int lightValue);

   default void markBlockForPostprocessing(BlockPos pos) {
      LogManager.getLogger().warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", (Object)pos);
   }

   default void addTileEntity(NBTTagCompound nbt) {
      LogManager.getLogger().warn("Trying to set a BlockEntity, but this operation is not supported.");
   }

   @Nullable
   default NBTTagCompound getDeferredTileEntity(BlockPos pos) {
      throw new UnsupportedOperationException();
   }

   default void setBiomes(Biome[] biomesIn) {
      throw new UnsupportedOperationException();
   }

   default void createHeightMap(Heightmap.Type... types) {
      throw new UnsupportedOperationException();
   }

   default List<BlockPos> getLightBlockPositions() {
      throw new UnsupportedOperationException();
   }

   ITickList<Block> getBlocksToBeTicked();

   ITickList<Fluid> func_212247_j();

   BitSet getCarvingMask(GenerationStage.Carving type);

   @Nullable
   default net.minecraft.world.IWorld getWorldForge() {
      return null;
   }
}