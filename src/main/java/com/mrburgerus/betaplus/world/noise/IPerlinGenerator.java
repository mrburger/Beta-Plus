package com.mrburgerus.betaplus.world.noise;

public interface IPerlinGenerator
{
	/* Generates Perlin Noise */
	void generate(double[] values, double x, double y, double z, int i, int j, int k, double xNoise, double yNoise, double zNoise, double multiplier);

	/* Grad-ify */
	double grad(int var1, double var2, double var4, double var6);

	/* Linear Interpolation */
	double lerp(double d1, double d2, double d3);
}
