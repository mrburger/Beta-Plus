package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum GenLayerDeepOcean implements ICastleTransformer {
   INSTANCE;

   public int apply(IContext context, int p_202748_2_, int p_202748_3_, int p_202748_4_, int p_202748_5_, int p_202748_6_) {
      if (LayerUtil.isShallowOcean(p_202748_6_)) {
         int i = 0;
         if (LayerUtil.isShallowOcean(p_202748_2_)) {
            ++i;
         }

         if (LayerUtil.isShallowOcean(p_202748_3_)) {
            ++i;
         }

         if (LayerUtil.isShallowOcean(p_202748_5_)) {
            ++i;
         }

         if (LayerUtil.isShallowOcean(p_202748_4_)) {
            ++i;
         }

         if (i > 3) {
            if (p_202748_6_ == LayerUtil.WARM_OCEAN) {
               return LayerUtil.DEEP_WARM_OCEAN;
            }

            if (p_202748_6_ == LayerUtil.LUKEWARM_OCEAN) {
               return LayerUtil.DEEP_LUKEWARM_OCEAN;
            }

            if (p_202748_6_ == LayerUtil.OCEAN) {
               return LayerUtil.DEEP_OCEAN;
            }

            if (p_202748_6_ == LayerUtil.COLD_OCEAN) {
               return LayerUtil.DEEP_COLD_OCEAN;
            }

            if (p_202748_6_ == LayerUtil.FROZEN_OCEAN) {
               return LayerUtil.DEEP_FROZEN_OCEAN;
            }

            return LayerUtil.DEEP_OCEAN;
         }
      }

      return p_202748_6_;
   }
}