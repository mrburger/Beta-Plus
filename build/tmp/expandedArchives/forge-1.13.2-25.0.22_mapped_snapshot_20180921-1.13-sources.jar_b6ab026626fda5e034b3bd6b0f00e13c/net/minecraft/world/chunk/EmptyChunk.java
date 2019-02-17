package net.minecraft.world.chunk;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EmptyChunk extends Chunk {
   private static final Biome[] BIOMES = Util.make(new Biome[256], (p_203406_0_) -> {
      Arrays.fill(p_203406_0_, Biomes.PLAINS);
   });

   public EmptyChunk(World worldIn, int x, int z) {
      super(worldIn, x, z, BIOMES);
   }

   /**
    * Checks whether the chunk is at the X/Z location specified
    */
   public boolean isAtLocation(int x, int z) {
      return x == this.x && z == this.z;
   }

   /**
    * Generates the height map for a chunk from scratch
    */
   public void generateHeightMap() {
   }

   /**
    * Generates the initial skylight map for the chunk upon generation or load.
    */
   public void generateSkylightMap() {
   }

   public IBlockState getBlockState(BlockPos pos) {
      return Blocks.VOID_AIR.getDefaultState();
   }

   public int getLight(EnumLightType lightType, BlockPos pos, boolean hasSkylight) {
      return lightType.defaultLightValue;
   }

   public void setLightFor(EnumLightType light, boolean hasSkylight, BlockPos pos, int lightValue) {
   }

   public int getLightSubtracted(BlockPos pos, int amount, boolean hasSkylight) {
      return 0;
   }

   /**
    * Adds an entity to the chunk.
    */
   public void addEntity(Entity entityIn) {
   }

   /**
    * removes entity using its y chunk coordinate as its index
    */
   public void removeEntity(Entity entityIn) {
   }

   /**
    * Removes entity at the specified index from the entity array.
    */
   public void removeEntityAtIndex(Entity entityIn, int index) {
   }

   public boolean canSeeSky(BlockPos pos) {
      return false;
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType creationMode) {
      return null;
   }

   public void addTileEntity(TileEntity tileEntityIn) {
   }

   public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
   }

   public void removeTileEntity(BlockPos pos) {
   }

   /**
    * Called when this Chunk is loaded by the ChunkProvider
    */
   public void onLoad() {
   }

   /**
    * Called when this Chunk is unloaded by the ChunkProvider
    */
   public void onUnload() {
   }

   /**
    * Sets the isModified flag for this Chunk
    */
   public void markDirty() {
   }

   /**
    * Fills the given list of all entities that intersect within the given bounding box that aren't the passed entity.
    */
   public void getEntitiesWithinAABBForEntity(@Nullable Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, Predicate<? super Entity> filter) {
   }

   /**
    * Gets all entities that can be assigned to the specified class.
    */
   public <T extends Entity> void getEntitiesOfTypeWithinAABB(Class<? extends T> entityClass, AxisAlignedBB aabb, List<T> listToFill, Predicate<? super T> filter) {
   }

   /**
    * Returns true if this Chunk needs to be saved
    */
   public boolean needsSaving(boolean p_76601_1_) {
      return false;
   }

   public boolean isEmpty() {
      return true;
   }

   /**
    * Returns whether the ExtendedBlockStorages containing levels (in blocks) from arg 1 to arg 2 are fully empty (true)
    * or not (false).
    */
   public boolean isEmptyBetween(int startY, int endY) {
      return true;
   }
}