package com.mrburgerus.betaplus.world.alpha_plus;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.util.ConfigRetroPlus;
import com.mrburgerus.betaplus.world.alpha_plus.sim.AlphaPlusSimulator;
import com.mrburgerus.betaplus.world.biome.TerrainType;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.feature.structure.Structure;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.mrburgerus.betaplus.world.beta_plus.ChunkGeneratorBetaPlus.CHUNK_SIZE;


// TODO: UPDATE WITH TERRAINTYPE DECLARATIONS
public class BiomeProviderAlphaPlus extends BiomeProvider
{
	private Biome landBiome;
	private Biome oceanBiome;
	private Biome hillBiome;
	private Biome deepOceanBiome;
	/* Had to create custom biomes */
	/* Custom Biomes created so that ICE sheets Spawn on Oceans in snowy worlds */
	private static final Biome ALPHA_FROZEN_BIOME = Biomes.SNOWY_TUNDRA;
	public static final Biome ALPHA_FROZEN_OCEAN = Biomes.FROZEN_OCEAN;
	private static final Biome ALPHA_BIOME = Biomes.PLAINS;
	public static final Biome ALPHA_OCEAN = Biomes.OCEAN;
	public static final Biome ALPHA_DEEP_OCEAN = Biomes.DEEP_OCEAN;
	private static final Biome[] BIOMES_LIST = new Biome[]{ALPHA_FROZEN_BIOME, ALPHA_FROZEN_OCEAN, ALPHA_BIOME, ALPHA_OCEAN, ALPHA_DEEP_OCEAN};
	// Simulator for Y-heights
	public final AlphaPlusSimulator simulator;

	public BiomeProviderAlphaPlus(World world)
	{
		if (world.getWorldInfo().getGeneratorOptions().getBoolean(WorldTypeAlphaPlus.SNOW_WORLD_TAG))
		{
			BetaPlus.LOGGER.info("Using Frozen");
			this.landBiome = ALPHA_FROZEN_BIOME;
			this.oceanBiome = ALPHA_FROZEN_OCEAN;
			this.hillBiome = Biomes.SNOWY_MOUNTAINS; // TODO
			this.deepOceanBiome = Biomes.DEEP_FROZEN_OCEAN;
		}
		else
		{
			this.landBiome = ALPHA_BIOME;
			this.oceanBiome = ALPHA_OCEAN;
			this.hillBiome = Biomes.FOREST;
			this.deepOceanBiome = ALPHA_DEEP_OCEAN;
		}
		simulator = new AlphaPlusSimulator(world);
	}

	/* Just Like Beta Plus, generates a single landBiome */
	private Biome[] generateBiomes(int startX, int startZ, int xSize, int zSize, boolean useAverage)
	{
		Biome[] biomeArr = new Biome[xSize * zSize];
		int counter = 0;
		Biome selected;
		// First, get initial terrain
		Pair<BlockPos, TerrainType>[][] pairArr = this.getInitialTerrain(startX, startZ, xSize, zSize);
		for (int x = 0; x < xSize; x++)
		{
			for (int z = 0; z < zSize; z++)
			{
				// Begin New
				BlockPos pos = new BlockPos(x + startX, 0 ,z + startZ);

				Pair pPos = pairArr[x][z];
				selected = this.landBiome;
				switch ((TerrainType) pPos.getSecond())
				{
					case land:
						selected = this.landBiome;
						break;
					case hillyLand:
						selected = this.hillBiome; // PLACEHOLDER
						break;
					case sea:
						selected = this.oceanBiome;
						break;
					case deepSea:
						selected = this.deepOceanBiome;
						break;
					case island:
						selected = Biomes.MUSHROOM_FIELDS;
						break;
					case generic:
						selected = this.landBiome;
						break;
					default:
						selected = Biomes.DEFAULT;
						break;
				}

				// Catch Averages
				if (useAverage)
				{
					Pair<Integer, Boolean> avg = simulator.simulateYAvg(pos);

					// OCEAN MONUMENT CATCHER
					if (avg.getFirst() < ConfigRetroPlus.seaLevel)
					{
						if (avg.getFirst() < MathHelper.floor(ConfigRetroPlus.seaLevel - (ConfigRetroPlus.seaDepth / ConfigRetroPlus.oceanYScale)))
						{
							// Overwrite.
							biomeArr[counter] = this.deepOceanBiome;
						}
						else
						{
							biomeArr[counter] = this.oceanBiome;
						}
					}
				}

				biomeArr[counter] = selected;
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
		return this.generateBiomes(i, i1, 1, 1, false)[0];
	}

	@Override
	public Biome[] getBiomes(int x, int z, int width, int length, boolean b)
	{
		return generateBiomes(x, z, width, length, true);
	}

	/* Used By Ocean Monuments and Mansions */
	@Override
	public Set<Biome> getBiomesInSquare(int centerX, int centerZ, int sideLength)
	{
		Set<Biome> set = Sets.newHashSet();
		Collections.addAll(set, this.generateBiomes(centerX, centerZ, sideLength, sideLength, true));
		return set;
	}

	@Override
	public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomeList, Random random)
	{
		int i = x - range >> 2;
		int j = z - range >> 2;
		int k = x + range >> 2;
		int l = z + range >> 2;
		int xSize = k - i + 1;
		int zSize = l - j + 1;
		Biome[] biomeArr = this.generateBiomes(i, j, xSize, zSize, false);

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

	// USER DEFINED NEW //
	public Pair<BlockPos, TerrainType>[][] getInitialTerrain(int startX, int startZ, int xSize, int zSize)
	{
		// There will be issues detecting large islands. I may run into chunk runaway issues if I don't recheck my running block tally.
		// Also, I may have to expand search area on the fly to accomodate. Probably not, hopefully.
		// Possibly a BlockPos, TerrainType Pair would be good?


		// Get chunk positions necessary, Added Ceiling to round up. Must also be a double.
		int xChunkSize = MathHelper.ceil(xSize / (CHUNK_SIZE * 1.0D));
		int zChunkSize = MathHelper.ceil(zSize / (CHUNK_SIZE * 1.0D));
		Pair<BlockPos, TerrainType>[][] terrainPairs = new Pair[xChunkSize * CHUNK_SIZE][zChunkSize * CHUNK_SIZE];

		for (int xChunk = 0; xChunk < xChunkSize; xChunk++)
		{
			for (int zChunk = 0; zChunk < zChunkSize; zChunk++)
			{
				// Looks to be incorrect.
				//ChunkPos chunkPos = new ChunkPos(startX + (xChunk * CHUNK_SIZE), startZ + (zChunk * CHUNK_SIZE));
				ChunkPos chunkPos = new ChunkPos(new BlockPos(startX + (xChunk * CHUNK_SIZE), 0, startZ + (zChunk * CHUNK_SIZE)));

				// Get simulated chunk
				int[][] yVals = simulator.simulateChunkYFull(chunkPos).getFirst();

				// Enter into initial Terrain list
				for (int x = 0; x < CHUNK_SIZE; x++)
				{
					for (int z = 0; z < CHUNK_SIZE; z++)
					{
						// Block Position in world
						BlockPos pos = new BlockPos(x + chunkPos.getXStart(), 0 ,z + chunkPos.getZStart());
						// TODO: GET TYPE OF TERRAIN MORE EFFECTIVELY
						terrainPairs[x + xChunk * CHUNK_SIZE][z + zChunk * CHUNK_SIZE] = Pair.of(pos, TerrainType.getTerrainNoIsland(yVals, x, z));
					}
				}
			}
		}
		// Now, find isolated "Land" or "Hilly" spots and declare as islands
		return terrainPairs;
	}
}
