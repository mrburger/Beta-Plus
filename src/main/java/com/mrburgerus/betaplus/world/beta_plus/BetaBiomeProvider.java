package com.mrburgerus.betaplus.world.beta_plus;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mrburgerus.betaplus.world.beta_plus.biome.BetaPlusBiomeSelector;
import com.mrburgerus.betaplus.world.beta_plus.biome.EnumBetaPlusBiome;
import com.mrburgerus.betaplus.world.beta_plus.noise.NoiseGeneratorOctavesBiome;
import com.mrburgerus.betaplus.world.beta_plus.sim.BetaPlusClimate;
import com.mrburgerus.betaplus.world.beta_plus.sim.BetaPlusSimulator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSourceConfig;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

// Biome Provider
public class BetaBiomeProvider extends BiomeProvider
{
	// Fields
	private NoiseGeneratorOctavesBiome temperatureOctave;
	private NoiseGeneratorOctavesBiome humidityOctave;
	private NoiseGeneratorOctavesBiome noiseOctave;
	public double[] temperatures;
	public double[] humidities;
	public double[] noise;
	private static final Biome[] BIOMES_LIST = getBiomesList();
	// New Fields
	private final BetaChunkGeneratorConfig settings;
	private final double scaleVal;
	private final double mult;

	private BetaPlusClimate climateSim;
	public BetaPlusSimulator simulator;



	public BetaBiomeProvider(World world, VanillaLayeredBiomeSourceConfig biomeSrcCfg, BetaChunkGeneratorConfig settingsIn)
	{
		super(biomeSrcCfg);
		temperatureOctave = new NoiseGeneratorOctavesBiome(new Random(world.getSeed() * 9871), 4);
		humidityOctave = new NoiseGeneratorOctavesBiome(new Random(world.getSeed() * 39811), 4);
		noiseOctave = new NoiseGeneratorOctavesBiome(new Random(world.getSeed() * 543321), 2);
		settings = settingsIn;
		scaleVal = settings.getScale();
		mult = settings.getMultiplierBiome();

		/*
		octaves12 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 16);
		octaves22 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 16);
		octaves32 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 8);
		octaves62 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 10);
		octaves72 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 16);
		*/

		simulator = new BetaPlusSimulator(world);
		climateSim = new BetaPlusClimate(world, scaleVal, mult);
	}

	/* Builds Possible Biome List */
	private static Biome[] getBiomesList()
	{
		EnumBetaPlusBiome[] betaPlusBiomes = EnumBetaPlusBiome.defaultB.getDeclaringClass().getEnumConstants();
		Set<Biome> biomeSet = Sets.newHashSet();
		for (int i = 0; i < betaPlusBiomes.length; i++)
		{
			biomeSet.add(betaPlusBiomes[i].handle);
		}
		return biomeSet.toArray(new Biome[biomeSet.size()]);
	}

	@Override
	public BlockPos locateBiome(int x, int z, int range, List<Biome> biomeList, Random random)
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

	@Override
	public Biome getBiome(BlockPos pos)
	{
		return generateBiomesWithOceans(pos.getX(), pos.getZ(), 1,1, false)[0];
	}

	// sampleBiomes is like getBiomes

	@Override
	public Biome[] sampleBiomes(int int_1, int int_2, int int_3, int int_4, boolean boolean_1)
	{
		return this.generateBiomes(int_1, int_2, int_3, int_4);
	}

	@Override
	public Biome[] sampleBiomes(int int_1, int int_2, int int_3, int int_4)
	{
		return this.sampleBiomes(int_1, int_2, int_3, int_4, true);
	}

	@Override
	public List<Biome> getSpawnBiomes()
	{
		return super.getSpawnBiomes();
	}

	@Override
	public Set<Biome> getBiomesInArea(int centerX, int centerZ, int sideLength)
	{
		// Like centerX - 4 if sideLength=16
		// like centerX - 7 if sidelength =29
		// AHH! 7,4 is the number I have seen as an error
		int startX = centerX - sideLength >> 2;
		int startZ = centerZ - sideLength >> 2;
		// probably end-X coordinate
		int endX = centerX + sideLength >> 2;
		int endZ = centerZ + sideLength >> 2;
		int xSize = endX - startX + 1;
		int zSize = endZ - startZ + 1;
		Set<Biome> set = Sets.newHashSet();
		Collections.addAll(set, this.generateBiomesWithOceans(centerX, centerZ, sideLength, sideLength, true));
		return set;
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
				biomeArr[counter] = EnumBetaPlusBiome.getBiomeFromLookup(temperatureVal, humidityVal);
				counter++;
			}
		}
		return biomeArr;
	}

	/* Could be possible that a conversion Z,X to X,Z needed */
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
				biomeArr[counter] = EnumBetaPlusBiome.getBiomeFromLookup(temperatureVal, humidityVal);
				/* Add Oceans by Simulation */
				if (useAverage) //useAverage only TRUE if we're searching in a square pattern, such as spawn, Ocean Monuments, or Mansions.
				{
					Pair<Integer, Boolean> avg = simulator.simulateYAvg(pos);
					// Tried 56, 58, 57
					if (avg.getFirst() < settings.getSeaLevel() - 5) // Usually 58
					{
						// Inversion was the intent, so false is supposed to be "all values below sea level"
						if (!avg.getSecond())
						{
							biomeArr[counter] = this.getOceanBiome(pos, true);
						}
						else
						{
							biomeArr[counter] = this.getOceanBiome(pos, false);
						}
					}
					// Commented out due to overhead. this is only used for spawn, and it isn't worth it.
					/*
					else if (avg.getFirst() >= settings.getSeaLevel() - 2 && avg.getFirst() <= settings.getSeaLevel())
					{
						// Now, add Beaches
						if (simulator.isBlockSandSim(pos))
						{
							biomeArr[counter] = this.getBeachBiome(pos);
						}
					}
					*/

				}
				else // Called when searching for "small" structures
				{
					Pair<Integer, Boolean> avg = simulator.simulateYChunk(pos);
					if (avg.getFirst() < settings.getSeaLevel() - 1) // 62 usually
					{
						biomeArr[counter] = this.getOceanBiome(pos, false);
					}
					else if (avg.getFirst() >= settings.getSeaLevel() - 2 && avg.getFirst() <= settings.getSeaLevel() + 1)
					{
						/* Now, add Beaches */
						if (simulator.isBlockSandSim(pos))
						{
							biomeArr[counter] = this.getBeachBiome(pos);
						}
					}
				}

				/* Finally, increment counter! */
				counter++;
			}
		}
		return biomeArr;
	}

	/* Provides Ocean Biomes appropriate to temperature */
	public Biome getOceanBiome(BlockPos pos, boolean isDeep)
	{
		double[] climate = climateSim.getClimateValuesatPos(pos);
		double temperature = climate[0];
		//return EnumBetaPlusBiome.getBiomeFromLookup(temperature, climate[1]);
		if (temperature < BetaPlusBiomeSelector.FROZEN_VALUE)
		{
			if(isDeep)
			{
				return Biomes.DEEP_FROZEN_OCEAN;
			}
			return Biomes.FROZEN_OCEAN;
		}
		else if (temperature > BetaPlusBiomeSelector.VERY_HOT_VAL && climate[1] >= 0.735) //Was 0.725
		{
			return Biomes.WARM_OCEAN;
		}
		else if (temperature > BetaPlusBiomeSelector.WARM_VAL)
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
		double[] climate = climateSim.getClimateValuesatPos(pos);
		if (climate[0] < BetaPlusBiomeSelector.FROZEN_VALUE)
		{
			return Biomes.SNOWY_BEACH;
		}
		return Biomes.BEACH;
	}

}
