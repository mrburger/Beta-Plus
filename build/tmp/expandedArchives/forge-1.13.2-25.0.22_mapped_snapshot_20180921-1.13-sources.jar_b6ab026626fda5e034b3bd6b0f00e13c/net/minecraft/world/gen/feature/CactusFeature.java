package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class CactusFeature extends Feature<NoFeatureConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      for(int i = 0; i < 10; ++i) {
         BlockPos blockpos = p_212245_4_.add(p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8), p_212245_3_.nextInt(4) - p_212245_3_.nextInt(4), p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8));
         if (p_212245_1_.isAirBlock(blockpos)) {
            int j = 1 + p_212245_3_.nextInt(p_212245_3_.nextInt(3) + 1);

            for(int k = 0; k < j; ++k) {
               if (Blocks.CACTUS.getDefaultState().isValidPosition(p_212245_1_, blockpos)) {
                  p_212245_1_.setBlockState(blockpos.up(k), Blocks.CACTUS.getDefaultState(), 2);
               }
            }
         }
      }

      return true;
   }
}