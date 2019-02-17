package net.minecraft.world.gen.placement;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class CaveEdge extends BasePlacement<CaveEdgeConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, CaveEdgeConfig placementConfig, Feature<C> featureIn, C featureConfig) {
      IChunk ichunk = worldIn.getChunkDefault(pos);
      ChunkPos chunkpos = ichunk.getPos();
      BitSet bitset = ichunk.getCarvingMask(placementConfig.carvingStage);

      for(int i = 0; i < bitset.length(); ++i) {
         if (bitset.get(i) && random.nextFloat() < placementConfig.chance) {
            int j = i & 15;
            int k = i >> 4 & 15;
            int l = i >> 8;
            featureIn.func_212245_a(worldIn, chunkGenerator, random, new BlockPos(chunkpos.getXStart() + j, l, chunkpos.getZStart() + k), featureConfig);
         }
      }

      return true;
   }
}