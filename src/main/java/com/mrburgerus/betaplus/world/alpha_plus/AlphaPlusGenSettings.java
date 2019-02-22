package com.mrburgerus.betaplus.world.alpha_plus;

import net.minecraft.world.gen.ChunkGenSettings;
import net.minecraft.world.gen.IChunkGenSettings;

public class AlphaPlusGenSettings extends ChunkGenSettings
{
	private boolean isSnowy = true;

	public void setSnowy(boolean snowy)
	{
		isSnowy = snowy;
	}

	public boolean getSnowy()
	{
		return isSnowy;
	}
}
