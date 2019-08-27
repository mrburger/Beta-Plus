package com.mrburgerus.betaplus.world.beta_plus.gen;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.IChunk;

import java.util.Random;

public class BetaGenCaves extends AbstractBetaGen
{
	protected void generate(int chunkX, int chunkZ, IChunk chunk, double var4, double var6, double var8) {
		this.generate(chunkX, chunkZ, chunk, var4, var6, var8, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D);
	}

	protected void generate(int chunkX, int chunkZ, IChunk chunk, double var4, double var6, double var8, float var10, float var11, float var12,
							int var13, int var14, double var15) {
		double var17 = (double) (chunkX * 16 + 8);
		double var19 = (double) (chunkZ * 16 + 8);
		float var21 = 0.0F;
		float var22 = 0.0F;
		Random random = new Random(this.rand.nextLong());
		if (var14 <= 0) {
			int var24 = this.num8 * 16 - 16;
			var14 = var24 - random.nextInt(var24 / 4);
		}

		boolean var55 = false;
		if (var13 == -1) {
			var13 = var14 / 2;
			var55 = true;
		}

		int var25 = random.nextInt(var14 / 2) + var14 / 4;

		for (boolean var26 = random.nextInt(6) == 0; var13 < var14; ++var13) {
			double var27 = 1.5D + (double) (MathHelper.sin((float) var13 * 3.1415927F / (float) var14) * var10 * 1.0F);
			double var29 = var27 * var15;
			float var31 = MathHelper.cos(var12);
			float var32 = MathHelper.sin(var12);
			var4 += (double) (MathHelper.cos(var11) * var31);
			var6 += (double) var32;
			var8 += (double) (MathHelper.sin(var11) * var31);
			if (var26) {
				var12 = var12 * 0.92F;
			} else {
				var12 = var12 * 0.7F;
			}

			var12 = var12 + var22 * 0.1F;
			var11 += var21 * 0.1F;
			var22 = var22 * 0.9F;
			var21 = var21 * 0.75F;
			var22 = var22 + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			var21 = var21 + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
			if (!var55 && var13 == var25 && var10 > 1.0F) {
				this.generate(chunkX, chunkZ, chunk, var4, var6, var8, random.nextFloat() * 0.5F + 0.5F, var11 - 1.5707964F, var12 / 3.0F, var13,
						var14, 1.0D);
				this.generate(chunkX, chunkZ, chunk, var4, var6, var8, random.nextFloat() * 0.5F + 0.5F, var11 + 1.5707964F, var12 / 3.0F, var13,
						var14, 1.0D);
				return;
			}

			if (var55 || random.nextInt(4) != 0) {
				double var33 = var4 - var17;
				double var35 = var8 - var19;
				double var37 = (double) (var14 - var13);
				double var39 = (double) (var10 + 2.0F + 16.0F);
				if (var33 * var33 + var35 * var35 - var37 * var37 > var39 * var39) {
					return;
				}

				if (var4 >= var17 - 16.0D - var27 * 2.0D && var8 >= var19 - 16.0D - var27 * 2.0D && var4 <= var17 + 16.0D + var27 * 2.0D
						&& var8 <= var19 + 16.0D + var27 * 2.0D) {
					int var56 = MathHelper.floor(var4 - var27) - chunkX * 16 - 1;
					int var34 = MathHelper.floor(var4 + var27) - chunkX * 16 + 1;
					int var57 = MathHelper.floor(var6 - var29) - 1;
					int y = MathHelper.floor(var6 + var29) + 1;
					int var58 = MathHelper.floor(var8 - var27) - chunkZ * 16 - 1;
					int var38 = MathHelper.floor(var8 + var27) - chunkZ * 16 + 1;
					if (var56 < 0) {
						var56 = 0;
					}

					if (var34 > 16) {
						var34 = 16;
					}

					if (var57 < 1) {
						var57 = 1;
					}

					if (y > 120) {
						y = 120;
					}

					if (var58 < 0) {
						var58 = 0;
					}

					if (var38 > 16) {
						var38 = 16;
					}

					boolean var59 = false;

					for (int x = var56; !var59 && x < var34; ++x) {
						for (int z = var58; !var59 && z < var38; ++z) {
							for (int y2 = y + 1; !var59 && y2 >= var57 - 1; --y2) {
								//int var43 = (x * 16 + z) * 128 + y2;
								if (y2 >= 0 && y2 < 128) {
									Block block = chunk.getBlockState(new BlockPos(x, y2, z)).getBlock();
									if (block == Blocks.WATER) {
										var59 = true;
									}

									if (y2 != var57 - 1 && x != var56 && x != var34 - 1 && z != var58 && z != var38 - 1) {
										y2 = var57;
									}
								}
							}
						}
					}

					if (!var59) {
						for (int x = var56; x < var34; ++x) {
							double var61 = ((double) (x + chunkX * 16) + 0.5D - var4) / var27;

							for (int z = var58; z < var38; ++z) {
								double var44 = ((double) (z + chunkZ * 16) + 0.5D - var8) / var27;
								//int var46 = (x * 16 + z) * 128 + y;
								int y2 = y;
								boolean var47 = false;

								if (var61 * var61 + var44 * var44 < 1.0D) {
									for (int var48 = y - 1; var48 >= var57; --var48) {
										double var49 = ((double) var48 + 0.5D - var6) / var29;
										if (var49 > -0.7D && var61 * var61 + var49 * var49 + var44 * var44 < 1.0D) {
											Block block = chunk.getBlockState(new BlockPos(x, y2, z)).getBlock();
											if (block == Blocks.GRASS_BLOCK) {
												var47 = true;
											}

											if (block == Blocks.STONE || block == Blocks.DIRT || block == Blocks.GRASS_BLOCK)
											{
												if (var48 < 10)
												{
													chunk.setBlockState(new BlockPos(x, y2, z), Blocks.LAVA.getDefaultState(), false);
												}
												else
												{
													chunk.setBlockState(new BlockPos(x, y2, z), Blocks.CAVE_AIR.getDefaultState(), false);
													if (var47 && chunk.getBlockState(new BlockPos(x, y2 - 1, z)).getBlock() == Blocks.DIRT)
													{
														chunk.setBlockState(new BlockPos(x, y2 - 1, z), Blocks.GRASS_BLOCK.getDefaultState(), false);
													}
												}
											}
										}

										//--var46;
										--y2;
									}
								}
							}
						}

						if (var55) {
							break;
						}
					}
				}
			}
		}

	}

	@Override
	void func_868_a(int x, int z, int chunkX, int chunkZ, IChunk chunk)
	{
		int var7 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(40) + 1) + 1);
		if (this.rand.nextInt(15) != 0) {
			var7 = 0;
		}

		for (int var8 = 0; var8 < var7; ++var8) {
			double var9 = (double) (x * 16 + this.rand.nextInt(16));
			double var11 = (double) this.rand.nextInt(this.rand.nextInt(120) + 8);
			double var13 = (double) (z * 16 + this.rand.nextInt(16));
			int var15 = 1;
			if (this.rand.nextInt(4) == 0) {
				this.generate(chunkX, chunkZ, chunk, var9, var11, var13);
				var15 += this.rand.nextInt(4);
			}

			for (int var16 = 0; var16 < var15; ++var16) {
				float var17 = this.rand.nextFloat() * 3.1415927F * 2.0F;
				float var18 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
				float var19 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
				this.generate(chunkX, chunkZ, chunk, var9, var11, var13, var19, var17, var18, 0, 0, 1.0D);
			}
		}

	}
}
