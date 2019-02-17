package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.storage.WorldSavedDataStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LegacyStructureDataUtil {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map<String, String> field_208220_b = Util.make(Maps.newHashMap(), (p_208213_0_) -> {
      p_208213_0_.put("Village", "Village");
      p_208213_0_.put("Mineshaft", "Mineshaft");
      p_208213_0_.put("Mansion", "Mansion");
      p_208213_0_.put("Igloo", "Temple");
      p_208213_0_.put("Desert_Pyramid", "Temple");
      p_208213_0_.put("Jungle_Pyramid", "Temple");
      p_208213_0_.put("Swamp_Hut", "Temple");
      p_208213_0_.put("Stronghold", "Stronghold");
      p_208213_0_.put("Monument", "Monument");
      p_208213_0_.put("Fortress", "Fortress");
      p_208213_0_.put("EndCity", "EndCity");
   });
   private static final Map<String, String> field_208221_c = Util.make(Maps.newHashMap(), (p_208215_0_) -> {
      p_208215_0_.put("Iglu", "Igloo");
      p_208215_0_.put("TeDP", "Desert_Pyramid");
      p_208215_0_.put("TeJP", "Jungle_Pyramid");
      p_208215_0_.put("TeSH", "Swamp_Hut");
   });
   private final boolean field_208222_d;
   private final Map<String, Long2ObjectMap<NBTTagCompound>> field_208223_e = Maps.newHashMap();
   private final Map<String, StructureIndexesSavedData> field_208224_f = Maps.newHashMap();

   public LegacyStructureDataUtil(@Nullable WorldSavedDataStorage p_i49789_1_) {
      this.func_212184_a(p_i49789_1_);
      boolean flag = false;

      for(String s : this.func_208218_b()) {
         flag |= this.field_208223_e.get(s) != null;
      }

      this.field_208222_d = flag;
   }

   public void func_208216_a(long p_208216_1_) {
      for(String s : this.func_208214_a()) {
         StructureIndexesSavedData structureindexessaveddata = this.field_208224_f.get(s);
         if (structureindexessaveddata != null && structureindexessaveddata.func_208023_c(p_208216_1_)) {
            structureindexessaveddata.func_201762_c(p_208216_1_);
            structureindexessaveddata.markDirty();
         }
      }

   }

   public NBTTagCompound func_212181_a(NBTTagCompound p_212181_1_) {
      NBTTagCompound nbttagcompound = p_212181_1_.getCompound("Level");
      ChunkPos chunkpos = new ChunkPos(nbttagcompound.getInt("xPos"), nbttagcompound.getInt("zPos"));
      if (this.func_208209_a(chunkpos.x, chunkpos.z)) {
         p_212181_1_ = this.func_212182_a(p_212181_1_, chunkpos);
      }

      NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Structures");
      NBTTagCompound nbttagcompound2 = nbttagcompound1.getCompound("References");

      for(String s : this.func_208218_b()) {
         Structure<?> structure = Feature.STRUCTURES.get(s.toLowerCase(Locale.ROOT));
         if (!nbttagcompound2.contains(s, 12) && structure != null) {
            int i = structure.getSize();
            LongList longlist = new LongArrayList();

            for(int j = chunkpos.x - i; j <= chunkpos.x + i; ++j) {
               for(int k = chunkpos.z - i; k <= chunkpos.z + i; ++k) {
                  if (this.func_208211_a(j, k, s)) {
                     longlist.add(ChunkPos.asLong(j, k));
                  }
               }
            }

            nbttagcompound2.setLongArray(s, longlist);
         }
      }

      nbttagcompound1.setTag("References", nbttagcompound2);
      nbttagcompound.setTag("Structures", nbttagcompound1);
      p_212181_1_.setTag("Level", nbttagcompound);
      return p_212181_1_;
   }

   protected abstract String[] func_208214_a();

   protected abstract String[] func_208218_b();

   private boolean func_208211_a(int p_208211_1_, int p_208211_2_, String p_208211_3_) {
      if (!this.field_208222_d) {
         return false;
      } else {
         return this.field_208223_e.get(p_208211_3_) != null && this.field_208224_f.get(field_208220_b.get(p_208211_3_)).func_208024_b(ChunkPos.asLong(p_208211_1_, p_208211_2_));
      }
   }

   private boolean func_208209_a(int p_208209_1_, int p_208209_2_) {
      if (!this.field_208222_d) {
         return false;
      } else {
         for(String s : this.func_208218_b()) {
            if (this.field_208223_e.get(s) != null && this.field_208224_f.get(field_208220_b.get(s)).func_208023_c(ChunkPos.asLong(p_208209_1_, p_208209_2_))) {
               return true;
            }
         }

         return false;
      }
   }

   private NBTTagCompound func_212182_a(NBTTagCompound p_212182_1_, ChunkPos p_212182_2_) {
      NBTTagCompound nbttagcompound = p_212182_1_.getCompound("Level");
      NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Structures");
      NBTTagCompound nbttagcompound2 = nbttagcompound1.getCompound("Starts");

      for(String s : this.func_208218_b()) {
         Long2ObjectMap<NBTTagCompound> long2objectmap = this.field_208223_e.get(s);
         if (long2objectmap != null) {
            long i = p_212182_2_.asLong();
            if (this.field_208224_f.get(field_208220_b.get(s)).func_208023_c(i)) {
               NBTTagCompound nbttagcompound3 = long2objectmap.get(i);
               if (nbttagcompound3 != null) {
                  nbttagcompound2.setTag(s, nbttagcompound3);
               }
            }
         }
      }

      nbttagcompound1.setTag("Starts", nbttagcompound2);
      nbttagcompound.setTag("Structures", nbttagcompound1);
      p_212182_1_.setTag("Level", nbttagcompound);
      return p_212182_1_;
   }

   private void func_212184_a(@Nullable WorldSavedDataStorage p_212184_1_) {
      if (p_212184_1_ != null) {
         for(String s : this.func_208214_a()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            try {
               nbttagcompound = p_212184_1_.func_208028_a(s, 1493).getCompound("data").getCompound("Features");
               if (nbttagcompound.isEmpty()) {
                  continue;
               }
            } catch (IOException var15) {
               ;
            }

            for(String s1 : nbttagcompound.keySet()) {
               NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound(s1);
               long i = ChunkPos.asLong(nbttagcompound1.getInt("ChunkX"), nbttagcompound1.getInt("ChunkZ"));
               NBTTagList nbttaglist = nbttagcompound1.getList("Children", 10);
               if (!nbttaglist.isEmpty()) {
                  String s3 = nbttaglist.getCompound(0).getString("id");
                  String s4 = field_208221_c.get(s3);
                  if (s4 != null) {
                     nbttagcompound1.setString("id", s4);
                  }
               }

               String s6 = nbttagcompound1.getString("id");
               this.field_208223_e.computeIfAbsent(s6, (p_208208_0_) -> {
                  return new Long2ObjectOpenHashMap<>();
               }).put(i, nbttagcompound1);
            }

            String s5 = s + "_index";
            StructureIndexesSavedData structureindexessaveddata = p_212184_1_.func_212426_a(DimensionType.OVERWORLD, StructureIndexesSavedData::new, s5);
            if (structureindexessaveddata != null && !structureindexessaveddata.getAll().isEmpty()) {
               this.field_208224_f.put(s, structureindexessaveddata);
            } else {
               StructureIndexesSavedData structureindexessaveddata1 = new StructureIndexesSavedData(s5);
               this.field_208224_f.put(s, structureindexessaveddata1);

               for(String s2 : nbttagcompound.keySet()) {
                  NBTTagCompound nbttagcompound2 = nbttagcompound.getCompound(s2);
                  structureindexessaveddata1.func_201763_a(ChunkPos.asLong(nbttagcompound2.getInt("ChunkX"), nbttagcompound2.getInt("ChunkZ")));
               }

               p_212184_1_.func_212424_a(DimensionType.OVERWORLD, s5, structureindexessaveddata1);
               structureindexessaveddata1.markDirty();
            }
         }

      }
   }

   public static LegacyStructureDataUtil func_212183_a(DimensionType p_212183_0_, @Nullable WorldSavedDataStorage p_212183_1_) {
      if (p_212183_0_ == DimensionType.OVERWORLD) {
         return new LegacyStructureDataUtil.Overworld(p_212183_1_);
      } else if (p_212183_0_ == DimensionType.NETHER) {
         return new LegacyStructureDataUtil.Nether(p_212183_1_);
      } else if (p_212183_0_ == DimensionType.THE_END) {
         return new LegacyStructureDataUtil.End(p_212183_1_);
      } else {
         throw new RuntimeException(String.format("Unknown dimension type : %s", p_212183_0_));
      }
   }

   public static class End extends LegacyStructureDataUtil {
      private static final String[] field_208227_a = new String[]{"EndCity"};

      public End(@Nullable WorldSavedDataStorage p_i49799_1_) {
         super(p_i49799_1_);
      }

      protected String[] func_208214_a() {
         return field_208227_a;
      }

      protected String[] func_208218_b() {
         return field_208227_a;
      }
   }

   public static class Nether extends LegacyStructureDataUtil {
      private static final String[] field_208228_a = new String[]{"Fortress"};

      public Nether(@Nullable WorldSavedDataStorage p_i49801_1_) {
         super(p_i49801_1_);
      }

      protected String[] func_208214_a() {
         return field_208228_a;
      }

      protected String[] func_208218_b() {
         return field_208228_a;
      }
   }

   public static class Overworld extends LegacyStructureDataUtil {
      private static final String[] field_208225_a = new String[]{"Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"};
      private static final String[] field_208226_b = new String[]{"Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument"};

      public Overworld(@Nullable WorldSavedDataStorage p_i49800_1_) {
         super(p_i49800_1_);
      }

      protected String[] func_208214_a() {
         return field_208225_a;
      }

      protected String[] func_208218_b() {
         return field_208226_b;
      }
   }
}