package com.mrburgerus.betaplus.world.alpha_plus.sim;

import com.mojang.datafixers.util.Pair;
import com.mrburgerus.betaplus.util.AbstractWorldSimulator;
import com.mrburgerus.betaplus.world.noise.NoiseGeneratorOctavesAlpha;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class AlphaPlusSimulator extends AbstractWorldSimulator
{

	public AlphaPlusSimulator(World world)
	{
		super(world);
		this.octaves1 = new NoiseGeneratorOctavesAlpha(this.rand, 16);
		this.octaves2 = new NoiseGeneratorOctavesAlpha(this.rand, 16);
		this.octaves3 = new NoiseGeneratorOctavesAlpha(this.rand, 8);
		// These are created ONLY to make sure the generation is precisely the same
		// Could be used later to implement the "Only Spawn on Sand" feature.
		new NoiseGeneratorOctavesAlpha(this.rand, 4);
		new NoiseGeneratorOctavesAlpha(this.rand, 4);
		this.scaleNoise = new NoiseGeneratorOctavesAlpha(this.rand, 10);
		this.octaves7 = new NoiseGeneratorOctavesAlpha(this.rand, 16);
	}

	@Override
	protected double[] generateOctaves(double[] values, int xChunkMult, int yValueZero, int zChunkMult, int size1, int size2, int size3)
	{
		if (values == null) {
			values = new double[size1 * size2 * size3];
		}

		double scaleX = 684.412D;
		double scaleZ = 684.412D;
		this.octaveArr4 =
				this.scaleNoise.generateNoiseOctaves(this.octaveArr4, (double) xChunkMult, (double) yValueZero, (double) zChunkMult, size1, 1, size3, 1.0D, 0.0D, 1.0D);
		this.octaveArr5 = this.octaves7
				.generateNoiseOctaves(this.octaveArr5, (double) xChunkMult, (double) yValueZero, (double) zChunkMult, size1, 1, size3, 100.0D, 0.0D, 100.0D);
		this.octaveArr3 = this.octaves3
				.generateNoiseOctaves(this.octaveArr3, (double) xChunkMult, (double) yValueZero, (double) zChunkMult, size1, size2, size3, scaleX / 80.0D, scaleZ / 160.0D,
						scaleX / 80.0D);
		this.octaveArr1 = this.octaves1
				.generateNoiseOctaves(this.octaveArr1, (double) xChunkMult, (double) yValueZero, (double) zChunkMult, size1, size2, size3, scaleX, scaleZ, scaleX);
		this.octaveArr2 = this.octaves2
				.generateNoiseOctaves(this.octaveArr2, (double) xChunkMult, (double) yValueZero, (double) zChunkMult, size1, size2, size3, scaleX, scaleZ, scaleX);
		int var12 = 0;
		int var13 = 0;

		for (int var14 = 0; var14 < size1; ++var14) {
			for (int var15 = 0; var15 < size3; ++var15) {
				double var16 = (this.octaveArr4[var13] + 256.0D) / 512.0D;
				if (var16 > 1.0D) {
					var16 = 1.0D;
				}

				double var18 = 0.0D;
				double var20 = this.octaveArr5[var13] / 8000.0D;
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

					double var29 = this.octaveArr1[var12] / 512.0D;
					double var31 = this.octaveArr2[var12] / 512.0D;
					double var33 = (this.octaveArr3[var12] / 10.0D + 1.0D) / 2.0D;
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

	/* Unused */
	@Override
	protected int simulateYZeroZeroChunk(ChunkPos pos)
	{
		return 0;
	}

	@Override
	protected Pair<int[][], Boolean> simulateChunkYFast(ChunkPos pos)
	{
		int[][] output = new int[4][4];

		// 1+1 converted to var4+1, like original
		byte var4 = 4;
		int var6 = var4 + 1;
		byte yHeight = 17;
		int var8 = var4 + 1;
		this.heightNoise = this.generateOctaves(this.heightNoise, pos.x * var4, 0, pos.z * var4, var6, yHeight, var8);

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
		return Pair.of(output, landValExists(output));
	}
}
