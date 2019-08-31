package com.mrburgerus.betaplus.util;

import com.mojang.datafixers.util.Pair;
import com.mrburgerus.betaplus.world.noise.AbstractOctavesGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Random;

/* Basis for World Simulators, which sample the Y-Height of the world and add Oceans, Biome structures, and Beaches */
/* Only simulates critical points for speed */
public abstract class AbstractWorldSimulator implements IWorldSimulator
{
	// The world seed
	private final long seed;
	/* Random Generator, based on seed. (Used concurrently to the original, it will generate the SAME results */
	protected final Random rand;
	//protected final int seaLevel;
	// Noise Generators
	protected AbstractOctavesGenerator octaves1;
	protected AbstractOctavesGenerator octaves2;
	protected AbstractOctavesGenerator octaves3;
	protected AbstractOctavesGenerator scaleNoise;
	protected AbstractOctavesGenerator octaves7;
	protected AbstractOctavesGenerator beachNoise;
	protected AbstractOctavesGenerator surfaceNoise;

	// Arrays of Noise "density"
	protected double[] octaveArr1;
	protected double[] octaveArr2;
	protected double[] octaveArr3;
	protected double[] octaveArr4;
	protected double[] octaveArr5;
	protected double[] heightNoise;
	// Arrays of sand / gravel position
	protected double[] sandNoise = new double[256];
	protected double[] gravelNoise = new double[256];
	protected double[] stoneNoise = new double[256];

	/* DATA CACHED HERE */
	/* The Pair is simply a Y Average for the ChunkPos and whether any values fall above sea level */
	// 1x1 Averages Cache
	protected HashMap<ChunkPos, Pair<Integer, Boolean>> yCache;
	// 3x3 Averages Cache
	protected HashMap<ChunkPos, Pair<Integer, Boolean>> avgYCache;
	// Sand & Gravel Block Cache
	protected HashMap<ChunkPos, Pair<boolean[][], Boolean>> beachBlockCache;
	// Holds cache of EVERY simulated y value. Testing for memory issues.
	// REMEMBER TO FILL THIS VALUE.
	protected HashMap<ChunkPos, int[][]> chunkYCache;

	// Constructor of Abstract Type
	protected AbstractWorldSimulator(World world)
	{
		seed = world.getSeed();
		rand = new Random(seed);

		// Initialize Caches
		yCache = new HashMap<>();
		avgYCache = new HashMap<>();
		beachBlockCache = new HashMap<>();
		chunkYCache = new HashMap<>();
	}

	/* Averages a Chunk's Y Coordinates, pretty useful */
	private Pair<Integer, Boolean> getSimulatedAvg(ChunkPos pos)
	{
		Pair<int[][], Boolean> chunkSimY = simulateChunkYFast(pos);
		int sum = 0;
		int numE = 0;
		for (int[] chunkSimA : chunkSimY.getFirst())
		{
			for (int chunkSimB : chunkSimA)
			{
				sum += chunkSimB;
				numE++;
			}
		}
		// Add it to the list of single Y (moved so that even when a 3x3 average is called for, it applies data for other use.
		int yAvg =  Math.floorDiv(sum, numE);
		// Removed cache put

		return Pair.of(yAvg, chunkSimY.getSecond());
	}

	/* Simulate, then Average a 3x3 chunk area centered on the ChunkPos */
	public Pair<Integer, Boolean> simulateYAvg(BlockPos blockPos)
	{
		ChunkPos chunkPosForUse = new ChunkPos(blockPos);
		if (avgYCache.containsKey(chunkPosForUse))
		{
			return avgYCache.get(chunkPosForUse);
		}
		else
		{
			int sum = 0;
			int numE = 0;
			// size * 2 + 1 is real size in chunks
			int size = 1;
			// If any chunk has a value above sea level
			boolean hasValueAbove = false;
			for (int xChunk = chunkPosForUse.x - size; xChunk <= chunkPosForUse.x + size; ++xChunk)
			{
				// Fixed looping error
				for (int zChunk = chunkPosForUse.z - size; zChunk <= chunkPosForUse.z + size; ++zChunk)
				{
					Pair<Integer, Boolean> posPair = getSimulatedAvg(new ChunkPos(xChunk, zChunk));
					//BetaPlus.LOGGER.info("Pos: " + new ChunkPos(xChunk, zChunk) + " ; " + posPair.getFirst());
					sum += posPair.getFirst();
					if (posPair.getSecond())
					{
						hasValueAbove = true;
					}
					numE++;
				}
			}
			int yV = Math.floorDiv(sum, numE) + 1; // Adding 1 because of FloorDiv
			// If it has a value above, notify.
			Pair<Integer, Boolean> retPair = Pair.of(yV, hasValueAbove);
			avgYCache.put(chunkPosForUse, retPair);
			return retPair;
		}
	}

	/* Simulates a single chunk's average */
	public Pair<Integer, Boolean> simulateYChunk(BlockPos pos)
	{
		ChunkPos chunkPosForUse = new ChunkPos(pos);

		if (yCache.containsKey(chunkPosForUse))
		{
			return yCache.get(chunkPosForUse);
		}
		else
		{
			Pair<Integer, Boolean> retPair = getSimulatedAvg(chunkPosForUse);
			yCache.put(chunkPosForUse, retPair);
			return retPair;
		}
	}

	/* Returns whether ANY value in simulatedY is greater than sea level */
	protected boolean landValExists(int[][] simulatedY)
	{
		for (int[] simulated : simulatedY)
		{
			for (int val : simulated)
			{
				if (val >= 60) //Modified
				{
					return true;
				}
			}
		}
		return false;
	}

	/* If ANY blocks in the chunk applied sand */
	protected boolean anyBlockSand(boolean[][] sandSimulated)
	{
		for(boolean[] simulated : sandSimulated)
		{
			for (boolean b : simulated)
			{
				if (b)
				{
					return true;
				}
			}
		}
		return false;
	}

	/* Is a block going to be sand according to the simulator? */
	/* DOES NOT CHECK WHETHER A VALUE IS ACTUALLY ABLE TO BE A BEACH BASED ON Y */
	/* Check if valid y externally! */
	public boolean isBlockBeach(BlockPos pos)
	{
		ChunkPos chunkPos = new ChunkPos(pos);
		int xPosChunk = pos.getX() & 15;
		int zPosChunk = pos.getZ() & 15;
		if (beachBlockCache.containsKey(chunkPos))
		{
			return beachBlockCache.get(chunkPos).getFirst()[xPosChunk][zPosChunk]; // Get boolean array at the position in question, if yes it is sand.
		}
		else
		{
			Pair<boolean[][], Boolean> sandPair = isBeachBlockSim(chunkPos);
			beachBlockCache.put(chunkPos, sandPair); // Enter the value
			return sandPair.getFirst()[xPosChunk][zPosChunk];
		}
	}

	private void enterIntoCache(ChunkPos cPos, int[][] yValues)
	{
		chunkYCache.put(cPos, yValues);
	}

	// TESTING A MOVE TO UNIFIED
	public Pair<int[][], Boolean> simulateChunkYFull(ChunkPos pos)
	{
		// Added for test 0.5c, STACKOVERFLOW. Do not use!
		//provider.getBiomes(pos.x * 16, pos.z * 16, 16, 16, false);


		// Check if already simulated
		if (chunkYCache.containsKey(pos))
		{
			//BetaPlus.LOGGER.info("WHAT! IT EXISTS");
			return Pair.of(chunkYCache.get(pos), landValExists(chunkYCache.get(pos)));
		}

		int[][] output = new int[16][16];
		heightNoise = generateOctaves(heightNoise, pos.x * 4, 0,pos.z * 4, 5, 17, 5);
		for (int cX = 0; cX < 4; ++cX)
		{
			for (int cZ = 0; cZ < 4; ++cZ)
			{
				for (int cY = 0; cY < 16; ++cY)
				{
					double eighth = 0.125;
					double noise1 = heightNoise[(cX * 5 + cZ) * 17 + cY];
					double noise2 = heightNoise[(cX * 5 + cZ + 1) * 17 + cY];
					double noise3 = heightNoise[((cX + 1) * 5 + cZ) * 17 + cY];
					double noise4 = heightNoise[((cX + 1) * 5 + cZ + 1) * 17 + cY];
					double noise5 = (heightNoise[(cX * 5 + cZ) * 17 + cY + 1] - noise1) * eighth;
					double noise6 = (heightNoise[(cX * 5 + cZ + 1) * 17 + cY + 1] - noise2) * eighth;
					double noise7 = (heightNoise[((cX + 1) * 5 + cZ) * 17 + cY + 1] - noise3) * eighth;
					double noise8 = (heightNoise[((cX + 1) * 5 + cZ + 1) * 17 + cY + 1] - noise4) * eighth;
					for (int cY2 = 0; cY2 < 8; ++cY2)
					{
						double quarter = 0.25;
						double noise11 = noise1;
						double noise22 = noise2;
						double noiseV1 = (noise3 - noise1) * quarter;
						double noiseV2 = (noise4 - noise2) * quarter;
						for (int m = 0; m < 4; ++m)
						{
							int x = m + cX * 4;
							int y = cY * 8 + cY2;
							int z = cZ * 4;
							double quarter2 = 0.25;
							double stoneNoise = noise11;
							double noiseV3 = (noise22 - noise11) * quarter2;
							for (int n = 0; n < 4; ++n)
							{
								if (stoneNoise > 0.0)
								{
									output[x][z] = y;
								}
								++z;
								stoneNoise += noiseV3;
							}
							noise11 += noiseV1;
							noise22 += noiseV2;
						}
						noise1 += noise5;
						noise2 += noise6;
						noise3 += noise7;
						noise4 += noise8;
					}

				}
			}
		}
		// Enter into cache
		enterIntoCache(pos, output);

		// Return
		return Pair.of(output, landValExists(output));
	}

	/* Simulates sand blocks, for spawning on beaches, and Buried Treasure */
	// MOVED SINCE IT IS THE SAME ACROSS VERSIONS
	private Pair<boolean[][], Boolean> isBeachBlockSim(ChunkPos chunkPos)
	{
		// Chunksize is 16
		boolean[][] outputBool = new boolean[16][16];

		double thirtySecond = 0.03125;
		this.sandNoise = this.beachNoise.generateNoiseOctaves(this.sandNoise, chunkPos.x * 16, chunkPos.z * 16, 0.0, 16, 16, 1, thirtySecond, thirtySecond, 1.0);
		this.gravelNoise = this.beachNoise.generateNoiseOctaves(this.gravelNoise, chunkPos.x * 16, 109.0134, chunkPos.z * 16, 16, 1, 16, thirtySecond, 1.0, thirtySecond);
		this.stoneNoise = this.surfaceNoise.generateNoiseOctaves(this.stoneNoise, chunkPos.x * 16, chunkPos.z * 16, 0.0, 16, 16, 1, thirtySecond * 2.0, thirtySecond * 2.0, thirtySecond * 2.0);
		for (int z = 0; z < 16; ++z)
		{
			for (int x = 0; x < 16; ++x)
			{
				boolean sandN = this.sandNoise[z + x * 16] + this.rand.nextDouble() * 0.2 > 0.0;
				boolean gravelN = this.gravelNoise[z + x * 16] + this.rand.nextDouble() * 0.2 > 3.0;
				//int stoneN = (int) (this.stoneNoise[z + x * 16] / 3.0 + 3.0 + this.rand.nextDouble() * 0.25);
				outputBool[x][z] = (sandN || gravelN);
			}
		}
		Pair<boolean[][], Boolean> retPair = Pair.of(outputBool, anyBlockSand(outputBool));
		beachBlockCache.put(chunkPos, retPair);
		return retPair;
	}

	/* Generate the Noise Octaves for the Generator to Use */
	/* yValueZero is ALWAYS ZERO */
	protected abstract double[] generateOctaves(double[] values, int xPos, int yValueZero, int zPos, int sizeX, int sizeY, int sizeZ);

	/* Simulate Every 4 Blocks in a Chunk */
	public abstract Pair<int[][], Boolean> simulateChunkYFast(ChunkPos pos);
}
