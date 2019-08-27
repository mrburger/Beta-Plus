package com.mrburgerus.betaplus.world.biome;

import com.google.common.collect.Lists;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

import java.util.List;

// Ripped straight from Beta, minimally processed.
public class BiomeSelectorBeta extends AbstractBiomeSelector
{
	// Mappings //
	private static final Biome tundra = Biomes.SNOWY_TUNDRA;
	private static final Biome savanna = Biomes.SAVANNA;
	private static final Biome desert = Biomes.DESERT;
	private static final Biome swampland = Biomes.SWAMP;
	private static final Biome taiga = Biomes.SNOWY_TAIGA;
	private static final Biome shrubland = Biomes.SAVANNA;
	private static final Biome plains = Biomes.PLAINS;
	private static final Biome seasonalForest = Biomes.BIRCH_FOREST;
	private static final Biome rainforest = Biomes.FLOWER_FOREST;
	private static final Biome forest = Biomes.FOREST;
	// Hill declarations
	private static final Biome tundraHills = Biomes.SNOWY_MOUNTAINS;
	private static final Biome savannaHills = Biomes.SAVANNA_PLATEAU;
	private static final Biome desertHills = Biomes.DESERT_HILLS;
	private static final Biome swamplandHills = Biomes.SWAMP_HILLS;
	private static final Biome taigaHills = Biomes.SNOWY_TAIGA_HILLS;
	private static final Biome shrublandHills = Biomes.SAVANNA_PLATEAU;
	private static final Biome plainsHills = Biomes.FOREST; // The only Non-Beta Type. Confusing though because plains don't have hills.
	private static final Biome seasonalForestHills = Biomes.BIRCH_FOREST_HILLS;
	private static final Biome rainforestHills = Biomes.FLOWER_FOREST;
	private static final Biome forestHills = Biomes.FOREST;
	// Ocean and other declarations
	private static final Biome ocean = Biomes.OCEAN;
	private static final Biome deepOcean = Biomes.DEEP_OCEAN;
	private static final Biome defaultBiome = Biomes.PLAINS; // I don't know how to handle, oops
	private static final Biome beachBiome = Biomes.BEACH;


	// CONSTRUCTOR //

	public BiomeSelectorBeta()
	{
		super(Lists.newArrayList(beachBiome, desert, desertHills));
	}


	@Override
	public Biome getBiome(double temperature, double humidity, TerrainType type)
	{
		// Ripped from Beta, This is the basis for all other functions
		// Added some switch statement for hills and the like
		switch (type)
		{
			case land:
				return temperature < 0.1f ? tundra : (humidity < 0.2f ? (temperature < 0.5f ? tundra : (temperature < 0.95f ? savanna : desert)) : (humidity > 0.5f && temperature < 0.7f ? swampland : (temperature < 0.5f ? taiga : (temperature < 0.97f ? (humidity < 0.35f ? shrubland : forest) : (humidity < 0.45f ? plains : ((humidity *= temperature) < 0.9f ? seasonalForest : rainforest))))));
			case hillyLand:
				return temperature < 0.1f ? tundraHills : (humidity < 0.2f ? (temperature < 0.5f ? tundraHills : (temperature < 0.95f ? savannaHills : desertHills)) : (humidity > 0.5f && temperature < 0.7f ? swamplandHills : (temperature < 0.5f ? taigaHills : (temperature < 0.97f ? (humidity < 0.35f ? shrublandHills : forestHills) : (humidity < 0.45f ? plainsHills : ((humidity *= temperature) < 0.9f ? seasonalForestHills : rainforestHills))))));
			case sea:
				return ocean;
			case deepSea:
				return deepOcean;
			case coastal:
				return beachBiome;
			case island:
				return beachBiome; // Update later.
			case generic:
				return defaultBiome;
			default:
				return defaultBiome;
		}
	}
}
