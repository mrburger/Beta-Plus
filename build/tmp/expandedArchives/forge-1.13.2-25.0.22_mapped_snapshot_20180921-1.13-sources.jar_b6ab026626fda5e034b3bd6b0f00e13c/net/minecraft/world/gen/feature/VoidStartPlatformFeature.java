package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class VoidStartPlatformFeature extends Feature<NoFeatureConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      BlockPos blockpos = p_212245_1_.getSpawnPoint();
      int i = 16;
      double d0 = blockpos.distanceSq(p_212245_4_.add(8, blockpos.getY(), 8));
      if (d0 > 1024.0D) {
         return true;
      } else {
         BlockPos blockpos1 = new BlockPos(blockpos.getX() - 16, Math.max(blockpos.getY(), 4) - 1, blockpos.getZ() - 16);
         BlockPos blockpos2 = new BlockPos(blockpos.getX() + 16, Math.max(blockpos.getY(), 4) - 1, blockpos.getZ() + 16);
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(blockpos1);

         for(int j = p_212245_4_.getZ(); j < p_212245_4_.getZ() + 16; ++j) {
            for(int k = p_212245_4_.getX(); k < p_212245_4_.getX() + 16; ++k) {
               if (j >= blockpos1.getZ() && j <= blockpos2.getZ() && k >= blockpos1.getX() && k <= blockpos2.getX()) {
                  blockpos$mutableblockpos.setPos(k, blockpos$mutableblockpos.getY(), j);
                  if (blockpos.getX() == k && blockpos.getZ() == j) {
                     p_212245_1_.setBlockState(blockpos$mutableblockpos, Blocks.COBBLESTONE.getDefaultState(), 2);
                  } else {
                     p_212245_1_.setBlockState(blockpos$mutableblockpos, Blocks.STONE.getDefaultState(), 2);
                  }
               }
            }
         }

         return true;
      }
   }
}