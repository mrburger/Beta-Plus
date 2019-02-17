package com.mrburgerus.betaplus.world.biome;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;

public class BetaPlusSelectBiome
{
	// Fields
	static double coldValue = 0.5;
	static double frozenValue = 0.15;
	static double hotValue = 0.9375;
	static double veryHotVal = 0.985;


	// ORIGINAL GANGSTA
	public static Biome getBiome(float temperature, float humidity)
	{
		humidity *= temperature;
		if (temperature < coldValue)
		{
			if ((temperature < frozenValue))
			{
				return BiomeGenBetaPlus.iceSpikes.handle;
			}
			if (humidity < 0.2)
			{
				return BiomeGenBetaPlus.tundra.handle;
			}
			return BiomeGenBetaPlus.taiga.handle;
		}
		if (temperature < hotValue)
		{
			if (humidity < 0.2)
			{
				if (temperature > 0.8)
				{
					return BiomeGenBetaPlus.savanna.handle;
				}
				return BiomeGenBetaPlus.flowerPlains.handle;
			}
			if (humidity > 0.75)
			{
				return BiomeGenBetaPlus.swampland.handle;
			}
			if (humidity > 0.6)
			{
				return BiomeGenBetaPlus.seasonalForest.handle;
			}
			if (temperature < 0.7)
			{
				return BiomeGenBetaPlus.megaTaiga.handle;
			}
			if (humidity < 0.3)
			{
				if (humidity > 0.23)
				{
					return BiomeGenBetaPlus.birchForest.handle;
				}
				return BiomeGenBetaPlus.flowerForest.handle;
			}
			return BiomeGenBetaPlus.forest.handle;
		}
		if (humidity < 0.2)
		{
			if (temperature > veryHotVal && humidity <= 0.0025)
			{
				return BiomeGenBetaPlus.mesa.handle;
			}
			return BiomeGenBetaPlus.desert.handle;
		}
		if (humidity > 0.75D)
		{
			return BiomeGenBetaPlus.rainforest.handle;
		}
		return BiomeGenBetaPlus.plains.handle;
	}

}
