package net.minecraft.world.gen;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.traits.IPixelTransformer;

public class LazyAreaLayerContext extends LayerContext<LazyArea> {
   private final Long2IntLinkedOpenHashMap cache = new Long2IntLinkedOpenHashMap(16, 0.25F);
   private final int maxCacheSize;
   private final int field_202705_d;

   public LazyAreaLayerContext(int maxCacheSizeIn, int p_i48647_2_, long seed, long p_i48647_5_) {
      super(p_i48647_5_);
      this.cache.defaultReturnValue(Integer.MIN_VALUE);
      this.maxCacheSize = maxCacheSizeIn;
      this.field_202705_d = p_i48647_2_;
      this.setSeed(seed);
   }

   public LazyArea makeArea(AreaDimension dimensionIn, IPixelTransformer transformer) {
      return new LazyArea(this.cache, this.maxCacheSize, dimensionIn, transformer);
   }

   public LazyArea makeArea(AreaDimension dimensionIn, IPixelTransformer transformer, LazyArea p_201489_3_) {
      return new LazyArea(this.cache, Math.min(256, p_201489_3_.getmaxCacheSize() * 4), dimensionIn, transformer);
   }

   public LazyArea makeArea(AreaDimension dimensionIn, IPixelTransformer transformer, LazyArea p_201488_3_, LazyArea p_201488_4_) {
      return new LazyArea(this.cache, Math.min(256, Math.max(p_201488_3_.getmaxCacheSize(), p_201488_4_.getmaxCacheSize()) * 4), dimensionIn, transformer);
   }
}