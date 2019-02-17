package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public abstract class AbstractFlowersFeature extends Feature<NoFeatureConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      IBlockState iblockstate = this.getRandomFlower(p_212245_3_, p_212245_4_);
      int i = 0;

      for(int j = 0; j < 64; ++j) {
         BlockPos blockpos = p_212245_4_.add(p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8), p_212245_3_.nextInt(4) - p_212245_3_.nextInt(4), p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8));
         if (p_212245_1_.isAirBlock(blockpos) && blockpos.getY() < 255 && iblockstate.isValidPosition(p_212245_1_, blockpos)) {
            p_212245_1_.setBlockState(blockpos, iblockstate, 2);
            ++i;
         }
      }

      return i > 0;
   }

   public abstract IBlockState getRandomFlower(Random p_202355_1_, BlockPos p_202355_2_);
}