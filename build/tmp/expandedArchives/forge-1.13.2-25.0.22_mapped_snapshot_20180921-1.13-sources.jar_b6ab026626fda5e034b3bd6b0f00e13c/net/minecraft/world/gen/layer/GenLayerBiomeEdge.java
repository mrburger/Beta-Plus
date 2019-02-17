package net.minecraft.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum GenLayerBiomeEdge implements ICastleTransformer {
   INSTANCE;

   private static final int DESERT = IRegistry.field_212624_m.getId(Biomes.DESERT);
   private static final int MOUNTAINS = IRegistry.field_212624_m.getId(Biomes.MOUNTAINS);
   private static final int WOODED_MOUNTAINS = IRegistry.field_212624_m.getId(Biomes.WOODED_MOUNTAINS);
   private static final int SNOWY_TUNDRA = IRegistry.field_212624_m.getId(Biomes.SNOWY_TUNDRA);
   private static final int JUNGLE = IRegistry.field_212624_m.getId(Biomes.JUNGLE);
   private static final int JUNGLE_EDGE = IRegistry.field_212624_m.getId(Biomes.JUNGLE_EDGE);
   private static final int BADLANDS = IRegistry.field_212624_m.getId(Biomes.BADLANDS);
   private static final int BADLANDS_PLATEAU = IRegistry.field_212624_m.getId(Biomes.BADLANDS_PLATEAU);
   private static final int WOODED_BADLANDS_PLATEAU = IRegistry.field_212624_m.getId(Biomes.WOODED_BADLANDS_PLATEAU);
   private static final int PLAINS = IRegistry.field_212624_m.getId(Biomes.PLAINS);
   private static final int GIANT_TREE_TAIGA = IRegistry.field_212624_m.getId(Biomes.GIANT_TREE_TAIGA);
   private static final int MOUNTAIN_EDGE = IRegistry.field_212624_m.getId(Biomes.MOUNTAIN_EDGE);
   private static final int SWAMP = IRegistry.field_212624_m.getId(Biomes.SWAMP);
   private static final int TAIGA = IRegistry.field_212624_m.getId(Biomes.TAIGA);
   private static final int SNOWY_TAIGA = IRegistry.field_212624_m.getId(Biomes.SNOWY_TAIGA);

   public int apply(IContext context, int p_202748_2_, int p_202748_3_, int p_202748_4_, int p_202748_5_, int p_202748_6_) {
      int[] aint = new int[1];
      if (!this.func_202751_a(aint, p_202748_2_, p_202748_3_, p_202748_4_, p_202748_5_, p_202748_6_, MOUNTAINS, MOUNTAIN_EDGE) && !this.replaceBiomeEdge(aint, p_202748_2_, p_202748_3_, p_202748_4_, p_202748_5_, p_202748_6_, WOODED_BADLANDS_PLATEAU, BADLANDS) && !this.replaceBiomeEdge(aint, p_202748_2_, p_202748_3_, p_202748_4_, p_202748_5_, p_202748_6_, BADLANDS_PLATEAU, BADLANDS) && !this.replaceBiomeEdge(aint, p_202748_2_, p_202748_3_, p_202748_4_, p_202748_5_, p_202748_6_, GIANT_TREE_TAIGA, TAIGA)) {
         if (p_202748_6_ != DESERT || p_202748_2_ != SNOWY_TUNDRA && p_202748_3_ != SNOWY_TUNDRA && p_202748_5_ != SNOWY_TUNDRA && p_202748_4_ != SNOWY_TUNDRA) {
            if (p_202748_6_ == SWAMP) {
               if (p_202748_2_ == DESERT || p_202748_3_ == DESERT || p_202748_5_ == DESERT || p_202748_4_ == DESERT || p_202748_2_ == SNOWY_TAIGA || p_202748_3_ == SNOWY_TAIGA || p_202748_5_ == SNOWY_TAIGA || p_202748_4_ == SNOWY_TAIGA || p_202748_2_ == SNOWY_TUNDRA || p_202748_3_ == SNOWY_TUNDRA || p_202748_5_ == SNOWY_TUNDRA || p_202748_4_ == SNOWY_TUNDRA) {
                  return PLAINS;
               }

               if (p_202748_2_ == JUNGLE || p_202748_4_ == JUNGLE || p_202748_3_ == JUNGLE || p_202748_5_ == JUNGLE) {
                  return JUNGLE_EDGE;
               }
            }

            return p_202748_6_;
         } else {
            return WOODED_MOUNTAINS;
         }
      } else {
         return aint[0];
      }
   }

   private boolean func_202751_a(int[] p_202751_1_, int p_202751_2_, int p_202751_3_, int p_202751_4_, int p_202751_5_, int p_202751_6_, int p_202751_7_, int p_202751_8_) {
      if (!LayerUtil.areBiomesSimilar(p_202751_6_, p_202751_7_)) {
         return false;
      } else {
         if (this.canBiomesBeNeighbors(p_202751_2_, p_202751_7_) && this.canBiomesBeNeighbors(p_202751_3_, p_202751_7_) && this.canBiomesBeNeighbors(p_202751_5_, p_202751_7_) && this.canBiomesBeNeighbors(p_202751_4_, p_202751_7_)) {
            p_202751_1_[0] = p_202751_6_;
         } else {
            p_202751_1_[0] = p_202751_8_;
         }

         return true;
      }
   }

   /**
    * Creates a border around a biome.
    */
   private boolean replaceBiomeEdge(int[] p_151635_1_, int p_151635_2_, int p_151635_3_, int p_151635_4_, int p_151635_5_, int p_151635_6_, int p_151635_7_, int p_151635_8_) {
      if (p_151635_6_ != p_151635_7_) {
         return false;
      } else {
         if (LayerUtil.areBiomesSimilar(p_151635_2_, p_151635_7_) && LayerUtil.areBiomesSimilar(p_151635_3_, p_151635_7_) && LayerUtil.areBiomesSimilar(p_151635_5_, p_151635_7_) && LayerUtil.areBiomesSimilar(p_151635_4_, p_151635_7_)) {
            p_151635_1_[0] = p_151635_6_;
         } else {
            p_151635_1_[0] = p_151635_8_;
         }

         return true;
      }
   }

   /**
    * Returns if two biomes can logically be neighbors. If one is hot and the other cold, for example, it returns false.
    */
   private boolean canBiomesBeNeighbors(int p_151634_1_, int p_151634_2_) {
      if (LayerUtil.areBiomesSimilar(p_151634_1_, p_151634_2_)) {
         return true;
      } else {
         Biome biome = IRegistry.field_212624_m.get(p_151634_1_);
         Biome biome1 = IRegistry.field_212624_m.get(p_151634_2_);
         if (biome != null && biome1 != null) {
            Biome.TempCategory biome$tempcategory = biome.getTempCategory();
            Biome.TempCategory biome$tempcategory1 = biome1.getTempCategory();
            return biome$tempcategory == biome$tempcategory1 || biome$tempcategory == Biome.TempCategory.MEDIUM || biome$tempcategory1 == Biome.TempCategory.MEDIUM;
         } else {
            return false;
         }
      }
   }
}