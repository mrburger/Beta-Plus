package net.minecraft.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.IBishopTransformer;

public enum GenLayerAddMushroomIsland implements IBishopTransformer {
   INSTANCE;

   private static final int MUSHROOM_FIELDS = IRegistry.field_212624_m.getId(Biomes.MUSHROOM_FIELDS);

   public int apply(IContext context, int x, int p_202792_3_, int p_202792_4_, int p_202792_5_, int p_202792_6_) {
      return LayerUtil.isShallowOcean(p_202792_6_) && LayerUtil.isShallowOcean(p_202792_5_) && LayerUtil.isShallowOcean(x) && LayerUtil.isShallowOcean(p_202792_4_) && LayerUtil.isShallowOcean(p_202792_3_) && context.random(100) == 0 ? MUSHROOM_FIELDS : p_202792_6_;
   }
}