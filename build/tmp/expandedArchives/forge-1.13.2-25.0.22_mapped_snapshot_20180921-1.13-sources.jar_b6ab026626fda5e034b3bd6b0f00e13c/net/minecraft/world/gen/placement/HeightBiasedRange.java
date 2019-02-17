package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class HeightBiasedRange extends BasePlacement<CountRangeConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, CountRangeConfig placementConfig, Feature<C> featureIn, C featureConfig) {
      for(int i = 0; i < placementConfig.count; ++i) {
         int j = random.nextInt(16);
         int k = random.nextInt(random.nextInt(placementConfig.maxHeight - placementConfig.maxHeightBase) + placementConfig.minHeight);
         int l = random.nextInt(16);
         featureIn.func_212245_a(worldIn, chunkGenerator, random, pos.add(j, k, l), featureConfig);
      }

      return true;
   }
}