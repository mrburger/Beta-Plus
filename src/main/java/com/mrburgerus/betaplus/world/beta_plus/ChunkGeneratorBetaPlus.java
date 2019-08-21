package com.mrburgerus.betaplus.world.beta_plus;

import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.util.BiomeReplaceUtil;
import com.mrburgerus.betaplus.util.ConfigBetaPlus;
import com.mrburgerus.betaplus.util.DeepenOceanUtil;
import com.mrburgerus.betaplus.world.biome.EnumBetaPlusBiome;
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
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.spawner.PhantomSpawner;
import net.minecraft.world.spawner.WorldEntitySpawner;

import java.util.Locale;
import java.util.Random;


/* SEE CHUNKGENERATOROVERWORLD.CLASS FOR BASE */

public class ChunkGeneratorBetaPlus extends NoiseChunkGenerator<BetaPlusGenSettings>
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
	private final PhantomSpawner phantomSpawner = new PhantomSpawner();
	private final BetaPlusGenSettings settings;
	public static final int CHUNK_SIZE = 16;

	public ChunkGeneratorBetaPlus(IWorld world, BiomeProviderBetaPlus biomeProvider, BetaPlusGenSettings settingsIn)
	{
		// Modified from Overworld & End Chunk Generator
		super(world, biomeProvider, 4,8, 256, settingsIn, true);
		this.settings = settingsIn;

		rand = new Random(seed);
		octaves1 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves2 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves3 = new NoiseGeneratorOctavesBeta(rand, 8);
		beachBlockNoise = new NoiseGeneratorOctavesBeta(rand, 4);
		surfaceNoise = new NoiseGeneratorOctavesBeta(rand, 4);
		scaleNoise = new NoiseGeneratorOctavesBeta(rand, 10);
		octaves7 = new NoiseGeneratorOctavesBeta(rand, 16);
		biomeProviderS = biomeProvider;
	}

	/* Modified From Abstract to support the fact BiomeProvider cannot detect oceans */
	/* Ocean injection is partially my fault. */
	@Override
	public void decorate(WorldGenRegion region)
	{
		super.decorate(region);
		/*
		BlockFalling.fallInstantly = true;
		int chunkX = region.getMainChunkX();
		int chunkZ = region.getMainChunkZ();
		int minX = chunkX * 16;
		int minZ = chunkZ * 16;
		BlockPos blockpos = new BlockPos(minX, 0, minZ);
		Biome biome = region.getChunk(chunkX + 1, chunkZ + 1).getBiomes()[0];
		//Biome biome = region.getBiome(new BlockPos(k + 8, 0, l + 8));
				//region.getChunk(i, j).getBiomes()[0];
		SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
		long seedRegion = sharedseedrandom.setDecorationSeed(region.getSeed(), minX, minZ);

		for(GenerationStage.Decoration decoration : GenerationStage.Decoration.values())
		{
			// I PUT WORLD INSTEAD OF REGION
			biome.decorate(decoration, this, region, seedRegion, sharedseedrandom, blockpos);
		}

		BlockFalling.fallInstantly = false;
		*/

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
		//BetaPlus.LOGGER.info("GEN: " + chunkIn.getPos().toString());
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
		DeepenOceanUtil.deepenOcean(chunkIn, rand, settings.getSeaLevel(), settings.getOceanSmoothSize(), ConfigBetaPlus.oceanYScale);
		// Replace Biomes (Oceans)
		// This is because detection of Oceans is an average operation.
		//this.replaceBiomes(chunkIn);

		// Replace Blocks (DIRT & SAND & STUFF)
		replaceBlocksForBiome(x, z, chunkIn, EnumBetaPlusBiome.convertBiomeTable(biomesForGeneration));
		// Replace Beaches, done afterwards.
		this.replaceBeaches(chunkIn);

		// Set Biomes
		chunkIn.setBiomes(BiomeReplaceUtil.convertBiomeArray(biomesForGeneration));
		//BetaPlus.LOGGER.info("DONE: " + chunkIn.getPos().toString());
	}

	@Override
	protected double[] func_222549_a(int i, int i1)
	{
		return new double[0];
	}

	@Override
	protected double func_222545_a(double v, double v1, int i)
	{
		return 0;
	}

	// Used for Villages AND Pillager Outposts

	// INCOMPLETE
	// TODO: ADD MAX-MIN TO DETERMINE HEIGHT OF BIOME
	@Override
	public int func_222529_a(int x, int z, Heightmap.Type p_222529_3_)
	{
		int[][] valuesInChunk = biomeProviderS.simulator.simulateChunkYFull(new ChunkPos(new BlockPos(x, 0, z))).getFirst();
		//BetaPlus.LOGGER.info("Called height: " + x  + ", " +  z);
		//BetaPlus.LOGGER.info("HERE: " + Math.floorMod(x, CHUNK_SIZE) + ", " + x % CHUNK_SIZE + "; " + Math.floorMod(z, CHUNK_SIZE) + ", " + z % CHUNK_SIZE );
		//return valuesInChunk[Math.floorMod(x, CHUNK_SIZE)][Math.floorMod(z, CHUNK_SIZE)];

		int yRet = valuesInChunk[x % CHUNK_SIZE][z % CHUNK_SIZE];
		if (yRet < getSeaLevel())
		{
			yRet = getSeaLevel() + 1;
		}

		// Is it Z, X? Yes? Maybe?
		// Added +1 to increase height slightly
		return yRet;
		// Seems to have issues
		//return valuesInChunk[Math.floorMod(x, CHUNK_SIZE)][Math.floorMod(z, CHUNK_SIZE)] + 1 > getSeaLevel() ? valuesInChunk[Math.floorMod(z, CHUNK_SIZE)][Math.floorMod(x, CHUNK_SIZE)] + 1 : getSeaLevel() + 1;

	}

	@Override
	protected void func_222548_a(double[] doubles, int i, int i1)
	{

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

	//Replace Biomes where necessary
	/*
	private void replaceBiomes(IChunk iChunk)
	{
		for (int z = 0; z < CHUNK_SIZE; ++z)
		{
			for (int x = 0; x < CHUNK_SIZE; ++x)
			{
				int xPos = iChunk.getPos().getXStart() + x;
				int zPos = iChunk.getPos().getZStart() + z;
				int yVal = BiomeReplaceUtil.getSolidHeightY(new BlockPos(xPos, 0, zPos), iChunk);
				if (yVal > settings.getHighAltitude())
				{
					biomesForGeneration[(x << 4 | z)] = EnumBetaPlusBiome.mountain.handle;
				}
				else if (yVal < settings.getSeaLevel() - 1)
				{

					if (yVal < settings.getSeaLevel() - settings.getSeaDepth())
					{
						biomesForGeneration[(x << 4 | z)] = biomeProviderS.getOceanBiome(new BlockPos(xPos, yVal, zPos), true);
					}
					else
					{
						biomesForGeneration[(x << 4 | z)] = biomeProviderS.getOceanBiome(new BlockPos(xPos, yVal, zPos), false);
					}
				}
			}
		}
	}
	*/

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
				if ((yVal <= (settings.getSeaLevel() + 1) && yVal >= settings.getSeaLevel() - 1) && (biome != EnumBetaPlusBiome.desert.handle) && chunk.getBlockState(new BlockPos(xPos, yVal, zPos)) == Blocks.SAND.getDefaultState())
				{
						this.biomesForGeneration[(x << 4 | z)] = Biomes.BEACH; //biomeProviderS.getBeachBiome(new BlockPos(xPos, yVal, zPos));
				}
			}
		}
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
		int var5 = 5;
		int var6 = 17;
		int var7 = 5;
		if (values == null)
		{
			values = new double[var5 * var6 * var7];
		}
		double noiseFactor = ConfigBetaPlus.noiseScale;
		double[] temps = biomeProviderS.temperatures;
		double[] humidities = biomeProviderS.humidities;
		octaveArr4 = scaleNoise.generateNoiseOctaves(octaveArr4, xPos, zPos, var5, var7, 1.121, 1.121, 0.5);
		octaveArr5 = octaves7.generateNoiseOctaves(octaveArr5, xPos, zPos, var5, var7, 200.0, 200.0, 0.5);
		octaveArr1 = octaves3.generateNoiseOctaves(octaveArr1, xPos, 0, zPos, var5, var6, var7, noiseFactor / 80.0, noiseFactor / 160.0, noiseFactor / 80.0);
		octaveArr2 = octaves1.generateNoiseOctaves(octaveArr2, xPos, 0, zPos, var5, var6, var7, noiseFactor, noiseFactor, noiseFactor);
		octaveArr3 = octaves2.generateNoiseOctaves(octaveArr3, xPos, 0, zPos, var5, var6, var7, noiseFactor, noiseFactor, noiseFactor);
		int incrementer1 = 0;
		int incrementer2 = 0;
		int var16 = 16 / var5;
		for (int i = 0; i < var5; ++i)
		{
			int var18 = i * var16 + var16 / 2;
			for (int j = 0; j < var7; ++j)
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
				var29 = var29 * (double) var6 / 16.0;
				double var31 = (double) var6 / 2.0 + var29 * 4.0;
				++incrementer2;
				for (int k = 0; k < var6; ++k)
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
					if (k > var6 - 4)
					{
						double var44 = (float) (k - (var6 - 4)) / 3.0f;
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
	private void replaceBlocksForBiome(int chunkX, int chunkZ, IChunk chunkprimer, EnumBetaPlusBiome[] biomes)
	{
		double thirtySecond = 0.03125;
		this.sandNoise = this.beachBlockNoise.generateNoiseOctaves(this.sandNoise, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, thirtySecond, thirtySecond, 1.0);
		this.gravelNoise = this.beachBlockNoise.generateNoiseOctaves(this.gravelNoise, chunkX * 16, 109.0134, chunkZ * 16, 16, 1, 16, thirtySecond, 1.0, thirtySecond);
		this.stoneNoise = this.surfaceNoise.generateNoiseOctaves(this.stoneNoise, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, thirtySecond * 2.0, thirtySecond * 2.0, thirtySecond * 2.0);
		for (int z = 0; z < 16; ++z)
		{
			for (int x = 0; x < 16; ++x)
			{
				EnumBetaPlusBiome biome = biomes[z + x * 16];
				boolean sandN = this.sandNoise[z + x * 16] + this.rand.nextDouble() * 0.2 > 0.0;
				boolean gravelN = this.gravelNoise[z + x * 16] + this.rand.nextDouble() * 0.2 > 3.0;
				int stoneN = (int) (this.stoneNoise[z + x * 16] / 3.0 + 3.0 + this.rand.nextDouble() * 0.25);
				int checkVal = -1;
				BlockState topBlock = biome.topBlock.getDefaultState();
				BlockState fillerBlock = biome.fillerBlock.getDefaultState();

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
								topBlock = biome.topBlock.getDefaultState();
								fillerBlock = biome.fillerBlock.getDefaultState();
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
