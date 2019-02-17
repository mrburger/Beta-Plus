package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockKelpTop;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class KelpFeature extends Feature<NoFeatureConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      int i = 0;
      int j = p_212245_1_.getHeight(Heightmap.Type.OCEAN_FLOOR, p_212245_4_.getX(), p_212245_4_.getZ());
      BlockPos blockpos = new BlockPos(p_212245_4_.getX(), j, p_212245_4_.getZ());
      if (p_212245_1_.getBlockState(blockpos).getBlock() == Blocks.WATER) {
         IBlockState iblockstate = Blocks.KELP.getDefaultState();
         IBlockState iblockstate1 = Blocks.KELP_PLANT.getDefaultState();
         int k = 1 + p_212245_3_.nextInt(10);

         for(int l = 0; l <= k; ++l) {
            if (p_212245_1_.getBlockState(blockpos).getBlock() == Blocks.WATER && p_212245_1_.getBlockState(blockpos.up()).getBlock() == Blocks.WATER && iblockstate1.isValidPosition(p_212245_1_, blockpos)) {
               if (l == k) {
                  p_212245_1_.setBlockState(blockpos, iblockstate.with(BlockKelpTop.AGE, Integer.valueOf(p_212245_3_.nextInt(23))), 2);
                  ++i;
               } else {
                  p_212245_1_.setBlockState(blockpos, iblockstate1, 2);
               }
            } else if (l > 0) {
               BlockPos blockpos1 = blockpos.down();
               if (iblockstate.isValidPosition(p_212245_1_, blockpos1) && p_212245_1_.getBlockState(blockpos1.down()).getBlock() != Blocks.KELP) {
                  p_212245_1_.setBlockState(blockpos1, iblockstate.with(BlockKelpTop.AGE, Integer.valueOf(p_212245_3_.nextInt(23))), 2);
                  ++i;
               }
               break;
            }

            blockpos = blockpos.up();
         }
      }

      return i > 0;
   }
}