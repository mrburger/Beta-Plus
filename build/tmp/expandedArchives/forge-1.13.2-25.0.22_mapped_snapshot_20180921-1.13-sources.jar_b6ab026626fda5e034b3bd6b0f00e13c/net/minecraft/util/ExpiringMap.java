package net.minecraft.util;

import it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map;

public class ExpiringMap<T> extends Long2ObjectOpenHashMap<T> {
   /** Lifespan for objects, in milliseconds */
   private final int lifespan;
   /** The last time each object it this map has been used. Key is the key to this map; value is the timestamp. */
   private final Long2LongMap times = new Long2LongLinkedOpenHashMap();

   public ExpiringMap(int expected, int lifespanIn) {
      super(expected);
      this.lifespan = lifespanIn;
   }

   /**
    * Updates the time information for the given value, and purges entries that are too old.
    */
   private void refreshTimes(long key) {
      long i = Util.milliTime();
      this.times.put(key, i);
      ObjectIterator<Long2LongMap.Entry> objectiterator = this.times.long2LongEntrySet().iterator();

      while(objectiterator.hasNext()) {
         Long2LongMap.Entry entry = objectiterator.next();
         T t = (T)super.get(entry.getLongKey());
         if (i - entry.getLongValue() <= (long)this.lifespan) {
            break;
         }

         if (t != null && this.shouldExpire(t)) {
            super.remove(entry.getLongKey());
            objectiterator.remove();
         }
      }

   }

   /**
    * Returns true if the given item is allowed to expire within {@link #refreshTimes}.
    */
   protected boolean shouldExpire(T element) {
      return true;
   }

   public T put(long p_put_1_, T p_put_3_) {
      this.refreshTimes(p_put_1_);
      return super.put(p_put_1_, p_put_3_);
   }

   public T put(Long p_put_1_, T p_put_2_) {
      this.refreshTimes(p_put_1_);
      return super.put(p_put_1_, p_put_2_);
   }

   public T get(long p_get_1_) {
      this.refreshTimes(p_get_1_);
      return (T)super.get(p_get_1_);
   }

   public void putAll(Map<? extends Long, ? extends T> p_putAll_1_) {
      throw new RuntimeException("Not implemented");
   }

   public T remove(long p_remove_1_) {
      throw new RuntimeException("Not implemented");
   }

   public T remove(Object p_remove_1_) {
      throw new RuntimeException("Not implemented");
   }
}