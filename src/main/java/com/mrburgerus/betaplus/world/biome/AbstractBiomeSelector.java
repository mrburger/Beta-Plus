package com.mrburgerus.betaplus.world.biome;

import net.minecraft.world.biome.Biome;

import java.util.List;

// Abstract Biome Selector, to allow for multiple types of Biome usage.
// This will be used in 0.5 and later.
public abstract class AbstractBiomeSelector
{
	// Fields
	public List<Biome> SPAWN_BIOMES;

	// MUST BE DECLARED
	public AbstractBiomeSelector(List<Biome> spawnBiomes)
	{
		SPAWN_BIOMES = spawnBiomes;
	}

	public abstract Biome getBiome(double temperature, double humidity, TerrainType terrainType);
}
