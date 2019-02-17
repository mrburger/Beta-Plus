package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.IContextExtended;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;

public interface IC0Transformer extends IAreaTransformer1, IDimOffset0Transformer {
   int apply(IContext context, int value);

   default int apply(IContextExtended<?> context, AreaDimension areaDimensionIn, IArea area, int x, int z) {
      return this.apply(context, area.getValue(x, z));
   }
}