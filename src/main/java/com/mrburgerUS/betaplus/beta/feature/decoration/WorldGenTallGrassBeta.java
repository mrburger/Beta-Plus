package com.mrburgerUS.betaplus.beta.feature.decoration;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGenTallGrassBeta
{
	private static final int grassChance = 5;

	public static void generateTallGrass(World world, Random random, int posX, int posZ)
	{
		for (int i = 0; i < grassChance; ++i)
		{
			int xRand = random.nextInt(16) + 8;
			int zRand = random.nextInt(16) + 8;
			int yRandBound = world.getHeight(new BlockPos(posX, 0, posZ).add(xRand, 0, zRand)).getY() + 32;

			if (yRandBound > 0)
			{
				int yRand = random.nextInt(yRandBound);
				BlockPos blockPos = new BlockPos(posX, 0, posZ).add(xRand, yRand, zRand);
				world.getBiome(blockPos).getRandomWorldGenForGrass(random).generate(world, random, blockPos);
			}
		}
	}
}
