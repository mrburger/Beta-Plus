package com.mrburgerus.betaplus.world.beta_plus;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.world.biome.BetaPlusSelectBiome;
import com.mrburgerus.betaplus.world.biome.BiomeGenBetaPlus;
import com.mrburgerus.betaplus.world.noise.NoiseGeneratorOctavesBeta;
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

/* Creates Biome Values */
/* Oceans are not a part of Beta proper, so I'm injecting them into findBiomePosition() because doing it every time generateBiomes()
	is called is VERY slow and tedious.
 */
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

	public BiomeProviderBetaPlus(World world, BetaPlusGenSettings settingsIn)
	{
		temperatureOctave = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 9871), 4);
		humidityOctave = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 39811), 4);
		noiseOctave = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 543321), 2);
		settings = settingsIn;
		scaleVal = settings.getScale();
		mult = settings.getMultiplierBiome();

		octaves12 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 16);
		octaves22 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 16);
		octaves32 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 8);
		octaves62 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 10);
		octaves72 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 16);

		seedLong = world.getSeed();
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
				/* Put Height Values */


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
		//TODO: FIND A WAY TO INJECT OCEANS
		//BetaPlus.LOGGER.info(i + ", " + j + ", " + xSize + ", " + zSize);
		Biome[] biomeArr = this.generateBiomes(i, j, xSize, zSize);
		// If the chunk exists, Get the biome map(s) from there (DOESNT WORK)
		// Simulate Y values at Position?
		// Generates in multiples of 16, the Simulated y-height
		// first and second param are startXchunk
		boolean[] isOcean = isBelowSeaLevelSim(x / chunkSize, z / chunkSize, xSize, zSize);
		/*
		for (int i1 = 0; i1 < isOcean.length; i1++)
		{
			if(isOcean[i1])
			{
				BetaPlus.LOGGER.info("Inject Ocean");
				biomeArr[i1] = Biomes.OCEAN;
			}
		}
		*/
		/*
		BetaPlus.LOGGER.info("Inputs: " + x + ", " + z + ", " + xSize + ", " + zSize);
		int iterator = 0;
		for (int s = 0; s < xSize; s++)
		{
			for(int t = 0; t < zSize; t++)
			{
				// ((i / chunkSize) + i % chunkSize)
				//int arrPos = ((x / chunkSize) + i % chunkSize) * s * xSize;
				//int arrPos = s * xSize + t;
				//BetaPlus.LOGGER.info("Pos: " + arrPos);
				if(isOcean[iterator])
				{
					BetaPlus.LOGGER.info("Inject Oceans");
					biomeArr[iterator] = Biomes.OCEAN;
				}
				iterator++;
			}
		}
		*/


		BlockPos blockpos = null;
		int k1 = 0;

		for(int counter = 0; counter < xSize * zSize; ++counter) {
			int i2 = i + counter % xSize << 2;
			int j2 = j + counter / xSize << 2;
			// If the input list of BIOMES_LIST has
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

	/* Work in Progress */
	public boolean isPosBelowSeaLevel(int xPos, int zPos)
	{
		//Create Simulated Value
		int xS = 1;
		int yS = 5;
		int zS = 1;
		int i = xPos / chunkSize;
		int j = xPos / chunkSize; // Second X-iterator? (OR Z Iterator)
		int k = 0; // Unknown in range [0, yS - 1) (possibly)
		hNoiseSim = octaveSim(hNoiseSim, xPos / 4, zPos / 4, xS, yS, zS);
		double var16 = hNoiseSim[(i * zS + j) * yS + k];
		double var18 = hNoiseSim[(i * zS + j + 1) * yS + k];
		double var20 = hNoiseSim[((i + 1) * zS + j) * yS + k];
		double var22 = hNoiseSim[((i + 1) * zS + j + 1) * yS + k];
		double var24 = (hNoiseSim[(i * zS + j) * yS + k + 1] - var16) * 0.125;
		double var26 = (hNoiseSim[(i * zS + j + 1) * yS + k + 1] - var18) * 0.125;
		double var28 = (hNoiseSim[((i + 1) * zS + j) * yS + k + 1] - var20) * 0.125;
		double var30 = (hNoiseSim[((i + 1) * zS + j + 1) * yS + k + 1] - var22) * 0.125;

		return false;
	}

	public boolean[] isBelowSeaLevelSim(int chunkX, int chunkZ, int xSize, int zSize)
	{
		// First calculate number of chunks we need to generate
		int xChunkSize = (xSize / chunkSize) + 1;
		int zChunkSize = (zSize / chunkSize) + 1; // Rounded down, just like we need. Add one because we always generate at least one chunk.
		boolean[] output = new boolean[xChunkSize * chunkSize * zChunkSize * chunkSize];
		int counter = 0;
		for (int s = 0; s < xChunkSize; s++) // Generate chunk Block
		{
			for (int t = 0; t < zChunkSize; t++)
			{
				hNoiseSim = octaveSim(hNoiseSim, (chunkX + s) * 4, (chunkZ + t) * 4, 5, 17, 5, seedLong, new Random(seedLong));
				for (int i = 0; i < 4; ++i)
				{
					for (int j = 0; j < 4; ++j)
					{
						for (int k = 0; k < 16; ++k)
						{
							double var16 = hNoiseSim[(i * 5 + j) * 17 + k];
							double var18 = hNoiseSim[(i * 5 + j + 1) * 17 + k];
							double var20 = hNoiseSim[((i + 1) * 5 + j) * 17 + k];
							double var22 = hNoiseSim[((i + 1) * 5 + j + 1) * 17 + k];
							double var24 = (hNoiseSim[(i * 5 + j) * 17 + k + 1] - var16) * 0.125;
							double var26 = (hNoiseSim[(i * 5 + j + 1) * 17 + k + 1] - var18) * 0.125;
							double var28 = (hNoiseSim[((i + 1) * 5 + j) * 17 + k + 1] - var20) * 0.125;
							double var30 = (hNoiseSim[((i + 1) * 5 + j + 1) * 17 + k + 1] - var22) * 0.125;
							for (int l = 0; l < 8; ++l)
							{
								double quarter = 0.25;
								double var35 = var16;
								double var37 = var18;
								double var39 = (var20 - var16) * quarter;
								double var41 = (var22 - var18) * quarter;
								for (int m = 0; m < 4; ++m)
								{
									int x = m + i * 4;
									int y = k * 8 + l;
									int z = j * 4;
									double var46 = 0.25;
									double var48 = var35;
									double var50 = (var37 - var35) * var46;
									/* Possible Second Z-iterator */
									for (int n = 0; n < 4; ++n)
									{
										// If below sea level and we find air, we know this will be an ocean.
										if (var48 <= 0.0 && y < settings.getSeaLevel())
										{
											// Found a Stone Block, re-assign until we find Air.
											//output[counter / zSize][counter % zSize] = true;
											output[counter] = true;
										}
										++z;
										var48 += var50;
									}
									var35 += var39;
									var37 += var41;
								}
								var16 += var24;
								var18 += var26;
								var20 += var28;
								var22 += var30;
							}
							counter++;
						}
					}

				}
			}
		}
		//BetaPlus.LOGGER.info("Finished: " + chunkX + ", " + chunkZ);
		return output;
	}

	public boolean[][] isBelowSeaLevelSim(int chunkX, int chunkZ, int xSize, int zSize, boolean yes)
	{
		// First calculate number of chunks we need to generate
		int xChunkSize = (xSize / chunkSize) + 1;
		int zChunkSize = (zSize / chunkSize) + 1; // Rounded down, just like we need. Add one because we always generate at least one chunk.
		boolean[][] output = new boolean[xChunkSize * chunkSize][zChunkSize * chunkSize];
		int counter = 0;
		for (int s = 0; s < xChunkSize; s++) // Generate chunk Block
		{
			for (int t = 0; t < zChunkSize; t++)
			{
				hNoiseSim = octaveSim(hNoiseSim, (chunkX + s) * 4, (chunkZ + t) * 4, 5, 17, 5, seedLong, new Random(seedLong));
				for (int i = 0; i < 4; ++i)
				{
					for (int j = 0; j < 4; ++j)
					{
						for (int k = 0; k < 16; ++k)
						{
							double var16 = hNoiseSim[(i * 5 + j) * 17 + k];
							double var18 = hNoiseSim[(i * 5 + j + 1) * 17 + k];
							double var20 = hNoiseSim[((i + 1) * 5 + j) * 17 + k];
							double var22 = hNoiseSim[((i + 1) * 5 + j + 1) * 17 + k];
							double var24 = (hNoiseSim[(i * 5 + j) * 17 + k + 1] - var16) * 0.125;
							double var26 = (hNoiseSim[(i * 5 + j + 1) * 17 + k + 1] - var18) * 0.125;
							double var28 = (hNoiseSim[((i + 1) * 5 + j) * 17 + k + 1] - var20) * 0.125;
							double var30 = (hNoiseSim[((i + 1) * 5 + j + 1) * 17 + k + 1] - var22) * 0.125;
							for (int l = 0; l < 8; ++l)
							{
								double quarter = 0.25;
								double var35 = var16;
								double var37 = var18;
								double var39 = (var20 - var16) * quarter;
								double var41 = (var22 - var18) * quarter;
								for (int m = 0; m < 4; ++m)
								{
									int x = m + i * 4;
									int y = k * 8 + l;
									int z = j * 4;
									double var46 = 0.25;
									double var48 = var35;
									double var50 = (var37 - var35) * var46;
									/* Possible Second Z-iterator */
									for (int n = 0; n < 4; ++n)
									{
										// If below sea level and we find air, we know this will be an ocean.
										if (var48 <= 0.0 && y < settings.getSeaLevel())
										{
											// Found a Stone Block, re-assign until we find Air.
											//output[counter / zSize][counter % zSize] = true;
										}
										++z;
										var48 += var50;
									}
									var35 += var39;
									var37 += var41;
								}
								var16 += var24;
								var18 += var26;
								var20 += var28;
								var22 += var30;
							}
							counter++;
						}
					}

				}
			}
		}
		BetaPlus.LOGGER.info("Finished: " + chunkX + ", " + chunkZ);
		return output;
	}

	/* xSize, ySize, zSize could be incorrect Names */
	private double[] octaveSim(double[] values, int xPos, int zPos, int xSize, int ySize, int zSize, long seed, Random rand1)
	{

		if (values == null)
		{
			values = new double[xSize * ySize * zSize];
		}
		double noiseFactor = 684.412;
		octaveArr42 = octaves62.generateNoiseOctaves(octaveArr42, xPos, zPos, xSize, zSize, 1.121, 1.121, 0.5);
		octaveArr52 = octaves72.generateNoiseOctaves(octaveArr52, xPos, zPos, xSize, zSize, 200.0, 200.0, 0.5);
		octaveArr12 = octaves32.generateNoiseOctaves(octaveArr12, xPos, 0, zPos, xSize, ySize, zSize, noiseFactor / 80.0, noiseFactor / 160.0, noiseFactor / 80.0);
		octaveArr22 = octaves12.generateNoiseOctaves(octaveArr22, xPos, 0, zPos, xSize, ySize, zSize, noiseFactor, noiseFactor, noiseFactor);
		octaveArr32 = octaves22.generateNoiseOctaves(octaveArr32, xPos, 0, zPos, xSize, ySize, zSize, noiseFactor, noiseFactor, noiseFactor);

		int incrementer1 = 0;
		int incrementer2 = 0;
		int someThing = 16 / xSize;
		for (int i = 0; i < xSize; ++i)
		{
			//int var18 = i * someThing + someThing / 2;
			for (int j = 0; j < zSize; ++j)
			{
				double var29;
				//int var20 = j * someThing + someThing / 2;
				double var21 = 1; //temps[var18 * 16 + var20];
				double var23 = 1; //humidities[var18 * 16 + var20] * var21;
				double var25 = 1.0 - var23;
				var25 *= var25;
				var25 *= var25;
				var25 = 1.0 - var25;
				double var27 = (octaveArr42[incrementer2] + 256.0) / 512.0;
				/*
				if ((var27 *= var25) > 1.0)
				{
					var27 = 1.0;
				}
				*/
				if ((var29 = octaveArr52[incrementer2] / 8000.0) < 0.0)
				{
					var29 = (-var29) * 0.3;
				}
				if ((var29 = var29 * 3.0 - 2.0) < 0.0)
				{
					if ((var29 /= 2.0) < -1.0)
					{
						var29 = -1.0;
					}
					var29 /= 1.4;
					var29 /= 2.0;
					var27 = 0.0;
				}
				else
				{
					if (var29 > 1.0)
					{
						var29 = 1.0;
					}
					var29 /= 8.0;
				}
				if (var27 < 0.0)
				{
					var27 = 0.0;
				}
				var27 += 0.5;
				var29 = var29 * (double) ySize / 16.0;
				double var31 = (double) ySize / 2.0 + var29 * 4.0;
				++incrementer2;
				for (int k = 0; k < ySize; ++k)
				{
					double var34;
					double var36 = ((double) k - var31) * 12.0 / var27;
					if (var36 < 0.0)
					{
						var36 *= 4.0;
					}
					double var38 = octaveArr22[incrementer1] / 512.0;
					double var40 = octaveArr32[incrementer1] / 512.0;
					double var42 = (octaveArr12[incrementer1] / 10.0 + 1.0) / 2.0;
					var34 = var42 < 0.0 ? var38 : (var42 > 1.0 ? var40 : var38 + (var40 - var38) * var42);
					var34 -= var36;
					if (k > ySize - 4)
					{
						double var44 = (float) (k - (ySize - 4)) / 3.0f;
						var34 = var34 * (1.0 - var44) + -10.0 * var44;
					}
					values[incrementer1] = var34;
					++incrementer1;
				}
			}
		}
		return values;
	}

	private double[] octaveSim(double[] values, int xPos, int zPos, int xSize, int ySize, int zSize)
	{
		if (values == null)
		{
			values = new double[xSize * ySize * zSize];
		}
		double noiseFactor = 684.412;
		double[] temps = this.temperatures;
		double[] humidities = this.humidities;
		octaveArr42 = octaves62.generateNoiseOctaves(octaveArr42, xPos, zPos, xSize, zSize, 1.121, 1.121, 0.5);
		octaveArr52 = octaves72.generateNoiseOctaves(octaveArr52, xPos, zPos, xSize, zSize, 200.0, 200.0, 0.5);
		octaveArr12 = octaves32.generateNoiseOctaves(octaveArr12, xPos, 0, zPos, xSize, ySize, zSize, noiseFactor / 80.0, noiseFactor / 160.0, noiseFactor / 80.0);
		octaveArr22 = octaves12.generateNoiseOctaves(octaveArr22, xPos, 0, zPos, xSize, ySize, zSize, noiseFactor, noiseFactor, noiseFactor);
		octaveArr32 = octaves22.generateNoiseOctaves(octaveArr32, xPos, 0, zPos, xSize, ySize, zSize, noiseFactor, noiseFactor, noiseFactor);

		int incrementer1 = 0;
		int incrementer2 = 0;
		int var16 = 16 / xSize;
		for (int i = 0; i < xSize; ++i)
		{
			int var18 = i * var16 + var16 / 2;
			for (int j = 0; j < zSize; ++j)
			{
				double var29;
				int var20 = j * var16 + var16 / 2;
				double var21 = temps[var18 * 16 + var20];
				double var23 = humidities[var18 * 16 + var20] * var21;
				double var25 = 1.0 - var23;
				var25 *= var25;
				var25 *= var25;
				var25 = 1.0 - var25;
				double var27 = (octaveArr42[incrementer2] + 256.0) / 512.0;
				if ((var27 *= var25) > 1.0)
				{
					var27 = 1.0;
				}
				if ((var29 = octaveArr52[incrementer2] / 8000.0) < 0.0)
				{
					var29 = (-var29) * 0.3;
				}
				if ((var29 = var29 * 3.0 - 2.0) < 0.0)
				{
					if ((var29 /= 2.0) < -1.0)
					{
						var29 = -1.0;
					}
					var29 /= 1.4;
					var29 /= 2.0;
					var27 = 0.0;
				}
				else
				{
					if (var29 > 1.0)
					{
						var29 = 1.0;
					}
					var29 /= 8.0;
				}
				if (var27 < 0.0)
				{
					var27 = 0.0;
				}
				var27 += 0.5;
				var29 = var29 * (double) ySize / 16.0;
				double var31 = (double) ySize / 2.0 + var29 * 4.0;
				++incrementer2;
				for (int k = 0; k < ySize; ++k)
				{
					double var34;
					double var36 = ((double) k - var31) * 12.0 / var27;
					if (var36 < 0.0)
					{
						var36 *= 4.0;
					}
					double var38 = octaveArr22[incrementer1] / 512.0;
					double var40 = octaveArr32[incrementer1] / 512.0;
					double var42 = (octaveArr12[incrementer1] / 10.0 + 1.0) / 2.0;
					var34 = var42 < 0.0 ? var38 : (var42 > 1.0 ? var40 : var38 + (var40 - var38) * var42);
					var34 -= var36;
					if (k > ySize - 4)
					{
						double var44 = (float) (k - (ySize - 4)) / 3.0f;
						var34 = var34 * (1.0 - var44) + -10.0 * var44;
					}
					values[incrementer1] = var34;
					++incrementer1;
				}
			}
		}
		return values;
	}

}
