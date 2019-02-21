package com.mrburgerus.betaplus.world.biome.layer;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.IContextExtended;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.layer.*;

import java.util.function.LongFunction;

import static net.minecraft.world.gen.layer.LayerUtil.getModdedBiomeSize;
import static net.minecraft.world.gen.layer.LayerUtil.repeat;

/* Modified LayerUtil, for Beta+ */
public class LayerUtilBetaPlus
{
	/* Modified for Beta */
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
		int biomeSize = 4;
		int RiverSize = biomeSize;
		if (settings != null) {
			biomeSize = settings.getBiomeSize();
			RiverSize = settings.getRiverSize();
		}

		if (worldTypeIn == WorldType.LARGE_BIOMES) {
			biomeSize = 6;
		}

		biomeSize = getModdedBiomeSize(worldTypeIn, biomeSize);

		IAreaFactory<T> areaFactory2 = repeat(1000L, GenLayerZoom.NORMAL, iareafactory, 0, contextFactory);
		areaFactory2 = GenLayerRiverInit.INSTANCE.apply((IContextExtended)contextFactory.apply(100L), areaFactory2);
		IAreaFactory<T> lvt_8_1_ = worldTypeIn.getBiomeLayer(iareafactory, settings, contextFactory);
		IAreaFactory<T> lvt_9_1_ = repeat(1000L, GenLayerZoom.NORMAL, areaFactory2, 2, contextFactory);
		lvt_8_1_ = GenLayerHills.INSTANCE.apply((IContextExtended)contextFactory.apply(1000L), lvt_8_1_, lvt_9_1_);
		areaFactory2 = repeat(1000L, GenLayerZoom.NORMAL, areaFactory2, 2, contextFactory);
		areaFactory2 = repeat(1000L, GenLayerZoom.NORMAL, areaFactory2, RiverSize, contextFactory);
		areaFactory2 = GenLayerRiver.INSTANCE.apply((IContextExtended)contextFactory.apply(1L), areaFactory2);
		areaFactory2 = GenLayerSmooth.INSTANCE.apply((IContextExtended)contextFactory.apply(1000L), areaFactory2);
		lvt_8_1_ = GenLayerRareBiome.INSTANCE.apply((IContextExtended)contextFactory.apply(1001L), lvt_8_1_);

		for(int k = 0; k < biomeSize; ++k) {
			lvt_8_1_ = GenLayerZoom.NORMAL.apply((IContextExtended)contextFactory.apply((long)(1000 + k)), lvt_8_1_);
			if (k == 0) {
				lvt_8_1_ = GenLayerAddIsland.INSTANCE.apply((IContextExtended)contextFactory.apply(3L), lvt_8_1_);
			}

			if (k == 1 || biomeSize == 1) {
				lvt_8_1_ = GenLayerShore.INSTANCE.apply((IContextExtended)contextFactory.apply(1000L), lvt_8_1_);
			}
		}

		lvt_8_1_ = GenLayerSmooth.INSTANCE.apply((IContextExtended)contextFactory.apply(1000L), lvt_8_1_);
		lvt_8_1_ = GenLayerRiverMix.INSTANCE.apply((IContextExtended)contextFactory.apply(100L), lvt_8_1_, areaFactory2);
		lvt_8_1_ = GenLayerMixOceans.INSTANCE.apply(contextFactory.apply(100L), lvt_8_1_, iareafactory1);
		IAreaFactory<T> iareafactory5 = GenLayerVoronoiZoom.INSTANCE.apply(contextFactory.apply(10L), lvt_8_1_);
		return ImmutableList.of(lvt_8_1_, iareafactory5, lvt_8_1_);
	}

}
