package com.mrburgerus.betaplus.world.biome;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.feature.structure.Structure;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BiomeProviderAlphaPlus extends BiomeProvider
{
	private Biome biome;

	public BiomeProviderAlphaPlus(Biome selectedBiome)
	{
		this.biome = selectedBiome;
	}

	/* Just Like Beta Plus, generates a single biome */
	private Biome[] generateBiomes(int width, int depth)
	{
		Biome[] biomeArr = new Biome[width * depth];
		for (int i = 0; i < biomeArr.length; i++)
		{
			biomeArr[i] = this.biome;
		}
		return biomeArr;
	}

	@Override
	public List<Biome> getBiomesToSpawnIn()
	{
		return Lists.newArrayList(this.biome);
	}

	@Nullable
	@Override
	public Biome getBiome(BlockPos blockPos, @Nullable Biome biome)
	{
		return this.biome;
	}

	@Override
	public Biome[] getBiomes(int x, int z, int width, int depth)
	{
		return getBiomes(x, z, width, depth, true);
	}

	@Override
	public Biome[] getBiomes(int x, int z, int width, int length, boolean b)
	{
		return generateBiomes(width, length);
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
		Collections.addAll(set, this.generateBiomes(i1, j1));
		return set;
	}

	@Nullable
	@Override
	public BlockPos findBiomePosition(int i, int i1, int i2, List<Biome> list, Random random)
	{
		return null;
	}

	@Override
	public boolean hasStructure(Structure<?> structure)
	{
		return false;
	}

	@Override
	public Set<IBlockState> getSurfaceBlocks()
	{
		this.topBlocksCache.add(biome.getSurfaceBuilderConfig().getTop());
		return this.topBlocksCache;
	}
}
