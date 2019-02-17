package net.minecraft.world.biome.provider;

public class EndBiomeProviderSettings implements IBiomeProviderSettings {
   private long seed;

   public EndBiomeProviderSettings setSeed(long seed) {
      this.seed = seed;
      return this;
   }

   public long getSeed() {
      return this.seed;
   }
}