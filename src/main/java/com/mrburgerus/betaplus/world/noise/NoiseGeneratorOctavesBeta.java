package com.mrburgerus.betaplus.world.noise;

import java.util.Random;

public class NoiseGeneratorOctavesBeta extends AbstractOctavesGenerator
{
	public NoiseGeneratorOctavesBeta(Random random, int boundIn)
	{
		super(boundIn);
		generatorCollection = new NoiseGeneratorPerlinBeta[bound];
		for (int i = 0; i < bound; i++)
		{
			generatorCollection[i] = new NoiseGeneratorPerlinBeta(random);
		}
	}

	public double[] generateNoiseOctaves(double[] doubles, int var2, int var3, int var4, int var5, double var6, double var8, double var10)
	{
		return generateNoiseOctaves(doubles, var2, 10.0D, var3, var4, 1, var5, var6, 1.0D, var8);
	}
}
