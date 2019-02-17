package net.minecraft.block.state.pattern;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class LiquidBlockMatcher implements IBlockMatcherReaderAware<IBlockState> {
   private static final LiquidBlockMatcher INSTANCE = new LiquidBlockMatcher();

   public boolean test(IBlockState p_test_1_, IBlockReader p_test_2_, BlockPos p_test_3_) {
      return !p_test_1_.getFluidState().isEmpty();
   }

   public static LiquidBlockMatcher getInstance() {
      return INSTANCE;
   }
}