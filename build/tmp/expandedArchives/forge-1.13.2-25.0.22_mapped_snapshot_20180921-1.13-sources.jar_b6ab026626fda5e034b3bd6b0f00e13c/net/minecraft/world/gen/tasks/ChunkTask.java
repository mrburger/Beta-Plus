package net.minecraft.world.gen.tasks;

import java.util.Map;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.IChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ChunkTask {
   private static final Logger LOGGER = LogManager.getLogger();

   protected ChunkPrimer[] makeChunkPrimerArray(ChunkStatus chunkStatusIn, int x, int z, Map<ChunkPos, ChunkPrimer> chunkPrimerMap) {
      int i = chunkStatusIn.getTaskRange();
      ChunkPrimer[] achunkprimer = new ChunkPrimer[(1 + 2 * i) * (1 + 2 * i)];
      int j = 0;

      for(int k = -i; k <= i; ++k) {
         for(int l = -i; l <= i; ++l) {
            ChunkPrimer chunkprimer = chunkPrimerMap.get(new ChunkPos(x + l, z + k));
            chunkprimer.setUpdateHeightmaps(chunkStatusIn.shouldUpdateHeightmaps());
            achunkprimer[j++] = chunkprimer;
         }
      }

      return achunkprimer;
   }

   public ChunkPrimer run(ChunkStatus chunkStatusIn, World worldIn, IChunkGenerator<?> chunkGenerator, Map<ChunkPos, ChunkPrimer> region, int x, int z) {
      ChunkPrimer[] achunkprimer = this.makeChunkPrimerArray(chunkStatusIn, x, z, region);
      return this.run(chunkStatusIn, worldIn, chunkGenerator, achunkprimer, x, z);
   }

   protected abstract ChunkPrimer run(ChunkStatus chunkStatusIn, World worldIn, IChunkGenerator<?> chunkGenerator, ChunkPrimer[] region, int x, int z);
}