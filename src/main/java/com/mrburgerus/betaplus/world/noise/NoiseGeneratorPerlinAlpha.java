package com.mrburgerus.betaplus.world.noise;

import java.util.Random;

public class NoiseGeneratorPerlinAlpha extends NoiseGenerator {

    private int[] permutations;
    public double xCoord;
    public double yCoord;
    public double zCoord;

    public NoiseGeneratorPerlinAlpha(Random random) {
        this.permutations = new int[512];
        this.xCoord = random.nextDouble() * 256.0D;
        this.yCoord = random.nextDouble() * 256.0D;
        this.zCoord = random.nextDouble() * 256.0D;

        for (int var2 = 0; var2 < 256; this.permutations[var2] = var2++) {
        }

        for (int var5 = 0; var5 < 256; ++var5) {
            int var3 = random.nextInt(256 - var5) + var5;
            int var4 = this.permutations[var5];
            this.permutations[var5] = this.permutations[var3];
            this.permutations[var3] = var4;
            this.permutations[var5 + 256] = this.permutations[var5];
        }

    }

    public double generateNoise(double px, double py, double pz) {
        double dx = px + this.xCoord;
        double dy = py + this.yCoord;
        double dz = pz + this.zCoord;
        int ix = (int) dx;
        int iy = (int) dy;
        int iz = (int) dz;
        if (dx < (double) ix) {
            --ix;
        }

        if (dy < (double) iy) {
            --iy;
        }

        if (dz < (double) iz) {
            --iz;
        }

        int var16 = ix & 255;
        int var17 = iy & 255;
        int var18 = iz & 255;
        dx = dx - (double) ix;
        dy = dy - (double) iy;
        dz = dz - (double) iz;
        double var19 = dx * dx * dx * (dx * (dx * 6.0D - 15.0D) + 10.0D);
        double var21 = dy * dy * dy * (dy * (dy * 6.0D - 15.0D) + 10.0D);
        double var23 = dz * dz * dz * (dz * (dz * 6.0D - 15.0D) + 10.0D);
        int var25 = this.permutations[var16] + var17;
        int var26 = this.permutations[var25] + var18;
        int var27 = this.permutations[var25 + 1] + var18;
        int var28 = this.permutations[var16 + 1] + var17;
        int var29 = this.permutations[var28] + var18;
        int var30 = this.permutations[var28 + 1] + var18;
        return this.lerp(var23, this.lerp(var21,
                this.lerp(var19, this.grad(this.permutations[var26], dx, dy, dz), this.grad(this.permutations[var29], dx - 1.0D, dy, dz)),
                this.lerp(var19, this.grad(this.permutations[var27], dx, dy - 1.0D, dz),
                        this.grad(this.permutations[var30], dx - 1.0D, dy - 1.0D, dz))), this.lerp(var21,
                this.lerp(var19, this.grad(this.permutations[var26 + 1], dx, dy, dz - 1.0D),
                        this.grad(this.permutations[var29 + 1], dx - 1.0D, dy, dz - 1.0D)),
                this.lerp(var19, this.grad(this.permutations[var27 + 1], dx, dy - 1.0D, dz - 1.0D),
                        this.grad(this.permutations[var30 + 1], dx - 1.0D, dy - 1.0D, dz - 1.0D))));
    }

    public double lerp(double var1, double var3, double var5) {
        return var3 + var1 * (var5 - var3);
    }

    public double grad(int var1, double var2, double var4, double var6) {
        int var8 = var1 & 15;
        double var9 = var8 < 8 ? var2 : var4;
        double var11 = var8 < 4 ? var4 : (var8 != 12 && var8 != 14 ? var6 : var2);
        return ((var8 & 1) == 0 ? var9 : -var9) + ((var8 & 2) == 0 ? var11 : -var11);
    }

    public void func_805_a(double[] var1, double var2, double var4, double var6, int var8, int var9, int var10, double var11, double var13,
            double var15, double var17) {
        int var19 = 0;
        double var20 = 1.0D / var17;
        int var22 = -1;
        int var23 = 0;
        int var24 = 0;
        int var25 = 0;
        int var26 = 0;
        int var27 = 0;
        int var28 = 0;
        double var29 = 0.0D;
        double var31 = 0.0D;
        double var33 = 0.0D;
        double var35 = 0.0D;

        for (int var37 = 0; var37 < var8; ++var37) {
            double var38 = (var2 + (double) var37) * var11 + this.xCoord;
            int var40 = (int) var38;
            if (var38 < (double) var40) {
                --var40;
            }

            int var41 = var40 & 255;
            var38 = var38 - (double) var40;
            double var42 = var38 * var38 * var38 * (var38 * (var38 * 6.0D - 15.0D) + 10.0D);

            for (int var44 = 0; var44 < var10; ++var44) {
                double var45 = (var6 + (double) var44) * var15 + this.zCoord;
                int var47 = (int) var45;
                if (var45 < (double) var47) {
                    --var47;
                }

                int var48 = var47 & 255;
                var45 = var45 - (double) var47;
                double var49 = var45 * var45 * var45 * (var45 * (var45 * 6.0D - 15.0D) + 10.0D);

                for (int var51 = 0; var51 < var9; ++var51) {
                    double var52 = (var4 + (double) var51) * var13 + this.yCoord;
                    int var54 = (int) var52;
                    if (var52 < (double) var54) {
                        --var54;
                    }

                    int var55 = var54 & 255;
                    var52 = var52 - (double) var54;
                    double var56 = var52 * var52 * var52 * (var52 * (var52 * 6.0D - 15.0D) + 10.0D);
                    if (var51 == 0 || var55 != var22) {
                        var22 = var55;
                        var23 = this.permutations[var41] + var55;
                        var24 = this.permutations[var23] + var48;
                        var25 = this.permutations[var23 + 1] + var48;
                        var26 = this.permutations[var41 + 1] + var55;
                        var27 = this.permutations[var26] + var48;
                        var28 = this.permutations[var26 + 1] + var48;
                        var29 = this.lerp(var42, this.grad(this.permutations[var24], var38, var52, var45),
                                this.grad(this.permutations[var27], var38 - 1.0D, var52, var45));
                        var31 = this.lerp(var42, this.grad(this.permutations[var25], var38, var52 - 1.0D, var45),
                                this.grad(this.permutations[var28], var38 - 1.0D, var52 - 1.0D, var45));
                        var33 = this.lerp(var42, this.grad(this.permutations[var24 + 1], var38, var52, var45 - 1.0D),
                                this.grad(this.permutations[var27 + 1], var38 - 1.0D, var52, var45 - 1.0D));
                        var35 = this.lerp(var42, this.grad(this.permutations[var25 + 1], var38, var52 - 1.0D, var45 - 1.0D),
                                this.grad(this.permutations[var28 + 1], var38 - 1.0D, var52 - 1.0D, var45 - 1.0D));
                    }

                    double var58 = this.lerp(var56, var29, var31);
                    double var60 = this.lerp(var56, var33, var35);
                    double var62 = this.lerp(var49, var58, var60);
                    int var10001 = var19++;
                    var1[var10001] += var62 * var20;
                }
            }
        }

    }

}
