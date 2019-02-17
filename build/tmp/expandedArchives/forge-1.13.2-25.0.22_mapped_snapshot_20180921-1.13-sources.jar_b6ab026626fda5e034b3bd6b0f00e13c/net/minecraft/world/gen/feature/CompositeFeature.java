package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.placement.BasePlacement;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class CompositeFeature<F extends IFeatureConfig, D extends IPlacementConfig> extends Feature<NoFeatureConfig> {
   protected final Feature<F> feature;
   protected final F featureConfig;
   protected final BasePlacement<D> basePlacement;
   protected final D placementConfig;

   public CompositeFeature(Feature<F> featureIn, F featureConfigIn, BasePlacement<D> basePlacementIn, D placementConfigIn) {
      this.featureConfig = featureConfigIn;
      this.placementConfig = placementConfigIn;
      this.basePlacement = basePlacementIn;
      this.feature = featureIn;
   }

   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      return this.basePlacement.generate(p_212245_1_, p_212245_2_, p_212245_3_, p_212245_4_, this.placementConfig, this.feature, this.featureConfig);
   }

   public String toString() {
      return String.format("< %s [%s | %s] >", this.getClass().getSimpleName(), this.basePlacement, this.feature);
   }

   public Feature<F> getFeature() {
      return this.feature;
   }
}