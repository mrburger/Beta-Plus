package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.ArrayUtils;

public class NBTTagIntArray extends NBTTagCollection<NBTTagInt> {
   /** The array of saved integers */
   private int[] intArray;

   NBTTagIntArray() {
   }

   public NBTTagIntArray(int[] p_i45132_1_) {
      this.intArray = p_i45132_1_;
   }

   public NBTTagIntArray(List<Integer> p_i47528_1_) {
      this(toArray(p_i47528_1_));
   }

   private static int[] toArray(List<Integer> p_193584_0_) {
      int[] aint = new int[p_193584_0_.size()];

      for(int i = 0; i < p_193584_0_.size(); ++i) {
         Integer integer = p_193584_0_.get(i);
         aint[i] = integer == null ? 0 : integer;
      }

      return aint;
   }

   /**
    * Write the actual data contents of the tag, implemented in NBT extension classes
    */
   public void write(DataOutput output) throws IOException {
      output.writeInt(this.intArray.length);

      for(int i : this.intArray) {
         output.writeInt(i);
      }

   }

   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(192L);
      int i = input.readInt();
      sizeTracker.read((long)(32 * i));
      this.intArray = new int[i];

      for(int j = 0; j < i; ++j) {
         this.intArray[j] = input.readInt();
      }

   }

   /**
    * Gets the type byte for the tag.
    */
   public byte getId() {
      return 11;
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder("[I;");

      for(int i = 0; i < this.intArray.length; ++i) {
         if (i != 0) {
            stringbuilder.append(',');
         }

         stringbuilder.append(this.intArray[i]);
      }

      return stringbuilder.append(']').toString();
   }

   /**
    * Creates a clone of the tag.
    */
   public NBTTagIntArray copy() {
      int[] aint = new int[this.intArray.length];
      System.arraycopy(this.intArray, 0, aint, 0, this.intArray.length);
      return new NBTTagIntArray(aint);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof NBTTagIntArray && Arrays.equals(this.intArray, ((NBTTagIntArray)p_equals_1_).intArray);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.intArray);
   }

   public int[] getIntArray() {
      return this.intArray;
   }

   public ITextComponent toFormattedComponent(String indentation, int indentDepth) {
      ITextComponent itextcomponent = (new TextComponentString("I")).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      ITextComponent itextcomponent1 = (new TextComponentString("[")).appendSibling(itextcomponent).appendText(";");

      for(int i = 0; i < this.intArray.length; ++i) {
         itextcomponent1.appendText(" ").appendSibling((new TextComponentString(String.valueOf(this.intArray[i]))).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER));
         if (i != this.intArray.length - 1) {
            itextcomponent1.appendText(",");
         }
      }

      itextcomponent1.appendText("]");
      return itextcomponent1;
   }

   public int size() {
      return this.intArray.length;
   }

   public NBTTagInt getTag(int p_197647_1_) {
      return new NBTTagInt(this.intArray[p_197647_1_]);
   }

   public void setTag(int p_197648_1_, INBTBase p_197648_2_) {
      this.intArray[p_197648_1_] = ((NBTPrimitive)p_197648_2_).getInt();
   }

   public void removeTag(int p_197649_1_) {
      this.intArray = ArrayUtils.remove(this.intArray, p_197649_1_);
   }
}