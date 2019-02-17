package net.minecraft.world.gen.area;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.world.gen.layer.traits.IPixelTransformer;

public final class LazyArea implements IArea {
   private final IPixelTransformer pixelTransformer;
   private final Long2IntLinkedOpenHashMap cachedValues;
   private final int maxCacheSize;
   private final AreaDimension dimension;

   public LazyArea(Long2IntLinkedOpenHashMap cachedValuesIn, int maxCacheSizeIn, AreaDimension dimensionIn, IPixelTransformer pixelTransformerIn) {
      this.cachedValues = cachedValuesIn;
      this.maxCacheSize = maxCacheSizeIn;
      this.dimension = dimensionIn;
      this.pixelTransformer = pixelTransformerIn;
   }

   public int getValue(int x, int z) {
      long i = this.getCacheKey(x, z);
      synchronized(this.cachedValues) {
         int j = this.cachedValues.get(i);
         if (j != Integer.MIN_VALUE) {
            return j;
         } else {
            int k = this.pixelTransformer.apply(x, z);
            this.cachedValues.put(i, k);
            if (this.cachedValues.size() > this.maxCacheSize) {
               for(int l = 0; l < this.maxCacheSize / 16; ++l) {
                  this.cachedValues.removeFirstInt();
               }
            }

            return k;
         }
      }
   }

   private long getCacheKey(int x, int z) {
      long i = 1L;
      i = i << 26;
      i = i | (long)(x + this.dimension.getStartX()) & 67108863L;
      i = i << 26;
      i = i | (long)(z + this.dimension.getStartZ()) & 67108863L;
      return i;
   }

   public int getmaxCacheSize() {
      return this.maxCacheSize;
   }
}