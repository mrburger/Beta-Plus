package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DimensionSavedDataManager {
   private static final Logger field_212776_a = LogManager.getLogger();
   private final DimensionType field_212777_b;
   private Map<String, WorldSavedData> field_212778_c = Maps.newHashMap();
   private final Object2IntMap<String> field_212779_d = new Object2IntOpenHashMap<>();
   @Nullable
   private final ISaveHandler field_212780_e;

   public DimensionSavedDataManager(DimensionType p_i49854_1_, @Nullable ISaveHandler p_i49854_2_) {
      this.field_212777_b = p_i49854_1_;
      this.field_212780_e = p_i49854_2_;
      this.field_212779_d.defaultReturnValue(-1);
   }

   @Nullable
   public <T extends WorldSavedData> T getOrLoadData(Function<String, T> factory, String name) {
      WorldSavedData worldsaveddata = this.field_212778_c.get(name);
      if (worldsaveddata == null && this.field_212780_e != null) {
         try {
            File file1 = this.field_212780_e.func_212423_a(this.field_212777_b, name);
            if (file1 != null && file1.exists()) {
               worldsaveddata = (WorldSavedData)factory.apply(name);
               worldsaveddata.read(func_212774_a(this.field_212780_e, this.field_212777_b, name, 1631).getCompound("data"));
               this.field_212778_c.put(name, worldsaveddata);
            }
         } catch (Exception exception) {
            field_212776_a.error("Error loading saved data: {}", name, exception);
         }
      }

      return (T)worldsaveddata;
   }

   /**
    * Assigns the given String id to the given MapDataBase, removing any existing ones of the same id.
    */
   public void setData(String dataIdentifier, WorldSavedData data) {
      this.field_212778_c.put(dataIdentifier, data);
   }

   /**
    * Loads the idCounts Map from the 'idcounts' file.
    */
   public void loadIdCounts() {
      try {
         this.field_212779_d.clear();
         if (this.field_212780_e == null) {
            return;
         }

         File file1 = this.field_212780_e.func_212423_a(this.field_212777_b, "idcounts");
         if (file1 != null && file1.exists()) {
            DataInputStream datainputstream = new DataInputStream(new FileInputStream(file1));
            NBTTagCompound nbttagcompound = CompressedStreamTools.read(datainputstream);
            datainputstream.close();

            for(String s : nbttagcompound.keySet()) {
               if (nbttagcompound.contains(s, 99)) {
                  this.field_212779_d.put(s, nbttagcompound.getInt(s));
               }
            }
         }
      } catch (Exception exception) {
         field_212776_a.error("Could not load aux values", (Throwable)exception);
      }

   }

   /**
    * Returns an unique new data id for the given prefix and saves the idCounts map to the 'idcounts' file.
    */
   public int getUniqueDataId(String key) {
      int i = this.field_212779_d.getInt(key) + 1;
      this.field_212779_d.put(key, i);
      if (this.field_212780_e == null) {
         return i;
      } else {
         try {
            File file1 = this.field_212780_e.func_212423_a(this.field_212777_b, "idcounts");
            if (file1 != null) {
               NBTTagCompound nbttagcompound = new NBTTagCompound();

               for(Entry<String> entry : this.field_212779_d.object2IntEntrySet()) {
                  nbttagcompound.setInt(entry.getKey(), entry.getIntValue());
               }

               DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(file1));
               CompressedStreamTools.write(nbttagcompound, dataoutputstream);
               dataoutputstream.close();
            }
         } catch (Exception exception) {
            field_212776_a.error("Could not get free aux value {}", key, exception);
         }

         return i;
      }
   }

   public static NBTTagCompound func_212774_a(ISaveHandler p_212774_0_, DimensionType p_212774_1_, String p_212774_2_, int p_212774_3_) throws IOException {
      File file1 = p_212774_0_.func_212423_a(p_212774_1_, p_212774_2_);

      NBTTagCompound nbttagcompound1;
      try (FileInputStream fileinputstream = new FileInputStream(file1)) {
         NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(fileinputstream);
         int i = nbttagcompound.contains("DataVersion", 99) ? nbttagcompound.getInt("DataVersion") : 1343;
         nbttagcompound1 = NBTUtil.update(p_212774_0_.getFixer(), DataFixTypes.SAVED_DATA, nbttagcompound, i, p_212774_3_);
      }

      return nbttagcompound1;
   }

   public void func_212775_b() {
      if (this.field_212780_e != null) {
         for(WorldSavedData worldsaveddata : this.field_212778_c.values()) {
            if (worldsaveddata.isDirty()) {
               this.saveData(worldsaveddata);
               worldsaveddata.setDirty(false);
            }
         }

      }
   }

   /**
    * Saves the given MapDataBase to disk.
    */
   private void saveData(WorldSavedData data) {
      if (this.field_212780_e != null) {
         try {
            File file1 = this.field_212780_e.func_212423_a(this.field_212777_b, data.getName());
            if (file1 != null) {
               NBTTagCompound nbttagcompound = new NBTTagCompound();
               nbttagcompound.setTag("data", data.write(new NBTTagCompound()));
               nbttagcompound.setInt("DataVersion", 1631);
               FileOutputStream fileoutputstream = new FileOutputStream(file1);
               CompressedStreamTools.writeCompressed(nbttagcompound, fileoutputstream);
               fileoutputstream.close();
            }
         } catch (Exception exception) {
            field_212776_a.error("Could not save data {}", data, exception);
         }

      }
   }
}