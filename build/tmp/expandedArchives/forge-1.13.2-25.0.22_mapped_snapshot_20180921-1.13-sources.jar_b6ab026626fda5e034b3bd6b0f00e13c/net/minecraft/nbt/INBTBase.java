package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public interface INBTBase {
   String[] NBT_TYPES = new String[]{"END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE", "BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]", "LONG[]"};
   TextFormatting SYNTAX_HIGHLIGHTING_KEY = TextFormatting.AQUA;
   TextFormatting SYNTAX_HIGHLIGHTING_STRING = TextFormatting.GREEN;
   TextFormatting SYNTAX_HIGHLIGHTING_NUMBER = TextFormatting.GOLD;
   TextFormatting SYNTAX_HIGHLIGHTING_NUMBER_TYPE = TextFormatting.RED;

   /**
    * Write the actual data contents of the tag, implemented in NBT extension classes
    */
   void write(DataOutput output) throws IOException;

   void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException;

   String toString();

   /**
    * Gets the type byte for the tag.
    */
   byte getId();

   /**
    * Creates a new NBTBase object that corresponds with the passed in id.
    */
   static INBTBase create(byte id) {
      switch(id) {
      case 0:
         return new NBTTagEnd();
      case 1:
         return new NBTTagByte();
      case 2:
         return new NBTTagShort();
      case 3:
         return new NBTTagInt();
      case 4:
         return new NBTTagLong();
      case 5:
         return new NBTTagFloat();
      case 6:
         return new NBTTagDouble();
      case 7:
         return new NBTTagByteArray();
      case 8:
         return new NBTTagString();
      case 9:
         return new NBTTagList();
      case 10:
         return new NBTTagCompound();
      case 11:
         return new NBTTagIntArray();
      case 12:
         return new NBTTagLongArray();
      default:
         return null;
      }
   }

   static String getTypeName(int id) {
      switch(id) {
      case 0:
         return "TAG_End";
      case 1:
         return "TAG_Byte";
      case 2:
         return "TAG_Short";
      case 3:
         return "TAG_Int";
      case 4:
         return "TAG_Long";
      case 5:
         return "TAG_Float";
      case 6:
         return "TAG_Double";
      case 7:
         return "TAG_Byte_Array";
      case 8:
         return "TAG_String";
      case 9:
         return "TAG_List";
      case 10:
         return "TAG_Compound";
      case 11:
         return "TAG_Int_Array";
      case 12:
         return "TAG_Long_Array";
      case 99:
         return "Any Numeric Tag";
      default:
         return "UNKNOWN";
      }
   }

   /**
    * Creates a clone of the tag.
    */
   INBTBase copy();

   default String getString() {
      return this.toString();
   }

   default ITextComponent toFormattedComponent() {
      return this.toFormattedComponent("", 0);
   }

   ITextComponent toFormattedComponent(String indentation, int indentDepth);
}