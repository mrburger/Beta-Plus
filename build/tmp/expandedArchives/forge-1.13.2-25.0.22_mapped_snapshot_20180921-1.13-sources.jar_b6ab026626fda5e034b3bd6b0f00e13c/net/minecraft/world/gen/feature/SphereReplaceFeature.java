package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class SphereReplaceFeature extends Feature<SphereReplaceConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, SphereReplaceConfig p_212245_5_) {
      if (!p_212245_1_.getFluidState(p_212245_4_).isTagged(FluidTags.WATER)) {
         return false;
      } else {
         int i = 0;
         int j = p_212245_3_.nextInt(p_212245_5_.field_202432_b - 2) + 2;

         for(int k = p_212245_4_.getX() - j; k <= p_212245_4_.getX() + j; ++k) {
            for(int l = p_212245_4_.getZ() - j; l <= p_212245_4_.getZ() + j; ++l) {
               int i1 = k - p_212245_4_.getX();
               int j1 = l - p_212245_4_.getZ();
               if (i1 * i1 + j1 * j1 <= j * j) {
                  for(int k1 = p_212245_4_.getY() - p_212245_5_.field_202433_c; k1 <= p_212245_4_.getY() + p_212245_5_.field_202433_c; ++k1) {
                     BlockPos blockpos = new BlockPos(k, k1, l);
                     Block block = p_212245_1_.getBlockState(blockpos).getBlock();
                     if (p_212245_5_.field_202434_d.contains(block)) {
                        p_212245_1_.setBlockState(blockpos, p_212245_5_.field_202431_a.getDefaultState(), 2);
                        ++i;
                     }
                  }
               }
            }
         }

         return i > 0;
      }
   }
}