package com.mrburgerus.betaplus.world.noise;

import java.util.Random;

//TODO: MAKE THIS EXTEND THE ABSTRACT AND UNIFY.
public class NoiseGeneratorOctavesAlpha extends AbstractOctavesGenerator
{

    private NoiseGeneratorPerlinAlpha[] generatorCollection;

    public NoiseGeneratorOctavesAlpha(Random random, int boundIn) {
        super(boundIn);
        this.generatorCollection = new NoiseGeneratorPerlinAlpha[boundIn];

        for (int i = 0; i < boundIn; ++i) {
            this.generatorCollection[i] = new NoiseGeneratorPerlinAlpha(random);
        }

    }

    public double[] generateNoiseOctaves(double[] var1, double var2, double var4, double var6, int var8, int var9, int var10, double var11,
            double var13, double var15) {
        if (var1 == null) {
            var1 = new double[var8 * var9 * var10];
        } else {
            for (int var17 = 0; var17 < var1.length; ++var17) {
                var1[var17] = 0.0D;
            }
        }

        double var20 = 1.0D;

        for (int var19 = 0; var19 < this.bound; ++var19) {
            this.generatorCollection[var19].func_805_a(var1, var2, var4, var6, var8, var9, var10, var11 * var20, var13 * var20, var15 * var20, var20);
            var20 /= 2.0D;
        }

        return var1;
    }

    public double func_806_a(double xPos, double zPos) {
        double var5 = 0.0D;
        double descendingMult = 1.0D;

        for (int var9 = 0; var9 < this.bound; ++var9) {
            var5 += this.generatorCollection[var9].generateNoise(xPos * descendingMult, zPos * descendingMult, 0.0) / descendingMult;
            descendingMult /= 2.0D;
        }

        return var5;
    }
}
