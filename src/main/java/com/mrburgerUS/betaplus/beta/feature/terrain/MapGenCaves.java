package com.mrburgerUS.betaplus.beta.feature.terrain;

import com.mrburgerUS.betaplus.MathHelper;
import com.mrburgerUS.betaplus.beta.feature.MapGenBase;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.Random;

public class MapGenCaves extends MapGenBase
{
	protected void generate(int chunkX, int chunkZ, ChunkPrimer chunk, double var4, double var6, double var8)
	{
		generate(chunkX, chunkZ, chunk, var4, var6, var8, 1.0f + rand.nextFloat() * 6.0f, 0.0f, 0.0f, -1, -1, 0.5);
	}

	protected void generate(int chunkX, int chunkZ, ChunkPrimer chunk, double var4, double var6, double var8, float var10, float var11, float var12, int var13, int var14, double var15)
	{
		boolean chance1;
		double middleX = chunkX * 16 + 8;
		double middleZ = chunkZ * 16 + 8;
		float var21 = 0.0f;
		float var22 = 0.0f;
		Random random = new Random(rand.nextLong());
		if (var14 <= 0)
		{
			int var24 = range * 16 - 16;
			var14 = var24 - random.nextInt(var24 / 4);
		}
		boolean var55 = false;
		if (var13 == -1)
		{
			var13 = var14 / 2;
			var55 = true;
		}
		int var25 = random.nextInt(var14 / 2) + var14 / 4;
		chance1 = random.nextInt(6) == 0;
		while (var13 < var14)
		{
			double var27 = 1.5 + (double) (MathHelper.sin((float) var13 * 3.1415927f / (float) var14) * var10 * 1.0f);
			double var29 = var27 * var15;
			float var31 = MathHelper.cos(var12);
			float var32 = MathHelper.sin(var12);
			var4 += (double) (MathHelper.cos(var11) * var31);
			var6 += (double) var32;
			var8 += (double) (MathHelper.sin(var11) * var31);
			var12 = chance1 ? (var12 *= 0.92f) : (var12 *= 0.7f);
			var12 += var22 * 0.1f;
			var11 += var21 * 0.1f;
			var22 *= 0.9f;
			var21 *= 0.75f;
			var22 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0f;
			var21 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0f;
			if (!var55 && var13 == var25 && var10 > 1.0f)
			{
				generate(chunkX, chunkZ, chunk, var4, var6, var8, random.nextFloat() * 0.5f + 0.5f, var11 - 1.5707964f, var12 / 3.0f, var13, var14, 1.0);
				generate(chunkX, chunkZ, chunk, var4, var6, var8, random.nextFloat() * 0.5f + 0.5f, var11 + 1.5707964f, var12 / 3.0f, var13, var14, 1.0);
				return;
			}
			if (var55 || random.nextInt(4) != 0)
			{
				double var33 = var4 - middleX;
				double var35 = var8 - middleZ;
				double var37 = var14 - var13;
				double var39 = var10 + 2.0f + 16.0f;
				if (var33 * var33 + var35 * var35 - var37 * var37 > var39 * var39)
				{
					return;
				}
				if (var4 >= middleX - 16.0 - var27 * 2.0 && var8 >= middleZ - 16.0 - var27 * 2.0 && var4 <= middleX + 16.0 + var27 * 2.0 && var8 <= middleZ + 16.0 + var27 * 2.0)
				{
					int x;
					int var56 = MathHelper.floor_double(var4 - var27) - chunkX * 16 - 1;
					int var34 = MathHelper.floor_double(var4 + var27) - chunkX * 16 + 1;
					int var57 = MathHelper.floor_double(var6 - var29) - 1;
					int y = MathHelper.floor_double(var6 + var29) + 1;
					int var58 = MathHelper.floor_double(var8 - var27) - chunkZ * 16 - 1;
					int var38 = MathHelper.floor_double(var8 + var27) - chunkZ * 16 + 1;
					if (var56 < 0)
					{
						var56 = 0;
					}
					if (var34 > 16)
					{
						var34 = 16;
					}
					if (var57 < 1)
					{
						var57 = 1;
					}
					if (y > 120)
					{
						y = 120;
					}
					if (var58 < 0)
					{
						var58 = 0;
					}
					if (var38 > 16)
					{
						var38 = 16;
					}
					boolean var59 = false;
					for (x = var56; !var59 && x < var34; ++x)
					{
						for (int z = var58; !var59 && z < var38; ++z)
						{
							for (int y2 = y + 1; !var59 && y2 >= var57 - 1; --y2)
							{
								if (y2 < 0 || y2 >= 128) continue;
								Block block = chunk.getBlockState(x, y2, z).getBlock();
								if (block == Blocks.WATER || block == Blocks.FLOWING_WATER)
								{
									var59 = true;
								}
								if (y2 == var57 - 1 || x == var56 || x == var34 - 1 || z == var58 || z == var38 - 1)
									continue;
								y2 = var57;
							}
						}
					}
					if (!var59)
					{
						for (x = var56; x < var34; ++x)
						{
							double var61 = ((double) (x + chunkX * 16) + 0.5 - var4) / var27;
							for (int z = var58; z < var38; ++z)
							{
								double var44 = ((double) (z + chunkZ * 16) + 0.5 - var8) / var27;
								int y2 = y;
								boolean var47 = false;
								if (var61 * var61 + var44 * var44 >= 1.0) continue;
								for (int var48 = y - 1; var48 >= var57; --var48)
								{
									double var49 = ((double) var48 + 0.5 - var6) / var29;
									if (var49 > -0.7 && var61 * var61 + var49 * var49 + var44 * var44 < 1.0)
									{
										Block block = chunk.getBlockState(x, y2, z).getBlock();
										if (block == Blocks.GRASS)
										{
											var47 = true;
										}
										if (block == Blocks.STONE || block == Blocks.DIRT || block == Blocks.GRASS)
										{
											if (var48 < 10)
											{
												chunk.setBlockState(x, y2, z, Blocks.LAVA.getDefaultState());
											}
											else
											{
												chunk.setBlockState(x, y2, z, Blocks.AIR.getDefaultState());
												if (var47 && chunk.getBlockState(x, y2 - 1, z).getBlock() == Blocks.DIRT)
												{
													chunk.setBlockState(x, y2 - 1, z, Blocks.GRASS.getDefaultState());
												}
											}
										}
									}
									--y2;
								}
							}
						}
						if (var55) break;
					}
				}
			}
			++var13;
		}
	}

	@Override
	protected void generator(World world, int x, int z, int chunkX, int chunkZ, ChunkPrimer chunk)
	{
		int var7 = rand.nextInt(rand.nextInt(rand.nextInt(40) + 1) + 1);
		if (rand.nextInt(15) != 0)
		{
			var7 = 0;
		}
		for (int i = 0; i < var7; ++i)
		{
			double var9 = x * 16 + rand.nextInt(16);
			double var11 = rand.nextInt(rand.nextInt(120) + 8);
			double var13 = z * 16 + rand.nextInt(16);
			int var15 = 1;
			if (rand.nextInt(4) == 0)
			{
				generate(chunkX, chunkZ, chunk, var9, var11, var13);
				var15 += rand.nextInt(4);
			}
			for (int var16 = 0; var16 < var15; ++var16)
			{
				float var17 = rand.nextFloat() * 3.1415927f * 2.0f;
				float var18 = (rand.nextFloat() - 0.5f) * 2.0f / 8.0f;
				float var19 = rand.nextFloat() * 2.0f + rand.nextFloat();
				generate(chunkX, chunkZ, chunk, var9, var11, var13, var19, var17, var18, 0, 0, 1.0);
			}
		}
	}
}
