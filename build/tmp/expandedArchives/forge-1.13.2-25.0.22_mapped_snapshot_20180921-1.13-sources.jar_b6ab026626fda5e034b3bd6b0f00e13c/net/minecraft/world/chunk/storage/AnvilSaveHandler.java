package net.minecraft.world.chunk.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraft.world.storage.WorldInfo;

public class AnvilSaveHandler extends SaveHandler {
   public AnvilSaveHandler(File p_i49568_1_, String p_i49568_2_, @Nullable MinecraftServer p_i49568_3_, DataFixer p_i49568_4_) {
      super(p_i49568_1_, p_i49568_2_, p_i49568_3_, p_i49568_4_);
   }

   /**
    * initializes and returns the chunk loader for the specified world provider
    */
   public IChunkLoader getChunkLoader(Dimension provider) {
      File file1 = provider.getType().func_212679_a(this.getWorldDirectory());
      file1.mkdirs();
      return new AnvilChunkLoader(file1, this.dataFixer);
   }

   /**
    * Saves the given World Info with the given NBTTagCompound as the Player.
    */
   public void saveWorldInfoWithPlayer(WorldInfo worldInformation, @Nullable NBTTagCompound tagCompound) {
      worldInformation.setSaveVersion(19133);
      super.saveWorldInfoWithPlayer(worldInformation, tagCompound);
   }

   /**
    * Called to flush all changes to disk, waiting for them to complete.
    */
   public void flush() {
      try {
         ThreadedFileIOBase.getThreadedIOInstance().waitForFinish();
      } catch (InterruptedException interruptedexception) {
         interruptedexception.printStackTrace();
      }

      RegionFileCache.clearRegionFileReferences();
   }
}