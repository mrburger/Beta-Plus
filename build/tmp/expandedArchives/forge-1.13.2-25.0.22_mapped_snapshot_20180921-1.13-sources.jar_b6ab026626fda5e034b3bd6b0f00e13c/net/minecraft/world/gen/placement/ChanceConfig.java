package net.minecraft.world.gen.placement;

public class ChanceConfig implements IPlacementConfig {
   public final int chance;

   public ChanceConfig(int chanceIn) {
      this.chance = chanceIn;
   }
}