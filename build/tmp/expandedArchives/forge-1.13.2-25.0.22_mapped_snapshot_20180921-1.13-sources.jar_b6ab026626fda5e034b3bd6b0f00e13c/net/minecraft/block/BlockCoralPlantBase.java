package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;

public class BlockCoralPlantBase extends Block implements IBucketPickupHandler, ILiquidContainer {
   public static final BooleanProperty field_212560_b = BlockStateProperties.WATERLOGGED;
   private static final VoxelShape field_212559_a = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);

   protected BlockCoralPlantBase(Block.Properties p_i49810_1_) {
      super(p_i49810_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(field_212560_b, Boolean.valueOf(true)));
   }

   protected void func_212558_a(IBlockState p_212558_1_, IWorld p_212558_2_, BlockPos p_212558_3_) {
      if (!func_212557_b_(p_212558_1_, p_212558_2_, p_212558_3_)) {
         p_212558_2_.getPendingBlockTicks().scheduleTick(p_212558_3_, this, 60 + p_212558_2_.getRandom().nextInt(40));
      }

   }

   protected static boolean func_212557_b_(IBlockState p_212557_0_, IBlockReader p_212557_1_, BlockPos p_212557_2_) {
      if (p_212557_0_.get(field_212560_b)) {
         return true;
      } else {
         for(EnumFacing enumfacing : EnumFacing.values()) {
            if (p_212557_1_.getFluidState(p_212557_2_.offset(enumfacing)).isTagged(FluidTags.WATER)) {
               return true;
            }
         }

         return false;
      }
   }

   protected boolean canSilkHarvest() {
      return true;
   }

   public int quantityDropped(IBlockState state, Random random) {
      return 0;
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      return this.getDefaultState().with(field_212560_b, Boolean.valueOf(ifluidstate.isTagged(FluidTags.WATER) && ifluidstate.getLevel() == 8));
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return field_212559_a;
   }

   /**
    * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
    */
   public boolean isFullCube(IBlockState state) {
      return false;
   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
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
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    *  
    * @param facingState The state that is currently at the position offset of the provided face to the stateIn at
    * currentPos
    */
   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (stateIn.get(field_212560_b)) {
         worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
      }

      return facing == EnumFacing.DOWN && !this.isValidPosition(stateIn, worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.down()).isTopSolid();
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(field_212560_b);
   }

   public IFluidState getFluidState(IBlockState state) {
      return state.get(field_212560_b) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
   }

   public Fluid pickupFluid(IWorld worldIn, BlockPos pos, IBlockState state) {
      if (state.get(field_212560_b)) {
         worldIn.setBlockState(pos, state.with(field_212560_b, Boolean.valueOf(false)), 3);
         return Fluids.WATER;
      } else {
         return Fluids.EMPTY;
      }
   }

   public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, IBlockState state, Fluid fluidIn) {
      return !state.get(field_212560_b) && fluidIn == Fluids.WATER;
   }

   public boolean receiveFluid(IWorld worldIn, BlockPos pos, IBlockState state, IFluidState fluidStateIn) {
      if (!state.get(field_212560_b) && fluidStateIn.getFluid() == Fluids.WATER) {
         if (!worldIn.isRemote()) {
            worldIn.setBlockState(pos, state.with(field_212560_b, Boolean.valueOf(true)), 3);
            worldIn.getPendingFluidTicks().scheduleTick(pos, fluidStateIn.getFluid(), fluidStateIn.getFluid().getTickRate(worldIn));
         }

         return true;
      } else {
         return false;
      }
   }
}