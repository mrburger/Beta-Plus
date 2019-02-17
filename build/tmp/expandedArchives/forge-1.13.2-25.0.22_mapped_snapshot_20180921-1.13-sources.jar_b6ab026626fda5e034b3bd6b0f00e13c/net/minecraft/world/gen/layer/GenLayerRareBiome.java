package net.minecraft.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

public enum GenLayerRareBiome implements IC1Transformer {
   INSTANCE;

   private static final int PLAINS = IRegistry.field_212624_m.getId(Biomes.PLAINS);
   private static final int SUNFLOWER_PLAINS = IRegistry.field_212624_m.getId(Biomes.SUNFLOWER_PLAINS);

   public int apply(IContext context, int value) {
      return context.random(57) == 0 && value == PLAINS ? SUNFLOWER_PLAINS : value;
   }
}