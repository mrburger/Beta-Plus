package com.mrburgerus.betaplus.world.biome.support;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

import java.util.List;
import java.util.Optional;

public class Support
{
	// FIELDS //

	// Values are within [-0.5, 2.0]
	// This has to be big enough to select a few biomes
	private static final double INIT_TEMP = 0.5;
	private static final double INIT_HUMID = 0.25;
	// Smaller Values increase processing time.
	private static final double TEMP_INCREMENT = 0.2;
	private static final double HUMID_INCREMENT = 0.1;


	// List of Land Biomes and corresponding "hills" biomes, if they exist
	public static List<Pair<Biome, Optional<Biome>>> landBiomes;
	// Pair of Ocean Biomes, both normal and deep
	public static List<Pair<Biome, Biome>> oceanBiomes;
	// Mountain Biomes, these are specialty.
	public static List<Biome> mountainBiomes;
	// List of Beach & Coastal Biomes, could come in handy.
	public static List<Biome> coastBiomes;
	// List of Island Biomes
	public static List<Biome> islandBiomes;

	// METHODS //

	// Get a standard Biome from parameters
	public static Biome getBiomeFromParams(double temperature, double humidity, double selectNoise, List<Biome> candidates)
	{
		double tempThreshold = INIT_TEMP;
		double humidThreshold = INIT_HUMID;
		List<Biome> validCandidates = Lists.newArrayList();
		// Get closest match, expand search if no found
		while (validCandidates.size() < 1)
		{
			for (Biome b : candidates)
			{
				// Get Differences, if within range add to select.
				if (Math.abs(b.getDefaultTemperature() - temperature) <= tempThreshold && Math.abs(b.getDownfall() - humidity) <= humidThreshold)
				{
					validCandidates.add(b);
				}
			}
			tempThreshold += TEMP_INCREMENT;
			humidThreshold += HUMID_INCREMENT;
		}
		int randSelect = (int) (validCandidates.size() * selectNoise);
		return validCandidates.get(randSelect);
	}

	// Get a Land Biome and it's hill variant
	public static Pair<Biome, Optional<Biome>> getLandBiomeFromParams(double temperature, double humidity, double selectNoise, List<Pair<Biome, Optional<Biome>>> candidates)
	{
		double tempThreshold = INIT_TEMP;
		double humidThreshold = INIT_HUMID;
		List<Pair<Biome, Optional<Biome>>> validCandidates = Lists.newArrayList();
		// Get closest match
		while (validCandidates.size() < 1)
		{
			for (Pair<Biome, Optional<Biome>> b : candidates)
			{
				// Get Differences, if within range add to select.
				if (Math.abs(b.getFirst().getDefaultTemperature() - temperature) <= tempThreshold && Math.abs(b.getFirst().getDownfall() - humidity) <= humidThreshold)
				{
					validCandidates.add(b);
				}
			}
			tempThreshold += TEMP_INCREMENT;
			humidThreshold += HUMID_INCREMENT;
		}
		int randSelect = (int) (validCandidates.size() * selectNoise);
		return validCandidates.get(randSelect);
	}

	// Get an ocean Biome
	// TODO: FIX SINCE ALL OCEANS ARE THE SAME TEMP & DOWNFALL
	public static Pair<Biome, Biome> getOceanBiomePair(double temperature, double humidity, double selectNoise, List<Pair<Biome, Biome>> candidates)
	{
		double tempThreshold = INIT_TEMP;
		double humidThreshold = INIT_HUMID;
		List<Pair<Biome, Biome>> validCandidates = Lists.newArrayList();
		while (validCandidates.size() < 1)
		{
			for (Pair<Biome, Biome> b : candidates)
			{
				// Get Differences, if within range add to select.
				if (Math.abs(b.getFirst().getDefaultTemperature() - temperature) <= tempThreshold && Math.abs(b.getFirst().getDownfall() - humidity) <= humidThreshold)
				{
					validCandidates.add(b);
				}
			}
			tempThreshold += TEMP_INCREMENT;
			humidThreshold += HUMID_INCREMENT;
		}
		int randSelect = (int) (validCandidates.size() * selectNoise);
		return validCandidates.get(randSelect);
	}


	// DECLARATIONS //
	static
	{
		// Land Biomes, This will contain ALL Land Biomes and hills.
		landBiomes = Lists.newArrayList(
				Pair.of(Biomes.PLAINS, Optional.empty()),
				Pair.of(Biomes.DESERT, Optional.of(Biomes.DESERT_HILLS)),
				Pair.of(Biomes.FOREST, Optional.of(Biomes.FLOWER_FOREST)),
				Pair.of(Biomes.SNOWY_TAIGA, Optional.of(Biomes.SNOWY_TAIGA_HILLS)),
				Pair.of(Biomes.SWAMP, Optional.of(Biomes.SWAMP_HILLS)),
				Pair.of(Biomes.JUNGLE, Optional.of(Biomes.JUNGLE_HILLS)),
				Pair.of(Biomes.BIRCH_FOREST, Optional.of(Biomes.BIRCH_FOREST_HILLS)),
				Pair.of(Biomes.DARK_FOREST, Optional.of(Biomes.DARK_FOREST_HILLS)),
				Pair.of(Biomes.SAVANNA, Optional.of(Biomes.SHATTERED_SAVANNA)), // Test the shattered.
				Pair.of(Biomes.SUNFLOWER_PLAINS, Optional.empty()),
				Pair.of(Biomes.TALL_BIRCH_FOREST, Optional.of(Biomes.TALL_BIRCH_HILLS)),
				Pair.of(Biomes.GIANT_SPRUCE_TAIGA, Optional.of(Biomes.GIANT_SPRUCE_TAIGA_HILLS)),
				Pair.of(Biomes.BAMBOO_JUNGLE, Optional.of(Biomes.BAMBOO_JUNGLE_HILLS))
		);

		oceanBiomes = Lists.newArrayList(
				Pair.of(Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN),
				Pair.of(Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN),
				Pair.of(Biomes.OCEAN, Biomes.DEEP_OCEAN),
				Pair.of(Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN),
				Pair.of(Biomes.WARM_OCEAN, Biomes.DEEP_WARM_OCEAN)
		);

		mountainBiomes = Lists.newArrayList(
				Biomes.SNOWY_MOUNTAINS,
				//Biomes.MOUNTAINS,
				Biomes.WOODED_MOUNTAINS
		);

		coastBiomes = Lists.newArrayList(
				Biomes.BEACH,
				Biomes.SNOWY_BEACH
		);

		islandBiomes = Lists.newArrayList(
				Biomes.MUSHROOM_FIELDS
		);

		// Try to initialize others
		BOPSupport.init();
		}
}
