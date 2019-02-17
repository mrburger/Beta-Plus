package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum GenLayerSmooth implements ICastleTransformer {
   INSTANCE;

   public int apply(IContext context, int p_202748_2_, int p_202748_3_, int p_202748_4_, int p_202748_5_, int p_202748_6_) {
      boolean flag = p_202748_3_ == p_202748_5_;
      boolean flag1 = p_202748_2_ == p_202748_4_;
      if (flag == flag1) {
         if (flag) {
            return context.random(2) == 0 ? p_202748_5_ : p_202748_2_;
         } else {
            return p_202748_6_;
         }
      } else {
         return flag ? p_202748_5_ : p_202748_2_;
      }
   }
}