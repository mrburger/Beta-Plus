package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

public enum GenLayerAddSnow implements IC1Transformer {
   INSTANCE;

   public int apply(IContext context, int value) {
      if (LayerUtil.isShallowOcean(value)) {
         return value;
      } else {
         int i = context.random(6);
         if (i == 0) {
            return 4;
         } else {
            return i == 1 ? 3 : 1;
         }
      }
   }
}