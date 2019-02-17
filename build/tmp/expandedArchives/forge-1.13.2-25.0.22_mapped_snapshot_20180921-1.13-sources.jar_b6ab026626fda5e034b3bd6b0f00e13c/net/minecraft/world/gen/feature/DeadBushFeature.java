package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class DeadBushFeature extends Feature<NoFeatureConfig> {
   private static final BlockDeadBush field_197166_a = (BlockDeadBush)Blocks.DEAD_BUSH;

   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      for(IBlockState iblockstate = p_212245_1_.getBlockState(p_212245_4_); (iblockstate.isAir(p_212245_1_, p_212245_4_) || iblockstate.isIn(BlockTags.LEAVES)) && p_212245_4_.getY() > 0; iblockstate = p_212245_1_.getBlockState(p_212245_4_)) {
         p_212245_4_ = p_212245_4_.down();
      }

      IBlockState iblockstate1 = field_197166_a.getDefaultState();

      for(int i = 0; i < 4; ++i) {
         BlockPos blockpos = p_212245_4_.add(p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8), p_212245_3_.nextInt(4) - p_212245_3_.nextInt(4), p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8));
         if (p_212245_1_.isAirBlock(blockpos) && iblockstate1.isValidPosition(p_212245_1_, blockpos)) {
            p_212245_1_.setBlockState(blockpos, iblockstate1, 2);
         }
      }

      return true;
   }
}