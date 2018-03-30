package com.mrburgerUS.betaplus.beta.feature.structure;

import com.mrburgerUS.betaplus.beta.biome.BiomeGenBeta;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nullable;
import java.util.Random;


public class WorldGenDesertPyramid extends MapGenStructure
{
	//Fields
	private String structureName = "Pyramid";
	private int maxDistanceBetweenPyramids;

	public WorldGenDesertPyramid(int distance)
	{
		MapGenStructureIO.registerStructure(WorldGenDesertPyramid.Start.class, structureName);
		MapGenStructureIO.registerStructureComponent(DesertPyramid.class, structureName);
		maxDistanceBetweenPyramids = distance;
	}

	@Override
	public String getStructureName()
	{
		return structureName;
	}

	@Nullable
	@Override
	public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored)
	{
		return findNearestStructurePosBySpacing(worldIn, this, pos, maxDistanceBetweenPyramids, 8, 14357617, false, 100, findUnexplored);
	}

	@Override
	protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
	{
		int i = chunkX;
		int j = chunkZ;

		if (chunkX < 0)
		{
			chunkX -= this.maxDistanceBetweenPyramids - 1;
		}

		if (chunkZ < 0)
		{
			chunkZ -= this.maxDistanceBetweenPyramids - 1;
		}

		int k = chunkX / this.maxDistanceBetweenPyramids;
		int l = chunkZ / this.maxDistanceBetweenPyramids;
		Random random = this.world.setRandomSeed(k, l, 14357617);
		k = k * this.maxDistanceBetweenPyramids;
		l = l * this.maxDistanceBetweenPyramids;
		k = k + random.nextInt(this.maxDistanceBetweenPyramids - 8);
		l = l + random.nextInt(this.maxDistanceBetweenPyramids - 8);

		if (i == k && j == l)
		{
			return world.getBiome(new BlockPos(i * 16 + 8, 0, j * 16 + 8)) == BiomeGenBeta.desert.handle;
		}

		return false;
	}

	@Override
	protected StructureStart getStructureStart(int chunkX, int chunkZ)
	{
		return new Start(this.rand, chunkX, 0, chunkZ, world.getBiome(new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8)));
	}

	public static class Start extends StructureStart
	{
		public Start(Random random, int chunkX, int yVal, int chunkZ, Biome biomeIn)
		{
			if (biomeIn == BiomeGenBeta.desert.handle)
			{
				DesertPyramid desertPyramidPieces = new DesertPyramid(random, chunkX * 16, yVal, chunkZ * 16);
				components.add(desertPyramidPieces);
				this.updateBoundingBox();
			}
		}
	}

	// COPIED OVER
	public static class DesertPyramid extends FeatureBeta
	{
		private final boolean[] hasPlacedChest = new boolean[4];


		public DesertPyramid(Random random, int posX, int posY, int posZ)
		{
			super(random, posX, posY, posZ, 21, 15, 21);
		}

		protected void writeStructureToNBT(NBTTagCompound tagCompound)
		{
			super.writeStructureToNBT(tagCompound);
			tagCompound.setBoolean("hasPlacedChest0", this.hasPlacedChest[0]);
			tagCompound.setBoolean("hasPlacedChest1", this.hasPlacedChest[1]);
			tagCompound.setBoolean("hasPlacedChest2", this.hasPlacedChest[2]);
			tagCompound.setBoolean("hasPlacedChest3", this.hasPlacedChest[3]);
		}

		protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager manager)
		{
			super.readStructureFromNBT(tagCompound, manager);
			this.hasPlacedChest[0] = tagCompound.getBoolean("hasPlacedChest0");
			this.hasPlacedChest[1] = tagCompound.getBoolean("hasPlacedChest1");
			this.hasPlacedChest[2] = tagCompound.getBoolean("hasPlacedChest2");
			this.hasPlacedChest[3] = tagCompound.getBoolean("hasPlacedChest3");
		}

		public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
		{
			//Checks for offest
			if (!this.offsetToAverageGroundLevel(worldIn, structureBoundingBoxIn, 0))
			{
				return false;
			}

			// Creates Base
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, -4, 0, this.width - 1, 0, this.depth - 1, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);

			//Generates "Pyramid"
			for (int i = 1; i <= 9; ++i)
			{
				this.fillWithBlocks(worldIn, structureBoundingBoxIn, i, i, i, this.width - 1 - i, i, this.depth - 1 - i, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
				this.fillWithBlocks(worldIn, structureBoundingBoxIn, i + 1, i, i + 1, this.width - 2 - i, i, this.depth - 2 - i, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			}

			//
			for (int i2 = 0; i2 < this.width; ++i2)
			{
				for (int j = 0; j < this.depth; ++j)
				{
					int k = -5;
					this.replaceAirAndLiquidDownwards(worldIn, Blocks.SANDSTONE.getDefaultState(), i2, -5, j, structureBoundingBoxIn);
				}
			}

			IBlockState iblockstate1 = Blocks.SANDSTONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH);
			IBlockState iblockstate2 = Blocks.SANDSTONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
			IBlockState iblockstate3 = Blocks.SANDSTONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
			IBlockState iblockstate = Blocks.SANDSTONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
			int orangeEnum = ~EnumDyeColor.ORANGE.getDyeDamage() & 15;
			int blueEnum = ~EnumDyeColor.BLUE.getDyeDamage() & 15;

			//FRONT FACADE
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 9, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 10, 1, 3, 10, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.setBlockState(worldIn, iblockstate1, 2, 10, 0, structureBoundingBoxIn);
			this.setBlockState(worldIn, iblockstate2, 2, 10, 4, structureBoundingBoxIn);
			this.setBlockState(worldIn, iblockstate3, 0, 10, 2, structureBoundingBoxIn);
			this.setBlockState(worldIn, iblockstate, 4, 10, 2, structureBoundingBoxIn);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.width - 5, 0, 0, this.width - 1, 9, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.width - 4, 10, 1, this.width - 2, 10, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.setBlockState(worldIn, iblockstate1, this.width - 3, 10, 0, structureBoundingBoxIn);
			this.setBlockState(worldIn, iblockstate2, this.width - 3, 10, 4, structureBoundingBoxIn);
			this.setBlockState(worldIn, iblockstate3, this.width - 5, 10, 2, structureBoundingBoxIn);
			this.setBlockState(worldIn, iblockstate, this.width - 1, 10, 2, structureBoundingBoxIn);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 0, 0, 12, 4, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 1, 0, 11, 3, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 9, 1, 1, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 9, 2, 1, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 9, 3, 1, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 10, 3, 1, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 11, 3, 1, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 11, 2, 1, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 11, 1, 1, structureBoundingBoxIn);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 1, 8, 3, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 2, 8, 2, 2, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 1, 1, 16, 3, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 1, 2, 16, 2, 2, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 4, 5, this.width - 6, 4, this.depth - 6, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 4, 9, 11, 4, 11, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 1, 8, 8, 3, 8, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 1, 8, 12, 3, 8, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 1, 12, 8, 3, 12, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 1, 12, 12, 3, 12, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 5, 4, 4, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.width - 5, 1, 5, this.width - 2, 4, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 7, 9, 6, 7, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.width - 7, 7, 9, this.width - 7, 7, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 5, 9, 5, 7, 11, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.width - 6, 5, 9, this.width - 6, 7, 11, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), false);
			this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 5, 5, 10, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 5, 6, 10, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 6, 6, 10, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), this.width - 6, 5, 10, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), this.width - 6, 6, 10, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), this.width - 7, 6, 10, structureBoundingBoxIn);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 4, 4, 2, 6, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.width - 3, 4, 4, this.width - 3, 6, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.setBlockState(worldIn, iblockstate1, 2, 4, 5, structureBoundingBoxIn);
			this.setBlockState(worldIn, iblockstate1, 2, 3, 4, structureBoundingBoxIn);
			this.setBlockState(worldIn, iblockstate1, this.width - 3, 4, 5, structureBoundingBoxIn);
			this.setBlockState(worldIn, iblockstate1, this.width - 3, 3, 4, structureBoundingBoxIn);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 3, 2, 2, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.width - 3, 1, 3, this.width - 2, 2, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.setBlockState(worldIn, Blocks.SANDSTONE.getDefaultState(), 1, 1, 2, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.SANDSTONE.getDefaultState(), this.width - 2, 1, 2, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.STONE_SLAB.getStateFromMeta(BlockStoneSlab.EnumType.SAND.getMetadata()), 1, 2, 2, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.STONE_SLAB.getStateFromMeta(BlockStoneSlab.EnumType.SAND.getMetadata()), this.width - 2, 2, 2, structureBoundingBoxIn);
			this.setBlockState(worldIn, iblockstate, 2, 1, 2, structureBoundingBoxIn);
			this.setBlockState(worldIn, iblockstate3, this.width - 3, 1, 2, structureBoundingBoxIn);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 3, 5, 4, 3, 18, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.width - 5, 3, 5, this.width - 5, 3, 17, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 5, 4, 2, 16, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.width - 6, 1, 5, this.width - 5, 2, 16, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			//END FRONT FACADE

			//Generates Pillars
			for (int j1 = 5; j1 <= 17; j1 += 2)
			{
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 4, 1, j1, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), 4, 2, j1, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), this.width - 5, 1, j1, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), this.width - 5, 2, j1, structureBoundingBoxIn);
			}

			this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), 10, 0, 7, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), 10, 0, 8, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), 9, 0, 9, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), 11, 0, 9, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), 8, 0, 10, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), 12, 0, 10, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), 7, 0, 10, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), 13, 0, 10, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), 9, 0, 11, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), 11, 0, 11, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), 10, 0, 12, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), 10, 0, 13, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(blueEnum), 10, 0, 10, structureBoundingBoxIn);

			for (int j2 = 0; j2 <= this.width - 1; j2 += this.width - 1)
			{
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), j2, 2, 1, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), j2, 2, 2, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), j2, 2, 3, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), j2, 3, 1, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), j2, 3, 2, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), j2, 3, 3, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), j2, 4, 1, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), j2, 4, 2, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), j2, 4, 3, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), j2, 5, 1, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), j2, 5, 2, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), j2, 5, 3, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), j2, 6, 1, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), j2, 6, 2, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), j2, 6, 3, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), j2, 7, 1, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), j2, 7, 2, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), j2, 7, 3, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), j2, 8, 1, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), j2, 8, 2, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), j2, 8, 3, structureBoundingBoxIn);
			}

			for (int k2 = 2; k2 <= this.width - 3; k2 += this.width - 3 - 2)
			{
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), k2 - 1, 2, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), k2, 2, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), k2 + 1, 2, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), k2 - 1, 3, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), k2, 3, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), k2 + 1, 3, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), k2 - 1, 4, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), k2, 4, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), k2 + 1, 4, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), k2 - 1, 5, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), k2, 5, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), k2 + 1, 5, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), k2 - 1, 6, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), k2, 6, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), k2 + 1, 6, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), k2 - 1, 7, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), k2, 7, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), k2 + 1, 7, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), k2 - 1, 8, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), k2, 8, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), k2 + 1, 8, 0, structureBoundingBoxIn);
			}

			//Create Area Underneath (TOMB)
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 4, 0, 12, 6, 0, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), false);
			this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 8, 6, 0, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 12, 6, 0, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), 9, 5, 0, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), 10, 5, 0, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(orangeEnum), 11, 5, 0, structureBoundingBoxIn);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, -14, 8, 12, -11, 12, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, -10, 8, 12, -10, 12, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, -9, 8, 12, -9, 12, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, -8, 8, 12, -1, 12, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, -11, 9, 11, -1, 11, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.setBlockState(worldIn, Blocks.STONE_PRESSURE_PLATE.getDefaultState(), 10, -11, 10, structureBoundingBoxIn);
			this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, -13, 9, 11, -13, 11, Blocks.TNT.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 8, -11, 10, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 8, -10, 10, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), 7, -10, 10, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 7, -11, 10, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 12, -11, 10, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 12, -10, 10, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), 13, -10, 10, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 13, -11, 10, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 10, -11, 8, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 10, -10, 8, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), 10, -10, 7, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 10, -11, 7, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 10, -11, 12, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 10, -10, 12, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), 10, -10, 13, structureBoundingBoxIn);
			this.setBlockState(worldIn, Blocks.SANDSTONE.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 10, -11, 13, structureBoundingBoxIn);


			//Generate Loot
			for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
			{
				if (!this.hasPlacedChest[enumfacing.getHorizontalIndex()])
				{
					int k1 = enumfacing.getFrontOffsetX() * 2;
					int l1 = enumfacing.getFrontOffsetZ() * 2;
					this.hasPlacedChest[enumfacing.getHorizontalIndex()] = this.generateChest(worldIn, structureBoundingBoxIn, randomIn, 10 + k1, -11, 10 + l1, LootTableList.CHESTS_DESERT_PYRAMID);
				}
			}

			return true;
		}
	}

}
