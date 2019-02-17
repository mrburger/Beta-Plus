package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGeneratorEnd;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class EndGateway extends BasePlacement<NoPlacementConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, NoPlacementConfig placementConfig, Feature<C> featureIn, C featureConfig) {
      boolean flag = false;
      if (random.nextInt(700) == 0) {
         int i = random.nextInt(16);
         int j = random.nextInt(16);
         int k = worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING, pos.add(i, 0, j)).getY();
         if (k > 0) {
            int l = k + 3 + random.nextInt(7);
            BlockPos blockpos = pos.add(i, l, j);
            featureIn.func_212245_a(worldIn, chunkGenerator, random, blockpos, featureConfig);
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof TileEntityEndGateway) {
               TileEntityEndGateway tileentityendgateway = (TileEntityEndGateway)tileentity;
               tileentityendgateway.setExitPortal(((ChunkGeneratorEnd)chunkGenerator).getSpawnPoint());
            }
         }
      }

      return false;
   }
}