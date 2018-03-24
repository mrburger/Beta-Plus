package com.mrburgerUS.betaplus.beta;

import com.mrburgerUS.betaplus.beta.biome.BiomeGenBeta;
import com.mrburgerUS.betaplus.beta.noise.NoiseGeneratorOctavesOld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.Random;

public class WorldChunkManager
{
	private NoiseGeneratorOctavesOld octave1;
	private NoiseGeneratorOctavesOld octave2;
	private NoiseGeneratorOctavesOld octave3;
	public double[] temperature;
	public double[] humidity;
	public double[] octave3Array;
	public BiomeGenBeta[] BiomeBaseArray;

	protected WorldChunkManager()
	{
	}

	public WorldChunkManager(World world)
	{
		octave1 = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 9871), 4);
		octave2 = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 39811), 4);
		octave3 = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 543321), 2);
	}

	public BiomeGenBeta getBiomeGenAtChunkCoord(ChunkPos chunkPos)
	{
		return getBiomeGenAt(chunkPos.x << 4, chunkPos.z << 4);
	}

	public BiomeGenBeta getBiomeGenAt(int chunkX, int chunkZ)
	{
		return findBiomeArray(chunkX, chunkZ, 1, 1)[0];
	}

	public double getTemperature(int x, int z)
	{
		temperature = octave1.generateOctaves(temperature, x, z, 1, 1, 0.02500000037252903, 0.02500000037252903, 0.5);
		return temperature[0];
	}

	public BiomeGenBeta[] findBiomeArray(int xChunk, int zChunk, int inV1, int inV2)
	{
		BiomeBaseArray = loadBlockGeneratorData(BiomeBaseArray, xChunk, zChunk, inV1, inV2);
		return BiomeBaseArray;
	}

	public double[] getTemperatures(double[] doubles, int var2, int var3, int var4, int var5)
	{
		if (doubles == null || doubles.length < var4 * var5)
		{
			doubles = new double[var4 * var5];
		}
		doubles = octave1.generateOctaves(doubles, var2, var3, var4, var5, 0.02500000037252903, 0.02500000037252903, 0.25);
		octave3Array = octave3.generateOctaves(octave3Array, var2, var3, var4, var5, 0.25, 0.25, 0.5882352941176471);
		int zeroVal = 0;
		for (int i = 0; i < var4; ++i)
		{
			for (int j = 0; j < var5; ++j)
			{
				double var9 = octave3Array[zeroVal] * 1.1 + 0.5;
				double oneHundredth = 0.01;
				double point99 = 1.0 - oneHundredth;
				double var15 = (doubles[zeroVal] * 0.15 + 0.7) * point99 + var9 * oneHundredth;
				if ((var15 = 1.0 - (1.0 - var15) * (1.0 - var15)) < 0.0)
				{
					var15 = 0.0;
				}
				if (var15 > 1.0)
				{
					var15 = 1.0;
				}
				doubles[zeroVal] = var15;
				++zeroVal;
			}
		}
		return doubles;
	}

	public BiomeGenBeta[] loadBlockGeneratorData(BiomeGenBeta[] biomeBases, int xChunk, int zChunk, int inV1, int inV2)
	{
		if (biomeBases == null || biomeBases.length < inV1 * inV2)
		{
			biomeBases = new BiomeGenBeta[inV1 * inV2];
		}
		temperature = octave1.generateOctaves(temperature, xChunk, zChunk, inV1, inV1, 0.02500000037252903, 0.02500000037252903, 0.25);
		humidity = octave2.generateOctaves(humidity, xChunk, zChunk, inV1, inV1, 0.05000000074505806, 0.05000000074505806, 0.3333333333333333);
		octave3Array = octave3.generateOctaves(octave3Array, xChunk, zChunk, inV1, inV1, 0.25, 0.25, 0.5882352941176471);
		int zeroVal = 0;
		for (int i = 0; i < inV1; ++i)
		{
			for (int j = 0; j < inV2; ++j)
			{
				double var9 = octave3Array[zeroVal] * 1.1 + 0.5;
				double oneHundredth = 0.01;
				double point99 = 1.0 - oneHundredth;
				double var15 = (temperature[zeroVal] * 0.15 + 0.7) * point99 + var9 * oneHundredth;
				oneHundredth = 0.002;
				point99 = 1.0 - oneHundredth;
				double var17 = (humidity[zeroVal] * 0.15 + 0.5) * point99 + var9 * oneHundredth;
				if ((var15 = 1.0 - (1.0 - var15) * (1.0 - var15)) < 0.0)
				{
					var15 = 0.0;
				}
				if (var17 < 0.0)
				{
					var17 = 0.0;
				}
				if (var15 > 1.0)
				{
					var15 = 1.0;
				}
				if (var17 > 1.0)
				{
					var17 = 1.0;
				}
				temperature[zeroVal] = var15;
				humidity[zeroVal] = var17;
				biomeBases[zeroVal++] = BiomeGenBeta.getBiomeFromLookup(var15, var17);
			}
		}
		return biomeBases;
	}
}

