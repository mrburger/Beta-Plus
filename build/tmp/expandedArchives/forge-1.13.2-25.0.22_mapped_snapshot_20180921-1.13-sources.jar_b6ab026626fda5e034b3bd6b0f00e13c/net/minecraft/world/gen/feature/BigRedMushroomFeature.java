package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class BigRedMushroomFeature extends Feature<NoFeatureConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      int i = p_212245_3_.nextInt(3) + 4;
      if (p_212245_3_.nextInt(12) == 0) {
         i *= 2;
      }

      int j = p_212245_4_.getY();
      if (j >= 1 && j + i + 1 < 256) {
         Block block = p_212245_1_.getBlockState(p_212245_4_.down()).getBlock();
         if (!Block.isDirt(block) && block != Blocks.GRASS_BLOCK && block != Blocks.MYCELIUM) {
            return false;
         } else {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for(int k = 0; k <= i; ++k) {
               int l = 0;
               if (k < i && k >= i - 3) {
                  l = 2;
               } else if (k == i) {
                  l = 1;
               }

               for(int i1 = -l; i1 <= l; ++i1) {
                  for(int j1 = -l; j1 <= l; ++j1) {
                     IBlockState iblockstate = p_212245_1_.getBlockState(blockpos$mutableblockpos.setPos(p_212245_4_).move(i1, k, j1));
                     if (!iblockstate.isAir(p_212245_1_, blockpos$mutableblockpos) && !iblockstate.isIn(BlockTags.LEAVES)) {
                        return false;
                     }
                  }
               }
            }

            IBlockState iblockstate1 = Blocks.RED_MUSHROOM_BLOCK.getDefaultState().with(BlockHugeMushroom.DOWN, Boolean.valueOf(false));

            for(int l1 = i - 3; l1 <= i; ++l1) {
               int i2 = l1 < i ? 2 : 1;
               int k2 = 0;

               for(int l2 = -i2; l2 <= i2; ++l2) {
                  for(int k1 = -i2; k1 <= i2; ++k1) {
                     boolean flag = l2 == -i2;
                     boolean flag1 = l2 == i2;
                     boolean flag2 = k1 == -i2;
                     boolean flag3 = k1 == i2;
                     boolean flag4 = flag || flag1;
                     boolean flag5 = flag2 || flag3;
                     if (l1 >= i || flag4 != flag5) {
                        blockpos$mutableblockpos.setPos(p_212245_4_).move(l2, l1, k1);
                        if (p_212245_1_.getBlockState(blockpos$mutableblockpos).canBeReplacedByLeaves(p_212245_1_, blockpos$mutableblockpos)) {
                           this.setBlockState(p_212245_1_, blockpos$mutableblockpos, iblockstate1.with(BlockHugeMushroom.UP, Boolean.valueOf(l1 >= i - 1)).with(BlockHugeMushroom.WEST, Boolean.valueOf(l2 < 0)).with(BlockHugeMushroom.EAST, Boolean.valueOf(l2 > 0)).with(BlockHugeMushroom.NORTH, Boolean.valueOf(k1 < 0)).with(BlockHugeMushroom.SOUTH, Boolean.valueOf(k1 > 0)));
                        }
                     }
                  }
               }
            }

            IBlockState iblockstate2 = Blocks.MUSHROOM_STEM.getDefaultState().with(BlockHugeMushroom.UP, Boolean.valueOf(false)).with(BlockHugeMushroom.DOWN, Boolean.valueOf(false));

            for(int j2 = 0; j2 < i; ++j2) {
               blockpos$mutableblockpos.setPos(p_212245_4_).move(EnumFacing.UP, j2);
               if (p_212245_1_.getBlockState(blockpos$mutableblockpos).canBeReplacedByLeaves(p_212245_1_, blockpos$mutableblockpos)) {
                  this.setBlockState(p_212245_1_, blockpos$mutableblockpos, iblockstate2);
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }
}