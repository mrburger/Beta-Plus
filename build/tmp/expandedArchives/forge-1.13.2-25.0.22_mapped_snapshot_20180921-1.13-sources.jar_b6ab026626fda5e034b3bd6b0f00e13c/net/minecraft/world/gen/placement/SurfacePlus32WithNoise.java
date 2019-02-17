package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class SurfacePlus32WithNoise extends BasePlacement<NoiseDependant> {
   public <C extends IFeatureConfig> boolean generate(IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, NoiseDependant placementConfig, Feature<C> featureIn, C featureConfig) {
      double d0 = Biome.INFO_NOISE.getValue((double)pos.getX() / 200.0D, (double)pos.getZ() / 200.0D);
      int i = d0 < placementConfig.noiseThreshold ? placementConfig.lowNoiseCount : placementConfig.highNoiseCount;

      for(int j = 0; j < i; ++j) {
         int k = random.nextInt(16);
         int l = random.nextInt(16);
         int i1 = worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING, pos.add(k, 0, l)).getY() + 32;
         if (i1 > 0) {
            int j1 = random.nextInt(i1);
            featureIn.func_212245_a(worldIn, chunkGenerator, random, pos.add(k, j1, l), featureConfig);
         }
      }

      return true;
   }
}