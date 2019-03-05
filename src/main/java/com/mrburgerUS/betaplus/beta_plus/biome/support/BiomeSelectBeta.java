package com.mrburgerUS.betaplus.beta_plus.biome.support;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;

public class BiomeSelectBeta implements IBiomeSelect
{
	public static Biome getBiome(float temperature, float humidity)
	{
		humidity *= temperature;
		if (temperature < 0.5)
		{
			if ((temperature < 0.1))
			{
				return Biomes.MUTATED_ICE_FLATS;
			}
			if (humidity < 0.2)
			{
				return Biomes.ICE_PLAINS;
			}
			return Biomes.COLD_TAIGA;
		}
		if (temperature < 0.9375)
		{
			if (humidity < 0.2)
			{
				if (temperature > 0.8)
				{
					return Biomes.SAVANNA;
				}
				return Biomes.PLAINS;
			}
			if (humidity > 0.7)
			{
				return Biomes.SWAMPLAND;
			}
			if (humidity > 0.6)
			{
				return Biomes.ROOFED_FOREST;
			}
			if (temperature < 0.7)
			{
				return Biomes.REDWOOD_TAIGA;
			}
			if (humidity < 0.3)
			{
				if (humidity > 0.23)
				{
					return Biomes.BIRCH_FOREST;
				}
				return Biomes.MUTATED_FOREST;
			}
			return Biomes.FOREST;
		}
		if (humidity < 0.2)
		{
			if (temperature > 0.98 && humidity == 0)
			{
				return Biomes.MESA;
			}
			return Biomes.DESERT;
		}
		if (humidity > 0.75D)
		{
			return Biomes.JUNGLE;
		}
		return Biomes.MUTATED_PLAINS;
	}

	/*
	//Original, Ripped from Beta.
	public static EnumBetaBiome getBiomeOld(float temperature, float humidity)
	{
		return temperature < 0.1f ? tundra : (humidity < 0.2f ? (temperature < 0.5f ? tundra : (temperature < 0.95f ? savanna : desert)) : (humidity > 0.5f && temperature < 0.7f ? swampland : (temperature < 0.5f ? taiga : (temperature < 0.97f ? (humidity < 0.35f ? shrubland : forest) : (humidity < 0.45f ? plains : ((humidity *= temperature) < 0.9f ? seasonalForest : rainforest))))));
	}


	// Old, used in 0.2
	public static EnumBetaBiome getBiomeNew(float temperature, float humidity)
	{
		EnumBetaBiome betaBiome;
		if (temperature < 0.1)
		{
			betaBiome = iceSpikes;
		}
		else if (humidity < 0.15 && temperature < 0.5)
		{
			betaBiome = tundra;
		}
		else if (humidity < 0.2 && temperature < 0.95)
		{
			betaBiome = savanna;
		}
		else if (humidity < 0.2 && temperature > 0.95)
		{
			betaBiome = desert;
		}
		else if (humidity > 0.5 && temperature < 0.45)
		{
			betaBiome = roofForest;
		}
		else if (humidity > 0.6 && temperature < 0.7)
		{
			betaBiome = swampland;
		}
		else if (humidity > 0.5 && temperature < 0.5)
		{
			betaBiome = taiga;
		}
		else if (humidity > 0.5 && temperature < 0.65)
		{
			betaBiome = megaTaiga;
		}
		else if (temperature < 0.97 && humidity < 0.35)
		{
			betaBiome = shrubland;
		}
		else if (temperature < 0.97)
		{
			betaBiome = forest;
		}
		else if (humidity < 0.45)
		{
			betaBiome = plains;
		}
		else if ((humidity * temperature) < 0.9)
		{
			betaBiome = seasonalForest;
		}
		else
		{
			betaBiome = rainforest;
		}
		return betaBiome;
	}

	public static EnumBetaBiome getBiomeBeta(Biome biome)
	{
		for (EnumBetaBiome biomeBeta : EnumBetaBiome.values())
		{
			if (biomeBeta.handle == biome)
				return biomeBeta;
		}
		return EnumBetaBiome.plains;
	}


	//0.2.1
	public static EnumBetaBiome getBiomeBetaPlus(float temperature, float humidity)
	{
		humidity *= temperature;
		if (temperature < 0.1)
		{
			return iceSpikes;
		}
		//Mesas? In MY house?
		else if (temperature > 0.9825 && humidity < 0.02)
		{
			return mesa;
		}
		else if (humidity < 0.2)
		{
			if (temperature < 0.5)
			{
				return tundra;
			}
			else if (temperature < 0.9)
			{
				return savanna;
			}
			else
				return desert;
		}
		else if (humidity > 0.55 && temperature < 0.7)
		{
			if (temperature < 0.65)
			{
				return roofForest;
			}
			else
				return swampland;
		}
		else if (temperature < 0.5)
		{
			if (humidity < 0.35)
				return taiga;
			else
				return megaTaiga;
		}
		else if (temperature < 0.97)
		{
			if (humidity < 0.35)
			{
				return plains;
			}
			else
			{
				return forest;
			}
		}
		else if (humidity < 0.45)
		{
			return plains;
		}
		else if (humidity < 0.9)
		{
			return seasonalForest;
		}
		else
		{
			return rainforest;
		}
	}

	//0.2.1, Needs TWEAKING
	private static EnumBetaBiome getBiome3(double temperature, double humidity)
	{
		humidity *= temperature;
		if (temperature < 0.5)
		{
			if ((temperature < 0.1))
			{
				return iceSpikes;
			}
			if (humidity < 0.2)
			{
				return tundra;
			}
			return taiga;
		}
		if (temperature < 0.9375)
		{
			if (humidity < 0.2)
			{
				if (temperature > 0.8)
				{
					return savanna;
				}
				return plains;
			}
			if (humidity > 0.7)
			{
				return swampland;
			}
			if (humidity > 0.6)
			{
				return roofForest;
			}
			if (temperature < 0.7)
			{
				return megaTaiga;
			}
			if (humidity < 0.3)
			{
				if (humidity > 0.23)
				{
					return birchForest;
				}
				return seasonalForest;
			}
			return forest;
		}
		if (humidity < 0.2)
		{
			if (temperature > 0.98 && humidity < 0.02)
			{
				return mesa;
			}
			return desert;
		}
		if (humidity > 0.75D)
		{
			return rainforest;
		}
		return plains;
	}
	*/
}
