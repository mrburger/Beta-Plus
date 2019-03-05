package com.mrburgerUS.betaplus.beta_plus.biome;

import com.mrburgerUS.betaplus.beta_plus.biome.support.BiomeSelectBeta;
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
	private static final Biome[] biomeLookupTable;

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
		biomeLookupTable = new Biome[4096];
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
				biomeLookupTable[i + j * 64] = BiomeSelectBeta.getBiome((float) i / 63.0f, (float) j / 63.0f);
			}
		}
	}

	//Gets Value
	public static Biome getBiomeFromLookup(double temperature, double humidity)
	{
		int i = (int) (temperature * 63.0);
		int j = (int) (humidity * 63.0);
		return biomeLookupTable[i + j * 64];
	}



}
