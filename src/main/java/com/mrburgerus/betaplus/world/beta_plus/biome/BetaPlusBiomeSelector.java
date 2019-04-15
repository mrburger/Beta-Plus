package com.mrburgerus.betaplus.world.beta_plus.biome;

import net.minecraft.world.biome.Biome;

public class BetaPlusBiomeSelector
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
				return EnumBetaPlusBiome.iceSpikes.handle;
			}
			if (humidity < 0.2)
			{
				return EnumBetaPlusBiome.tundra.handle;
			}
			return EnumBetaPlusBiome.taiga.handle;
		}
		if (temperature < HOT_VALUE)
		{
			if (humidity < 0.2)
			{
				if (temperature > WARM_VAL)
				{
					return EnumBetaPlusBiome.savanna.handle;
				}
				return EnumBetaPlusBiome.flowerPlains.handle;
			}
			if (humidity > 0.75)
			{
				return EnumBetaPlusBiome.swampland.handle;
			}
			if (humidity > 0.6)
			{
				return EnumBetaPlusBiome.seasonalForest.handle;
			}
			if (temperature < 0.675) // Modified
			{
				return EnumBetaPlusBiome.megaTaiga.handle;
			}
			if (humidity < 0.3)
			{
				if (humidity > 0.23)
				{
					return EnumBetaPlusBiome.birchForest.handle;
				}
				return EnumBetaPlusBiome.flowerForest.handle;
			}
			return EnumBetaPlusBiome.forest.handle;
		}
		if (humidity < 0.2)
		{
			if (temperature > VERY_HOT_VAL && humidity <= 0.0025)
			{
				return EnumBetaPlusBiome.mesa.handle;
			}
			return EnumBetaPlusBiome.desert.handle;
		}
		if (humidity > 0.75D)
		{
			return EnumBetaPlusBiome.rainforest.handle;
		}
		return EnumBetaPlusBiome.plains.handle;
	}


}
