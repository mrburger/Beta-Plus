package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.area.AreaDimension;

public interface IDimTransformer {
   AreaDimension apply(AreaDimension dimensionIn);
}