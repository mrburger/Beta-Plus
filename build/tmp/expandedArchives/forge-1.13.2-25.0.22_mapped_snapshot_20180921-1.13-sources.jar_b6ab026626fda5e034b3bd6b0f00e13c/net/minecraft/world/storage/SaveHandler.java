package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveHandler implements ISaveHandler, IPlayerFileData {
   private static final Logger LOGGER = LogManager.getLogger();
   /** The directory in which to save world data. */
   private final File worldDirectory;
   /** The directory in which to save player data. */
   public final File playersDirectory;
   /** The time in milliseconds when this field was initialized. Stored in the session lock file. */
   private final long initializationTime = Util.milliTime();
   /** The directory name of the world */
   private final String saveDirectoryName;
   private final TemplateManager structureTemplateManager;
   protected final DataFixer dataFixer;

   public SaveHandler(File p_i49566_1_, String p_i49566_2_, @Nullable MinecraftServer p_i49566_3_, DataFixer p_i49566_4_) {
      this.dataFixer = p_i49566_4_;
      this.worldDirectory = new File(p_i49566_1_, p_i49566_2_);
      this.worldDirectory.mkdirs();
      this.playersDirectory = new File(this.worldDirectory, "playerdata");
      this.saveDirectoryName = p_i49566_2_;
      if (p_i49566_3_ != null) {
         this.playersDirectory.mkdirs();
         this.structureTemplateManager = new TemplateManager(p_i49566_3_, this.worldDirectory, p_i49566_4_);
      } else {
         this.structureTemplateManager = null;
      }

      this.setSessionLock();
   }

   /**
    * Creates a session lock file for this process
    */
   private void setSessionLock() {
      try {
         File file1 = new File(this.worldDirectory, "session.lock");
         DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(file1));

         try {
            dataoutputstream.writeLong(this.initializationTime);
         } finally {
            dataoutputstream.close();
         }

      } catch (IOException ioexception) {
         ioexception.printStackTrace();
         throw new RuntimeException("Failed to check session lock, aborting");
      }
   }

   /**
    * Gets the File object corresponding to the base directory of this world.
    */
   public File getWorldDirectory() {
      return this.worldDirectory;
   }

   /**
    * Checks the session lock to prevent save collisions
    */
   public void checkSessionLock() throws SessionLockException {
      try {
         File file1 = new File(this.worldDirectory, "session.lock");
         DataInputStream datainputstream = new DataInputStream(new FileInputStream(file1));

         try {
            if (datainputstream.readLong() != this.initializationTime) {
               throw new SessionLockException("The save is being accessed from another location, aborting");
            }
         } finally {
            datainputstream.close();
         }

      } catch (IOException var7) {
         throw new SessionLockException("Failed to check session lock, aborting");
      }
   }

   /**
    * initializes and returns the chunk loader for the specified world provider
    */
   public IChunkLoader getChunkLoader(Dimension provider) {
      throw new RuntimeException("Old Chunk Storage is no longer supported.");
   }

   /**
    * Loads and returns the world info
    */
   @Nullable
   public WorldInfo loadWorldInfo() {
      File file1 = new File(this.worldDirectory, "level.dat");
      if (file1.exists()) {
         WorldInfo worldinfo = SaveFormatOld.getWorldData(file1, this.dataFixer, this);
         if (worldinfo != null) {
            return worldinfo;
         }
      }

      file1 = new File(this.worldDirectory, "level.dat_old");
      return file1.exists() ? SaveFormatOld.getWorldData(file1, this.dataFixer, this) : null;
   }

   /**
    * Saves the given World Info with the given NBTTagCompound as the Player.
    */
   public void saveWorldInfoWithPlayer(WorldInfo worldInformation, @Nullable NBTTagCompound tagCompound) {
      NBTTagCompound nbttagcompound = worldInformation.cloneNBTCompound(tagCompound);
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      nbttagcompound1.setTag("Data", nbttagcompound);

      net.minecraftforge.fml.WorldPersistenceHooks.handleWorldDataSave(this, worldInformation, nbttagcompound1);

      try {
         File file1 = new File(this.worldDirectory, "level.dat_new");
         File file2 = new File(this.worldDirectory, "level.dat_old");
         File file3 = new File(this.worldDirectory, "level.dat");
         CompressedStreamTools.writeCompressed(nbttagcompound1, new FileOutputStream(file1));
         if (file2.exists()) {
            file2.delete();
         }

         file3.renameTo(file2);
         if (file3.exists()) {
            file3.delete();
         }

         file1.renameTo(file3);
         if (file1.exists()) {
            file1.delete();
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }

   }

   /**
    * used to update level.dat from old format to MCRegion format
    */
   public void saveWorldInfo(WorldInfo worldInformation) {
      this.saveWorldInfoWithPlayer(worldInformation, (NBTTagCompound)null);
   }

   /**
    * Writes the player data to disk from the specified PlayerEntityMP.
    */
   public void writePlayerData(EntityPlayer player) {
      try {
         NBTTagCompound nbttagcompound = player.writeWithoutTypeId(new NBTTagCompound());
         File file1 = new File(this.playersDirectory, player.getCachedUniqueIdString() + ".dat.tmp");
         File file2 = new File(this.playersDirectory, player.getCachedUniqueIdString() + ".dat");
         CompressedStreamTools.writeCompressed(nbttagcompound, new FileOutputStream(file1));
         if (file2.exists()) {
            file2.delete();
         }

         file1.renameTo(file2);
         net.minecraftforge.event.ForgeEventFactory.firePlayerSavingEvent(player, this.playersDirectory, player.getUniqueID().toString());
      } catch (Exception var5) {
         LOGGER.warn("Failed to save player data for {}", (Object)player.getName().getString());
      }

   }

   /**
    * Reads the player data from disk into the specified PlayerEntityMP.
    */
   @Nullable
   public NBTTagCompound readPlayerData(EntityPlayer player) {
      NBTTagCompound nbttagcompound = null;

      try {
         File file1 = new File(this.playersDirectory, player.getCachedUniqueIdString() + ".dat");
         if (file1.exists() && file1.isFile()) {
            nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file1));
         }
      } catch (Exception var4) {
         LOGGER.warn("Failed to load player data for {}", (Object)player.getName().getString());
      }

      if (nbttagcompound != null) {
         int i = nbttagcompound.contains("DataVersion", 3) ? nbttagcompound.getInt("DataVersion") : -1;
         player.read(NBTUtil.update(this.dataFixer, DataFixTypes.PLAYER, nbttagcompound, i));
      }
      net.minecraftforge.event.ForgeEventFactory.firePlayerLoadingEvent(player, playersDirectory, player.getUniqueID().toString());

      return nbttagcompound;
   }

   public IPlayerFileData getPlayerNBTManager() {
      return this;
   }

   /**
    * Returns an array of usernames for which player.dat exists for.
    */
   public String[] getAvailablePlayerDat() {
      String[] astring = this.playersDirectory.list();
      if (astring == null) {
         astring = new String[0];
      }

      for(int i = 0; i < astring.length; ++i) {
         if (astring[i].endsWith(".dat")) {
            astring[i] = astring[i].substring(0, astring[i].length() - 4);
         }
      }

      return astring;
   }

   /**
    * Called to flush all changes to disk, waiting for them to complete.
    */
   public void flush() {
   }

   public File func_212423_a(DimensionType p_212423_1_, String p_212423_2_) {
      File file1 = new File(p_212423_1_.func_212679_a(this.worldDirectory), "data");
      file1.mkdirs();
      return new File(file1, p_212423_2_ + ".dat");
   }

   public TemplateManager getStructureTemplateManager() {
      return this.structureTemplateManager;
   }

   public DataFixer getFixer() {
      return this.dataFixer;
   }

   public NBTTagCompound getPlayerNBT(net.minecraft.entity.player.EntityPlayerMP player) {
      try {
         File file1 = new File(this.playersDirectory, player.getCachedUniqueIdString() + ".dat");
         if (file1.exists() && file1.isFile()) {
            NBTTagCompound nbt = CompressedStreamTools.readCompressed(new FileInputStream(file1));
            if (nbt != null) {
               nbt = NBTUtil.update(this.dataFixer, DataFixTypes.PLAYER, nbt, nbt.contains("DataVersion", 3) ? nbt.getInt("DataVersion") : -1);
            }
            return nbt;
         }
      } catch (Exception exception) {
         LOGGER.warn("Failed to load player data for " + player.getName());
      }
      return null;
   }
}