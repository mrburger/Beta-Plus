package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.area.AreaDimension;

public interface IDimOffset1Transformer extends IDimTransformer {
   default AreaDimension apply(AreaDimension dimensionIn) {
      return new AreaDimension(dimensionIn.getStartX() - 1, dimensionIn.getStartZ() - 1, dimensionIn.getXSize() + 2, dimensionIn.getZSize() + 2);
   }
}