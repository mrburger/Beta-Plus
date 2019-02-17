package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.template.TemplateManager;

public interface ISaveHandler {
   /**
    * Loads and returns the world info
    */
   @Nullable
   WorldInfo loadWorldInfo();

   /**
    * Checks the session lock to prevent save collisions
    */
   void checkSessionLock() throws SessionLockException;

   /**
    * initializes and returns the chunk loader for the specified world provider
    */
   IChunkLoader getChunkLoader(Dimension provider);

   /**
    * Saves the given World Info with the given NBTTagCompound as the Player.
    */
   void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound);

   /**
    * used to update level.dat from old format to MCRegion format
    */
   void saveWorldInfo(WorldInfo worldInformation);

   IPlayerFileData getPlayerNBTManager();

   /**
    * Called to flush all changes to disk, waiting for them to complete.
    */
   void flush();

   /**
    * Gets the File object corresponding to the base directory of this world.
    */
   File getWorldDirectory();

   @Nullable
   File func_212423_a(DimensionType p_212423_1_, String p_212423_2_);

   TemplateManager getStructureTemplateManager();

   DataFixer getFixer();
}