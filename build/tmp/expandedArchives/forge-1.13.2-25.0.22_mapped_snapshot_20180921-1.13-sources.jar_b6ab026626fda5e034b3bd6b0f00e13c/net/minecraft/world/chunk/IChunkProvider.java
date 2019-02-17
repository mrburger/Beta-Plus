package net.minecraft.world.chunk;

import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.world.gen.IChunkGenerator;

public interface IChunkProvider extends AutoCloseable {
   @Nullable
   Chunk provideChunk(int x, int z, boolean p_186025_3_, boolean p_186025_4_);

   @Nullable
   default IChunk provideChunkOrPrimer(int x, int z, boolean p_201713_3_) {
      Chunk chunk = this.provideChunk(x, z, true, false);
      if (chunk == null && p_201713_3_) {
         throw new UnsupportedOperationException("Could not create an empty chunk");
      } else {
         return chunk;
      }
   }

   /**
    * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
    *  
    * The return value is ignored, and is always false.
    */
   boolean tick(BooleanSupplier p_73156_1_);

   /**
    * Converts the instance data to a readable string.
    */
   String makeString();

   IChunkGenerator<?> getChunkGenerator();

   default void close() {
   }
}