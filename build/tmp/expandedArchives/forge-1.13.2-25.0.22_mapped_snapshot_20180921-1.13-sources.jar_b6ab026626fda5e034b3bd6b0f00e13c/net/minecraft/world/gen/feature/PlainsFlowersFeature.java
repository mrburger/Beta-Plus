package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class PlainsFlowersFeature extends AbstractFlowersFeature {
   public IBlockState getRandomFlower(Random p_202355_1_, BlockPos p_202355_2_) {
      double d0 = Biome.INFO_NOISE.getValue((double)p_202355_2_.getX() / 200.0D, (double)p_202355_2_.getZ() / 200.0D);
      if (d0 < -0.8D) {
         int j = p_202355_1_.nextInt(4);
         switch(j) {
         case 0:
            return Blocks.ORANGE_TULIP.getDefaultState();
         case 1:
            return Blocks.RED_TULIP.getDefaultState();
         case 2:
            return Blocks.PINK_TULIP.getDefaultState();
         case 3:
         default:
            return Blocks.WHITE_TULIP.getDefaultState();
         }
      } else if (p_202355_1_.nextInt(3) > 0) {
         int i = p_202355_1_.nextInt(3);
         if (i == 0) {
            return Blocks.POPPY.getDefaultState();
         } else {
            return i == 1 ? Blocks.AZURE_BLUET.getDefaultState() : Blocks.OXEYE_DAISY.getDefaultState();
         }
      } else {
         return Blocks.DANDELION.getDefaultState();
      }
   }
}