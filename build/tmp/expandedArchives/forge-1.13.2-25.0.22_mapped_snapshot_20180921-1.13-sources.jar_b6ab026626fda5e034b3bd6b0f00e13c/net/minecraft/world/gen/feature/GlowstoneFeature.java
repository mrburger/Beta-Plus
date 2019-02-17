package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class GlowstoneFeature extends Feature<NoFeatureConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      if (!p_212245_1_.isAirBlock(p_212245_4_)) {
         return false;
      } else if (p_212245_1_.getBlockState(p_212245_4_.up()).getBlock() != Blocks.NETHERRACK) {
         return false;
      } else {
         p_212245_1_.setBlockState(p_212245_4_, Blocks.GLOWSTONE.getDefaultState(), 2);

         for(int i = 0; i < 1500; ++i) {
            BlockPos blockpos = p_212245_4_.add(p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8), -p_212245_3_.nextInt(12), p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8));
            if (p_212245_1_.getBlockState(blockpos).isAir(p_212245_1_, blockpos)) {
               int j = 0;

               for(EnumFacing enumfacing : EnumFacing.values()) {
                  if (p_212245_1_.getBlockState(blockpos.offset(enumfacing)).getBlock() == Blocks.GLOWSTONE) {
                     ++j;
                  }

                  if (j > 1) {
                     break;
                  }
               }

               if (j == 1) {
                  p_212245_1_.setBlockState(blockpos, Blocks.GLOWSTONE.getDefaultState(), 2);
               }
            }
         }

         return true;
      }
   }
}