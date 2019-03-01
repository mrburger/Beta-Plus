package com.mrburgerus.betaplus.world.noise;

import java.util.Random;

//TODO: MAKE THIS EXTEND THE ABSTRACT CLASS AND UNIFY.
public class NoiseGeneratorOctavesAlphaOLD extends AbstractOctavesGenerator
{

    private NoiseGeneratorPerlinAlphaOLD[] generatorCollection;

    public NoiseGeneratorOctavesAlphaOLD(Random random, int boundIn)
	{
        super(boundIn);
        this.generatorCollection = new NoiseGeneratorPerlinAlphaOLD[boundIn];

        for (int i = 0; i < boundIn; ++i) {
            this.generatorCollection[i] = new NoiseGeneratorPerlinAlphaOLD(random);
        }

    }

    public double[] generateNoiseOctaves(double[] values, double xVal, double yValZero, double zVal, int size1, int size2, int size3, double var11, double var13, double var15) {
        if (values == null) {
            values = new double[size1 * size2 * size3];
        } else {
            for (int i = 0; i < values.length; ++i)
            {
                values[i] = 0.0D;
            }
        }

        double divideByTwo = 1.0D;

        for (int i = 0; i < this.bound; ++i) {
            this.generatorCollection[i].generate(values, xVal, yValZero, zVal, size1, size2, size3, var11 * divideByTwo, var13 * divideByTwo, var15 * divideByTwo, divideByTwo);

            divideByTwo /= 2.0D;
        }

        return values;
    }
}
