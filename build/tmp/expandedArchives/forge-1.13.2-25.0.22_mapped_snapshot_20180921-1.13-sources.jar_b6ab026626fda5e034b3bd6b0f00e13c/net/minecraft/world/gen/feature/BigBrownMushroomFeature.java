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

public class BigBrownMushroomFeature extends Feature<NoFeatureConfig> {
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

            for(int k = 0; k <= 1 + i; ++k) {
               int l = k <= 3 ? 0 : 3;

               for(int i1 = -l; i1 <= l; ++i1) {
                  for(int j1 = -l; j1 <= l; ++j1) {
                     IBlockState iblockstate = p_212245_1_.getBlockState(blockpos$mutableblockpos.setPos(p_212245_4_).move(i1, k, j1));
                     if (!iblockstate.isAir(p_212245_1_, blockpos$mutableblockpos) && !iblockstate.isIn(BlockTags.LEAVES)) {
                        return false;
                     }
                  }
               }
            }

            IBlockState iblockstate1 = Blocks.BROWN_MUSHROOM_BLOCK.getDefaultState().with(BlockHugeMushroom.UP, Boolean.valueOf(true)).with(BlockHugeMushroom.DOWN, Boolean.valueOf(false));
            int k1 = 3;

            for(int l1 = -3; l1 <= 3; ++l1) {
               for(int i2 = -3; i2 <= 3; ++i2) {
                  boolean flag9 = l1 == -3;
                  boolean flag = l1 == 3;
                  boolean flag1 = i2 == -3;
                  boolean flag2 = i2 == 3;
                  boolean flag3 = flag9 || flag;
                  boolean flag4 = flag1 || flag2;
                  if (!flag3 || !flag4) {
                     blockpos$mutableblockpos.setPos(p_212245_4_).move(l1, i, i2);
                     if (p_212245_1_.getBlockState(blockpos$mutableblockpos).canBeReplacedByLeaves(p_212245_1_, blockpos$mutableblockpos)) {
                        boolean flag5 = flag9 || flag4 && l1 == -2;
                        boolean flag6 = flag || flag4 && l1 == 2;
                        boolean flag7 = flag1 || flag3 && i2 == -2;
                        boolean flag8 = flag2 || flag3 && i2 == 2;
                        this.setBlockState(p_212245_1_, blockpos$mutableblockpos, iblockstate1.with(BlockHugeMushroom.WEST, Boolean.valueOf(flag5)).with(BlockHugeMushroom.EAST, Boolean.valueOf(flag6)).with(BlockHugeMushroom.NORTH, Boolean.valueOf(flag7)).with(BlockHugeMushroom.SOUTH, Boolean.valueOf(flag8)));
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