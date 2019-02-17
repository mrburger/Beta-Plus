package net.minecraft.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

public class GenLayerBiome implements IC0Transformer {
   private static final int BIRCH_FOREST = IRegistry.field_212624_m.getId(Biomes.BIRCH_FOREST);
   private static final int DESERT = IRegistry.field_212624_m.getId(Biomes.DESERT);
   private static final int MOUNTAINS = IRegistry.field_212624_m.getId(Biomes.MOUNTAINS);
   private static final int FOREST = IRegistry.field_212624_m.getId(Biomes.FOREST);
   private static final int SNOWY_TUNDRA = IRegistry.field_212624_m.getId(Biomes.SNOWY_TUNDRA);
   private static final int JUNGLE = IRegistry.field_212624_m.getId(Biomes.JUNGLE);
   private static final int BADLANDS_PLATEAU = IRegistry.field_212624_m.getId(Biomes.BADLANDS_PLATEAU);
   private static final int WOODED_BADLANDS_PLATEAU = IRegistry.field_212624_m.getId(Biomes.WOODED_BADLANDS_PLATEAU);
   private static final int MUSHROOM_FIELDS = IRegistry.field_212624_m.getId(Biomes.MUSHROOM_FIELDS);
   private static final int PLAINS = IRegistry.field_212624_m.getId(Biomes.PLAINS);
   private static final int GIANT_TREE_TAIGA = IRegistry.field_212624_m.getId(Biomes.GIANT_TREE_TAIGA);
   private static final int DARK_FOREST = IRegistry.field_212624_m.getId(Biomes.DARK_FOREST);
   private static final int SAVANNA = IRegistry.field_212624_m.getId(Biomes.SAVANNA);
   private static final int SWAMP = IRegistry.field_212624_m.getId(Biomes.SWAMP);
   private static final int TAIGA = IRegistry.field_212624_m.getId(Biomes.TAIGA);
   private static final int SNOWY_TAIGA = IRegistry.field_212624_m.getId(Biomes.SNOWY_TAIGA);
   @SuppressWarnings("unchecked")
   private java.util.List<net.minecraftforge.common.BiomeManager.BiomeEntry>[] biomes = new java.util.ArrayList[net.minecraftforge.common.BiomeManager.BiomeType.values().length];
   private final OverworldGenSettings settings;

   public GenLayerBiome(WorldType p_i48641_1_, OverworldGenSettings p_i48641_2_) {
      for (net.minecraftforge.common.BiomeManager.BiomeType type : net.minecraftforge.common.BiomeManager.BiomeType.values()) {
         com.google.common.collect.ImmutableList<net.minecraftforge.common.BiomeManager.BiomeEntry> biomesToAdd = net.minecraftforge.common.BiomeManager.getBiomes(type);
         int idx = type.ordinal();

         if (biomes[idx] == null) biomes[idx] = new java.util.ArrayList<net.minecraftforge.common.BiomeManager.BiomeEntry>();
         if (biomesToAdd != null) biomes[idx].addAll(biomesToAdd);
      }

      int desertIdx = net.minecraftforge.common.BiomeManager.BiomeType.DESERT.ordinal();

      biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.DESERT, 30));
      biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.SAVANNA, 20));
      biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.PLAINS, 10));

      if (p_i48641_1_ == WorldType.DEFAULT_1_1) {
         biomes[desertIdx].clear();
         biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.DESERT, 10));
         biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.FOREST, 10));
         biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.MOUNTAINS, 10));
         biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.SWAMP, 10));
         biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.PLAINS, 10));
         biomes[desertIdx].add(new net.minecraftforge.common.BiomeManager.BiomeEntry(Biomes.TAIGA, 10));
         this.settings = null;
      } else {
         this.settings = p_i48641_2_;
      }

   }

   public int apply(IContext context, int value) {
      if (this.settings != null && this.settings.func_202199_l() >= 0) {
         return this.settings.func_202199_l();
      } else {
         int i = (value & 3840) >> 8;
         value = value & -3841;
         if (!LayerUtil.isOcean(value) && value != MUSHROOM_FIELDS) {
            switch(value) {
            case 1:
               if (i > 0) {
                  return context.random(3) == 0 ? BADLANDS_PLATEAU : WOODED_BADLANDS_PLATEAU;
               }

               return IRegistry.field_212624_m.getId(getWeightedBiomeEntry(net.minecraftforge.common.BiomeManager.BiomeType.DESERT, context).biome);
            case 2:
               if (i > 0) {
                  return JUNGLE;
               }

               return IRegistry.field_212624_m.getId(getWeightedBiomeEntry(net.minecraftforge.common.BiomeManager.BiomeType.WARM, context).biome);
            case 3:
               if (i > 0) {
                  return GIANT_TREE_TAIGA;
               }

               return IRegistry.field_212624_m.getId(getWeightedBiomeEntry(net.minecraftforge.common.BiomeManager.BiomeType.COOL, context).biome);
            case 4:
               return IRegistry.field_212624_m.getId(getWeightedBiomeEntry(net.minecraftforge.common.BiomeManager.BiomeType.ICY, context).biome);
            default:
               return MUSHROOM_FIELDS;
            }
         } else {
            return value;
         }
      }
   }

   protected net.minecraftforge.common.BiomeManager.BiomeEntry getWeightedBiomeEntry(net.minecraftforge.common.BiomeManager.BiomeType type, IContext context) {
      java.util.List<net.minecraftforge.common.BiomeManager.BiomeEntry> biomeList = biomes[type.ordinal()];
      int totalWeight = net.minecraft.util.WeightedRandom.getTotalWeight(biomeList);
      int weight = net.minecraftforge.common.BiomeManager.isTypeListModded(type)?context.random(totalWeight):context.random(totalWeight / 10) * 10;
      return (net.minecraftforge.common.BiomeManager.BiomeEntry)net.minecraft.util.WeightedRandom.getRandomItem(biomeList, weight);
   }
}