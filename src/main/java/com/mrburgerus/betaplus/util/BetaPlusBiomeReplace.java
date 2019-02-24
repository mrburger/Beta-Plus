package com.mrburgerus.betaplus.util;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class BetaPlusBiomeReplace
{
	/* Converts Biome Array As Generated to a usable Biome Array */
	public static Biome[] convertBiomeArray(Biome[] biomesIn)
	{
		Biome[] biomesOut = new Biome[biomesIn.length];
		for (int i = 0; i < biomesOut.length; i++)
		{
			//int z = i / 16;
			//int x = i % 16;
			int place = (i & 15) << 4 | i >> 4 & 15;
			//System.out.println(place + " " + x + " " + z + " " + (x << 4 | z));
			biomesOut[i] = biomesIn[place];
		}
		return biomesOut;
	}

	public static int getSolidHeightY(int x, int z, IChunk chunk)
	{
		for (int y = 130; y >= 0; --y)
		{
			Block block = chunk.getBlockState(new BlockPos(x, y, z)).getBlock();
			if (block != Blocks.AIR && block != Blocks.WATER)
			{
				return y;
			}
		}
		return 0;
	}

}