package net.minecraft.client.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SuffixArray<T> {
   /**
    * A debug property (<code>SuffixArray.printComparisons</code>) that can be specified in the JVM arguments, that
    * causes debug printing of comparisons as they happen.
    */
   private static final boolean DEBUG_PRINT_COMPARISONS = Boolean.parseBoolean(System.getProperty("SuffixArray.printComparisons", "false"));
   /**
    * A debug property (<code>SuffixArray.printArray</code>) that can be specified in the JVM arguments, that causes the
    * full array to be printed ({@link #printArray()}) after calling {@link #generate()}
    */
   private static final boolean DEBUG_PRINT_ARRAY = Boolean.parseBoolean(System.getProperty("SuffixArray.printArray", "false"));
   private static final Logger LOGGER = LogManager.getLogger();
   protected final List<T> list = Lists.newArrayList();
   private final IntList chars = new IntArrayList();
   private final IntList wordStarts = new IntArrayList();
   private IntList suffixToT = new IntArrayList();
   private IntList offsets = new IntArrayList();
   private int maxStringLength;

   public void add(T p_194057_1_, String p_194057_2_) {
      this.maxStringLength = Math.max(this.maxStringLength, p_194057_2_.length());
      int i = this.list.size();
      this.list.add(p_194057_1_);
      this.wordStarts.add(this.chars.size());

      for(int j = 0; j < p_194057_2_.length(); ++j) {
         this.suffixToT.add(i);
         this.offsets.add(j);
         this.chars.add(p_194057_2_.charAt(j));
      }

      this.suffixToT.add(i);
      this.offsets.add(p_194057_2_.length());
      this.chars.add(-1);
   }

   public void generate() {
      int i = this.chars.size();
      int[] aint = new int[i];
      final int[] aint1 = new int[i];
      final int[] aint2 = new int[i];
      int[] aint3 = new int[i];
      IntComparator intcomparator = new IntComparator() {
         public int compare(int p_compare_1_, int p_compare_2_) {
            return aint1[p_compare_1_] == aint1[p_compare_2_] ? Integer.compare(aint2[p_compare_1_], aint2[p_compare_2_]) : Integer.compare(aint1[p_compare_1_], aint1[p_compare_2_]);
         }

         public int compare(Integer p_compare_1_, Integer p_compare_2_) {
            return this.compare(p_compare_1_.intValue(), p_compare_2_.intValue());
         }
      };
      Swapper swapper = (p_194054_3_, p_194054_4_) -> {
         if (p_194054_3_ != p_194054_4_) {
            int i2 = aint1[p_194054_3_];
            aint1[p_194054_3_] = aint1[p_194054_4_];
            aint1[p_194054_4_] = i2;
            i2 = aint2[p_194054_3_];
            aint2[p_194054_3_] = aint2[p_194054_4_];
            aint2[p_194054_4_] = i2;
            i2 = aint3[p_194054_3_];
            aint3[p_194054_3_] = aint3[p_194054_4_];
            aint3[p_194054_4_] = i2;
         }

      };

      for(int j = 0; j < i; ++j) {
         aint[j] = this.chars.getInt(j);
      }

      int k1 = 1;

      for(int k = Math.min(i, this.maxStringLength); k1 * 2 < k; k1 *= 2) {
         for(int l = 0; l < i; aint3[l] = l++) {
            aint1[l] = aint[l];
            aint2[l] = l + k1 < i ? aint[l + k1] : -2;
         }

         Arrays.quickSort(0, i, intcomparator, swapper);

         for(int l1 = 0; l1 < i; ++l1) {
            if (l1 > 0 && aint1[l1] == aint1[l1 - 1] && aint2[l1] == aint2[l1 - 1]) {
               aint[aint3[l1]] = aint[aint3[l1 - 1]];
            } else {
               aint[aint3[l1]] = l1;
            }
         }
      }

      IntList intlist1 = this.suffixToT;
      IntList intlist = this.offsets;
      this.suffixToT = new IntArrayList(intlist1.size());
      this.offsets = new IntArrayList(intlist.size());

      for(int i1 = 0; i1 < i; ++i1) {
         int j1 = aint3[i1];
         this.suffixToT.add(intlist1.getInt(j1));
         this.offsets.add(intlist.getInt(j1));
      }

      if (DEBUG_PRINT_ARRAY) {
         this.printArray();
      }

   }

   /**
    * Prints the entire array to the logger, on debug level
    */
   private void printArray() {
      for(int i = 0; i < this.suffixToT.size(); ++i) {
         LOGGER.debug("{} {}", i, this.getString(i));
      }

      LOGGER.debug("");
   }

   private String getString(int p_194059_1_) {
      int i = this.offsets.getInt(p_194059_1_);
      int j = this.wordStarts.getInt(this.suffixToT.getInt(p_194059_1_));
      StringBuilder stringbuilder = new StringBuilder();

      for(int k = 0; j + k < this.chars.size(); ++k) {
         if (k == i) {
            stringbuilder.append('^');
         }

         int l = this.chars.get(j + k);
         if (l == -1) {
            break;
         }

         stringbuilder.append((char)l);
      }

      return stringbuilder.toString();
   }

   private int compare(String p_194056_1_, int p_194056_2_) {
      int i = this.wordStarts.getInt(this.suffixToT.getInt(p_194056_2_));
      int j = this.offsets.getInt(p_194056_2_);

      for(int k = 0; k < p_194056_1_.length(); ++k) {
         int l = this.chars.getInt(i + j + k);
         if (l == -1) {
            return 1;
         }

         char c0 = p_194056_1_.charAt(k);
         char c1 = (char)l;
         if (c0 < c1) {
            return -1;
         }

         if (c0 > c1) {
            return 1;
         }
      }

      return 0;
   }

   public List<T> search(String p_194055_1_) {
      int i = this.suffixToT.size();
      int j = 0;
      int k = i;

      while(j < k) {
         int l = j + (k - j) / 2;
         int i1 = this.compare(p_194055_1_, l);
         if (DEBUG_PRINT_COMPARISONS) {
            LOGGER.debug("comparing lower \"{}\" with {} \"{}\": {}", p_194055_1_, l, this.getString(l), i1);
         }

         if (i1 > 0) {
            j = l + 1;
         } else {
            k = l;
         }
      }

      if (j >= 0 && j < i) {
         int i2 = j;
         k = i;

         while(j < k) {
            int j2 = j + (k - j) / 2;
            int j1 = this.compare(p_194055_1_, j2);
            if (DEBUG_PRINT_COMPARISONS) {
               LOGGER.debug("comparing upper \"{}\" with {} \"{}\": {}", p_194055_1_, j2, this.getString(j2), j1);
            }

            if (j1 >= 0) {
               j = j2 + 1;
            } else {
               k = j2;
            }
         }

         int k2 = j;
         IntSet intset = new IntOpenHashSet();

         for(int k1 = i2; k1 < k2; ++k1) {
            intset.add(this.suffixToT.getInt(k1));
         }

         int[] aint = intset.toIntArray();
         java.util.Arrays.sort(aint);
         Set<T> set = Sets.newLinkedHashSet();

         for(int l1 : aint) {
            set.add(this.list.get(l1));
         }

         return Lists.newArrayList(set);
      } else {
         return Collections.emptyList();
      }
   }
}