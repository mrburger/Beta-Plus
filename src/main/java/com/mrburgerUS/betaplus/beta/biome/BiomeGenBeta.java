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
	shrubland(Biomes.SAVANNA),
	taiga(Biomes.COLD_TAIGA),
	desert(Biomes.DESERT, (Block) Blocks.SAND, (Block) Blocks.SAND),
	plains(Biomes.PLAINS),
	tundra(Biomes.ICE_PLAINS),
	ocean(Biomes.OCEAN),
	deepOcean(Biomes.DEEP_OCEAN),
	beach(Biomes.BEACH);

	//Overrides
	@Override
	public Biome getHandle()
	{
		return handle;
	}

	@Override
	public void setHandle(Biome handle)
	{
		handle = handle;
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
				biomeLookupTable[i + j * 64] = getBiome((float) i / 63.0f, (float) j / 63.0f);
			}
		}
	}

	public static BiomeGenBeta getBiomeFromLookup(double var0, double var2)
	{
		int var4 = (int) (var0 * 63.0);
		int var5 = (int) (var2 * 63.0);
		return biomeLookupTable[var4 + var5 * 64];
	}

	public static BiomeGenBeta getBiome(float var0, float var1)
	{
		return var0 < 0.1f ? tundra : (var1 < 0.2f ? (var0 < 0.5f ? tundra : (var0 < 0.95f ? savanna : desert)) : (var1 > 0.5f && var0 < 0.7f ? swampland : (var0 < 0.5f ? taiga : (var0 < 0.97f ? (var1 < 0.35f ? shrubland : forest) : (var1 < 0.45f ? plains : ((var1 *= var0) < 0.9f ? seasonalForest : rainforest))))));
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
