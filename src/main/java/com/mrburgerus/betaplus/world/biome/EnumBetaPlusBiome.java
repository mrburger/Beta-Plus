package com.mrburgerus.betaplus.world.biome;

import biomesoplenty.api.biome.BOPBiomes;
import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.util.ConfigBetaPlus;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.fml.ModList;


//TODO: REMOVE IN FAVOR OF NEW IMPLEMENTATION
public enum EnumBetaPlusBiome implements IBetaPlusBiome
{
	//Enums
	rainforest(ConfigBetaPlus.rainforest),
	swampland(ConfigBetaPlus.swampland),
	seasonalForest(ConfigBetaPlus.seasonalForest),
	forest(ConfigBetaPlus.forest),
	savanna(ConfigBetaPlus.savanna),
	shrubland(ConfigBetaPlus.shrubland),
	taiga(ConfigBetaPlus.taiga),
	desert(ConfigBetaPlus.desert, Blocks.SAND, Blocks.SAND),
	plains(ConfigBetaPlus.plains),
	tundra(ConfigBetaPlus.tundra),
	//New Enums
	warmOcean(Biomes.WARM_OCEAN),
	lukewarmOcean(Biomes.LUKEWARM_OCEAN),
	deepLukewarmOcean(Biomes.DEEP_LUKEWARM_OCEAN),
	coldOcean(Biomes.COLD_OCEAN),
	deepColdOcean(Biomes.DEEP_COLD_OCEAN),
	frozenOcean(ConfigBetaPlus.frozenOcean),
	beach(Biomes.BEACH),
	//roofForest(Biomes.DARK_FOREST),
	mountain(Biomes.WOODED_MOUNTAINS),
	iceSpikes(Biomes.ICE_SPIKES),
	megaTaiga(Biomes.GIANT_SPRUCE_TAIGA),
	birchForest(Biomes.BIRCH_FOREST),
	flowerPlains(Biomes.SUNFLOWER_PLAINS),
	newForest(Biomes.FLOWER_FOREST),
	defaultB(Biomes.PLAINS),

	// MESA UNUSED
	mesa(Biomes.BADLANDS, Blocks.TERRACOTTA, Blocks.TERRACOTTA);


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
	private static final Biome[] BIOME_LOOKUP_TABLE;

	//Constructors
	EnumBetaPlusBiome(Biome handle)
	{
		this(handle, Blocks.GRASS_BLOCK, Blocks.DIRT);
	}

	EnumBetaPlusBiome(Biome biomeHandle, Block top, Block filler)
	{
		handle = biomeHandle;
		topBlock = top;
		fillerBlock = filler;
	}

	//Initialize
	static
	{
		BIOME_LOOKUP_TABLE = new Biome[4096];
		EnumBetaPlusBiome.generateBiomeLookup();
	}

	//Methods
	public static void generateBiomeLookup()
	{
		for (int i = 0; i < 64; ++i)
		{
			for (int j = 0; j < 64; ++j)
			{
				//EDITED
				BIOME_LOOKUP_TABLE[i + j * 64] = BetaPlusBiomeSelector.getBiome((float) i / 63.0f, (float) j / 63.0f);
			}
		}
	}

	//Gets Value
	public static Biome getBiomeFromLookup(double temperature, double humidity)
	{
		int i = (int) (temperature * 63.0);
		int j = (int) (humidity * 63.0);

		return BIOME_LOOKUP_TABLE[i + j * 64];
	}

	// Convert Biome map to Enum Map, Respective.
	public static EnumBetaPlusBiome[] convertBiomeTable(Biome[] biomeLookupTable)
	{
		// Create Equal Length Array
		EnumBetaPlusBiome[] biomePlus = new EnumBetaPlusBiome[biomeLookupTable.length];

		for (int i = 0; i < biomeLookupTable.length; i++)
		{
			for (EnumBetaPlusBiome biomeGenBetaPlus : EnumBetaPlusBiome.values())
			{
				if (biomeGenBetaPlus.handle == biomeLookupTable[i])
				{
					//System.out.println("FOUND: " + biomeGenBetaPlus.handle.toString());
					biomePlus[i] = biomeGenBetaPlus;
				}
			}
			if (biomePlus[i] == null)
			{
				biomePlus[i] = EnumBetaPlusBiome.defaultB;
			}
		}
		return biomePlus;
	}

}
