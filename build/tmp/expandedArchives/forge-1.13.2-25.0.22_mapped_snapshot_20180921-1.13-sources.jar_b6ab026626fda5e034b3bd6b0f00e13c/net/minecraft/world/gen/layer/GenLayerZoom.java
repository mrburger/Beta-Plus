package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContextExtended;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;

public enum GenLayerZoom implements IAreaTransformer1 {
   NORMAL,
   FUZZY {
      protected int func_202715_a(IContextExtended<?> p_202715_1_, int p_202715_2_, int p_202715_3_, int p_202715_4_, int p_202715_5_) {
         return p_202715_1_.selectRandomly(p_202715_2_, p_202715_3_, p_202715_4_, p_202715_5_);
      }
   };

   private GenLayerZoom() {
   }

   public AreaDimension apply(AreaDimension dimensionIn) {
      int i = dimensionIn.getStartX() >> 1;
      int j = dimensionIn.getStartZ() >> 1;
      int k = (dimensionIn.getXSize() >> 1) + 3;
      int l = (dimensionIn.getZSize() >> 1) + 3;
      return new AreaDimension(i, j, k, l);
   }

   public int apply(IContextExtended<?> context, AreaDimension areaDimensionIn, IArea area, int x, int z) {
      int i = areaDimensionIn.getStartX() >> 1;
      int j = areaDimensionIn.getStartZ() >> 1;
      int k = x + areaDimensionIn.getStartX();
      int l = z + areaDimensionIn.getStartZ();
      int i1 = (k >> 1) - i;
      int j1 = i1 + 1;
      int k1 = (l >> 1) - j;
      int l1 = k1 + 1;
      int i2 = area.getValue(i1, k1);
      context.setPosition((long)(k >> 1 << 1), (long)(l >> 1 << 1));
      int j2 = k & 1;
      int k2 = l & 1;
      if (j2 == 0 && k2 == 0) {
         return i2;
      } else {
         int l2 = area.getValue(i1, l1);
         int i3 = context.selectRandomly(i2, l2);
         if (j2 == 0 && k2 == 1) {
            return i3;
         } else {
            int j3 = area.getValue(j1, k1);
            int k3 = context.selectRandomly(i2, j3);
            if (j2 == 1 && k2 == 0) {
               return k3;
            } else {
               int l3 = area.getValue(j1, l1);
               return this.func_202715_a(context, i2, j3, l2, l3);
            }
         }
      }
   }

   protected int func_202715_a(IContextExtended<?> p_202715_1_, int p_202715_2_, int p_202715_3_, int p_202715_4_, int p_202715_5_) {
      if (p_202715_3_ == p_202715_4_ && p_202715_4_ == p_202715_5_) {
         return p_202715_3_;
      } else if (p_202715_2_ == p_202715_3_ && p_202715_2_ == p_202715_4_) {
         return p_202715_2_;
      } else if (p_202715_2_ == p_202715_3_ && p_202715_2_ == p_202715_5_) {
         return p_202715_2_;
      } else if (p_202715_2_ == p_202715_4_ && p_202715_2_ == p_202715_5_) {
         return p_202715_2_;
      } else if (p_202715_2_ == p_202715_3_ && p_202715_4_ != p_202715_5_) {
         return p_202715_2_;
      } else if (p_202715_2_ == p_202715_4_ && p_202715_3_ != p_202715_5_) {
         return p_202715_2_;
      } else if (p_202715_2_ == p_202715_5_ && p_202715_3_ != p_202715_4_) {
         return p_202715_2_;
      } else if (p_202715_3_ == p_202715_4_ && p_202715_2_ != p_202715_5_) {
         return p_202715_3_;
      } else if (p_202715_3_ == p_202715_5_ && p_202715_2_ != p_202715_4_) {
         return p_202715_3_;
      } else {
         return p_202715_4_ == p_202715_5_ && p_202715_2_ != p_202715_3_ ? p_202715_4_ : p_202715_1_.selectRandomly(p_202715_2_, p_202715_3_, p_202715_4_, p_202715_5_);
      }
   }
}