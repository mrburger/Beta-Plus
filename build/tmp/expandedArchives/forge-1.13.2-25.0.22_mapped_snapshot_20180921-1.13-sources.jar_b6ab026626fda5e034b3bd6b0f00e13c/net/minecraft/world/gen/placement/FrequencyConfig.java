package net.minecraft.world.gen.placement;

public class FrequencyConfig implements IPlacementConfig {
   public final int frequency;

   public FrequencyConfig(int frequencyIn) {
      this.frequency = frequencyIn;
   }
}