package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class BlockBlobFeature extends Feature<BlockBlobConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, BlockBlobConfig p_212245_5_) {
      while(true) {
         label50: {
            if (p_212245_4_.getY() > 3) {
               if (p_212245_1_.isAirBlock(p_212245_4_.down())) {
                  break label50;
               }

               Block block = p_212245_1_.getBlockState(p_212245_4_.down()).getBlock();
               if (block != Blocks.GRASS_BLOCK && !Block.isDirt(block) && !Block.isRock(block)) {
                  break label50;
               }
            }

            if (p_212245_4_.getY() <= 3) {
               return false;
            }

            int i1 = p_212245_5_.field_202464_b;

            for(int i = 0; i1 >= 0 && i < 3; ++i) {
               int j = i1 + p_212245_3_.nextInt(2);
               int k = i1 + p_212245_3_.nextInt(2);
               int l = i1 + p_212245_3_.nextInt(2);
               float f = (float)(j + k + l) * 0.333F + 0.5F;

               for(BlockPos blockpos : BlockPos.getAllInBox(p_212245_4_.add(-j, -k, -l), p_212245_4_.add(j, k, l))) {
                  if (blockpos.distanceSq(p_212245_4_) <= (double)(f * f)) {
                     p_212245_1_.setBlockState(blockpos, p_212245_5_.block.getDefaultState(), 4);
                  }
               }

               p_212245_4_ = p_212245_4_.add(-(i1 + 1) + p_212245_3_.nextInt(2 + i1 * 2), 0 - p_212245_3_.nextInt(2), -(i1 + 1) + p_212245_3_.nextInt(2 + i1 * 2));
            }

            return true;
         }

         p_212245_4_ = p_212245_4_.down();
      }
   }
}