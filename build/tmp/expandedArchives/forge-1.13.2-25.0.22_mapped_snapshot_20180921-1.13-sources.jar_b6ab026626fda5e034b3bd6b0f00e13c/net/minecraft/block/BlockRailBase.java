package net.minecraft.block;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class BlockRailBase extends Block {
   protected static final VoxelShape FLAT_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   protected static final VoxelShape ASCENDING_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   private final boolean disableCorners;

   public static boolean isRail(World p_208488_0_, BlockPos p_208488_1_) {
      return isRail(p_208488_0_.getBlockState(p_208488_1_));
   }

   public static boolean isRail(IBlockState p_208487_0_) {
      return p_208487_0_.isIn(BlockTags.RAILS);
   }

   protected BlockRailBase(boolean p_i48444_1_, Block.Properties p_i48444_2_) {
      super(p_i48444_2_);
      this.disableCorners = p_i48444_1_;
   }

   public boolean areCornersDisabled() {
      return this.disableCorners;
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      RailShape railshape = state.getBlock() == this ? getRailDirection(state, worldIn, pos, null) : null;
      return railshape != null && railshape.isAscending() ? ASCENDING_AABB : FLAT_AABB;
   }

   /**
    * Get the geometry of the queried face at the given position and state. This is used to decide whether things like
    * buttons are allowed to be placed on the face, or how glass panes connect to the face, among other things.
    * <p>
    * Common values are {@code SOLID}, which is the default, and {@code UNDEFINED}, which represents something that does
    * not fit the other descriptions and will generally cause other things not to connect to the face.
    * 
    * @return an approximation of the form of the given face
    * @deprecated call via {@link IBlockState#getBlockFaceShape(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
    * Implementing/overriding is fine.
    */
   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   /**
    * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
    */
   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.down()).getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP) == BlockFaceShape.SOLID;
   }

   public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
      if (oldState.getBlock() != state.getBlock()) {
         if (!worldIn.isRemote) {
            state = this.func_208489_a(worldIn, pos, state, true);
            if (this.disableCorners) {
               state.neighborChanged(worldIn, pos, this, pos);
            }
         }

      }
   }

   /**
    * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
    * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
    * block, etc.
    */
   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!worldIn.isRemote) {
         RailShape railshape = getRailDirection(state, worldIn, pos, null);
         boolean flag = false;
         if (worldIn.getBlockState(pos.down()).getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP) != BlockFaceShape.SOLID) {
            flag = true;
         }

         if (railshape == RailShape.ASCENDING_EAST && worldIn.getBlockState(pos.east()).getBlockFaceShape(worldIn, pos.east(), EnumFacing.UP) != BlockFaceShape.SOLID) {
            flag = true;
         } else if (railshape == RailShape.ASCENDING_WEST && worldIn.getBlockState(pos.west()).getBlockFaceShape(worldIn, pos.west(), EnumFacing.UP) != BlockFaceShape.SOLID) {
            flag = true;
         } else if (railshape == RailShape.ASCENDING_NORTH && worldIn.getBlockState(pos.north()).getBlockFaceShape(worldIn, pos.north(), EnumFacing.UP) != BlockFaceShape.SOLID) {
            flag = true;
         } else if (railshape == RailShape.ASCENDING_SOUTH && worldIn.getBlockState(pos.south()).getBlockFaceShape(worldIn, pos.south(), EnumFacing.UP) != BlockFaceShape.SOLID) {
            flag = true;
         }

         if (flag && !worldIn.isAirBlock(pos)) {
            state.dropBlockAsItemWithChance(worldIn, pos, 1.0F, 0);
            worldIn.removeBlock(pos);
         } else {
            this.updateState(state, worldIn, pos, blockIn);
         }

      }
   }

   protected void updateState(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
   }

   protected IBlockState func_208489_a(World p_208489_1_, BlockPos p_208489_2_, IBlockState p_208489_3_, boolean p_208489_4_) {
      return p_208489_1_.isRemote ? p_208489_3_ : (new BlockRailState(p_208489_1_, p_208489_2_, p_208489_3_)).update(p_208489_1_.isBlockPowered(p_208489_2_), p_208489_4_).getNewState();
   }

   /**
    * @deprecated call via {@link IBlockState#getMobilityFlag()} whenever possible. Implementing/overriding is fine.
    */
   public EnumPushReaction getPushReaction(IBlockState state) {
      return EnumPushReaction.NORMAL;
   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
      if (!isMoving) {
         super.onReplaced(state, worldIn, pos, newState, isMoving);
         if (getRailDirection(state, worldIn, pos, null).isAscending()) {
            worldIn.notifyNeighborsOfStateChange(pos.up(), this);
         }

         if (this.disableCorners) {
            worldIn.notifyNeighborsOfStateChange(pos, this);
            worldIn.notifyNeighborsOfStateChange(pos.down(), this);
         }

      }
   }

   //Forge: Use getRailDirection(IBlockAccess, BlockPos, IBlockState, EntityMinecart) for enhanced ability
   public abstract IProperty<RailShape> getShapeProperty();

   /* ======================================== FORGE START =====================================*/
   /**
    * Return true if the rail can make corners.
    * Used by placement logic.
    * @param world The world.
    * @param pos Block's position in world
    * @return True if the rail can make corners.
    */
   public boolean isFlexibleRail(IBlockState state, IBlockReader world, BlockPos pos)
   {
       return !this.disableCorners;
   }

   /**
    * Returns true if the rail can make up and down slopes.
    * Used by placement logic.
    * @param world The world.
    * @param pos Block's position in world
    * @return True if the rail can make slopes.
    */
   public boolean canMakeSlopes(IBlockState state, IBlockReader world, BlockPos pos) {
       return true;
   }

   /**
    * Return the rail's direction.
    * Can be used to make the cart think the rail is a different shape,
    * for example when making diamond junctions or switches.
    * The cart parameter will often be null unless it it called from EntityMinecart.
    *
    * @param world The world.
    * @param pos Block's position in world
    * @param state The BlockState
    * @param cart The cart asking for the metadata, null if it is not called by EntityMinecart.
    * @return The direction.
    */
   public RailShape getRailDirection(IBlockState state, IBlockReader world, BlockPos pos, @javax.annotation.Nullable net.minecraft.entity.item.EntityMinecart cart) {
       return state.get(getShapeProperty());
   }

   /**
    * Returns the max speed of the rail at the specified position.
    * @param world The world.
    * @param cart The cart on the rail, may be null.
    * @param pos Block's position in world
    * @return The max speed of the current rail.
    */
   public float getRailMaxSpeed(IBlockState state, World world, BlockPos pos, net.minecraft.entity.item.EntityMinecart cart) {
       return 0.4f;
   }

   /**
    * This function is called by any minecart that passes over this rail.
    * It is called once per update tick that the minecart is on the rail.
    * @param world The world.
    * @param cart The cart on the rail.
    * @param pos Block's position in world
    */
   public void onMinecartPass(IBlockState state, World world, BlockPos pos, net.minecraft.entity.item.EntityMinecart cart) { }
}