package com.mrburgerUS.betaplus.beta;

import com.mrburgerUS.betaplus.beta.biome.BiomeGenBeta;
import com.mrburgerUS.betaplus.beta.noise.NoiseGeneratorOctavesOld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.layer.GenLayer;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BiomeProviderBeta extends BiomeProvider
{
	private GenLayer genBiomes;
	private GenLayer biomeIndexLayer;
	private NoiseGeneratorOctavesOld octave1;
	private NoiseGeneratorOctavesOld octave2;
	private NoiseGeneratorOctavesOld octave3;
	public double[] temperature;
	public double[] humidity;
	public double[] octave3Array;
	public Biome[] biomeBaseArray;
	private List<Biome> SPAWN_BIOMES = Arrays.asList(BiomeGenBeta.beach.handle, BiomeGenBeta.desert.handle);

	public BiomeProviderBeta(World world)
	{
		octave1 = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 9871), 4);
		octave2 = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 39811), 4);
		octave3 = new NoiseGeneratorOctavesOld(new Random(world.getSeed() * 543321), 2);
	}

	public Biome[] findBiomeArray(int xChunk, int zChunk, int width, int depth)
	{
		biomeBaseArray = getBiomesForGeneration(biomeBaseArray, xChunk, zChunk, width, depth);
		return biomeBaseArray;
	}

	//BEGIN OVERRIDES
	@Override
	public List<Biome> getBiomesToSpawnIn()
	{
		return this.SPAWN_BIOMES;
	}

	@Override
	public Biome getBiome(BlockPos pos)
	{
		return this.getBiome(pos, (Biome) null);
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
		temperature = octave1.generateOctaves(temperature, xChunk, zChunk, width, width, 0.02500000037252903, 0.02500000037252903, 0.25);
		humidity = octave2.generateOctaves(humidity, xChunk, zChunk, width, width, 0.05000000074505806, 0.05000000074505806, 0.3333333333333333);
		octave3Array = octave3.generateOctaves(octave3Array, xChunk, zChunk, width, width, 0.25, 0.25, 0.5882352941176471);
		int counter = 0;
		for (int i = 0; i < width; ++i)
		{
			for (int j = 0; j < height; ++j)
			{
				double var9 = octave3Array[counter] * 1.1 + 0.5;
				double oneHundredth = 0.01;
				double point99 = 1.0 - oneHundredth;
				double var15 = (temperature[counter] * 0.15 + 0.7) * point99 + var9 * oneHundredth;
				oneHundredth = 0.002;
				point99 = 1.0 - oneHundredth;
				double var17 = (humidity[counter] * 0.15 + 0.5) * point99 + var9 * oneHundredth;
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
				temperature[counter] = var15;
				humidity[counter] = var17;
				biomeBases[counter++] = BiomeGenBeta.getBiomeFromLookup(var15, var17).handle;
			}
		}
		return biomeBases;
	}

	@Override
	public Biome[] getBiomes(@Nullable Biome[] oldBiomeList, int x, int z, int width, int depth)
	{
		return this.getBiomes(oldBiomeList, x, z, width, depth, true);
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

}

