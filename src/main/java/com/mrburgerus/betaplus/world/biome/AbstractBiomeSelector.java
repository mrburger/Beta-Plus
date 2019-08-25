package com.mrburgerus.betaplus.world.biome;

import net.minecraft.world.biome.Biome;

// Abstract Biome Selector, to allow for multiple types of Biome usage.
// This will be used in 0.5 and later.
public abstract class AbstractBiomeSelector
{
	public abstract Biome getBiome(double temperature, double humidity, TerrainType terrainType);
}
