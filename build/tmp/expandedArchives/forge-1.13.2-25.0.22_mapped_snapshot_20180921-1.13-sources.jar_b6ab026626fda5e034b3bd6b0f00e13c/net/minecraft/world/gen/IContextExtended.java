package net.minecraft.world.gen;

import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IPixelTransformer;

public interface IContextExtended<R extends IArea> extends IContext {
   void setPosition(long x, long z);

   R makeArea(AreaDimension dimensionIn, IPixelTransformer transformer);

   default R makeArea(AreaDimension dimensionIn, IPixelTransformer transformer, R p_201489_3_) {
      return (R)this.makeArea(dimensionIn, transformer);
   }

   default R makeArea(AreaDimension dimensionIn, IPixelTransformer transformer, R p_201488_3_, R p_201488_4_) {
      return (R)this.makeArea(dimensionIn, transformer);
   }

   int selectRandomly(int... choices);
}