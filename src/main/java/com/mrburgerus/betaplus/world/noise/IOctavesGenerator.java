package com.mrburgerus.betaplus.world.noise;

interface IOctavesGenerator
{
	/* Generates Octaves Using Perlin Segments */
	double[] generateNoiseOctaves(double[] values, double xVal, double yValZero, double zVal, int size1, int size2, int size3, double var11, double var13, double var15);

}
