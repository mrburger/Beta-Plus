package net.minecraft.block;

import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockHugeMushroom extends Block {
   public static final BooleanProperty NORTH = BlockSixWay.NORTH;
   public static final BooleanProperty EAST = BlockSixWay.EAST;
   public static final BooleanProperty SOUTH = BlockSixWay.SOUTH;
   public static final BooleanProperty WEST = BlockSixWay.WEST;
   public static final BooleanProperty UP = BlockSixWay.UP;
   public static final BooleanProperty DOWN = BlockSixWay.DOWN;
   private static final Map<EnumFacing, BooleanProperty> field_196462_B = BlockSixWay.FACING_TO_PROPERTY_MAP;
   @Nullable
   private final Block smallBlock;

   public BlockHugeMushroom(@Nullable Block p_i48376_1_, Block.Properties p_i48376_2_) {
      super(p_i48376_2_);
      this.smallBlock = p_i48376_1_;
      this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, Boolean.valueOf(true)).with(EAST, Boolean.valueOf(true)).with(SOUTH, Boolean.valueOf(true)).with(WEST, Boolean.valueOf(true)).with(UP, Boolean.valueOf(true)).with(DOWN, Boolean.valueOf(true)));
   }

   public int quantityDropped(IBlockState state, Random random) {
      return Math.max(0, random.nextInt(9) - 6);
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return (IItemProvider)(this.smallBlock == null ? Items.AIR : this.smallBlock);
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IBlockReader iblockreader = context.getWorld();
      BlockPos blockpos = context.getPos();
      return this.getDefaultState().with(DOWN, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.down()).getBlock())).with(UP, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.up()).getBlock())).with(NORTH, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.north()).getBlock())).with(EAST, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.east()).getBlock())).with(SOUTH, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.south()).getBlock())).with(WEST, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.west()).getBlock()));
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
      return facingState.getBlock() == this ? stateIn.with(field_196462_B.get(facing), Boolean.valueOf(false)) : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   public IBlockState rotate(IBlockState state, Rotation rot) {
      return state.with(field_196462_B.get(rot.rotate(EnumFacing.NORTH)), state.get(NORTH)).with(field_196462_B.get(rot.rotate(EnumFacing.SOUTH)), state.get(SOUTH)).with(field_196462_B.get(rot.rotate(EnumFacing.EAST)), state.get(EAST)).with(field_196462_B.get(rot.rotate(EnumFacing.WEST)), state.get(WEST)).with(field_196462_B.get(rot.rotate(EnumFacing.UP)), state.get(UP)).with(field_196462_B.get(rot.rotate(EnumFacing.DOWN)), state.get(DOWN));
   }

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
    */
   public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
      return state.with(field_196462_B.get(mirrorIn.mirror(EnumFacing.NORTH)), state.get(NORTH)).with(field_196462_B.get(mirrorIn.mirror(EnumFacing.SOUTH)), state.get(SOUTH)).with(field_196462_B.get(mirrorIn.mirror(EnumFacing.EAST)), state.get(EAST)).with(field_196462_B.get(mirrorIn.mirror(EnumFacing.WEST)), state.get(WEST)).with(field_196462_B.get(mirrorIn.mirror(EnumFacing.UP)), state.get(UP)).with(field_196462_B.get(mirrorIn.mirror(EnumFacing.DOWN)), state.get(DOWN));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
   }
}