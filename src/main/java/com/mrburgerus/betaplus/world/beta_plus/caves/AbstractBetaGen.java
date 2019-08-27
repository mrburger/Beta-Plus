package com.mrburgerus.betaplus.world.beta_plus.caves;

import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;

import java.util.Random;

public abstract class AbstractBetaGen
{
	protected int field_1306_a = 8;
	protected Random rand = new Random();

	public void generate(long seed, int chunkX, int chunkZ, IChunk chunk) {
		int var6 = this.field_1306_a;
		this.rand.setSeed(seed);
		long var7 = this.rand.nextLong() / 2L * 2L + 1L;
		long var9 = this.rand.nextLong() / 2L * 2L + 1L;

		for (int x = chunkX - var6; x <= chunkX + var6; ++x) {
			for (int z = chunkZ - var6; z <= chunkZ + var6; ++z) {
				this.rand.setSeed((long) x * var7 + (long) z * var9 ^ seed);
				this.func_868_a(x, z, chunkX, chunkZ, chunk);
			}
		}

	}

	abstract void func_868_a(int x, int z, int chunkX, int chunkZ, IChunk chunk);
}
