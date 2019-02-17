package net.minecraft.stats;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SPacketStatistics;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatisticsManagerServer extends StatisticsManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final MinecraftServer server;
   private final File statsFile;
   private final Set<Stat<?>> dirty = Sets.newHashSet();
   private int lastStatRequest = -300;

   public StatisticsManagerServer(MinecraftServer serverIn, File statsFileIn) {
      this.server = serverIn;
      this.statsFile = statsFileIn;
      if (statsFileIn.isFile()) {
         try {
            this.parseLocal(serverIn.getDataFixer(), FileUtils.readFileToString(statsFileIn));
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't read statistics file {}", statsFileIn, ioexception);
         } catch (JsonParseException jsonparseexception) {
            LOGGER.error("Couldn't parse statistics file {}", statsFileIn, jsonparseexception);
         }
      }

   }

   public void saveStatFile() {
      try {
         FileUtils.writeStringToFile(this.statsFile, this.func_199061_b());
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't save stats", (Throwable)ioexception);
      }

   }

   /**
    * Triggers the logging of an achievement and attempts to announce to server
    */
   public void setValue(EntityPlayer playerIn, Stat<?> statIn, int p_150873_3_) {
      super.setValue(playerIn, statIn, p_150873_3_);
      this.dirty.add(statIn);
   }

   private Set<Stat<?>> getDirty() {
      Set<Stat<?>> set = Sets.newHashSet(this.dirty);
      this.dirty.clear();
      return set;
   }

   public void parseLocal(DataFixer p_199062_1_, String p_199062_2_) {
      try (JsonReader jsonreader = new JsonReader(new StringReader(p_199062_2_))) {
         jsonreader.setLenient(false);
         JsonElement jsonelement = Streams.parse(jsonreader);
         if (!jsonelement.isJsonNull()) {
            NBTTagCompound nbttagcompound = func_199065_a(jsonelement.getAsJsonObject());
            if (!nbttagcompound.contains("DataVersion", 99)) {
               nbttagcompound.setInt("DataVersion", 1343);
            }

            nbttagcompound = NBTUtil.update(p_199062_1_, DataFixTypes.STATS, nbttagcompound, nbttagcompound.getInt("DataVersion"));
            if (nbttagcompound.contains("stats", 10)) {
               NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("stats");

               for(String s : nbttagcompound1.keySet()) {
                  if (nbttagcompound1.contains(s, 10)) {
                     StatType<?> stattype = IRegistry.field_212634_w.func_212608_b(new ResourceLocation(s));
                     if (stattype == null) {
                        LOGGER.warn("Invalid statistic type in {}: Don't know what {} is", this.statsFile, s);
                     } else {
                        NBTTagCompound nbttagcompound2 = nbttagcompound1.getCompound(s);

                        for(String s1 : nbttagcompound2.keySet()) {
                           if (nbttagcompound2.contains(s1, 99)) {
                              Stat<?> stat = this.func_199063_a(stattype, s1);
                              if (stat == null) {
                                 LOGGER.warn("Invalid statistic in {}: Don't know what {} is", this.statsFile, s1);
                              } else {
                                 this.statsData.put(stat, nbttagcompound2.getInt(s1));
                              }
                           } else {
                              LOGGER.warn("Invalid statistic value in {}: Don't know what {} is for key {}", this.statsFile, nbttagcompound2.getTag(s1), s1);
                           }
                        }
                     }
                  }
               }
            }
         } else {
            LOGGER.error("Unable to parse Stat data from {}", (Object)this.statsFile);
         }
      } catch (IOException | JsonParseException jsonparseexception) {
         LOGGER.error("Unable to parse Stat data from {}", this.statsFile, jsonparseexception);
      }

   }

   @Nullable
   private <T> Stat<T> func_199063_a(StatType<T> p_199063_1_, String p_199063_2_) {
      ResourceLocation resourcelocation = ResourceLocation.makeResourceLocation(p_199063_2_);
      if (resourcelocation == null) {
         return null;
      } else {
         T t = p_199063_1_.getRegistry().func_212608_b(resourcelocation);
         return t == null ? null : p_199063_1_.get(t);
      }
   }

   private static NBTTagCompound func_199065_a(JsonObject p_199065_0_) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();

      for(Entry<String, JsonElement> entry : p_199065_0_.entrySet()) {
         JsonElement jsonelement = entry.getValue();
         if (jsonelement.isJsonObject()) {
            nbttagcompound.setTag(entry.getKey(), func_199065_a(jsonelement.getAsJsonObject()));
         } else if (jsonelement.isJsonPrimitive()) {
            JsonPrimitive jsonprimitive = jsonelement.getAsJsonPrimitive();
            if (jsonprimitive.isNumber()) {
               nbttagcompound.setInt(entry.getKey(), jsonprimitive.getAsInt());
            }
         }
      }

      return nbttagcompound;
   }

   protected String func_199061_b() {
      Map<StatType<?>, JsonObject> map = Maps.newHashMap();

      for(it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Stat<?>> entry : this.statsData.object2IntEntrySet()) {
         Stat<?> stat = entry.getKey();
         map.computeIfAbsent(stat.getType(), (p_199064_0_) -> {
            return new JsonObject();
         }).addProperty(func_199066_b(stat).toString(), entry.getIntValue());
      }

      JsonObject jsonobject = new JsonObject();

      for(Entry<StatType<?>, JsonObject> entry1 : map.entrySet()) {
         jsonobject.add(IRegistry.field_212634_w.getKey(entry1.getKey()).toString(), entry1.getValue());
      }

      JsonObject jsonobject1 = new JsonObject();
      jsonobject1.add("stats", jsonobject);
      jsonobject1.addProperty("DataVersion", 1631);
      return jsonobject1.toString();
   }

   private static <T> ResourceLocation func_199066_b(Stat<T> p_199066_0_) {
      return p_199066_0_.getType().getRegistry().getKey(p_199066_0_.getValue());
   }

   public void markAllDirty() {
      this.dirty.addAll(this.statsData.keySet());
   }

   public void sendStats(EntityPlayerMP player) {
      int i = this.server.getTickCounter();
      Object2IntMap<Stat<?>> object2intmap = new Object2IntOpenHashMap<>();
      if (i - this.lastStatRequest > 300) {
         this.lastStatRequest = i;

         for(Stat<?> stat : this.getDirty()) {
            object2intmap.put(stat, this.getValue(stat));
         }
      }

      player.connection.sendPacket(new SPacketStatistics(object2intmap));
   }
}