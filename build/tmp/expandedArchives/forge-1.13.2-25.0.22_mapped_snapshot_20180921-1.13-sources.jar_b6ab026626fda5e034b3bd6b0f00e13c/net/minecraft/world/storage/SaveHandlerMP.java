package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SaveHandlerMP implements ISaveHandler {
   /**
    * Loads and returns the world info
    */
   public WorldInfo loadWorldInfo() {
      return null;
   }

   /**
    * Checks the session lock to prevent save collisions
    */
   public void checkSessionLock() throws SessionLockException {
   }

   /**
    * initializes and returns the chunk loader for the specified world provider
    */
   public IChunkLoader getChunkLoader(Dimension provider) {
      return null;
   }

   /**
    * Saves the given World Info with the given NBTTagCompound as the Player.
    */
   public void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound) {
   }

   /**
    * used to update level.dat from old format to MCRegion format
    */
   public void saveWorldInfo(WorldInfo worldInformation) {
   }

   public IPlayerFileData getPlayerNBTManager() {
      return null;
   }

   /**
    * Called to flush all changes to disk, waiting for them to complete.
    */
   public void flush() {
   }

   @Nullable
   public File func_212423_a(DimensionType p_212423_1_, String p_212423_2_) {
      return null;
   }

   /**
    * Gets the File object corresponding to the base directory of this world.
    */
   public File getWorldDirectory() {
      return null;
   }

   public TemplateManager getStructureTemplateManager() {
      return null;
   }

   public DataFixer getFixer() {
      return null;
   }
}