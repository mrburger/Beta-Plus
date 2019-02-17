package net.minecraft.world.gen.feature.structure;

import net.minecraft.world.gen.feature.IFeatureConfig;

public class BuriedTreasureConfig implements IFeatureConfig {
   public final float chance;

   public BuriedTreasureConfig(float chance) {
      this.chance = chance;
   }
}