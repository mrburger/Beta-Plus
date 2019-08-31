package com.mrburgerus.betaplus.world.biome;


import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mrburgerus.betaplus.world.biome.support.Support;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

import java.util.Optional;

// Yet another Biome Selector, does some cool injection of modern biomes.

// BiomeDictionary.class?


// NOTES
// Temperature is from -0.5 (SNOWY TAIGA) to 2.0 (DESERT / MESA) in vanilla
// Range 2.5
// [0, 1) to [-0.5, 2.0]



public class BiomeSelectorBetaPlus extends AbstractBiomeSelector
{
	// Fields
	private static Biome defaultBiome = Biomes.PLAINS;
	private static final double TEMP_RANGE = 2.5; // Temperature range of all Biomes

	public BiomeSelectorBetaPlus()
	{
		// Spawn on sand, or coastal Biomes. HOPEFULLY IT WORKS
		// Causes Chunk issues....
		//super(Lists.asList(Biomes.DESERT, Support.coastBiomes.toArray(new Biome[0])));
		super(Support.coastBiomes);
	}

	@Override
	public Biome getBiome(double temperature, double humidity, double noiseSelect, TerrainType terrainType)
	{
		double ran;
		// NEW, confusing, but it works kinda.
		//ran = temperature * 0.9999999999;
		//temperature = (noiseSelect * 2.5) - 0.5; // TESTING swap
		// ORIGINAL, stopped use because of biome "striping", could be another issue.
		//ran = noiseSelect;
		//temperature = (temperature * 2.5) - 0.5;
		// Newest, temperature is a composite of the noise select and temperature input
		// [0, 1] + [0, 1) = [0, 2)
		// Scale factor of 1.25 so the max is 2.5
		// Subtract 0.5 to get [-0.5, 2.0]
		//temperature = ((temperature + noiseSelect) * 1.25) - 0.5; // Creates too many mini-biomes

		// NEW IMPLEMENTATIONS (WITH NOTES!) //

		// Implementation 0 //
		// NOTES: STILL HAS JAGGED SECTIONS. POSSIBLY MOVE THE noiseVoronoi to a different value.
		//ran = noiseSelect;
		//double tempScale = 0.5; // Tempscale + NoiseScale = TEMP_RANGE
		//temperature = ((temperature * tempScale) + (noiseSelect * (TEMP_RANGE - tempScale))) - 0.5;

		// Implementation 1 //
		// NOTES: Much Better, jagged edges still exist.
		//ran = (temperature + humidity) * (0.5 - Float.MIN_VALUE);
		//temperature = (noiseSelect * TEMP_RANGE) - 0.5;
		//humidity = (noiseSelect + humidity) * (0.5 - Float.MIN_VALUE);

		// Implementation 2 //
		// NOTES: Striping has returned! ARGH
		//ran = (temperature + humidity + noiseSelect) * 0.33333333333333;
		//temperature = ((temperature + humidity) * (TEMP_RANGE / 2.0)) - 0.5;
		//humidity = (noiseSelect + humidity) * (0.5 - Float.MIN_VALUE);

		// Implementation 3 //
		// NOTES: Too much noiseselect weight
		//ran = (temperature * humidity + noiseSelect) * 0.5;
		//temperature = ((temperature + humidity) * (TEMP_RANGE / 2.0)) - 0.5;
		//humidity = (noiseSelect + humidity) * (0.5 - Float.MIN_VALUE);

		// Implementation 4 //
		// NOTES: Mini-biomes are the normal, too small.
		ran = (temperature + humidity * noiseSelect) * 0.5;
		temperature = ((temperature + humidity) * (TEMP_RANGE / 2.0)) - 0.5;
		humidity = (noiseSelect + humidity) * (0.5 - Float.MIN_VALUE);

		// Implementation 5 //
		// NOTES: STRIPES OF BIOMES
		//ran = (temperature * noiseSelect + humidity * noiseSelect) * 0.5;
		//temperature = ((temperature + noiseSelect) * (TEMP_RANGE / 2.0)) - 0.5;
		//humidity = humidity * noiseSelect;

		// Implementation 6 //
		// NOTES: Pretty darn good, actually. BUT, Temperature still creates stripes.
		//ran = (temperature * noiseSelect + humidity * noiseSelect) * 0.5;
		//temperature = (temperature * TEMP_RANGE) - 0.5;
		// Assign humidity to itself

		// Implementation 7 //
		// NOTES: WEIRD Temperature Artifacts
		//ran = (temperature * noiseSelect + humidity * noiseSelect) * 0.5;
		//temperature = (temperature + noiseSelect + humidity) / (TEMP_RANGE / 3.0) - 0.5;
		// Assign humidity to itself

		// Implementation 8 //
		// NOTES:
		//ran = (temperature * noiseSelect + humidity * noiseSelect) * 0.5;
		//temperature = (temperature * TEMP_RANGE) - 0.5;
		// Assign humidity to itself

		// Implementation X //
		// NOTES:


		Biome select;
		switch (terrainType)
		{
			case land:
				select = getLandBiome(temperature, humidity, ran).getFirst();
				break;
			case hillyLand:
				select = getHillyBiome(temperature, humidity, ran, getLandBiome(temperature, humidity, ran).getSecond());
				break;
			case mountains:
				select = Support.getBiomeFromParams(temperature, humidity, ran, Support.mountainBiomes);
				break;
			case sea:
				select = getOceanBiome(temperature, humidity, ran, false);
				break;
			case deepSea:
				select = getOceanBiome(temperature, humidity, ran, true);
				break;
			case coastal:
				select = Support.getBiomeFromParams(temperature, humidity, ran, Support.coastBiomes);
				break;
			case island:
				select = Support.getBiomeFromParams(temperature, humidity, ran, Support.islandBiomes);
				break;
			case generic:
				select = defaultBiome;
				break;
			default:
				select = defaultBiome;
		}
		return select;
	}

	// HELPERS

	private static Pair<Biome, Optional<Biome>> getLandBiome(double temperature, double humidity, double selectNoise)
	{
		return Support.getLandBiomeFromParams(temperature, humidity, selectNoise, Support.landBiomes);
	}

	private static Biome getHillyBiome(double temperature, double humidity, double selectNoise, Optional<Biome> hillBiome)
	{
		Biome select;
		select = hillBiome.orElseGet(() -> Support.getBiomeFromParams(temperature, humidity, selectNoise, Support.mountainBiomes));
		return select;
	}

	private static Biome getOceanBiome(double temperature, double humidity, double selectNoise, boolean isDeep)
	{
		if (isDeep)
		{
			return Support.getOceanBiomePair(temperature, humidity, selectNoise, Support.oceanBiomes).getSecond();
		}
		return Support.getOceanBiomePair(temperature, humidity, selectNoise, Support.oceanBiomes).getFirst();
	}

}
