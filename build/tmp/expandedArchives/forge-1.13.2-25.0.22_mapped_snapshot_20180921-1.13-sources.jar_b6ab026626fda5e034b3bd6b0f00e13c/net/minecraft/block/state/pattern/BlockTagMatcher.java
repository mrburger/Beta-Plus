package net.minecraft.block.state.pattern;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tags.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockTagMatcher implements IBlockMatcherReaderAware<IBlockState> {
   private final Tag<Block> tag;

   public BlockTagMatcher(Tag<Block> tagIn) {
      this.tag = tagIn;
   }

   public static BlockTagMatcher forTag(Tag<Block> tagIn) {
      return new BlockTagMatcher(tagIn);
   }

   public boolean test(@Nullable IBlockState p_test_1_, IBlockReader p_test_2_, BlockPos p_test_3_) {
      return p_test_1_ != null && p_test_1_.isIn(this.tag);
   }
}