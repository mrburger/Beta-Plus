package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.NoiseGeneratorImproved;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

public enum OceanLayer implements IAreaTransformer0 {
   INSTANCE;

   public int apply(IContext context, AreaDimension areaDimensionIn, int x, int z) {
      NoiseGeneratorImproved noisegeneratorimproved = context.getNoiseGenerator();
      double d0 = noisegeneratorimproved.func_205562_a((double)(x + areaDimensionIn.getStartX()) / 8.0D, (double)(z + areaDimensionIn.getStartZ()) / 8.0D);
      if (d0 > 0.4D) {
         return LayerUtil.WARM_OCEAN;
      } else if (d0 > 0.2D) {
         return LayerUtil.LUKEWARM_OCEAN;
      } else if (d0 < -0.4D) {
         return LayerUtil.FROZEN_OCEAN;
      } else {
         return d0 < -0.2D ? LayerUtil.COLD_OCEAN : LayerUtil.OCEAN;
      }
   }
}