package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class LakeWater extends BasePlacement<LakeChanceConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, LakeChanceConfig placementConfig, Feature<C> featureIn, C featureConfig) {
      if (random.nextInt(placementConfig.rarity) == 0) {
         int i = random.nextInt(16);
         int j = random.nextInt(chunkGenerator.getMaxHeight());
         int k = random.nextInt(16);
         featureIn.func_212245_a(worldIn, chunkGenerator, random, pos.add(i, j, k), featureConfig);
      }

      return true;
   }
}