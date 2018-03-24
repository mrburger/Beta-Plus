package com.mrburgerUS.betaplus.beta.biome;

import net.minecraft.world.biome.Biome;

public interface BetaPlusBiome
{
	public String name();

	public Biome getHandle();

	public void setHandle(Biome handle);
}
