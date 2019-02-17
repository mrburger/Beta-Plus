package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class WaterlilyFeature extends Feature<NoFeatureConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      BlockPos blockpos1;
      for(BlockPos blockpos = p_212245_4_; blockpos.getY() > 0; blockpos = blockpos1) {
         blockpos1 = blockpos.down();
         if (!p_212245_1_.isAirBlock(blockpos1)) {
            break;
         }
      }

      for(int i = 0; i < 10; ++i) {
         BlockPos blockpos2 = p_212245_4_.add(p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8), p_212245_3_.nextInt(4) - p_212245_3_.nextInt(4), p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8));
         IBlockState iblockstate = Blocks.LILY_PAD.getDefaultState();
         if (p_212245_1_.isAirBlock(blockpos2) && iblockstate.isValidPosition(p_212245_1_, blockpos2)) {
            p_212245_1_.setBlockState(blockpos2, iblockstate, 2);
         }
      }

      return true;
   }
}