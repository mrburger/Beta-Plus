package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.util.math.MathHelper;

public class NoiseGeneratorOctaves extends NoiseGenerator {
   /** Collection of noise generation functions.  Output is combined to produce different octaves of noise. */
   private final NoiseGeneratorImproved[] generatorCollection;
   private final int octaves;

   public NoiseGeneratorOctaves(Random seed, int octavesIn) {
      this.octaves = octavesIn;
      this.generatorCollection = new NoiseGeneratorImproved[octavesIn];

      for(int i = 0; i < octavesIn; ++i) {
         this.generatorCollection[i] = new NoiseGeneratorImproved(seed);
      }

   }

   public double func_205563_a(double p_205563_1_, double p_205563_3_, double p_205563_5_) {
      double d0 = 0.0D;
      double d1 = 1.0D;

      for(int i = 0; i < this.octaves; ++i) {
         d0 += this.generatorCollection[i].func_205560_c(p_205563_1_ * d1, p_205563_3_ * d1, p_205563_5_ * d1) / d1;
         d1 /= 2.0D;
      }

      return d0;
   }

   public double[] func_202647_a(int p_202647_1_, int p_202647_2_, int p_202647_3_, int p_202647_4_, int p_202647_5_, int p_202647_6_, double p_202647_7_, double p_202647_9_, double p_202647_11_) {
      double[] adouble = new double[p_202647_4_ * p_202647_5_ * p_202647_6_];
      double d0 = 1.0D;

      for(int i = 0; i < this.octaves; ++i) {
         double d1 = (double)p_202647_1_ * d0 * p_202647_7_;
         double d2 = (double)p_202647_2_ * d0 * p_202647_9_;
         double d3 = (double)p_202647_3_ * d0 * p_202647_11_;
         long j = MathHelper.lfloor(d1);
         long k = MathHelper.lfloor(d3);
         d1 = d1 - (double)j;
         d3 = d3 - (double)k;
         j = j % 16777216L;
         k = k % 16777216L;
         d1 = d1 + (double)j;
         d3 = d3 + (double)k;
         this.generatorCollection[i].populateNoiseArray(adouble, d1, d2, d3, p_202647_4_, p_202647_5_, p_202647_6_, p_202647_7_ * d0, p_202647_9_ * d0, p_202647_11_ * d0, d0);
         d0 /= 2.0D;
      }

      return adouble;
   }

   public double[] func_202646_a(int p_202646_1_, int p_202646_2_, int p_202646_3_, int p_202646_4_, double p_202646_5_, double p_202646_7_, double p_202646_9_) {
      return this.func_202647_a(p_202646_1_, 10, p_202646_2_, p_202646_3_, 1, p_202646_4_, p_202646_5_, 1.0D, p_202646_7_);
   }
}