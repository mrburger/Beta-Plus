package com.mrburgerus.betaplus.world.biome;

import net.minecraft.world.biome.Biome;

public interface IBetaPlusBiome
{
	String name();

	Biome getHandle();

	void setHandle(Biome handle);
}
