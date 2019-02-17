package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class AtSurfaceWithExtra extends BasePlacement<AtSurfaceWithExtraConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, AtSurfaceWithExtraConfig placementConfig, Feature<C> featureIn, C featureConfig) {
      int i = placementConfig.baseCount;
      if (random.nextFloat() < placementConfig.extraChance) {
         i += placementConfig.extraCount;
      }

      for(int j = 0; j < i; ++j) {
         int k = random.nextInt(16);
         int l = random.nextInt(16);
         featureIn.func_212245_a(worldIn, chunkGenerator, random, worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING, pos.add(k, 0, l)), featureConfig);
      }

      return true;
   }
}