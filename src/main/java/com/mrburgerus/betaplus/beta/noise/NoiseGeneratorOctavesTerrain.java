package com.mrburgerus.betaplus.beta.noise;

import java.util.Random;

public class NoiseGeneratorOctavesTerrain
{
	private NoiseGeneratorPerlinBeta[] generatorCollection;
	private int boundNum;

	public NoiseGeneratorOctavesTerrain(Random random, int bound)
	{
		boundNum = bound;
		generatorCollection = new NoiseGeneratorPerlinBeta[bound];
		for (int i = 0; i < bound; i++)
		{
			generatorCollection[i] = new NoiseGeneratorPerlinBeta(random);
		}
	}

	public double func_806_a(double dMult1, double dMult2)
	{
		double returnVal = 0.0D;
		double descendingMult = 1.0D;
		for (int i = 0; i < boundNum; i++)
		{
			returnVal += generatorCollection[i].generateNoiseZero(dMult1 * descendingMult, dMult2 * descendingMult) / descendingMult;
			descendingMult /= 2.0D;
		}
		return returnVal;
	}

	public double[] generateNoiseOctaves(double[] doubles, double xC, double var4, double var6, int var8, int var9, int var10, double var11, double var13, double var15)
	{
		if (doubles == null)
		{
			doubles = new double[var8 * var9 * var10];
		}
		else
		{
			for (int i = 0; i < doubles.length; i++)
			{
				doubles[i] = 0.0D;
			}
		}
		double descendingMult = 1.0D;
		for (int i = 0; i < boundNum; i++)
		{
			generatorCollection[i].generate(doubles, xC, var4, var6, var8, var9, var10, var11 * descendingMult, var13 * descendingMult, var15 * descendingMult, descendingMult);
			descendingMult /= 2.0D;
		}
		return doubles;
	}

	public double[] generateNoiseOctaves(double[] doubles, int var2, int var3, int var4, int var5, double var6, double var8, double var10)
	{
		return generateNoiseOctaves(doubles, var2, 10.0D, var3, var4, 1, var5, var6, 1.0D, var8);
	}
}
