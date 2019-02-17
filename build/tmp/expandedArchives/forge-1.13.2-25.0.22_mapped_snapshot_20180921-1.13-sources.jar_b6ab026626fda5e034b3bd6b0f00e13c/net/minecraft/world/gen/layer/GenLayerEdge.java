package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.IC0Transformer;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public class GenLayerEdge {
   public static enum CoolWarm implements ICastleTransformer {
      INSTANCE;

      public int apply(IContext context, int p_202748_2_, int p_202748_3_, int p_202748_4_, int p_202748_5_, int p_202748_6_) {
         return p_202748_6_ != 1 || p_202748_2_ != 3 && p_202748_3_ != 3 && p_202748_5_ != 3 && p_202748_4_ != 3 && p_202748_2_ != 4 && p_202748_3_ != 4 && p_202748_5_ != 4 && p_202748_4_ != 4 ? p_202748_6_ : 2;
      }
   }

   public static enum HeatIce implements ICastleTransformer {
      INSTANCE;

      public int apply(IContext context, int p_202748_2_, int p_202748_3_, int p_202748_4_, int p_202748_5_, int p_202748_6_) {
         return p_202748_6_ != 4 || p_202748_2_ != 1 && p_202748_3_ != 1 && p_202748_5_ != 1 && p_202748_4_ != 1 && p_202748_2_ != 2 && p_202748_3_ != 2 && p_202748_5_ != 2 && p_202748_4_ != 2 ? p_202748_6_ : 3;
      }
   }

   public static enum Special implements IC0Transformer {
      INSTANCE;

      public int apply(IContext context, int value) {
         if (!LayerUtil.isShallowOcean(value) && context.random(13) == 0) {
            value |= 1 + context.random(15) << 8 & 3840;
         }

         return value;
      }
   }
}