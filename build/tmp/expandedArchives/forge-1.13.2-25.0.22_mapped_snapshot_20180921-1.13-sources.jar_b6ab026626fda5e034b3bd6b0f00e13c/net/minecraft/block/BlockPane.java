package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockPane extends BlockFourWay {
   protected BlockPane(Block.Properties builder) {
      super(1.0F, 1.0F, 16.0F, 16.0F, 16.0F, builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false)));
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IBlockReader iblockreader = context.getWorld();
      BlockPos blockpos = context.getPos();
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      return this.getDefaultState()
          .with(NORTH, canPaneConnectTo(iblockreader, blockpos, EnumFacing.NORTH))
          .with(SOUTH, canPaneConnectTo(iblockreader, blockpos, EnumFacing.SOUTH))
          .with(WEST, canPaneConnectTo(iblockreader, blockpos, EnumFacing.WEST))
          .with(EAST, canPaneConnectTo(iblockreader, blockpos, EnumFacing.EAST))
          .with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
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
      if (stateIn.get(WATERLOGGED)) {
         worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
      }

      return facing.getAxis().isHorizontal() ? stateIn.with(FACING_TO_PROPERTY_MAP.get(facing), Boolean.valueOf(this.canPaneConnectTo(worldIn, currentPos, facing))) : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   /**
    * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
    */
   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isSideInvisible(IBlockState state, IBlockState adjacentBlockState, EnumFacing side) {
      if (adjacentBlockState.getBlock() == this) {
         if (!side.getAxis().isHorizontal()) {
            return true;
         }

         if (state.get(FACING_TO_PROPERTY_MAP.get(side)) && adjacentBlockState.get(FACING_TO_PROPERTY_MAP.get(side.getOpposite()))) {
            return true;
         }
      }

      return super.isSideInvisible(state, adjacentBlockState, side);
   }

   public final boolean attachesTo(IBlockState p_196417_1_, BlockFaceShape p_196417_2_) {
      Block block = p_196417_1_.getBlock();
      return !shouldSkipAttachment(block) && p_196417_2_ == BlockFaceShape.SOLID || p_196417_2_ == BlockFaceShape.MIDDLE_POLE_THIN;
   }

   public static boolean shouldSkipAttachment(Block p_196418_0_) {
      return p_196418_0_ instanceof BlockShulkerBox || p_196418_0_ instanceof BlockLeaves || p_196418_0_ == Blocks.BEACON || p_196418_0_ == Blocks.CAULDRON || p_196418_0_ == Blocks.GLOWSTONE || p_196418_0_ == Blocks.ICE || p_196418_0_ == Blocks.SEA_LANTERN || p_196418_0_ == Blocks.PISTON || p_196418_0_ == Blocks.STICKY_PISTON || p_196418_0_ == Blocks.PISTON_HEAD || p_196418_0_ == Blocks.MELON || p_196418_0_ == Blocks.PUMPKIN || p_196418_0_ == Blocks.CARVED_PUMPKIN || p_196418_0_ == Blocks.JACK_O_LANTERN || p_196418_0_ == Blocks.BARRIER;
   }

   protected boolean canSilkHarvest() {
      return true;
   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT_MIPPED;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
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
      return face != EnumFacing.UP && face != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE_THIN : BlockFaceShape.CENTER_SMALL;
   }

   @Override
   public boolean canBeConnectedTo(IBlockState state, IBlockReader world, BlockPos pos, EnumFacing facing) {
      IBlockState other = world.getBlockState(pos.offset(facing));
      return attachesTo(other, other.getBlockFaceShape(world, pos.offset(facing), facing.getOpposite()));
   }

   private boolean canPaneConnectTo(IBlockReader world, BlockPos pos, EnumFacing facing) {
      BlockPos offset = pos.offset(facing);
      IBlockState other = world.getBlockState(offset);
      return other.canBeConnectedTo(world, offset, facing.getOpposite()) || getDefaultState().canBeConnectedTo(world, pos, facing);
   }
}