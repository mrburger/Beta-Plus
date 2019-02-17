package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class ChorusPlant extends BasePlacement<NoPlacementConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, NoPlacementConfig placementConfig, Feature<C> featureIn, C featureConfig) {
      boolean flag = false;
      int i = random.nextInt(5);

      for(int j = 0; j < i; ++j) {
         int k = random.nextInt(16);
         int l = random.nextInt(16);
         int i1 = worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING, pos.add(k, 0, l)).getY();
         if (i1 > 0) {
            int j1 = i1 - 1;
            flag |= featureIn.func_212245_a(worldIn, chunkGenerator, random, new BlockPos(pos.getX() + k, j1, pos.getZ() + l), featureConfig);
         }
      }

      return flag;
   }
}