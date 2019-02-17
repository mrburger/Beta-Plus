package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockBreakable extends Block {
   protected BlockBreakable(Block.Properties builder) {
      super(builder);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isSideInvisible(IBlockState state, IBlockState adjacentBlockState, EnumFacing side) {
      return adjacentBlockState.getBlock() == this ? true : super.isSideInvisible(state, adjacentBlockState, side);
   }
}