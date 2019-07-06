package com.mrburgerus.betaplus.world.alpha_plus;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mrburgerus.betaplus.world.alpha_plus.sim.AlphaPlusSimulator;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.feature.structure.Structure;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BiomeProviderAlphaPlus extends BiomeProvider
{
	private Biome landBiome;
	private Biome oceanBiome;
	/* Had to create custom biomes */
	/* Custom Biomes created so that ICE sheets Spawn on Oceans in snowy worlds */
	private static final Biome ALPHA_FROZEN_BIOME = Biomes.SNOWY_TUNDRA;
	public static final Biome ALPHA_FROZEN_OCEAN = Biomes.FROZEN_OCEAN;
	private static final Biome ALPHA_BIOME = Biomes.PLAINS;
	public static final Biome ALPHA_OCEAN = Biomes.DEEP_OCEAN;
	private static final Biome[] BIOMES_LIST = new Biome[]{ALPHA_FROZEN_BIOME, ALPHA_FROZEN_OCEAN, ALPHA_BIOME, ALPHA_OCEAN};
	// Simulator for Y-heights
	private AlphaPlusSimulator simulator;

	public BiomeProviderAlphaPlus(World world)
	{
		if (world.getWorldInfo().getGeneratorOptions().getString(WorldTypeAlphaPlus.SNOW_WORLD_TAG).equals("true"))
		{
			this.landBiome = ALPHA_FROZEN_BIOME;
			this.oceanBiome = ALPHA_FROZEN_OCEAN;
		}
		else
		{
			this.landBiome = ALPHA_BIOME;
			this.oceanBiome = ALPHA_OCEAN;
		}
		simulator = new AlphaPlusSimulator(world);
	}

	/* Just Like Beta Plus, generates a single landBiome */
	private Biome[] generateBiomes(int startX, int startZ, int xSize, int zSize)
	{
		Biome[] biomeArr = new Biome[xSize * zSize];
		for (int i = 0; i < biomeArr.length; i++)
		{
			biomeArr[i] = this.landBiome;
		}
		return biomeArr;
	}

	/* Adds OCEANS to the mix to the Biome Provider. */
	/* ONLY CALL WHEN NECESSARY, has to simulate the Y-heights of the world */
	/* useAverage should be TRUE if searching for Monuments, false Otherwise */
	private Biome[] generateBiomesWithOceans(int startX, int startZ, int xSize, int zSize, boolean useAverage)
	{
		Biome[] biomeArr = new Biome[xSize * zSize];
		int counter = 0;
		// Swapped X and Z, to match beta (HAD NO EFFECT!)
		for (int x = startX; x < xSize + startX; ++x)
		{
			for (int z = startZ; z < zSize + startZ; ++z)
			{
				BlockPos blockPos = new BlockPos(x, 0, z);
				//Assign this first
				biomeArr[counter] = this.landBiome;
				// If using the 3x3 Average (Monuments and Mansions)
				if (useAverage)
				{
					// Changed from avg to chunk
					Pair<Integer, Boolean> avg = simulator.simulateYAvg(blockPos);
					// Tried 56, 58, 57
					if (avg.getFirst() < 57 && !avg.getSecond())
					{
						biomeArr[counter] = this.oceanBiome;
					}
				}
				else
				{
					Pair<Integer, Boolean> avg = simulator.simulateYChunk(blockPos);
					if (avg.getFirst() < 56) //&& !avg.getSecond())  //Typically for Shipwrecks, Ruins, and Chests
					{
						biomeArr[counter] = this.oceanBiome;
					}
				}
				counter++;
			}
		}
		return biomeArr;
	}

	@Override
	public List<Biome> getBiomesToSpawnIn()
	{
		return Lists.newArrayList(this.landBiome);
	}

	@Override
	public Biome getBiome(int i, int i1)
	{
		return this.generateBiomesWithOceans(i, i1, 1, 1, false)[0];
	}

	@Override
	public Biome[] getBiomes(int x, int z, int width, int length, boolean b)
	{
		return generateBiomes(x, z, width, length);
	}

	/* Used By Ocean Monuments and Mansions */
	@Override
	public Set<Biome> getBiomesInSquare(int centerX, int centerZ, int sideLength)
	{
		Set<Biome> set = Sets.newHashSet();
		Collections.addAll(set, this.generateBiomesWithOceans(centerX, centerZ, sideLength, sideLength, true));
		return set;
	}

	@Nullable
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
	public Set<BlockState> getSurfaceBlocks()
	{
		this.topBlocksCache.add(landBiome.getSurfaceBuilderConfig().getTop());
		return this.topBlocksCache;
	}
}
