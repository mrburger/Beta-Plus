package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class IceSpikeFeature extends Feature<NoFeatureConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      while(p_212245_1_.isAirBlock(p_212245_4_) && p_212245_4_.getY() > 2) {
         p_212245_4_ = p_212245_4_.down();
      }

      if (p_212245_1_.getBlockState(p_212245_4_).getBlock() != Blocks.SNOW_BLOCK) {
         return false;
      } else {
         p_212245_4_ = p_212245_4_.up(p_212245_3_.nextInt(4));
         int i = p_212245_3_.nextInt(4) + 7;
         int j = i / 4 + p_212245_3_.nextInt(2);
         if (j > 1 && p_212245_3_.nextInt(60) == 0) {
            p_212245_4_ = p_212245_4_.up(10 + p_212245_3_.nextInt(30));
         }

         for(int k = 0; k < i; ++k) {
            float f = (1.0F - (float)k / (float)i) * (float)j;
            int l = MathHelper.ceil(f);

            for(int i1 = -l; i1 <= l; ++i1) {
               float f1 = (float)MathHelper.abs(i1) - 0.25F;

               for(int j1 = -l; j1 <= l; ++j1) {
                  float f2 = (float)MathHelper.abs(j1) - 0.25F;
                  if ((i1 == 0 && j1 == 0 || !(f1 * f1 + f2 * f2 > f * f)) && (i1 != -l && i1 != l && j1 != -l && j1 != l || !(p_212245_3_.nextFloat() > 0.75F))) {
                     IBlockState iblockstate = p_212245_1_.getBlockState(p_212245_4_.add(i1, k, j1));
                     Block block = iblockstate.getBlock();
                     if (iblockstate.isAir(p_212245_1_, p_212245_4_.add(i1, k, j1)) || Block.isDirt(block) || block == Blocks.SNOW_BLOCK || block == Blocks.ICE) {
                        this.setBlockState(p_212245_1_, p_212245_4_.add(i1, k, j1), Blocks.PACKED_ICE.getDefaultState());
                     }

                     if (k != 0 && l > 1) {
                        iblockstate = p_212245_1_.getBlockState(p_212245_4_.add(i1, -k, j1));
                        block = iblockstate.getBlock();
                        if (iblockstate.isAir(p_212245_1_, p_212245_4_.add(i1, -k, j1)) || Block.isDirt(block) || block == Blocks.SNOW_BLOCK || block == Blocks.ICE) {
                           this.setBlockState(p_212245_1_, p_212245_4_.add(i1, -k, j1), Blocks.PACKED_ICE.getDefaultState());
                        }
                     }
                  }
               }
            }
         }

         int k1 = j - 1;
         if (k1 < 0) {
            k1 = 0;
         } else if (k1 > 1) {
            k1 = 1;
         }

         for(int l1 = -k1; l1 <= k1; ++l1) {
            for(int i2 = -k1; i2 <= k1; ++i2) {
               BlockPos blockpos = p_212245_4_.add(l1, -1, i2);
               int j2 = 50;
               if (Math.abs(l1) == 1 && Math.abs(i2) == 1) {
                  j2 = p_212245_3_.nextInt(5);
               }

               while(blockpos.getY() > 50) {
                  IBlockState iblockstate1 = p_212245_1_.getBlockState(blockpos);
                  Block block1 = iblockstate1.getBlock();
                  if (!iblockstate1.isAir(p_212245_1_, blockpos) && !Block.isDirt(block1) && block1 != Blocks.SNOW_BLOCK && block1 != Blocks.ICE && block1 != Blocks.PACKED_ICE) {
                     break;
                  }

                  this.setBlockState(p_212245_1_, blockpos, Blocks.PACKED_ICE.getDefaultState());
                  blockpos = blockpos.down();
                  --j2;
                  if (j2 <= 0) {
                     blockpos = blockpos.down(p_212245_3_.nextInt(5) + 1);
                     j2 = p_212245_3_.nextInt(5);
                  }
               }
            }
         }

         return true;
      }
   }
}