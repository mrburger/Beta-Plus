package net.minecraft.world.gen.placement;

public class TopSolidRangeConfig implements IPlacementConfig {
   public final int minCount;
   public final int maxCount;

   public TopSolidRangeConfig(int min, int max) {
       this.minCount = min;
       this.maxCount = max;
}
}