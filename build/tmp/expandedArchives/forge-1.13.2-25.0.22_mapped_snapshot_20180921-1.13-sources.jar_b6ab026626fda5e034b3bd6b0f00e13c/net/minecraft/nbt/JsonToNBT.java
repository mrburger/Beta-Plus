package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.util.text.TextComponentTranslation;

public class JsonToNBT {
   public static final SimpleCommandExceptionType ERROR_TRAILING_DATA = new SimpleCommandExceptionType(new TextComponentTranslation("argument.nbt.trailing"));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_KEY = new SimpleCommandExceptionType(new TextComponentTranslation("argument.nbt.expected.key"));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_VALUE = new SimpleCommandExceptionType(new TextComponentTranslation("argument.nbt.expected.value"));
   public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_LIST = new Dynamic2CommandExceptionType((p_208775_0_, p_208775_1_) -> {
      return new TextComponentTranslation("argument.nbt.list.mixed", p_208775_0_, p_208775_1_);
   });
   public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_ARRAY = new Dynamic2CommandExceptionType((p_208774_0_, p_208774_1_) -> {
      return new TextComponentTranslation("argument.nbt.array.mixed", p_208774_0_, p_208774_1_);
   });
   public static final DynamicCommandExceptionType ERROR_INVALID_ARRAY = new DynamicCommandExceptionType((p_208773_0_) -> {
      return new TextComponentTranslation("argument.nbt.array.invalid", p_208773_0_);
   });
   private static final Pattern DOUBLE_PATTERN_NOSUFFIX = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
   private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
   private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
   private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
   private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
   private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
   private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
   private final StringReader reader;

   public static NBTTagCompound getTagFromJson(String jsonString) throws CommandSyntaxException {
      return (new JsonToNBT(new StringReader(jsonString))).readSingleStruct();
   }

   @VisibleForTesting
   NBTTagCompound readSingleStruct() throws CommandSyntaxException {
      NBTTagCompound nbttagcompound = this.readStruct();
      this.reader.skipWhitespace();
      if (this.reader.canRead()) {
         throw ERROR_TRAILING_DATA.createWithContext(this.reader);
      } else {
         return nbttagcompound;
      }
   }

   public JsonToNBT(StringReader p_i47948_1_) {
      this.reader = p_i47948_1_;
   }

   protected String readKey() throws CommandSyntaxException {
      this.reader.skipWhitespace();
      if (!this.reader.canRead()) {
         throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
      } else {
         return this.reader.readString();
      }
   }

   protected INBTBase readTypedValue() throws CommandSyntaxException {
      this.reader.skipWhitespace();
      int i = this.reader.getCursor();
      if (this.reader.peek() == '"') {
         return new NBTTagString(this.reader.readQuotedString());
      } else {
         String s = this.reader.readUnquotedString();
         if (s.isEmpty()) {
            this.reader.setCursor(i);
            throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
         } else {
            return this.type(s);
         }
      }
   }

   private INBTBase type(String stringIn) {
      try {
         if (FLOAT_PATTERN.matcher(stringIn).matches()) {
            return new NBTTagFloat(Float.parseFloat(stringIn.substring(0, stringIn.length() - 1)));
         }

         if (BYTE_PATTERN.matcher(stringIn).matches()) {
            return new NBTTagByte(Byte.parseByte(stringIn.substring(0, stringIn.length() - 1)));
         }

         if (LONG_PATTERN.matcher(stringIn).matches()) {
            return new NBTTagLong(Long.parseLong(stringIn.substring(0, stringIn.length() - 1)));
         }

         if (SHORT_PATTERN.matcher(stringIn).matches()) {
            return new NBTTagShort(Short.parseShort(stringIn.substring(0, stringIn.length() - 1)));
         }

         if (INT_PATTERN.matcher(stringIn).matches()) {
            return new NBTTagInt(Integer.parseInt(stringIn));
         }

         if (DOUBLE_PATTERN.matcher(stringIn).matches()) {
            return new NBTTagDouble(Double.parseDouble(stringIn.substring(0, stringIn.length() - 1)));
         }

         if (DOUBLE_PATTERN_NOSUFFIX.matcher(stringIn).matches()) {
            return new NBTTagDouble(Double.parseDouble(stringIn));
         }

         if ("true".equalsIgnoreCase(stringIn)) {
            return new NBTTagByte((byte)1);
         }

         if ("false".equalsIgnoreCase(stringIn)) {
            return new NBTTagByte((byte)0);
         }
      } catch (NumberFormatException var3) {
         ;
      }

      return new NBTTagString(stringIn);
   }

   protected INBTBase readValue() throws CommandSyntaxException {
      this.reader.skipWhitespace();
      if (!this.reader.canRead()) {
         throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
      } else {
         char c0 = this.reader.peek();
         if (c0 == '{') {
            return this.readStruct();
         } else {
            return c0 == '[' ? this.readList() : this.readTypedValue();
         }
      }
   }

   protected INBTBase readList() throws CommandSyntaxException {
      return this.reader.canRead(3) && this.reader.peek(1) != '"' && this.reader.peek(2) == ';' ? this.readArrayTag() : this.readListTag();
   }

   public NBTTagCompound readStruct() throws CommandSyntaxException {
      this.expect('{');
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      this.reader.skipWhitespace();

      while(this.reader.canRead() && this.reader.peek() != '}') {
         int i = this.reader.getCursor();
         String s = this.readKey();
         if (s.isEmpty()) {
            this.reader.setCursor(i);
            throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
         }

         this.expect(':');
         nbttagcompound.setTag(s, this.readValue());
         if (!this.hasElementSeparator()) {
            break;
         }

         if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
         }
      }

      this.expect('}');
      return nbttagcompound;
   }

   private INBTBase readListTag() throws CommandSyntaxException {
      this.expect('[');
      this.reader.skipWhitespace();
      if (!this.reader.canRead()) {
         throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
      } else {
         NBTTagList nbttaglist = new NBTTagList();
         int i = -1;

         while(this.reader.peek() != ']') {
            int j = this.reader.getCursor();
            INBTBase inbtbase = this.readValue();
            int k = inbtbase.getId();
            if (i < 0) {
               i = k;
            } else if (k != i) {
               this.reader.setCursor(j);
               throw ERROR_INSERT_MIXED_LIST.createWithContext(this.reader, INBTBase.getTypeName(k), INBTBase.getTypeName(i));
            }

            nbttaglist.add(inbtbase);
            if (!this.hasElementSeparator()) {
               break;
            }

            if (!this.reader.canRead()) {
               throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
            }
         }

         this.expect(']');
         return nbttaglist;
      }
   }

   private INBTBase readArrayTag() throws CommandSyntaxException {
      this.expect('[');
      int i = this.reader.getCursor();
      char c0 = this.reader.read();
      this.reader.read();
      this.reader.skipWhitespace();
      if (!this.reader.canRead()) {
         throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
      } else if (c0 == 'B') {
         return new NBTTagByteArray(this.readArray((byte)7, (byte)1));
      } else if (c0 == 'L') {
         return new NBTTagLongArray(this.readArray((byte)12, (byte)4));
      } else if (c0 == 'I') {
         return new NBTTagIntArray(this.readArray((byte)11, (byte)3));
      } else {
         this.reader.setCursor(i);
         throw ERROR_INVALID_ARRAY.createWithContext(this.reader, String.valueOf(c0));
      }
   }

   private <T extends Number> List<T> readArray(byte p_193603_1_, byte p_193603_2_) throws CommandSyntaxException {
      List<T> list = Lists.newArrayList();

      while(true) {
         if (this.reader.peek() != ']') {
            int i = this.reader.getCursor();
            INBTBase inbtbase = this.readValue();
            int j = inbtbase.getId();
            if (j != p_193603_2_) {
               this.reader.setCursor(i);
               throw ERROR_INSERT_MIXED_ARRAY.createWithContext(this.reader, INBTBase.getTypeName(j), INBTBase.getTypeName(p_193603_1_));
            }

            if (p_193603_2_ == 1) {
               list.add((T)Byte.valueOf(((NBTPrimitive)inbtbase).getByte()));
            } else if (p_193603_2_ == 4) {
               list.add((T)Long.valueOf(((NBTPrimitive)inbtbase).getLong()));
            } else {
               list.add((T)Integer.valueOf(((NBTPrimitive)inbtbase).getInt()));
            }

            if (this.hasElementSeparator()) {
               if (!this.reader.canRead()) {
                  throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
               }
               continue;
            }
         }

         this.expect(']');
         return list;
      }
   }

   private boolean hasElementSeparator() {
      this.reader.skipWhitespace();
      if (this.reader.canRead() && this.reader.peek() == ',') {
         this.reader.skip();
         this.reader.skipWhitespace();
         return true;
      } else {
         return false;
      }
   }

   private void expect(char expected) throws CommandSyntaxException {
      this.reader.skipWhitespace();
      this.reader.expect(expected);
   }
}