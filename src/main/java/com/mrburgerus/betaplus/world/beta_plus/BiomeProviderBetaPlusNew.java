package com.mrburgerus.betaplus.world.beta_plus;

import biomesoplenty.api.biome.BOPBiomes;
import com.mrburgerus.betaplus.BetaPlus;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.Set;

/* This is a WIP class that will provide a NEW biome system. */
/* Based off BiomeProviderBetaPlus */
public class BiomeProviderBetaPlusNew extends BiomeProvider
{
	// FIELDS //

	// Biome List, possibly a good injection point for Biomes O' Plenty stuff.
	private static final Biome[] BIOMES_LIST = buildBiomesList();


	// METHODS //

	// Builds the possible biomes list.
	private static Biome[] buildBiomesList()
	{
		// Possibly a for-each would be better.
		// Initialize all enabled biomes.
		// Check for Biomes o' Plenty, and if so, get the enabled biome list.
		// This will determine which biomes are placed where

		if (ModList.get().isLoaded("biomesoplenty"))
		{
			BetaPlus.LOGGER.debug("ADDING BIOME");
			return new Biome[] {BOPBiomes.bayou.get()};
		}


		return new Biome[0];
	}








	// OVERRIDES //

	// Gets Biome for a specific coordinate
	// X, Z are block coordinates
	@Override
	public Biome getBiome(int x, int z)
	{
		return null;
	}

	// Gets a biome array of xSize * zSize
	// cacheFlag is not implemented yet.
	@Override
	public Biome[] getBiomes(int x, int z, int xSize, int zSize, boolean cacheFlag)
	{
		return new Biome[0];
	}

	// This method is a mess, and will most likely be a direct-copy from the original.
	// Turns out, getting vertices is difficult
	@Override
	public Set<Biome> getBiomesInSquare(int centerX, int centerZ, int sideLength)
	{
		return null;
	}

	// Copy and modify from OverworldBiomeProvider, or the original Biome Provider
	@Nullable
	@Override
	public BlockPos findBiomePosition(int i, int i1, int i2, List<Biome> list, Random random)
	{
		return null;
	}

	// Probably gets the structures to generate per Biome. I copied it over.
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

	// Unknown purpose.
	@Override
	public Set<BlockState> getSurfaceBlocks()
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
}
