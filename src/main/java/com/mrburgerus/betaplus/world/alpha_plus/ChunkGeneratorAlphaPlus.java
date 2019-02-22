package com.mrburgerus.betaplus.world.alpha_plus;

import com.mrburgerus.betaplus.util.BetaPlusDeepenOcean;
import com.mrburgerus.betaplus.world.noise.NoiseGeneratorOctavesAlpha;
import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.AbstractChunkGenerator;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.WorldGenRegion;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ChunkGeneratorAlphaPlus extends AbstractChunkGenerator
{
	// Fields
	private Random rand;
	private NoiseGeneratorOctavesAlpha field_912_k;
	private NoiseGeneratorOctavesAlpha field_911_l;
	private NoiseGeneratorOctavesAlpha field_910_m;
	private NoiseGeneratorOctavesAlpha field_909_n;
	private NoiseGeneratorOctavesAlpha field_908_o;
	public NoiseGeneratorOctavesAlpha field_922_a;
	public NoiseGeneratorOctavesAlpha field_921_b;
	private double[] field_906_q;
	double[] field_919_d;
	double[] field_918_e;
	double[] field_917_f;
	double[] field_916_g;
	double[] field_915_h;
	private double[] sandNoise = new double[256];
	private double[] gravelNoise = new double[256];
	private double[] stoneNoise = new double[256];

	public ChunkGeneratorAlphaPlus(IWorld world, BiomeProvider biomeProvider)
	{
		super(world, biomeProvider);
		this.rand = new Random(seed);
		this.field_912_k = new NoiseGeneratorOctavesAlpha(this.rand, 16);
		this.field_911_l = new NoiseGeneratorOctavesAlpha(this.rand, 16);
		this.field_910_m = new NoiseGeneratorOctavesAlpha(this.rand, 8);
		this.field_909_n = new NoiseGeneratorOctavesAlpha(this.rand, 4);
		this.field_908_o = new NoiseGeneratorOctavesAlpha(this.rand, 4);
		this.field_922_a = new NoiseGeneratorOctavesAlpha(this.rand, 10);
		this.field_921_b = new NoiseGeneratorOctavesAlpha(this.rand, 16);
	}

	@Override
	public void makeBase(IChunk iChunk)
	{
		int xPos = iChunk.getPos().x;
		int zPos =  iChunk.getPos().z;
		setBlocksInChunk(iChunk);
		Biome[] biomeBlock = this.biomeProvider.getBiomeBlock(xPos * 16, zPos * 16, 16, 16);
		iChunk.setBiomes(biomeBlock);
		this.replaceBlocks(iChunk);
		BetaPlusDeepenOcean.deepenOcean(iChunk, rand, 64, 7);
		iChunk.setStatus(ChunkStatus.BASE);
	}

	@Override
	public void decorate(WorldGenRegion region)
	{
		super.decorate(region);
	}

	@Nullable
	public BlockPos findNearestStructure(World worldIn, String name, BlockPos pos, int radius, boolean p_211403_5_)
	{
		return null;
	}

	@Override
	public void spawnMobs(WorldGenRegion worldGenRegion)
	{

	}

	@Override
	public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType enumCreatureType, BlockPos blockPos)
	{
		return null;
	}

	@Override
	public IChunkGenSettings getSettings()
	{
		return null;
	}

	@Override
	public int spawnMobs(World world, boolean b, boolean b1)
	{
		return 0;
	}

	@Override
	public int getGroundHeight()
	{
		return world.getSeaLevel();
	}

	/* Not Used */
	@Override
	public double[] generateNoiseRegion(int x, int z)
	{
		return new double[0];
	}

	/* Sets blocks, just like beta */
	public void setBlocksInChunk(IChunk chunk)
	{
		int chunkX = chunk.getPos().x;
		int chunkZ = chunk.getPos().z;
		byte var4 = 4;
		byte var5 = 64;
		int var6 = var4 + 1;
		byte var7 = 17;
		int var8 = var4 + 1;
		this.field_906_q = this.generateOctaves(this.field_906_q, chunkX * var4, 0, chunkZ * var4, var6, var7, var8);

		for (int var9 = 0; var9 < var4; ++var9) {
			for (int var10 = 0; var10 < var4; ++var10) {
				for (int var11 = 0; var11 < 16; ++var11) {
					double var12 = 0.125D;
					double var14 = this.field_906_q[((((var9) * var8) + var10) * var7) + var11];
					double var16 = this.field_906_q[((var9) * var8 + var10 + 1) * var7 + var11];
					double var18 = this.field_906_q[((var9 + 1) * var8 + var10) * var7 + var11];
					double var20 = this.field_906_q[((var9 + 1) * var8 + var10 + 1) * var7 + var11];
					double var22 = (this.field_906_q[((var9) * var8 + var10) * var7 + var11 + 1] - var14) * var12;
					double var24 = (this.field_906_q[((var9) * var8 + var10 + 1) * var7 + var11 + 1] - var16) * var12;
					double var26 = (this.field_906_q[((var9 + 1) * var8 + var10) * var7 + var11 + 1] - var18) * var12;
					double var28 = (this.field_906_q[((var9 + 1) * var8 + var10 + 1) * var7 + var11 + 1] - var20) * var12;

					for (int var30 = 0; var30 < 8; ++var30) {
						double var31 = 0.25D;
						double var33 = var14;
						double var35 = var16;
						double var37 = (var18 - var14) * var31;
						double var39 = (var20 - var16) * var31;

						for (int var41 = 0; var41 < 4; ++var41) {
							//int index = var41 + var9 * 4 << 11 | 0 + var10 * 4 << 7 | var11 * 8 + var30;
							int x = var41 + var9 * 4;
							int y = var11 * 8 + var30;
							int z = var10 * 4;

							double var44 = 0.25D;
							double var46 = var33;
							double var48 = (var35 - var33) * var44;

							for (int var50 = 0; var50 < 4; ++var50) {
								Block block = null;
								if (var11 * 8 + var30 < var5) {
									block = Blocks.WATER;
								}

								if (var46 > 0.0D) {
									block = Blocks.STONE;
								}

								if (block != null) {
									chunk.setBlockState(new BlockPos(x, y, z), block.getDefaultState(), false);
								}

								//index += var43;
								++z;
								var46 += var48;
							}

							var33 += var37;
							var35 += var39;
						}

						var14 += var22;
						var16 += var24;
						var18 += var26;
						var20 += var28;
					}
				}
			}
		}

	}

	private double[] generateOctaves(double[] var1, int var2, int var3, int var4, int var5, int var6, int var7) {
		if (var1 == null) {
			var1 = new double[var5 * var6 * var7];
		}

		double var8 = 684.412D;
		double var10 = 684.412D;
		this.field_916_g =
				this.field_922_a.generateNoiseOctaves(this.field_916_g, (double) var2, (double) var3, (double) var4, var5, 1, var7, 1.0D, 0.0D, 1.0D);
		this.field_915_h = this.field_921_b
				.generateNoiseOctaves(this.field_915_h, (double) var2, (double) var3, (double) var4, var5, 1, var7, 100.0D, 0.0D, 100.0D);
		this.field_919_d = this.field_910_m
				.generateNoiseOctaves(this.field_919_d, (double) var2, (double) var3, (double) var4, var5, var6, var7, var8 / 80.0D, var10 / 160.0D,
						var8 / 80.0D);
		this.field_918_e = this.field_912_k
				.generateNoiseOctaves(this.field_918_e, (double) var2, (double) var3, (double) var4, var5, var6, var7, var8, var10, var8);
		this.field_917_f = this.field_911_l
				.generateNoiseOctaves(this.field_917_f, (double) var2, (double) var3, (double) var4, var5, var6, var7, var8, var10, var8);
		int var12 = 0;
		int var13 = 0;

		for (int var14 = 0; var14 < var5; ++var14) {
			for (int var15 = 0; var15 < var7; ++var15) {
				double var16 = (this.field_916_g[var13] + 256.0D) / 512.0D;
				if (var16 > 1.0D) {
					var16 = 1.0D;
				}

				double var18 = 0.0D;
				double var20 = this.field_915_h[var13] / 8000.0D;
				if (var20 < 0.0D) {
					var20 = -var20;
				}

				var20 = var20 * 3.0D - 3.0D;
				if (var20 < 0.0D) {
					var20 = var20 / 2.0D;
					if (var20 < -1.0D) {
						var20 = -1.0D;
					}

					var20 = var20 / 1.4D;
					var20 = var20 / 2.0D;
					var16 = 0.0D;
				} else {
					if (var20 > 1.0D) {
						var20 = 1.0D;
					}

					var20 = var20 / 6.0D;
				}

				var16 = var16 + 0.5D;
				var20 = var20 * (double) var6 / 16.0D;
				double var22 = (double) var6 / 2.0D + var20 * 4.0D;
				++var13;

				for (int var24 = 0; var24 < var6; ++var24) {
					double var25 = 0.0D;
					double var27 = ((double) var24 - var22) * 12.0D / var16;
					if (var27 < 0.0D) {
						var27 *= 4.0D;
					}

					double var29 = this.field_918_e[var12] / 512.0D;
					double var31 = this.field_917_f[var12] / 512.0D;
					double var33 = (this.field_919_d[var12] / 10.0D + 1.0D) / 2.0D;
					if (var33 < 0.0D) {
						var25 = var29;
					} else if (var33 > 1.0D) {
						var25 = var31;
					} else {
						var25 = var29 + (var31 - var29) * var33;
					}

					var25 = var25 - var27;
					if (var24 > var6 - 4) {
						double var35 = (double) ((float) (var24 - (var6 - 4)) / 3.0F);
						var25 = var25 * (1.0D - var35) + -10.0D * var35;
					}

					if ((double) var24 < var18) {
						double var45 = (var18 - (double) var24) / 4.0D;
						if (var45 < 0.0D) {
							var45 = 0.0D;
						}

						if (var45 > 1.0D) {
							var45 = 1.0D;
						}

						var25 = var25 * (1.0D - var45) + -10.0D * var45;
					}

					var1[var12] = var25;
					++var12;
				}
			}
		}

		return var1;
	}

	public void replaceBlocks( IChunk chunk)
	{
		int chunkX = chunk.getPos().x;
		int chunkZ = chunk.getPos().z;

		byte seaLevel = 64;
		double var5 = 0.03125D;


		this.sandNoise = this.field_909_n.generateNoiseOctaves(this.sandNoise, chunkX * 16, chunkZ * 16, 0.0D, 16, 16, 1, var5, var5, 1.0D);

		this.gravelNoise = this.field_909_n.generateNoiseOctaves(this.gravelNoise, chunkZ * 16, 109.0134D, chunkX * 16, 16, 1, 16, var5, 1.0D, var5);

		this.stoneNoise = this.field_908_o.generateNoiseOctaves(this.stoneNoise, chunkX * 16, chunkZ * 16, 0.0D, 16, 16, 1, var5 * 2.0D, var5 * 2.0D, var5 * 2.0D);


		for (int x = 0; x < 16; x++)
		{
			for (int z = 0; z < 16; z++)
			{
				boolean sand = this.sandNoise[(x + z * 16)] + this.rand.nextDouble() * 0.2D > 0.0D;
				boolean gravel = this.gravelNoise[(x + z * 16)] + this.rand.nextDouble() * 0.2D > 3.0D;
				int stone = (int) (this.stoneNoise[(x + z * 16)] / 3.0D + 3.0D + this.rand.nextDouble() * 0.25D);
				int var12 = -1;
				Block topBlock = Blocks.GRASS_BLOCK;
				Block fillerBlock = Blocks.DIRT;


				boolean top = true;
				boolean water = false;

				for (int y = 127; y >= 0; y--)
				{
					if (y <= this.rand.nextInt(6) - 1)
					{
						chunk.setBlockState(new BlockPos(x, y, z), Blocks.BEDROCK.getDefaultState(), false);
					}
					else
					{
						Block block = chunk.getBlockState(new BlockPos(x, y, z)).getBlock();

						if (block == Blocks.AIR)
						{
							var12 = -1;
						}
						else if (block == Blocks.STONE)
						{
							if (var12 == -1)
							{
								if (stone <= 0)
								{
									topBlock = Blocks.AIR;
									fillerBlock = Blocks.STONE;
								}
								else if ((y >= seaLevel - 4) && (y <= seaLevel + 1))
								{
									topBlock = Blocks.GRASS_BLOCK;
									fillerBlock = Blocks.DIRT;
									if (gravel)
									{
										topBlock = Blocks.AIR;
									}

									if (gravel)
									{
										fillerBlock = Blocks.GRAVEL;
									}

									if (sand)
									{
										topBlock = Blocks.SAND;
									}

									if (sand)
									{
										fillerBlock = Blocks.SAND;
									}
								}

								if ((y < seaLevel) && (topBlock == Blocks.AIR))
								{
									topBlock = Blocks.WATER;
								}

								var12 = stone;
								if (y >= seaLevel - 1)
								{
									chunk.setBlockState(new BlockPos(x, y, z), topBlock.getDefaultState(), false);
								}
								else
								{
									chunk.setBlockState(new BlockPos(x, y, z), fillerBlock.getDefaultState(), false);
								}
							}
							else if (var12 > 0)
							{
								var12--;
								chunk.setBlockState(new BlockPos(x, y, z), fillerBlock.getDefaultState(), false);
							}
						}
						else if (block == Blocks.WATER)
						{
							water = true;
						}
					}
				}
			}
		}
	}

}
