package com.mrburgerus.betaplus.world.alpha_plus;

import net.minecraft.world.gen.GenerationSettings;

public class AlphaPlusGenSettings extends GenerationSettings
{
	private boolean isSnowy = false;
	private final int seaLevel = 63; // Had to be changed :(

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

	// DISABLED
//	public static AlphaPlusGenSettings createSettings(NBTTagCompound inputSettings)
//	{
//		AlphaPlusGenSettings settingsOut = new AlphaPlusGenSettings();
//		if (inputSettings.hasKey(WorldTypeAlphaPlus.SNOW_WORLD_TAG))
//		{
//			if (inputSettings.getTag(WorldTypeAlphaPlus.SNOW_WORLD_TAG).getString().equals("true"))
//			{
//				settingsOut.setSnowy(true);
//			}
//		}
//		return settingsOut;
//	}
}
