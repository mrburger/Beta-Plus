package com.mrburgerus.betaplus.world.noise;

import java.util.Random;

abstract class AbstractPerlinGenerator implements IPerlinGenerator
{
	// Fields
	int[] permutations;
	public double xCoord;
	public double yCoord;
	public double zCoord;

	/* Abstract Constructor */
	AbstractPerlinGenerator(Random random)
	{
		this.permutations = new int[512];
		this.xCoord = random.nextDouble() * 256.0D;
		this.yCoord = random.nextDouble() * 256.0D;
		this.zCoord = random.nextDouble() * 256.0D;

		// Carried Across Alpha and Beta, by the look of it
		for (int i = 0; i < 256; i++)
		{
			permutations[i] = i;
		}

		for (int j = 0; j < 256; j++)
		{
			int k = random.nextInt(256 - j) + j;
			int l = permutations[j];
			permutations[j] = permutations[k];
			permutations[k] = l;
			permutations[j + 256] = permutations[j];
		}
	}

	// Lerp? Whats that? LARPing?
	public double lerp(double d1, double d2, double d3) {
		return d2 + d1 * (d3 - d2);
	}

	// Should be the same function regardless of Perlin.
	public double grad(int var1, double var2, double var4, double var6)
	{
		int var8 = var1 & 15;
		double var9 = var8 < 8 ? var2 : var4;
		double var11 = var8 < 4 ? var4 : (var8 != 12 && var8 != 14 ? var6 : var2);
		return ((var8 & 1) == 0 ? var9 : -var9) + ((var8 & 2) == 0 ? var11 : -var11);
	}
}
