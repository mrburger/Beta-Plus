package com.mrburgerus.betaplus.world.beta_plus.biome;

import net.minecraft.world.biome.Biome;

public interface IBetaPlusBiome
{
	String name();

	Biome getHandle();

	void setHandle(Biome handle);
}
