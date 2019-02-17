package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.IContextExtended;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;

public interface ICastleTransformer extends IAreaTransformer1, IDimOffset1Transformer {
   int apply(IContext context, int p_202748_2_, int p_202748_3_, int p_202748_4_, int p_202748_5_, int p_202748_6_);

   default int apply(IContextExtended<?> context, AreaDimension areaDimensionIn, IArea area, int x, int z) {
      return this.apply(context, area.getValue(x + 1, z + 0), area.getValue(x + 2, z + 1), area.getValue(x + 1, z + 2), area.getValue(x + 0, z + 1), area.getValue(x + 1, z + 1));
   }
}