package net.minecraft.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum GenLayerShore implements ICastleTransformer {
   INSTANCE;

   private static final int BEACH = IRegistry.field_212624_m.getId(Biomes.BEACH);
   private static final int SNOWY_BEACH = IRegistry.field_212624_m.getId(Biomes.SNOWY_BEACH);
   private static final int DESERT = IRegistry.field_212624_m.getId(Biomes.DESERT);
   private static final int MOUNTAINS = IRegistry.field_212624_m.getId(Biomes.MOUNTAINS);
   private static final int WOODED_MOUNTAINS = IRegistry.field_212624_m.getId(Biomes.WOODED_MOUNTAINS);
   private static final int FOREST = IRegistry.field_212624_m.getId(Biomes.FOREST);
   private static final int JUNGLE = IRegistry.field_212624_m.getId(Biomes.JUNGLE);
   private static final int JUNGLE_EDGE = IRegistry.field_212624_m.getId(Biomes.JUNGLE_EDGE);
   private static final int JUNGLE_HILLS = IRegistry.field_212624_m.getId(Biomes.JUNGLE_HILLS);
   private static final int BADLANDS = IRegistry.field_212624_m.getId(Biomes.BADLANDS);
   private static final int WOODED_BADLANDS_PLATEAU = IRegistry.field_212624_m.getId(Biomes.WOODED_BADLANDS_PLATEAU);
   private static final int BADLANDS_PLATEAU = IRegistry.field_212624_m.getId(Biomes.BADLANDS_PLATEAU);
   private static final int ERODED_BADLANDS = IRegistry.field_212624_m.getId(Biomes.ERODED_BADLANDS);
   private static final int MODIFIED_WOODED_BADLANDS_PLATEAU = IRegistry.field_212624_m.getId(Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU);
   private static final int MODIFIED_BADLANDS_PLATEAU = IRegistry.field_212624_m.getId(Biomes.MODIFIED_BADLANDS_PLATEAU);
   private static final int MUSHROOM_FIELDS = IRegistry.field_212624_m.getId(Biomes.MUSHROOM_FIELDS);
   private static final int MUSHROOM_FIELD_SHORE = IRegistry.field_212624_m.getId(Biomes.MUSHROOM_FIELD_SHORE);
   private static final int RIVER = IRegistry.field_212624_m.getId(Biomes.RIVER);
   private static final int MOUNTAIN_EDGE = IRegistry.field_212624_m.getId(Biomes.MOUNTAIN_EDGE);
   private static final int STONE_SHORE = IRegistry.field_212624_m.getId(Biomes.STONE_SHORE);
   private static final int SWAMP = IRegistry.field_212624_m.getId(Biomes.SWAMP);
   private static final int TAIGA = IRegistry.field_212624_m.getId(Biomes.TAIGA);

   public int apply(IContext context, int p_202748_2_, int p_202748_3_, int p_202748_4_, int p_202748_5_, int p_202748_6_) {
      Biome biome = IRegistry.field_212624_m.get(p_202748_6_);
      if (p_202748_6_ == MUSHROOM_FIELDS) {
         if (LayerUtil.isShallowOcean(p_202748_2_) || LayerUtil.isShallowOcean(p_202748_3_) || LayerUtil.isShallowOcean(p_202748_4_) || LayerUtil.isShallowOcean(p_202748_5_)) {
            return MUSHROOM_FIELD_SHORE;
         }
      } else if (biome != null && biome.getCategory() == Biome.Category.JUNGLE) {
         if (!isJungleCompatible(p_202748_2_) || !isJungleCompatible(p_202748_3_) || !isJungleCompatible(p_202748_4_) || !isJungleCompatible(p_202748_5_)) {
            return JUNGLE_EDGE;
         }

         if (LayerUtil.isOcean(p_202748_2_) || LayerUtil.isOcean(p_202748_3_) || LayerUtil.isOcean(p_202748_4_) || LayerUtil.isOcean(p_202748_5_)) {
            return BEACH;
         }
      } else if (p_202748_6_ != MOUNTAINS && p_202748_6_ != WOODED_MOUNTAINS && p_202748_6_ != MOUNTAIN_EDGE) {
         if (biome != null && biome.getPrecipitation() == Biome.RainType.SNOW) {
            if (!LayerUtil.isOcean(p_202748_6_) && (LayerUtil.isOcean(p_202748_2_) || LayerUtil.isOcean(p_202748_3_) || LayerUtil.isOcean(p_202748_4_) || LayerUtil.isOcean(p_202748_5_))) {
               return SNOWY_BEACH;
            }
         } else if (p_202748_6_ != BADLANDS && p_202748_6_ != WOODED_BADLANDS_PLATEAU) {
            if (!LayerUtil.isOcean(p_202748_6_) && p_202748_6_ != RIVER && p_202748_6_ != SWAMP && (LayerUtil.isOcean(p_202748_2_) || LayerUtil.isOcean(p_202748_3_) || LayerUtil.isOcean(p_202748_4_) || LayerUtil.isOcean(p_202748_5_))) {
               return BEACH;
            }
         } else if (!LayerUtil.isOcean(p_202748_2_) && !LayerUtil.isOcean(p_202748_3_) && !LayerUtil.isOcean(p_202748_4_) && !LayerUtil.isOcean(p_202748_5_) && (!this.isMesa(p_202748_2_) || !this.isMesa(p_202748_3_) || !this.isMesa(p_202748_4_) || !this.isMesa(p_202748_5_))) {
            return DESERT;
         }
      } else if (!LayerUtil.isOcean(p_202748_6_) && (LayerUtil.isOcean(p_202748_2_) || LayerUtil.isOcean(p_202748_3_) || LayerUtil.isOcean(p_202748_4_) || LayerUtil.isOcean(p_202748_5_))) {
         return STONE_SHORE;
      }

      return p_202748_6_;
   }

   private static boolean isJungleCompatible(int p_151631_0_) {
      if (IRegistry.field_212624_m.get(p_151631_0_) != null && IRegistry.field_212624_m.get(p_151631_0_).getCategory() == Biome.Category.JUNGLE) {
         return true;
      } else {
         return p_151631_0_ == JUNGLE_EDGE || p_151631_0_ == JUNGLE || p_151631_0_ == JUNGLE_HILLS || p_151631_0_ == FOREST || p_151631_0_ == TAIGA || LayerUtil.isOcean(p_151631_0_);
      }
   }

   private boolean isMesa(int p_151633_1_) {
      return p_151633_1_ == BADLANDS || p_151633_1_ == WOODED_BADLANDS_PLATEAU || p_151633_1_ == BADLANDS_PLATEAU || p_151633_1_ == ERODED_BADLANDS || p_151633_1_ == MODIFIED_WOODED_BADLANDS_PLATEAU || p_151633_1_ == MODIFIED_BADLANDS_PLATEAU;
   }
}