package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockChorusFlower;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class ChorusPlantFeature extends Feature<NoFeatureConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      if (p_212245_1_.isAirBlock(p_212245_4_.up()) && p_212245_1_.getBlockState(p_212245_4_).getBlock() == Blocks.END_STONE) {
         BlockChorusFlower.generatePlant(p_212245_1_, p_212245_4_.up(), p_212245_3_, 8);
         return true;
      } else {
         return false;
      }
   }
}