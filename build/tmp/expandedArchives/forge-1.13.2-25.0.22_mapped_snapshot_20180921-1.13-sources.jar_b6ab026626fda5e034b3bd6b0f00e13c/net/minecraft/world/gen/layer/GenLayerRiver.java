package net.minecraft.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum GenLayerRiver implements ICastleTransformer {
   INSTANCE;

   public static final int RIVER = IRegistry.field_212624_m.getId(Biomes.RIVER);

   public int apply(IContext context, int p_202748_2_, int p_202748_3_, int p_202748_4_, int p_202748_5_, int p_202748_6_) {
      int i = riverFilter(p_202748_6_);
      return i == riverFilter(p_202748_5_) && i == riverFilter(p_202748_2_) && i == riverFilter(p_202748_3_) && i == riverFilter(p_202748_4_) ? -1 : RIVER;
   }

   private static int riverFilter(int p_151630_0_) {
      return p_151630_0_ >= 2 ? 2 + (p_151630_0_ & 1) : p_151630_0_;
   }
}