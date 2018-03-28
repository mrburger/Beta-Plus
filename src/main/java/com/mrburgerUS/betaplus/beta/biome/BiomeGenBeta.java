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
	desert(Biomes.DESERT, (Block) Blocks.SAND, (Block) Blocks.SAND),
	plains(Biomes.PLAINS),
	tundra(Biomes.ICE_PLAINS),
	//New Enums
	ocean(Biomes.OCEAN),
	deepOcean(Biomes.DEEP_OCEAN),
	beach(Biomes.BEACH),
	roofForest(Biomes.ROOFED_FOREST),
	iceSpikes(Biomes.MUTATED_ICE_FLATS);

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
	private BiomeGenBeta(Biome handle)
	{
		this(handle, (Block) Blocks.GRASS, Blocks.DIRT);
	}

	private BiomeGenBeta(Biome biomeHandle, Block top, Block filler)
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
				biomeLookupTable[i + j * 64] = getBiomeNew((float) i / 63.0f, (float) j / 63.0f);
			}
		}
	}

	public static BiomeGenBeta getBiomeFromLookup(double temperature, double humidity)
	{
		int var4 = (int) (temperature * 63.0);
		int var5 = (int) (humidity * 63.0);
		return biomeLookupTable[var4 + var5 * 64];
	}

	public static BiomeGenBeta getBiome(float temperature, float humidity)
	{
		return temperature < 0.1f ? tundra : (humidity < 0.2f ? (temperature < 0.5f ? tundra : (temperature < 0.95f ? savanna : desert)) : (humidity > 0.5f && temperature < 0.7f ? swampland : (temperature < 0.5f ? taiga : (temperature < 0.97f ? (humidity < 0.35f ? shrubland : forest) : (humidity < 0.45f ? plains : ((humidity *= temperature) < 0.9f ? seasonalForest : rainforest))))));
	}

	public static BiomeGenBeta getBiomeNew(float temperature, float humidity)
	{
		BiomeGenBeta betaBiome;
		if (temperature < 0.1)
		{
			betaBiome = iceSpikes;
		}
		else if (humidity < 0.2 && temperature < 0.5)
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
		else if (humidity > 0.5 && temperature < 0.7)
		{
			betaBiome = swampland;
		}
		else if (humidity > 0.5 && temperature < 0.5)
		{
			betaBiome = taiga;
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
		else if ((humidity *= temperature) < 0.9)
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
}
