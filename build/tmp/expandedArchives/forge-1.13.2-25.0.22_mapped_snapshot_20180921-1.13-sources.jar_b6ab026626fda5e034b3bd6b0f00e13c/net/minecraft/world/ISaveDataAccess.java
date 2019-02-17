package net.minecraft.world;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.storage.WorldSavedDataStorage;

public interface ISaveDataAccess {
   @Nullable
   WorldSavedDataStorage getMapStorage();

   @Nullable
   default <T extends WorldSavedData> T func_212411_a(DimensionType p_212411_1_, Function<String, T> p_212411_2_, String p_212411_3_) {
      WorldSavedDataStorage worldsaveddatastorage = this.getMapStorage();
      return (T)(worldsaveddatastorage == null ? null : worldsaveddatastorage.func_212426_a(p_212411_1_, p_212411_2_, p_212411_3_));
   }

   default void func_212409_a(DimensionType p_212409_1_, String p_212409_2_, WorldSavedData p_212409_3_) {
      WorldSavedDataStorage worldsaveddatastorage = this.getMapStorage();
      if (worldsaveddatastorage != null) {
         worldsaveddatastorage.func_212424_a(p_212409_1_, p_212409_2_, p_212409_3_);
      }

   }

   default int func_212410_a(DimensionType p_212410_1_, String p_212410_2_) {
      return this.getMapStorage().func_212425_a(p_212410_1_, p_212410_2_);
   }
}