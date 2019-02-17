package net.minecraft.world.gen.placement;

public class HeightWithChanceConfig implements IPlacementConfig {
   public final int height;
   public final float chance;

   public HeightWithChanceConfig(int heightIn, float chanceIn) {
      this.height = heightIn;
      this.chance = chanceIn;
   }
}