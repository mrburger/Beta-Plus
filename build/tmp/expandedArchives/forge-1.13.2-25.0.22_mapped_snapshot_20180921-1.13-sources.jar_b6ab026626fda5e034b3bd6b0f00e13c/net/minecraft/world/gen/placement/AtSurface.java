package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class AtSurface extends BasePlacement<FrequencyConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, FrequencyConfig placementConfig, Feature<C> featureIn, C featureConfig) {
      for(int i = 0; i < placementConfig.frequency; ++i) {
         int j = random.nextInt(16);
         int k = random.nextInt(16);
         featureIn.func_212245_a(worldIn, chunkGenerator, random, worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING, pos.add(j, 0, k)), featureConfig);
      }

      return true;
   }
}