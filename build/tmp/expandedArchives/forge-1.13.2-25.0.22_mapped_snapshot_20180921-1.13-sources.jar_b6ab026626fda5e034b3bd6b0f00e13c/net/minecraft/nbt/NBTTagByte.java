package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class NBTTagByte extends NBTPrimitive {
   /** The byte value for the tag. */
   private byte data;

   NBTTagByte() {
   }

   public NBTTagByte(byte data) {
      this.data = data;
   }

   /**
    * Write the actual data contents of the tag, implemented in NBT extension classes
    */
   public void write(DataOutput output) throws IOException {
      output.writeByte(this.data);
   }

   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(72L);
      this.data = input.readByte();
   }

   /**
    * Gets the type byte for the tag.
    */
   public byte getId() {
      return 1;
   }

   public String toString() {
      return this.data + "b";
   }

   /**
    * Creates a clone of the tag.
    */
   public NBTTagByte copy() {
      return new NBTTagByte(this.data);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof NBTTagByte && this.data == ((NBTTagByte)p_equals_1_).data;
      }
   }

   public int hashCode() {
      return this.data;
   }

   public ITextComponent toFormattedComponent(String indentation, int indentDepth) {
      ITextComponent itextcomponent = (new TextComponentString("b")).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      return (new TextComponentString(String.valueOf((int)this.data))).appendSibling(itextcomponent).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public long getLong() {
      return (long)this.data;
   }

   public int getInt() {
      return this.data;
   }

   public short getShort() {
      return (short)this.data;
   }

   public byte getByte() {
      return this.data;
   }

   public double getDouble() {
      return (double)this.data;
   }

   public float getFloat() {
      return (float)this.data;
   }

   public Number getAsNumber() {
      return this.data;
   }
}