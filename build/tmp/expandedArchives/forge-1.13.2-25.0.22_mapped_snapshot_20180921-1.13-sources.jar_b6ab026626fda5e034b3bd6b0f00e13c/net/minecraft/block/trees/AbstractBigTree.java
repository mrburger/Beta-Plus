package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public abstract class AbstractBigTree extends AbstractTree {
   public boolean spawn(IWorld worldIn, BlockPos pos, IBlockState blockUnder, Random random) {
      for(int i = 0; i >= -1; --i) {
         for(int j = 0; j >= -1; --j) {
            if (canBigTreeSpawnAt(blockUnder, worldIn, pos, i, j)) {
               return this.spawnBigTree(worldIn, pos, blockUnder, random, i, j);
            }
         }
      }

      return super.spawn(worldIn, pos, blockUnder, random);
   }

   @Nullable
   protected abstract AbstractTreeFeature<NoFeatureConfig> getBigTreeFeature(Random random);

   public boolean spawnBigTree(IWorld worldIn, BlockPos pos, IBlockState blockUnder, Random random, int xOffset, int zOffset) {
      AbstractTreeFeature<NoFeatureConfig> abstracttreefeature = this.getBigTreeFeature(random);
      if (abstracttreefeature == null) {
         return false;
      } else {
         IBlockState iblockstate = Blocks.AIR.getDefaultState();
         worldIn.setBlockState(pos.add(xOffset, 0, zOffset), iblockstate, 4);
         worldIn.setBlockState(pos.add(xOffset + 1, 0, zOffset), iblockstate, 4);
         worldIn.setBlockState(pos.add(xOffset, 0, zOffset + 1), iblockstate, 4);
         worldIn.setBlockState(pos.add(xOffset + 1, 0, zOffset + 1), iblockstate, 4);
         if (abstracttreefeature.func_212245_a(worldIn, worldIn.getChunkProvider().getChunkGenerator(), random, pos.add(xOffset, 0, zOffset), IFeatureConfig.NO_FEATURE_CONFIG)) {
            return true;
         } else {
            worldIn.setBlockState(pos.add(xOffset, 0, zOffset), blockUnder, 4);
            worldIn.setBlockState(pos.add(xOffset + 1, 0, zOffset), blockUnder, 4);
            worldIn.setBlockState(pos.add(xOffset, 0, zOffset + 1), blockUnder, 4);
            worldIn.setBlockState(pos.add(xOffset + 1, 0, zOffset + 1), blockUnder, 4);
            return false;
         }
      }
   }

   public static boolean canBigTreeSpawnAt(IBlockState blockUnder, IBlockReader worldIn, BlockPos pos, int xOffset, int zOffset) {
      Block block = blockUnder.getBlock();
      return block == worldIn.getBlockState(pos.add(xOffset, 0, zOffset)).getBlock() && block == worldIn.getBlockState(pos.add(xOffset + 1, 0, zOffset)).getBlock() && block == worldIn.getBlockState(pos.add(xOffset, 0, zOffset + 1)).getBlock() && block == worldIn.getBlockState(pos.add(xOffset + 1, 0, zOffset + 1)).getBlock();
   }
}