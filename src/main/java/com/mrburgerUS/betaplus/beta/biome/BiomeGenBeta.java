package com.mrburgerUS.betaplus.beta.biome;

import net.minecraft.block.Block;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;

public enum BiomeGenBeta implements BetaPlusBiome
{
	//Enums
	rainforest(Biomes.JUNGLE),
	swampland(Biomes.SWAMPLAND),
	seasonalForest(Biomes.MUTATED_FOREST),
	forest(Biomes.FOREST),
	savanna(Biomes.SAVANNA),
	shrubland(Biomes.PLAINS),
	taiga(Biomes.COLD_TAIGA),
	desert(Biomes.DESERT, Blocks.SAND, Blocks.SAND),
	plains(Biomes.PLAINS),
	tundra(Biomes.ICE_PLAINS),
	//New Enums
	ocean(Biomes.OCEAN),
	deepOcean(Biomes.DEEP_OCEAN),
	beach(Biomes.BEACH),
	roofForest(Biomes.ROOFED_FOREST),
	mountain(Biomes.EXTREME_HILLS),
	iceSpikes(Biomes.MUTATED_ICE_FLATS),
	megaTaiga(Biomes.REDWOOD_TAIGA),
	mesa(Biomes.MESA, Blocks.SAND, Blocks.HARDENED_CLAY),
	birchForest(Biomes.BIRCH_FOREST),
	flowerPlains(Biomes.MUTATED_PLAINS);

	//Overrides
	@Override
	public Biome getHandle()
	{
		return handle;
	}

	@Override
	public void setHandle(Biome biomeHandle)
	{
		handle = biomeHandle;
	}

	//Fields
	public Biome handle;
	public final Block topBlock;
	public final Block fillerBlock;
	private static final BiomeGenBeta[] biomeLookupTable;

	//Constructors
	BiomeGenBeta(Biome handle)
	{
		this(handle, Blocks.GRASS, Blocks.DIRT);
	}

	BiomeGenBeta(Biome biomeHandle, Block top, Block filler)
	{
		handle = biomeHandle;
		topBlock = top;
		fillerBlock = filler;
	}

	//Initialize
	static
	{
		biomeLookupTable = new BiomeGenBeta[4096];
		BiomeGenBeta.generateBiomeLookup();
	}

	//Methods
	public static void generateBiomeLookup()
	{
		for (int i = 0; i < 64; ++i)
		{
			for (int j = 0; j < 64; ++j)
			{
				//EDITED
				biomeLookupTable[i + j * 64] = getBiome3((float) i / 63.0f, (float) j / 63.0f);
			}
		}
	}

	public static BiomeGenBeta getBiomeFromLookup(double temperature, double humidity)
	{
		int i = (int) (temperature * 63.0);
		int j = (int) (humidity * 63.0);
		return biomeLookupTable[i + j * 64];
	}


	//Original, Ripped from Beta.
	public static BiomeGenBeta getBiomeOld(float temperature, float humidity)
	{
		return temperature < 0.1f ? tundra : (humidity < 0.2f ? (temperature < 0.5f ? tundra : (temperature < 0.95f ? savanna : desert)) : (humidity > 0.5f && temperature < 0.7f ? swampland : (temperature < 0.5f ? taiga : (temperature < 0.97f ? (humidity < 0.35f ? shrubland : forest) : (humidity < 0.45f ? plains : ((humidity *= temperature) < 0.9f ? seasonalForest : rainforest))))));
	}


	// Old, used in 0.2
	public static BiomeGenBeta getBiomeNew(float temperature, float humidity)
	{
		BiomeGenBeta betaBiome;
		if (temperature < 0.1)
		{
			betaBiome = iceSpikes;
		}
		else if (humidity < 0.15 && temperature < 0.5)
		{
			betaBiome = tundra;
		}
		else if (humidity < 0.2 && temperature < 0.95)
		{
			betaBiome = savanna;
		}
		else if (humidity < 0.2 && temperature > 0.95)
		{
			betaBiome = desert;
		}
		else if (humidity > 0.5 && temperature < 0.45)
		{
			betaBiome = roofForest;
		}
		else if (humidity > 0.6 && temperature < 0.7)
		{
			betaBiome = swampland;
		}
		else if (humidity > 0.5 && temperature < 0.5)
		{
			betaBiome = taiga;
		}
		else if (humidity > 0.5 && temperature < 0.65)
		{
			betaBiome = megaTaiga;
		}
		else if (temperature < 0.97 && humidity < 0.35)
		{
			betaBiome = shrubland;
		}
		else if (temperature < 0.97)
		{
			betaBiome = forest;
		}
		else if (humidity < 0.45)
		{
			betaBiome = plains;
		}
		else if ((humidity * temperature) < 0.9)
		{
			betaBiome = seasonalForest;
		}
		else
		{
			betaBiome = rainforest;
		}
		return betaBiome;
	}

	public static BiomeGenBeta getBiomeBeta(Biome biome)
	{
		for (BiomeGenBeta biomeBeta : BiomeGenBeta.values())
		{
			if (biomeBeta.handle == biome)
				return biomeBeta;
		}
		return BiomeGenBeta.plains;
	}


	//0.2.1
	public static BiomeGenBeta getBiomeBetaPlus(float temperature, float humidity)
	{
		humidity *= temperature;
		if (temperature < 0.1)
		{
			return iceSpikes;
		}
		//Mesas? In MY house?
		else if (temperature > 0.9825 && humidity < 0.02)
		{
			return mesa;
		}
		else if (humidity < 0.2)
		{
			if (temperature < 0.5)
			{
				return tundra;
			}
			else if (temperature < 0.9)
			{
				return savanna;
			}
			else
				return desert;
		}
		else if (humidity > 0.55 && temperature < 0.7)
		{
			if (temperature < 0.65)
			{
				return roofForest;
			}
			else
				return swampland;
		}
		else if (temperature < 0.5)
		{
			if (humidity < 0.35)
				return taiga;
			else
				return megaTaiga;
		}
		else if (temperature < 0.97)
		{
			if (humidity < 0.35)
			{
				return plains;
			}
			else
			{
				return forest;
			}
		}
		else if (humidity < 0.45)
		{
			return plains;
		}
		else if (humidity < 0.9)
		{
			return seasonalForest;
		}
		else
		{
			return rainforest;
		}
	}

	//0.3, Needs TWEAKING
	private static BiomeGenBeta getBiome3(double temperature, double humidity)
	{
		humidity *= temperature;
		if (temperature < 0.5)
		{
			if ((temperature < 0.1))
			{
				return iceSpikes;
			}
			if (humidity < 0.2)
			{
				return tundra;
			}
			return taiga;
		}
		if (temperature < 0.9375)
		{
			if (humidity < 0.2)
			{
				if (temperature > 0.8)
				{
					return savanna;
				}
				return plains;
			}
			if (humidity > 0.7)
			{
				return swampland;
			}
			if (humidity > 0.6)
			{
				return roofForest;
			}
			if (temperature < 0.7)
			{
				return megaTaiga;
			}
			if (humidity < 0.3)
			{
				if (humidity > 0.23)
				{
					return birchForest;
				}
				return seasonalForest;
			}
			return forest;
		}
		if (humidity < 0.2)
		{
			if (temperature > 0.98 && humidity < 0.02)
			{
				return mesa;
			}
			return desert;
		}
		if (humidity > 0.75D)
		{
			return rainforest;
		}
		return plains;
	}
}
