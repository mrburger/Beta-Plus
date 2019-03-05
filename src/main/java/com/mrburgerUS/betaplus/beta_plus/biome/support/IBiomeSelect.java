package com.mrburgerUS.betaplus.beta_plus.biome.support;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;

public interface IBiomeSelect
{
	// Gets the Biome for the specified values
	static Biome getBiome(float temperature, float humidity)
	{
		return Biomes.MESA_CLEAR_ROCK;
	}
}
