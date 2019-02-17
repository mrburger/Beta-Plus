package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class AtHeight64 extends BasePlacement<FrequencyConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, FrequencyConfig placementConfig, Feature<C> featureIn, C featureConfig) {
      for(int i = 0; i < placementConfig.frequency; ++i) {
         int j = random.nextInt(16);
         int k = 64;
         int l = random.nextInt(16);
         featureIn.func_212245_a(worldIn, chunkGenerator, random, pos.add(j, 64, l), featureConfig);
      }

      return true;
   }
}