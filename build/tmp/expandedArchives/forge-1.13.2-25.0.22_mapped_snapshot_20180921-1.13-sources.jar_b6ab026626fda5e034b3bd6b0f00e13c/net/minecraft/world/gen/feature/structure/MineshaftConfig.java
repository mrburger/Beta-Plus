package net.minecraft.world.gen.feature.structure;

import net.minecraft.world.gen.feature.IFeatureConfig;

public class MineshaftConfig implements IFeatureConfig {
   public final double field_202439_a;
   public final MineshaftStructure.Type type;

   public MineshaftConfig(double p_i48676_1_, MineshaftStructure.Type p_i48676_3_) {
      this.field_202439_a = p_i48676_1_;
      this.type = p_i48676_3_;
   }
}