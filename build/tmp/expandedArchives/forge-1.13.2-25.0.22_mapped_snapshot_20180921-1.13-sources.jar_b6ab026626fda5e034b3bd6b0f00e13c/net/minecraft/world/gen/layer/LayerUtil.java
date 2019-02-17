package net.minecraft.world.gen.layer;

import com.google.common.collect.ImmutableList;
import java.util.function.LongFunction;
import net.minecraft.init.Biomes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IContextExtended;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;

public class LayerUtil {
   protected static final int WARM_OCEAN = IRegistry.field_212624_m.getId(Biomes.WARM_OCEAN);
   protected static final int LUKEWARM_OCEAN = IRegistry.field_212624_m.getId(Biomes.LUKEWARM_OCEAN);
   protected static final int OCEAN = IRegistry.field_212624_m.getId(Biomes.OCEAN);
   protected static final int COLD_OCEAN = IRegistry.field_212624_m.getId(Biomes.COLD_OCEAN);
   protected static final int FROZEN_OCEAN = IRegistry.field_212624_m.getId(Biomes.FROZEN_OCEAN);
   protected static final int DEEP_WARM_OCEAN = IRegistry.field_212624_m.getId(Biomes.DEEP_WARM_OCEAN);
   protected static final int DEEP_LUKEWARM_OCEAN = IRegistry.field_212624_m.getId(Biomes.DEEP_LUKEWARM_OCEAN);
   protected static final int DEEP_OCEAN = IRegistry.field_212624_m.getId(Biomes.DEEP_OCEAN);
   protected static final int DEEP_COLD_OCEAN = IRegistry.field_212624_m.getId(Biomes.DEEP_COLD_OCEAN);
   protected static final int DEEP_FROZEN_OCEAN = IRegistry.field_212624_m.getId(Biomes.DEEP_FROZEN_OCEAN);

   public static <T extends IArea, C extends IContextExtended<T>> IAreaFactory<T> repeat(long seed, IAreaTransformer1 parent, IAreaFactory<T> p_202829_3_, int count, LongFunction<C> contextFactory) {
      IAreaFactory<T> iareafactory = p_202829_3_;

      for(int i = 0; i < count; ++i) {
         iareafactory = parent.apply(contextFactory.apply(seed + (long)i), iareafactory);
      }

      return iareafactory;
   }

   public static <T extends IArea, C extends IContextExtended<T>> ImmutableList<IAreaFactory<T>> buildOverworldProcedure(WorldType worldTypeIn, OverworldGenSettings settings, LongFunction<C> contextFactory) {
      IAreaFactory<T> iareafactory = GenLayerIsland.INSTANCE.apply(contextFactory.apply(1L));
      iareafactory = GenLayerZoom.FUZZY.apply(contextFactory.apply(2000L), iareafactory);
      iareafactory = GenLayerAddIsland.INSTANCE.apply(contextFactory.apply(1L), iareafactory);
      iareafactory = GenLayerZoom.NORMAL.apply(contextFactory.apply(2001L), iareafactory);
      iareafactory = GenLayerAddIsland.INSTANCE.apply(contextFactory.apply(2L), iareafactory);
      iareafactory = GenLayerAddIsland.INSTANCE.apply(contextFactory.apply(50L), iareafactory);
      iareafactory = GenLayerAddIsland.INSTANCE.apply(contextFactory.apply(70L), iareafactory);
      iareafactory = GenLayerRemoveTooMuchOcean.INSTANCE.apply(contextFactory.apply(2L), iareafactory);
      IAreaFactory<T> iareafactory1 = OceanLayer.INSTANCE.apply(contextFactory.apply(2L));
      iareafactory1 = repeat(2001L, GenLayerZoom.NORMAL, iareafactory1, 6, contextFactory);
      iareafactory = GenLayerAddSnow.INSTANCE.apply(contextFactory.apply(2L), iareafactory);
      iareafactory = GenLayerAddIsland.INSTANCE.apply(contextFactory.apply(3L), iareafactory);
      iareafactory = GenLayerEdge.CoolWarm.INSTANCE.apply(contextFactory.apply(2L), iareafactory);
      iareafactory = GenLayerEdge.HeatIce.INSTANCE.apply(contextFactory.apply(2L), iareafactory);
      iareafactory = GenLayerEdge.Special.INSTANCE.apply(contextFactory.apply(3L), iareafactory);
      iareafactory = GenLayerZoom.NORMAL.apply(contextFactory.apply(2002L), iareafactory);
      iareafactory = GenLayerZoom.NORMAL.apply(contextFactory.apply(2003L), iareafactory);
      iareafactory = GenLayerAddIsland.INSTANCE.apply(contextFactory.apply(4L), iareafactory);
      iareafactory = GenLayerAddMushroomIsland.INSTANCE.apply(contextFactory.apply(5L), iareafactory);
      iareafactory = GenLayerDeepOcean.INSTANCE.apply(contextFactory.apply(4L), iareafactory);
      iareafactory = repeat(1000L, GenLayerZoom.NORMAL, iareafactory, 0, contextFactory);
      int i = 4;
      int j = i;
      if (settings != null) {
         i = settings.getBiomeSize();
         j = settings.getRiverSize();
      }

      if (worldTypeIn == WorldType.LARGE_BIOMES) {
         i = 6;
      }

      i = getModdedBiomeSize(worldTypeIn, i);

      IAreaFactory<T> lvt_7_1_ = repeat(1000L, GenLayerZoom.NORMAL, iareafactory, 0, contextFactory);
      lvt_7_1_ = GenLayerRiverInit.INSTANCE.apply((IContextExtended)contextFactory.apply(100L), lvt_7_1_);
      IAreaFactory<T> lvt_8_1_ = worldTypeIn.getBiomeLayer(iareafactory, settings, contextFactory);
      IAreaFactory<T> lvt_9_1_ = repeat(1000L, GenLayerZoom.NORMAL, lvt_7_1_, 2, contextFactory);
      lvt_8_1_ = GenLayerHills.INSTANCE.apply((IContextExtended)contextFactory.apply(1000L), lvt_8_1_, lvt_9_1_);
      lvt_7_1_ = repeat(1000L, GenLayerZoom.NORMAL, lvt_7_1_, 2, contextFactory);
      lvt_7_1_ = repeat(1000L, GenLayerZoom.NORMAL, lvt_7_1_, j, contextFactory);
      lvt_7_1_ = GenLayerRiver.INSTANCE.apply((IContextExtended)contextFactory.apply(1L), lvt_7_1_);
      lvt_7_1_ = GenLayerSmooth.INSTANCE.apply((IContextExtended)contextFactory.apply(1000L), lvt_7_1_);
      lvt_8_1_ = GenLayerRareBiome.INSTANCE.apply((IContextExtended)contextFactory.apply(1001L), lvt_8_1_);

      for(int k = 0; k < i; ++k) {
         lvt_8_1_ = GenLayerZoom.NORMAL.apply((IContextExtended)contextFactory.apply((long)(1000 + k)), lvt_8_1_);
         if (k == 0) {
            lvt_8_1_ = GenLayerAddIsland.INSTANCE.apply((IContextExtended)contextFactory.apply(3L), lvt_8_1_);
         }

         if (k == 1 || i == 1) {
            lvt_8_1_ = GenLayerShore.INSTANCE.apply((IContextExtended)contextFactory.apply(1000L), lvt_8_1_);
         }
      }

      lvt_8_1_ = GenLayerSmooth.INSTANCE.apply((IContextExtended)contextFactory.apply(1000L), lvt_8_1_);
      lvt_8_1_ = GenLayerRiverMix.INSTANCE.apply((IContextExtended)contextFactory.apply(100L), lvt_8_1_, lvt_7_1_);
      lvt_8_1_ = GenLayerMixOceans.INSTANCE.apply(contextFactory.apply(100L), lvt_8_1_, iareafactory1);
      IAreaFactory<T> iareafactory5 = GenLayerVoronoiZoom.INSTANCE.apply(contextFactory.apply(10L), lvt_8_1_);
      return ImmutableList.of(lvt_8_1_, iareafactory5, lvt_8_1_);
   }

   public static GenLayer[] buildOverworldProcedure(long seed, WorldType typeIn, OverworldGenSettings settings) {
      int i = 1;
      int[] aint = new int[1];
      ImmutableList<IAreaFactory<LazyArea>> immutablelist = buildOverworldProcedure(typeIn, settings, (p_202825_3_) -> {
         ++aint[0];
         return new LazyAreaLayerContext(1, aint[0], seed, p_202825_3_);
      });
      GenLayer genlayer = new GenLayer(immutablelist.get(0));
      GenLayer genlayer1 = new GenLayer(immutablelist.get(1));
      GenLayer genlayer2 = new GenLayer(immutablelist.get(2));
      return new GenLayer[]{genlayer, genlayer1, genlayer2};
   }

   public static boolean areBiomesSimilar(int p_202826_0_, int p_202826_1_) {
      if (p_202826_0_ == p_202826_1_) {
         return true;
      } else {
         Biome biome = IRegistry.field_212624_m.get(p_202826_0_);
         Biome biome1 = IRegistry.field_212624_m.get(p_202826_1_);
         if (biome != null && biome1 != null) {
            if (biome != Biomes.WOODED_BADLANDS_PLATEAU && biome != Biomes.BADLANDS_PLATEAU) {
               if (biome.getCategory() != Biome.Category.NONE && biome1.getCategory() != Biome.Category.NONE && biome.getCategory() == biome1.getCategory()) {
                  return true;
               } else {
                  return biome == biome1;
               }
            } else {
               return biome1 == Biomes.WOODED_BADLANDS_PLATEAU || biome1 == Biomes.BADLANDS_PLATEAU;
            }
         } else {
            return false;
         }
      }
   }

   /* ======================================== FORGE START =====================================*/
   public static int getModdedBiomeSize(net.minecraft.world.WorldType worldType, int original)
   {
       net.minecraftforge.event.terraingen.WorldTypeEvent.BiomeSize event = new net.minecraftforge.event.terraingen.WorldTypeEvent.BiomeSize(worldType, original);
       net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
       return event.getNewSize();
   }
   /* ========================================= FORGE END ======================================*/

   protected static boolean isOcean(int biomeIn) {
      return biomeIn == WARM_OCEAN || biomeIn == LUKEWARM_OCEAN || biomeIn == OCEAN || biomeIn == COLD_OCEAN || biomeIn == FROZEN_OCEAN || biomeIn == DEEP_WARM_OCEAN || biomeIn == DEEP_LUKEWARM_OCEAN || biomeIn == DEEP_OCEAN || biomeIn == DEEP_COLD_OCEAN || biomeIn == DEEP_FROZEN_OCEAN;
   }

   protected static boolean isShallowOcean(int biomeIn) {
      return biomeIn == WARM_OCEAN || biomeIn == LUKEWARM_OCEAN || biomeIn == OCEAN || biomeIn == COLD_OCEAN || biomeIn == FROZEN_OCEAN;
   }
}