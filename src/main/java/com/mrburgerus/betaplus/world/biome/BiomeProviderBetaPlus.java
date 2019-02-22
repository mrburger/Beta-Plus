package com.mrburgerus.betaplus.world.biome;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.world.noise.NoiseGeneratorOctavesOld;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GrassColors;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.feature.structure.Structure;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BiomeProviderBetaPlus extends BiomeProvider
{
	// Fields
	private NoiseGeneratorOctavesOld temperatureOctave;
	private NoiseGeneratorOctavesOld humidityOctave;
	private NoiseGeneratorOctavesOld noiseOctave;
	public double[] temperatures;
	public double[] humidities;
	public double[] noise;
	public Biome[] genBiomes; // Formerly biomeBaseArray, not fully a Gen Layer?
	private static final Biome[] biomes = buildBiomesList();
	// New Fields

	public BiomeProviderBetaPlus(World world)
	{
		temperatureOctave = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 9871), 4);
		humidityOctave = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 39811), 4);
		noiseOctave = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 543321), 2);
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
		// Decreasing someVal increases biome size.
		double someVal = 0.01;
		//double someVal = 0.02500000037252903;
		double mult = 1.5; // 2 Originally
		temperatures = temperatureOctave.generateOctaves(temperatures, (double) startX, (double) startZ, xSize, xSize, someVal, someVal, 0.25);
		humidities = humidityOctave.generateOctaves(humidities, (double) startX, (double) startZ, xSize, xSize, someVal * mult, someVal * mult, 0.3333333333333333);
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
		//BetaPlus.LOGGER.info("Biome Finished Generating: " + startX + ", " + startZ);
		return biomeArr;
	}

	//BEGIN OVERRIDES
	/* This HAS to be populated to avoid issues */
	@Override
	public List<Biome> getBiomesToSpawnIn()
	{
		return Lists.newArrayList(BiomeGenBetaPlus.beach.handle, BiomeGenBetaPlus.desert.handle);
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
		Collections.addAll(set, this.generateBiomes(i, j, i1, j1));
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
			// If the input list of biomes has
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

	// DOES NOT WORK!!!
	@Override
	public boolean hasStructure(Structure<?> structure)
	{
		return this.hasStructureCache.computeIfAbsent(structure, (param1) -> {
			for(Biome biome : this.biomes) // Go through list of declared Biomes
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
			Biome[] var1 = biomes;
			int var2 = var1.length;

			for(int var3 = 0; var3 < var2; ++var3) {
				Biome biome = var1[var3];
				this.topBlocksCache.add(biome.getSurfaceBuilderConfig().getTop());
			}
		}

		return this.topBlocksCache;
	}


	// Working Feb 21, 2019
	public double[] getClimateValuesatPos(BlockPos pos)
	{
		//Copied Over
		int startX = pos.getX();
		int startZ = pos.getZ();
		int xSize = 1;

		temperatures = temperatureOctave.generateOctaves(temperatures, startX, startZ, xSize, xSize, 0.02500000037252903, 0.02500000037252903, 0.25);
		humidities = humidityOctave.generateOctaves(humidities, startX, startZ, xSize, xSize, 0.05000000074505806, 0.05000000074505806, 0.3333333333333333);
		BetaPlus.LOGGER.info("T: " + temperatures[0] + " H: " + humidities[0]);

		double[] returnVal = {MathHelper.clamp(temperatures[0], 0.0, 1.0), MathHelper.clamp(humidities[0], 0.0, 1.0)};
		return returnVal;
	}

	/* Provides Ocean Biomes appropriate to temperature */
	public Biome getOceanBiome(BlockPos pos, boolean isDeep)
	{
		double[] climate = this.getClimateValuesatPos(pos);
		double temperature = climate[0];
		//return BiomeGenBetaPlus.getBiomeFromLookup(temperature, climate[1]);
		if (temperature < BetaPlusSelectBiome.frozenValue)
		{
			if(isDeep)
			{
				return Biomes.DEEP_FROZEN_OCEAN;
			}
			return Biomes.FROZEN_OCEAN;
		}
		else if (temperature > BetaPlusSelectBiome.veryHotVal && climate[1] >= 0.725)
		{
			return Biomes.WARM_OCEAN;
		}
		else if (temperature > BetaPlusSelectBiome.warmVal)
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
		if (climate[0] < BetaPlusSelectBiome.frozenValue)
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
