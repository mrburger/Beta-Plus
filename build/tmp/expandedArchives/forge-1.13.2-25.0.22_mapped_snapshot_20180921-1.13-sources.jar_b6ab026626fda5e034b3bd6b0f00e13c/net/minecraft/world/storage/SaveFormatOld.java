package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveFormatOld implements ISaveFormat {
   private static final Logger LOGGER = LogManager.getLogger();
   /** Reference to the File object representing the directory for the world saves */
   public final Path savesDirectory;
   protected final Path field_197717_b;
   protected final DataFixer dataFixer;

   public SaveFormatOld(Path p_i49565_1_, Path p_i49565_2_, DataFixer p_i49565_3_) {
      this.dataFixer = p_i49565_3_;

      try {
         Files.createDirectories(Files.exists(p_i49565_1_) ? p_i49565_1_.toRealPath() : p_i49565_1_);
      } catch (IOException ioexception) {
         throw new RuntimeException(ioexception);
      }

      this.savesDirectory = p_i49565_1_;
      this.field_197717_b = p_i49565_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public String getName() {
      return "Old Format";
   }

   @OnlyIn(Dist.CLIENT)
   public List<WorldSummary> getSaveList() throws AnvilConverterException {
      List<WorldSummary> list = Lists.newArrayList();

      for(int i = 0; i < 5; ++i) {
         String s = "World" + (i + 1);
         WorldInfo worldinfo = this.getWorldInfo(s);
         if (worldinfo != null) {
            list.add(new WorldSummary(worldinfo, s, "", worldinfo.getSizeOnDisk(), false));
         }
      }

      return list;
   }

   @OnlyIn(Dist.CLIENT)
   public void flushCache() {
   }

   /**
    * Returns the world's WorldInfo object
    */
   @Nullable
   public WorldInfo getWorldInfo(String saveName) {
      File file1 = new File(this.savesDirectory.toFile(), saveName);
      if (!file1.exists()) {
         return null;
      } else {
         File file2 = new File(file1, "level.dat");
         if (file2.exists()) {
            WorldInfo worldinfo = getWorldData(file2, this.dataFixer);
            if (worldinfo != null) {
               return worldinfo;
            }
         }

         file2 = new File(file1, "level.dat_old");
         return file2.exists() ? getWorldData(file2, this.dataFixer) : null;
      }
   }

   @Nullable
   public static WorldInfo getWorldData(File p_186353_0_, DataFixer dataFixerIn) {
       return getWorldData(p_186353_0_, dataFixerIn, null);
   }

   @Nullable
   public static WorldInfo getWorldData(File p_186353_0_, DataFixer dataFixerIn, @Nullable SaveHandler saveHandler) {
      try {
         NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(p_186353_0_));
         NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Data");
         NBTTagCompound nbttagcompound2 = nbttagcompound1.contains("Player", 10) ? nbttagcompound1.getCompound("Player") : null;
         nbttagcompound1.removeTag("Player");
         int i = nbttagcompound1.contains("DataVersion", 99) ? nbttagcompound1.getInt("DataVersion") : -1;
         WorldInfo ret = new WorldInfo(NBTUtil.update(dataFixerIn, DataFixTypes.LEVEL, nbttagcompound1, i), dataFixerIn, i, nbttagcompound2);
         if (saveHandler != null)
            net.minecraftforge.fml.WorldPersistenceHooks.handleWorldDataLoad(saveHandler, ret, nbttagcompound);
         return ret;
      } catch (net.minecraftforge.fml.StartupQuery.AbortedException e) {
         throw e;
      } catch (Exception exception) {
         LOGGER.error("Exception reading {}", p_186353_0_, exception);
         return null;
      }
   }

   /**
    * Renames the world by storing the new name in level.dat. It does *not* rename the directory containing the world
    * data.
    */
   @OnlyIn(Dist.CLIENT)
   public void renameWorld(String dirName, String newName) {
      File file1 = new File(this.savesDirectory.toFile(), dirName);
      if (file1.exists()) {
         File file2 = new File(file1, "level.dat");
         if (file2.exists()) {
            try {
               NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file2));
               NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Data");
               nbttagcompound1.setString("LevelName", newName);
               CompressedStreamTools.writeCompressed(nbttagcompound, new FileOutputStream(file2));
            } catch (Exception exception) {
               exception.printStackTrace();
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isNewLevelIdAcceptable(String saveName) {
      File file1 = new File(this.savesDirectory.toFile(), saveName);
      if (file1.exists()) {
         return false;
      } else {
         try {
            file1.mkdir();
            file1.delete();
            return true;
         } catch (Throwable throwable) {
            LOGGER.warn("Couldn't make new level", throwable);
            return false;
         }
      }
   }

   /**
    * Deletes a world directory.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean deleteWorldDirectory(String saveName) {
      File file1 = new File(this.savesDirectory.toFile(), saveName);
      if (!file1.exists()) {
         return true;
      } else {
         LOGGER.info("Deleting level {}", (Object)saveName);

         for(int i = 1; i <= 5; ++i) {
            LOGGER.info("Attempt {}...", (int)i);
            if (deleteFiles(file1.listFiles())) {
               break;
            }

            LOGGER.warn("Unsuccessful in deleting contents.");
            if (i < 5) {
               try {
                  Thread.sleep(500L);
               } catch (InterruptedException var5) {
                  ;
               }
            }
         }

         return file1.delete();
      }
   }

   /**
    * Deletes a list of files and directories.
    */
   @OnlyIn(Dist.CLIENT)
   protected static boolean deleteFiles(File[] files) {
      for(File file1 : files) {
         LOGGER.debug("Deleting {}", (Object)file1);
         if (file1.isDirectory() && !deleteFiles(file1.listFiles())) {
            LOGGER.warn("Couldn't delete directory {}", (Object)file1);
            return false;
         }

         if (!file1.delete()) {
            LOGGER.warn("Couldn't delete file {}", (Object)file1);
            return false;
         }
      }

      return true;
   }

   public ISaveHandler getSaveLoader(String saveName, @Nullable MinecraftServer server) {
      return new SaveHandler(this.savesDirectory.toFile(), saveName, server, this.dataFixer);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isConvertible(String saveName) {
      return false;
   }

   /**
    * gets if the map is old chunk saving (true) or McRegion (false)
    */
   public boolean isOldMapFormat(String saveName) {
      return false;
   }

   /**
    * converts the map to mcRegion
    */
   public boolean convertMapFormat(String filename, IProgressUpdate progressCallback) {
      return false;
   }

   /**
    * Return whether the given world can be loaded.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean canLoadWorld(String saveName) {
      return Files.isDirectory(this.savesDirectory.resolve(saveName));
   }

   /**
    * Gets a file within the given world.
    *  
    * @param saveName Name of the world
    * @param filePath Path to the file, relative to the world's folder
    */
   public File getFile(String saveName, String filePath) {
      return this.savesDirectory.resolve(saveName).resolve(filePath).toFile();
   }

   /**
    * Gets the folder for the given world.
    */
   @OnlyIn(Dist.CLIENT)
   public Path getWorldFolder(String saveName) {
      return this.savesDirectory.resolve(saveName);
   }

   /**
    * Gets the folder where backups are stored
    */
   @OnlyIn(Dist.CLIENT)
   public Path getBackupsFolder() {
      return this.field_197717_b;
   }
}