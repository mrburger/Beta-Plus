package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class SwampFlowersFeature extends AbstractFlowersFeature {
   public IBlockState getRandomFlower(Random p_202355_1_, BlockPos p_202355_2_) {
      return Blocks.BLUE_ORCHID.getDefaultState();
   }
}