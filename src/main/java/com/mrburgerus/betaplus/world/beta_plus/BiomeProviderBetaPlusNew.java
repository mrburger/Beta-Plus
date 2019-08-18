package com.mrburgerus.betaplus.world.beta_plus;

//import biomesoplenty.api.biome.BOPBiomes;
import com.mojang.datafixers.util.Pair;
import com.mrburgerus.betaplus.world.beta_plus.sim.BetaPlusSimulator;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.feature.structure.Structure;

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
	// Biome Layer? New for 1.14, testing now.
	//private final Layer biomeLayer; // Could end up disabled.
	// The simulator for Y-heights.
	public final BetaPlusSimulator simulator;


	// CONSTRUCTORS //
	public BiomeProviderBetaPlusNew(World world, BetaPlusGenSettings settingsIn)
	{
		// Declare first
		simulator = new BetaPlusSimulator(world);
		//biomeLayer = BetaPlusLayerUtil.buildOverworldBiomeLayer(world, simulator, settingsIn);

	}


	// METHODS //

	// Builds the possible biomes list.
	// Note: DOES NOT CURRENTLY HAVE PURPOSE FOR BIOME SELECTION
	private static Biome[] buildBiomesList()
	{
		// Possibly a for-each would be better.
		// Initialize all enabled biomes.
		// Check for Biomes o' Plenty, and if so, get the enabled biome list.
		// Determines which structures are allowed
		return new Biome[0];
	}



	// OVERRIDES //


	@Override
	public List<Biome> getBiomesToSpawnIn()
	{
		return super.getBiomesToSpawnIn();
	}

	// Gets Biome for a specific coordinate
	// X, Z are block coordinates
	@Override
	public Biome getBiome(int x, int z)
	{
		// Get for a specific X and Z. This is typically used for structures, such as Buried Treasure
		return getBiomes(x, z, 1, 1, false)[0];
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


	// USER DEFINED //

	// Since Layers will most likely not work in any capacity, I will fall back on the land simulator I developed.
	//TerrainType[][] terrain = new TerrainType[xSize][zSize];
	public Pair[][] getInitialTerrain(int startX, int startZ, int xSize, int zSize)
	{
		// There will be issues detecting large islands. I may run into chunk runaway issues if I don't recheck my running block tally.
		// Also, I may have to expand search area on the fly to accomodate. Probably not, hopefully.
		// Possibly a BlockPos, TerrainType Pair would be good?

		Pair[][] terrainPairs = new Pair[xSize][zSize];
		// Get chunk positions necessary.
		int xChunkSize = xSize / ChunkGeneratorBetaPlus.CHUNK_SIZE;
		int zChunkSize = zSize / ChunkGeneratorBetaPlus.CHUNK_SIZE;

		for (int xChunk = 0; xChunk < xChunkSize; xChunk++)
		{
			for (int zChunk = 0; zChunk < zChunkSize; zChunk++)
			{
				ChunkPos chunkPos = new ChunkPos(startX + (xChunk * ChunkGeneratorBetaPlus.CHUNK_SIZE), startZ + (zChunk * ChunkGeneratorBetaPlus.CHUNK_SIZE));
				// Get simulated chunk
				int[][] yVals = simulator.simulateChunkYFull(chunkPos).getFirst();

				// Enter into initial Terrain list
				for (int x = 0; x < ChunkGeneratorBetaPlus.CHUNK_SIZE; x++)
				{
					for (int z = 0; z < ChunkGeneratorBetaPlus.CHUNK_SIZE; z++)
					{
						// Block Position in world
						BlockPos pos = new BlockPos(x + chunkPos.getXStart(), 0 ,z + chunkPos.getZStart());
						// TODO: GET TYPE OF TERRAIN
						terrainPairs[x][z] = Pair.of(pos, TerrainType.generic);
					}
				}
			}
		}
		return terrainPairs;
	}
}
