package net.minecraft.world.gen.placement;

public class DepthAverageConfig implements IPlacementConfig {
   public final int count;
   public final int averageHeight;
   public final int heightSpread;

   public DepthAverageConfig(int countIn, int averageHeightIn, int heightSpreadIn) {
      this.count = countIn;
      this.averageHeight = averageHeightIn;
      this.heightSpread = heightSpreadIn;
   }
}