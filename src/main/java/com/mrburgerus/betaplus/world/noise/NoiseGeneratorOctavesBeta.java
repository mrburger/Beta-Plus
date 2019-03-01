package com.mrburgerus.betaplus.world.noise;

import java.util.Random;

public class NoiseGeneratorOctavesBeta extends AbstractOctavesGenerator
{
	private NoiseGeneratorPerlinBeta[] generatorCollection;

	public NoiseGeneratorOctavesBeta(Random random, int bound)
	{
		super(bound);
		generatorCollection = new NoiseGeneratorPerlinBeta[bound];
		for (int i = 0; i < bound; i++)
		{
			generatorCollection[i] = new NoiseGeneratorPerlinBeta(random);
		}
	}

	public double[] generateNoiseOctaves(double[] values, double xVal, double yValZero, double zVal, int size1, int size2, int size3, double var11, double var13, double var15)
	{
		if (values == null)
		{
			values = new double[size1 * size2 * size3];
		}
		else
		{
			for (int i = 0; i < values.length; i++)
			{
				values[i] = 0.0D;
			}
		}
		double divideByTwo = 1.0D;
		for (int i = 0; i < bound; i++)
		{
			generatorCollection[i].generate(values, xVal, yValZero, zVal, size1, size2, size3, var11 * divideByTwo, var13 * divideByTwo, var15 * divideByTwo, divideByTwo);
			divideByTwo /= 2.0D;
		}
		return values;
	}

	public double[] generateNoiseOctaves(double[] doubles, int var2, int var3, int var4, int var5, double var6, double var8, double var10)
	{
		return generateNoiseOctaves(doubles, var2, 10.0D, var3, var4, 1, var5, var6, 1.0D, var8);
	}
}
