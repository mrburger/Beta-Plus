package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.area.AreaDimension;

public interface IDimOffset0Transformer extends IDimTransformer {
   default AreaDimension apply(AreaDimension dimensionIn) {
      return dimensionIn;
   }
}