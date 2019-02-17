package net.minecraft.world.gen.tasks;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.lighting.BlockLightEngine;
import net.minecraft.world.lighting.SkyLightEngine;

public class LightChunkTask extends ChunkTask {
   protected ChunkPrimer run(ChunkStatus chunkStatusIn, World worldIn, IChunkGenerator<?> chunkGenerator, ChunkPrimer[] region, int x, int z) {
      ChunkPrimer chunkprimer = region[region.length / 2];
      WorldGenRegion worldgenregion = new WorldGenRegion(region, chunkStatusIn.getTaskRange() * 2 + 1, chunkStatusIn.getTaskRange() * 2 + 1, x, z, worldIn);
      chunkprimer.createHeightMap(Heightmap.Type.LIGHT_BLOCKING);
      if (worldgenregion.getDimension().hasSkyLight()) {
         (new SkyLightEngine()).calculateLight(worldgenregion, chunkprimer);
      }

      (new BlockLightEngine()).calculateLight(worldgenregion, chunkprimer);
      chunkprimer.setStatus(ChunkStatus.LIGHTED);
      return chunkprimer;
   }
}