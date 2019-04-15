package com.mrburgerus.betaplus.world.beta_plus;

import net.minecraft.world.gen.ChunkGenSettings;

public class BetaPlusGenSettings extends ChunkGenSettings
{
	// Sea Level, self-explanatory
	private final int seaLevel = 64;
	// Deep Sea Threshold
	private final int seaDepth = 20;
	// Mountain Threshold, above this is Mountains
	private final int highAltitude = 112;
	// ODD NUMBER: Diameter of Guassian Kernel
	private final int oceanSmoothSize = 7;
	// Scale Size for Biomes (smaller value = bigger biomes)
	// BETA DEFAULT: 0.02500000037252903
	private double scaleVal = 0.015; //Modified for Biome Scale
	// Multiplier for Humidity
	// BETA DEFAULT: 2;
	private double multBiome = 1.75;

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
}
