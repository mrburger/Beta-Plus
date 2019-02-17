package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class EndIsland extends BasePlacement<NoPlacementConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, NoPlacementConfig placementConfig, Feature<C> featureIn, C featureConfig) {
      boolean flag = false;
      if (random.nextInt(14) == 0) {
         flag |= featureIn.func_212245_a(worldIn, chunkGenerator, random, pos.add(random.nextInt(16), 55 + random.nextInt(16), random.nextInt(16)), featureConfig);
         if (random.nextInt(4) == 0) {
            flag |= featureIn.func_212245_a(worldIn, chunkGenerator, random, pos.add(random.nextInt(16), 55 + random.nextInt(16), random.nextInt(16)), featureConfig);
         }
      }

      return flag;
   }
}