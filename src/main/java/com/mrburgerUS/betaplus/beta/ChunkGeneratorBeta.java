package com.mrburgerUS.betaplus.beta;

import com.mrburgerUS.betaplus.BetaPlusSettings;
import com.mrburgerUS.betaplus.beta.biome.BiomeGenBeta;
import com.mrburgerUS.betaplus.beta.feature.decoration.*;
import com.mrburgerUS.betaplus.beta.feature.structure.WorldGenDesertPyramid;
import com.mrburgerUS.betaplus.beta.feature.structure.WorldGenDungeons;
import com.mrburgerUS.betaplus.beta.feature.structure.WorldGenIgloo;
import com.mrburgerUS.betaplus.beta.feature.structure.WorldGenJunglePyramid;
import com.mrburgerUS.betaplus.beta.noise.NoiseGeneratorOctavesBeta;
import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenStronghold;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ChunkGeneratorBeta implements IChunkGenerator
{
	private final BiomeProviderBeta biomeProvider;

	//Fields
	private Random rand;
	private World worldObj;
	public static Biome[] biomesForGeneration;
	public static final int seaLevel = 64;
	//Noise Generators
	private NoiseGeneratorOctavesBeta octaves1;
	private NoiseGeneratorOctavesBeta octaves2;
	private NoiseGeneratorOctavesBeta octaves3;
	private NoiseGeneratorOctavesBeta octaves4;
	private NoiseGeneratorOctavesBeta octaves5;
	private NoiseGeneratorOctavesBeta octaves6;
	private NoiseGeneratorOctavesBeta octaves7;
	private NoiseGeneratorOctavesBeta mobSpawnerNoise;
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
	private double[] generatedTemperatures;
	//World Features
	private net.minecraft.world.gen.MapGenBase caveGenerator = new net.minecraft.world.gen.MapGenCaves();
	private MapGenRavine ravineGenerator = new MapGenRavine();
	private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
	private MapGenStronghold strongholdGenerator = new MapGenStronghold();
	private WorldGenDesertPyramid desertPyramidGenerator;
	private WorldGenJunglePyramid junglePyramidGenerator;
	private WorldGenIgloo iglooGenerator;
	//Settings
	public static BetaPlusSettings settings;


	//Constructors
	public ChunkGeneratorBeta(World world, long seed, String generationOptions)
	{
		//If we have options
		if (generationOptions != null)
		{
			settings = BetaPlusSettings.Factory.jsonToSettings(generationOptions).build();
		}

		worldObj = world;
		worldObj.setSeaLevel(seaLevel);

		rand = new Random(seed);
		octaves1 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves2 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves3 = new NoiseGeneratorOctavesBeta(rand, 8);
		octaves4 = new NoiseGeneratorOctavesBeta(rand, 4);
		octaves5 = new NoiseGeneratorOctavesBeta(rand, 4);
		octaves6 = new NoiseGeneratorOctavesBeta(rand, 10);
		octaves7 = new NoiseGeneratorOctavesBeta(rand, 16);
		mobSpawnerNoise = new NoiseGeneratorOctavesBeta(rand, 8);
		biomeProvider = new BiomeProviderBeta(world);

		//Set Generators up
		desertPyramidGenerator = new WorldGenDesertPyramid(settings.maxDistanceBetweenPyramids);
		junglePyramidGenerator = new WorldGenJunglePyramid(settings.maxDistanceBetweenPyramids);
		iglooGenerator = new WorldGenIgloo(settings.maxDistanceBetweenPyramids);
	}


	//Overrides
	@Override
	public Chunk generateChunk(int x, int z)
	{
		rand.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
		//Generate Biome Lookup
		biomesForGeneration = this.biomeProvider.getBiomes(biomesForGeneration, x * 16, z * 16, 16, 16);
		//Create Chunk Primer
		ChunkPrimer primer = new ChunkPrimer();
		//Generate Basic Terrain
		generateTerrain(x, z, primer);
		//Add Grass or Sand or Gravel fill
		replaceBlocksForBiome(x, z, primer, biomesForGeneration);
		//Make Caves
		caveGenerator.generate(worldObj, x, z, primer);
		//Make Ravines
		if (settings.useRavines)
			ravineGenerator.generate(worldObj, x, z, primer);

		//Creates chunk
		Chunk chunk = new Chunk(worldObj, primer, x, z);

		byte[] biomes = chunk.getBiomeArray();
		for (int i = 0; i < biomes.length; ++i)
		{
			//Set Biomes
			//biomes[i] = ((byte) Biome.getIdForBiome(biomesForGeneration[(i & 15) << 4 | i >> 4 & 15]));
		}


		//BEGIN STRUCTURES GENERATION

		if (settings.useMineShafts)
			mineshaftGenerator.generate(worldObj, x, z, primer);
		if (settings.useStrongholds)
			strongholdGenerator.generate(worldObj, x, z, primer);
		if (settings.useTemples)
		{
			desertPyramidGenerator.generate(worldObj, x, z, primer);
			junglePyramidGenerator.generate(worldObj, x, z, primer);
			iglooGenerator.generate(worldObj, x, z, primer);
		}

		//END STRUCTURES GENERATION

		chunk.generateSkylightMap();
		return chunk;
	}

	@Override
	public void populate(int x, int z)
	{
		//Create variables for later use
		int posX = x * 16;
		int posZ = z * 16;
		BlockPos blockPos = new BlockPos(posX, 0, posZ);
		Biome biomeAtPos = worldObj.getBiome(blockPos.add(16, 0, 16));
		ChunkPos cPos = new ChunkPos(x, z);

		//Decorate Vanilla Plus
		biomeAtPos.decorate(worldObj, rand, blockPos);
		if (biomeAtPos == BiomeGenBeta.seasonalForest.handle)
		{
			WorldGenMinableBeta.generateOre(worldObj, rand, x, z, 1, 16, 4, Blocks.EMERALD_ORE.getDefaultState());
		}
		WorldGenSnowLayerBeta.generateSnow(worldObj, cPos);

		//Features
		if (settings.useDungeons)
			WorldGenDungeons.generateDungeons(worldObj, rand, blockPos);
		if (settings.useMineShafts)
			mineshaftGenerator.generateStructure(worldObj, rand, cPos);
		if (settings.useStrongholds)
			strongholdGenerator.generateStructure(worldObj, rand, cPos);
		if (settings.useTemples)
		{
			desertPyramidGenerator.generateStructure(worldObj, rand, cPos);
			junglePyramidGenerator.generateStructure(worldObj, rand, cPos);
			iglooGenerator.generateStructure(worldObj, rand, cPos);
		}

		//Lakes
		if (biomeAtPos != Biomes.DESERT && biomeAtPos != Biomes.DESERT_HILLS && this.rand.nextInt(settings.waterLakeChance) == 0)
		{
			int i1 = this.rand.nextInt(16) + 8;
			int j1 = this.rand.nextInt(256);
			int k1 = this.rand.nextInt(16) + 8;
			(new WorldGenLakes(Blocks.WATER)).generate(worldObj, this.rand, blockPos.add(i1, j1, k1));
		}
		//Lava Lakes
		int addY = this.rand.nextInt(this.rand.nextInt(248) + 8);
		if (addY < worldObj.getSeaLevel() - 2 || this.rand.nextInt(250) == 0)
		{
			int addX = this.rand.nextInt(16) + 8;
			int addZ = this.rand.nextInt(16) + 8;
			(new WorldGenLakes(Blocks.LAVA)).generate(worldObj, this.rand, blockPos.add(addX, addY, addZ));
		}


		//Spawn Passive Entities
		WorldEntitySpawner.performWorldGenSpawning(worldObj, biomeAtPos, posX + 8, posZ + 8, 16, 16, this.rand);
	}

	@Override
	public boolean generateStructures(Chunk chunkIn, int x, int z)
	{
		return false;
	}

	@Override
	public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
	{
		return worldObj.getBiome(pos).getSpawnableList(creatureType);
	}

	@Nullable
	@Override
	public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored)
	{
		if ("Stronghold".equals(structureName) && strongholdGenerator != null && settings.useStrongholds)
			return strongholdGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
		else if ("Pyramid".equals(structureName) && desertPyramidGenerator != null && settings.useTemples)
			return desertPyramidGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
		else if ("Jungle_Temple".equals(structureName) && junglePyramidGenerator != null && settings.useTemples)
			return junglePyramidGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
		else if ("Igloo".equals(structureName) && iglooGenerator != null && settings.useTemples)
			return iglooGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
		return null;
	}

	@Override
	public void recreateStructures(Chunk chunkIn, int x, int z)
	{

	}

	@Override
	public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos)
	{
		return false;
	}


	//BEGIN OUTSIDE METHODS

	//Methods
	public void generateTerrain(int chunkX, int chunkZ, ChunkPrimer chunk)
	{
		int four = 4;
		int sixtyFour = 64;
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
								if (k * 8 + l < sixtyFour)
								{
									//Removed Ice Gen from Here
									block = Blocks.WATER;
								}
								if (var48 > 0.0)
								{
									block = Blocks.STONE;
								}
								if (block != null)
								{
									chunk.setBlockState(x, y, z, block.getDefaultState());
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

	private double[] octaveGenerator(double[] values, int var2, int var4, int var5, int var6, int var7)
	{
		if (values == null)
		{
			values = new double[var5 * var6 * var7];
		}
		double noiseFactor = 684.412;
		double[] temps = biomeProvider.temperature;
		double[] humidities = biomeProvider.humidity;
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

	public void replaceBlocksForBiome(int chunkX, int chunkZ, ChunkPrimer chunk, Biome[] biomes)
	{
		double thirtySecond = 0.03125;
		this.sandNoise = this.octaves4.generateNoiseOctaves(this.sandNoise, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, thirtySecond, thirtySecond, 1.0);
		this.gravelNoise = this.octaves4.generateNoiseOctaves(this.gravelNoise, chunkX * 16, 109.0134, chunkZ * 16, 16, 1, 16, thirtySecond, 1.0, thirtySecond);
		this.stoneNoise = this.octaves5.generateNoiseOctaves(this.stoneNoise, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, thirtySecond * 2.0, thirtySecond * 2.0, thirtySecond * 2.0);
		for (int z = 0; z < 16; ++z)
		{
			for (int x = 0; x < 16; ++x)
			{
				BiomeGenBeta biome = BiomeGenBeta.getBiomeBeta(biomes[z + x * 16]);
				boolean sandN = this.sandNoise[z + x * 16] + this.rand.nextDouble() * 0.2 > 0.0;
				boolean gravelN = this.gravelNoise[z + x * 16] + this.rand.nextDouble() * 0.2 > 3.0;
				int stoneN = (int) (this.stoneNoise[z + x * 16] / 3.0 + 3.0 + this.rand.nextDouble() * 0.25);
				int checkVal = -1;
				Block topBlock = biome.topBlock;
				Block fillerBlock = biome.fillerBlock;

				// GO from Top to bottom of world
				for (int y = 127; y >= 0; --y)
				{
					if (y <= this.rand.nextInt(5))
					{
						chunk.setBlockState(x, y, z, Blocks.BEDROCK.getDefaultState());
					}
					else
					{
						Block block = chunk.getBlockState(x, y, z).getBlock();

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
								topBlock = Blocks.AIR;
								fillerBlock = Blocks.STONE;
							}
							else if (y >= seaLevel - 4 && y <= seaLevel + 1)
							{
								topBlock = biome.topBlock;
								fillerBlock = biome.fillerBlock;
								if (gravelN)
								{
									topBlock = Blocks.AIR;
								}
								if (gravelN)
								{
									fillerBlock = Blocks.GRAVEL;
								}
								if (sandN)
								{
									topBlock = Blocks.SAND;
								}
								if (sandN)
								{
									fillerBlock = Blocks.SAND;
								}
							}
							if (y < seaLevel && topBlock == Blocks.AIR)
							{
								topBlock = Blocks.WATER;
							}

							// FILLS IN OCEAN + BEACH BIOMES
							if ((y < seaLevel + 1 && y > seaLevel - (settings.seaDepth / 2)) && (topBlock == Blocks.SAND || fillerBlock == Blocks.GRAVEL) && biomesForGeneration[(x << 4 | z)] != BiomeGenBeta.desert.handle && biomesForGeneration[(x << 4 | z)] != BiomeGenBeta.swampland.handle)
							{
								biomesForGeneration[(x << 4 | z)] = BiomeGenBeta.beach.handle;
							}
							else if (y <= seaLevel - settings.seaDepth)
							{
								biomesForGeneration[(x << 4 | z)] = BiomeGenBeta.deepOcean.handle;
							}
							else if (y < seaLevel - 1 && biomesForGeneration[(x << 4 | z)] != BiomeGenBeta.swampland.handle)
							{
								biomesForGeneration[(x << 4 | z)] = BiomeGenBeta.ocean.handle;
							}
							// END OF FILLING OCEANS


							// Sets top & filler Blocks
							checkVal = stoneN;
							if (y >= seaLevel - 1)
							{
								chunk.setBlockState(x, y, z, topBlock.getDefaultState());
								continue;
							}
							chunk.setBlockState(x, y, z, fillerBlock.getDefaultState());
							continue;

						}
						if (checkVal <= 0) continue;
						chunk.setBlockState(x, y, z, fillerBlock.getDefaultState());
						if (--checkVal != 0 || fillerBlock != Blocks.SAND) continue;
						checkVal = this.rand.nextInt(4);
						fillerBlock = Blocks.SANDSTONE;


					} //END OF Y LOOP
				}
			}
		}
	}

	private static void decorate(World world, Random random, BlockPos blockPos, Biome biome)
	{
		int posX = blockPos.getX();
		int posZ = blockPos.getZ();

		//Add Trees
		WorldGenTreesBeta.generateTrees(world, random, blockPos, biome);

		//Add Flowers
		WorldGenFlowersBeta.generateFlowers(world, random, posX, posZ);

		//Add Grass
		WorldGenTallGrassBeta.generateTallGrass(world, random, posX, posZ);
		if (biome == BiomeGenBeta.desert.handle)
		{
			WorldGenDeadBushBeta.generateBush(world, random, blockPos);
		}

		//Add Ores
		WorldGenMinableBeta.generateOres(world, random, posX / 16, posZ / 16);
	}
}
