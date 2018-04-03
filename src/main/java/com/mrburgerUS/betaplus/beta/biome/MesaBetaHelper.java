package com.mrburgerUS.betaplus.beta.biome;

import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

import java.util.Arrays;
import java.util.Random;

public class MesaBetaHelper
{


	//MESA
	private static IBlockState[] clayBands;
	private static NoiseGeneratorPerlin clayBandsOffsetNoise;
	//Blocks
	private static final IBlockState HARDENED_CLAY = Blocks.HARDENED_CLAY.getDefaultState();
	private static final IBlockState STAINED_HARDENED_CLAY = Blocks.STAINED_HARDENED_CLAY.getDefaultState();
	private static final IBlockState ORANGE_STAINED_HARDENED_CLAY = STAINED_HARDENED_CLAY.withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE);

	private static void genClayBands(long seed)
	{
		clayBands = new IBlockState[64];
		Arrays.fill(clayBands, HARDENED_CLAY);
		Random random = new Random(seed);
		clayBandsOffsetNoise = new NoiseGeneratorPerlin(random, 1);

		for (int i = 0; i < 64; ++i)
		{
			i += random.nextInt(5) + 1;

			if (i < 64)
			{
				clayBands[i] = ORANGE_STAINED_HARDENED_CLAY;
			}
		}

		int bound = random.nextInt(4) + 2;

		for (int i = 0; i < bound; ++i)
		{
			int j = random.nextInt(3) + 1;
			int k = random.nextInt(64);

			for (int l = 0; k + l < 64 && l < j; ++l)
			{
				clayBands[k + l] = STAINED_HARDENED_CLAY.withProperty(BlockColored.COLOR, EnumDyeColor.YELLOW);
			}
		}

		int j2 = random.nextInt(4) + 2;

		for (int k2 = 0; k2 < j2; ++k2)
		{
			int i3 = random.nextInt(3) + 2;
			int l3 = random.nextInt(64);

			for (int i1 = 0; l3 + i1 < 64 && i1 < i3; ++i1)
			{
				clayBands[l3 + i1] = STAINED_HARDENED_CLAY.withProperty(BlockColored.COLOR, EnumDyeColor.BROWN);
			}
		}

		int l2 = random.nextInt(4) + 2;

		for (int j3 = 0; j3 < l2; ++j3)
		{
			int i4 = random.nextInt(3) + 1;
			int k4 = random.nextInt(64);

			for (int j1 = 0; k4 + j1 < 64 && j1 < i4; ++j1)
			{
				clayBands[k4 + j1] = STAINED_HARDENED_CLAY.withProperty(BlockColored.COLOR, EnumDyeColor.RED);
			}
		}

		int k3 = random.nextInt(3) + 3;
		int j4 = 0;

		for (int l4 = 0; l4 < k3; ++l4)
		{
			int i5 = 1;
			j4 += random.nextInt(16) + 4;

			for (int k1 = 0; j4 + k1 < 64 && k1 < 1; ++k1)
			{
				clayBands[j4 + k1] = STAINED_HARDENED_CLAY.withProperty(BlockColored.COLOR, EnumDyeColor.WHITE);

				if (j4 + k1 > 1 && random.nextBoolean())
				{
					clayBands[j4 + k1 - 1] = STAINED_HARDENED_CLAY.withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);
				}

				if (j4 + k1 < 63 && random.nextBoolean())
				{
					clayBands[j4 + k1 + 1] = STAINED_HARDENED_CLAY.withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);
				}
			}
		}
	}


	public static IBlockState getBand(int x, int y, int z, long seed)
	{
		genClayBands(seed);
		int i = (int) Math.round(clayBandsOffsetNoise.getValue((double) x / 512.0D, (double) x / 512.0D) * 2.0D);
		return clayBands[(y + i + 64) % 64];
	}
}
