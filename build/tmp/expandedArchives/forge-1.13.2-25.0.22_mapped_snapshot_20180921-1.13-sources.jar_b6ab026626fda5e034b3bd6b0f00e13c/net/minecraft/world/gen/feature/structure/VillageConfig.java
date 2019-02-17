package net.minecraft.world.gen.feature.structure;

import net.minecraft.world.gen.feature.IFeatureConfig;

public class VillageConfig implements IFeatureConfig {
   public final int field_202461_a;
   public final VillagePieces.Type type;

   public VillageConfig(int p_i48666_1_, VillagePieces.Type p_i48666_2_) {
      this.field_202461_a = p_i48666_1_;
      this.type = p_i48666_2_;
   }
}