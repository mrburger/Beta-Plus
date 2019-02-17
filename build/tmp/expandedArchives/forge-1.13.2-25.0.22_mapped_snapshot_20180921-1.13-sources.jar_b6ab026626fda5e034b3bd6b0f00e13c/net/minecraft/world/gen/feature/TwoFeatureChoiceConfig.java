package net.minecraft.world.gen.feature;

public class TwoFeatureChoiceConfig implements IFeatureConfig {
   public final Feature<?> field_202445_a;
   public final IFeatureConfig field_202446_b;
   public final Feature<?> field_202447_c;
   public final IFeatureConfig field_202448_d;

   public <FC extends IFeatureConfig> TwoFeatureChoiceConfig(Feature<?> p_i48672_1_, IFeatureConfig p_i48672_2_, Feature<?> p_i48672_3_, IFeatureConfig p_i48672_4_) {
      this.field_202445_a = p_i48672_1_;
      this.field_202446_b = p_i48672_2_;
      this.field_202447_c = p_i48672_3_;
      this.field_202448_d = p_i48672_4_;
   }
}