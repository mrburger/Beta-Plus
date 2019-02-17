package com.mrburgerus.betaplus.world.biome;

import com.google.common.collect.Sets;
import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.world.noise.NoiseGeneratorOctavesOld;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.feature.structure.Structure;
import java.util.*;

public class BiomeProviderBetaPlus extends BiomeProvider
{
	// Fields
	private NoiseGeneratorOctavesOld octave1;
	private NoiseGeneratorOctavesOld octave2;
	private NoiseGeneratorOctavesOld octave3;
	public double[] temperatures;
	public double[] humidities;
	public double[] noise;
	public Biome[] biomeBaseArray;
	// New Fields
	private final BiomeCache cache = new BiomeCache(this);


	public BiomeProviderBetaPlus(World world)
	{
		//ADDED BECAUSE TEST (IT WORKS)
		this.biomeBaseArray = new Biome[]{Biomes.OCEAN, Biomes.PLAINS, Biomes.DESERT, Biomes.MOUNTAINS, Biomes.FOREST, Biomes.TAIGA, Biomes.SWAMP, Biomes.RIVER, Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER, Biomes.SNOWY_TUNDRA, Biomes.SNOWY_MOUNTAINS, Biomes.MUSHROOM_FIELDS, Biomes.MUSHROOM_FIELD_SHORE, Biomes.BEACH, Biomes.DESERT_HILLS, Biomes.WOODED_HILLS, Biomes.TAIGA_HILLS, Biomes.MOUNTAIN_EDGE, Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.DEEP_OCEAN, Biomes.STONE_SHORE, Biomes.SNOWY_BEACH, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.DARK_FOREST, Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.WOODED_MOUNTAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.BADLANDS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.BADLANDS_PLATEAU, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_WARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SUNFLOWER_PLAINS, Biomes.DESERT_LAKES, Biomes.GRAVELLY_MOUNTAINS, Biomes.FLOWER_FOREST, Biomes.TAIGA_MOUNTAINS, Biomes.SWAMP_HILLS, Biomes.ICE_SPIKES, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE, Biomes.TALL_BIRCH_FOREST, Biomes.TALL_BIRCH_HILLS, Biomes.DARK_FOREST_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.MODIFIED_BADLANDS_PLATEAU};

		octave1 = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 9871), 4);
		octave2 = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 39811), 4);
		octave3 = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 543321), 2);
	}

	/* Similar to GenLayer.generateBiomes() */
	/* MERGED FOR 1.13 with getBiomesForGeneration */
	public Biome[] generateBiomesTrue(int startX, int startZ, int xSize, int zSize)
	{
		if (biomeBaseArray == null || biomeBaseArray.length < xSize * zSize)
		{
			biomeBaseArray = new Biome[xSize * zSize];
		}
		temperatures = octave1.generateOctaves(temperatures, (double) startX, (double) startZ, xSize, xSize, 0.02500000037252903, 0.02500000037252903, 0.25);
		humidities = octave2.generateOctaves(humidities, (double) startX, (double) startZ, xSize, xSize, 0.05000000074505806, 0.05000000074505806, 0.3333333333333333);
		noise = octave3.generateOctaves(noise, (double) startX, (double) startZ, xSize, xSize, 0.25, 0.25, 0.5882352941176471);
		int counter = 0;
		for (int x = 0; x < xSize; ++x)
		{

			for (int z = 0; z < zSize; ++z)
			{
				double var9 = noise[counter] * 1.1 + 0.5;
				double oneHundredth = 0.01;
				double point99 = 1.0 - oneHundredth;
				double temperatureVal = (temperatures[counter] * 0.15 + 0.7) * point99 + var9 * oneHundredth;
				oneHundredth = 0.002;
				point99 = 1.0 - oneHundredth;
				double humidityVal = (humidities[counter] * 0.15 + 0.5) * point99 + var9 * oneHundredth;
				temperatureVal = 1.0 - (1.0 - temperatureVal) * (1.0 - temperatureVal);
				temperatureVal = MathHelper.clamp(temperatureVal, 0.0, 1.0);
				humidityVal = MathHelper.clamp(humidityVal, 0.0, 1.0);
				temperatures[counter] = temperatureVal;
				humidities[counter] = humidityVal;
				biomeBaseArray[counter++] = BiomeGenBetaPlus.getBiomeFromLookup(temperatureVal, humidityVal);
			}
		}
		return biomeBaseArray;
	}

	/* Similar to GenLayer.generateBiomes() */
	/* MERGED FOR 1.13 with getBiomesForGeneration */
	public Biome[] generateBiomes(int startX, int startZ, int xSize, int zSize)
	{
		if (biomeBaseArray == null || biomeBaseArray.length < xSize * zSize)
		{
			biomeBaseArray = new Biome[xSize * zSize];
		}
		// Decreasing someVal increases biome size.
		double someVal = 0.009; //0.02500000037252903;
		temperatures = octave1.generateOctaves(temperatures, (double) startX, (double) startZ, xSize, xSize, someVal, someVal, 0.25);
		humidities = octave2.generateOctaves(humidities, (double) startX, (double) startZ, xSize, xSize, someVal * 1.5, someVal * 1.5, 0.3333333333333333);
		noise = octave3.generateOctaves(noise, (double) startX, (double) startZ, xSize, xSize, 0.25, 0.25, 0.5882352941176471);
		int counter = 0;
		for (int x = 0; x < xSize; ++x)
		{

			for (int z = 0; z < zSize; ++z)
			{
				double var9 = noise[counter] * 1.1 + 0.5;
				double oneHundredth = 0.01;
				double point99 = 1.0 - oneHundredth;
				double temperatureVal = (temperatures[counter] * 0.15 + 0.7) * point99 + var9 * oneHundredth;
				oneHundredth = 0.002;
				point99 = 1.0 - oneHundredth;
				double humidityVal = (humidities[counter] * 0.15 + 0.5) * point99 + var9 * oneHundredth;
				temperatureVal = 1.0 - (1.0 - temperatureVal) * (1.0 - temperatureVal);
				temperatureVal = MathHelper.clamp(temperatureVal, 0.0, 1.0);
				humidityVal = MathHelper.clamp(humidityVal, 0.0, 1.0);
				temperatures[counter] = temperatureVal;
				humidities[counter] = humidityVal;
				biomeBaseArray[counter++] = BiomeGenBetaPlus.getBiomeFromLookup(temperatureVal, humidityVal);
			}
		}
		return biomeBaseArray;
	}


	//BEGIN OVERRIDES
	@Override
	public List<Biome> getBiomesToSpawnIn()
	{
		return BiomeProvider.BIOMES_TO_SPAWN_IN;
	}

	@Override
	public Biome getBiome(BlockPos pos, Biome defaultBiome)
	{
		return getBiomes(pos.getX(), pos.getZ(), 1, 1, true)[0];
	}

	@Override
	public Biome[] getBiomes(int x, int z, int width, int depth)
	{
		return getBiomes(x, z, width, depth, true);
	}

	@Override
	public Biome[] getBiomes(int x, int z, int width, int length, boolean b)
	{
		return generateBiomes(x, z, width, length);
	}

	@Override
	public Set<Biome> getBiomesInSquare(int centerX, int centerZ, int sideLength)
	{
		int i = centerX - sideLength >> 2;
		int j = centerZ - sideLength >> 2;
		int k = centerX + sideLength >> 2;
		int l = centerZ + sideLength >> 2;
		int i1 = k - i + 1;
		int j1 = l - j + 1;
		Set<Biome> set = Sets.newHashSet();
		Collections.addAll(set, this.generateBiomes(i, j, i1, j1));
		return set;
	}

	@Override
	public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random)
	{
		return null;
	}

	@Override
	public boolean hasStructure(Structure<?> structure)
	{
		return false;
	}

	@Override
	public Set<IBlockState> getSurfaceBlocks() {
		if (this.topBlocksCache.isEmpty()) {
			Biome[] var1 = this.biomeBaseArray;
			int var2 = var1.length;

			for(int var3 = 0; var3 < var2; ++var3) {
				Biome biome = var1[var3];
				this.topBlocksCache.add(biome.getSurfaceBuilderConfig().getTop());
			}
		}

		return this.topBlocksCache;
	}


	// Working Feb 16, 2019
	public double[] getClimateValuesatPos(BlockPos pos)
	{
		//Copied Over
		int startX = pos.getX();
		int startZ = pos.getZ();
		int xSize = 1;

		temperatures = octave1.generateOctaves(temperatures, startX, startZ, xSize, xSize, 0.02500000037252903, 0.02500000037252903, 0.25);
		humidities = octave2.generateOctaves(humidities, startX, startZ, xSize, xSize, 0.05000000074505806, 0.05000000074505806, 0.3333333333333333);
		noise = octave3.generateOctaves(noise, startX, startZ, xSize, xSize, 0.25, 0.25, 0.5882352941176471);

		int counter = 0;
		double var9 = noise[counter] * 1.1 + 0.5;
		double oneHundredth = 0.01;
		double point99 = 1.0 - oneHundredth;
		double temperatureVal = (temperatures[counter] * 0.15 + 0.7) * point99 + var9 * oneHundredth;
		oneHundredth = 0.002;
		point99 = 1.0 - oneHundredth;
		double humidityVal = (humidities[counter] * 0.15 + 0.5) * point99 + var9 * oneHundredth;
		temperatureVal = 1.0 - (1.0 - temperatureVal) * (1.0 - temperatureVal);
		temperatureVal = MathHelper.clamp(temperatureVal, 0.0, 1.0);
		humidityVal = MathHelper.clamp(humidityVal, 0.0, 1.0);
		double[] returnVal = {temperatureVal, humidityVal};
		return returnVal;
	}

	// Written
	public int getGrassColor(BlockPos blockPos)
	{
		generateBiomes(blockPos.getX(), blockPos.getZ(), 1, 1);
		double temperature = this.temperatures[0];
		double humidity = this.humidities[0]; //We'll Remove just a wee bit of Number (for looks)
		//Resets Temp within Bounds
		if (temperature > 1.0)
			temperature = 1.0;
		else if (temperature < 0.0)
			temperature = 0.0;
		// Resets Humidity within bounds
		if (humidity > 1.0)
			humidity = 1.0;
		else if (humidity < 0.0)
		{
			humidity = 0.0;
		}
		return 0;
	}


	/* Provides Ocean Biomes appropriate to temperature */
	public Biome getOceanBiome(BlockPos pos, boolean isDeep)
	{
		double[] climate = this.getClimateValuesatPos(pos);
		double temperature = climate[0];
		//return BiomeGenBetaPlus.getBiomeFromLookup(temperature, climate[1]);
		if (temperature < BetaPlusSelectBiome.coldValue / 2)
		{
			if(isDeep)
			{
				return Biomes.DEEP_FROZEN_OCEAN;
			}
			return Biomes.FROZEN_OCEAN;
		}
		else if (temperature > BetaPlusSelectBiome.veryHotVal && climate[1] >= 0.75)
		{
			return Biomes.WARM_OCEAN;
		}
		else if (temperature < BetaPlusSelectBiome.coldValue)
		{
			if(isDeep)
			{
				return Biomes.DEEP_COLD_OCEAN;
			}
			return Biomes.COLD_OCEAN;
		}
		else
		{
			if(isDeep)
			{
				return Biomes.DEEP_LUKEWARM_OCEAN;
			}
			return Biomes.LUKEWARM_OCEAN;
		}
	}

	public Biome getBeachBiome(BlockPos pos)
	{
		double[] climate = this.getClimateValuesatPos(pos);
		if (climate[0] < BetaPlusSelectBiome.frozenValue)
		{
			return Biomes.SNOWY_BEACH;
		}
		return Biomes.BEACH;
	}
}

