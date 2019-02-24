package com.mrburgerus.betaplus.world.biome;

import net.minecraft.world.biome.Biome;

public class BetaPlusSelectBiome
{
	// Fields
	public static final double COLD_VALUE = 0.5;
	public static final double FROZEN_VALUE = 0.15;
	public static final double HOT_VALUE = 0.9375;
	public static final double VERY_HOT_VAL = 0.985;
	public static final double WARM_VAL = 0.8;


	// ORIGINAL GANGSTA
	public static Biome getBiome(float temperature, float humidity)
	{
		humidity *= temperature;
		if (temperature < COLD_VALUE)
		{
			if ((temperature < FROZEN_VALUE))
			{
				return BiomeGenBetaPlus.iceSpikes.handle;
			}
			if (humidity < 0.2)
			{
				return BiomeGenBetaPlus.tundra.handle;
			}
			return BiomeGenBetaPlus.taiga.handle;
		}
		if (temperature < HOT_VALUE)
		{
			if (humidity < 0.2)
			{
				if (temperature > WARM_VAL)
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
			if (temperature < 0.675) // Modified
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
			if (temperature > VERY_HOT_VAL && humidity <= 0.0025)
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
