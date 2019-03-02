package com.mrburgerus.betaplus.util;

import com.mojang.datafixers.util.Pair;
import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.world.noise.AbstractOctavesGenerator;
import net.minecraft.util.math.BlockPos;
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
	//protected final int seaLevel;
	// Noise Generators
	protected AbstractOctavesGenerator octaves1;
	protected AbstractOctavesGenerator octaves2;
	protected AbstractOctavesGenerator octaves3;
	protected AbstractOctavesGenerator scaleNoise;
	protected AbstractOctavesGenerator octaves7;

	// Arrays of Noise "density"
	protected double[] octaveArr1;
	protected double[] octaveArr2;
	protected double[] octaveArr3;
	protected double[] octaveArr4;
	protected double[] octaveArr5;
	protected double[] heightNoise;

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
		//seaLevel = world.getSeaLevel();

		// Initialize Caches
		yCache = new HashMap<>();
		avgYCache = new HashMap<>();
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
		Pair<Integer, Boolean> retPair = Pair.of(yAvg, chunkSimY.getSecond());
		yCache.put(pos, retPair);

		return retPair;
	}

	/* Simulate, then Average a 3x3 chunk area centered on the ChunkPos */
	public Pair<Integer, Boolean> simulateYAvg(BlockPos blockPos)
	{
		ChunkPos pos = new ChunkPos(blockPos);
		if (avgYCache.containsKey(pos))
		{
			// Fixed!
			//BetaPlus.LOGGER.info("Getting Cached Value");
			return avgYCache.get(pos);
		}
		else
		{
			int sum = 0;
			int numE = 0;
			// If any chunk has a value above sea level
			boolean hasValueAbove = false;
			for (int xChunk = pos.x - 1; xChunk <= pos.x + 1; ++xChunk)
			{
				for (int zChunk = pos.z - 1; zChunk <= pos.z; ++zChunk)
				{
					Pair<Integer, Boolean> posPair = getSimulatedAvg(new ChunkPos(xChunk, zChunk));
					sum += posPair.getFirst();
					if (posPair.getSecond())
					{
						//BetaPlus.LOGGER.info("Has Value Above! " + new ChunkPos(xChunk, zChunk));
						hasValueAbove = true;
					}
					numE++;
				}
			}
			// If it has a value above, notify.
			Pair<Integer, Boolean> ret = Pair.of(Math.floorDiv(sum, numE), true);
			avgYCache.put(pos, ret);
			return ret;
		}
	}

	/* Generate the Noise Octaves for the Generator to Use */
	/* yValueZero is ALWAYS ZERO */
	protected abstract double[] generateOctaves(double[] values, int xChunkMult, int yValueZero, int zChunkMult, int size1, int size2, int size3);

	/* Simulates Y at (0, 0) in Chunk. */
	protected abstract int simulateYZeroZeroChunk(ChunkPos pos);

	/* Simulate Every 4 Blocks in a Chunk */
	protected abstract Pair<int[][], Boolean> simulateChunkYFast(ChunkPos pos);

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
				if (val > 61) //Modified
				{
					return true;
				}
			}
		}
		//BetaPlus.LOGGER.info("Simulated contains no values above sea level");
		return false;
	}
}
