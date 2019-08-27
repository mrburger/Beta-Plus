package com.mrburgerus.betaplus.world.biome;


import com.google.common.collect.Lists;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

import java.util.List;

// Yet another Biome Selector, does some cool injection of modern biomes.
public class BiomeSelectorBetaPlus extends AbstractBiomeSelector
{

	public BiomeSelectorBetaPlus()
	{
		// Spawn on sand.
		super(Lists.newArrayList(Biomes.BEACH, Biomes.DESERT));
	}

	@Override
	public Biome getBiome(double temperature, double humidity, TerrainType terrainType)
	{
		return null;
	}
}
