package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;

public enum GenLayerMixOceans implements IAreaTransformer2, IDimOffset0Transformer {
   INSTANCE;

   public int apply(IContext context, AreaDimension dimensionIn, IArea area1, IArea area2, int x, int z) {
      int i = area1.getValue(x, z);
      int j = area2.getValue(x, z);
      if (!LayerUtil.isOcean(i)) {
         return i;
      } else {
         int k = 8;
         int l = 4;

         for(int i1 = -8; i1 <= 8; i1 += 4) {
            for(int j1 = -8; j1 <= 8; j1 += 4) {
               int k1 = area1.getValue(x + i1, z + j1);
               if (!LayerUtil.isOcean(k1)) {
                  if (j == LayerUtil.WARM_OCEAN) {
                     return LayerUtil.LUKEWARM_OCEAN;
                  }

                  if (j == LayerUtil.FROZEN_OCEAN) {
                     return LayerUtil.COLD_OCEAN;
                  }
               }
            }
         }

         if (i == LayerUtil.DEEP_OCEAN) {
            if (j == LayerUtil.LUKEWARM_OCEAN) {
               return LayerUtil.DEEP_LUKEWARM_OCEAN;
            }

            if (j == LayerUtil.OCEAN) {
               return LayerUtil.DEEP_OCEAN;
            }

            if (j == LayerUtil.COLD_OCEAN) {
               return LayerUtil.DEEP_COLD_OCEAN;
            }

            if (j == LayerUtil.FROZEN_OCEAN) {
               return LayerUtil.DEEP_FROZEN_OCEAN;
            }
         }

         return j;
      }
   }
}