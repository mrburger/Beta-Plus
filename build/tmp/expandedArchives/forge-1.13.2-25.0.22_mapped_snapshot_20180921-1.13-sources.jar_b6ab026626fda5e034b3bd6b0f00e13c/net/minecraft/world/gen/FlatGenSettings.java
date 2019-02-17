package net.minecraft.world.gen;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.CompositeFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.LakesConfig;
import net.minecraft.world.gen.feature.structure.DesertPyramidConfig;
import net.minecraft.world.gen.feature.structure.EndCityConfig;
import net.minecraft.world.gen.feature.structure.FortressConfig;
import net.minecraft.world.gen.feature.structure.IglooConfig;
import net.minecraft.world.gen.feature.structure.JunglePyramidConfig;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.OceanMonumentConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinStructure;
import net.minecraft.world.gen.feature.structure.ShipwreckConfig;
import net.minecraft.world.gen.feature.structure.StrongholdConfig;
import net.minecraft.world.gen.feature.structure.SwampHutConfig;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.structure.VillagePieces;
import net.minecraft.world.gen.feature.structure.WoodlandMansionConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.LakeChanceConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlatGenSettings extends ChunkGenSettings {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final CompositeFeature<MineshaftConfig, NoPlacementConfig> field_202250_m = Biome.createCompositeFeature(Feature.MINESHAFT, new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<VillageConfig, NoPlacementConfig> field_202251_n = Biome.createCompositeFeature(Feature.VILLAGE, new VillageConfig(0, VillagePieces.Type.OAK), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<StrongholdConfig, NoPlacementConfig> field_202252_o = Biome.createCompositeFeature(Feature.STRONGHOLD, new StrongholdConfig(), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<SwampHutConfig, NoPlacementConfig> field_202253_p = Biome.createCompositeFeature(Feature.SWAMP_HUT, new SwampHutConfig(), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<DesertPyramidConfig, NoPlacementConfig> field_202254_q = Biome.createCompositeFeature(Feature.DESERT_PYRAMID, new DesertPyramidConfig(), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<JunglePyramidConfig, NoPlacementConfig> field_202255_r = Biome.createCompositeFeature(Feature.JUNGLE_PYRAMID, new JunglePyramidConfig(), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<IglooConfig, NoPlacementConfig> field_202256_s = Biome.createCompositeFeature(Feature.IGLOO, new IglooConfig(), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<ShipwreckConfig, NoPlacementConfig> field_204750_v = Biome.createCompositeFeature(Feature.SHIPWRECK, new ShipwreckConfig(false), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<OceanMonumentConfig, NoPlacementConfig> field_202257_t = Biome.createCompositeFeature(Feature.OCEAN_MONUMENT, new OceanMonumentConfig(), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<LakesConfig, LakeChanceConfig> field_202258_u = Biome.createCompositeFeature(Feature.LAKES, new LakesConfig(Blocks.WATER), Biome.LAKE_WATER, new LakeChanceConfig(4));
   private static final CompositeFeature<LakesConfig, LakeChanceConfig> field_202259_v = Biome.createCompositeFeature(Feature.LAKES, new LakesConfig(Blocks.LAVA), Biome.LAVA_LAKE, new LakeChanceConfig(80));
   private static final CompositeFeature<EndCityConfig, NoPlacementConfig> field_202260_w = Biome.createCompositeFeature(Feature.END_CITY, new EndCityConfig(), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<WoodlandMansionConfig, NoPlacementConfig> field_202261_x = Biome.createCompositeFeature(Feature.WOODLAND_MANSION, new WoodlandMansionConfig(), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<FortressConfig, NoPlacementConfig> field_202262_y = Biome.createCompositeFeature(Feature.FORTRESS, new FortressConfig(), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<OceanRuinConfig, NoPlacementConfig> field_204028_A = Biome.createCompositeFeature(Feature.OCEAN_RUIN, new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.1F), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   public static final Map<CompositeFeature<?, ?>, GenerationStage.Decoration> field_202248_k = Util.make(Maps.newHashMap(), (p_209406_0_) -> {
      p_209406_0_.put(field_202250_m, GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
      p_209406_0_.put(field_202251_n, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(field_202252_o, GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
      p_209406_0_.put(field_202253_p, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(field_202254_q, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(field_202255_r, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(field_202256_s, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(field_204750_v, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(field_204028_A, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(field_202258_u, GenerationStage.Decoration.LOCAL_MODIFICATIONS);
      p_209406_0_.put(field_202259_v, GenerationStage.Decoration.LOCAL_MODIFICATIONS);
      p_209406_0_.put(field_202260_w, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(field_202261_x, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(field_202262_y, GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
      p_209406_0_.put(field_202257_t, GenerationStage.Decoration.SURFACE_STRUCTURES);
   });
   public static final Map<String, CompositeFeature<?, ?>[]> field_202247_j = Util.make(Maps.newHashMap(), (p_209404_0_) -> {
      p_209404_0_.put("mineshaft", new CompositeFeature[]{field_202250_m});
      p_209404_0_.put("village", new CompositeFeature[]{field_202251_n});
      p_209404_0_.put("stronghold", new CompositeFeature[]{field_202252_o});
      p_209404_0_.put("biome_1", new CompositeFeature[]{field_202253_p, field_202254_q, field_202255_r, field_202256_s, field_204028_A, field_204750_v});
      p_209404_0_.put("oceanmonument", new CompositeFeature[]{field_202257_t});
      p_209404_0_.put("lake", new CompositeFeature[]{field_202258_u});
      p_209404_0_.put("lava_lake", new CompositeFeature[]{field_202259_v});
      p_209404_0_.put("endcity", new CompositeFeature[]{field_202260_w});
      p_209404_0_.put("mansion", new CompositeFeature[]{field_202261_x});
      p_209404_0_.put("fortress", new CompositeFeature[]{field_202262_y});
   });
   public static final Map<CompositeFeature<?, ?>, IFeatureConfig> field_202249_l = Util.make(Maps.newHashMap(), (p_209405_0_) -> {
      p_209405_0_.put(field_202250_m, new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL));
      p_209405_0_.put(field_202251_n, new VillageConfig(0, VillagePieces.Type.OAK));
      p_209405_0_.put(field_202252_o, new StrongholdConfig());
      p_209405_0_.put(field_202253_p, new SwampHutConfig());
      p_209405_0_.put(field_202254_q, new DesertPyramidConfig());
      p_209405_0_.put(field_202255_r, new JunglePyramidConfig());
      p_209405_0_.put(field_202256_s, new IglooConfig());
      p_209405_0_.put(field_204028_A, new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.9F));
      p_209405_0_.put(field_204750_v, new ShipwreckConfig(false));
      p_209405_0_.put(field_202257_t, new OceanMonumentConfig());
      p_209405_0_.put(field_202260_w, new EndCityConfig());
      p_209405_0_.put(field_202261_x, new WoodlandMansionConfig());
      p_209405_0_.put(field_202262_y, new FortressConfig());
   });
   /** List of layers on this preset. */
   private final List<FlatLayerInfo> flatLayers = Lists.newArrayList();
   /** List of world features enabled on this preset. */
   private final Map<String, Map<String, String>> worldFeatures = Maps.newHashMap();
   private Biome biomeToUse;
   /** All states that are generated, one for each y value. */
   private final IBlockState[] states = new IBlockState[256];
   /** True if all generated blocks are air; false if at least one is not air. */
   private boolean allAir;
   private int field_202246_E;

   @Nullable
   public static Block func_212683_a(String p_212683_0_) {
      try {
         ResourceLocation resourcelocation = new ResourceLocation(p_212683_0_);
         if (IRegistry.field_212618_g.func_212607_c(resourcelocation)) {
            return IRegistry.field_212618_g.get(resourcelocation);
         }
      } catch (IllegalArgumentException illegalargumentexception) {
         LOGGER.warn("Invalid blockstate: {}", p_212683_0_, illegalargumentexception);
      }

      return null;
   }

   /**
    * Return the biome used on this preset.
    */
   public Biome getBiome() {
      return this.biomeToUse;
   }

   /**
    * Set the biome used on this preset.
    */
   public void setBiome(Biome biome) {
      this.biomeToUse = biome;
   }

   /**
    * Return the list of world features enabled on this preset.
    */
   public Map<String, Map<String, String>> getWorldFeatures() {
      return this.worldFeatures;
   }

   /**
    * Return the list of layers on this preset.
    */
   public List<FlatLayerInfo> getFlatLayers() {
      return this.flatLayers;
   }

   public void updateLayers() {
      int i = 0;

      for(FlatLayerInfo flatlayerinfo : this.flatLayers) {
         flatlayerinfo.setMinY(i);
         i += flatlayerinfo.getLayerCount();
      }

      this.field_202246_E = 0;
      this.allAir = true;
      i = 0;

      for(FlatLayerInfo flatlayerinfo1 : this.flatLayers) {
         for(int j = flatlayerinfo1.getMinY(); j < flatlayerinfo1.getMinY() + flatlayerinfo1.getLayerCount(); ++j) {
            IBlockState iblockstate = flatlayerinfo1.getLayerMaterial();
            if (iblockstate.getBlock() != Blocks.AIR) {
               this.allAir = false;
               this.states[j] = iblockstate;
            }
         }

         if (flatlayerinfo1.getLayerMaterial().getBlock() == Blocks.AIR) {
            i += flatlayerinfo1.getLayerCount();
         } else {
            this.field_202246_E += flatlayerinfo1.getLayerCount() + i;
            i = 0;
         }
      }

   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();

      for(int i = 0; i < this.flatLayers.size(); ++i) {
         if (i > 0) {
            stringbuilder.append(",");
         }

         stringbuilder.append(this.flatLayers.get(i));
      }

      stringbuilder.append(";");
      stringbuilder.append((Object)IRegistry.field_212624_m.getKey(this.biomeToUse));
      stringbuilder.append(";");
      if (!this.worldFeatures.isEmpty()) {
         int k = 0;

         for(Entry<String, Map<String, String>> entry : this.worldFeatures.entrySet()) {
            if (k++ > 0) {
               stringbuilder.append(",");
            }

            stringbuilder.append(entry.getKey().toLowerCase(Locale.ROOT));
            Map<String, String> map = entry.getValue();
            if (!map.isEmpty()) {
               stringbuilder.append("(");
               int j = 0;

               for(Entry<String, String> entry1 : map.entrySet()) {
                  if (j++ > 0) {
                     stringbuilder.append(" ");
                  }

                  stringbuilder.append(entry1.getKey());
                  stringbuilder.append("=");
                  stringbuilder.append(entry1.getValue());
               }

               stringbuilder.append(")");
            }
         }
      }

      return stringbuilder.toString();
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   private static FlatLayerInfo func_197526_a(String p_197526_0_, int p_197526_1_) {
      String[] astring = p_197526_0_.split("\\*", 2);
      int i;
      if (astring.length == 2) {
         try {
            i = MathHelper.clamp(Integer.parseInt(astring[0]), 0, 256 - p_197526_1_);
         } catch (NumberFormatException numberformatexception) {
            LOGGER.error("Error while parsing flat world string => {}", (Object)numberformatexception.getMessage());
            return null;
         }
      } else {
         i = 1;
      }

      Block block;
      try {
         block = func_212683_a(astring[astring.length - 1]);
      } catch (Exception exception) {
         LOGGER.error("Error while parsing flat world string => {}", (Object)exception.getMessage());
         return null;
      }

      if (block == null) {
         LOGGER.error("Error while parsing flat world string => Unknown block, {}", (Object)astring[astring.length - 1]);
         return null;
      } else {
         FlatLayerInfo flatlayerinfo = new FlatLayerInfo(i, block);
         flatlayerinfo.setMinY(p_197526_1_);
         return flatlayerinfo;
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static List<FlatLayerInfo> func_197527_b(String p_197527_0_) {
      List<FlatLayerInfo> list = Lists.newArrayList();
      String[] astring = p_197527_0_.split(",");
      int i = 0;

      for(String s : astring) {
         FlatLayerInfo flatlayerinfo = func_197526_a(s, i);
         if (flatlayerinfo == null) {
            return Collections.emptyList();
         }

         list.add(flatlayerinfo);
         i += flatlayerinfo.getLayerCount();
      }

      return list;
   }

   @OnlyIn(Dist.CLIENT)
   public <T> Dynamic<T> func_210834_a(DynamicOps<T> p_210834_1_) {
      T t = p_210834_1_.createList(this.flatLayers.stream().map((p_210837_1_) -> {
         return p_210834_1_.createMap(ImmutableMap.of(p_210834_1_.createString("height"), p_210834_1_.createInt(p_210837_1_.getLayerCount()), p_210834_1_.createString("block"), p_210834_1_.createString(IRegistry.field_212618_g.getKey(p_210837_1_.getLayerMaterial().getBlock()).toString())));
      }));
      T t1 = p_210834_1_.createMap(this.worldFeatures.entrySet().stream().map((p_210833_1_) -> {
         return Pair.of(p_210834_1_.createString(p_210833_1_.getKey().toLowerCase(Locale.ROOT)), p_210834_1_.createMap(p_210833_1_.getValue().entrySet().stream().map((p_210836_1_) -> {
            return Pair.of(p_210834_1_.createString(p_210836_1_.getKey()), p_210834_1_.createString(p_210836_1_.getValue()));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))));
      }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
      return new Dynamic<>(p_210834_1_, p_210834_1_.createMap(ImmutableMap.of(p_210834_1_.createString("layers"), t, p_210834_1_.createString("biome"), p_210834_1_.createString(IRegistry.field_212624_m.getKey(this.biomeToUse).toString()), p_210834_1_.createString("structures"), t1)));
   }

   public static FlatGenSettings createFlatGenerator(Dynamic<?> settings) {
      FlatGenSettings flatgensettings = ChunkGeneratorType.FLAT.createSettings();
      List<Pair<Integer, Block>> list = settings.get("layers").flatMap(Dynamic::getStream).orElse(Stream.empty()).map((p_210838_0_) -> {
         return Pair.of(p_210838_0_.getInt("height", 1), func_212683_a(p_210838_0_.getString("block")));
      }).collect(Collectors.toList());
      if (list.stream().anyMatch((p_211743_0_) -> {
         return p_211743_0_.getSecond() == null;
      })) {
         return getDefaultFlatGenerator();
      } else {
         List<FlatLayerInfo> list1 = list.stream().map((p_211740_0_) -> {
            return new FlatLayerInfo(p_211740_0_.getFirst(), p_211740_0_.getSecond());
         }).collect(Collectors.toList());
         if (list1.isEmpty()) {
            return getDefaultFlatGenerator();
         } else {
            flatgensettings.getFlatLayers().addAll(list1);
            flatgensettings.updateLayers();
            flatgensettings.setBiome(IRegistry.field_212624_m.func_212608_b(new ResourceLocation(settings.getString("biome"))));
            settings.get("structures").flatMap(Dynamic::getMapValues).ifPresent((p_211738_1_) -> {
               p_211738_1_.keySet().forEach((p_211739_1_) -> {
                  p_211739_1_.getStringValue().map((p_211742_1_) -> {
                     return flatgensettings.getWorldFeatures().put(p_211742_1_, Maps.newHashMap());
                  });
               });
            });
            return flatgensettings;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static FlatGenSettings createFlatGeneratorFromString(String flatGeneratorSettings) {
      Iterator<String> iterator = Splitter.on(';').split(flatGeneratorSettings).iterator();
      if (!iterator.hasNext()) {
         return getDefaultFlatGenerator();
      } else {
         FlatGenSettings flatgensettings = ChunkGeneratorType.FLAT.createSettings();
         List<FlatLayerInfo> list = func_197527_b(iterator.next());
         if (list.isEmpty()) {
            return getDefaultFlatGenerator();
         } else {
            flatgensettings.getFlatLayers().addAll(list);
            flatgensettings.updateLayers();
            Biome biome = iterator.hasNext() ? IRegistry.field_212624_m.func_212608_b(new ResourceLocation(iterator.next())) : null;
            flatgensettings.setBiome(biome == null ? Biomes.PLAINS : biome);
            if (iterator.hasNext()) {
               String[] astring = iterator.next().toLowerCase(Locale.ROOT).split(",");

               for(String s : astring) {
                  String[] astring1 = s.split("\\(", 2);
                  if (!astring1[0].isEmpty()) {
                     flatgensettings.func_202234_c(astring1[0]);
                     if (astring1.length > 1 && astring1[1].endsWith(")") && astring1[1].length() > 1) {
                        String[] astring2 = astring1[1].substring(0, astring1[1].length() - 1).split(" ");

                        for(String s1 : astring2) {
                           String[] astring3 = s1.split("=", 2);
                           if (astring3.length == 2) {
                              flatgensettings.func_202229_a(astring1[0], astring3[0], astring3[1]);
                           }
                        }
                     }
                  }
               }
            } else {
               flatgensettings.getWorldFeatures().put("village", Maps.newHashMap());
            }

            return flatgensettings;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void func_202234_c(String p_202234_1_) {
      Map<String, String> map = Maps.newHashMap();
      this.worldFeatures.put(p_202234_1_, map);
   }

   @OnlyIn(Dist.CLIENT)
   private void func_202229_a(String p_202229_1_, String p_202229_2_, String p_202229_3_) {
      this.worldFeatures.get(p_202229_1_).put(p_202229_2_, p_202229_3_);
      if ("village".equals(p_202229_1_) && "distance".equals(p_202229_2_)) {
         this.villageDistance = MathHelper.getInt(p_202229_3_, this.villageDistance, 9);
      }

      if ("biome_1".equals(p_202229_1_) && "distance".equals(p_202229_2_)) {
         this.biomeFeatureDistance = MathHelper.getInt(p_202229_3_, this.biomeFeatureDistance, 9);
      }

      if ("stronghold".equals(p_202229_1_)) {
         if ("distance".equals(p_202229_2_)) {
            this.strongholdDistance = MathHelper.getInt(p_202229_3_, this.strongholdDistance, 1);
         } else if ("count".equals(p_202229_2_)) {
            this.strongholdCount = MathHelper.getInt(p_202229_3_, this.strongholdCount, 1);
         } else if ("spread".equals(p_202229_2_)) {
            this.strongholdSpread = MathHelper.getInt(p_202229_3_, this.strongholdSpread, 1);
         }
      }

      if ("oceanmonument".equals(p_202229_1_)) {
         if ("separation".equals(p_202229_2_)) {
            this.oceanMonumentSeparation = MathHelper.getInt(p_202229_3_, this.oceanMonumentSeparation, 1);
         } else if ("spacing".equals(p_202229_2_)) {
            this.oceanMonumentSpacing = MathHelper.getInt(p_202229_3_, this.oceanMonumentSpacing, 1);
         }
      }

      if ("endcity".equals(p_202229_1_) && "distance".equals(p_202229_2_)) {
         this.endCityDistance = MathHelper.getInt(p_202229_3_, this.endCityDistance, 1);
      }

      if ("mansion".equals(p_202229_1_) && "distance".equals(p_202229_2_)) {
         this.mansionDistance = MathHelper.getInt(p_202229_3_, this.mansionDistance, 1);
      }

   }

   public static FlatGenSettings getDefaultFlatGenerator() {
      FlatGenSettings flatgensettings = ChunkGeneratorType.FLAT.createSettings();
      flatgensettings.setBiome(Biomes.PLAINS);
      flatgensettings.getFlatLayers().add(new FlatLayerInfo(1, Blocks.BEDROCK));
      flatgensettings.getFlatLayers().add(new FlatLayerInfo(2, Blocks.DIRT));
      flatgensettings.getFlatLayers().add(new FlatLayerInfo(1, Blocks.GRASS_BLOCK));
      flatgensettings.updateLayers();
      flatgensettings.getWorldFeatures().put("village", Maps.newHashMap());
      return flatgensettings;
   }

   /**
    * True if all generated blocks are air; false if at least one is not air.
    */
   public boolean isAllAir() {
      return this.allAir;
   }

   public IBlockState[] getStates() {
      return this.states;
   }
}