package com.mrburgerus.betaplus.world.noise;

import java.util.Random;

@Deprecated
public class NoiseGeneratorOctavesBiomeOLD
{
	private NoiseGeneratorPerlinBiomeOLD[] generatorCollection;
	private int bound;

	public NoiseGeneratorOctavesBiomeOLD(Random random, int bound1)
	{
		bound = bound1;
		generatorCollection = new NoiseGeneratorPerlinBiomeOLD[bound1];
		for (int i = 0; i < bound1; i++)
		{
			generatorCollection[i] = new NoiseGeneratorPerlinBiomeOLD(random);
		}
	}

	// Probably Amplitude
	public double[] generateOctaves(double[] doubles, double xChunkCoord, double zChunkCoord, int xSize, int zSize, double var8, double var10, double amplitude)
	{
		return generateOctaves(doubles, xChunkCoord, zChunkCoord, xSize, zSize, var8, var10, amplitude, 0.5D);
	}

	public double[] generateOctaves(double[] doubles, double xChunk, double zChunk, int xSize, int zSize, double var8, double var10, double amplitude2, double multiplier)
	{
		var8 /= 1.5D;
		var10 /= 1.5D;
		if ((doubles != null) && (doubles.length >= xSize * zSize))
		{
			for (int i = 0; i < doubles.length; i++)
			{
				doubles[i] = 0.0D;
			}
		}
		else
		{
			doubles = new double[xSize * zSize];
		}
		double mult1 = 1.0D;
		double mult2 = 1.0D;
		for (int i = 0; i < bound; i++)
		{
			generatorCollection[i].generate(doubles, xChunk, zChunk, xSize, zSize, var8 * mult2, var10 * mult2, 0.55D / mult1);
			mult2 *= amplitude2;
			mult1 *= multiplier;
		}
		return doubles;
	}
}

