package net.minecraft.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;

public enum GenLayerRiverMix implements IAreaTransformer2, IDimOffset0Transformer {
   INSTANCE;

   private static final int FROZEN_RIVER = IRegistry.field_212624_m.getId(Biomes.FROZEN_RIVER);
   private static final int SNOWY_TUNDRA = IRegistry.field_212624_m.getId(Biomes.SNOWY_TUNDRA);
   private static final int MUSHROOM_FIELDS = IRegistry.field_212624_m.getId(Biomes.MUSHROOM_FIELDS);
   private static final int MUSHROOM_FIELD_SHORE = IRegistry.field_212624_m.getId(Biomes.MUSHROOM_FIELD_SHORE);
   private static final int RIVER = IRegistry.field_212624_m.getId(Biomes.RIVER);

   public int apply(IContext context, AreaDimension dimensionIn, IArea area1, IArea area2, int x, int z) {
      int i = area1.getValue(x, z);
      int j = area2.getValue(x, z);
      if (LayerUtil.isOcean(i)) {
         return i;
      } else if (j == RIVER) {
         if (i == SNOWY_TUNDRA) {
            return FROZEN_RIVER;
         } else {
            return i != MUSHROOM_FIELDS && i != MUSHROOM_FIELD_SHORE ? j & 255 : MUSHROOM_FIELD_SHORE;
         }
      } else {
         return i;
      }
   }
}