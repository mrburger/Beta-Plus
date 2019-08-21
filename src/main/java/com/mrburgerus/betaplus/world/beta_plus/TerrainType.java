package com.mrburgerus.betaplus.world.beta_plus;

import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.util.ConfigBetaPlus;
import net.minecraft.util.math.MathHelper;
import sun.security.krb5.Config;

// Contains the Enumerated Type of Terrain. For Islands, Hills, and such.
public enum TerrainType
{
	generic(),
	land(),
	hillyLand(),
	sea(),
	deepSea(),
	island();

	// FIELDS //
	// None.

	TerrainType()
	{

	}

	// Check North, south, east, west. for hills
	public static TerrainType getTerrainNoIsland(int[][] yVals, int xValChunk, int zValChunk)
	{
		// First, check if underwater
		int y = yVals[xValChunk][zValChunk];
		if (y < ConfigBetaPlus.seaLevel)
		{
			// Check if deep (5 is placeholder)
			if (y < ConfigBetaPlus.seaLevel - 5)
			{
				return deepSea;
			}
			else
			{
				return sea;
			}
		}
		else if (y >= ConfigBetaPlus.seaLevel)
		{
			// If above a predetermined threshold, it must be "Hills"
			if (y >= 80)
			{
				return hillyLand;
			}
			else
			{
				// This is where the fun begins, check the neighors
				// Check that is is inside the edges of the chunk
				if (xValChunk > 0 && xValChunk < ChunkGeneratorBetaPlus.CHUNK_SIZE - 1 && zValChunk > 0 && zValChunk < ChunkGeneratorBetaPlus.CHUNK_SIZE - 1)
				{
					// Neighbors
					int nTop = yVals[xValChunk][zValChunk + 1];
					int nBottom = yVals[xValChunk][zValChunk - 1];
					int nLeft = yVals[xValChunk - 1][zValChunk];
					int nRight = yVals[xValChunk + 1][zValChunk];

					// Get Height Difference
					// If greater than threshold, it is hilly.
					if (Math.max(Math.max(nTop, nBottom), Math.max(nLeft, nRight)) - Math.min(Math.min(nTop, nBottom), Math.min(nLeft, nRight)) > 8)
					{
						//BetaPlus.LOGGER.info("HILLS");
						return hillyLand;
					}
				}
				return land;
			}
		}
		// Catch it
		return generic;
		// Checking islands will be harder. Possibly delegate elsewhere?
	}
}
