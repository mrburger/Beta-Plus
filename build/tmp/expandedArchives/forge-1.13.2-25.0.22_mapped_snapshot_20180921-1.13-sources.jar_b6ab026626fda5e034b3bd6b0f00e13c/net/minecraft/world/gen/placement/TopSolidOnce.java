package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class TopSolidOnce extends BasePlacement<NoPlacementConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, NoPlacementConfig placementConfig, Feature<C> featureIn, C featureConfig) {
      int i = random.nextInt(16);
      int j = random.nextInt(16);
      int k = worldIn.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, pos.getX() + i, pos.getZ() + j);
      featureIn.func_212245_a(worldIn, chunkGenerator, random, new BlockPos(pos.getX() + i, k, pos.getZ() + j), featureConfig);
      return false;
   }
}