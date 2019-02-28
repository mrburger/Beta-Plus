package com.mrburgerus.betaplus.world.alpha_plus;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.util.ResourceHelper;
import com.mrburgerus.betaplus.world.alpha_plus.sim.AlphaPlusSimulator;
import com.mrburgerus.betaplus.world.biome.alpha.BiomeAlphaFrozenLand;
import com.mrburgerus.betaplus.world.biome.alpha.BiomeAlphaFrozenOcean;
import com.mrburgerus.betaplus.world.biome.alpha.BiomeAlphaLand;
import com.mrburgerus.betaplus.world.biome.alpha.BiomeAlphaOcean;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.registries.ForgeRegistries;

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
	private static final Biome ALPHA_FROZEN_BIOME = ForgeRegistries.BIOMES.getValue(new ResourceLocation(ResourceHelper.getResourceStringBetaPlus(BiomeAlphaFrozenLand.NAME)));;
	public static final Biome ALPHA_FROZEN_OCEAN = ForgeRegistries.BIOMES.getValue(new ResourceLocation(ResourceHelper.getResourceStringBetaPlus(BiomeAlphaFrozenOcean.NAME)));
	private static final Biome ALPHA_BIOME = ForgeRegistries.BIOMES.getValue(new ResourceLocation(ResourceHelper.getResourceStringBetaPlus(BiomeAlphaLand.NAME)));
	public static final Biome ALPHA_OCEAN = ForgeRegistries.BIOMES.getValue(new ResourceLocation(ResourceHelper.getResourceStringBetaPlus(BiomeAlphaOcean.NAME)));
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
	private Biome[] generateBiomes(int x, int z, int width, int depth)
	{
		Biome[] biomeArr = new Biome[width * depth];
		for (int i = 0; i < biomeArr.length; i++)
		{
			biomeArr[i] = this.landBiome;
		}
		return biomeArr;
	}

	/* Adds OCEANS to the mix to the Biome Provider. */
	/* ONLY CALL WHEN NECESSARY, has to simulate the Y-heights of the world */
	private Biome[] generateBiomesWithOceans(int startX, int startZ, int width, int depth)
	{
		int xP = startX;
		int zP = startZ;
		Biome[] biomeArr = new Biome[width * depth];
		for (int i = 0; i < biomeArr.length; i++)
		{
			xP = startX + i % width;
			zP = startZ + Math.floorDiv(i, depth);
			int pos = (xP - startX) + ((zP - startZ) * depth);
			if (pos != i)
			{
				BetaPlus.LOGGER.warn("Not equal: " + pos + " : " + i);
			}

			if (simulator.simulateYSingle(new BlockPos(xP, 0, zP)) < 56) // Deep Ocean Value
			{
				biomeArr[i] = this.oceanBiome;
 			}
			else
			{
				biomeArr[i] = this.landBiome;
			}
		}
		return biomeArr;
	}

	@Override
	public List<Biome> getBiomesToSpawnIn()
	{
		return Lists.newArrayList(this.landBiome);
	}

	/* Used By Shipwrecks and Buried Treasure */
	@Nullable
	@Override
	public Biome getBiome(BlockPos blockPos, @Nullable Biome biome)
	{
		return this.generateBiomesWithOceans(blockPos.getX(), blockPos.getZ(), 1, 1)[0];
	}

	@Override
	public Biome[] getBiomes(int x, int z, int width, int depth)
	{
		return getBiomes(x, z, width, depth, true);
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
		int i = centerX - sideLength >> 2;
		int j = centerZ - sideLength >> 2;
		int k = centerX + sideLength >> 2;
		int l = centerZ + sideLength >> 2;
		int i1 = k - i + 1;
		int j1 = l - j + 1;
		Set<Biome> set = Sets.newHashSet();
		Collections.addAll(set, this.generateBiomesWithOceans(i, j, i1, j1));
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
	public Set<IBlockState> getSurfaceBlocks()
	{
		this.topBlocksCache.add(landBiome.getSurfaceBuilderConfig().getTop());
		return this.topBlocksCache;
	}
}
