package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class NBTTagFloat extends NBTPrimitive {
   /** The float value for the tag. */
   private float data;

   NBTTagFloat() {
   }

   public NBTTagFloat(float data) {
      this.data = data;
   }

   /**
    * Write the actual data contents of the tag, implemented in NBT extension classes
    */
   public void write(DataOutput output) throws IOException {
      output.writeFloat(this.data);
   }

   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(96L);
      this.data = input.readFloat();
   }

   /**
    * Gets the type byte for the tag.
    */
   public byte getId() {
      return 5;
   }

   public String toString() {
      return this.data + "f";
   }

   /**
    * Creates a clone of the tag.
    */
   public NBTTagFloat copy() {
      return new NBTTagFloat(this.data);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof NBTTagFloat && this.data == ((NBTTagFloat)p_equals_1_).data;
      }
   }

   public int hashCode() {
      return Float.floatToIntBits(this.data);
   }

   public ITextComponent toFormattedComponent(String indentation, int indentDepth) {
      ITextComponent itextcomponent = (new TextComponentString("f")).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      return (new TextComponentString(String.valueOf(this.data))).appendSibling(itextcomponent).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public long getLong() {
      return (long)this.data;
   }

   public int getInt() {
      return MathHelper.floor(this.data);
   }

   public short getShort() {
      return (short)(MathHelper.floor(this.data) & '\uffff');
   }

   public byte getByte() {
      return (byte)(MathHelper.floor(this.data) & 255);
   }

   public double getDouble() {
      return (double)this.data;
   }

   public float getFloat() {
      return this.data;
   }

   public Number getAsNumber() {
      return this.data;
   }
}