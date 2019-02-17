package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.IContextExtended;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;

public interface IAreaTransformer2 extends IDimTransformer {
   default <R extends IArea> IAreaFactory<R> apply(IContextExtended<R> context, IAreaFactory<R> areaFactory, IAreaFactory<R> areaFactoryIn) {
      return (p_202710_4_) -> {
         R r = areaFactory.make(this.apply(p_202710_4_));
         R r1 = areaFactoryIn.make(this.apply(p_202710_4_));
         return context.makeArea(p_202710_4_, (p_202708_5_, p_202708_6_) -> {
            context.setPosition((long)(p_202708_5_ + p_202710_4_.getStartX()), (long)(p_202708_6_ + p_202710_4_.getStartZ()));
            return this.apply(context, p_202710_4_, r, r1, p_202708_5_, p_202708_6_);
         }, r, r1);
      };
   }

   int apply(IContext context, AreaDimension dimensionIn, IArea area1, IArea area2, int x, int z);
}