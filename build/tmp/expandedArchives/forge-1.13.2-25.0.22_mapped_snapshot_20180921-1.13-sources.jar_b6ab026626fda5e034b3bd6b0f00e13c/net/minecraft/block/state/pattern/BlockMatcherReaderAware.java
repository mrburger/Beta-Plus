package net.minecraft.block.state.pattern;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockMatcherReaderAware implements IBlockMatcherReaderAware<IBlockState> {
   private final Block block;

   public BlockMatcherReaderAware(Block blockIn) {
      this.block = blockIn;
   }

   public static BlockMatcherReaderAware forBlock(Block blockIn) {
      return new BlockMatcherReaderAware(blockIn);
   }

   public boolean test(@Nullable IBlockState p_test_1_, IBlockReader p_test_2_, BlockPos p_test_3_) {
      return p_test_1_ != null && p_test_1_.getBlock() == this.block;
   }
}