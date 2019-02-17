package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class DoublePlantFeature extends Feature<DoublePlantConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, DoublePlantConfig p_212245_5_) {
      boolean flag = false;

      for(int i = 0; i < 64; ++i) {
         BlockPos blockpos = p_212245_4_.add(p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8), p_212245_3_.nextInt(4) - p_212245_3_.nextInt(4), p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8));
         if (p_212245_1_.isAirBlock(blockpos) && blockpos.getY() < p_212245_1_.getWorld().getHeight() - 2 && p_212245_5_.state.isValidPosition(p_212245_1_, blockpos)) {
            ((BlockDoublePlant)p_212245_5_.state.getBlock()).placeAt(p_212245_1_, blockpos, 2);
            flag = true;
         }
      }

      return flag;
   }
}