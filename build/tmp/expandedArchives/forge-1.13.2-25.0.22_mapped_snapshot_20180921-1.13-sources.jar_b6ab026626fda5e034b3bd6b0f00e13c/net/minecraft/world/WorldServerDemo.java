package net.minecraft.world;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataStorage;

public class WorldServerDemo extends WorldServer {
   private static final long DEMO_WORLD_SEED = (long)"North Carolina".hashCode();
   public static final WorldSettings DEMO_WORLD_SETTINGS = (new WorldSettings(DEMO_WORLD_SEED, GameType.SURVIVAL, true, false, WorldType.DEFAULT)).enableBonusChest();

   public WorldServerDemo(MinecraftServer p_i49821_1_, ISaveHandler p_i49821_2_, WorldSavedDataStorage p_i49821_3_, WorldInfo p_i49821_4_, DimensionType p_i49821_5_, Profiler p_i49821_6_) {
      super(p_i49821_1_, p_i49821_2_, p_i49821_3_, p_i49821_4_, p_i49821_5_, p_i49821_6_);
      this.worldInfo.populateFromWorldSettings(DEMO_WORLD_SETTINGS);
   }
}