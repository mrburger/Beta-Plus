package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockSeaPickle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.placement.CountConfig;

public class SeaPickleFeature extends Feature<CountConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<?> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, CountConfig p_212245_5_) {
      int i = 0;

      for(int j = 0; j < p_212245_5_.count; ++j) {
         int k = p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8);
         int l = p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8);
         int i1 = p_212245_1_.getHeight(Heightmap.Type.OCEAN_FLOOR, p_212245_4_.getX() + k, p_212245_4_.getZ() + l);
         BlockPos blockpos = new BlockPos(p_212245_4_.getX() + k, i1, p_212245_4_.getZ() + l);
         IBlockState iblockstate = Blocks.SEA_PICKLE.getDefaultState().with(BlockSeaPickle.PICKLES, Integer.valueOf(p_212245_3_.nextInt(4) + 1));
         if (p_212245_1_.getBlockState(blockpos).getBlock() == Blocks.WATER && iblockstate.isValidPosition(p_212245_1_, blockpos)) {
            p_212245_1_.setBlockState(blockpos, iblockstate, 2);
            ++i;
         }
      }

      return i > 0;
   }
}