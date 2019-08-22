package com.mrburgerus.betaplus.world.biome;

import com.mrburgerus.betaplus.util.ConfigRetroPlus;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

import java.util.ArrayList;

// New Biome Selector, works on a more sophisticated data set
public class BetaPlusBiomeSelectorNew
{
	// FIELDS //
	private static ArrayList<Biome> frozenBiomes; // Biomes below Frozen Threshold
	private static ArrayList<Biome> frozenHillBiomes; // Frozen Hilly Biomes
	private static ArrayList<Biome> coldBiomes; // Cold Biomes
	// Threshold values
	private static final double frozenThreshold = 0.1; // PLACEHOLDER
	private static final double coldThreshold = ConfigRetroPlus.coldTh;
	private static final double warmThreshold = 0.75; // PLACEHOLDER
	private static final double hotThreshold = 0.97; // PLACEHOLDER

	private static final double dryThreshold = 0.25; // PLACEHOLDER
	private static final double wetThreshold = 0.75; // PLACEHOLDER


	// METHODS //

	// selected is usually randomly generated by the Voronoi generator and passed in
	// isHilly determines which list to use
	public static Biome getBiome(double temperature, double humidity, double selected, TerrainType type)
	{
		Biome selectBiome = Biomes.BADLANDS_PLATEAU;
		// Switch between selections of biome based on the TerrainType Enum
		switch (type)
		{
			case land:
			{
				// Do some fun stuff regarding finding normal biomes
				selectBiome =  getLandBiome(temperature, humidity, selected);
				break;
			}
			case hillyLand:
			{
				// Get Hilly Biomes, which can be counterparts of normal biomes, or special types
				selectBiome = getHillBiome(temperature, humidity, selected);
				break;
			}
			case sea:
			{
				// Get typical ocean biomes.
				selectBiome = getOceanBiome(temperature, humidity, selected);
				break;
			}
			case deepSea:
			{
				// Get deeper ocean biomes based on temperature
				selectBiome = Biomes.DEEP_LUKEWARM_OCEAN; // TODO
				break;
			}
			case island:
			{
				// Islands are strange, and will most likely be selected entirely at random
				selectBiome = getIslandBiome(selected);
				break;
			}
			case generic:
			{
				// This is an issue.
				selectBiome = Biomes.DEFAULT;
				break;
			}
		}
		return selectBiome;
	}

	// Gets an Island Biome from the registered list.
	// Primarily, this gives Mushroom islands / Regular Biomes in vanilla
	public static Biome getIslandBiome(double selected)
	{
		// Multiply by length
		int selectIdx = (int) (selected * ConfigRetroPlus.islandBiomeList.size());
		return ConfigRetroPlus.islandBiomeList.get(selectIdx);
	}

	// HELPER METHODS //
	// These will look largely the same, and serve only to make my life simpler.

	// Gets a "Land" Type Biome
	private static Biome getLandBiome(double temperature, double humidity, double selected)
	{
		int selectIdx = 0;
		// If Frozen
		if (temperature < frozenThreshold)
		{
			selectIdx = (int) (selected * ConfigRetroPlus.frozenBiomesList.size());
			return ConfigRetroPlus.frozenBiomesList.get(selectIdx);
		}
		// If Extremely Hot
		else if (temperature > hotThreshold)
		{
			return Biomes.DESERT; // PLACEHOLDER
		}
		// Cold
		else if (temperature < coldThreshold)
		{
			// Dry, like a tundra
			if (humidity < dryThreshold)
			{
				selectIdx = 1; // PLACEHOLDER
				return Biomes.SNOWY_TUNDRA;
			}
			// Wet, like a swamp.
			else if (humidity > wetThreshold)
			{
				return Biomes.DARK_FOREST;
			}
			// Temperate, like a forest.
			else
			{
				return Biomes.TAIGA;
			}
		}
		// Warm
		else if (temperature > warmThreshold)
		{
			// Dry, like a tundra
			if (humidity < dryThreshold)
			{
				selectIdx = 1; // PLACEHOLDER
				return Biomes.SAVANNA;
			}
			// Wet, like a swamp.
			else if (humidity > wetThreshold)
			{
				return Biomes.JUNGLE;
			}
			// Temperate, like a forest.
			else
			{
				return Biomes.FOREST;
			}
		}
		// Temperate Biomes are selected last
		else
		{
			// Dry, like a tundra
			if (humidity < dryThreshold)
			{
				selectIdx = 1; // PLACEHOLDER
				return Biomes.SUNFLOWER_PLAINS;
			}
			// Wet, like a swamp.
			else if (humidity > wetThreshold)
			{
				return Biomes.FLOWER_FOREST;
			}
			// Temperate, like a forest.
			else
			{
				return Biomes.PLAINS;
			}
		}
	}

	private static Biome getHillBiome(double temperature, double humidity, double selected)
	{
		int selectIdx = 0;
		// If Frozen
		if (temperature < frozenThreshold)
		{
			selectIdx = (int) (selected * ConfigRetroPlus.frozenHillBiomesList.size());
			return ConfigRetroPlus.frozenHillBiomesList.get(selectIdx);
		}
		// If Extremely Hot
		else if (temperature > hotThreshold)
		{
			return Biomes.DESERT_HILLS; // PLACEHOLDER
		}
		// Cold
		else if (temperature < coldThreshold)
		{
			// Dry, like a tundra
			if (humidity < dryThreshold)
			{
				selectIdx = 1; // PLACEHOLDER
				return Biomes.SNOWY_MOUNTAINS;
			}
			// Wet, like a swamp.
			else if (humidity > wetThreshold)
			{
				return Biomes.DARK_FOREST_HILLS;
			}
			// Temperate, like a forest.
			else
			{
				return Biomes.TAIGA_HILLS;
			}
		}
		// Warm
		else if (temperature > warmThreshold)
		{
			// Dry, like a tundra
			if (humidity < dryThreshold)
			{
				selectIdx = 1; // PLACEHOLDER
				return Biomes.SAVANNA_PLATEAU;
			}
			// Wet, like a swamp.
			else if (humidity > wetThreshold)
			{
				return Biomes.BAMBOO_JUNGLE_HILLS;
			}
			// Temperate, like a forest.
			else
			{
				return Biomes.BIRCH_FOREST_HILLS;
			}
		}
		// Temperate Biomes are selected last
		else
		{
			// Dry, like a tundra
			if (humidity < dryThreshold)
			{
				selectIdx = 1; // PLACEHOLDER
				return Biomes.GRAVELLY_MOUNTAINS;
			}
			// Wet, like a swamp.
			else if (humidity > wetThreshold)
			{
				return Biomes.WOODED_MOUNTAINS;
			}
			// Temperate, like a forest.
			else
			{
				return Biomes.MOUNTAINS;
			}
		}
	}

	private static Biome getOceanBiome(double temperature, double humidity, double selected)
	{
		if (temperature < frozenThreshold)
		{
			return Biomes.FROZEN_OCEAN;
		}
		else if (temperature > hotThreshold)
		{
			return Biomes.WARM_OCEAN;
		}
		else
		{
			return Biomes.OCEAN;
		}
	}
}