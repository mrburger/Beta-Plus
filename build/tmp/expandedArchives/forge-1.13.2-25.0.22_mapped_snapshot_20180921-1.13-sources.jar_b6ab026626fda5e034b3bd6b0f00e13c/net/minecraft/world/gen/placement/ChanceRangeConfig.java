package net.minecraft.world.gen.placement;

public class ChanceRangeConfig implements IPlacementConfig {
   public final float chance;
   public final int field_202489_b;
   public final int minHeight;
   public final int maxHeight;

   public ChanceRangeConfig(float chanceIn, int minHeightIn, int maxHeightBase, int maxHeightIn) {
      this.chance = chanceIn;
      this.minHeight = minHeightIn;
      this.field_202489_b = maxHeightBase;
      this.maxHeight = maxHeightIn;
   }
}