package com.mrburgerus.betaplus.world.noise;

public class AbstractOctavesGenerator implements IOctavesGenerator
{
	final int bound;
	IPerlinGenerator[] generatorCollection;

	/* Probably horrible Data Management, but I'm not a computer scientist! */
	AbstractOctavesGenerator(int boundIn)
	{
		this.bound = boundIn;
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
}
