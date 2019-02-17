package net.minecraft.world.biome;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.provider.BiomeProvider;

public class BiomeCache {
   /** Reference to the WorldChunkManager */
   private final BiomeProvider provider;
   /** The map of keys to BiomeCacheBlocks. Keys are based on the chunk x, z coordinates as (x | z << 32). */
   private final LoadingCache<ChunkPos, BiomeCache.Entry> cacheMap = CacheBuilder.newBuilder().expireAfterAccess(30000L, TimeUnit.MILLISECONDS).build(new CacheLoader<ChunkPos, BiomeCache.Entry>() {
      public BiomeCache.Entry load(ChunkPos p_load_1_) throws Exception {
         return BiomeCache.this.new Entry(p_load_1_.x, p_load_1_.z);
      }
   });

   public BiomeCache(BiomeProvider provider) {
      this.provider = provider;
   }

   /**
    * Returns a biome cache block at location specified.
    */
   public BiomeCache.Entry getEntry(int x, int z) {
      x = x >> 4;
      z = z >> 4;
      return this.cacheMap.getUnchecked(new ChunkPos(x, z));
   }

   public Biome getBiome(int x, int z, Biome defaultValue) {
      Biome biome = this.getEntry(x, z).getBiome(x, z);
      return biome == null ? defaultValue : biome;
   }

   /**
    * Removes BiomeCacheBlocks from this cache that haven't been accessed in at least 30 seconds.
    */
   public void cleanupCache() {
   }

   /**
    * Returns the array of cached biome types in the BiomeCacheBlock at the given location.
    */
   public Biome[] getCachedBiomes(int x, int z) {
      return this.getEntry(x, z).biomes;
   }

   public class Entry {
      /** Flattened 16 * 16 array of the biomes in this chunk */
      private final Biome[] biomes;

      public Entry(int x, int z) {
         this.biomes = BiomeCache.this.provider.getBiomes(x << 4, z << 4, 16, 16, false);
      }

      /**
       * Returns the BiomeGenBase related to the x, z position from the cache block.
       */
      public Biome getBiome(int x, int z) {
         return this.biomes[x & 15 | (z & 15) << 4];
      }
   }
}