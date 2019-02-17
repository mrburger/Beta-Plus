package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.storage.loot.LootTableList;

public class BonusChestFeature extends Feature<NoFeatureConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      for(IBlockState iblockstate = p_212245_1_.getBlockState(p_212245_4_); (iblockstate.isAir(p_212245_1_, p_212245_4_) || iblockstate.isIn(BlockTags.LEAVES)) && p_212245_4_.getY() > 1; iblockstate = p_212245_1_.getBlockState(p_212245_4_)) {
         p_212245_4_ = p_212245_4_.down();
      }

      if (p_212245_4_.getY() < 1) {
         return false;
      } else {
         p_212245_4_ = p_212245_4_.up();

         for(int i = 0; i < 4; ++i) {
            BlockPos blockpos = p_212245_4_.add(p_212245_3_.nextInt(4) - p_212245_3_.nextInt(4), p_212245_3_.nextInt(3) - p_212245_3_.nextInt(3), p_212245_3_.nextInt(4) - p_212245_3_.nextInt(4));
            if (p_212245_1_.isAirBlock(blockpos) && p_212245_1_.getBlockState(blockpos.down()).isTopSolid(p_212245_1_.getWorld(), blockpos.down())) {
               p_212245_1_.setBlockState(blockpos, Blocks.CHEST.getDefaultState(), 2);
               TileEntityLockableLoot.setLootTable(p_212245_1_, p_212245_3_, blockpos, LootTableList.CHESTS_SPAWN_BONUS_CHEST);
               BlockPos blockpos1 = blockpos.east();
               BlockPos blockpos2 = blockpos.west();
               BlockPos blockpos3 = blockpos.north();
               BlockPos blockpos4 = blockpos.south();
               if (p_212245_1_.isAirBlock(blockpos2) && p_212245_1_.getBlockState(blockpos2.down()).isTopSolid(p_212245_1_.getWorld(), blockpos2.down())) {
                  p_212245_1_.setBlockState(blockpos2, Blocks.TORCH.getDefaultState(), 2);
               }

               if (p_212245_1_.isAirBlock(blockpos1) && p_212245_1_.getBlockState(blockpos1.down()).isTopSolid(p_212245_1_.getWorld(), blockpos1.down())) {
                  p_212245_1_.setBlockState(blockpos1, Blocks.TORCH.getDefaultState(), 2);
               }

               if (p_212245_1_.isAirBlock(blockpos3) && p_212245_1_.getBlockState(blockpos3.down()).isTopSolid(p_212245_1_.getWorld(), blockpos3.down())) {
                  p_212245_1_.setBlockState(blockpos3, Blocks.TORCH.getDefaultState(), 2);
               }

               if (p_212245_1_.isAirBlock(blockpos4) && p_212245_1_.getBlockState(blockpos4.down()).isTopSolid(p_212245_1_.getWorld(), blockpos4.down())) {
                  p_212245_1_.setBlockState(blockpos4, Blocks.TORCH.getDefaultState(), 2);
               }

               return true;
            }
         }

         return false;
      }
   }
}