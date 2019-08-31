package com.mrburgerus.betaplus.world.alpha_plus;

import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.util.ConfigRetroPlus;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.gen.GenerationSettings;

public class AlphaPlusGenSettings extends GenerationSettings
{
	private boolean isSnowy = false;
	private final int seaLevel = ConfigRetroPlus.seaLevel; // Had to be changed :(

	public void setSnowy(boolean snowy)
	{
		isSnowy = snowy;
	}

	public boolean getSnowy()
	{
		return isSnowy;
	}

	public int getSeaLevel()
	{
		return seaLevel;
	}

	// Re-enabled for 0.5
	public static AlphaPlusGenSettings createSettings(CompoundNBT inputSettings)
	{
		AlphaPlusGenSettings settingsOut = new AlphaPlusGenSettings();
		if (inputSettings.contains(WorldTypeAlphaPlus.SNOW_WORLD_TAG))
		{
			settingsOut.isSnowy = inputSettings.getBoolean(WorldTypeAlphaPlus.SNOW_WORLD_TAG);
		}
		return settingsOut;
	}
}
