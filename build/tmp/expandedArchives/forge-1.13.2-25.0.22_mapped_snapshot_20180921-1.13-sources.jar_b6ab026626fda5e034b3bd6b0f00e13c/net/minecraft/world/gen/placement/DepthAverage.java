package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class DepthAverage extends BasePlacement<DepthAverageConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, DepthAverageConfig placementConfig, Feature<C> featureIn, C featureConfig) {
      int i = placementConfig.count;
      int j = placementConfig.averageHeight;
      int k = placementConfig.heightSpread;

      for(int l = 0; l < i; ++l) {
         int i1 = random.nextInt(16);
         int j1 = random.nextInt(k) + random.nextInt(k) - k + j;
         int k1 = random.nextInt(16);
         BlockPos blockpos = pos.add(i1, j1, k1);
         featureIn.func_212245_a(worldIn, chunkGenerator, random, blockpos, featureConfig);
      }

      return true;
   }
}