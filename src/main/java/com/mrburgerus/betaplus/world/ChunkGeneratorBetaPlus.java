package com.mrburgerus.betaplus.world;

import com.mrburgerus.betaplus.world.biome.BiomeGenBetaPlus;
import com.mrburgerus.betaplus.world.biome.BiomeProviderBetaPlus;
import com.mrburgerus.betaplus.world.noise.NoiseGeneratorOctavesBeta;
import com.mrburgerus.betaplus.world.noise.NoiseGeneratorPerlinBeta;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.PhantomSpawner;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.AbstractChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.SwampHutStructure;

import java.util.List;
import java.util.Random;


/* SEE CHUNKGENERATOROVERWORLD.CLASS FOR BASE */

public class ChunkGeneratorBetaPlus extends AbstractChunkGenerator
{
	// Fields
	private Random rand;
	public static int seaLevel = 63; // Assign to 64 initially, can be overriden.
	private int seaDepth = 12;
	private int altitude = 108;
	private Biome[] biomesForGeneration;
	//Noise Generators
	private NoiseGeneratorOctavesBeta octaves1;
	private NoiseGeneratorOctavesBeta octaves2;
	private NoiseGeneratorOctavesBeta octaves3;
	private NoiseGeneratorOctavesBeta octaves4;
	private NoiseGeneratorOctavesBeta octaves5;
	private NoiseGeneratorOctavesBeta octaves6;
	private NoiseGeneratorOctavesBeta octaves7;
	//Perlin
	private NoiseGeneratorPerlinBeta surfaceNoise;
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

	public ChunkGeneratorBetaPlus(IWorld world, BiomeProviderBetaPlus biomeProvider)
	{
		super(world, biomeProvider);
		seaLevel = world.getSeaLevel();

		rand = new Random(seed);
		octaves1 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves2 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves3 = new NoiseGeneratorOctavesBeta(rand, 8);
		octaves4 = new NoiseGeneratorOctavesBeta(rand, 4);
		octaves5 = new NoiseGeneratorOctavesBeta(rand, 4);
		octaves6 = new NoiseGeneratorOctavesBeta(rand, 10);
		octaves7 = new NoiseGeneratorOctavesBeta(rand, 16);
		surfaceNoise = new NoiseGeneratorPerlinBeta(this.rand);
		biomeProviderS = biomeProvider;
	}

	/* This Method is an Analog to generateChunk, albeit HEAVILY modified! */
	@Override
	public void makeBase(IChunk chunkIn)
	{
		// Get Position
		int x = chunkIn.getPos().x;
		int z = chunkIn.getPos().z;
		// Functions As setBaseChunkSee(), but broken down.
		rand.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
		// Similar to ChunkGeneratorOverworld
		biomesForGeneration = biomeProviderS.getBiomes(x * 16, z * 16, 16, 16);
		// Written similarly to "generateTerrain" from earlier versions.
		setBlocksInChunk(x, z, chunkIn);
		BetaPlusDeepenOcean.deepenOcean(chunkIn);
		// Replace Biomes
		this.replaceBiomes(chunkIn);

		// Replace Blocks (DIRT & SAND & STUFF)
		replaceBlocksForBiome(x, z, chunkIn, BiomeGenBetaPlus.convertBiomeTable(biomesForGeneration));
		// Replace Beaches, done afterwards.
		this.replaceBeaches(chunkIn);

		// Set Biomes
		chunkIn.setBiomes(convertBiomeArray(biomesForGeneration));

		chunkIn.setStatus(ChunkStatus.BASE);
	}

	@Override
	public void decorate(WorldGenRegion worldGenRegion)
	{
		BlockFalling.fallInstantly = true;
		int i = worldGenRegion.getMainChunkX();
		int j = worldGenRegion.getMainChunkZ();
		int k = i * 16;
		int l = j * 16;
		BlockPos blockpos = new BlockPos(k, 0, l);
		Biome biome = worldGenRegion.getChunk(i + 1, j + 1).getBiomes()[0];
		SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
		long i1 = sharedseedrandom.setDecorationSeed(worldGenRegion.getSeed(), k, l);
		GenerationStage.Decoration[] var11 = GenerationStage.Decoration.values();
		int var12 = var11.length;

		for(int var13 = 0; var13 < var12; ++var13) {
			GenerationStage.Decoration decoration = var11[var13];
			biome.decorate(decoration, this, worldGenRegion, i1, sharedseedrandom, blockpos);
		}

		BlockFalling.fallInstantly = false;
	}

	@Override
	public void spawnMobs(WorldGenRegion region)
	{
		int i = region.getMainChunkX();
		int j = region.getMainChunkZ();
		Biome biome = region.getChunk(i, j).getBiomes()[0];
		SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
		sharedseedrandom.setDecorationSeed(region.getSeed(), i << 4, j << 4);
		//System.out.println("Biome: " + biome.getDisplayName().getString());
		WorldEntitySpawner.performWorldGenSpawning(region, biome, i, j, sharedseedrandom);
	}

	//TODO: WRITE ACTUAL CODE
	@Override
	public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
	{
		//Copied from Overworld
		Biome biome = this.world.getBiome(pos);
		if (creatureType == EnumCreatureType.MONSTER && ((SwampHutStructure) Feature.SWAMP_HUT).func_202383_b(this.world, pos)) {
			return Feature.SWAMP_HUT.getSpawnList();
		} else {
			return creatureType == EnumCreatureType.MONSTER && Feature.OCEAN_MONUMENT.isPositionInStructure(this.world, pos) ? Feature.OCEAN_MONUMENT.getSpawnList() : biome.getSpawns(creatureType);
		}
	}

	@Override
	public OverworldGenSettings getSettings()
	{
		return null;
	}

	@Override
	public int spawnMobs(World world, boolean spawnHostile, boolean spawnPeaceful)
	{
		int i = 0;
		i = i + this.phantomSpawner.spawnMobs(world, spawnHostile, spawnPeaceful);
		return i;
	}

	@Override
	public int getGroundHeight()
	{
		return this.world.getSeaLevel() + 1;
	}

	@Override
	public double[] generateNoiseRegion(int i, int i1)
	{
		return new double[0];
	}


	/* GENERATION METHODS */

	//TEST METHOD
	/* GENERATES THE BLOCKS */
	// PREVIOUSLY other methods, updated for 1.13!
	private void setBlocksInChunk(int chunkX, int chunkZ, IChunk chunk)
	{
		{
			int four = 4;
			int var8 = four + 1;
			int seventeen = 17;
			int fiver = four + 1;
			heightNoise = octaveGenerator(heightNoise, chunkX * four, chunkZ * four, var8, seventeen, fiver);
			for (int i = 0; i < four; ++i)
			{
				for (int j = 0; j < four; ++j)
				{
					for (int k = 0; k < 16; ++k)
					{
						double var14 = 0.125;
						double var16 = heightNoise[((i) * fiver + j) * seventeen + k];
						double var18 = heightNoise[((i) * fiver + j + 1) * seventeen + k];
						double var20 = heightNoise[((i + 1) * fiver + j) * seventeen + k];
						double var22 = heightNoise[((i + 1) * fiver + j + 1) * seventeen + k];
						double var24 = (heightNoise[((i) * fiver + j) * seventeen + k + 1] - var16) * var14;
						double var26 = (heightNoise[((i) * fiver + j + 1) * seventeen + k + 1] - var18) * var14;
						double var28 = (heightNoise[((i + 1) * fiver + j) * seventeen + k + 1] - var20) * var14;
						double var30 = (heightNoise[((i + 1) * fiver + j + 1) * seventeen + k + 1] - var22) * var14;
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
									if (y < seaLevel)
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
	}

	//Replace Biomes where necessary
	private void replaceBiomes(IChunk iChunk)
	{
		for (int z = 0; z < 16; ++z)
		{
			for (int x = 0; x < 16; ++x)
			{
				int xPos = iChunk.getPos().getXStart() + x;
				int zPos = iChunk.getPos().getZStart() + z;
				int yVal = getSolidHeightY(xPos, zPos, iChunk);
				if (yVal > altitude)
				{
					biomesForGeneration[(x << 4 | z)] = BiomeGenBetaPlus.mountain.handle;
				}
				else if (yVal < seaLevel - 1)
				{

					if (yVal < seaLevel - seaDepth)
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

	private void replaceBeaches(IChunk chunk)
	{
		for (int z = 0; z < 16; ++z)
		{

			for (int x = 0; x < 16; ++x)
			{
				int xPos = chunk.getPos().getXStart() + x;
				int zPos = chunk.getPos().getZStart() + z;
				int yVal = getSolidHeightY(xPos, zPos, chunk);
				// New Line
				Biome biome = biomesForGeneration[(x << 4 | z)];
				//Inject Beaches (MODIFIED)
				if ((yVal <= (seaLevel + 1) && yVal >= seaLevel - 1) && (biome != BiomeGenBetaPlus.desert.handle) && chunk.getBlockState(new BlockPos(xPos, yVal, zPos)) == Blocks.SAND.getDefaultState())
				{
						this.biomesForGeneration[(x << 4 | z)] = biomeProviderS.getBeachBiome(new BlockPos(xPos, yVal, zPos));
				}
			}
		}
	}

	/* 1.13, COPY BOOGALOO */
	private double[] octaveGenerator(double[] values, int var2, int var4, int var5, int var6, int var7)
	{
		if (values == null)
		{
			values = new double[var5 * var6 * var7];
		}
		double noiseFactor = 684.412;
		double[] temps = biomeProviderS.temperatures;
		double[] humidities = biomeProviderS.humidities;
		octaveArr4 = octaves6.generateNoiseOctaves(octaveArr4, var2, var4, var5, var7, 1.121, 1.121, 0.5);
		octaveArr5 = octaves7.generateNoiseOctaves(octaveArr5, var2, var4, var5, var7, 200.0, 200.0, 0.5);
		octaveArr1 = octaves3.generateNoiseOctaves(octaveArr1, var2, 0, var4, var5, var6, var7, noiseFactor / 80.0, noiseFactor / 160.0, noiseFactor / 80.0);
		octaveArr2 = octaves1.generateNoiseOctaves(octaveArr2, var2, 0, var4, var5, var6, var7, noiseFactor, noiseFactor, noiseFactor);
		octaveArr3 = octaves2.generateNoiseOctaves(octaveArr3, var2, 0, var4, var5, var6, var7, noiseFactor, noiseFactor, noiseFactor);
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

	// YES, IT IS COPIED AND MODIFIED.
	private void replaceBlocksForBiome(int chunkX, int chunkZ, IChunk chunkprimer, BiomeGenBetaPlus[] biomes)
	{
		double thirtySecond = 0.03125;
		this.sandNoise = this.octaves4.generateNoiseOctaves(this.sandNoise, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, thirtySecond, thirtySecond, 1.0);
		this.gravelNoise = this.octaves4.generateNoiseOctaves(this.gravelNoise, chunkX * 16, 109.0134, chunkZ * 16, 16, 1, 16, thirtySecond, 1.0, thirtySecond);
		this.stoneNoise = this.octaves5.generateNoiseOctaves(this.stoneNoise, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, thirtySecond * 2.0, thirtySecond * 2.0, thirtySecond * 2.0);
		for (int z = 0; z < 16; ++z)
		{
			for (int x = 0; x < 16; ++x)
			{
				BiomeGenBetaPlus biome = biomes[z + x * 16];
				boolean sandN = this.sandNoise[z + x * 16] + this.rand.nextDouble() * 0.2 > 0.0;
				boolean gravelN = this.gravelNoise[z + x * 16] + this.rand.nextDouble() * 0.2 > 3.0;
				int stoneN = (int) (this.stoneNoise[z + x * 16] / 3.0 + 3.0 + this.rand.nextDouble() * 0.25);
				int checkVal = -1;
				IBlockState topBlock = biome.topBlock.getDefaultState();
				IBlockState fillerBlock = biome.fillerBlock.getDefaultState();

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

						//Checks if block already changed
						if (block != Blocks.STONE) continue;

						if (checkVal == -1)
						{
							if (stoneN <= 0)
							{
								topBlock = Blocks.AIR.getDefaultState();
								fillerBlock = Blocks.STONE.getDefaultState();
							}
							else if (y >= seaLevel - 4 && y <= seaLevel + 1)
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
							if (y < seaLevel && topBlock == Blocks.AIR.getDefaultState())
							{
								topBlock = Blocks.WATER.getDefaultState();
							}

							// Sets top & filler Blocks
							checkVal = stoneN;
							// Test this still.
							if (y >= seaLevel -1)
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
							if (checkVal == 0 && fillerBlock == Blocks.SAND)
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

	public static int getSolidHeightY (int x, int z, IChunk chunk)
	{
		for (int y = 130; y >= 0; --y)
		{
			Block block = chunk.getBlockState(new BlockPos(x, y, z)).getBlock();
			if (block != Blocks.AIR && block != Blocks.WATER)
			{
				return y;
			}
		}
		return 0;
	}

	/* Converts Biome Array As Generated to a usable Biome Array */
	private static Biome[] convertBiomeArray(Biome[] biomesIn)
	{
		Biome[] biomesOut = new Biome[biomesIn.length];
		for (int i = 0; i < biomesOut.length; i++)
		{
			//int z = i / 16;
			//int x = i % 16;
			int place = (i & 15) << 4 | i >> 4 & 15;
			//System.out.println(place + " " + x + " " + z + " " + (x << 4 | z));
			biomesOut[i] = biomesIn[place];
		}
		return biomesOut;
	}
}
