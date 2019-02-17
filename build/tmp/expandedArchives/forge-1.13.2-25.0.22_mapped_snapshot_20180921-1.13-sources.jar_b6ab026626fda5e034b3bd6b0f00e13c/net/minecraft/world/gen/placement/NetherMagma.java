package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class NetherMagma extends BasePlacement<FrequencyConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, FrequencyConfig placementConfig, Feature<C> featureIn, C featureConfig) {
      int i = worldIn.getSeaLevel() / 2 + 1;

      for(int j = 0; j < placementConfig.frequency; ++j) {
         int k = random.nextInt(16);
         int l = i - 5 + random.nextInt(10);
         int i1 = random.nextInt(16);
         featureIn.func_212245_a(worldIn, chunkGenerator, random, pos.add(k, l, i1), featureConfig);
      }

      return true;
   }
}