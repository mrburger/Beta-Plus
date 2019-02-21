package com.mrburgerus.betaplus.world.biome;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.world.beta_plus.BetaPlusGenSettings;
import com.mrburgerus.betaplus.world.beta_plus.ChunkGeneratorBetaPlus;
import com.mrburgerus.betaplus.world.noise.NoiseGeneratorOctavesOld;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GrassColors;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.feature.structure.Structure;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BiomeProviderBetaPlus extends BiomeProvider
{
	// Fields
	private NoiseGeneratorOctavesOld octave1;
	private NoiseGeneratorOctavesOld octave2;
	private NoiseGeneratorOctavesOld octave3;
	public double[] temperatures;
	public double[] humidities;
	public double[] noise;
	private Biome[] genBiomes; // Formerly biomeBaseArray, not fully a Gen Layer?
	private final Biome[] biomes;
	// New Fields
	private World worldObj;
	private double[] temps2;
	private double[] humid2;
	private double[] noise1;
	private NoiseGeneratorOctavesOld octave11;
	private NoiseGeneratorOctavesOld octave22;
	private NoiseGeneratorOctavesOld octave33;
	// Decreasing this INCREASES BIOME SIZE
	private final double scaleVal;
	// Multiplier.
	private final double mult;
	// Settings for world
	private final BetaPlusGenSettings settings;
	// Biome Cache
	private final BiomeCache cache = new BiomeCache(this);


	public BiomeProviderBetaPlus(World world, BetaPlusGenSettings settingsIn)
	{
		biomes = buildBiomesList();
		octave1 = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 9871), 4);
		octave2 = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 39811), 4);
		octave3 = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 543321), 2);

		// Initialize new variables, reusing might cause issues.
		octave11 = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 9871), 4);
		octave22 = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 39811), 4);
		octave33 = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 543321), 2);

		this.worldObj = world;
		settings = settingsIn;
		scaleVal = settings.getScale();
		mult = settings.getMultiplierBiome();

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
		return biomeSet.toArray(new Biome[0]);
	}

	/* Similar to GenLayer.generateBiomes() */
	/* MERGED FOR 1.13 with getBiomesForGeneration */
	public Biome[] generateBiomesTrue(int startX, int startZ, int xSize, int zSize)
	{
		if (genBiomes == null || genBiomes.length < xSize * zSize)
		{
			genBiomes = new Biome[xSize * zSize];
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
				genBiomes[counter++] = BiomeGenBetaPlus.getBiomeFromLookup(temperatureVal, humidityVal);
			}
		}
		return genBiomes;
	}

	/* Similar to GenLayer.generateBiomes() */
	/* MERGED FOR 1.13 with getBiomesForGeneration */
	// Not Working Fully
	public Biome[] generateBiomesOld(int startX, int startZ, int xSize, int zSize)
	{
		Biome[] biomeArr = new Biome[xSize * zSize];
		if (genBiomes == null || genBiomes.length < xSize * zSize)
		{
			genBiomes = new Biome[xSize * zSize];
		}
		// Decreasing someVal increases biome size.
		double someVal = 0.01;
		someVal = 0.02500000037252903;
		double mult = 2; // 2 Originally
		temperatures = octave1.generateOctaves(temperatures, (double) startX, (double) startZ, xSize, xSize, someVal, someVal, 0.25);
		humidities = octave2.generateOctaves(humidities, (double) startX, (double) startZ, xSize, xSize, someVal * mult, someVal * mult, 0.3333333333333333);
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
				biomeArr[counter] = BiomeGenBetaPlus.getBiomeFromLookup(temperatureVal, humidityVal);
				counter++;
			}
		}
		return biomeArr;
	}

	public Biome[] generateBiomes(int xStartChunk, int zStartChunk, int xSize, int zSize)
	{
		/* Simulated Y Value ONLY ONCE */
		int[] yVals = ChunkGeneratorBetaPlus.getSimYBlock(xStartChunk, zStartChunk, xSize, zSize, worldObj);

		Biome[] biomeArr = new Biome[xSize * zSize];
		temperatures = octave1.generateOctaves(temperatures, (double) xStartChunk, (double) zStartChunk, xSize, xSize, scaleVal, scaleVal, 0.25);
		humidities = octave2.generateOctaves(humidities, (double) xStartChunk, (double) zStartChunk, xSize, xSize, scaleVal * mult, scaleVal * mult, 0.3333333333333333);
		noise = octave3.generateOctaves(noise, (double) xStartChunk, (double) zStartChunk, xSize, xSize, 0.25, 0.25, 0.5882352941176471);
		int counter = 0;
		// X value, could be in chunks or Blocks. IDK Yet.
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
				// Now Inject Oceans Here by getting the position and height at position.
				BetaPlus.LOGGER.info("X, Z:" + x + ", " + z);
				BetaPlus.LOGGER.info("CPos: " + (xStartChunk + (x/16)) + ", " + (zStartChunk + (z/16)));
				//IChunk chunk = worldObj.getChunk(xStartChunk + (x / 16), zStartChunk + (x / 16)); //Gets the chunk operated on.
				int xP = (xStartChunk + (x/16)) * 16 + x;
				int zP = (zStartChunk + (z/16)) * 16 + z;
				BetaPlus.LOGGER.info("Pos: " + xP + ", " + zP);
				if (ChunkGeneratorBetaPlus.getSimulatedYPos(xStartChunk + (x/16),zStartChunk + (z/16), worldObj, new BlockPos(xP, 0, zP)) < settings.getSeaLevel())
				{
					BetaPlus.LOGGER.info("Add Oceans");
					// For Test
					biomeArr[counter] = Biomes.FROZEN_OCEAN;
				}
				counter++;
			}
		}

		/* Now, pseudo-generate the terrain so we can inject Oceans */
		/* Sped up, we only need to make a generator object once */
		/* Allows for moving biome detection to Biome Provider for future-proofing */
		/* WAYYY TOO SLOW */

		return biomeArr;
	}

	//BEGIN OVERRIDES
	/* This HAS to be populated to avoid issues */
	@Override
	public List<Biome> getBiomesToSpawnIn()
	{
		return Lists.newArrayList(BiomeGenBetaPlus.beach.handle, BiomeGenBetaPlus.desert.handle);
	}

	/* Should be avoided if possible, doesnt provide Oceans OR Beaches */
	@Override
	public Biome getBiome(BlockPos pos, Biome defaultBiome)
	{
		return getBiomes(pos.getX(), pos.getZ(), 1, 1, true)[0];
		//return this.cache.getBiome(pos.getX(), pos.getZ(), defaultBiome);
	}

	@Override
	public Biome[] getBiomes(int x, int z, int width, int length)
	{
		return getBiomes(x, z, width, length, true);
	}

	@Override
	public Biome[] getBiomes(int x, int z, int width, int length, boolean cacheFlag)
	{
		// Possibly: Generate All chunks, get Biomes from that?
		// Or: Simulate Height n stuff.
		return generateBiomes(x, z, width, length);
		//return cacheFlag && width == 16 && length == 16 && (x & 15) == 0 && (z & 15) == 0 ? this.cache.getCachedBiomes(x, z) : generateBiomes(x, z, width, length);
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
	/* Possibly causing issues with Ocean Structures */
	@Override
	public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomeList, Random random)
	{
		//BetaPlus.LOGGER.info("Looking for: " + Arrays.toString(biomes.toArray()));
		int i = x - range >> 2;
		int j = z - range >> 2;
		int k = x + range >> 2;
		int l = z + range >> 2;
		int xSize = k - i + 1;
		int zSize = l - j + 1;
		// Culprit Line
		Biome[] biomeArr = this.generateBiomes(i, j, xSize, zSize);
		//BetaPlus.LOGGER.info("X " + i + ", " + j);
		//Biome[] biomeArr = worldObj.getChunk(i, j).getBiomes();
		BlockPos blockpos = null;
		int k1 = 0;

		for(int counter = 0; counter < xSize * zSize; ++counter) {
			int i2 = i + counter % xSize << 2;
			int j2 = j + counter / xSize << 2;
			// If the input list of biomes has the generated biome list
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
			for(Biome biome : this.biomes) // Go through list of declared Biomes
			{
				if (biome.hasStructure(param1))
				{
					//System.out.println("We have structure: " + biome.getDisplayName().getString() + " " + param1.toString());
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
			Biome[] biomeArr = this.biomes;

			for(int i = 0; i < biomeArr.length; ++i) {
				Biome biome = biomeArr[i];
				this.topBlocksCache.add(biome.getSurfaceBuilderConfig().getTop());
			}
		}

		return this.topBlocksCache;
	}


	// BREAKS CLIMATE VALUES
	public double[] getClimateValuesatPos(BlockPos pos)
	{
		//Copied Over
		int startX = pos.getX();
		int startZ = pos.getZ();
		int xSize = 1;

		temps2 = octave11.generateOctaves(temps2, (double) startX, (double) startZ, xSize, xSize, scaleVal, scaleVal, 0.25);
		humid2 = octave22.generateOctaves(humid2, (double) startX, (double) startZ, xSize, xSize, scaleVal * mult, scaleVal * mult, 0.3333333333333333);
		noise1 = octave33.generateOctaves(noise1, (double) startX, (double) startZ, xSize, xSize, 0.25, 0.25, 0.5882352941176471);


		int counter = 0;
		double var9 = noise1[counter] * 1.1 + 0.5;
		double oneHundredth = 0.01;
		double point99 = 1.0 - oneHundredth;
		double temperatureVal = (temps2[counter] * 0.15 + 0.7) * point99 + var9 * oneHundredth;
		oneHundredth = 0.002;
		point99 = 1.0 - oneHundredth;
		double humidityVal = (humid2[counter] * 0.15 + 0.5) * point99 + var9 * oneHundredth;
		temperatureVal = 1.0 - (1.0 - temperatureVal) * (1.0 - temperatureVal);
		temperatureVal = MathHelper.clamp(temperatureVal, 0.0, 1.0);
		humidityVal = MathHelper.clamp(humidityVal, 0.0, 1.0);
		double[] returnVal = {temperatureVal, humidityVal};
		return returnVal;
	}

	/* Provides Ocean Biomes appropriate to temperature */
	public Biome getOceanBiome(BlockPos pos, boolean isDeep)
	{
		double[] climate = this.getClimateValuesatPos(pos);
		double temperature = climate[0];
		//return BiomeGenBetaPlus.getBiomeFromLookup(temperature, climate[1]);
		if (temperature < BetaPlusSelectBiome.coldValue / 1.35) // Changed to 1.5, then 1.35
		{
			if(isDeep)
			{
				return Biomes.DEEP_FROZEN_OCEAN;
			}
			return Biomes.FROZEN_OCEAN;
		}
		else if (temperature >= BetaPlusSelectBiome.veryHotVal && climate[1] >= 0.725)
		{
			return Biomes.WARM_OCEAN;
		}
		else if (temperature < BetaPlusSelectBiome.coldValue * 1.125) //Multiplied by 1.125 to make cold Oceans more common
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


	/* Gets Equivalent Grass Color */
	// Creates NASTINESS
	public int getGrassColor2(BlockPos pos)
	{
		double[] noises;
		noises = octave3.generateOctaves(noise, pos.getX(), pos.getZ(), 1, 1, 0.025D, 0.025D, 0.5882352941176471D);
		double d1 = noises[0] * 1.1D + 0.5D;
		noises = octave1.generateOctaves(noises, pos.getX(), pos.getZ(), 1, 1, 0.025D, 0.025D, 0.25D);
		double d2 = 0.01D;
		double d3 = 1.0D - d2;
		double temperature = (noises[0] * 0.15D + 0.7D) * d3 + d1 * d2;
		noises = octave2.generateOctaves(noises, pos.getX(), pos.getZ(), 1, 1, 0.05D, 0.05D, 0.3333333333333333D);
		d2 = 0.002D;
		d3 = 1.0D - d2;
		double humidity = (noises[0] * 0.15D + 0.5D) * d3 + d1 * d2;
		temperature = 1.0D - (1.0D - temperature) * (1.0D - temperature);
		temperature = MathHelper.clamp(temperature, 0.0, 1.0);
		humidity = MathHelper.clamp(humidity, 0.0, 1.0);
		return GrassColors.get(temperature, humidity);
	}

	/* Does NOT Work currently */
	public int getGrassColor(BlockPos blockPos)
	{
		double[] climate = getClimateValuesatPos(blockPos);
		return GrassColors.get(climate[0], climate[1]);
	}

	/* Cleans Cache */
	public void tick() {
		this.cache.cleanupCache();
	}
}

