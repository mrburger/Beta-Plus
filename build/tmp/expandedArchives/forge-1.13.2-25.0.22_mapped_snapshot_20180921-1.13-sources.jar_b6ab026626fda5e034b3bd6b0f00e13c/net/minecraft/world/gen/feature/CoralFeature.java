package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockCoralWallFanDead;
import net.minecraft.block.BlockSeaPickle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public abstract class CoralFeature extends Feature<NoFeatureConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      IBlockState iblockstate = BlockTags.CORAL_BLOCKS.getRandomElement(p_212245_3_).getDefaultState();
      return this.func_204623_a(p_212245_1_, p_212245_3_, p_212245_4_, iblockstate);
   }

   protected abstract boolean func_204623_a(IWorld p_204623_1_, Random p_204623_2_, BlockPos p_204623_3_, IBlockState p_204623_4_);

   protected boolean func_204624_b(IWorld p_204624_1_, Random p_204624_2_, BlockPos p_204624_3_, IBlockState p_204624_4_) {
      BlockPos blockpos = p_204624_3_.up();
      IBlockState iblockstate = p_204624_1_.getBlockState(p_204624_3_);
      if ((iblockstate.getBlock() == Blocks.WATER || iblockstate.isIn(BlockTags.CORALS)) && p_204624_1_.getBlockState(blockpos).getBlock() == Blocks.WATER) {
         p_204624_1_.setBlockState(p_204624_3_, p_204624_4_, 3);
         if (p_204624_2_.nextFloat() < 0.25F) {
            p_204624_1_.setBlockState(blockpos, BlockTags.CORALS.getRandomElement(p_204624_2_).getDefaultState(), 2);
         } else if (p_204624_2_.nextFloat() < 0.05F) {
            p_204624_1_.setBlockState(blockpos, Blocks.SEA_PICKLE.getDefaultState().with(BlockSeaPickle.PICKLES, Integer.valueOf(p_204624_2_.nextInt(4) + 1)), 2);
         }

         for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            if (p_204624_2_.nextFloat() < 0.2F) {
               BlockPos blockpos1 = p_204624_3_.offset(enumfacing);
               if (p_204624_1_.getBlockState(blockpos1).getBlock() == Blocks.WATER) {
                  IBlockState iblockstate1 = BlockTags.WALL_CORALS.getRandomElement(p_204624_2_).getDefaultState().with(BlockCoralWallFanDead.FACING, enumfacing);
                  p_204624_1_.setBlockState(blockpos1, iblockstate1, 2);
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }
}