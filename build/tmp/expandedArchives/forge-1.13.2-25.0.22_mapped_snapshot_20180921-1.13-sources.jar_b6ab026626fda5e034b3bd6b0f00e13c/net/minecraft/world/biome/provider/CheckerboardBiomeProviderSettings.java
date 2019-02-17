package net.minecraft.world.biome.provider;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;

public class CheckerboardBiomeProviderSettings implements IBiomeProviderSettings {
   private Biome[] field_205434_a = new Biome[]{Biomes.PLAINS};
   private int size = 1;

   public CheckerboardBiomeProviderSettings func_206860_a(Biome[] p_206860_1_) {
      this.field_205434_a = p_206860_1_;
      return this;
   }

   public CheckerboardBiomeProviderSettings setSize(int p_206861_1_) {
      this.size = p_206861_1_;
      return this;
   }

   public Biome[] func_205432_a() {
      return this.field_205434_a;
   }

   public int getSize() {
      return this.size;
   }
}