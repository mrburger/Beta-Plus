package net.minecraft.world.gen.feature;

public class RandomDefaultFeatureListConfig implements IFeatureConfig {
   public final Feature<?>[] field_202449_a;
   public final IFeatureConfig[] field_202450_b;
   public final float[] field_202451_c;
   public final Feature<?> field_202452_d;
   public final IFeatureConfig field_202453_f;

   public <FC extends IFeatureConfig> RandomDefaultFeatureListConfig(Feature<?>[] p_i48671_1_, IFeatureConfig[] p_i48671_2_, float[] p_i48671_3_, Feature<FC> p_i48671_4_, FC p_i48671_5_) {
      this.field_202449_a = p_i48671_1_;
      this.field_202450_b = p_i48671_2_;
      this.field_202451_c = p_i48671_3_;
      this.field_202452_d = p_i48671_4_;
      this.field_202453_f = p_i48671_5_;
   }
}