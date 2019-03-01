package com.mrburgerus.betaplus.world.noise;

public interface IPerlinGenerator
{
	/* Generates Perlin Noise */
	void generate(double[] values, double x, double z, double d2, int i, int j, int k, double d3, double d4, double d5, double d6);

	/* Grad-ify */
	double grad(int var1, double var2, double var4, double var6);

	/* Linear Interpolation */
	double lerp(double d1, double d2, double d3);
}
