package net.minecraft.block.state.pattern;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface IBlockMatcherReaderAware<T> {
   boolean test(T p_test_1_, IBlockReader p_test_2_, BlockPos p_test_3_);
}