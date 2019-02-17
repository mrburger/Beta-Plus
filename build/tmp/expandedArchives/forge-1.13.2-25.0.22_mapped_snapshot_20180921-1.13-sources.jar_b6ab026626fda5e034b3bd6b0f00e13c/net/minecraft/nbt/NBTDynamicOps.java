package net.minecraft.nbt;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class NBTDynamicOps implements DynamicOps<INBTBase> {
   public static final NBTDynamicOps INSTANCE = new NBTDynamicOps();

   public INBTBase empty() {
      return new NBTTagEnd();
   }

   public Type<?> getType(INBTBase p_getType_1_) {
      switch(p_getType_1_.getId()) {
      case 0:
         return DSL.nilType();
      case 1:
         return DSL.byteType();
      case 2:
         return DSL.shortType();
      case 3:
         return DSL.intType();
      case 4:
         return DSL.longType();
      case 5:
         return DSL.floatType();
      case 6:
         return DSL.doubleType();
      case 7:
         return DSL.list(DSL.byteType());
      case 8:
         return DSL.string();
      case 9:
         return DSL.list(DSL.remainderType());
      case 10:
         return DSL.compoundList(DSL.remainderType(), DSL.remainderType());
      case 11:
         return DSL.list(DSL.intType());
      case 12:
         return DSL.list(DSL.longType());
      default:
         return DSL.remainderType();
      }
   }

   public Optional<Number> getNumberValue(INBTBase p_getNumberValue_1_) {
      return p_getNumberValue_1_ instanceof NBTPrimitive ? Optional.of(((NBTPrimitive)p_getNumberValue_1_).getAsNumber()) : Optional.empty();
   }

   public INBTBase createNumeric(Number p_createNumeric_1_) {
      return new NBTTagDouble(p_createNumeric_1_.doubleValue());
   }

   public INBTBase createByte(byte p_createByte_1_) {
      return new NBTTagByte(p_createByte_1_);
   }

   public INBTBase createShort(short p_createShort_1_) {
      return new NBTTagShort(p_createShort_1_);
   }

   public INBTBase createInt(int p_createInt_1_) {
      return new NBTTagInt(p_createInt_1_);
   }

   public INBTBase createLong(long p_createLong_1_) {
      return new NBTTagLong(p_createLong_1_);
   }

   public INBTBase createFloat(float p_createFloat_1_) {
      return new NBTTagFloat(p_createFloat_1_);
   }

   public INBTBase createDouble(double p_createDouble_1_) {
      return new NBTTagDouble(p_createDouble_1_);
   }

   public Optional<String> getStringValue(INBTBase p_getStringValue_1_) {
      return p_getStringValue_1_ instanceof NBTTagString ? Optional.of(p_getStringValue_1_.getString()) : Optional.empty();
   }

   public INBTBase createString(String p_createString_1_) {
      return new NBTTagString(p_createString_1_);
   }

   public INBTBase mergeInto(INBTBase p_mergeInto_1_, INBTBase p_mergeInto_2_) {
      if (p_mergeInto_2_ instanceof NBTTagEnd) {
         return p_mergeInto_1_;
      } else if (!(p_mergeInto_1_ instanceof NBTTagCompound)) {
         if (p_mergeInto_1_ instanceof NBTTagEnd) {
            throw new IllegalArgumentException("mergeInto called with a null input.");
         } else if (p_mergeInto_1_ instanceof NBTTagCollection) {
            NBTTagCollection<INBTBase> nbttagcollection = new NBTTagList();
            NBTTagCollection<?> nbttagcollection1 = (NBTTagCollection)p_mergeInto_1_;
            nbttagcollection.addAll(nbttagcollection1);
            nbttagcollection.add(p_mergeInto_2_);
            return nbttagcollection;
         } else {
            return p_mergeInto_1_;
         }
      } else if (!(p_mergeInto_2_ instanceof NBTTagCompound)) {
         return p_mergeInto_1_;
      } else {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         NBTTagCompound nbttagcompound1 = (NBTTagCompound)p_mergeInto_1_;

         for(String s : nbttagcompound1.keySet()) {
            nbttagcompound.setTag(s, nbttagcompound1.getTag(s));
         }

         NBTTagCompound nbttagcompound2 = (NBTTagCompound)p_mergeInto_2_;

         for(String s1 : nbttagcompound2.keySet()) {
            nbttagcompound.setTag(s1, nbttagcompound2.getTag(s1));
         }

         return nbttagcompound;
      }
   }

   public INBTBase mergeInto(INBTBase p_mergeInto_1_, INBTBase p_mergeInto_2_, INBTBase p_mergeInto_3_) {
      NBTTagCompound nbttagcompound;
      if (p_mergeInto_1_ instanceof NBTTagEnd) {
         nbttagcompound = new NBTTagCompound();
      } else {
         if (!(p_mergeInto_1_ instanceof NBTTagCompound)) {
            return p_mergeInto_1_;
         }

         NBTTagCompound nbttagcompound1 = (NBTTagCompound)p_mergeInto_1_;
         nbttagcompound = new NBTTagCompound();
         nbttagcompound1.keySet().forEach((p_212014_2_) -> {
            nbttagcompound.setTag(p_212014_2_, nbttagcompound1.getTag(p_212014_2_));
         });
      }

      nbttagcompound.setTag(p_mergeInto_2_.getString(), p_mergeInto_3_);
      return nbttagcompound;
   }

   public INBTBase merge(INBTBase p_merge_1_, INBTBase p_merge_2_) {
      if (p_merge_1_ instanceof NBTTagEnd) {
         return p_merge_2_;
      } else if (p_merge_2_ instanceof NBTTagEnd) {
         return p_merge_1_;
      } else {
         if (p_merge_1_ instanceof NBTTagCompound && p_merge_2_ instanceof NBTTagCompound) {
            NBTTagCompound nbttagcompound = (NBTTagCompound)p_merge_1_;
            NBTTagCompound nbttagcompound1 = (NBTTagCompound)p_merge_2_;
            NBTTagCompound nbttagcompound2 = new NBTTagCompound();
            nbttagcompound.keySet().forEach((p_211384_2_) -> {
               nbttagcompound2.setTag(p_211384_2_, nbttagcompound.getTag(p_211384_2_));
            });
            nbttagcompound1.keySet().forEach((p_212012_2_) -> {
               nbttagcompound2.setTag(p_212012_2_, nbttagcompound1.getTag(p_212012_2_));
            });
         }

         if (p_merge_1_ instanceof NBTTagCollection && p_merge_2_ instanceof NBTTagCollection) {
            NBTTagList nbttaglist = new NBTTagList();
            nbttaglist.addAll((NBTTagCollection)p_merge_1_);
            nbttaglist.addAll((NBTTagCollection)p_merge_2_);
            return nbttaglist;
         } else {
            throw new IllegalArgumentException("Could not merge " + p_merge_1_ + " and " + p_merge_2_);
         }
      }
   }

   public Optional<Map<INBTBase, INBTBase>> getMapValues(INBTBase p_getMapValues_1_) {
      if (p_getMapValues_1_ instanceof NBTTagCompound) {
         NBTTagCompound nbttagcompound = (NBTTagCompound)p_getMapValues_1_;
         return Optional.of(nbttagcompound.keySet().stream().map((p_210819_2_) -> {
            return Pair.of(this.createString(p_210819_2_), nbttagcompound.getTag(p_210819_2_));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
      } else {
         return Optional.empty();
      }
   }

   public INBTBase createMap(Map<INBTBase, INBTBase> p_createMap_1_) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();

      for(Entry<INBTBase, INBTBase> entry : p_createMap_1_.entrySet()) {
         nbttagcompound.setTag(entry.getKey().getString(), entry.getValue());
      }

      return nbttagcompound;
   }

   public Optional<Stream<INBTBase>> getStream(INBTBase p_getStream_1_) {
      return p_getStream_1_ instanceof NBTTagCollection ? Optional.of(((NBTTagCollection)p_getStream_1_).stream().map((p_210817_0_) -> {
         return p_210817_0_;
      })) : Optional.empty();
   }

   public Optional<ByteBuffer> getByteBuffer(INBTBase p_getByteBuffer_1_) {
      return p_getByteBuffer_1_ instanceof NBTTagByteArray ? Optional.of(ByteBuffer.wrap(((NBTTagByteArray)p_getByteBuffer_1_).getByteArray())) : DynamicOps.super.getByteBuffer(p_getByteBuffer_1_);
   }

   public INBTBase createByteList(ByteBuffer p_createByteList_1_) {
      return new NBTTagByteArray(DataFixUtils.toArray(p_createByteList_1_));
   }

   public Optional<IntStream> getIntStream(INBTBase p_getIntStream_1_) {
      return p_getIntStream_1_ instanceof NBTTagIntArray ? Optional.of(Arrays.stream(((NBTTagIntArray)p_getIntStream_1_).getIntArray())) : DynamicOps.super.getIntStream(p_getIntStream_1_);
   }

   public INBTBase createIntList(IntStream p_createIntList_1_) {
      return new NBTTagIntArray(p_createIntList_1_.toArray());
   }

   public Optional<LongStream> getLongStream(INBTBase p_getLongStream_1_) {
      return p_getLongStream_1_ instanceof NBTTagLongArray ? Optional.of(Arrays.stream(((NBTTagLongArray)p_getLongStream_1_).getAsLongArray())) : DynamicOps.super.getLongStream(p_getLongStream_1_);
   }

   public INBTBase createLongList(LongStream p_createLongList_1_) {
      return new NBTTagLongArray(p_createLongList_1_.toArray());
   }

   public INBTBase createList(Stream<INBTBase> p_createList_1_) {
      PeekingIterator<INBTBase> peekingiterator = Iterators.peekingIterator(p_createList_1_.iterator());
      if (!peekingiterator.hasNext()) {
         return new NBTTagList();
      } else {
         INBTBase inbtbase = peekingiterator.peek();
         if (inbtbase instanceof NBTTagByte) {
            ArrayList<Byte> arraylist2 = Lists.newArrayList(Iterators.transform(peekingiterator, (p_210815_0_) -> {
               return ((NBTTagByte)p_210815_0_).getByte();
            }));
            return new NBTTagByteArray((List<Byte>)arraylist2);
         } else if (inbtbase instanceof NBTTagInt) {
            ArrayList<Integer> arraylist1 = Lists.newArrayList(Iterators.transform(peekingiterator, (p_210818_0_) -> {
               return ((NBTTagInt)p_210818_0_).getInt();
            }));
            return new NBTTagIntArray((List<Integer>)arraylist1);
         } else if (inbtbase instanceof NBTTagLong) {
            ArrayList<Long> arraylist = Lists.newArrayList(Iterators.transform(peekingiterator, (p_210816_0_) -> {
               return ((NBTTagLong)p_210816_0_).getLong();
            }));
            return new NBTTagLongArray((List<Long>)arraylist);
         } else {
            NBTTagList nbttaglist = new NBTTagList();

            while(peekingiterator.hasNext()) {
               INBTBase inbtbase1 = peekingiterator.next();
               if (!(inbtbase1 instanceof NBTTagEnd)) {
                  nbttaglist.add(inbtbase1);
               }
            }

            return nbttaglist;
         }
      }
   }

   public INBTBase remove(INBTBase p_remove_1_, String p_remove_2_) {
      if (p_remove_1_ instanceof NBTTagCompound) {
         NBTTagCompound nbttagcompound = (NBTTagCompound)p_remove_1_;
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();
         nbttagcompound.keySet().stream().filter((p_212019_1_) -> {
            return !Objects.equals(p_212019_1_, p_remove_2_);
         }).forEach((p_212010_2_) -> {
            nbttagcompound1.setTag(p_212010_2_, nbttagcompound.getTag(p_212010_2_));
         });
         return nbttagcompound1;
      } else {
         return p_remove_1_;
      }
   }

   public String toString() {
      return "NBT";
   }
}