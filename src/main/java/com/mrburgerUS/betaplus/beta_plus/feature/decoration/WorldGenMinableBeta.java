package com.mrburgerUS.betaplus.beta_plus.feature.decoration;

import com.mrburgerUS.betaplus.beta_plus.biome.BiomeGenBeta;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;

import java.util.Random;

public class WorldGenMinableBeta
{
	//Fields
	private static final int maxRockY = 63;

	public static void generateOres(World world, Random random, int chunkX, int chunkZ)
	{
		//Rocks
		generateOre(world, random, chunkX, chunkZ, 20, 128, 32, Blocks.DIRT.getDefaultState());
		generateOre(world, random, chunkX, chunkZ, 10, 128, 32, Blocks.GRAVEL.getDefaultState());
		generateOre(world, random, chunkX, chunkZ, 20, maxRockY, 32, Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE));
		generateOre(world, random, chunkX, chunkZ, 20, maxRockY, 32, Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE));
		generateOre(world, random, chunkX, chunkZ, 20, maxRockY, 32, Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE));

		// Ores
		generateOre(world, random, chunkX, chunkZ, 20, 128, 16, Blocks.COAL_ORE.getDefaultState());
		generateOre(world, random, chunkX, chunkZ, 20, 64, 8, Blocks.IRON_ORE.getDefaultState());
		generateOre(world, random, chunkX, chunkZ, 2, 32, 8, Blocks.GOLD_ORE.getDefaultState());
		generateOre(world, random, chunkX, chunkZ, 8, 16, 7, Blocks.REDSTONE_ORE.getDefaultState());
		generateOre(world, random, chunkX, chunkZ, 1, 16, 7, Blocks.DIAMOND_ORE.getDefaultState());
		generateOre2(world, random, chunkX, chunkZ, 1, 16, 6, Blocks.LAPIS_ORE.getDefaultState());

		//Emeralds
		if (world.getBiome(new BlockPos(chunkX * 16 + 8, 64, chunkZ * 16 + 8)) == BiomeGenBeta.seasonalForest.handle)
		{
			generateOre(world, random, chunkX, chunkZ, 1, 16, 4, Blocks.EMERALD_ORE.getDefaultState());
		}
	}


	public static void generateOre(World world, Random random, int chunkX, int chunkZ, int chances, int maxY, int numBlocks, IBlockState blockToGen)
	{
		int xVal = chunkX * 16;
		int zVal = chunkZ * 16;

		for (int i = 0; i < chances; ++i)
		{
			int xBase = xVal + random.nextInt(16);
			int yBase = random.nextInt(maxY);
			int zBase = zVal + random.nextInt(16);
			new WorldGenMinable(blockToGen, numBlocks).generate(world, random, new BlockPos(xBase, yBase, zBase));
		}

	}

	public static void generateOre2(World world, Random random, int chunkX, int chunkZ, int chances, int maxY, int numBlocks, IBlockState blockToGen)
	{
		int xVal = chunkX * 16;
		int zVal = chunkZ * 16;

		for (int i = 0; i < chances; ++i)
		{
			int xBase = xVal + random.nextInt(16);
			int yBase = random.nextInt(maxY) + random.nextInt(maxY);
			int zBase = zVal + random.nextInt(16);
			new WorldGenMinable(blockToGen, numBlocks).generate(world, random, new BlockPos(xBase, yBase, zBase));
		}
	}
}
