package net.minecraft.world.gen;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkCacheNeighborNotification extends Long2ObjectOpenHashMap<Chunk> {
   private static final Logger LOGGER = LogManager.getLogger();

   public ChunkCacheNeighborNotification(int p_i48732_1_) {
      super(p_i48732_1_);
   }

   public Chunk put(long p_put_1_, Chunk p_put_3_) {
      Chunk chunk = super.put(p_put_1_, p_put_3_);
      ChunkPos chunkpos = new ChunkPos(p_put_1_);

      for(int i = chunkpos.x - 1; i <= chunkpos.x + 1; ++i) {
         for(int j = chunkpos.z - 1; j <= chunkpos.z + 1; ++j) {
            if (i != chunkpos.x || j != chunkpos.z) {
               long k = ChunkPos.asLong(i, j);
               Chunk chunk1 = this.get(k);
               if (chunk1 != null) {
                  p_put_3_.neighborAdded();
                  chunk1.neighborAdded();
               }
            }
         }
      }

      return chunk;
   }

   public Chunk put(Long p_put_1_, Chunk p_put_2_) {
      return this.put(p_put_1_.longValue(), p_put_2_);
   }

   public Chunk remove(long p_remove_1_) {
      Chunk chunk = (Chunk)super.remove(p_remove_1_);
      ChunkPos chunkpos = new ChunkPos(p_remove_1_);

      for(int i = chunkpos.x - 1; i <= chunkpos.x + 1; ++i) {
         for(int j = chunkpos.z - 1; j <= chunkpos.z + 1; ++j) {
            if (i != chunkpos.x || j != chunkpos.z) {
               Chunk chunk1 = this.get(ChunkPos.asLong(i, j));
               if (chunk1 != null) {
                  chunk1.neighborRemoved();
               }
            }
         }
      }

      return chunk;
   }

   public Chunk remove(Object p_remove_1_) {
      return this.remove(((Long) p_remove_1_).longValue());
   }

   public void putAll(Map<? extends Long, ? extends Chunk> p_putAll_1_) {
      throw new RuntimeException("Not yet implemented");
   }

   public boolean remove(Object p_remove_1_, Object p_remove_2_) {
      throw new RuntimeException("Not yet implemented");
   }
}