package net.minecraft.world.gen.feature.template;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface ITemplateProcessor {
   @Nullable
   Template.BlockInfo processBlock(IBlockReader worldIn, BlockPos pos, Template.BlockInfo blockInfoIn);
}