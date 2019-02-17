package net.minecraft.util;

import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;

public class BitArray {
   /** The long array that is used to store the data for this BitArray. */
   private final long[] longArray;
   /** Number of bits a single entry takes up */
   private final int bitsPerEntry;
   /**
    * The maximum value for a single entry. This also asks as a bitmask for a single entry.
    * For instance, if bitsPerEntry were 5, this value would be 31 (ie, {@code 0b00011111}).
    */
   private final long maxEntryValue;
   /** Number of entries in this array (<b>not</b> the length of the long array that internally backs this array) */
   private final int arraySize;

   public BitArray(int bitsPerEntryIn, int arraySizeIn) {
      this(bitsPerEntryIn, arraySizeIn, new long[MathHelper.roundUp(arraySizeIn * bitsPerEntryIn, 64) / 64]);
   }

   public BitArray(int p_i47901_1_, int p_i47901_2_, long[] p_i47901_3_) {
      Validate.inclusiveBetween(1L, 32L, (long)p_i47901_1_);
      this.arraySize = p_i47901_2_;
      this.bitsPerEntry = p_i47901_1_;
      this.longArray = p_i47901_3_;
      this.maxEntryValue = (1L << p_i47901_1_) - 1L;
      int i = MathHelper.roundUp(p_i47901_2_ * p_i47901_1_, 64) / 64;
      if (p_i47901_3_.length != i) {
         throw new RuntimeException("Invalid length given for storage, got: " + p_i47901_3_.length + " but expected: " + i);
      }
   }

   /**
    * Sets the entry at the given location to the given value
    */
   public void setAt(int index, int value) {
      Validate.inclusiveBetween(0L, (long)(this.arraySize - 1), (long)index);
      Validate.inclusiveBetween(0L, this.maxEntryValue, (long)value);
      int i = index * this.bitsPerEntry;
      int j = i / 64;
      int k = ((index + 1) * this.bitsPerEntry - 1) / 64;
      int l = i % 64;
      this.longArray[j] = this.longArray[j] & ~(this.maxEntryValue << l) | ((long)value & this.maxEntryValue) << l;
      if (j != k) {
         int i1 = 64 - l;
         int j1 = this.bitsPerEntry - i1;
         this.longArray[k] = this.longArray[k] >>> j1 << j1 | ((long)value & this.maxEntryValue) >> i1;
      }

   }

   /**
    * Gets the entry at the given index
    */
   public int getAt(int index) {
      Validate.inclusiveBetween(0L, (long)(this.arraySize - 1), (long)index);
      int i = index * this.bitsPerEntry;
      int j = i / 64;
      int k = ((index + 1) * this.bitsPerEntry - 1) / 64;
      int l = i % 64;
      if (j == k) {
         return (int)(this.longArray[j] >>> l & this.maxEntryValue);
      } else {
         int i1 = 64 - l;
         return (int)((this.longArray[j] >>> l | this.longArray[k] << i1) & this.maxEntryValue);
      }
   }

   /**
    * Gets the long array that is used to store the data in this BitArray. This is useful for sending packet data.
    */
   public long[] getBackingLongArray() {
      return this.longArray;
   }

   public int size() {
      return this.arraySize;
   }

   public int bitsPerEntry() {
      return this.bitsPerEntry;
   }
}