package com.mrburgerUS.betaplus.beta_plus.biome;

import net.minecraft.world.biome.Biome;

public interface IBetaBiome
{
	String name();

	Biome getHandle();

	void setHandle(Biome handle);
}
