package com.mrburgerUS.betaplus.beta_plus.biome.support;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.Loader;

import java.util.ArrayList;

public class SupportBiome
{
	public static ArrayList<Biome> snowBiomes;
	public static ArrayList<Biome> coldBiomes;
	public static ArrayList<Biome> hotBiomes;
	public static ArrayList<Biome> wetBiomes;
	public static ArrayList<Biome> smallBiomes;

	public static void init()
	{
		snowBiomes = new ArrayList<>();
		coldBiomes = new ArrayList<>();
		hotBiomes = new ArrayList<>();
		wetBiomes = new ArrayList<>();
		smallBiomes = new ArrayList<>();

		//Add Default Biomes
		addDefaults();

		//Add BOP, if it is loaded
		if (Loader.isModLoaded("biomesoplenty"))
		{
			BOPSupport.init();
		}

		//Add AbyssalCraft
		if (Loader.isModLoaded("abyssalcraft"))
		{
			ACSupport.init();
		}
	}

	private static void addDefaults()
	{
		snowBiomes.add(Biomes.COLD_TAIGA);
		snowBiomes.add(Biomes.MUTATED_ICE_FLATS);
		snowBiomes.add(Biomes.ICE_PLAINS);
		coldBiomes.add(Biomes.PLAINS);
		coldBiomes.add(Biomes.REDWOOD_TAIGA);
		coldBiomes.add(Biomes.BIRCH_FOREST);
		coldBiomes.add(Biomes.MUTATED_FOREST);
		coldBiomes.add(Biomes.FOREST);
		coldBiomes.add(Biomes.MUTATED_PLAINS);
		hotBiomes.add(Biomes.SAVANNA);
		hotBiomes.add(Biomes.MESA);
		hotBiomes.add(Biomes.DESERT);
		wetBiomes.add(Biomes.SWAMPLAND);
		wetBiomes.add(Biomes.ROOFED_FOREST);
		wetBiomes.add(Biomes.JUNGLE);
	}
}