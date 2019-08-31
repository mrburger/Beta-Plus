package com.mrburgerus.betaplus.world.beta_plus;

import com.mrburgerus.betaplus.util.BiomeReplaceUtil;
import com.mrburgerus.betaplus.util.ConfigRetroPlus;
import com.mrburgerus.betaplus.util.DeepenOceanUtil;
import com.mrburgerus.betaplus.world.beta_plus.gen.BetaGenCaves;
import com.mrburgerus.betaplus.world.noise.NoiseGeneratorOctavesBeta;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.CatSpawner;
import net.minecraft.world.spawner.PatrolSpawner;
import net.minecraft.world.spawner.PhantomSpawner;
import net.minecraft.world.spawner.WorldEntitySpawner;

import java.util.Locale;
import java.util.Random;


/* SEE CHUNKGENERATOROVERWORLD.CLASS FOR BASE */

public class ChunkGeneratorBetaPlus extends ChunkGenerator<BetaPlusGenSettings>
{
	// Fields
	private Random rand;
	private Biome[] biomesForGeneration;
	//Noise Generators
	private NoiseGeneratorOctavesBeta octaves1;
	private NoiseGeneratorOctavesBeta octaves2;
	private NoiseGeneratorOctavesBeta octaves3;
	private NoiseGeneratorOctavesBeta beachBlockNoise; // Formerly scaleNoise, used for Gravel and Sand, so probably beaches.
	private NoiseGeneratorOctavesBeta surfaceNoise; // Formerly octaves7
	private NoiseGeneratorOctavesBeta scaleNoise; // Formerly octaves6, renamed using ChunkGeneratorOverworld
	private NoiseGeneratorOctavesBeta octaves7;
	//Noise Arrays
	private double[] octaveArr1;
	private double[] octaveArr2;
	private double[] octaveArr3;
	private double[] octaveArr4;
	private double[] octaveArr5;
	private double[] heightNoise;
	private double[] sandNoise = new double[256];
	private double[] gravelNoise = new double[256];
	private double[] stoneNoise = new double[256];
	// New Fields
	private BiomeProviderBetaPlus biomeProviderS;
	private final BetaPlusGenSettings settings;
	public static final int CHUNK_SIZE = 16;
	// Spawners
	private final PhantomSpawner phantomSpawner = new PhantomSpawner();
	private final PatrolSpawner patrolSpawner = new PatrolSpawner();
	private final CatSpawner catSpawner = new CatSpawner();
	// Carver crap, still figuring out.
	BetaGenCaves caveGenerator = new BetaGenCaves();

	public ChunkGeneratorBetaPlus(IWorld world, BiomeProviderBetaPlus biomeProvider, BetaPlusGenSettings settingsIn)
	{
		// Simplified 0.5c
		super(world, biomeProvider, settingsIn);

		rand = new Random(seed);
		octaves1 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves2 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves3 = new NoiseGeneratorOctavesBeta(rand, 8);
		beachBlockNoise = new NoiseGeneratorOctavesBeta(rand, 4);
		surfaceNoise = new NoiseGeneratorOctavesBeta(rand, 4);
		scaleNoise = new NoiseGeneratorOctavesBeta(rand, 10);
		octaves7 = new NoiseGeneratorOctavesBeta(rand, 16);
		biomeProviderS = biomeProvider;
		this.settings = settingsIn;
	}

	// Could be more useful.
	// How about moving the Chunk block setting?
	@Override
	public void generateSurface(IChunk chunk)
	{
		// TESTING
		this.replaceBlocksForBiome(chunk.getPos().x, chunk.getPos().z, chunk, BiomeReplaceUtil.backConvertBiomeArray(chunk.getBiomes()));
	}

	// TESTING 0.5D
	@Override
	public void carve(IChunk chunkIn, GenerationStage.Carving carvingSettings)
	{
		if (!settings.isUseOldCaves())
		{
			super.carve(chunkIn, carvingSettings);
		}
		else
		{
			ChunkPos chunkPos = chunkIn.getPos();
			caveGenerator.generate(seed, chunkPos.x, chunkPos.z, chunkIn);
		}
	}

	/* Spawns Passive Mobs */
	@Override
	public void spawnMobs(WorldGenRegion region)
	{
		//sBetaPlus.LOGGER.info("Called Spawn");
		int lvt_2_1_ = region.getMainChunkX();
		int lvt_3_1_ = region.getMainChunkZ();
		Biome lvt_4_1_ = region.getChunk(lvt_2_1_, lvt_3_1_).getBiomes()[0];
		SharedSeedRandom lvt_5_1_ = new SharedSeedRandom();
		lvt_5_1_.setDecorationSeed(region.getSeed(), lvt_2_1_ << 4, lvt_3_1_ << 4);
		WorldEntitySpawner.performWorldGenSpawning(region, lvt_4_1_, lvt_2_1_, lvt_3_1_, lvt_5_1_);
	}

	@Override
	public BetaPlusGenSettings getSettings()
	{
		return this.settings;
	}

	@Override
	public int getGroundHeight()
	{
		return this.world.getSeaLevel();
	}

	/* Make Base Updated */
	@Override
	public void makeBase(IWorld iWorld, IChunk chunkIn)
	{
		// Get Position
		int x = chunkIn.getPos().x;
		int z = chunkIn.getPos().z;
		// Functions As setBaseChunkSeed(), but broken down.
		rand.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
		// Similar to ChunkGeneratorOverworld
		biomesForGeneration = biomeProviderS.getBiomes(x * 16, z * 16, 16, 16, false);
		// Written similarly to "generateTerrain" from earlier versions.
		setBlocksInChunk(chunkIn);
		// Scale factor formerly 2.85
		DeepenOceanUtil.deepenOcean(chunkIn, rand, settings.getSeaLevel(), settings.getOceanSmoothSize(), ConfigRetroPlus.oceanYScale);

		// Replace Blocks (DIRT & SAND & STUFF) SEE ABOVE
		//replaceBlocksForBiome(x, z, chunkIn, biomesForGeneration);

		// Set Biomes
		chunkIn.setBiomes(BiomeReplaceUtil.convertBiomeArray(biomesForGeneration));
	}

	// Used for Villages AND Pillager Outposts
	// TESTED: AUG 21, 2019
	// COULD BE MANIPULATED AND COMBINED
	@Override
	public int func_222529_a(int x, int z, Heightmap.Type unused)
	{
		int[][] valuesInChunk = biomeProviderS.simulator.simulateChunkYFull(new ChunkPos(new BlockPos(x, 0, z))).getFirst();
		// Working!
		int yRet = valuesInChunk[x & 0x000F][z & 0x000F];
		if (yRet < getSeaLevel())
		{
			yRet = getSeaLevel() + 1;
		}
		return yRet;
	}

	/* -- GENERATION METHODS -- */

	/* GENERATES THE BLOCKS */
	// PREVIOUSLY other methods, updated for 1.13!
	private void setBlocksInChunk(IChunk chunk)
	{
		heightNoise = octaveGenerator(heightNoise, chunk.getPos().x * 4, chunk.getPos().z * 4);
		for (int i = 0; i < 4; ++i)
		{
			for (int j = 0; j < 4; ++j)
			{
				for (int k = 0; k < 16; ++k)
				{
					double eigth = 0.125;
					double var16 = heightNoise[((i) * 5 + j) * 17 + k];
					double var18 = heightNoise[((i) * 5 + j + 1) * 17 + k];
					double var20 = heightNoise[((i + 1) * 5 + j) * 17 + k];
					double var22 = heightNoise[((i + 1) * 5 + j + 1) * 17 + k];
					double var24 = (heightNoise[((i) * 5 + j) * 17 + k + 1] - var16) * eigth;
					double var26 = (heightNoise[((i) * 5 + j + 1) * 17 + k + 1] - var18) * eigth;
					double var28 = (heightNoise[((i + 1) * 5 + j) * 17 + k + 1] - var20) * eigth;
					double var30 = (heightNoise[((i + 1) * 5 + j + 1) * 17 + k + 1] - var22) * eigth;
					for (int l = 0; l < 8; ++l)
					{
						double quarter = 0.25;
						double var35 = var16;
						double var37 = var18;
						double var39 = (var20 - var16) * quarter;
						double var41 = (var22 - var18) * quarter;
						for (int m = 0; m < 4; ++m)
						{
							int x = m + i * 4;
							int y = k * 8 + l;
							int z = j * 4;
							double var46 = 0.25;
							double var48 = var35;
							double var50 = (var37 - var35) * var46;
							for (int n = 0; n < 4; ++n)
							{
								Block block = null;
								if (y < settings.getSeaLevel())
								{
									block = Blocks.WATER;
								}
								if (var48 > 0.0)
								{
									block = Blocks.STONE;
								}
								if (block != null)
								{
									chunk.setBlockState(new BlockPos(x, y, z), block.getDefaultState(), false);
								}
								++z;
								var48 += var50;
							}
							var35 += var39;
							var37 += var41;
						}
						var16 += var24;
						var18 += var26;
						var20 += var28;
						var22 += var30;
					}

				}
			}
		}
	}

	// Causes Issues with BOP beaches.
	private void replaceBeaches(IChunk chunk)
	{
		for (int z = 0; z < CHUNK_SIZE; ++z)
		{

			for (int x = 0; x < CHUNK_SIZE; ++x)
			{
				int xPos = chunk.getPos().getXStart() + x;
				int zPos = chunk.getPos().getZStart() + z;
				int yVal = BiomeReplaceUtil.getSolidHeightY(new BlockPos(xPos, 0, zPos), chunk);
				// New Line
				Biome biome = biomesForGeneration[(x << 4 | z)];
				//Inject Beaches (MODIFIED)
				if ((yVal <= (settings.getSeaLevel() + 1) && yVal >= settings.getSeaLevel() - 1) &&
						(biome != Biomes.DESERT && biome != Biomes.DESERT_HILLS) &&
						chunk.getBlockState(new BlockPos(xPos, yVal, zPos)) == Blocks.SAND.getDefaultState() ||
						chunk.getBlockState(new BlockPos(xPos, yVal, zPos)) == Blocks.GRAVEL.getDefaultState()) // Added Gravel, beaches.
				{
						this.biomesForGeneration[(x << 4 | z)] = Biomes.BEACH; //biomeProviderS.getBeachBiome(new BlockPos(xPos, yVal, zPos));
				}
			}
		}
	}

	// Copied from Overworld
	@Override
	public void spawnMobs(ServerWorld worldIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs)
	{
		this.phantomSpawner.tick(worldIn, spawnHostileMobs, spawnPeacefulMobs);
		this.patrolSpawner.tick(worldIn, spawnHostileMobs, spawnPeacefulMobs);
		this.catSpawner.tick(worldIn, spawnHostileMobs, spawnPeacefulMobs);
	}

	/* Modified from AbstractChunkGenerator, provides /locate command values */
	@Override
	public BlockPos findNearestStructure(World worldIn, String name, BlockPos pos, int radius, boolean p_211403_5_)
	{
		Structure<?> structure = Feature.STRUCTURES.get(name.toLowerCase(Locale.ROOT));
		if (structure != null)
		{
			return structure.findNearest(worldIn, this, pos, radius, p_211403_5_);
		}
		return null;
	}

	/* 1.14, COPY TRIGALOO */
	private double[] octaveGenerator(double[] values, int xPos, int zPos)
	{
		int size1 = 5;
		int size2 = 17;
		int size3 = 5;
		if (values == null)
		{
			values = new double[size1 * size2 * size3];
		}
		double noiseFactor = ConfigRetroPlus.noiseScale;
		double[] temps = biomeProviderS.temperatures;
		double[] humidities = biomeProviderS.humidities;
		octaveArr4 = scaleNoise.generateNoiseOctaves(octaveArr4, xPos, zPos, size1, size3, 1.121, 1.121, 0.5);
		octaveArr5 = octaves7.generateNoiseOctaves(octaveArr5, xPos, zPos, size1, size3, 200.0, 200.0, 0.5);
		octaveArr1 = octaves3.generateNoiseOctaves(octaveArr1, xPos, 0, zPos, size1, size2, size3, noiseFactor / 80.0, noiseFactor / 160.0, noiseFactor / 80.0);
		octaveArr2 = octaves1.generateNoiseOctaves(octaveArr2, xPos, 0, zPos, size1, size2, size3, noiseFactor, noiseFactor, noiseFactor);
		octaveArr3 = octaves2.generateNoiseOctaves(octaveArr3, xPos, 0, zPos, size1, size2, size3, noiseFactor, noiseFactor, noiseFactor);
		int incrementer1 = 0;
		int incrementer2 = 0;
		int var16 = 16 / size1;
		for (int i = 0; i < size1; ++i)
		{
			int var18 = i * var16 + var16 / 2;
			for (int j = 0; j < size3; ++j)
			{
				double var29;
				int var20 = j * var16 + var16 / 2;
				double var21 = temps[var18 * 16 + var20];
				double var23 = humidities[var18 * 16 + var20] * var21;
				double var25 = 1.0 - var23;
				var25 *= var25;
				var25 *= var25;
				var25 = 1.0 - var25;
				double var27 = (octaveArr4[incrementer2] + 256.0) / 512.0;
				if ((var27 *= var25) > 1.0)
				{
					var27 = 1.0;
				}
				if ((var29 = octaveArr5[incrementer2] / 8000.0) < 0.0)
				{
					var29 = (-var29) * 0.3;
				}
				if ((var29 = var29 * 3.0 - 2.0) < 0.0)
				{
					if ((var29 /= 2.0) < -1.0)
					{
						var29 = -1.0;
					}
					var29 /= 1.4;
					var29 /= 2.0;
					var27 = 0.0;
				}
				else
				{
					if (var29 > 1.0)
					{
						var29 = 1.0;
					}
					var29 /= 8.0;
				}
				if (var27 < 0.0)
				{
					var27 = 0.0;
				}
				var27 += 0.5;
				var29 = var29 * (double) size2 / 16.0;
				double var31 = (double) size2 / 2.0 + var29 * 4.0;
				++incrementer2;
				for (int k = 0; k < size2; ++k)
				{
					double var34;
					double var36 = ((double) k - var31) * 12.0 / var27;
					if (var36 < 0.0)
					{
						var36 *= 4.0;
					}
					double var38 = octaveArr2[incrementer1] / 512.0;
					double var40 = octaveArr3[incrementer1] / 512.0;
					double var42 = (octaveArr1[incrementer1] / 10.0 + 1.0) / 2.0;
					var34 = var42 < 0.0 ? var38 : (var42 > 1.0 ? var40 : var38 + (var40 - var38) * var42);
					var34 -= var36;
					if (k > size2 - 4)
					{
						double var44 = (float) (k - (size2 - 4)) / 3.0f;
						var34 = var34 * (1.0 - var44) + -10.0 * var44;
					}
					values[incrementer1] = var34;
					++incrementer1;
				}
			}
		}
		return values;
	}

	/* YES, IT IS COPIED AND MODIFIED FROM 1.12 */
	private void replaceBlocksForBiome(int chunkX, int chunkZ, IChunk chunkprimer, Biome[] biomes)
	{
		double thirtySecond = 0.03125;
		this.sandNoise = this.beachBlockNoise.generateNoiseOctaves(this.sandNoise, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, thirtySecond, thirtySecond, 1.0);
		this.gravelNoise = this.beachBlockNoise.generateNoiseOctaves(this.gravelNoise, chunkX * 16, 109.0134, chunkZ * 16, 16, 1, 16, thirtySecond, 1.0, thirtySecond);
		this.stoneNoise = this.surfaceNoise.generateNoiseOctaves(this.stoneNoise, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, thirtySecond * 2.0, thirtySecond * 2.0, thirtySecond * 2.0);
		for (int z = 0; z < 16; ++z)
		{
			for (int x = 0; x < 16; ++x)
			{
				Biome biome = biomes[z + x * 16];
				boolean sandN = this.sandNoise[z + x * 16] + this.rand.nextDouble() * 0.2 > 0.0;
				boolean gravelN = this.gravelNoise[z + x * 16] + this.rand.nextDouble() * 0.2 > 3.0;
				int stoneN = (int) (this.stoneNoise[z + x * 16] / 3.0 + 3.0 + this.rand.nextDouble() * 0.25);
				int checkVal = -1;
				// Changed to use the actual Biome Config.
				BlockState topBlock = biome.getSurfaceBuilderConfig().getTop();
				BlockState fillerBlock = biome.getSurfaceBuilderConfig().getUnder();

				// GO from Top to bottom of world
				for (int y = 127; y >= 0; --y)
				{
					if (y <= this.rand.nextInt(5))
					{
						chunkprimer.setBlockState(new BlockPos(x, y, z), Blocks.BEDROCK.getDefaultState(), false);
					}
					else
					{
						Block block = chunkprimer.getBlockState(new BlockPos(x, y, z)).getBlock();

						if (block == Blocks.AIR)
						{
							checkVal = -1;
							continue;
						}

						//Checks if model already changed
						if (block != Blocks.STONE) continue;

						if (checkVal == -1)
						{
							if (stoneN <= 0)
							{
								topBlock = Blocks.AIR.getDefaultState();
								fillerBlock = Blocks.STONE.getDefaultState();
							}
							else if (y >= settings.getSeaLevel() - 4 && y <= settings.getSeaLevel() + 1)
							{
								topBlock = biome.getSurfaceBuilderConfig().getTop();
								fillerBlock = biome.getSurfaceBuilderConfig().getUnder();
								if (gravelN)
								{
									topBlock = Blocks.AIR.getDefaultState();
									fillerBlock = Blocks.GRAVEL.getDefaultState();
								}
								if (sandN)
								{
									topBlock = Blocks.SAND.getDefaultState();
									fillerBlock = Blocks.SAND.getDefaultState();
								}
							}
							if (y < settings.getSeaLevel() && topBlock == Blocks.AIR.getDefaultState())
							{
								topBlock = Blocks.WATER.getDefaultState();
							}

							// Sets top & filler Blocks
							checkVal = stoneN;
							// Test this still.
							if (y >= settings.getSeaLevel() -1)
							{
								chunkprimer.setBlockState(new BlockPos(x, y, z), topBlock, false);
							}
							else
							{
								chunkprimer.setBlockState(new BlockPos(x, y, z), fillerBlock, false);
							}
						}
						// Add Sandstone (NOT WORKING)
						else if (checkVal > 0)
						{
							--checkVal;
							chunkprimer.setBlockState(new BlockPos(x, y, z), fillerBlock, false);
							//Possibly state comparison fucked it
							if (checkVal == 0 && fillerBlock == Blocks.SAND.getDefaultState())
							{
								checkVal = this.rand.nextInt(4);
								fillerBlock = Blocks.SANDSTONE.getDefaultState();
							}
						} //END OF Y LOOP
					}
				}
			}
		}
	}
}
