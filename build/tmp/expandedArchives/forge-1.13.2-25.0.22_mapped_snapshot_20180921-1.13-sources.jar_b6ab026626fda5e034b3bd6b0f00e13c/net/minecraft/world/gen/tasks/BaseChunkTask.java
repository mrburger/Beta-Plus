package net.minecraft.world.gen.tasks;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.IChunkGenerator;

public class BaseChunkTask extends ChunkTask {
   protected ChunkPrimer run(ChunkStatus chunkStatusIn, World worldIn, IChunkGenerator<?> chunkGenerator, ChunkPrimer[] region, int x, int z) {
      ChunkPrimer chunkprimer = region[region.length / 2];
      chunkGenerator.makeBase(chunkprimer);
      return chunkprimer;
   }
}