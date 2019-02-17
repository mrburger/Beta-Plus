package net.minecraft.world.gen.placement;

public class CountRangeConfig implements IPlacementConfig {
   public final int count;
   public final int minHeight;
   public final int maxHeightBase;
   public final int maxHeight;

   public CountRangeConfig(int countIn, int minHeightIn, int maxHeightBaseIn, int maxHeightIn) {
      this.count = countIn;
      this.minHeight = minHeightIn;
      this.maxHeightBase = maxHeightBaseIn;
      this.maxHeight = maxHeightIn;
   }
}