package com.mrburgerus.betaplus.world.beta_plus;

import net.minecraft.world.gen.ChunkGenSettings;

public class BetaPlusGenSettings extends ChunkGenSettings
{
	private final int seaLevel = 63;
	private final int seaDepth = 16;
	private final int highAltitude = 112;
	private final int oceanSmoothSize = 7;

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
}
