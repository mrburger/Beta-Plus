package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTTagCompound implements INBTBase {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");
   /** The key-value pairs for the tag. Each key is a UTF string, each value is a tag. */
   private final Map<String, INBTBase> tagMap = Maps.newHashMap();

   /**
    * Write the actual data contents of the tag, implemented in NBT extension classes
    */
   public void write(DataOutput output) throws IOException {
      for(String s : this.tagMap.keySet()) {
         INBTBase inbtbase = this.tagMap.get(s);
         writeEntry(s, inbtbase, output);
      }

      output.writeByte(0);
   }

   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(384L);
      if (depth > 512) {
         throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
      } else {
         this.tagMap.clear();

         byte b0;
         while((b0 = readType(input, sizeTracker)) != 0) {
            String s = readKey(input, sizeTracker);
            sizeTracker.read((long)(224 + 16 * s.length()));
            INBTBase inbtbase = readNBT(b0, s, input, depth + 1, sizeTracker);
            if (this.tagMap.put(s, inbtbase) != null) {
               sizeTracker.read(288L);
            }
         }

      }
   }

   /**
    * Gets a set with the names of the keys in the tag compound.
    */
   public Set<String> keySet() {
      return this.tagMap.keySet();
   }

   /**
    * Gets the type byte for the tag.
    */
   public byte getId() {
      return 10;
   }

   public int size() {
      return this.tagMap.size();
   }

   /**
    * Stores the given tag into the map with the given string key. This is mostly used to store tag lists.
    */
   public void setTag(String key, INBTBase value) {
      this.tagMap.put(key, value);
   }

   /**
    * Stores a new NBTTagByte with the given byte value into the map with the given string key.
    */
   public void setByte(String key, byte value) {
      this.tagMap.put(key, new NBTTagByte(value));
   }

   /**
    * Stores a new NBTTagShort with the given short value into the map with the given string key.
    */
   public void setShort(String key, short value) {
      this.tagMap.put(key, new NBTTagShort(value));
   }

   /**
    * Stores a new NBTTagInt with the given integer value into the map with the given string key.
    */
   public void setInt(String key, int value) {
      this.tagMap.put(key, new NBTTagInt(value));
   }

   /**
    * Stores a new NBTTagLong with the given long value into the map with the given string key.
    */
   public void setLong(String key, long value) {
      this.tagMap.put(key, new NBTTagLong(value));
   }

   public void setUniqueId(String key, UUID value) {
      this.setLong(key + "Most", value.getMostSignificantBits());
      this.setLong(key + "Least", value.getLeastSignificantBits());
   }

   @Nullable
   public UUID getUniqueId(String key) {
      return new UUID(this.getLong(key + "Most"), this.getLong(key + "Least"));
   }

   public boolean hasUniqueId(String key) {
      return this.contains(key + "Most", 99) && this.contains(key + "Least", 99);
   }

   /**
    * Stores a new NBTTagFloat with the given float value into the map with the given string key.
    */
   public void setFloat(String key, float value) {
      this.tagMap.put(key, new NBTTagFloat(value));
   }

   /**
    * Stores a new NBTTagDouble with the given double value into the map with the given string key.
    */
   public void setDouble(String key, double value) {
      this.tagMap.put(key, new NBTTagDouble(value));
   }

   /**
    * Stores a new NBTTagString with the given string value into the map with the given string key.
    */
   public void setString(String key, String value) {
      this.tagMap.put(key, new NBTTagString(value));
   }

   /**
    * Stores a new NBTTagByteArray with the given array as data into the map with the given string key.
    */
   public void setByteArray(String key, byte[] value) {
      this.tagMap.put(key, new NBTTagByteArray(value));
   }

   /**
    * Stores a new NBTTagIntArray with the given array as data into the map with the given string key.
    */
   public void setIntArray(String key, int[] value) {
      this.tagMap.put(key, new NBTTagIntArray(value));
   }

   public void setIntArray(String p_197646_1_, List<Integer> p_197646_2_) {
      this.tagMap.put(p_197646_1_, new NBTTagIntArray(p_197646_2_));
   }

   public void setLongArray(String p_197644_1_, long[] p_197644_2_) {
      this.tagMap.put(p_197644_1_, new NBTTagLongArray(p_197644_2_));
   }

   public void setLongArray(String p_202168_1_, List<Long> p_202168_2_) {
      this.tagMap.put(p_202168_1_, new NBTTagLongArray(p_202168_2_));
   }

   /**
    * Stores the given boolean value as a NBTTagByte, storing 1 for true and 0 for false, using the given string key.
    */
   public void setBoolean(String key, boolean value) {
      this.setByte(key, (byte)(value ? 1 : 0));
   }

   /**
    * gets a generic tag with the specified name
    */
   public INBTBase getTag(String key) {
      return this.tagMap.get(key);
   }

   /**
    * Gets the ID byte for the given tag key
    */
   public byte getTagId(String key) {
      INBTBase inbtbase = this.tagMap.get(key);
      return inbtbase == null ? 0 : inbtbase.getId();
   }

   /**
    * Returns whether the given string has been previously stored as a key in the map.
    */
   public boolean hasKey(String key) {
      return this.tagMap.containsKey(key);
   }

   /**
    * Returns whether the given string has been previously stored as a key in this tag compound as a particular type,
    * denoted by a parameter in the form of an ordinal. If the provided ordinal is 99, this method will match tag types
    * representing numbers.
    */
   public boolean contains(String key, int type) {
      int i = this.getTagId(key);
      if (i == type) {
         return true;
      } else if (type != 99) {
         return false;
      } else {
         return i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6;
      }
   }

   /**
    * Retrieves a byte value using the specified key, or 0 if no such key was stored.
    */
   public byte getByte(String key) {
      try {
         if (this.contains(key, 99)) {
            return ((NBTPrimitive)this.tagMap.get(key)).getByte();
         }
      } catch (ClassCastException var3) {
         ;
      }

      return 0;
   }

   /**
    * Retrieves a short value using the specified key, or 0 if no such key was stored.
    */
   public short getShort(String key) {
      try {
         if (this.contains(key, 99)) {
            return ((NBTPrimitive)this.tagMap.get(key)).getShort();
         }
      } catch (ClassCastException var3) {
         ;
      }

      return 0;
   }

   /**
    * Retrieves an integer value using the specified key, or 0 if no such key was stored.
    */
   public int getInt(String key) {
      try {
         if (this.contains(key, 99)) {
            return ((NBTPrimitive)this.tagMap.get(key)).getInt();
         }
      } catch (ClassCastException var3) {
         ;
      }

      return 0;
   }

   /**
    * Retrieves a long value using the specified key, or 0 if no such key was stored.
    */
   public long getLong(String key) {
      try {
         if (this.contains(key, 99)) {
            return ((NBTPrimitive)this.tagMap.get(key)).getLong();
         }
      } catch (ClassCastException var3) {
         ;
      }

      return 0L;
   }

   /**
    * Retrieves a float value using the specified key, or 0 if no such key was stored.
    */
   public float getFloat(String key) {
      try {
         if (this.contains(key, 99)) {
            return ((NBTPrimitive)this.tagMap.get(key)).getFloat();
         }
      } catch (ClassCastException var3) {
         ;
      }

      return 0.0F;
   }

   /**
    * Retrieves a double value using the specified key, or 0 if no such key was stored.
    */
   public double getDouble(String key) {
      try {
         if (this.contains(key, 99)) {
            return ((NBTPrimitive)this.tagMap.get(key)).getDouble();
         }
      } catch (ClassCastException var3) {
         ;
      }

      return 0.0D;
   }

   /**
    * Retrieves a string value using the specified key, or an empty string if no such key was stored.
    */
   public String getString(String key) {
      try {
         if (this.contains(key, 8)) {
            return this.tagMap.get(key).getString();
         }
      } catch (ClassCastException var3) {
         ;
      }

      return "";
   }

   /**
    * Retrieves a byte array using the specified key, or a zero-length array if no such key was stored.
    */
   public byte[] getByteArray(String key) {
      try {
         if (this.contains(key, 7)) {
            return ((NBTTagByteArray)this.tagMap.get(key)).getByteArray();
         }
      } catch (ClassCastException classcastexception) {
         throw new ReportedException(this.createCrashReport(key, 7, classcastexception));
      }

      return new byte[0];
   }

   /**
    * Retrieves an int array using the specified key, or a zero-length array if no such key was stored.
    */
   public int[] getIntArray(String key) {
      try {
         if (this.contains(key, 11)) {
            return ((NBTTagIntArray)this.tagMap.get(key)).getIntArray();
         }
      } catch (ClassCastException classcastexception) {
         throw new ReportedException(this.createCrashReport(key, 11, classcastexception));
      }

      return new int[0];
   }

   public long[] getLongArray(String key) {
      try {
         if (this.contains(key, 12)) {
            return ((NBTTagLongArray)this.tagMap.get(key)).getAsLongArray();
         }
      } catch (ClassCastException classcastexception) {
         throw new ReportedException(this.createCrashReport(key, 12, classcastexception));
      }

      return new long[0];
   }

   /**
    * Retrieves a NBTTagCompound subtag matching the specified key, or a new empty NBTTagCompound if no such key was
    * stored.
    */
   public NBTTagCompound getCompound(String key) {
      try {
         if (this.contains(key, 10)) {
            return (NBTTagCompound)this.tagMap.get(key);
         }
      } catch (ClassCastException classcastexception) {
         throw new ReportedException(this.createCrashReport(key, 10, classcastexception));
      }

      return new NBTTagCompound();
   }

   /**
    * Gets the NBTTagList object with the given name.
    */
   public NBTTagList getList(String key, int type) {
      try {
         if (this.getTagId(key) == 9) {
            NBTTagList nbttaglist = (NBTTagList)this.tagMap.get(key);
            if (!nbttaglist.isEmpty() && nbttaglist.getTagType() != type) {
               return new NBTTagList();
            }

            return nbttaglist;
         }
      } catch (ClassCastException classcastexception) {
         throw new ReportedException(this.createCrashReport(key, 9, classcastexception));
      }

      return new NBTTagList();
   }

   /**
    * Retrieves a boolean value using the specified key, or false if no such key was stored. This uses the getByte
    * method.
    */
   public boolean getBoolean(String key) {
      return this.getByte(key) != 0;
   }

   /**
    * Remove the specified tag.
    */
   public void removeTag(String key) {
      this.tagMap.remove(key);
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder("{");
      Collection<String> collection = this.tagMap.keySet();
      if (LOGGER.isDebugEnabled()) {
         List<String> list = Lists.newArrayList(this.tagMap.keySet());
         Collections.sort(list);
         collection = list;
      }

      for(String s : collection) {
         if (stringbuilder.length() != 1) {
            stringbuilder.append(',');
         }

         stringbuilder.append(handleEscape(s)).append(':').append(this.tagMap.get(s));
      }

      return stringbuilder.append('}').toString();
   }

   public boolean isEmpty() {
      return this.tagMap.isEmpty();
   }

   /**
    * Create a crash report which indicates a NBT read error.
    */
   private CrashReport createCrashReport(String key, int expectedType, ClassCastException ex) {
      CrashReport crashreport = CrashReport.makeCrashReport(ex, "Reading NBT data");
      CrashReportCategory crashreportcategory = crashreport.makeCategoryDepth("Corrupt NBT tag", 1);
      crashreportcategory.addDetail("Tag type found", () -> {
         return NBT_TYPES[this.tagMap.get(key).getId()];
      });
      crashreportcategory.addDetail("Tag type expected", () -> {
         return NBT_TYPES[expectedType];
      });
      crashreportcategory.addDetail("Tag name", key);
      return crashreport;
   }

   /**
    * Creates a clone of the tag.
    */
   public NBTTagCompound copy() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();

      for(String s : this.tagMap.keySet()) {
         nbttagcompound.setTag(s, this.tagMap.get(s).copy());
      }

      return nbttagcompound;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof NBTTagCompound && Objects.equals(this.tagMap, ((NBTTagCompound)p_equals_1_).tagMap);
      }
   }

   public int hashCode() {
      return this.tagMap.hashCode();
   }

   private static void writeEntry(String name, INBTBase data, DataOutput output) throws IOException {
      output.writeByte(data.getId());
      if (data.getId() != 0) {
         output.writeUTF(name);
         data.write(output);
      }
   }

   private static byte readType(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(8);
      return input.readByte();
   }

   private static String readKey(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
      return input.readUTF();
   }

   static INBTBase readNBT(byte id, String key, DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(32); //Forge: 4 extra bytes for the object allocation.
      INBTBase inbtbase = INBTBase.create(id);

      try {
         inbtbase.read(input, depth, sizeTracker);
         return inbtbase;
      } catch (IOException ioexception) {
         CrashReport crashreport = CrashReport.makeCrashReport(ioexception, "Loading NBT data");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("NBT Tag");
         crashreportcategory.addDetail("Tag name", key);
         crashreportcategory.addDetail("Tag type", id);
         throw new ReportedException(crashreport);
      }
   }

   /**
    * Deep copies all the tags of {@code other} into this tag, then returns itself.
    */
   public NBTTagCompound merge(NBTTagCompound other) {
      for(String s : other.tagMap.keySet()) {
         INBTBase inbtbase = other.tagMap.get(s);
         if (inbtbase.getId() == 10) {
            if (this.contains(s, 10)) {
               NBTTagCompound nbttagcompound = this.getCompound(s);
               nbttagcompound.merge((NBTTagCompound)inbtbase);
            } else {
               this.setTag(s, inbtbase.copy());
            }
         } else {
            this.setTag(s, inbtbase.copy());
         }
      }

      return this;
   }

   protected static String handleEscape(String p_193582_0_) {
      return SIMPLE_VALUE.matcher(p_193582_0_).matches() ? p_193582_0_ : NBTTagString.quoteAndEscape(p_193582_0_, true);
   }

   protected static ITextComponent func_197642_t(String p_197642_0_) {
      if (SIMPLE_VALUE.matcher(p_197642_0_).matches()) {
         return (new TextComponentString(p_197642_0_)).applyTextStyle(SYNTAX_HIGHLIGHTING_KEY);
      } else {
         ITextComponent itextcomponent = (new TextComponentString(NBTTagString.quoteAndEscape(p_197642_0_, false))).applyTextStyle(SYNTAX_HIGHLIGHTING_KEY);
         return (new TextComponentString("\"")).appendSibling(itextcomponent).appendText("\"");
      }
   }

   public ITextComponent toFormattedComponent(String indentation, int indentDepth) {
      if (this.tagMap.isEmpty()) {
         return new TextComponentString("{}");
      } else {
         ITextComponent itextcomponent = new TextComponentString("{");
         Collection<String> collection = this.tagMap.keySet();
         if (LOGGER.isDebugEnabled()) {
            List<String> list = Lists.newArrayList(this.tagMap.keySet());
            Collections.sort(list);
            collection = list;
         }

         if (!indentation.isEmpty()) {
            itextcomponent.appendText("\n");
         }

         ITextComponent itextcomponent1;
         for(Iterator<String> iterator = collection.iterator(); iterator.hasNext(); itextcomponent.appendSibling(itextcomponent1)) {
            String s = iterator.next();
            itextcomponent1 = (new TextComponentString(Strings.repeat(indentation, indentDepth + 1))).appendSibling(func_197642_t(s)).appendText(String.valueOf(':')).appendText(" ").appendSibling(this.tagMap.get(s).toFormattedComponent(indentation, indentDepth + 1));
            if (iterator.hasNext()) {
               itextcomponent1.appendText(String.valueOf(',')).appendText(indentation.isEmpty() ? " " : "\n");
            }
         }

         if (!indentation.isEmpty()) {
            itextcomponent.appendText("\n").appendText(Strings.repeat(indentation, indentDepth));
         }

         itextcomponent.appendText("}");
         return itextcomponent;
      }
   }
}