package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;

public class BlockWall extends BlockFourWay {
   public static final BooleanProperty UP = BlockStateProperties.UP;
   private final VoxelShape[] field_196422_D;
   private final VoxelShape[] field_196423_E;

   public BlockWall(Block.Properties builder) {
      super(0.0F, 3.0F, 0.0F, 14.0F, 24.0F, builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(UP, Boolean.valueOf(true)).with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false)));
      this.field_196422_D = this.func_196408_a(4.0F, 3.0F, 16.0F, 0.0F, 14.0F);
      this.field_196423_E = this.func_196408_a(4.0F, 3.0F, 24.0F, 0.0F, 24.0F);
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return state.get(UP) ? this.field_196422_D[this.getIndex(state)] : super.getShape(state, worldIn, pos);
   }

   public VoxelShape getCollisionShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return state.get(UP) ? this.field_196423_E[this.getIndex(state)] : super.getCollisionShape(state, worldIn, pos);
   }

   /**
    * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
    */
   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }

   private boolean attachesTo(IBlockState p_196421_1_, BlockFaceShape p_196421_2_) {
      Block block = p_196421_1_.getBlock();
      boolean flag = p_196421_2_ == BlockFaceShape.MIDDLE_POLE_THICK || p_196421_2_ == BlockFaceShape.MIDDLE_POLE && block instanceof BlockFenceGate;
      return !isExcepBlockForAttachWithPiston(block) && p_196421_2_ == BlockFaceShape.SOLID || flag;
   }

   @Override
   public boolean canBeConnectedTo(IBlockState state, IBlockReader world, BlockPos pos, EnumFacing facing) {
      IBlockState other = world.getBlockState(pos.offset(facing));
      return attachesTo(other, other.getBlockFaceShape(world, pos.offset(facing), facing.getOpposite()));
   }

   private boolean canWallConnectTo(IBlockReader world, BlockPos pos, EnumFacing facing) {
      BlockPos off = pos.offset(facing);
      IBlockState other = world.getBlockState(off);
      return other.canBeConnectedTo(world, off, facing.getOpposite()) || attachesTo(other, other.getBlockFaceShape(world, off, facing.getOpposite()));
   }

   public static boolean isExcepBlockForAttachWithPiston(Block p_194143_0_) {
      return Block.isExceptBlockForAttachWithPiston(p_194143_0_) || p_194143_0_ == Blocks.BARRIER || p_194143_0_ == Blocks.MELON || p_194143_0_ == Blocks.PUMPKIN || p_194143_0_ == Blocks.CARVED_PUMPKIN || p_194143_0_ == Blocks.JACK_O_LANTERN || p_194143_0_ == Blocks.FROSTED_ICE || p_194143_0_ == Blocks.TNT;
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IWorldReaderBase iworldreaderbase = context.getWorld();
      BlockPos blockpos = context.getPos();
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      boolean flag = canWallConnectTo(iworldreaderbase, blockpos, EnumFacing.NORTH);
      boolean flag1 = canWallConnectTo(iworldreaderbase, blockpos, EnumFacing.EAST);
      boolean flag2 = canWallConnectTo(iworldreaderbase, blockpos, EnumFacing.SOUTH);
      boolean flag3 = canWallConnectTo(iworldreaderbase, blockpos, EnumFacing.WEST);
      boolean flag4 = (!flag || flag1 || !flag2 || flag3) && (flag || !flag1 || flag2 || !flag3);
      return this.getDefaultState().with(UP, Boolean.valueOf(flag4 || !iworldreaderbase.isAirBlock(blockpos.up()))).with(NORTH, Boolean.valueOf(flag)).with(EAST, Boolean.valueOf(flag1)).with(SOUTH, Boolean.valueOf(flag2)).with(WEST, Boolean.valueOf(flag3)).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
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

      if (facing == EnumFacing.DOWN) {
         return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      } else {
         boolean flag = facing == EnumFacing.NORTH ? this.attachesTo(facingState, facingState.getBlockFaceShape(worldIn, facingPos, facing.getOpposite())) : stateIn.get(NORTH);
         boolean flag1 = facing == EnumFacing.EAST ? this.attachesTo(facingState, facingState.getBlockFaceShape(worldIn, facingPos, facing.getOpposite())) : stateIn.get(EAST);
         boolean flag2 = facing == EnumFacing.SOUTH ? this.attachesTo(facingState, facingState.getBlockFaceShape(worldIn, facingPos, facing.getOpposite())) : stateIn.get(SOUTH);
         boolean flag3 = facing == EnumFacing.WEST ? this.attachesTo(facingState, facingState.getBlockFaceShape(worldIn, facingPos, facing.getOpposite())) : stateIn.get(WEST);
         boolean flag4 = (!flag || flag1 || !flag2 || flag3) && (flag || !flag1 || flag2 || !flag3);
         return stateIn.with(UP, Boolean.valueOf(flag4 || !worldIn.isAirBlock(currentPos.up()))).with(NORTH, Boolean.valueOf(flag)).with(EAST, Boolean.valueOf(flag1)).with(SOUTH, Boolean.valueOf(flag2)).with(WEST, Boolean.valueOf(flag3));
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(UP, NORTH, EAST, WEST, SOUTH, WATERLOGGED);
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
      return face != EnumFacing.UP && face != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE_THICK : BlockFaceShape.CENTER_BIG;
   }
}