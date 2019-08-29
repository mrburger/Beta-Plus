package com.mrburgerus.betaplus.world.beta_plus;

import com.mrburgerus.betaplus.util.ConfigRetroPlus;
import com.mrburgerus.betaplus.world.alpha_plus.AlphaPlusGenSettings;
import com.mrburgerus.betaplus.world.biome.AbstractBiomeSelector;
import com.mrburgerus.betaplus.world.biome.BetaPlusBiomeSelectorNew;
import com.mrburgerus.betaplus.world.biome.BiomeSelectorBeta;
import com.mrburgerus.betaplus.world.biome.BiomeSelectorBetaPlus;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.gen.OverworldGenSettings;

public class BetaPlusGenSettings extends OverworldGenSettings
{
	// Sea Level, self-explanatory
	private final int seaLevel = ConfigRetroPlus.seaLevel;
	// Deep Sea Threshold
	private final int seaDepth = ConfigRetroPlus.seaDepth;
	// Mountain Threshold, above this is Mountains
	private final int highAltitude = 112;
	// ODD NUMBER: Diameter of Guassian Kernel
	private final int oceanSmoothSize = ConfigRetroPlus.smoothSize;
	// Scale Size for Biomes (smaller value = bigger biomes)
	// BETA DEFAULT: 0.02500000037252903
	private double scaleVal = (1.0D / ConfigRetroPlus.biomeScale); //Modified for Biome Scale
	// Multiplier for Humidity
	// BETA DEFAULT: 2;
	private double multBiome = ConfigRetroPlus.humidityScale;
	// Biome Selector to use, in the future this will be selectable.
	private AbstractBiomeSelector biomeSelector = new BiomeSelectorBeta();
	// Cave carver to use
	private boolean useOldCaves = true;

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

	public AbstractBiomeSelector getBiomeSelector()
	{
		return biomeSelector;
	}

	public boolean isUseOldCaves()
	{
		return useOldCaves;
	}

	@Override
	public int getVillageDistance()
	{
		return super.getVillageDistance();
	}

	// Create settings
	public static BetaPlusGenSettings createSettings(CompoundNBT nbtSettings)
	{
		BetaPlusGenSettings settingsOut = new BetaPlusGenSettings();
		// For each value we care about, check if exists
		if (nbtSettings.contains(WorldTypeBetaPlus.OLD_CAVES_TAG))
		{
			settingsOut.useOldCaves = nbtSettings.getBoolean(WorldTypeBetaPlus.OLD_CAVES_TAG);
		}
		if (nbtSettings.contains(WorldTypeBetaPlus.BIOME_PROVIDER_TAG))
		{
			AbstractBiomeSelector selector;
			switch (nbtSettings.getInt(WorldTypeBetaPlus.BIOME_PROVIDER_TAG))
			{
				// Use True Beta
				case 0:
					selector = new BiomeSelectorBeta();
					break;
				case 1:
					// Modified
					selector = new BiomeSelectorBetaPlus();
					break;
				default:
					selector = new BiomeSelectorBeta();
			}
			settingsOut.biomeSelector = selector;
		}

		return settingsOut;
	}
}
