package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class LiquidsFeature extends Feature<LiquidsConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, LiquidsConfig p_212245_5_) {
      if (!Block.isRock(p_212245_1_.getBlockState(p_212245_4_.up()).getBlock())) {
         return false;
      } else if (!Block.isRock(p_212245_1_.getBlockState(p_212245_4_.down()).getBlock())) {
         return false;
      } else {
         IBlockState iblockstate = p_212245_1_.getBlockState(p_212245_4_);
         if (!iblockstate.isAir(p_212245_1_, p_212245_4_) && !Block.isRock(iblockstate.getBlock())) {
            return false;
         } else {
            int i = 0;
            int j = 0;
            if (Block.isRock(p_212245_1_.getBlockState(p_212245_4_.west()).getBlock())) {
               ++j;
            }

            if (Block.isRock(p_212245_1_.getBlockState(p_212245_4_.east()).getBlock())) {
               ++j;
            }

            if (Block.isRock(p_212245_1_.getBlockState(p_212245_4_.north()).getBlock())) {
               ++j;
            }

            if (Block.isRock(p_212245_1_.getBlockState(p_212245_4_.south()).getBlock())) {
               ++j;
            }

            int k = 0;
            if (p_212245_1_.isAirBlock(p_212245_4_.west())) {
               ++k;
            }

            if (p_212245_1_.isAirBlock(p_212245_4_.east())) {
               ++k;
            }

            if (p_212245_1_.isAirBlock(p_212245_4_.north())) {
               ++k;
            }

            if (p_212245_1_.isAirBlock(p_212245_4_.south())) {
               ++k;
            }

            if (j == 3 && k == 1) {
               p_212245_1_.setBlockState(p_212245_4_, p_212245_5_.field_202459_a.getDefaultState().getBlockState(), 2);
               p_212245_1_.getPendingFluidTicks().scheduleTick(p_212245_4_, p_212245_5_.field_202459_a, 0);
               ++i;
            }

            return i > 0;
         }
      }
   }
}