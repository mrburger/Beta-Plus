package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

public enum GenLayerRiverInit implements IC0Transformer {
   INSTANCE;

   public int apply(IContext context, int value) {
      return LayerUtil.isShallowOcean(value) ? value : context.random(299999) + 2;
   }
}