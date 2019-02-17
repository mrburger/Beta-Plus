package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class BlueIceFeature extends Feature<NoFeatureConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      if (p_212245_4_.getY() > p_212245_1_.getSeaLevel() - 1) {
         return false;
      } else if (p_212245_1_.getBlockState(p_212245_4_).getBlock() != Blocks.WATER && p_212245_1_.getBlockState(p_212245_4_.down()).getBlock() != Blocks.WATER) {
         return false;
      } else {
         boolean flag = false;

         for(EnumFacing enumfacing : EnumFacing.values()) {
            if (enumfacing != EnumFacing.DOWN && p_212245_1_.getBlockState(p_212245_4_.offset(enumfacing)).getBlock() == Blocks.PACKED_ICE) {
               flag = true;
               break;
            }
         }

         if (!flag) {
            return false;
         } else {
            p_212245_1_.setBlockState(p_212245_4_, Blocks.BLUE_ICE.getDefaultState(), 2);

            for(int i = 0; i < 200; ++i) {
               int j = p_212245_3_.nextInt(5) - p_212245_3_.nextInt(6);
               int k = 3;
               if (j < 2) {
                  k += j / 2;
               }

               if (k >= 1) {
                  BlockPos blockpos = p_212245_4_.add(p_212245_3_.nextInt(k) - p_212245_3_.nextInt(k), j, p_212245_3_.nextInt(k) - p_212245_3_.nextInt(k));
                  IBlockState iblockstate = p_212245_1_.getBlockState(blockpos);
                  Block block = iblockstate.getBlock();
                  if (iblockstate.getMaterial() == Material.AIR || block == Blocks.WATER || block == Blocks.PACKED_ICE || block == Blocks.ICE) {
                     for(EnumFacing enumfacing1 : EnumFacing.values()) {
                        Block block1 = p_212245_1_.getBlockState(blockpos.offset(enumfacing1)).getBlock();
                        if (block1 == Blocks.BLUE_ICE) {
                           p_212245_1_.setBlockState(blockpos, Blocks.BLUE_ICE.getDefaultState(), 2);
                           break;
                        }
                     }
                  }
               }
            }

            return true;
         }
      }
   }
}