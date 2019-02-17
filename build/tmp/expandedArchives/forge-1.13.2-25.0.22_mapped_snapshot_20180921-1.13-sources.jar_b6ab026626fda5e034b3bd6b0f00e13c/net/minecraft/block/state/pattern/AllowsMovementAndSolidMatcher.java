package net.minecraft.block.state.pattern;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class AllowsMovementAndSolidMatcher implements IBlockMatcherReaderAware<IBlockState> {
   private static final AllowsMovementAndSolidMatcher INSTANCE = new AllowsMovementAndSolidMatcher();

   public boolean test(@Nullable IBlockState p_test_1_, IBlockReader p_test_2_, BlockPos p_test_3_) {
      return p_test_1_ != null && !p_test_1_.getMaterial().blocksMovement() && p_test_1_.getFluidState().isEmpty();
   }

   public static AllowsMovementAndSolidMatcher getInstance() {
      return INSTANCE;
   }
}