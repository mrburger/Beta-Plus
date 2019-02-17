package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class TopSolidRange extends BasePlacement<TopSolidRangeConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, TopSolidRangeConfig placementConfig, Feature<C> featureIn, C featureConfig) {
      int i = random.nextInt(placementConfig.maxCount - placementConfig.minCount) + placementConfig.minCount;

      for(int j = 0; j < i; ++j) {
         int k = random.nextInt(16);
         int l = random.nextInt(16);
         int i1 = worldIn.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, pos.getX() + k, pos.getZ() + l);
         featureIn.func_212245_a(worldIn, chunkGenerator, random, new BlockPos(pos.getX() + k, i1, pos.getZ() + l), featureConfig);
      }

      return false;
   }
}