package net.minecraft.world.gen.tasks;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.WorldGenRegion;

public class SpawnMobsTask extends ChunkTask {
   protected ChunkPrimer run(ChunkStatus chunkStatusIn, World worldIn, IChunkGenerator<?> chunkGenerator, ChunkPrimer[] region, int x, int z) {
      WorldGenRegion worldgenregion = new WorldGenRegion(region, chunkStatusIn.getTaskRange() * 2 + 1, chunkStatusIn.getTaskRange() * 2 + 1, x, z, worldIn);
      ChunkPrimer chunkprimer = region[region.length / 2];
      chunkGenerator.spawnMobs(worldgenregion);
      chunkprimer.setStatus(ChunkStatus.MOBS_SPAWNED);
      return chunkprimer;
   }
}