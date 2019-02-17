package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class RandomCountWithRange extends BasePlacement<CountRangeConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, CountRangeConfig placementConfig, Feature<C> featureIn, C featureConfig) {
      int i = random.nextInt(Math.max(placementConfig.count, 1));

      for(int j = 0; j < i; ++j) {
         int k = random.nextInt(16);
         int l = random.nextInt(placementConfig.maxHeight - placementConfig.maxHeightBase) + placementConfig.minHeight;
         int i1 = random.nextInt(16);
         featureIn.func_212245_a(worldIn, chunkGenerator, random, pos.add(k, l, i1), featureConfig);
      }

      return true;
   }
}