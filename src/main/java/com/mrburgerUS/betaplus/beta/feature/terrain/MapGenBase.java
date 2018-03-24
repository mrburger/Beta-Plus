package com.mrburgerUS.betaplus.beta.feature.terrain;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.Random;

public class MapGenBase
{
	int chanceNumber = 8;
	Random rand = new Random();

	public void generate(World world, int chunkX, int chunkZ, ChunkPrimer chunk)
	{
		int var6 = chanceNumber;
		rand.setSeed(world.getSeed());
		long var7 = rand.nextLong() / 2 * 2 + 1;
		long var9 = rand.nextLong() / 2 * 2 + 1;
		for (int x = chunkX - var6; x <= chunkX + var6; ++x)
		{
			for (int z = chunkZ - var6; z <= chunkZ + var6; ++z)
			{
				rand.setSeed((long) x * var7 + (long) z * var9 ^ world.getSeed());
				generator(world, x, z, chunkX, chunkZ, chunk);
			}
		}
	}

	protected void generator(World world, int x, int z, int chunkX, int chunkZ, ChunkPrimer chunk)
	{
	}
}
