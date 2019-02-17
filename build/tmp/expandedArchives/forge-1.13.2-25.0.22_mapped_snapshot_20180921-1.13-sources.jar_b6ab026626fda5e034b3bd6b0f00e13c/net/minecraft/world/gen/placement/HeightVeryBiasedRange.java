package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class HeightVeryBiasedRange extends BasePlacement<CountRangeConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, CountRangeConfig placementConfig, Feature<C> featureIn, C featureConfig) {
      for(int i = 0; i < placementConfig.count; ++i) {
         int j = random.nextInt(16);
         int k = random.nextInt(16);
         int l = random.nextInt(random.nextInt(random.nextInt(placementConfig.maxHeight - placementConfig.maxHeightBase) + placementConfig.minHeight) + placementConfig.minHeight);
         BlockPos blockpos = pos.add(j, l, k);
         featureIn.func_212245_a(worldIn, chunkGenerator, random, blockpos, featureConfig);
      }

      return true;
   }
}