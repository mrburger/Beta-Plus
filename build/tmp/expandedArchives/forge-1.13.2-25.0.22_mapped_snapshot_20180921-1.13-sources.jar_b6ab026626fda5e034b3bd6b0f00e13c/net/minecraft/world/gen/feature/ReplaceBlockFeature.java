package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class ReplaceBlockFeature extends Feature<ReplaceBlockConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, ReplaceBlockConfig p_212245_5_) {
      if (p_212245_5_.field_202457_a.test(p_212245_1_.getBlockState(p_212245_4_))) {
         p_212245_1_.setBlockState(p_212245_4_, p_212245_5_.state, 2);
      }

      return true;
   }
}