package com.mrburgerus.betaplus.world.beta_plus.sim;

import com.mojang.datafixers.util.Pair;
import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.util.IWorldSimulator;
import com.mrburgerus.betaplus.world.noise.NoiseGeneratorOctavesBeta;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;

import java.util.HashMap;
import java.util.Random;

public class BetaPlusSimulatorOLD
{
	// Basic Fields
	private final long seed;
	private Random rand;
	private int seaLevel;

	// Fields, mostly carried from ChunkGeneratorBetaPlus
	//Noise Generators
	private NoiseGeneratorOctavesBeta octaves1;
	private NoiseGeneratorOctavesBeta octaves2;
	private NoiseGeneratorOctavesBeta octaves3;
	private NoiseGeneratorOctavesBeta beachBlockNoise; // Formerly octaves4, used for Gravel and Sand, so probably beaches.
	private NoiseGeneratorOctavesBeta surfaceNoise; // Formerly octaves5
	private NoiseGeneratorOctavesBeta scaleNoise; // Formerly octaves6, renamed using ChunkGeneratorOverworld
	private NoiseGeneratorOctavesBeta octaves7;
	//Noise Arrays
	private double[] octaveArr1;
	private double[] octaveArr2;
	private double[] octaveArr3;
	private double[] octaveArr4;
	private double[] octaveArr5;
	private double[] heightNoise;
	private double[] sandNoise = new double[256];
	private double[] gravelNoise = new double[256];
	private double[] stoneNoise = new double[256];

	// Save the data HERE
	private static HashMap<ChunkPos, Integer> yCache;
	private static  HashMap<ChunkPos, Integer> avgYCache;

	// Constructor
	public BetaPlusSimulatorOLD(World world)
	{
		seed = world.getSeed();
		rand = new Random(seed);
		seaLevel = world.getSeaLevel(); // Should work

		// Remember to assign values EXACTLY the same way, otherwise the .next[X]() value order will be disturbed.
		octaves1 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves2 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves3 = new NoiseGeneratorOctavesBeta(rand, 8);
		beachBlockNoise = new NoiseGeneratorOctavesBeta(rand, 4);
		surfaceNoise = new NoiseGeneratorOctavesBeta(rand, 4);
		scaleNoise = new NoiseGeneratorOctavesBeta(rand, 10);
		octaves7 = new NoiseGeneratorOctavesBeta(rand, 16);

		// Moved down here to remove ANY possiblity of using an old cache across world loads.
		yCache = new HashMap<>();
		avgYCache = new HashMap<>();
	}

	/* This method is like setBlocksInChunk, but it only cares about Y Values */
	//TODO: MODIFY SO IT FITS INTO OTHER METHODS
	//TODO: MAKE THE SINGLE Y CACHE TAKE VALUES CREATED BY THE 3x3 FOR SPEEDIER PROCESSING
	//TODO: Find a way to tell the biome provider if ANY of the values inside a block of data fall above sea Level, so I can use it for monuments.
	// settings.getSeaLevel = 63, btw
	@Deprecated
	private void unusedBase(IChunk chunk, int chunkX, int chunkZ)
	{
		heightNoise = this.generateOctaves(heightNoise, chunkX * 4, 0,chunkZ * 4, 5, 17, 5);
		for (int i = 0; i < 4; ++i)
		{
			for (int j = 0; j < 4; ++j)
			{
				for (int k = 0; k < 16; ++k)
				{
					double eigth = 0.125;
					double var16 = heightNoise[((i) * 5 + j) * 17 + k];
					double var18 = heightNoise[((i) * 5 + j + 1) * 17 + k];
					double var20 = heightNoise[((i + 1) * 5 + j) * 17 + k];
					double var22 = heightNoise[((i + 1) * 5 + j + 1) * 17 + k];
					double var24 = (heightNoise[((i) * 5 + j) * 17 + k + 1] - var16) * eigth;
					double var26 = (heightNoise[((i) * 5 + j + 1) * 17 + k + 1] - var18) * eigth;
					double var28 = (heightNoise[((i + 1) * 5 + j) * 17 + k + 1] - var20) * eigth;
					double var30 = (heightNoise[((i + 1) * 5 + j + 1) * 17 + k + 1] - var22) * eigth;
					for (int l = 0; l < 8; ++l)
					{
						double quarter = 0.25;
						double var35 = var16;
						double var37 = var18;
						double var39 = (var20 - var16) * quarter;
						double var41 = (var22 - var18) * quarter;
						for (int m = 0; m < 4; ++m)
						{
							int x = m + i * 4;
							int y = k * 8 + l;
							int z = j * 4;
							double var46 = 0.25;
							double var48 = var35;
							double var50 = (var37 - var35) * var46;
							for (int n = 0; n < 4; ++n)
							{
								Block block = null;
								if (y < this.seaLevel)
								{
									block = Blocks.WATER;
								}
								if (var48 > 0.0)
								{
									block = Blocks.STONE;
								}
								if (block != null)
								{
									chunk.setBlockState(new BlockPos(x, y, z), block.getDefaultState(), false);
								}
								++z;
								var48 += var50;
							}
							var35 += var39;
							var37 += var41;
						}
						var16 += var24;
						var18 += var26;
						var20 += var28;
						var22 += var30;
					}

				}
			}
		}
	}


	public Pair<Integer, Boolean> simulateYChunk(BlockPos pos)
	{
		ChunkPos chunkPosForUse = new ChunkPos(pos);

		if (yCache.containsKey(chunkPosForUse))
		{
			return Pair.of(avgYCache.get(chunkPosForUse), false);
		}
		else
		{
			int ret = getSimulatedAvg(chunkPosForUse.x, chunkPosForUse.z);
			return Pair.of(ret, false);
		}
	}

	public Pair<Integer, Boolean> simulateYAvg(BlockPos pos)
	{
		ChunkPos chunkPosForUse = new ChunkPos(pos);

		if (avgYCache.containsKey(chunkPosForUse))
		{
			return Pair.of(avgYCache.get(chunkPosForUse), false);
		}
		else
		{
			BetaPlus.LOGGER.info("Simulating: " + chunkPosForUse);
			int ret = getSimulatedAvg3x3(chunkPosForUse.x, chunkPosForUse.z).getFirst();
			avgYCache.put(chunkPosForUse, ret);
			return Pair.of(ret, false);
		}
	}

	/* Averages a Chunk's Y Coordinates, pretty useful */
	private int getSimulatedAvg(int chunkX, int chunkZ)
	{
		int[][] chunkSimY = simulateChunkYFast(chunkX, chunkZ);
		int sum = 0;
		int numElem = 0;
		for (int[] chunkSimA : chunkSimY)
		{
			for (int chunkSimB : chunkSimA)
			{
				sum += chunkSimB;
				numElem++;
			}
		}
		// Add it to the list of single Y (moved so that even when a 3x3 average is called for, it applies data for other use.
		int yAvg =  Math.floorDiv(sum, numElem);
		yCache.put(new ChunkPos(chunkX, chunkZ), yAvg);


		return yAvg;
	}

	/* Simulates and averages a 3x3 chunk, used to  */
	/* Added: Boolean corresponding to whether any simulate value is ABOVE sea level */
	private Pair<Integer, Boolean> getSimulatedAvg3x3(int middleChunkX, int middleChunkZ)
	{
		int sum = 0;
		int numE = 0;
		for (int xChunk = middleChunkX - 1; xChunk <= middleChunkX + 1; ++xChunk)
		{
			for (int zChunk = middleChunkZ -1; zChunk <= middleChunkZ; ++zChunk)
			{
				sum += getSimulatedAvg(xChunk, zChunk);
				numE++;
			}
		}
		return Pair.of(Math.floorDiv(sum, numE), false);
	}


	public int simulateYZeroZeroChunk(int chunkX, int chunkZ)
	{
		int output = 0;

		heightNoise = this.generateOctaves(heightNoise, chunkX * 4, 0, chunkZ * 4, 5, 17, 5);
		for (int cY = 0; cY < 16; ++cY)
		{
			// Highly simplified for 0,0 in chunk!
			double eigth = 0.125;
			double noise1 = heightNoise[cY];
			double noise2 = heightNoise[17 + cY];
			double eightNoise1 = (heightNoise[cY + 1] - noise1) * eigth;
			for (int y2 = 0; y2 < 8; ++y2)
			{
				double quarter = 0.25;
				double var35 = noise1;
				// m = 0 always in this case
				int yP = cY * 8 + y2;
				double stonePosPrime = var35;
					double var50 = (noise2 - var35) * quarter;
					for (int n = 0; n < 4; ++n)
					{
						if (stonePosPrime > 0.0)
						{
							return yP;
						}
						stonePosPrime += var50;
					}

				noise1 += eightNoise1;
			}

		}
		// In case
		BetaPlus.LOGGER.debug("Cannot Guarantee Y Simulator Results!");
		return output;
	}

	/* EXACTLY like ChunkGeneratorBetaPlus, with slight modifications */
	/* Modifications: Removed Calls to temperature */
	public double[] generateOctaves(double[] values, int xChunkMult, int yValueZero, int zChunkMult, int size1, int size2, int size3)
	{
		if (values == null)
		{
			values = new double[size1 * size2 * size3];
		}
		double noiseFactor = 684.412;
		octaveArr4 = scaleNoise.generateNoiseOctaves(octaveArr4, xChunkMult, zChunkMult, size1, size3, 1.121, 1.121, 0.5);
		octaveArr5 = octaves7.generateNoiseOctaves(octaveArr5, xChunkMult, zChunkMult, size1, size3, 200.0, 200.0, 0.5);
		octaveArr1 = octaves3.generateNoiseOctaves(octaveArr1, xChunkMult, 0, zChunkMult, size1, size2, size3, noiseFactor / 80.0, noiseFactor / 160.0, noiseFactor / 80.0);
		octaveArr2 = octaves1.generateNoiseOctaves(octaveArr2, xChunkMult, 0, zChunkMult, size1, size2, size3, noiseFactor, noiseFactor, noiseFactor);
		octaveArr3 = octaves2.generateNoiseOctaves(octaveArr3, xChunkMult, 0, zChunkMult, size1, size2, size3, noiseFactor, noiseFactor, noiseFactor);
		int incrementer1 = 0;
		int incrementer2 = 0;
		int var16 = 16 / size1;
		for (int i = 0; i < size1; ++i)
		{
			for (int j = 0; j < size3; ++j)
			{
				double var29;
				double var27 = (octaveArr4[incrementer2] + 256.0) / 512.0;
				if ((var29 = octaveArr5[incrementer2] / 8000.0) < 0.0)
				{
					var29 = (-var29) * 0.3;
				}
				if ((var29 = var29 * 3.0 - 2.0) < 0.0)
				{
					if ((var29 /= 2.0) < -1.0)
					{
						var29 = -1.0;
					}
					var29 /= 1.4;
					var29 /= 2.0;
					var27 = 0.0;
				}
				else
				{
					if (var29 > 1.0)
					{
						var29 = 1.0;
					}
					var29 /= 8.0;
				}
				if (var27 < 0.0)
				{
					var27 = 0.0;
				}
				var27 += 0.5;
				var29 = var29 * (double) size2 / 16.0;
				double var31 = (double) size2 / 2.0 + var29 * 4.0;
				++incrementer2;
				for (int k = 0; k < size2; ++k)
				{
					double var34;
					double var36 = ((double) k - var31) * 12.0 / var27;
					if (var36 < 0.0)
					{
						var36 *= 4.0;
					}
					double var38 = octaveArr2[incrementer1] / 512.0;
					double var40 = octaveArr3[incrementer1] / 512.0;
					double var42 = (octaveArr1[incrementer1] / 10.0 + 1.0) / 2.0;
					var34 = var42 < 0.0 ? var38 : (var42 > 1.0 ? var40 : var38 + (var40 - var38) * var42);
					var34 -= var36;
					if (k > size2 - 4)
					{
						double var44 = (float) (k - (size2 - 4)) / 3.0f;
						var34 = var34 * (1.0 - var44) + -10.0 * var44;
					}
					values[incrementer1] = var34;
					++incrementer1;
				}
			}
		}
		return values;
	}

	/* See Alpha Simulator for Example */
	/* HOPEFULLY THIS WORKS */
	public int[][] simulateChunkYFast(int chunkX, int chunkZ)
	{
		int[][] output = new int[4][4];
		heightNoise = this.generateOctaves(heightNoise, chunkX * 4, 0,chunkZ * 4, 5, 17, 5);
		for (int cX = 0; cX < 4; ++cX)
		{
			for (int cZ = 0; cZ < 4; ++cZ)
			{
				for (int cY = 0; cY < 16; ++cY)
				{
					double eigth = 0.125;
					double noise1 = heightNoise[((cX) * 5 + cZ) * 17 + cY];
					double var18 = heightNoise[((cX) * 5 + cZ + 1) * 17 + cY];
					double var20 = heightNoise[((cX + 1) * 5 + cZ) * 17 + cY];
					double var22 = heightNoise[((cX + 1) * 5 + cZ + 1) * 17 + cY];
					double eightNoise1 = (heightNoise[((cX) * 5 + cZ) * 17 + cY + 1] - noise1) * eigth;
					for (int y2 = 0; y2 < 8; ++y2)
					{
						double quarter = 0.25;
						double stoneP2 = noise1;
						double stoneP1 = var18;
						double var39 = (var20 - noise1) * quarter;
						double var41 = (var22 - var18) * quarter;
						for (int m = 0; m < 4; ++m)
						{
							int y = cY * 8 + y2;
							double stonePosPrime = stoneP2;
							double stoneAdder = (stoneP1 - stoneP2) * 0.25;
							for (int n = 0; n < 4; ++n)
							{
								if (stonePosPrime > 0.0)
								{
									output[cX][cZ] = y;
								}
								stonePosPrime += stoneAdder;
							}
							stoneP2 += var39;
							stoneP1 += var41;
						}
						noise1 += eightNoise1;
					}

				}
			}
		}
		return output;
	}
}
