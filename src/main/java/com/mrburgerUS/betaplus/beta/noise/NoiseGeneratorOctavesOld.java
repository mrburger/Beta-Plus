package com.mrburgerUS.betaplus.beta.noise;

import java.util.Random;

public class NoiseGeneratorOctavesOld
		extends NoiseGenerator
{
	private NoiseGenerator2[] generatorCollection;
	private int field_4233_b;

	public NoiseGeneratorOctavesOld(Random random, int bound1)
	{
		field_4233_b = bound1;
		generatorCollection = new NoiseGenerator2[bound1];
		for (int var3 = 0; var3 < bound1; var3++)
		{
			generatorCollection[var3] = new NoiseGenerator2(random);
		}
	}

	public double[] generateOctaves(double[] doubles, double xChunkCoord, double zChunkCoord, int var6, int var7, double var8, double var10, double var12)
	{
		return generateOctaves(doubles, xChunkCoord, zChunkCoord, var6, var7, var8, var10, var12, 0.5D);
	}

	public double[] generateOctaves(double[] doubles, double var2, double var4, int var6, int var7, double var8, double var10, double var12, double multiplier)
	{
		var8 /= 1.5D;
		var10 /= 1.5D;
		if ((doubles != null) && (doubles.length >= var6 * var7))
		{
			for (int i = 0; i < doubles.length; i++)
			{
				doubles[i] = 0.0D;
			}
		}
		else
		{
			doubles = new double[var6 * var7];
		}
		double mult1 = 1.0D;
		double mult2 = 1.0D;
		for (int i = 0; i < field_4233_b; i++)
		{
			generatorCollection[i].noiseBounder1(doubles, var2, var4, var6, var7, var8 * mult2, var10 * mult2, 0.55D / mult1);
			mult2 *= var12;
			mult1 *= multiplier;
		}
		return doubles;
	}
}
