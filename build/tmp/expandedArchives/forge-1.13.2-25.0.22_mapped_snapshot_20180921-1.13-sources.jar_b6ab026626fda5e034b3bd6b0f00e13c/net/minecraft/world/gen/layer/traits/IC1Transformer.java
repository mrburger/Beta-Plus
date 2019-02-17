package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.IContextExtended;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;

public interface IC1Transformer extends IAreaTransformer1, IDimOffset1Transformer {
   int apply(IContext context, int value);

   default int apply(IContextExtended<?> context, AreaDimension areaDimensionIn, IArea area, int x, int z) {
      int i = area.getValue(x + 1, z + 1);
      return this.apply(context, i);
   }
}