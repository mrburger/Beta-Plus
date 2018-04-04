package com.mrburgerUS.betaplus.beta;

import com.mrburgerUS.betaplus.beta.biome.BiomeGenBeta;
import com.mrburgerUS.betaplus.beta.biome.support.SupportBiome;
import com.mrburgerUS.betaplus.beta.noise.NoiseGeneratorOctavesOld;
import com.mrburgerUS.betaplus.beta.noise.PerlinNoise;
import com.mrburgerUS.betaplus.beta.noise.VoronoiNoiseGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BiomeProviderBeta extends BiomeProvider
{
	private NoiseGeneratorOctavesOld octave1;
	private NoiseGeneratorOctavesOld octave2;
	private NoiseGeneratorOctavesOld octave3;
	public double[] temperatures;
	public double[] humidities;
	public double[] noise;
	public Biome[] biomeBaseArray;
	public final int biomeSetting;

	//Added in 0.3
	public static ArrayList<Biome> snowBiomesList = new ArrayList<>();
	public static ArrayList<Biome> coldBiomesList = new ArrayList<>();
	public static ArrayList<Biome> hotBiomesList = new ArrayList<>();
	public static ArrayList<Biome> wetBiomesList = new ArrayList<>();
	public static ArrayList<Biome> smallBiomesList = new ArrayList<>();

	//Noise for custom
	PerlinNoise perlinNoise;
	VoronoiNoiseGenerator voronoiNoise;


	public BiomeProviderBeta(World world, int biomeType)
	{
		octave1 = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 9871), 4);
		octave2 = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 39811), 4);
		octave3 = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 543321), 2);
		biomeSetting = biomeType;

		//0.3
		perlinNoise = new PerlinNoise(world.getSeed());
		voronoiNoise = new VoronoiNoiseGenerator(world.getSeed(), (short) 0);

		if (biomeSetting == 1)
		{
			snowBiomesList.addAll(SupportBiome.snowBiomes);
			coldBiomesList.addAll(SupportBiome.coldBiomes);
			hotBiomesList.addAll(SupportBiome.hotBiomes);
			wetBiomesList.addAll(SupportBiome.wetBiomes);
		}
	}

	public Biome[] findBiomeArray(int xPos, int zPos, int width, int depth)
	{
		biomeBaseArray = getBiomesForGeneration(biomeBaseArray, xPos, zPos, width, depth);
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
				biomeBases[counter++] = BiomeGenBeta.getBiomeFromLookup(temperatureVal, humidityVal);
			}
		}

		//If BiomeType is set to modded, allow biomes to Overwrite
		if (biomeSetting == 1)
		{
			double randomSelect;
			float offsetX;
			float offsetZ;
			double vNoise;
			float scaleFactor = 30;

			int counter2 = 0;
			for (int x = 0; x < width; x++)
			{
				for (int z = 0; z < height; z++)
				{
					offsetX = perlinNoise.noise2((xChunk + x) / scaleFactor, ((zChunk + z) / scaleFactor)) * 80 + perlinNoise.noise2((xChunk + x) / 7, (zChunk + z) / 7) * 20;
					offsetZ = perlinNoise.noise2((xChunk + x) / scaleFactor, ((zChunk + z) / scaleFactor)) * 80 + perlinNoise.noise2((xChunk + x - 1000) / 7, (xChunk + x) / 7) * 20;
					vNoise = (voronoiNoise.noise((xChunk + x + offsetX + 1000) / 1000D, (zChunk + z - offsetZ) / 1000D, 1) * 0.5f) + 0.5f;
					randomSelect = (voronoiNoise.noise((xChunk + x + offsetX + 2000) / 180, (zChunk + z - offsetZ) / 180, 1) * 0.5) + 0.5;
					randomSelect = MathHelper.clamp(randomSelect, 0, 0.9999999);

					//Biome Selection in Quarters
					if (vNoise < 0.25)
					{
						randomSelect *= snowBiomesList.size();
						biomeBases[counter2++] = snowBiomesList.get((int) randomSelect);
					}
					else if (vNoise < 0.5)
					{
						randomSelect *= coldBiomesList.size();
						biomeBases[counter2++] = coldBiomesList.get((int) randomSelect);
					}
					else if (vNoise < 0.75)
					{
						randomSelect *= hotBiomesList.size();
						biomeBases[counter2++] = hotBiomesList.get((int) randomSelect);
					}
					else
					{
						randomSelect *= wetBiomesList.size();
						biomeBases[counter2++] = wetBiomesList.get((int) randomSelect);
					}
				}
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
		return findBiomeArray(x, z, width, length);
	}

	@Override
	public boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed)
	{
		return false;
	}

	@Override
	public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random)
	{
		return null;
	}

	@Override
	public boolean isFixedBiome()
	{
		return false;
	}

	// Written
	public int getGrassColor(BlockPos blockPos)
	{
		findBiomeArray(blockPos.getX(), blockPos.getZ(), 1, 1);
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
		return ColorizerGrass.getGrassColor(temperature, humidity);
	}

	public int getGrassColor2(BlockPos pos)
	{
		this.noise = octave3.generateOctaves(this.noise, pos.getX(), pos.getZ(), 1, 1, 0.025D, 0.025D, 0.5882352941176471D);
		double d1 = noise[0] * 1.1D + 0.5D;
		this.noise = octave1.generateOctaves(this.noise, pos.getX(), pos.getZ(), 1, 1, 0.025D, 0.025D, 0.25D);
		double d2 = 0.01D;
		double d3 = 1.0D - d2;
		double temperature = (this.noise[0] * 0.15D + 0.7D) * d3 + d1 * d2;
		this.noise = octave2.generateOctaves(this.noise, pos.getX(), pos.getZ(), 1, 1, 0.05D, 0.05D, 0.3333333333333333D);
		d2 = 0.002D;
		d3 = 1.0D - d2;
		double humidity = (this.noise[0] * 0.15D + 0.5D) * d3 + d1 * d2;
		temperature = 1.0D - (1.0D - temperature) * (1.0D - temperature);
		temperature = MathHelper.clamp(temperature, 0.0, 1.0);
		humidity = MathHelper.clamp(humidity, 0.0, 1.0);
		return ColorizerGrass.getGrassColor(temperature, humidity);
	}
}

