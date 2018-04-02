package com.mrburgerUS.betaplus.beta.biome.layer;

import com.mrburgerUS.betaplus.beta.biome.BiomeGenBeta;
import com.mrburgerUS.betaplus.beta.noise.NoiseGeneratorOctavesOld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import java.util.Random;

public class GenLayerBeta extends GenLayer
{

	private NoiseGeneratorOctavesOld octave1;
	private NoiseGeneratorOctavesOld octave2;
	private NoiseGeneratorOctavesOld octave3;

	public GenLayerBeta(long baseSeed, GenLayer baseLayer)
	{
		super(baseSeed);

		this.parent = baseLayer;
	}

	@Override
	public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight)
	{
		int[] parentInts = this.parent.getInts(areaX, areaY, areaWidth, areaHeight);
		int[] biomeCache = IntCache.getIntCache(areaWidth * areaHeight);

		double[] temperatures = octave1.generateOctaves(null, areaX, areaY, areaWidth, areaHeight, 0.025, 0.025, 0.25);
		double[] humidities = octave2.generateOctaves(null, areaX, areaY, areaWidth, areaHeight, 0.05, 0.05, 0.3333333333333333);
		double[] noise = octave3.generateOctaves(null, areaX, areaY, areaWidth, areaHeight, 0.025, 0.025, 0.5882352941176471);

		int counter = 0;
		for (int i = 0; i < areaWidth; i++)
		{
			int c2 = i;
			for (int j = 0; j < areaHeight; j++)
			{
				double noiseVal = noise[i] * 1.1D * 0.5D;
				double temperatureVal = (temperatures[i] * 0.15D + 0.7D) * 0.9D + noiseVal * 0.1D;
				double humidityVal = (humidities[i] * 0.15D + 0.5D) * 0.998D + noiseVal * 0.002D;
				temperatureVal = 1.0D - (1.0D - temperatureVal) * (1.0D - temperatureVal);
				MathHelper.clamp(temperatureVal, 0.0, 1.0);
				MathHelper.clamp(humidityVal, 0.0, 1.0);

				biomeCache[c2] = getBiome(temperatureVal, humidityVal, parentInts[c2]);

				counter++;
				c2 += areaWidth;
			}
		}
		return biomeCache;
	}

	@Override
	public void initWorldGenSeed(long seed)
	{
		octave1 = new NoiseGeneratorOctavesOld(new Random(seed * 9871), 4);
		octave2 = new NoiseGeneratorOctavesOld(new Random(seed * 39811), 4);
		octave3 = new NoiseGeneratorOctavesOld(new Random(seed * 543321), 2);
	}

	private static int getBiome(double temperature, double humidity, int mutationVal)
	{
		humidity *= temperature;
		if (temperature < 0.5D)
		{
			if ((mutationVal == 11) && (temperature < 0.1D))
			{
				return Biome.getIdForBiome(BiomeGenBeta.iceSpikes.handle);
			}
			if (humidity < 0.2D)
			{
				return Biome.getIdForBiome(BiomeGenBeta.tundra.handle);
			}
			return Biome.getIdForBiome(BiomeGenBeta.taiga.handle);
		}
		if (temperature < 0.9375D)
		{
			if (humidity < 0.2D)
			{
				if (temperature > 0.8D)
				{
					return Biome.getIdForBiome(BiomeGenBeta.savanna.handle);
				}
				if ((mutationVal & 0xFFFFFFFE) == 12)
				{
					return Biome.getIdForBiome(BiomeGenBeta.flowerPlains.handle);
				}
				return Biome.getIdForBiome(BiomeGenBeta.plains.handle);
			}
			if (humidity > 0.8D)
			{
				return Biome.getIdForBiome(BiomeGenBeta.swampland.handle);
			}
			if ((mutationVal < 4) && (humidity > 0.5D))
			{
				return Biome.getIdForBiome(BiomeGenBeta.roofForest.handle);
			}
			if (temperature < 0.7D)
			{
				if ((mutationVal & 0xFFFFFFFE) == 4)
				{
					return Biome.getIdForBiome(BiomeGenBeta.taiga.handle);
				}
				if (mutationVal == 6)
				{
					return Biome.getIdForBiome(BiomeGenBeta.megaTaiga.handle);
				}
				if (mutationVal == 7)
				{
					return Biome.getIdForBiome(BiomeGenBeta.megaTaiga.handle);
				}
			}
			if (mutationVal == 8 || mutationVal == 9)
			{
				return Biome.getIdForBiome(BiomeGenBeta.birchForest.handle);
			}
			if (mutationVal == 10)
			{
				return Biome.getIdForBiome(BiomeGenBeta.seasonalForest.handle);
			}
			return Biome.getIdForBiome(BiomeGenBeta.forest.handle);
		}
		if (humidity < 0.2D)
		{
			if ((mutationVal & 0xFFFFFFFE) == 14)
			{
				return Biome.getIdForBiome(BiomeGenBeta.mesa.handle);
			}
			return Biome.getIdForBiome(BiomeGenBeta.desert.handle);
		}
		if (humidity > 0.8D)
		{
			return Biome.getIdForBiome(BiomeGenBeta.rainforest.handle);
		}
		if (humidity > 0.75D)
		{
			return Biome.getIdForBiome(BiomeGenBeta.rainforest.handle);
		}
		if ((mutationVal & 0xFFFFFFFE) == 12)
		{
			return Biome.getIdForBiome(BiomeGenBeta.flowerPlains.handle);
		}
		return Biome.getIdForBiome(BiomeGenBeta.plains.handle);
	}
}
