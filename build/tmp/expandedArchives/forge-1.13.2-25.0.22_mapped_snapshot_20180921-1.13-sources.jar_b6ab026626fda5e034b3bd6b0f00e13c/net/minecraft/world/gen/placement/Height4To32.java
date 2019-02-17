package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class Height4To32 extends BasePlacement<NoPlacementConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, NoPlacementConfig placementConfig, Feature<C> featureIn, C featureConfig) {
      int i = 3 + random.nextInt(6);

      for(int j = 0; j < i; ++j) {
         int k = random.nextInt(16);
         int l = random.nextInt(28) + 4;
         int i1 = random.nextInt(16);
         featureIn.func_212245_a(worldIn, chunkGenerator, random, pos.add(k, l, i1), featureConfig);
      }

      return true;
   }
}