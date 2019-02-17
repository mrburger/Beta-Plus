package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockCoral extends Block {
   /** The dead variant of this block. */
   private final Block deadBlock;

   public BlockCoral(Block p_i48893_1_, Block.Properties builder) {
      super(builder);
      this.deadBlock = p_i48893_1_;
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (!this.canLive(worldIn, pos)) {
         worldIn.setBlockState(pos, this.deadBlock.getDefaultState(), 2);
      }

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
      if (!this.canLive(worldIn, currentPos)) {
         worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 60 + worldIn.getRandom().nextInt(40));
      }

      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   protected boolean canLive(IBlockReader p_203943_1_, BlockPos p_203943_2_) {
      for(EnumFacing enumfacing : EnumFacing.values()) {
         IFluidState ifluidstate = p_203943_1_.getFluidState(p_203943_2_.offset(enumfacing));
         if (ifluidstate.isTagged(FluidTags.WATER)) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      if (!this.canLive(context.getWorld(), context.getPos())) {
         context.getWorld().getPendingBlockTicks().scheduleTick(context.getPos(), this, 60 + context.getWorld().getRandom().nextInt(40));
      }

      return this.getDefaultState();
   }

   protected boolean canSilkHarvest() {
      return true;
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return this.deadBlock;
   }
}