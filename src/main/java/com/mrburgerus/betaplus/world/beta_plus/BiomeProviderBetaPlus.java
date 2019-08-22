package com.mrburgerus.betaplus.world.beta_plus;

//import biomesoplenty.api.biome.BOPBiomes;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mrburgerus.betaplus.world.beta_plus.sim.BetaPlusSimulator;
import com.mrburgerus.betaplus.world.biome.BetaPlusBiomeSelectorNew;
import com.mrburgerus.betaplus.world.biome.TerrainType;
import com.mrburgerus.betaplus.world.noise.NoiseGeneratorOctavesBiome;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
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

// Get the Chunk Size
import static com.mrburgerus.betaplus.world.beta_plus.ChunkGeneratorBetaPlus.CHUNK_SIZE;

/* This is a WIP class that will provide a NEW biome system. */
/* Based off BiomeProviderBetaPlus */
public class BiomeProviderBetaPlus extends BiomeProvider
{
	// FIELDS //
	// 8 lists, of Hot, Warm, Cool, Cold + Wet, Humid, Arid, Dry Biomes

	// Biome List, possibly a good injection point for Biomes O' Plenty stuff.
	private static final Biome[] BIOMES_LIST = buildBiomesList();
	// Biome Layer? New for 1.14, testing now.
	//private final Layer biomeLayer; // Could end up disabled.
	// The simulator for Y-heights.
	public final BetaPlusSimulator simulator;
	private final BetaPlusGenSettings settings;

	// Required for legacy operations
	private NoiseGeneratorOctavesBiome temperatureOctave;
	private NoiseGeneratorOctavesBiome humidityOctave;
	private NoiseGeneratorOctavesBiome noiseOctave;
	public double[] temperatures;
	public double[] humidities;
	public double[] noise;
	private final double scaleVal;
	private final double mult;

	// TODO: ADD A BIOME CACHE SO I CAN GENERATE JUST ONCE


	// CONSTRUCTORS //
	public BiomeProviderBetaPlus(World world, BetaPlusGenSettings settingsIn)
	{
		settings = settingsIn;
		mult = settingsIn.getMultiplierBiome();
		scaleVal = settingsIn.getScale();
		temperatureOctave = new NoiseGeneratorOctavesBiome(new Random(world.getSeed() * 9871), 4);
		humidityOctave = new NoiseGeneratorOctavesBiome(new Random(world.getSeed() * 39811), 4);
		noiseOctave = new NoiseGeneratorOctavesBiome(new Random(world.getSeed() * 543321), 2);
		// Declare immediately after other stuff
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
		return new Biome[]{Biomes.PLAINS, Biomes.DESERT, Biomes.DARK_FOREST, Biomes.DEEP_COLD_OCEAN};
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
		//return getBiomes(x, z, 1, 1, false)[0];
		return this.generateBiomes(x, z, 1, 1, false)[0];
	}

	// Gets a biome array of xSize * zSize
	// cacheFlag is not implemented yet.
	@Override
	public Biome[] getBiomes(int x, int z, int xSize, int zSize, boolean cacheFlag)
	{
			return this.generateBiomes(x, z, xSize, zSize, true);
	}

	// This method is a mess, and will most likely be a direct-copy from the original.
	// Turns out, getting vertices is difficult
	@Override
	public Set<Biome> getBiomesInSquare(int centerX, int centerZ, int sideLength)
	{
		int startX = centerX - sideLength >> 2;
		int startZ = centerZ - sideLength >> 2;
		int endX = centerX + sideLength >> 2;
		int endZ = centerZ + sideLength >> 2;
		Set<Biome> set = Sets.newHashSet();
		Collections.addAll(set, this.generateBiomes(centerX, centerZ, sideLength, sideLength, true));
		return set;
	}

	// Copy and modify from OverworldBiomeProvider, or the original Biome Provider
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
		// TODO: FIX
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

	private Biome[] generateBiomes(int startX, int startZ, int xSize, int zSize, final boolean useAverage)
	{
		// Required for legacy operations
		temperatures = temperatureOctave.generateOctaves(temperatures, (double) startX, (double) startZ, xSize, xSize, scaleVal, scaleVal, 0.25);
		humidities = humidityOctave.generateOctaves(humidities, (double) startX, (double) startZ, xSize, xSize, scaleVal * mult, scaleVal * mult, 0.3333333333333333);
		noise = noiseOctave.generateOctaves(noise, (double) startX, (double) startZ, xSize, xSize, 0.25, 0.25, 0.5882352941176471);

		Biome[] biomeArr = new Biome[xSize * zSize];
		int counter = 0;
		Biome selected;
		// First, get initial terrain
		Pair<BlockPos, TerrainType>[][] pairArr = this.getInitialTerrain(startX, startZ, xSize, zSize);

		// Process
		for (int x = 0; x < xSize; x++)
		{
			for (int z = 0; z < zSize; z++)
			{
				// Testing, this could be required.
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

				// Begin New
				BlockPos pos = new BlockPos(x + startX, 0 ,z + startZ);

				Pair pPos = pairArr[x][z];
				// Debug
				if (!pos.equals(pPos.getFirst()))
				{
					//BetaPlus.LOGGER.error("M: " + pos + " : " + pPos.getFirst());
				}
				// TODO: ADD SELECTOR FOR BIOME FROM LIST
				// TODO: REMOVE MATH.RANDOM()
				selected = BetaPlusBiomeSelectorNew.getBiome((float) temperatureVal, (float) humidityVal, Math.random(), (TerrainType) pPos.getSecond());
				/*
				switch ((TerrainType) pPos.getSecond())
				{
					case land:
						selected = Biomes.PLAINS;
						break;
					case hillyLand:
						selected = BetaPlusBiomeSelectorNew.
						break;
					case sea:
						selected = Biomes.WARM_OCEAN;
						break;
					case deepSea:
						selected = Biomes.DEEP_COLD_OCEAN;
						break;
					case island:
						selected = Biomes.MUSHROOM_FIELDS;
						break;
					case generic:
						selected = Biomes.DESERT;
						break;
					default:
						selected = Biomes.DEFAULT;
						break;
				}
				*/

				biomeArr[counter] = selected;
				counter++;
			}
		}
		return biomeArr;
	}


}
