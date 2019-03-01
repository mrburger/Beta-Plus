package com.mrburgerus.betaplus.world.noise;

import java.util.Random;

public class NoiseGeneratorPerlinAlpha extends AbstractPerlinGenerator
{


	NoiseGeneratorPerlinAlpha(Random random)
	{
		super(random);
	}

	@Override
	public void generate(double[] values, double x, double y, double z, int i, int j, int k, double xNoise, double yNoise, double zNoise, double multiplier)
	{
		int var19 = 0;
		double var20 = 1.0D / multiplier;
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

		for (int counter = 0; counter < i; ++counter) {
			double var38 = (x + (double) counter) * xNoise + this.xCoord;
			int var40 = (int) var38;
			if (var38 < (double) var40) {
				--var40;
			}

			int var41 = var40 & 255;
			var38 = var38 - (double) var40;
			double var42 = var38 * var38 * var38 * (var38 * (var38 * 6.0D - 15.0D) + 10.0D);

			for (int var44 = 0; var44 < k; ++var44) {
				double var45 = (z + (double) var44) * zNoise + this.zCoord;
				int var47 = (int) var45;
				if (var45 < (double) var47) {
					--var47;
				}

				int var48 = var47 & 255;
				var45 = var45 - (double) var47;
				double var49 = var45 * var45 * var45 * (var45 * (var45 * 6.0D - 15.0D) + 10.0D);

				for (int var51 = 0; var51 < j; ++var51) {
					double var52 = (y + (double) var51) * yNoise + this.yCoord;
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
					values[var10001] += var62 * var20;
				}
			}
		}
	}
}
