package net.minecraft.world.biome.provider;

import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.storage.WorldInfo;

public class OverworldBiomeProviderSettings implements IBiomeProviderSettings {
   private WorldInfo worldInfo;
   private OverworldGenSettings generatorSettings;

   public OverworldBiomeProviderSettings setWorldInfo(WorldInfo info) {
      this.worldInfo = info;
      return this;
   }

   public OverworldBiomeProviderSettings setGeneratorSettings(OverworldGenSettings p_205441_1_) {
      this.generatorSettings = p_205441_1_;
      return this;
   }

   public WorldInfo getWorldInfo() {
      return this.worldInfo;
   }

   public OverworldGenSettings getGeneratorSettings() {
      return this.generatorSettings;
   }
}