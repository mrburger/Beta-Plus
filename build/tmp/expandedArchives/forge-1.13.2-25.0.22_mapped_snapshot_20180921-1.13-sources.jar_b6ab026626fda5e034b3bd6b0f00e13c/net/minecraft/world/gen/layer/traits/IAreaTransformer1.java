package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IContextExtended;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;

public interface IAreaTransformer1 extends IDimTransformer {
   default <R extends IArea> IAreaFactory<R> apply(IContextExtended<R> context, IAreaFactory<R> areaFactory) {
      return (p_202714_3_) -> {
         R r = areaFactory.make(this.apply(p_202714_3_));
         return context.makeArea(p_202714_3_, (p_202711_4_, p_202711_5_) -> {
            context.setPosition((long)(p_202711_4_ + p_202714_3_.getStartX()), (long)(p_202711_5_ + p_202714_3_.getStartZ()));
            return this.apply(context, p_202714_3_, r, p_202711_4_, p_202711_5_);
         }, r);
      };
   }

   int apply(IContextExtended<?> context, AreaDimension areaDimensionIn, IArea area, int x, int z);
}