package net.minecraft.world.gen.tasks;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenerator;

public class FinializeChunkTask extends ChunkTask {
   protected ChunkPrimer run(ChunkStatus chunkStatusIn, World worldIn, IChunkGenerator<?> chunkGenerator, ChunkPrimer[] region, int x, int z) {
      ChunkPrimer chunkprimer = region[region.length / 2];
      chunkprimer.setStatus(ChunkStatus.FINALIZED);
      chunkprimer.createHeightMap(Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Heightmap.Type.LIGHT_BLOCKING, Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE);
      return chunkprimer;
   }
}