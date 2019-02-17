package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;

interface IDoubleListMerger {
   DoubleList func_212435_a();

   boolean forMergedIndexes(IDoubleListMerger.Consumer consumer);

   public interface Consumer {
      boolean merge(int p_merge_1_, int p_merge_2_, int p_merge_3_);
   }
}