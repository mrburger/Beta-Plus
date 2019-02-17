package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class RandomFeatureWithConfigFeature extends Feature<RandomFeatureWithConfigConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, RandomFeatureWithConfigConfig p_212245_5_) {
      int i = p_212245_3_.nextInt(p_212245_5_.features.length);
      return this.func_204627_a(p_212245_5_.features[i], p_212245_5_.configs[i], p_212245_1_, p_212245_2_, p_212245_3_, p_212245_4_);
   }

   <FC extends IFeatureConfig> boolean func_204627_a(Feature<FC> p_204627_1_, IFeatureConfig p_204627_2_, IWorld p_204627_3_, IChunkGenerator<? extends IChunkGenSettings> p_204627_4_, Random p_204627_5_, BlockPos p_204627_6_) {
      return p_204627_1_.func_212245_a(p_204627_3_, p_204627_4_, p_204627_5_, p_204627_6_, (FC)p_204627_2_);
   }
}