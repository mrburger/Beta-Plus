package com.mrburgerus.betaplus.world.alpha_plus.sim;

import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.util.IWorldSimulator;
import com.mrburgerus.betaplus.world.noise.NoiseGeneratorOctavesAlpha;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Random;

/* Simulates Y values */
/* Potential for the Cache to cause a Stack Overflow, but WHATEVER. If it happens I'll fix it. */
/* Seems to work, needs additional testing. */
/* POTENTIAL ISSUE: 3x3 Averages Not Interacting well with Simple Chunk Averages */
public class AlphaPlusSimulator implements IWorldSimulator
{
	// Basic Fields
	private final long seed;
	private Random rand;

	// Noise Generators
	private NoiseGeneratorOctavesAlpha octaves1;
	private NoiseGeneratorOctavesAlpha octaves2;
	private NoiseGeneratorOctavesAlpha octaves3;
	private NoiseGeneratorOctavesAlpha octaves4;
	private NoiseGeneratorOctavesAlpha octaves5;

	// Noise Arrays
	private double[] heightNoise;
	double[] octave3Arr;
	double[] octave1Arr;
	double[] octave2Arr;
	double[] octave4Arr;
	double[] octave5Arr;


	// Save the data HERE
	private static HashMap<ChunkPos, Integer> singleYCache;

	public AlphaPlusSimulator(World world)
	{
		seed = world.getSeed();
		rand = new Random(seed);
		this.octaves1 = new NoiseGeneratorOctavesAlpha(this.rand, 16);
		this.octaves2 = new NoiseGeneratorOctavesAlpha(this.rand, 16);
		this.octaves3 = new NoiseGeneratorOctavesAlpha(this.rand, 8);
		new NoiseGeneratorOctavesAlpha(this.rand, 4);
		new NoiseGeneratorOctavesAlpha(this.rand, 4);
		this.octaves4 = new NoiseGeneratorOctavesAlpha(this.rand, 10);
		this.octaves5 = new NoiseGeneratorOctavesAlpha(this.rand, 16);
		// Moved down here to remove ANY possiblity of using an old cache across world loads.
		singleYCache = new HashMap<>();
	}

	@Override
	public int simulateYSingle(BlockPos pos)
	{
		//Formerly xP, zP. This was not a correct assignment.
		// Could this be an issue?
		// Attempt to assign blockpos
		ChunkPos chunkPosForUse = new ChunkPos(pos);

		if (singleYCache.containsKey(chunkPosForUse))
		{
			return singleYCache.get(chunkPosForUse);
		}
		else
		{
			int ret = getSimulatedAvg(chunkPosForUse.x, chunkPosForUse.z);
			singleYCache.put(chunkPosForUse, ret);
			return ret;
		}
	}

	public int simulateYSingleWithAvg(BlockPos pos)
	{
		//Formerly xP, zP. This was not a correct assignment.
		// Could this be an issue?
		// Attempt to assign blockpos
		ChunkPos chunkPosForUse = new ChunkPos(pos);

		if (singleYCache.containsKey(chunkPosForUse))
		{
			return singleYCache.get(chunkPosForUse);
		}
		else
		{
			int ret = getSimulatedAvg3x3(chunkPosForUse.x, chunkPosForUse.z);
			singleYCache.put(chunkPosForUse, ret);
			return ret;
		}
	}

	/* Simulates a SINGLE Y Value, for usage. */
	/* Unverified if it works */
	@Override
	public int simulateYZeroZeroChunk(int chunkX, int chunkZ)
	{
		int output = 256; //Iterated through.

		byte yHeight = 17;
		this.heightNoise = this.generateOctaves(this.heightNoise, chunkX * 4, 0, chunkZ * 4, 5, yHeight, 5);

		for (int cY = 0; cY < 16; ++cY)
		{
			// Thankfully, since these values simplify, we can easily get just a few values.
			double noise1 = this.heightNoise[cY];
			double eightNoise1 = (this.heightNoise[cY + 1] - noise1) * 0.125D;

			// Iterate through Y
			for (int y2 = 0; y2 < 8; ++y2)
			{
				// Since we only care about the Pos at Prime values, we can simplify.
				double stonePosPrime = noise1;
				int yP = cY * 8 + y2;

				// Should hopefully emulate (does not)
				if(stonePosPrime > 0.0D)
				{
					//BetaPlus.LOGGER.debug("Y: " + yP);
					output = yP;
				}

				noise1 += eightNoise1;
			}
		}

		//BetaPlus.LOGGER.debug("Final Y: " + output);
		return output;
	}


	/* Simulate Y Values every 4 blocks in a chunk */
	/* Ouput is 2D array Z, X : This is how it used to be. */
	private int[][] simulateChunkYFast(int chunkX, int chunkZ)
	{
		int[][] output = new int[4][4];

		// 1+1 converted to var4+1, like original
		byte var4 = 4;
		int var6 = var4 + 1;
		byte yHeight = 17;
		int var8 = var4 + 1;
		this.heightNoise = this.generateOctaves(this.heightNoise, chunkX * var4, 0, chunkZ * var4, var6, yHeight, var8);

		/* These go to every 4 blocks only */
		for (int cX = 0; cX < var4; ++cX)
		{
			for (int cZ = 0; cZ < var4; ++cZ)
			{
				for (int cY = 0; cY < 16; ++cY)
				{
					// Assign these values like world gen.
					double noise1 = this.heightNoise[(((cX * var8) + cZ) * yHeight) + cY];
					double eightNoise1 = (this.heightNoise[(cX * var8 + cZ) * yHeight + cY + 1] - noise1) * 0.125D;

					// Iterate through Y
					for (int y2 = 0; y2 < 8; ++y2)
					{
						// Since we only care about the Pos at Prime values, we can simplify.
						double stonePosPrime = noise1;
						int yP = cY * 8 + y2;

						// Should hopefully emulate. (I think it does!)
						if(stonePosPrime > 0.0D)
						{
							output[cX][cZ] = yP;
						}

						noise1 += eightNoise1;
					}
				}
			}
		}

		return output;
	}

	/* Simulates Y Values in a chunk */
	/* Ouput is 2D array Z, X (Which is strange, but whatever) */
	/* Works, but SLOW */
	private int[][] simulateChunkY(int chunkX, int chunkZ)
	{
		// 256 because 1 chunk
		int[][] output = new int[16][16];

		byte var4 = 4;
		int var6 = var4 + 1;
		byte var7 = 17;
		int var8 = var4 + 1;
		// heightNoise length = var6 * var7 * var8
		this.heightNoise = this.generateOctaves(this.heightNoise, chunkX * var4, 0, chunkZ * var4, var6, var7, var8);

		for (int c1X = 0; c1X < var4; ++c1X)
		{
			for (int c2Z = 0; c2Z < var4; ++c2Z)
			{
				for (int c3Y = 0; c3Y < 16; ++c3Y)
				{
					double eighth = 0.125D;
					double noise1 = this.heightNoise[((((c1X) * var8) + c2Z) * var7) + c3Y];
					double noise2 = this.heightNoise[((c1X) * var8 + c2Z + 1) * var7 + c3Y];
					double noise3 = this.heightNoise[((c1X + 1) * var8 + c2Z) * var7 + c3Y];
					double noise4 = this.heightNoise[((c1X + 1) * var8 + c2Z + 1) * var7 + c3Y];
					double eightNoise1 = (this.heightNoise[((c1X) * var8 + c2Z) * var7 + c3Y + 1] - noise1) * eighth;
					double eightNoise2 = (this.heightNoise[((c1X) * var8 + c2Z + 1) * var7 + c3Y + 1] - noise2) * eighth;
					double eightNoise3 = (this.heightNoise[((c1X + 1) * var8 + c2Z) * var7 + c3Y + 1] - noise3) * eighth;
					double eightNoise4 = (this.heightNoise[((c1X + 1) * var8 + c2Z + 1) * var7 + c3Y + 1] - noise4) * eighth;

					for (int c4Y = 0; c4Y < 8; ++c4Y) {
						double quarter = 0.25D;
						double var33 = noise1;
						double var35 = noise2;
						double var37 = (noise3 - noise1) * quarter;
						double var39 = (noise4 - noise2) * quarter;

						for (int c5X = 0; c5X < 4; ++c5X) {
							int x = c5X + c1X * 4;
							int y = c3Y * 8 + c4Y;
							int z = c2Z * 4;

							double var44 = 0.25D;
							double stonePos = var33;
							double var48 = (var35 - var33) * var44;

							for (int var50 = 0; var50 < 4; ++var50)
							{
								//TODO: Insert Logic
								if (stonePos > 0.0D)
								{
									// Re assign a bunch (it is stone)
									output[x][z] = y;
								}
								++z;
								stonePos += var48;
							}

							var33 += var37;
							var35 += var39;
						}

						noise1 += eightNoise1;
						noise2 += eightNoise2;
						noise3 += eightNoise3;
						noise4 += eightNoise4;
					}
				}
			}
		}
		return output;
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

		return Math.floorDiv(sum, numElem);
	}

	/* Probably REALLY slow */
	private int getSimulatedAvg3x3(int middleChunkX, int middleChunkZ)
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
		return Math.floorDiv(sum, numE);
	}


	/* Generates Octaves similarly to the Chunk Generator */
	@Override
	public double[] generateOctaves(double[] var1, int var2, int var3, int var4, int size1, int size2, int size3)
	{
		if (var1 == null) {
			var1 = new double[size1 * size2 * size3];
		}

		double var8 = 684.412D;
		double var10 = 684.412D;
		this.octave4Arr =
				this.octaves4.generateNoiseOctaves(this.octave4Arr, (double) var2, (double) var3, (double) var4, size1, 1, size3, 1.0D, 0.0D, 1.0D);
		this.octave5Arr = this.octaves5
				.generateNoiseOctaves(this.octave5Arr, (double) var2, (double) var3, (double) var4, size1, 1, size3, 100.0D, 0.0D, 100.0D);
		this.octave3Arr = this.octaves3
				.generateNoiseOctaves(this.octave3Arr, (double) var2, (double) var3, (double) var4, size1, size2, size3, var8 / 80.0D, var10 / 160.0D,
						var8 / 80.0D);
		this.octave1Arr = this.octaves1
				.generateNoiseOctaves(this.octave1Arr, (double) var2, (double) var3, (double) var4, size1, size2, size3, var8, var10, var8);
		this.octave2Arr = this.octaves2
				.generateNoiseOctaves(this.octave2Arr, (double) var2, (double) var3, (double) var4, size1, size2, size3, var8, var10, var8);
		int var12 = 0;
		int var13 = 0;

		for (int var14 = 0; var14 < size1; ++var14) {
			for (int var15 = 0; var15 < size3; ++var15) {
				double var16 = (this.octave4Arr[var13] + 256.0D) / 512.0D;
				if (var16 > 1.0D) {
					var16 = 1.0D;
				}

				double var18 = 0.0D;
				double var20 = this.octave5Arr[var13] / 8000.0D;
				if (var20 < 0.0D) {
					var20 = -var20;
				}

				var20 = var20 * 3.0D - 3.0D;
				if (var20 < 0.0D) {
					var20 = var20 / 2.0D;
					if (var20 < -1.0D) {
						var20 = -1.0D;
					}

					var20 = var20 / 1.4D;
					var20 = var20 / 2.0D;
					var16 = 0.0D;
				} else {
					if (var20 > 1.0D) {
						var20 = 1.0D;
					}

					var20 = var20 / 6.0D;
				}

				var16 = var16 + 0.5D;
				var20 = var20 * (double) size2 / 16.0D;
				double var22 = (double) size2 / 2.0D + var20 * 4.0D;
				++var13;

				for (int var24 = 0; var24 < size2; ++var24) {
					double var25 = 0.0D;
					double var27 = ((double) var24 - var22) * 12.0D / var16;
					if (var27 < 0.0D) {
						var27 *= 4.0D;
					}

					double var29 = this.octave1Arr[var12] / 512.0D;
					double var31 = this.octave2Arr[var12] / 512.0D;
					double var33 = (this.octave3Arr[var12] / 10.0D + 1.0D) / 2.0D;
					if (var33 < 0.0D) {
						var25 = var29;
					} else if (var33 > 1.0D) {
						var25 = var31;
					} else {
						var25 = var29 + (var31 - var29) * var33;
					}

					var25 = var25 - var27;
					if (var24 > size2 - 4) {
						double var35 = (double) ((float) (var24 - (size2 - 4)) / 3.0F);
						var25 = var25 * (1.0D - var35) + -10.0D * var35;
					}

					if ((double) var24 < var18) {
						double var45 = (var18 - (double) var24) / 4.0D;
						if (var45 < 0.0D) {
							var45 = 0.0D;
						}

						if (var45 > 1.0D) {
							var45 = 1.0D;
						}

						var25 = var25 * (1.0D - var45) + -10.0D * var45;
					}

					var1[var12] = var25;
					++var12;
				}
			}
		}

		return var1;
	}
}
