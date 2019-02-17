package net.minecraft.world.gen.placement;

import net.minecraft.world.gen.feature.IFeatureConfig;

public class CountConfig implements IFeatureConfig {
   public final int count;

   public CountConfig(int countIn) {
      this.count = countIn;
   }
}