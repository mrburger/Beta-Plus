package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class JungleGrassFeature extends Feature<NoFeatureConfig> {
   public IBlockState func_202357_a(Random p_202357_1_) {
      return p_202357_1_.nextInt(4) == 0 ? Blocks.FERN.getDefaultState() : Blocks.GRASS.getDefaultState();
   }

   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      IBlockState iblockstate = this.func_202357_a(p_212245_3_);

      for(IBlockState iblockstate1 = p_212245_1_.getBlockState(p_212245_4_); (iblockstate1.isAir(p_212245_1_, p_212245_4_) || iblockstate1.isIn(BlockTags.LEAVES)) && p_212245_4_.getY() > 0; iblockstate1 = p_212245_1_.getBlockState(p_212245_4_)) {
         p_212245_4_ = p_212245_4_.down();
      }

      int i = 0;

      for(int j = 0; j < 128; ++j) {
         BlockPos blockpos = p_212245_4_.add(p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8), p_212245_3_.nextInt(4) - p_212245_3_.nextInt(4), p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8));
         if (p_212245_1_.isAirBlock(blockpos) && iblockstate.isValidPosition(p_212245_1_, blockpos)) {
            p_212245_1_.setBlockState(blockpos, iblockstate, 2);
            ++i;
         }
      }

      return i > 0;
   }
}