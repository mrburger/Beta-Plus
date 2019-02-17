package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockFence extends BlockFourWay {
   private final VoxelShape[] field_199609_B;

   public BlockFence(Block.Properties builder) {
      super(2.0F, 2.0F, 16.0F, 16.0F, 24.0F, builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false)));
      this.field_199609_B = this.func_196408_a(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
   }

   public VoxelShape getRenderShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return this.field_199609_B[this.getIndex(state)];
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

   public boolean attachesTo(IBlockState p_196416_1_, BlockFaceShape p_196416_2_) {
      Block block = p_196416_1_.getBlock();
      boolean flag = p_196416_2_ == BlockFaceShape.MIDDLE_POLE && (p_196416_1_.getMaterial() == this.material || block instanceof BlockFenceGate);
      return !isExcepBlockForAttachWithPiston(block) && p_196416_2_ == BlockFaceShape.SOLID || flag;
   }

   public static boolean isExcepBlockForAttachWithPiston(Block p_194142_0_) {
      return Block.isExceptBlockForAttachWithPiston(p_194142_0_) || p_194142_0_ == Blocks.BARRIER || p_194142_0_ == Blocks.MELON || p_194142_0_ == Blocks.PUMPKIN || p_194142_0_ == Blocks.CARVED_PUMPKIN || p_194142_0_ == Blocks.JACK_O_LANTERN || p_194142_0_ == Blocks.FROSTED_ICE || p_194142_0_ == Blocks.TNT;
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!worldIn.isRemote) {
         return ItemLead.attachToFence(player, worldIn, pos);
      } else {
         ItemStack itemstack = player.getHeldItem(hand);
         return itemstack.getItem() == Items.LEAD || itemstack.isEmpty();
      }
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IBlockReader iblockreader = context.getWorld();
      BlockPos blockpos = context.getPos();
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      return super.getStateForPlacement(context)
              .with(NORTH, canFenceConnectTo(iblockreader, blockpos, EnumFacing.NORTH))
              .with(EAST, canFenceConnectTo(iblockreader, blockpos, EnumFacing.EAST))
              .with(SOUTH, canFenceConnectTo(iblockreader, blockpos, EnumFacing.SOUTH))
              .with(WEST, canFenceConnectTo(iblockreader, blockpos, EnumFacing.WEST))
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

      return facing.getAxis().getPlane() == EnumFacing.Plane.HORIZONTAL ? stateIn.with(FACING_TO_PROPERTY_MAP.get(facing), Boolean.valueOf(this.canFenceConnectTo(worldIn, currentPos, facing))) : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
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
      return face != EnumFacing.UP && face != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.CENTER;
   }

   @Override
   public boolean canBeConnectedTo(IBlockState state, IBlockReader world, BlockPos pos, EnumFacing facing) {
      IBlockState other = world.getBlockState(pos.offset(facing));
      return attachesTo(other, other.getBlockFaceShape(world, pos.offset(facing), facing.getOpposite()));
   }

   private boolean canFenceConnectTo(IBlockReader world, BlockPos pos, EnumFacing facing) {
      BlockPos offset = pos.offset(facing);
      IBlockState other = world.getBlockState(offset);
      return other.canBeConnectedTo(world, offset, facing.getOpposite()) || getDefaultState().canBeConnectedTo(world, pos, facing);
   }
}