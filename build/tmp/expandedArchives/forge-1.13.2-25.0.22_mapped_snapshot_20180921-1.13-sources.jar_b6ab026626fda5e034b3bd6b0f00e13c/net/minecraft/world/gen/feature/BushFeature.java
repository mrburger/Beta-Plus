package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class BushFeature extends Feature<BushConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, BushConfig p_212245_5_) {
      int i = 0;
      IBlockState iblockstate = p_212245_5_.block.getDefaultState();

      for(int j = 0; j < 64; ++j) {
         BlockPos blockpos = p_212245_4_.add(p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8), p_212245_3_.nextInt(4) - p_212245_3_.nextInt(4), p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8));
         if (p_212245_1_.isAirBlock(blockpos) && (!p_212245_1_.getDimension().isNether() || blockpos.getY() < p_212245_1_.getWorld().getHeight() - 1) && iblockstate.isValidPosition(p_212245_1_, blockpos)) {
            p_212245_1_.setBlockState(blockpos, iblockstate, 2);
            ++i;
         }
      }

      return i > 0;
   }
}