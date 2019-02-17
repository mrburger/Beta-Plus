package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.IContextExtended;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;

public interface IAreaTransformer0 {
   default <R extends IArea> IAreaFactory<R> apply(IContextExtended<R> context) {
      return (p_202822_2_) -> {
         return context.makeArea(p_202822_2_, (p_202820_3_, p_202820_4_) -> {
            context.setPosition((long)(p_202820_3_ + p_202822_2_.getStartX()), (long)(p_202820_4_ + p_202822_2_.getStartZ()));
            return this.apply(context, p_202822_2_, p_202820_3_, p_202820_4_);
         });
      };
   }

   int apply(IContext context, AreaDimension areaDimensionIn, int x, int z);
}