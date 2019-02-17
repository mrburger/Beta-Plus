package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

public enum GenLayerIsland implements IAreaTransformer0 {
   INSTANCE;

   public int apply(IContext context, AreaDimension areaDimensionIn, int x, int z) {
      if (x == -areaDimensionIn.getStartX() && z == -areaDimensionIn.getStartZ() && areaDimensionIn.getStartX() > -areaDimensionIn.getXSize() && areaDimensionIn.getStartX() <= 0 && areaDimensionIn.getStartZ() > -areaDimensionIn.getZSize() && areaDimensionIn.getStartZ() <= 0) {
         return 1;
      } else {
         return context.random(10) == 0 ? 1 : LayerUtil.OCEAN;
      }
   }
}