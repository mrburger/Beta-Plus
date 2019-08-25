package com.mrburgerus.betaplus.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

// Ripped straight from Beta, minimally processed.
public class BiomeSelectorBeta extends AbstractBiomeSelector
{
	// Mappings //
	private Biome tundra = Biomes.SNOWY_TUNDRA;
	private Biome savanna = Biomes.SAVANNA;
	private Biome desert = Biomes.DESERT;
	private Biome swampland = Biomes.SWAMP;
	private Biome taiga = Biomes.SNOWY_TAIGA;
	private Biome shrubland = Biomes.SAVANNA;
	private Biome plains = Biomes.PLAINS;
	private Biome seasonalForest = Biomes.BIRCH_FOREST;
	private Biome rainforest = Biomes.FLOWER_FOREST;
	private Biome forest = Biomes.FOREST;
	// Hill declarations
	private Biome tundraHills = Biomes.SNOWY_MOUNTAINS;
	private Biome savannaHills = Biomes.SAVANNA_PLATEAU;
	private Biome desertHills = Biomes.DESERT_HILLS;
	private Biome swamplandHills = Biomes.SWAMP_HILLS;
	private Biome taigaHills = Biomes.SNOWY_TAIGA_HILLS;
	private Biome shrublandHills = Biomes.SAVANNA_PLATEAU;
	private Biome plainsHills = Biomes.FOREST; // The only Non-Beta Type. Confusing though bbecause plains don't have hills.
	private Biome seasonalForestHills = Biomes.BIRCH_FOREST_HILLS;
	private Biome rainforestHills = Biomes.FLOWER_FOREST;
	private Biome forestHills = Biomes.FOREST;
	// Ocean and other declarations
	private Biome ocean = Biomes.OCEAN;
	private Biome deepOcean = Biomes.DEEP_OCEAN;
	private Biome defaultBiome = Biomes.DEFAULT;

	@Override
	public Biome getBiome(double temperature, double humidity, TerrainType type)
	{
		// Ripped from Beta, This is the basis for all other functions
		if (type == TerrainType.land)
		{
			return temperature < 0.1f ? tundra : (humidity < 0.2f ? (temperature < 0.5f ? tundra : (temperature < 0.95f ? savanna : desert)) : (humidity > 0.5f && temperature < 0.7f ? swampland : (temperature < 0.5f ? taiga : (temperature < 0.97f ? (humidity < 0.35f ? shrubland : forest) : (humidity < 0.45f ? plains : ((humidity *= temperature) < 0.9f ? seasonalForest : rainforest))))));
		}
		// Same as above, but with Hills.
		else if (type == TerrainType.hillyLand)
		{
			return temperature < 0.1f ? tundraHills : (humidity < 0.2f ? (temperature < 0.5f ? tundraHills : (temperature < 0.95f ? savannaHills : desertHills)) : (humidity > 0.5f && temperature < 0.7f ? swamplandHills : (temperature < 0.5f ? taigaHills : (temperature < 0.97f ? (humidity < 0.35f ? shrublandHills : forestHills) : (humidity < 0.45f ? plainsHills : ((humidity *= temperature) < 0.9f ? seasonalForestHills : rainforestHills))))));
		}
		else if (type == TerrainType.sea)
		{
			return ocean;
		}
		else if (type == TerrainType.deepSea)
		{
			return  deepOcean;
		}
		else
		{
			return defaultBiome;
		}
	}
}
