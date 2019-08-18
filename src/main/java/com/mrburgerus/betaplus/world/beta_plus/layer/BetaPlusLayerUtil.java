package com.mrburgerus.betaplus.world.beta_plus.layer;

import com.google.common.collect.ImmutableList;
import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.world.beta_plus.BetaPlusGenSettings;
import com.mrburgerus.betaplus.world.beta_plus.sim.BetaPlusSimulator;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.BiomeLayer;
import net.minecraft.world.gen.layer.IslandLayer;
import net.minecraft.world.gen.layer.Layer;
import net.minecraft.world.gen.layer.ZoomLayer;

import java.util.function.LongFunction;

// A new class to handle the Biomes
// Still figuring it out
// Yee Haw.
public class BetaPlusLayerUtil
{
	// FIELDS //

	// METHODS //

	// This method does some wacky stuff.
	public static Layer[] createGenLayers(long seed, World world, OverworldGenSettings settings)
	{
		ImmutableList<IAreaFactory<LazyArea>> factoryList = createAreaFactories(world, settings, (seedModifier) ->
				new LazyAreaLayerContext(1, seed, seedModifier));

		Layer biomesLayer = new Layer(factoryList.get(0));
		Layer voroniZoomBiomesLayer = new Layer(factoryList.get(1));
		Layer biomesLayer2 = new Layer(factoryList.get(2));
		return new Layer[]{biomesLayer, voroniZoomBiomesLayer, biomesLayer2};
	}

	public static <T extends IArea, C extends IExtendedNoiseRandom<T>> ImmutableList<IAreaFactory<T>> createAreaFactories(World world, OverworldGenSettings settings, LongFunction<C> contextFactory)
	{
		// Initially, we only need to know Land, Sea, and Island
		IAreaFactory<T> landSeaFactory = createLandAndSeaFactory(contextFactory, world);
		return ImmutableList.of(landSeaFactory);
	}

	// Since the world is generated and we just superimpose Biomes on top, simulate the world and find Blobs (Islands), hilly areas, etc.
	public static <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> createLandAndSeaFactory(LongFunction<C> context, World world)
	{
		// I do not fully understand this
		IAreaFactory<T> factory = IslandLayer.INSTANCE.apply(context.apply(1L));
		// Find land and sea values
		BetaPlusSimulator simulatorI = new BetaPlusSimulator(world);

		return factory;
	}


//	public static Layer buildOverworldBiomeLayer(World world, BetaPlusSimulator simulator, BetaPlusGenSettings settings)
//	{
//		// First, get the simulated Y Height, this will determine sea placement.
//		return new BiomeLayer(world.getWorldType(), );
//	}
}
