package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class RoofedTree extends BasePlacement<NoPlacementConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, NoPlacementConfig placementConfig, Feature<C> featureIn, C featureConfig) {
      for(int i = 0; i < 4; ++i) {
         for(int j = 0; j < 4; ++j) {
            int k = i * 4 + 1 + random.nextInt(3);
            int l = j * 4 + 1 + random.nextInt(3);
            featureIn.func_212245_a(worldIn, chunkGenerator, random, worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING, pos.add(k, 0, l)), featureConfig);
         }
      }

      return true;
   }
}