package com.mrburgerus.betaplus.world.beta_plus.util;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;

public class BiomeReplaceUtil
{
	/* Converts Biome Array As Generated to a usable Biome Array */
	public static Biome[] convertBiomeArray(Biome[] biomesIn)
	{
		Biome[] biomesOut = new Biome[biomesIn.length];
		for (int i = 0; i < biomesOut.length; i++)
		{
			int place = (i & 15) << 4 | i >> 4 & 15;
			biomesOut[i] = biomesIn[place];
		}
		return biomesOut;
	}

	/* Gets the first solid block at a Position */
	public static int getSolidHeightY(BlockPos pos, IChunk chunk)
	{
		for (int y = 130; y >= 0; --y)
		{
			//DUH
			Block block = chunk.getBlockState(new BlockPos(pos.getX(), y, pos.getZ())).getBlock();
			if (block != Blocks.AIR && block != Blocks.WATER)
			{
				return y;
			}
		}
		return 0;
	}

}
