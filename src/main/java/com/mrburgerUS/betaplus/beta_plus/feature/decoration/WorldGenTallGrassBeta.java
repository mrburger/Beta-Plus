package com.mrburgerUS.betaplus.beta_plus.feature.decoration;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Random;

import static com.mrburgerUS.betaplus.beta_plus.biome.BiomeGenBeta.*;

public class WorldGenTallGrassBeta
{
	public static void generateTallGrass(World world, Random random, int posX, int posZ)
	{
		for (int i = 0; i < getGrassChance(world.getBiome(new BlockPos(posX, 0, posZ))); ++i)
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

	private static int getGrassChance(Biome biome)
	{
		if (biome == beach.handle || biome == ocean.handle || biome == deepOcean.handle || biome == desert.handle || biome == tundra.handle)
			return 0;
		else if (biome == plains.handle)
			return 25;
		else if (biome == savanna.handle || biome == shrubland.handle)
			return 20;
		else if (biome == swampland.handle || biome == taiga.handle)
			return 5;
		else if (biome == seasonalForest.handle || biome == forest.handle)
			return 7;
		else if (biome == rainforest.handle)
			return 10;
		else
			return 2;
	}
}
