package com.mrburgerUS.betaplus.beta_plus;

import com.google.common.collect.Sets;
import com.mrburgerUS.betaplus.beta_plus.biome.EnumBetaBiome;
import com.mrburgerUS.betaplus.beta_plus.noise.NoiseGeneratorOctavesBiome;
import com.mrburgerUS.betaplus.beta_plus.sim.BetaSimulator;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BiomeProviderBeta extends BiomeProvider
{
	private NoiseGeneratorOctavesBiome octave1;
	private NoiseGeneratorOctavesBiome octave2;
	private NoiseGeneratorOctavesBiome octave3;
	public double[] temperatures;
	public double[] humidities;
	public double[] noise;
	public Biome[] biomeBaseArray;
	private BetaSimulator simulator;

	public BiomeProviderBeta(World world)
	{
		octave1 = new NoiseGeneratorOctavesBiome(new Random(world.getSeed() * 9871), 4);
		octave2 = new NoiseGeneratorOctavesBiome(new Random(world.getSeed() * 39811), 4);
		octave3 = new NoiseGeneratorOctavesBiome(new Random(world.getSeed() * 543321), 2);

		// Simulator Added
		simulator = new BetaSimulator(world);
	}

	public Biome[] findBiomeArray(int xPos, int zPos, int width, int depth)
	{
		biomeBaseArray = getBiomesForGeneration(biomeBaseArray, xPos, zPos, width, depth);
		return biomeBaseArray;
	}

	public Biome[] findBiomeArrayWithOceans(int xPos, int zPos, int width, int depth, boolean useAverage)
	{
		biomeBaseArray = getBiomesWithOceans(biomeBaseArray, xPos, zPos, width, depth, useAverage);
		return biomeBaseArray;
	}

	//BEGIN OVERRIDES
	@Override
	public List<Biome> getBiomesToSpawnIn()
	{
		return null;
	}

	@Override
	public Biome getBiome(BlockPos pos)
	{
		return getBiome(pos, (Biome) null);
	}

	@Override
	public Biome getBiome(BlockPos pos, Biome defaultBiome)
	{
		return findBiomeArray(pos.getX(), pos.getZ(), 1, 1)[0];
	}

	@Override
	public Biome[] getBiomesForGeneration(Biome[] biomeBases, int xChunk, int zChunk, int width, int height)
	{
		if (biomeBases == null || biomeBases.length < width * height)
		{
			biomeBases = new Biome[width * height];
		}
		temperatures = octave1.generateOctaves(temperatures, xChunk, zChunk, width, width, 0.02500000037252903, 0.02500000037252903, 0.25);
		humidities = octave2.generateOctaves(humidities, xChunk, zChunk, width, width, 0.05000000074505806, 0.05000000074505806, 0.3333333333333333);
		noise = octave3.generateOctaves(noise, xChunk, zChunk, width, width, 0.25, 0.25, 0.5882352941176471);
		int counter = 0;
		for (int x = 0; x < width; ++x)
		{

			for (int z = 0; z < height; ++z)
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
				biomeBases[counter++] = EnumBetaBiome.getBiomeFromLookup(temperatureVal, humidityVal);
			}
		}
		return biomeBases;
	}

	//NEW
	public Biome[] getBiomesWithOceans(Biome[] biomeBases, int xChunk, int zChunk, int width, int height, boolean useAverage)
	{
		if (biomeBases == null || biomeBases.length < width * height)
		{
			biomeBases = new Biome[width * height];
		}
		temperatures = octave1.generateOctaves(temperatures, xChunk, zChunk, width, width, 0.02500000037252903, 0.02500000037252903, 0.25);
		humidities = octave2.generateOctaves(humidities, xChunk, zChunk, width, width, 0.05000000074505806, 0.05000000074505806, 0.3333333333333333);
		noise = octave3.generateOctaves(noise, xChunk, zChunk, width, width, 0.25, 0.25, 0.5882352941176471);
		int counter = 0;
		for (int x = 0; x < width; ++x)
		{

			for (int z = 0; z < height; ++z)
			{
				// Added March 5, 2019
				// Modified "startX" to "xChunk"
				BlockPos pos = new BlockPos(x + xChunk, 0, z + zChunk);

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
				biomeBases[counter] = EnumBetaBiome.getBiomeFromLookup(temperatureVal, humidityVal);

				// Inject Oceans
				if (useAverage)
				{
					Pair<Integer, Boolean> avg = simulator.simulateYAvg(pos);
					// Tried 56, 58, 57
					if (avg.getLeft() < 58) // Usually 58
					{
						// Inversion was the intent, so false is supposed to be "all values below sea level"
						if (!avg.getRight())
						{
							biomeBases[counter] = Biomes.DEEP_OCEAN;
						}
						else
						{
							biomeBases[counter] = Biomes.OCEAN;
						}
					}
				}
				else
				{
					Pair<Integer, Boolean> avg = simulator.simulateYChunk(pos);
					if (avg.getLeft() < 62) // 62 usually
					{
						biomeBases[counter] = Biomes.OCEAN;
					}
				}
				// Increment counter
				counter++;
			}
		}
		return biomeBases;
	}

	@Override
	public Biome[] getBiomes(@Nullable Biome[] oldBiomeList, int x, int z, int width, int depth)
	{
		return getBiomes(oldBiomeList, x, z, width, depth, true);
	}

	@Override
	public Biome[] getBiomes(@Nullable Biome[] listToReuse, int x, int z, int width, int length, boolean cacheFlag)
	{
		//Modified with oceans. to test
		return findBiomeArrayWithOceans(x, z, width, length, false);
	}

	/* Villages, Monuments, Mansions */
	//TODO: MAKE IT WORK
	@Override
	public boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed)
	{
		Set<Biome> set = Sets.newHashSet();
		//TODO: ADD OCEANS
		Collections.addAll(set, this.findBiomeArrayWithOceans(x, z, radius, radius, true));
		for (Biome b : allowed)
		{
			if (set.contains(b))
			{
				return true;
			}
		}
		return false;
	}

	/* Null for now */
	@Override
	public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomeList, Random random)
	{
		return null;
	}

	@Override
	public boolean isFixedBiome()
	{
		return false;
	}
}

