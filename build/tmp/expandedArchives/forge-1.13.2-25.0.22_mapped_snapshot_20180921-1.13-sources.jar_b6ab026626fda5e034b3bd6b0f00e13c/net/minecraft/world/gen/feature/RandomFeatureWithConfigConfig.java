package net.minecraft.world.gen.feature;

public class RandomFeatureWithConfigConfig implements IFeatureConfig {
   public final Feature<?>[] features;
   public final IFeatureConfig[] configs;

   public RandomFeatureWithConfigConfig(Feature<?>[] featuresIn, IFeatureConfig[] configsIn) {
      this.features = featuresIn;
      this.configs = configsIn;
   }
}