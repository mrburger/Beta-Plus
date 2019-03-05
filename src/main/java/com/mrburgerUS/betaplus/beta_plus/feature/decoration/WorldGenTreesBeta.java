package com.mrburgerUS.betaplus.beta_plus.feature.decoration;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import java.util.Random;

import static com.mrburgerUS.betaplus.beta_plus.biome.BiomeGenBeta.*;

public class WorldGenTreesBeta
{
	// Partially Stolen from BiomeDecorator.class
	public static void generateTrees(World worldIn, Random random, BlockPos pos, Biome biomeIn)
	{
		int treeChance = 0;

		if (random.nextInt(10) == 0)
		{
			++treeChance;
		}
		treeChance += getTreeChance(biomeIn);

		for (int tryCount = 0; tryCount < treeChance; ++tryCount)
		{
			int xAdd = random.nextInt(16) + 8;
			int zAdd = random.nextInt(16) + 8;
			WorldGenAbstractTree worldgenabstracttree = biomeIn.getRandomTreeFeature(random);
			worldgenabstracttree.setDecorationDefaults();
			BlockPos blockpos = worldIn.getHeight(pos.add(xAdd, 0, zAdd));

			if (worldgenabstracttree.generate(worldIn, random, blockpos))
			{
				worldgenabstracttree.generateSaplings(worldIn, random, blockpos);
			}
		}
	}

	// UP FOR DEBATE
	private static int getTreeChance(Biome biome)
	{
		int treeOffset = 2;
		if (biome == beach.handle || biome == ocean.handle || biome == deepOcean.handle || biome == desert.handle)
			return -999;
		else if (biome == plains.handle || biome == tundra.handle)
			return -2 + treeOffset;
		else if (biome == savanna.handle || biome == shrubland.handle)
			return 0;
		else if (biome == swampland.handle)
			return treeOffset;
		else if (biome == seasonalForest.handle || biome == forest.handle || biome == taiga.handle)
			return 6 + treeOffset;
		else if (biome == rainforest.handle)
			return 9 + treeOffset;
		else
			return 0;
	}
}
