package com.mrburgerus.betaplus.world.biome;

import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.util.ConfigRetroPlus;

import static com.mrburgerus.betaplus.world.beta_plus.ChunkGeneratorBetaPlus.CHUNK_SIZE;

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

	// PLACEHOLDER
	static final int heightThreshold = 3;

	// Check North, south, east, west. for hills
	public static TerrainType getTerrainNoIsland(int[][] yVals, int xValChunk, int zValChunk)
	{
		int y = yVals[xValChunk][zValChunk];
		// First, check if underwater
		if (y < ConfigRetroPlus.seaLevel)
		{
			// Check if Depth exceeds the general threshold (Done before actual Depth applied)
			if (y < ConfigRetroPlus.seaLevel - (ConfigRetroPlus.seaDepth / ConfigRetroPlus.oceanYScale))
			{
				return deepSea;
			}
			else
			{
				return sea;
			}
		}
		else if (y >= ConfigRetroPlus.seaLevel)
		{
			// If above a predetermined threshold, it must be "Hills"
			if (y >= 96)
			{
				return hillyLand;
			}
			// This is where the fun begins, check the neighors
			// Check that is is inside the edges of the chunk
			else
			{
				// Thought process
				// At Top Left, the X and Z cannot be subtracted
				// At Top Right, the X cannot be added and the Z cannot be subtracted
				// At Bottom Left, the X cannot be subtracted and the Z cannot be added
				// At Bottom Right, the X and Z cannot be Added
				int nTop;
				int nBottom;
				int nLeft;
				int nRight;

				// Assignments
				nTop = yVals[xValChunk][zValChunk];
				nBottom = yVals[xValChunk][zValChunk];
				nLeft = yVals[xValChunk][zValChunk];
				nRight = yVals[xValChunk][zValChunk];
				if (xValChunk > 0 && xValChunk < CHUNK_SIZE - 1 && zValChunk > 0 && zValChunk < CHUNK_SIZE - 1)
				{
					nTop = yVals[xValChunk][zValChunk + 1];
					nBottom = yVals[xValChunk][zValChunk - 1];
					nLeft = yVals[xValChunk - 1][zValChunk];
					nRight = yVals[xValChunk + 1][zValChunk];
				}
				else if (xValChunk == 0 && zValChunk == 0) // Top Left (No Left or Bottom?)
				{
					nTop = yVals[xValChunk][zValChunk + 1];
					nRight = yVals[xValChunk + 1][zValChunk];
				}
				else if (xValChunk == 0 && zValChunk == CHUNK_SIZE - 1) // Bottom Left
				{
					nBottom = yVals[xValChunk][zValChunk - 1];
					nRight = yVals[xValChunk + 1][zValChunk];
				}
				else if (xValChunk == CHUNK_SIZE - 1 && zValChunk == 0) // Top Right
				{
					nTop = yVals[xValChunk][zValChunk + 1];
					nLeft = yVals[xValChunk - 1][zValChunk];
				}
				else if (xValChunk == CHUNK_SIZE - 1 && zValChunk == CHUNK_SIZE - 1) // Bottom Right
				{
					nBottom = yVals[xValChunk][zValChunk - 1];
					nLeft = yVals[xValChunk - 1][zValChunk];
				}
				else
				{
					// Sides
					if (xValChunk == 0)
					{
						// ignore nLeft
						nTop = yVals[xValChunk][zValChunk + 1];
						nBottom = yVals[xValChunk][zValChunk - 1];
						nRight = yVals[xValChunk + 1][zValChunk];
					}
					else if (xValChunk == CHUNK_SIZE - 1)
					{
						// ignore nRight
						nTop = yVals[xValChunk][zValChunk + 1];
						nBottom = yVals[xValChunk][zValChunk - 1];
						nLeft = yVals[xValChunk - 1][zValChunk];
					}
					else if (zValChunk == 0)
					{
						// ignore nBottom
						nTop = yVals[xValChunk][zValChunk + 1];
						nLeft = yVals[xValChunk - 1][zValChunk];
						nRight = yVals[xValChunk + 1][zValChunk];
					}
					else if (zValChunk == CHUNK_SIZE - 1)
					{
						// ignore nTop
						nBottom = yVals[xValChunk][zValChunk - 1];
						nLeft = yVals[xValChunk - 1][zValChunk];
						nRight = yVals[xValChunk + 1][zValChunk];
					}
					else
					{
						// This shouldn't Happen!
						BetaPlus.LOGGER.error("INV: " + xValChunk + ", " + zValChunk);
					}
				}

				// Get Height Difference
				// If greater than threshold, it is hilly.
				if (Math.max(Math.max(nTop, nBottom), Math.max(nLeft, nRight)) - Math.min(Math.min(nTop, nBottom), Math.min(nLeft, nRight)) > heightThreshold)
				{
					return hillyLand;
				}
				else
				{
					return land;
				}
			}
		}
		// Catch it
		return generic;
		// Checking islands will be harder. Possibly delegate elsewhere?
	}
}
