package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class HellLavaFeature extends Feature<HellLavaConfig> {
   private static final IBlockState field_205173_a = Blocks.NETHERRACK.getDefaultState();

   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, HellLavaConfig p_212245_5_) {
      if (p_212245_1_.getBlockState(p_212245_4_.up()) != field_205173_a) {
         return false;
      } else if (!p_212245_1_.isAirBlock(p_212245_4_) && p_212245_1_.getBlockState(p_212245_4_) != field_205173_a) {
         return false;
      } else {
         int i = 0;
         if (p_212245_1_.getBlockState(p_212245_4_.west()) == field_205173_a) {
            ++i;
         }

         if (p_212245_1_.getBlockState(p_212245_4_.east()) == field_205173_a) {
            ++i;
         }

         if (p_212245_1_.getBlockState(p_212245_4_.north()) == field_205173_a) {
            ++i;
         }

         if (p_212245_1_.getBlockState(p_212245_4_.south()) == field_205173_a) {
            ++i;
         }

         if (p_212245_1_.getBlockState(p_212245_4_.down()) == field_205173_a) {
            ++i;
         }

         int j = 0;
         if (p_212245_1_.isAirBlock(p_212245_4_.west())) {
            ++j;
         }

         if (p_212245_1_.isAirBlock(p_212245_4_.east())) {
            ++j;
         }

         if (p_212245_1_.isAirBlock(p_212245_4_.north())) {
            ++j;
         }

         if (p_212245_1_.isAirBlock(p_212245_4_.south())) {
            ++j;
         }

         if (p_212245_1_.isAirBlock(p_212245_4_.down())) {
            ++j;
         }

         if (!p_212245_5_.field_202437_a && i == 4 && j == 1 || i == 5) {
            p_212245_1_.setBlockState(p_212245_4_, Blocks.LAVA.getDefaultState(), 2);
            p_212245_1_.getPendingFluidTicks().scheduleTick(p_212245_4_, Fluids.LAVA, 0);
         }

         return true;
      }
   }
}