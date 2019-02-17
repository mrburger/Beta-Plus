package net.minecraft.world.gen.placement;

public class AtSurfaceWithExtraConfig implements IPlacementConfig {
   public final int baseCount;
   public final float extraChance;
   public final int extraCount;

   public AtSurfaceWithExtraConfig(int baseCountIn, float extraChanceIn, int extraCountIn) {
      this.baseCount = baseCountIn;
      this.extraChance = extraChanceIn;
      this.extraCount = extraCountIn;
   }
}