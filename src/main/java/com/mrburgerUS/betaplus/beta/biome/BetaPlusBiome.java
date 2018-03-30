package com.mrburgerUS.betaplus.beta.biome;

import net.minecraft.world.biome.Biome;

public interface BetaPlusBiome
{
	String name();

	Biome getHandle();

	void setHandle(Biome handle);
}
