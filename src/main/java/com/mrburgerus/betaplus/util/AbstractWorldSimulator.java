package com.mrburgerus.betaplus.util;

import com.mojang.datafixers.util.Pair;
import com.mrburgerus.betaplus.world.noise.AbstractOctavesGenerator;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Random;

/* Basis For World Simulators, which sample the Y-Height of thw world and add Oceans */
public abstract class AbstractWorldSimulator implements IWorldSimulator
{
	// The world seed
	private final long seed;
	/* Random Generator, based on seed. (Used concurrently to the original, it will generate the SAME results */
	protected final Random rand;
	protected final int seaLevel;
	// Noise Generators
	protected AbstractOctavesGenerator octaves1;
	protected AbstractOctavesGenerator octaves2;
	protected AbstractOctavesGenerator octaves3;
	protected AbstractOctavesGenerator octaves4;
	protected AbstractOctavesGenerator octaves5;

	// Arrays of Noise "density"
	double[] octaveArr1;
	double[] octaveArr2;
	double[] octaveArr3;
	double[] octaveArr4;
	double[] octaveArr5;
	double[] heightNoise;

	/* DATA CACHED HERE */
	/* The Pair is simply a Y Average for the ChunkPos and whether any values fall above sea level */
	// 1x1 Averages Cache
	protected HashMap<ChunkPos, Pair<Integer, Boolean>> yCache;
	// 3x3 Averages Cache
	protected HashMap<ChunkPos, Pair<Integer, Boolean>> avgYCache;

	// Constructor of Abstract Type
	protected AbstractWorldSimulator(World world)
	{
		seed = world.getSeed();
		rand = new Random(seed);
		seaLevel = world.getSeaLevel();

		// Initialize Caches
		yCache = new HashMap<>();
		avgYCache = new HashMap<>();
	}

	/* Averages a Chunk's Y Coordinates, pretty useful */
	protected Pair<Integer, Boolean> getSimulatedAvg(ChunkPos pos)
	{
		Pair<Integer[][], Boolean> chunkSimY = simulateChunkYFast(pos);
		int sum = 0;
		int numE = 0;
		for (Integer[] chunkSimA : chunkSimY.getFirst())
		{
			for (int chunkSimB : chunkSimA)
			{
				sum += chunkSimB;
				numE++;
			}
		}
		// Add it to the list of single Y (moved so that even when a 3x3 average is called for, it applies data for other use.
		int yAvg =  Math.floorDiv(sum, numE);
		Pair<Integer, Boolean> retPair = Pair.of(yAvg, chunkSimY.getSecond());
		yCache.put(pos, retPair);

		return retPair;
	}

	/* Simulate, then Average a 3x3 chunk area centered on the ChunkPos */
	protected Pair<Integer, Boolean> getSimulatedAvg3x3(ChunkPos pos)
	{
		int sum = 0;
		int numE = 0;
		// If any chunk has a value above sea level
		boolean hasValueAbove = false;
		for (int xChunk = pos.x - 1; xChunk <= pos.x + 1; ++xChunk)
		{
			for (int zChunk = pos.z -1; zChunk <= pos.z; ++zChunk)
			{
				Pair<Integer, Boolean> posPair = getSimulatedAvg(new ChunkPos(xChunk, zChunk));
				sum += posPair.getFirst();
				if (posPair.getSecond())
				{
					hasValueAbove = true;
				}
				numE++;
			}
		}
		return Pair.of(Math.floorDiv(sum, numE), hasValueAbove);
	}

	/* Generate the Noise Octaves for the Generator to Use */
	/* yValueZero is ALWAYS ZERO */
	protected abstract double[] generateOctaves(double[] values, int xChunkMult, int yValueZero, int zChunkMult, int size1, int size2, int size3);

	/* Simulates Y at (0, 0) in Chunk. */
	protected abstract int simulateYZeroZeroChunk(ChunkPos pos);

	/* Simulate Every 4 Blocks in a Chunk */
	protected abstract Pair<Integer[][], Boolean> simulateChunkYFast(ChunkPos pos);
}
