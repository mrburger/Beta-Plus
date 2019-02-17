package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class TallGrassFeature extends Feature<TallGrassConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, TallGrassConfig p_212245_5_) {
      for(IBlockState iblockstate = p_212245_1_.getBlockState(p_212245_4_); (iblockstate.isAir(p_212245_1_, p_212245_4_) || iblockstate.isIn(BlockTags.LEAVES)) && p_212245_4_.getY() > 0; iblockstate = p_212245_1_.getBlockState(p_212245_4_)) {
         p_212245_4_ = p_212245_4_.down();
      }

      int i = 0;

      for(int j = 0; j < 128; ++j) {
         BlockPos blockpos = p_212245_4_.add(p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8), p_212245_3_.nextInt(4) - p_212245_3_.nextInt(4), p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8));
         if (p_212245_1_.isAirBlock(blockpos) && p_212245_5_.state.isValidPosition(p_212245_1_, blockpos)) {
            p_212245_1_.setBlockState(blockpos, p_212245_5_.state, 2);
            ++i;
         }
      }

      return i > 0;
   }
}