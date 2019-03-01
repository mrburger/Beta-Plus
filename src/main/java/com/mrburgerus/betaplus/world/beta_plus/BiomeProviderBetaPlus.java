package com.mrburgerus.betaplus.world.beta_plus;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.world.beta_plus.sim.BetaPlusSimulator;
import com.mrburgerus.betaplus.world.biome.BetaPlusSelectBiome;
import com.mrburgerus.betaplus.world.biome.BiomeGenBetaPlus;
import com.mrburgerus.betaplus.world.noise.NoiseGeneratorOctavesBeta;
import com.mrburgerus.betaplus.world.noise.NoiseGeneratorOctavesBiome;
import com.sun.org.apache.xpath.internal.operations.Bool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GrassColors;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.feature.structure.Structure;

import javax.annotation.Nonnull;
import java.util.*;

/* Creates Biome Values */
/* Oceans are not a part of Beta proper, so I'm injecting them */
public class BiomeProviderBetaPlus extends BiomeProvider
{
	// Fields
	private NoiseGeneratorOctavesBiome temperatureOctave;
	private NoiseGeneratorOctavesBiome humidityOctave;
	private NoiseGeneratorOctavesBiome noiseOctave;
	public double[] temperatures;
	public double[] humidities;
	public double[] noise;
	private static final Biome[] BIOMES_LIST = buildBiomesList();
	private double[] temps2;
	private double[] humid2;
	private double[] noise2;
	// New Fields
	private BetaPlusGenSettings settings;
	private double scaleVal;
	private double mult;
	private static int chunkSize = 16;

	NoiseGeneratorOctavesBeta octaves12;
	NoiseGeneratorOctavesBeta octaves22;
	NoiseGeneratorOctavesBeta octaves32;
	NoiseGeneratorOctavesBeta octaves62;
	NoiseGeneratorOctavesBeta octaves72;
	private static double[] hNoiseSim;
	private static double octaveArr42[];
	private static double octaveArr52[];
	private static double octaveArr12[];
	private static double octaveArr22[];
	private static double octaveArr32[];
	private long seedLong;

	// The simulator for Y-heights.
	private BetaPlusSimulator simulator;

	public BiomeProviderBetaPlus(World world, BetaPlusGenSettings settingsIn)
	{
		temperatureOctave = new NoiseGeneratorOctavesBiome(new Random(world.getSeed() * 9871), 4);
		humidityOctave = new NoiseGeneratorOctavesBiome(new Random(world.getSeed() * 39811), 4);
		noiseOctave = new NoiseGeneratorOctavesBiome(new Random(world.getSeed() * 543321), 2);
		settings = settingsIn;
		scaleVal = settings.getScale();
		mult = settings.getMultiplierBiome();

		octaves12 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 16);
		octaves22 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 16);
		octaves32 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 8);
		octaves62 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 10);
		octaves72 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 16);

		seedLong = world.getSeed();

		simulator = new BetaPlusSimulator(world);
	}

	/* Builds Possible Biome List */
	private static Biome[] buildBiomesList()
	{
		BiomeGenBetaPlus[] betaPlusBiomes = BiomeGenBetaPlus.defaultB.getDeclaringClass().getEnumConstants();
		Set<Biome> biomeSet = Sets.newHashSet();
		for (int i = 0; i < betaPlusBiomes.length; i++)
		{
			biomeSet.add(betaPlusBiomes[i].handle);
		}
		return biomeSet.toArray(new Biome[biomeSet.size()]);
	}

	public Biome[] generateBiomes(int startX, int startZ, int xSize, int zSize)
	{
		Biome[] biomeArr = new Biome[xSize * zSize];
		temperatures = temperatureOctave.generateOctaves(temperatures, (double) startX, (double) startZ, xSize, xSize, scaleVal, scaleVal, 0.25);
		humidities = humidityOctave.generateOctaves(humidities, (double) startX, (double) startZ, xSize, xSize, scaleVal * mult, scaleVal * mult, 0.3333333333333333);
		noise = noiseOctave.generateOctaves(noise, (double) startX, (double) startZ, xSize, xSize, 0.25, 0.25, 0.5882352941176471);
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
				biomeArr[counter] = BiomeGenBetaPlus.getBiomeFromLookup(temperatureVal, humidityVal);
				counter++;
			}
		}
		return biomeArr;
	}

	/* Adds OCEANS to the mix to the Biome Provider. */
	/* ONLY CALL WHEN NECESSARY, has to simulate the Y-heights of the world */
	/* STILL TESTING, USE WITH CAUTION */
	/* ERROR: DO NOT CALL THE BLOCKPOS SIMULATOR A BUNCH */
	private Biome[] generateBiomesWithOceans(int startX, int startZ, int xSize, int zSize, boolean useAverage, Object b)
	{
		int xP = startX;
		int zP = startZ;
		Biome[] biomeArr = new Biome[xSize * zSize];
		temperatures = temperatureOctave.generateOctaves(temperatures, (double) startX, (double) startZ, xSize, xSize, scaleVal, scaleVal, 0.25);
		humidities = humidityOctave.generateOctaves(humidities, (double) startX, (double) startZ, xSize, xSize, scaleVal * mult, scaleVal * mult, 0.3333333333333333);
		noise = noiseOctave.generateOctaves(noise, (double) startX, (double) startZ, xSize, xSize, 0.25, 0.25, 0.5882352941176471);
		for (int counter = 0; counter < biomeArr.length; counter++)
		{
			xP = startX + counter % xSize;
			zP = startZ + Math.floorDiv(counter, zSize);
			int pos = (xP - startX) + ((zP - startZ) * zSize);
			if (pos != counter)
			{
				BetaPlus.LOGGER.warn("Not equal: " + pos + " : " + counter);
			}
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
			biomeArr[counter] = BiomeGenBetaPlus.getBiomeFromLookup(temperatureVal, humidityVal);
			// Previous: 55, 58
			if (useAverage)
			{
				Pair<Integer, Boolean> avg = simulator.simulateYAvg(new BlockPos(xP, 0, zP));
				if (avg.getFirst() < 56 && !avg.getSecond())
				{
					BetaPlus.LOGGER.debug("Found Deep Ocean" + new BlockPos(xP, 0, zP));
					// useAverage is only set to true if searching for a deep ocean, so we can use it.
					biomeArr[counter] = Biomes.DEEP_OCEAN; //this.getOceanBiome(new BlockPos(xP, 0, zP), true);
				}
			}
			else
			{
				Pair<Integer, Boolean> avg = simulator.simulateYChunk(new BlockPos(xP, 0, zP));
				if (avg.getFirst() < 57 && !avg.getSecond())
				{
					biomeArr[counter] = Biomes.OCEAN; //this.getOceanBiome(new BlockPos(xP, 0, zP), false);
				}
			}
			counter++;
		}


		return biomeArr;
	}

	private Biome[] generateBiomesWithOceans(int startX, int startZ, int xSize, int zSize, final boolean useAverage)
	{
		Biome[] biomeArr = new Biome[xSize * zSize];
		temperatures = temperatureOctave.generateOctaves(temperatures, (double) startX, (double) startZ, xSize, xSize, scaleVal, scaleVal, 0.25);
		humidities = humidityOctave.generateOctaves(humidities, (double) startX, (double) startZ, xSize, xSize, scaleVal * mult, scaleVal * mult, 0.3333333333333333);
		noise = noiseOctave.generateOctaves(noise, (double) startX, (double) startZ, xSize, xSize, 0.25, 0.25, 0.5882352941176471);
		int counter = 0;
		// Aren't these values WRONG? Like Beta Generates Z, X
		// ANSWER: NO! The implementation below is correct
		for (int x = 0; x < xSize; ++x)
		{

			for (int z = 0; z < zSize; ++z)
			{
				// No, x + startX, z + startZ MUST BE USED, by the looks of it
				// Previously, I made an oops.
				BlockPos pos = new BlockPos(x + startX, 0 ,z + startZ);
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
				biomeArr[counter] = BiomeGenBetaPlus.getBiomeFromLookup(temperatureVal, humidityVal);
				if (useAverage)
				{
					Pair<Integer, Boolean> avg = simulator.simulateYAvg(pos);
					if (avg.getFirst() < 56 && !avg.getSecond())
					{
						BetaPlus.LOGGER.debug("Found Deep Ocean" + pos);
						// useAverage is only set to true if searching for a deep ocean, so we can use it.
						biomeArr[counter] = Biomes.DEEP_OCEAN; //this.getOceanBiome(new BlockPos(xP, 0, zP), true);
					}
				}
				else
				{
					Pair<Integer, Boolean> avg = simulator.simulateYChunk(pos);
					if (avg.getFirst() < 57 && !avg.getSecond())
					{
						biomeArr[counter] = Biomes.OCEAN; //this.getOceanBiome(new BlockPos(xP, 0, zP), false);
					}
				}
				counter++;
			}
		}
		return biomeArr;
	}


	//BEGIN OVERRIDES
	/* This HAS to be populated to avoid issues */
	@Override
	@Nonnull
	public List<Biome> getBiomesToSpawnIn()
	{
		return Lists.newArrayList(BiomeGenBetaPlus.beach.handle, BiomeGenBetaPlus.desert.handle);
	}

	@Override
	public Biome getBiome(BlockPos pos, Biome defaultBiome)
	{
		return this.generateBiomesWithOceans(pos.getX(), pos.getZ(), 1, 1, false)[0];
	}

	@Override
	public Biome[] getBiomes(int x, int z, int width, int depth)
	{
		return getBiomes(x, z, width, depth, true);
	}

	@Override
	public Biome[] getBiomes(int x, int z, int width, int length, boolean cacheFlag)
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
		Collections.addAll(set, this.generateBiomesWithOceans(i, j, i1, j1, true));
		return set;
	}

	/* Copied From OverworldBiomeProvider & Modified. */
	@Override
	public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomeList, Random random)
	{
		int i = x - range >> 2;
		int j = z - range >> 2;
		int k = x + range >> 2;
		int l = z + range >> 2;
		int xSize = k - i + 1;
		int zSize = l - j + 1;
		Biome[] biomeArr = this.generateBiomes(i, j, xSize, zSize);

		BlockPos blockpos = null;
		int k1 = 0;

		for(int counter = 0; counter < xSize * zSize; ++counter) {
			int i2 = i + counter % xSize << 2;
			int j2 = j + counter / xSize << 2;
			if (biomeList.contains(biomeArr[counter]))
			{
				if (blockpos == null || random.nextInt(k1 + 1) == 0) {
					blockpos = new BlockPos(i2, 0, j2);
				}

				++k1;
			}
		}
		return blockpos;
	}

	/* Copied from OverworldBiomeProvider.class */
	@Override
	public boolean hasStructure(Structure<?> structure)
	{
		return this.hasStructureCache.computeIfAbsent(structure, (param1) -> {
			for(Biome biome : BIOMES_LIST) // Go through list of declared Biomes
			{
				if (biome.hasStructure(param1))
				{
					return true;
				}
			}

			return false;
		});
	}

	@Override
	public Set<IBlockState> getSurfaceBlocks()
	{
		if (this.topBlocksCache.isEmpty()) {
			Biome[] var1 = BIOMES_LIST;
			int var2 = var1.length;

			for(int var3 = 0; var3 < var2; ++var3) {
				Biome biome = var1[var3];
				this.topBlocksCache.add(biome.getSurfaceBuilderConfig().getTop());
			}
		}

		return this.topBlocksCache;
	}


	// Working Feb 21, 2019
	/* Gets Climate Values */
	public double[] getClimateValuesatPos(BlockPos pos)
	{
		//Copied Over
		int startX = pos.getX();
		int startZ = pos.getZ();
		int xSize = 1;

		temps2 = temperatureOctave.generateOctaves(temps2, startX, startZ, xSize, xSize, scaleVal, scaleVal, 0.25);
		humid2 = humidityOctave.generateOctaves(humid2, startX, startZ, xSize, xSize, scaleVal * mult, scaleVal * mult, 0.3333333333333333);
		noise2 = noiseOctave.generateOctaves(noise2, startX, startZ, xSize, xSize, 0.25, 0.25, 0.5882352941176471);

		double var9 = noise2[0] * 1.1 + 0.5;
		double oneHundredth = 0.01;
		double point99 = 1.0 - oneHundredth;
		double temperatureVal = (temps2[0] * 0.15 + 0.7) * point99 + var9 * oneHundredth;
		oneHundredth = 0.002;
		point99 = 1.0 - oneHundredth;
		double humidityVal = (humid2[0] * 0.15 + 0.5) * point99 + var9 * oneHundredth;
		temperatureVal = 1.0 - (1.0 - temperatureVal) * (1.0 - temperatureVal);
		temperatureVal = MathHelper.clamp(temperatureVal, 0.0, 1.0);
		humidityVal = MathHelper.clamp(humidityVal, 0.0, 1.0);

		//BetaPlus.LOGGER.info("T: " + temperatureVal + " H: " + humidityVal);

		double[] returnVal = {MathHelper.clamp(temperatureVal, 0.0, 1.0), MathHelper.clamp(humidityVal, 0.0, 1.0)};
		return returnVal;
	}

	/* Provides Ocean Biomes appropriate to temperature */
	public Biome getOceanBiome(BlockPos pos, boolean isDeep)
	{
		double[] climate = this.getClimateValuesatPos(pos);
		double temperature = climate[0];
		//return BiomeGenBetaPlus.getBiomeFromLookup(temperature, climate[1]);
		if (temperature < BetaPlusSelectBiome.FROZEN_VALUE)
		{
			if(isDeep)
			{
				return Biomes.DEEP_FROZEN_OCEAN;
			}
			return Biomes.FROZEN_OCEAN;
		}
		else if (temperature > BetaPlusSelectBiome.VERY_HOT_VAL && climate[1] >= 0.725)
		{
			return Biomes.WARM_OCEAN;
		}
		else if (temperature > BetaPlusSelectBiome.WARM_VAL)
		{
			if(isDeep)
			{
				return Biomes.DEEP_LUKEWARM_OCEAN;
			}
			return Biomes.LUKEWARM_OCEAN;

		}
		else
		{
			if(isDeep)
			{
				return Biomes.DEEP_COLD_OCEAN;
			}
			return Biomes.COLD_OCEAN;
		}
	}

	public Biome getBeachBiome(BlockPos pos)
	{
		double[] climate = this.getClimateValuesatPos(pos);
		if (climate[0] < BetaPlusSelectBiome.FROZEN_VALUE)
		{
			return Biomes.SNOWY_BEACH;
		}
		return Biomes.BEACH;
	}

	public int getGrassColorBeta(BlockPos pos)
	{
		double[] climate = getClimateValuesatPos(pos);
		return GrassColors.get(climate[0], climate[1]);
	}
}
