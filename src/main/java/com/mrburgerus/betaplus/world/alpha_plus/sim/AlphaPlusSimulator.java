package com.mrburgerus.betaplus.world.alpha_plus.sim;

import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.world.noise.NoiseGeneratorOctavesAlpha;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;

import java.util.Arrays;
import java.util.Random;

/* Simulates Y values */
public class AlphaPlusSimulator
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

	public AlphaPlusSimulator(World world)
	{
		seed = world.getSeed();
		// Test with Seed 69: It is consistent across loads, with 65 in top left and 54 bottom right
		// Seed 69 with 2 unused Declarations: 93 top left, 76 bottom right
		rand = new Random(seed);
		this.octaves1 = new NoiseGeneratorOctavesAlpha(this.rand, 16);
		this.octaves2 = new NoiseGeneratorOctavesAlpha(this.rand, 16);
		this.octaves3 = new NoiseGeneratorOctavesAlpha(this.rand, 8);
		/* Testing to see if Random is affected by declarations */
		/* It does affect it, from what I see */
		// Declarations to fix the simulator (hopefully, probably)
		new NoiseGeneratorOctavesAlpha(this.rand, 4);
		new NoiseGeneratorOctavesAlpha(this.rand, 4);
		this.octaves4 = new NoiseGeneratorOctavesAlpha(this.rand, 10);
		this.octaves5 = new NoiseGeneratorOctavesAlpha(this.rand, 16);

		// Simulate at (0,0) Chunk
		simulateY(new BlockPos(0,0,0), 1, 1);
	}

	/* Simulates Y-height every 4 blocks for the Biome Provider. This helps determine where oceans will most likely be injected. */
	/* For a 1x1 size, simulates that block directly. */
	/* For a 16x16 (chunk), a 4x4 matrix will be created using the values at 0,0 to 12,12 in the chunk. */
	//TODO: Write properly
	//TODO: Figure out a fast way to do this.
	public int[] simulateY(BlockPos pos, int xSize, int zSize)
	{
		// First, determine chunk position
		int chunkX = pos.getX() >> 4;
		int chunkZ = pos.getZ() >> 4;

		int[][] arr1 = simulateChunkY(chunkX, chunkZ);
		int[][] arr2 = simulateChunkYFast(chunkX, chunkZ);

		BetaPlus.LOGGER.info("Begin Dump");
		for (int c = 0; c < arr1.length; c++)
		{
			BetaPlus.LOGGER.info(Arrays.toString(arr1[c]));
		}
		BetaPlus.LOGGER.info("End Dump");

		return new int[0];
	}

	/* Simulate Y Values every 4 blocks in a chunk */
	private int[][] simulateChunkYFast(int chunkX, int chunkZ)
	{
		int[][] output = new int[4][4];

		byte var4 = 4;
		int var6 = var4 + 1;
		byte var7 = 17;
		int var8 = var4 + 1;
		this.heightNoise = this.generateOctaves(this.heightNoise, chunkX * var4, 0, chunkZ * var4, var6, var7, var8);


		return output;
	}

	/* Simulates Y Values in a chunk */
	/* Ouput is 2D array Z, X (Which is strange, but whatever) */
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

	/* Generates Octaves similarly to the Chunk Generator */
	private double[] generateOctaves(double[] values, int xChunkMult, int var3, int zChunkMult, int size1, int size2, int size3) {
		if (values == null) {
			values = new double[size1 * size2 * size3];
		}

		double scale1 = 684.412D;
		double scale2 = 684.412D;
		this.octave4Arr = this.octaves4.generateNoiseOctaves(this.octave4Arr, (double) xChunkMult, (double) var3, (double) zChunkMult, size1, 1, size3, 1.0D, 0.0D, 1.0D);
		this.octave5Arr = this.octaves5.generateNoiseOctaves(this.octave5Arr, (double) xChunkMult, (double) var3, (double) zChunkMult, size1, 1, size3, 100.0D, 0.0D, 100.0D);
		this.octave3Arr = this.octaves3.generateNoiseOctaves(this.octave3Arr, (double) xChunkMult, (double) var3, (double) zChunkMult, size1, size2, size3, scale1 / 80.0D, scale2 / 160.0D, scale1 / 80.0D);
		this.octave1Arr = this.octaves1.generateNoiseOctaves(this.octave1Arr, (double) xChunkMult, (double) var3, (double) zChunkMult, size1, size2, size3, scale1, scale2, scale1);
		this.octave2Arr = this.octaves2.generateNoiseOctaves(this.octave2Arr, (double) xChunkMult, (double) var3, (double) zChunkMult, size1, size2, size3, scale1, scale2, scale1);
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

					values[var12] = var25;
					++var12;
				}
			}
		}

		return values;
	}
}
