package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class ReedFeature extends Feature<NoFeatureConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      int i = 0;

      for(int j = 0; j < 20; ++j) {
         BlockPos blockpos = p_212245_4_.add(p_212245_3_.nextInt(4) - p_212245_3_.nextInt(4), 0, p_212245_3_.nextInt(4) - p_212245_3_.nextInt(4));
         if (p_212245_1_.isAirBlock(blockpos)) {
            BlockPos blockpos1 = blockpos.down();
            if (p_212245_1_.getFluidState(blockpos1.west()).isTagged(FluidTags.WATER) || p_212245_1_.getFluidState(blockpos1.east()).isTagged(FluidTags.WATER) || p_212245_1_.getFluidState(blockpos1.north()).isTagged(FluidTags.WATER) || p_212245_1_.getFluidState(blockpos1.south()).isTagged(FluidTags.WATER)) {
               int k = 2 + p_212245_3_.nextInt(p_212245_3_.nextInt(3) + 1);
               for(int l = 0; l < k; ++l) {
                  if (Blocks.SUGAR_CANE.getDefaultState().isValidPosition(p_212245_1_, blockpos)) {
                     p_212245_1_.setBlockState(blockpos.up(l), Blocks.SUGAR_CANE.getDefaultState(), 2);
                     ++i;
                  }
               }
            }
         }
      }

      return i > 0;
   }
}