package net.minecraft.block.state.pattern;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class LightEmittingMatcher implements IBlockMatcherReaderAware<IBlockState> {
   private static final LightEmittingMatcher INSTANCE = new LightEmittingMatcher();

   public boolean test(@Nullable IBlockState p_test_1_, IBlockReader p_test_2_, BlockPos p_test_3_) {
      return p_test_1_ != null && p_test_1_.getOpacity(p_test_2_, p_test_3_) == 0;
   }

   public static LightEmittingMatcher getInstance() {
      return INSTANCE;
   }
}