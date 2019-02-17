package net.minecraft.world.gen.placement;

public class NoiseDependant implements IPlacementConfig {
   public final double noiseThreshold;
   public final int lowNoiseCount;
   public final int highNoiseCount;

   public NoiseDependant(double noiseThresholdIn, int lowNoiseCountIn, int highNoiseCountIn) {
      this.noiseThreshold = noiseThresholdIn;
      this.lowNoiseCount = lowNoiseCountIn;
      this.highNoiseCount = highNoiseCountIn;
   }
}