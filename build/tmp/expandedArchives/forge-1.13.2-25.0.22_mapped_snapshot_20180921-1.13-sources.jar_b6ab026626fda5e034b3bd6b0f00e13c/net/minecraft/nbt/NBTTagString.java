package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class NBTTagString implements INBTBase {
   /** The string value for the tag (cannot be empty). */
   private String data;

   public NBTTagString() {
      this("");
   }

   public NBTTagString(String data) {
      Objects.requireNonNull(data, "Null string not allowed");
      this.data = data;
   }

   /**
    * Write the actual data contents of the tag, implemented in NBT extension classes
    */
   public void write(DataOutput output) throws IOException {
      output.writeUTF(this.data);
   }

   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(288L);
      this.data = input.readUTF();
      NBTSizeTracker.readUTF(sizeTracker, this.data);  // Forge: Correctly read String length including header.
   }

   /**
    * Gets the type byte for the tag.
    */
   public byte getId() {
      return 8;
   }

   public String toString() {
      return quoteAndEscape(this.data, true);
   }

   /**
    * Creates a clone of the tag.
    */
   public NBTTagString copy() {
      return new NBTTagString(this.data);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof NBTTagString && Objects.equals(this.data, ((NBTTagString)p_equals_1_).data);
      }
   }

   public int hashCode() {
      return this.data.hashCode();
   }

   public String getString() {
      return this.data;
   }

   public ITextComponent toFormattedComponent(String indentation, int indentDepth) {
      ITextComponent itextcomponent = (new TextComponentString(quoteAndEscape(this.data, false))).applyTextStyle(SYNTAX_HIGHLIGHTING_STRING);
      return (new TextComponentString("\"")).appendSibling(itextcomponent).appendText("\"");
   }

   public static String quoteAndEscape(String p_197654_0_, boolean p_197654_1_) {
      StringBuilder stringbuilder = new StringBuilder();
      if (p_197654_1_) {
         stringbuilder.append('"');
      }

      for(int i = 0; i < p_197654_0_.length(); ++i) {
         char c0 = p_197654_0_.charAt(i);
         if (c0 == '\\' || c0 == '"') {
            stringbuilder.append('\\');
         }

         stringbuilder.append(c0);
      }

      if (p_197654_1_) {
         stringbuilder.append('"');
      }

      return stringbuilder.toString();
   }
}