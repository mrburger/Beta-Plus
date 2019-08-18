package com.mrburgerus.betaplus.world.beta_plus;

import com.mrburgerus.betaplus.util.ConfigBetaPlus;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.OverworldGenSettings;

public class BetaPlusGenSettings extends OverworldGenSettings
{
	// Sea Level, self-explanatory
	private final int seaLevel = ConfigBetaPlus.seaLevel;
	// Deep Sea Threshold
	private final int seaDepth = ConfigBetaPlus.seaDepth;
	// Mountain Threshold, above this is Mountains
	private final int highAltitude = 112;
	// ODD NUMBER: Diameter of Guassian Kernel
	private final int oceanSmoothSize = ConfigBetaPlus.smoothSize;
	// Scale Size for Biomes (smaller value = bigger biomes)
	// BETA DEFAULT: 0.02500000037252903
	private double scaleVal = (1.0D / ConfigBetaPlus.biomeScale); //Modified for Biome Scale
	// Multiplier for Humidity
	// BETA DEFAULT: 2;
	private double multBiome = ConfigBetaPlus.humidityScale;

	public int getSeaLevel()
	{
		return seaLevel;
	}

	public int getSeaDepth()
	{
		return seaDepth;
	}

	public int getHighAltitude()
	{
		return highAltitude;
	}

	public int getOceanSmoothSize()
	{
		return oceanSmoothSize;
	}

	public double getScale()
	{
		return scaleVal;
	}

	public double getMultiplierBiome()
	{
		return multBiome;
	}

	@Override
	public int getVillageDistance()
	{
		return super.getVillageDistance();
	}
}
