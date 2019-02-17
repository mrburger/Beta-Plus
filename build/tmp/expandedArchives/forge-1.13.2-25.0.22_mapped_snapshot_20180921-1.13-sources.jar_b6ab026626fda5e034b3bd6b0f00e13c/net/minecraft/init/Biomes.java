package net.minecraft.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.biome.Biome;

@net.minecraftforge.registries.ObjectHolder("minecraft")
public abstract class Biomes {
   public static final Biome OCEAN;
   public static final Biome DEFAULT;
   public static final Biome PLAINS;
   public static final Biome DESERT;
   public static final Biome MOUNTAINS;
   public static final Biome FOREST;
   public static final Biome TAIGA;
   public static final Biome SWAMP;
   public static final Biome RIVER;
   public static final Biome NETHER;
   /** Is the biome used for sky world. */
   public static final Biome THE_END;
   public static final Biome FROZEN_OCEAN;
   public static final Biome FROZEN_RIVER;
   public static final Biome SNOWY_TUNDRA;
   public static final Biome SNOWY_MOUNTAINS;
   public static final Biome MUSHROOM_FIELDS;
   public static final Biome MUSHROOM_FIELD_SHORE;
   /** Beach biome. */
   public static final Biome BEACH;
   /** Desert Hills biome. */
   public static final Biome DESERT_HILLS;
   /** Forest Hills biome. */
   public static final Biome WOODED_HILLS;
   /** Taiga Hills biome. */
   public static final Biome TAIGA_HILLS;
   /** Extreme Hills Edge biome. */
   public static final Biome MOUNTAIN_EDGE;
   /** Jungle biome identifier */
   public static final Biome JUNGLE;
   public static final Biome JUNGLE_HILLS;
   public static final Biome JUNGLE_EDGE;
   public static final Biome DEEP_OCEAN;
   public static final Biome STONE_SHORE;
   public static final Biome SNOWY_BEACH;
   public static final Biome BIRCH_FOREST;
   public static final Biome BIRCH_FOREST_HILLS;
   public static final Biome DARK_FOREST;
   public static final Biome SNOWY_TAIGA;
   public static final Biome SNOWY_TAIGA_HILLS;
   public static final Biome GIANT_TREE_TAIGA;
   public static final Biome GIANT_TREE_TAIGA_HILLS;
   public static final Biome WOODED_MOUNTAINS;
   public static final Biome SAVANNA;
   public static final Biome SAVANNA_PLATEAU;
   public static final Biome BADLANDS;
   public static final Biome WOODED_BADLANDS_PLATEAU;
   public static final Biome BADLANDS_PLATEAU;
   public static final Biome SMALL_END_ISLANDS;
   public static final Biome END_MIDLANDS;
   public static final Biome END_HIGHLANDS;
   public static final Biome END_BARRENS;
   public static final Biome WARM_OCEAN;
   public static final Biome LUKEWARM_OCEAN;
   public static final Biome COLD_OCEAN;
   public static final Biome DEEP_WARM_OCEAN;
   public static final Biome DEEP_LUKEWARM_OCEAN;
   public static final Biome DEEP_COLD_OCEAN;
   public static final Biome DEEP_FROZEN_OCEAN;
   public static final Biome THE_VOID;
   public static final Biome SUNFLOWER_PLAINS;
   public static final Biome DESERT_LAKES;
   public static final Biome GRAVELLY_MOUNTAINS;
   public static final Biome FLOWER_FOREST;
   public static final Biome TAIGA_MOUNTAINS;
   public static final Biome SWAMP_HILLS;
   public static final Biome ICE_SPIKES;
   public static final Biome MODIFIED_JUNGLE;
   public static final Biome MODIFIED_JUNGLE_EDGE;
   public static final Biome TALL_BIRCH_FOREST;
   public static final Biome TALL_BIRCH_HILLS;
   public static final Biome DARK_FOREST_HILLS;
   public static final Biome SNOWY_TAIGA_MOUNTAINS;
   public static final Biome GIANT_SPRUCE_TAIGA;
   public static final Biome GIANT_SPRUCE_TAIGA_HILLS;
   public static final Biome MODIFIED_GRAVELLY_MOUNTAINS;
   public static final Biome SHATTERED_SAVANNA;
   public static final Biome SHATTERED_SAVANNA_PLATEAU;
   public static final Biome ERODED_BADLANDS;
   public static final Biome MODIFIED_WOODED_BADLANDS_PLATEAU;
   public static final Biome MODIFIED_BADLANDS_PLATEAU;

   private static Biome getRegisteredBiome(String id) {
      Biome biome = IRegistry.field_212624_m.func_212608_b(new ResourceLocation(id));
      if (biome == null) {
         throw new IllegalStateException("Invalid Biome requested: " + id);
      } else {
         return biome;
      }
   }

   static {
      if (!Bootstrap.isRegistered()) {
         throw new RuntimeException("Accessed Biomes before Bootstrap!");
      } else {
         OCEAN = getRegisteredBiome("ocean");
         DEFAULT = OCEAN;
         PLAINS = getRegisteredBiome("plains");
         DESERT = getRegisteredBiome("desert");
         MOUNTAINS = getRegisteredBiome("mountains");
         FOREST = getRegisteredBiome("forest");
         TAIGA = getRegisteredBiome("taiga");
         SWAMP = getRegisteredBiome("swamp");
         RIVER = getRegisteredBiome("river");
         NETHER = getRegisteredBiome("nether");
         THE_END = getRegisteredBiome("the_end");
         FROZEN_OCEAN = getRegisteredBiome("frozen_ocean");
         FROZEN_RIVER = getRegisteredBiome("frozen_river");
         SNOWY_TUNDRA = getRegisteredBiome("snowy_tundra");
         SNOWY_MOUNTAINS = getRegisteredBiome("snowy_mountains");
         MUSHROOM_FIELDS = getRegisteredBiome("mushroom_fields");
         MUSHROOM_FIELD_SHORE = getRegisteredBiome("mushroom_field_shore");
         BEACH = getRegisteredBiome("beach");
         DESERT_HILLS = getRegisteredBiome("desert_hills");
         WOODED_HILLS = getRegisteredBiome("wooded_hills");
         TAIGA_HILLS = getRegisteredBiome("taiga_hills");
         MOUNTAIN_EDGE = getRegisteredBiome("mountain_edge");
         JUNGLE = getRegisteredBiome("jungle");
         JUNGLE_HILLS = getRegisteredBiome("jungle_hills");
         JUNGLE_EDGE = getRegisteredBiome("jungle_edge");
         DEEP_OCEAN = getRegisteredBiome("deep_ocean");
         STONE_SHORE = getRegisteredBiome("stone_shore");
         SNOWY_BEACH = getRegisteredBiome("snowy_beach");
         BIRCH_FOREST = getRegisteredBiome("birch_forest");
         BIRCH_FOREST_HILLS = getRegisteredBiome("birch_forest_hills");
         DARK_FOREST = getRegisteredBiome("dark_forest");
         SNOWY_TAIGA = getRegisteredBiome("snowy_taiga");
         SNOWY_TAIGA_HILLS = getRegisteredBiome("snowy_taiga_hills");
         GIANT_TREE_TAIGA = getRegisteredBiome("giant_tree_taiga");
         GIANT_TREE_TAIGA_HILLS = getRegisteredBiome("giant_tree_taiga_hills");
         WOODED_MOUNTAINS = getRegisteredBiome("wooded_mountains");
         SAVANNA = getRegisteredBiome("savanna");
         SAVANNA_PLATEAU = getRegisteredBiome("savanna_plateau");
         BADLANDS = getRegisteredBiome("badlands");
         WOODED_BADLANDS_PLATEAU = getRegisteredBiome("wooded_badlands_plateau");
         BADLANDS_PLATEAU = getRegisteredBiome("badlands_plateau");
         SMALL_END_ISLANDS = getRegisteredBiome("small_end_islands");
         END_MIDLANDS = getRegisteredBiome("end_midlands");
         END_HIGHLANDS = getRegisteredBiome("end_highlands");
         END_BARRENS = getRegisteredBiome("end_barrens");
         WARM_OCEAN = getRegisteredBiome("warm_ocean");
         LUKEWARM_OCEAN = getRegisteredBiome("lukewarm_ocean");
         COLD_OCEAN = getRegisteredBiome("cold_ocean");
         DEEP_WARM_OCEAN = getRegisteredBiome("deep_warm_ocean");
         DEEP_LUKEWARM_OCEAN = getRegisteredBiome("deep_lukewarm_ocean");
         DEEP_COLD_OCEAN = getRegisteredBiome("deep_cold_ocean");
         DEEP_FROZEN_OCEAN = getRegisteredBiome("deep_frozen_ocean");
         THE_VOID = getRegisteredBiome("the_void");
         SUNFLOWER_PLAINS = getRegisteredBiome("sunflower_plains");
         DESERT_LAKES = getRegisteredBiome("desert_lakes");
         GRAVELLY_MOUNTAINS = getRegisteredBiome("gravelly_mountains");
         FLOWER_FOREST = getRegisteredBiome("flower_forest");
         TAIGA_MOUNTAINS = getRegisteredBiome("taiga_mountains");
         SWAMP_HILLS = getRegisteredBiome("swamp_hills");
         ICE_SPIKES = getRegisteredBiome("ice_spikes");
         MODIFIED_JUNGLE = getRegisteredBiome("modified_jungle");
         MODIFIED_JUNGLE_EDGE = getRegisteredBiome("modified_jungle_edge");
         TALL_BIRCH_FOREST = getRegisteredBiome("tall_birch_forest");
         TALL_BIRCH_HILLS = getRegisteredBiome("tall_birch_hills");
         DARK_FOREST_HILLS = getRegisteredBiome("dark_forest_hills");
         SNOWY_TAIGA_MOUNTAINS = getRegisteredBiome("snowy_taiga_mountains");
         GIANT_SPRUCE_TAIGA = getRegisteredBiome("giant_spruce_taiga");
         GIANT_SPRUCE_TAIGA_HILLS = getRegisteredBiome("giant_spruce_taiga_hills");
         MODIFIED_GRAVELLY_MOUNTAINS = getRegisteredBiome("modified_gravelly_mountains");
         SHATTERED_SAVANNA = getRegisteredBiome("shattered_savanna");
         SHATTERED_SAVANNA_PLATEAU = getRegisteredBiome("shattered_savanna_plateau");
         ERODED_BADLANDS = getRegisteredBiome("eroded_badlands");
         MODIFIED_WOODED_BADLANDS_PLATEAU = getRegisteredBiome("modified_wooded_badlands_plateau");
         MODIFIED_BADLANDS_PLATEAU = getRegisteredBiome("modified_badlands_plateau");
      }
   }
}