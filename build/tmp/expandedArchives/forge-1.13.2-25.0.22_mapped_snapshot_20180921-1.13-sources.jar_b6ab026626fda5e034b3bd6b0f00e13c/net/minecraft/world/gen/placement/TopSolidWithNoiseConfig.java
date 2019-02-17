package net.minecraft.world.gen.placement;

public class TopSolidWithNoiseConfig implements IPlacementConfig {
   public final int maxCount;
   public final double noiseStretch;

   public TopSolidWithNoiseConfig(int maxCountIn, double noiseStretchIn) {
      this.maxCount = maxCountIn;
      this.noiseStretch = noiseStretchIn;
   }
}