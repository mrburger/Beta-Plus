package com.mrburgerUS.betaplus.beta_plus.sim;

import com.mrburgerUS.betaplus.beta_plus.noise.AbstractOctavesGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

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
	// Sand Block Cache
	protected HashMap<ChunkPos, Pair<boolean[][], Boolean>> sandBlockCache;

	// Constructor of Abstract Type
	protected AbstractWorldSimulator(World world)
	{
		seed = world.getSeed();
		rand = new Random(seed);
		//seaLevel = world.getSeaLevel();

		// Initialize Caches
		yCache = new HashMap<>();
		avgYCache = new HashMap<>();
		sandBlockCache = new HashMap<>();
	}

	/* Averages a Chunk's Y Coordinates, pretty useful */
	private Pair<Integer, Boolean> getSimulatedAvg(ChunkPos pos)
	{
		Pair<int[][], Boolean> chunkSimY = simulateChunkYFast(pos);
		int sum = 0;
		int numE = 0;
		for (int[] chunkSimA : chunkSimY.getLeft())
		{
			for (int chunkSimB : chunkSimA)
			{
				sum += chunkSimB;
				numE++;
			}
		}
		// Add it to the list of single Y (moved so that even when a 3x3 average is called for, it applies data for other use.
		int yAvg = Math.floorDiv(sum, numE);
		// Removed cache put

		return Pair.of(yAvg, chunkSimY.getRight());
	}

	/* Simulate, then Average a 3x3 chunk area centered on the ChunkPos */
	// WARNING: COULD BE WRONG
	public Pair<Integer, Boolean> simulateYAvg(BlockPos blockPos)
	{
		ChunkPos chunkPosForUse = new ChunkPos(blockPos);
		if (avgYCache.containsKey(chunkPosForUse))
		{
			// Fixed!
			return avgYCache.get(chunkPosForUse);
		}
		else
		{
			int sum = 0;
			int numE = 0;
			// size * 2 + 1 is real size in chunks
			int size = 1; // Could cause issues with the Y value average being weird and assigning a bunch
			// If any chunk has a value above sea level
			boolean hasValueAbove = false;
			for (int xChunk = chunkPosForUse.x - size; xChunk <= chunkPosForUse.x + size; ++xChunk)
			{
				// Fixed looping error
				for (int zChunk = chunkPosForUse.z - size; zChunk <= chunkPosForUse.z + size; ++zChunk)
				{
					Pair<Integer, Boolean> posPair = getSimulatedAvg(new ChunkPos(xChunk, zChunk));
					//BetaPlus.LOGGER.info("Pos: " + new ChunkPos(xChunk, zChunk) + " ; " + posPair.getFirst());
					sum += posPair.getLeft();
					if (posPair.getRight())
					{
						hasValueAbove = true;
					}
					numE++;
				}
			}
			//BetaPlus.LOGGER.info("Sum: " + sum + ", NumE: " + numE);
			int yV = Math.floorDiv(sum, numE) + 1; // Adding 1 because of FloorDiv
			// If it has a value above, notify.
			Pair<Integer, Boolean> retPair = Pair.of(yV, hasValueAbove);
			avgYCache.put(chunkPosForUse, retPair);
			return retPair;
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
		for (boolean[] simulated : sandSimulated)
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
}
