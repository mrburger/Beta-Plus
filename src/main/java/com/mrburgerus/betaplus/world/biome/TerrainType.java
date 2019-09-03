package com.mrburgerus.betaplus.world.biome;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.util.ConfigRetroPlus;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Arrays;
import java.util.List;

import static com.mrburgerus.betaplus.world.beta_plus.ChunkGeneratorBetaPlus.CHUNK_SIZE;

// Contains the Enumerated Type of Terrain. For Islands, Hills, and such.
// TODO: IMPLEMENT MOUNTAINOUS AREAS
public enum TerrainType
{
	generic(),
	land(),
	hillyLand(),
	sea(),
	deepSea(),
	coastal(),
	mountains(),
	island();

	// FIELDS //
	// Maximum allowable blob / island size
	private static final int maxBlobSize = 16;



	// CONSTRUCTOR //
	TerrainType()
	{

	}

	// PLACEHOLDER
	// Changing between 3 and 4....
	// Switched to 4 while I add the detector
	static final int heightThreshold = 4;
	static final int altThreshold = 77; // was 78
	static final int highAltThreshold = 96; // was 90

	// Check North, south, east, west. for hills
	public static TerrainType getTerrainNoIsland(int[][] yVals, int xValChunk, int zValChunk)
	{
		int y = yVals[xValChunk][zValChunk];
		// First, check if underwater
		// -1 BECAUSE OTHERWISE SHIT BREAKS (BEACHES = OCEAN?)
		if (y < ConfigRetroPlus.seaLevel - 1)
		{
			// Check if Depth exceeds the general threshold (Done before actual Depth applied)
			if (y < MathHelper.floor(ConfigRetroPlus.seaLevel - (ConfigRetroPlus.seaDepth / ConfigRetroPlus.oceanYScale)))
			{
				return deepSea;
			}
			else
			{
				return sea;
			}
		}
		// Trying -2 to fix.
		else if (y >= ConfigRetroPlus.seaLevel - 2)
		{
			// If above a predetermined threshold, it must be "Hills" or "Mountains"
			if (y >= altThreshold)
			{
				// Check for mountains
				if (y >= highAltThreshold)
				{
					return mountains;
				}
				else
				{
					return hillyLand;
				}
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
						BetaPlus.LOGGER.error("INVALID: " + xValChunk + ", " + zValChunk);
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

	// Returns a processed copy of the input TerrainType array
	// Adds islands and finds hills
	// WE ARE ASSUMING A RECTANGULAR, NOT JAGGED ARRAY
	// TODO: MAKE WORK!
	public static TerrainType[][] processTerrain(Pair<BlockPos, TerrainType>[][] inputPairs)
	{
		// Convert pairs to array
		TerrainType[][] types = new TerrainType[inputPairs.length][inputPairs[0].length];
		for (int i = 0; i < inputPairs.length; i++)
		{
			for (int j = 0; j < inputPairs[i].length; j++)
			{
				types[i][j] = inputPairs[i][j].getSecond();
			}
		}
		if (true)
		{
			return types;
		}

		// Assign to types
		TerrainType[][] processed = types;
		int blobCounter = 1;

		// Boolean = visited
		// Integer = blob detect ID
		Pair<Boolean, Integer>[][] processedData =  new Pair[processed.length][processed[0].length];
		// Fill values
		for (Pair<Boolean, Integer>[] innerData : processedData)
		{
			Arrays.fill(innerData, Pair.of(false, 0));
		}
		// Man, fuck 2D Array stuff.

		// Detect islands
		for (int x = 0; x < types.length; x++)
		{
			for (int z = 0; z < types[x].length; z++)
			{
				// Not visited and TerrainType is land or hilly
				if (!processedData[x][z].getFirst() && (types[x][z] == land || types[x][z] == hillyLand))
				{
					processedData = recursiveDetect(types, processedData, x, z, blobCounter, Lists.newArrayList(land, hillyLand));
					blobCounter++;
				}
			}
		}
		// Process using the processedData array
		// BLOB MUST BE UNDER A CERTAIN SIZE
		int blobSize;
		for (int i = 1; i < blobCounter; i++)
		{
			blobSize = getBlobSize(processedData, i);
			// Sum all values of blob counter, Identified blobs must be set to island.
			for (int x = 0; x < types.length; x++)
			{
				for (int z = 0; z < types[x].length; z++)
				{
					if (processedData[x][z].getSecond() == i && blobSize <= maxBlobSize)
					{
						// Check it does not intersect edge. If it does, skip it
						if (x == 0 || z == 0 || x == types.length - 1 || z == types[x].length - 1)
						{
							i++;
						}
						else
						{
							processed[x][z] = TerrainType.island;
						}
					}
				}
			}
		}

		// Reset processedData & blobCounter
		processedData = new Pair[processed.length][processed[0].length];
		blobCounter = 1;

		// Now Detect land areas surrounded by hilly terrain. Like the previous operation but in reverse.



		// Return
		return processed;
	}

	// Recursive Method, I developed this PAIN-STAKINGLY
	// UNDER TEST
	private static Pair<Boolean, Integer>[][] recursiveDetect(TerrainType[][] types, Pair<Boolean, Integer>[][] data, int x, int z, int counter, List<TerrainType> allowedTypes)
	{
		int sz = 1;
		// Exit conditions
		// Exit if at edge OR not an allowed value OR already visited.
		if (x < 0 || z < 0 || x >= types.length || z >= types[0].length || !allowedTypes.contains(types[x][z]) || (data[x][z].getFirst() != null && data[x][z].getFirst()))
		{
			return data;
		}

		// Main Body
		//Pair<Boolean, Integer>[][] retPairs = data;
		// We have visited this value, assign counter.
		data[x][z] = Pair.of(true, counter);
		// Recursive find neighbors.
		for (int i = x - sz; i <= x + sz; i++)
		{
			for (int j = z - sz; j <= z + sz; j++)
			{
				// Avoid infinite recursion
				if (i != x && j != z)
				{
					data = recursiveDetect(types, data, x, z, counter, allowedTypes);
				}
			}
		}

		// Added
		return data;
	}

	// Count the number of units belonging to a blob
	private static int getBlobSize(Pair<Boolean, Integer>[][] data, int blobNum)
	{
		int count = 0;
		for (int i = 0; i < data.length; i++)
		{
			for (int j = 0; j < data[i].length; j++)
			{
				if (data[i][j].getSecond() == blobNum)
				{
					count++;
				}
			}
		}
		return count;
	}
}
