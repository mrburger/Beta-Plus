package net.minecraft.client.util;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SearchTree<T> implements ISearchTree<T> {
   /** A {@link SuffixArray} that contains values keyed by the name (as produced by {@link #nameFunc}). */
   protected SuffixArray<T> byName = new SuffixArray<>();
   protected SuffixArray<T> byDomain = new SuffixArray<>();
   protected SuffixArray<T> byPath = new SuffixArray<>();
   /**
    * A function that takes a <code>T</code> and returns a list of Strings describing it normally.
    * <p>
    * For both items and recipes, this is implemented via {@link net.minecraft.item.ItemStack#getTooltip()
    * ItemStack.getTooltip} (with NORMAL tooltip flags), with formatting codes stripped, text trimmed, and empty lines
    * removed.
    *  
    * The result does not need to be corrected for
    */
   private final Function<T, Iterable<String>> nameFunc;
   /**
    * A function that takes a <code>T</code> and returns a list of {@link ResourceLocation}s describing it.
    * <p>
    * For both items and recipes, this is implemented via <code>Item.REGISTRY.getNameForObject</code>. (In the case of
    * registries, it is applied to all results)
    */
   private final Function<T, Iterable<ResourceLocation>> idFunc;
   /** All entries in the search tree. */
   private final List<T> contents = Lists.newArrayList();
   /** Maps each entry in the search tree to a locally unique, increasing number (staring at 0). */
   private final Object2IntMap<T> numericContents = new Object2IntOpenHashMap<>();

   public SearchTree(Function<T, Iterable<String>> nameFuncIn, Function<T, Iterable<ResourceLocation>> idFuncIn) {
      this.nameFunc = nameFuncIn;
      this.idFunc = idFuncIn;
   }

   /**
    * Recalculates the contents of this search tree, reapplying {@link #nameFunc} and {@link #idFunc}. Should be called
    * whenever resources are reloaded (e.g. language changes).
    */
   public void recalculate() {
      this.byName = new SuffixArray<>();
      this.byDomain = new SuffixArray<>();
      this.byPath = new SuffixArray<>();

      for(T t : this.contents) {
         this.index(t);
      }

      this.byName.generate();
      this.byDomain.generate();
      this.byPath.generate();
   }

   /**
    * Adds the given item to the search tree.
    */
   public void add(T element) {
      this.numericContents.put(element, this.contents.size());
      this.contents.add(element);
      this.index(element);
   }

   public void clear() {
      this.contents.clear();
      this.numericContents.clear();
   }

   /**
    * Directly puts the given item into {@link #byId} and {@link #byName}, applying {@link #nameFunc} and {@link
    * idFunc}.
    */
   private void index(T element) {
      this.idFunc.apply(element).forEach((p_194039_2_) -> {
         this.byDomain.add(element, p_194039_2_.getNamespace().toLowerCase(Locale.ROOT));
         this.byPath.add(element, p_194039_2_.getPath().toLowerCase(Locale.ROOT));
      });
      this.nameFunc.apply(element).forEach((p_194041_2_) -> {
         this.byName.add(element, p_194041_2_.toLowerCase(Locale.ROOT));
      });
   }

   /**
    * Searches this search tree for the given text.
    * <p>
    * If the query does not contain a <code>:</code>, then only {@link #byName} is searched; if it does contain a colon,
    * both {@link #byName} and {@link #byId} are searched and the results are merged using a {@link MergingIterator}.
    * @return A list of all matching items in this search tree.
    */
   public List<T> search(String searchText) {
      int i = searchText.indexOf(58);
      if (i < 0) {
         return this.byName.search(searchText);
      } else {
         List<T> list = this.byDomain.search(searchText.substring(0, i).trim());
         String s = searchText.substring(i + 1, searchText.length()).trim();
         List<T> list1 = this.byPath.search(s);
         List<T> list2 = this.byName.search(s);
         return Lists.newArrayList(new SearchTree.IntersectingIterator<>(list.iterator(), new SearchTree.MergingIterator<>(list1.iterator(), list2.iterator(), this.numericContents), this.numericContents));
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class IntersectingIterator<T> extends AbstractIterator<T> {
      private final PeekingIterator<T> field_195831_a;
      private final PeekingIterator<T> field_195832_b;
      private final Object2IntMap<T> field_195833_c;

      public IntersectingIterator(Iterator<T> p_i47715_1_, Iterator<T> p_i47715_2_, Object2IntMap<T> p_i47715_3_) {
         this.field_195831_a = Iterators.peekingIterator(p_i47715_1_);
         this.field_195832_b = Iterators.peekingIterator(p_i47715_2_);
         this.field_195833_c = p_i47715_3_;
      }

      protected T computeNext() {
         while(this.field_195831_a.hasNext() && this.field_195832_b.hasNext()) {
            int i = Integer.compare(this.field_195833_c.getInt(this.field_195831_a.peek()), this.field_195833_c.getInt(this.field_195832_b.peek()));
            if (i == 0) {
               this.field_195832_b.next();
               return this.field_195831_a.next();
            }

            if (i < 0) {
               this.field_195831_a.next();
            } else {
               this.field_195832_b.next();
            }
         }

         return (T)this.endOfData();
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class MergingIterator<T> extends AbstractIterator<T> {
      private final PeekingIterator<T> leftItr;
      private final PeekingIterator<T> rightItr;
      /**
       * A mapping of objects to unique numeric IDs, used to sort the list.
       * <p>
       * Since there's no good place to document how this class works, it basically just interleaves two iterators
       * together, choosing the entry that has the lower numeric ID in this map.
       */
      private final Object2IntMap<T> numbers;

      public MergingIterator(Iterator<T> leftIn, Iterator<T> rightIn, Object2IntMap<T> numbersIn) {
         this.leftItr = Iterators.peekingIterator(leftIn);
         this.rightItr = Iterators.peekingIterator(rightIn);
         this.numbers = numbersIn;
      }

      protected T computeNext() {
         boolean flag = !this.leftItr.hasNext();
         boolean flag1 = !this.rightItr.hasNext();
         if (flag && flag1) {
            return (T)this.endOfData();
         } else if (flag) {
            return this.rightItr.next();
         } else if (flag1) {
            return this.leftItr.next();
         } else {
            int i = Integer.compare(this.numbers.getInt(this.leftItr.peek()), this.numbers.getInt(this.rightItr.peek()));
            if (i == 0) {
               this.rightItr.next();
            }

            return (T)(i <= 0 ? this.leftItr.next() : this.rightItr.next());
         }
      }
   }
}