package com.mrburgerus.betaplus.beta.features.generator;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.Random;

public class MapGenBase
{
	protected int range = 4;
	protected Random rand = new Random();
	protected World world;

	public void generate(World world, int chunkX, int chunkZ, ChunkPrimer chunk)
	{
		int r2 = range;
		this.world = world;
		rand.setSeed(world.getSeed());
		long var7 = rand.nextLong() / 2 * 2 + 1;
		long var9 = rand.nextLong() / 2 * 2 + 1;
		for (int x = chunkX - r2; x <= chunkX + r2; ++x)
		{
			for (int z = chunkZ - r2; z <= chunkZ + r2; ++z)
			{
				rand.setSeed((long) x * var7 + (long) z * var9 ^ world.getSeed());
				recursiveGenerate(world, x, z, chunkX, chunkZ, chunk);
			}
		}
	}

	protected void recursiveGenerate(World world, int x, int z, int chunkX, int chunkZ, ChunkPrimer chunk)
	{
	}
}
