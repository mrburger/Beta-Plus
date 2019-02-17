package net.minecraft.world.gen.feature;

public class RandomFeatureListConfig implements IFeatureConfig {
   public final Feature<?>[] field_202454_a;
   public final IFeatureConfig[] field_202455_b;
   public final int field_202456_c;

   public RandomFeatureListConfig(Feature<?>[] p_i48670_1_, IFeatureConfig[] p_i48670_2_, int p_i48670_3_) {
      this.field_202454_a = p_i48670_1_;
      this.field_202455_b = p_i48670_2_;
      this.field_202456_c = p_i48670_3_;
   }
}