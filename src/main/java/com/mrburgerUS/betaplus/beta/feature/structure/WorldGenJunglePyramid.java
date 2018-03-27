package com.mrburgerUS.betaplus.beta.feature.structure;

import com.mrburgerUS.betaplus.BetaPlusHelper;
import com.mrburgerUS.betaplus.beta.biome.BiomeGenBeta;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.*;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nullable;
import java.util.Random;

public class WorldGenJunglePyramid extends MapGenStructure
{
	//Fields
	private int maxDistanceBetweenPyramids = BetaPlusHelper.maxDistanceBetweenPyramids;
	private String structureName = "Jungle_Temple";

	public WorldGenJunglePyramid()
	{
		MapGenStructureIO.registerStructure(WorldGenJunglePyramid.Start.class, structureName);
		MapGenStructureIO.registerStructureComponent(JunglePyramid.class, structureName);
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
			if (world.getBiome(new BlockPos(i * 16 + 8, 0, j * 16 + 8)) == BiomeGenBeta.rainforest.handle)
			{
				return true;
			}
		}

		return false;
	}

	@Override
	protected StructureStart getStructureStart(int chunkX, int chunkZ)
	{
		return new Start(this.rand, chunkX, world.getHeight(chunkX * 16, chunkZ * 16), chunkZ, world.getBiome(new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8)));
	}

	public static class Start extends StructureStart
	{
		public Start(Random random, int chunkX, int yVal, int chunkZ, Biome biomeIn)
		{
			if (biomeIn == BiomeGenBeta.rainforest.handle)
			{
				JunglePyramid junglePieces = new JunglePyramid(random, chunkX * 16, yVal, chunkZ * 16);
				components.add(junglePieces);
				this.updateBoundingBox();
			}
		}
	}

	public static class JunglePyramid extends FeatureBeta
	{
		private boolean placedMainChest;
		private boolean placedHiddenChest;
		private boolean placedTrap1;
		private boolean placedTrap2;
		/**
		 * List of random stones to be generated in the Jungle Pyramid.
		 */
		private static final Stones cobblestoneSelector = new Stones();


		public JunglePyramid(Random rand, int x, int y, int z)
		{
			super(rand, x, 64, z, 12, 10, 15);
		}

		/**
		 * (abstract) Helper method to write subclass data to NBT
		 */
		protected void writeStructureToNBT(NBTTagCompound tagCompound)
		{
			super.writeStructureToNBT(tagCompound);
			tagCompound.setBoolean("placedMainChest", this.placedMainChest);
			tagCompound.setBoolean("placedHiddenChest", this.placedHiddenChest);
			tagCompound.setBoolean("placedTrap1", this.placedTrap1);
			tagCompound.setBoolean("placedTrap2", this.placedTrap2);
		}

		/**
		 * (abstract) Helper method to read subclass data from NBT
		 */
		protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_)
		{
			super.readStructureFromNBT(tagCompound, p_143011_2_);
			this.placedMainChest = tagCompound.getBoolean("placedMainChest");
			this.placedHiddenChest = tagCompound.getBoolean("placedHiddenChest");
			this.placedTrap1 = tagCompound.getBoolean("placedTrap1");
			this.placedTrap2 = tagCompound.getBoolean("placedTrap2");
		}

		/**
		 * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes
		 * Mineshafts at the end, it adds Fences...
		 */
		public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
		{
			if (!this.offsetToAverageGroundLevel(worldIn, structureBoundingBoxIn, 0))
			{
				return false;
			}
			else
			{
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, -4, 0, this.width - 1, 0, this.depth - 1, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 1, 2, 9, 2, 2, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 1, 12, 9, 2, 12, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 1, 3, 2, 2, 11, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, 1, 3, 9, 2, 11, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 3, 1, 10, 6, 1, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 3, 13, 10, 6, 13, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 3, 2, 1, 6, 12, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 10, 3, 2, 10, 6, 12, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 3, 2, 9, 3, 12, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 6, 2, 9, 6, 12, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 3, 7, 3, 8, 7, 11, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 8, 4, 7, 8, 10, false, randomIn, cobblestoneSelector);
				this.fillWithAir(worldIn, structureBoundingBoxIn, 3, 1, 3, 8, 2, 11);
				this.fillWithAir(worldIn, structureBoundingBoxIn, 4, 3, 6, 7, 3, 9);
				this.fillWithAir(worldIn, structureBoundingBoxIn, 2, 4, 2, 9, 5, 12);
				this.fillWithAir(worldIn, structureBoundingBoxIn, 4, 6, 5, 7, 6, 9);
				this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 7, 6, 6, 7, 8);
				this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 1, 2, 6, 2, 2);
				this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 2, 12, 6, 2, 12);
				this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 5, 1, 6, 5, 1);
				this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 5, 13, 6, 5, 13);
				this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 5, 5, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 10, 5, 5, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 5, 9, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 10, 5, 9, structureBoundingBoxIn);

				for (int i = 0; i <= 14; i += 14)
				{
					this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 4, i, 2, 5, i, false, randomIn, cobblestoneSelector);
					this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 4, i, 4, 5, i, false, randomIn, cobblestoneSelector);
					this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 7, 4, i, 7, 5, i, false, randomIn, cobblestoneSelector);
					this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, 4, i, 9, 5, i, false, randomIn, cobblestoneSelector);
				}

				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 5, 6, 0, 6, 6, 0, false, randomIn, cobblestoneSelector);

				for (int l = 0; l <= 11; l += 11)
				{
					for (int j = 2; j <= 12; j += 2)
					{
						this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, l, 4, j, l, 5, j, false, randomIn, cobblestoneSelector);
					}

					this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, l, 6, 5, l, 6, 5, false, randomIn, cobblestoneSelector);
					this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, l, 6, 9, l, 6, 9, false, randomIn, cobblestoneSelector);
				}

				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 7, 2, 2, 9, 2, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, 7, 2, 9, 9, 2, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, 7, 12, 2, 9, 12, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, 7, 12, 9, 9, 12, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 9, 4, 4, 9, 4, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 7, 9, 4, 7, 9, 4, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 9, 10, 4, 9, 10, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 7, 9, 10, 7, 9, 10, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 5, 9, 7, 6, 9, 7, false, randomIn, cobblestoneSelector);
				IBlockState iblockstate2 = Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
				IBlockState iblockstate3 = Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
				IBlockState iblockstate = Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
				IBlockState iblockstate1 = Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH);
				this.setBlockState(worldIn, iblockstate1, 5, 9, 6, structureBoundingBoxIn);
				this.setBlockState(worldIn, iblockstate1, 6, 9, 6, structureBoundingBoxIn);
				this.setBlockState(worldIn, iblockstate, 5, 9, 8, structureBoundingBoxIn);
				this.setBlockState(worldIn, iblockstate, 6, 9, 8, structureBoundingBoxIn);
				this.setBlockState(worldIn, iblockstate1, 4, 0, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, iblockstate1, 5, 0, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, iblockstate1, 6, 0, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, iblockstate1, 7, 0, 0, structureBoundingBoxIn);
				this.setBlockState(worldIn, iblockstate1, 4, 1, 8, structureBoundingBoxIn);
				this.setBlockState(worldIn, iblockstate1, 4, 2, 9, structureBoundingBoxIn);
				this.setBlockState(worldIn, iblockstate1, 4, 3, 10, structureBoundingBoxIn);
				this.setBlockState(worldIn, iblockstate1, 7, 1, 8, structureBoundingBoxIn);
				this.setBlockState(worldIn, iblockstate1, 7, 2, 9, structureBoundingBoxIn);
				this.setBlockState(worldIn, iblockstate1, 7, 3, 10, structureBoundingBoxIn);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 9, 4, 1, 9, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 7, 1, 9, 7, 1, 9, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 10, 7, 2, 10, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 5, 4, 5, 6, 4, 5, false, randomIn, cobblestoneSelector);
				this.setBlockState(worldIn, iblockstate2, 4, 4, 5, structureBoundingBoxIn);
				this.setBlockState(worldIn, iblockstate3, 7, 4, 5, structureBoundingBoxIn);

				for (int k = 0; k < 4; ++k)
				{
					this.setBlockState(worldIn, iblockstate, 5, 0 - k, 6 + k, structureBoundingBoxIn);
					this.setBlockState(worldIn, iblockstate, 6, 0 - k, 6 + k, structureBoundingBoxIn);
					this.fillWithAir(worldIn, structureBoundingBoxIn, 5, 0 - k, 7 + k, 6, 0 - k, 9 + k);
				}

				this.fillWithAir(worldIn, structureBoundingBoxIn, 1, -3, 12, 10, -1, 13);
				this.fillWithAir(worldIn, structureBoundingBoxIn, 1, -3, 1, 3, -1, 13);
				this.fillWithAir(worldIn, structureBoundingBoxIn, 1, -3, 1, 9, -1, 5);

				for (int i1 = 1; i1 <= 13; i1 += 2)
				{
					this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, -3, i1, 1, -2, i1, false, randomIn, cobblestoneSelector);
				}

				for (int j1 = 2; j1 <= 12; j1 += 2)
				{
					this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, -1, j1, 3, -1, j1, false, randomIn, cobblestoneSelector);
				}

				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, -2, 1, 5, -2, 1, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 7, -2, 1, 9, -2, 1, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 6, -3, 1, 6, -3, 1, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 6, -1, 1, 6, -1, 1, false, randomIn, cobblestoneSelector);
				this.setBlockState(worldIn, Blocks.TRIPWIRE_HOOK.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.EAST).withProperty(BlockTripWireHook.ATTACHED, Boolean.valueOf(true)), 1, -3, 8, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.TRIPWIRE_HOOK.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.WEST).withProperty(BlockTripWireHook.ATTACHED, Boolean.valueOf(true)), 4, -3, 8, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.TRIPWIRE.getDefaultState().withProperty(BlockTripWire.ATTACHED, Boolean.valueOf(true)), 2, -3, 8, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.TRIPWIRE.getDefaultState().withProperty(BlockTripWire.ATTACHED, Boolean.valueOf(true)), 3, -3, 8, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 7, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 6, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 5, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 4, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 3, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 2, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 1, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 4, -3, 1, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 3, -3, 1, structureBoundingBoxIn);

				if (!this.placedTrap1)
				{
					this.placedTrap1 = this.createDispenser(worldIn, structureBoundingBoxIn, randomIn, 3, -2, 1, EnumFacing.NORTH, LootTableList.CHESTS_JUNGLE_TEMPLE_DISPENSER);
				}

				this.setBlockState(worldIn, Blocks.VINE.getDefaultState().withProperty(BlockVine.SOUTH, Boolean.valueOf(true)), 3, -2, 2, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.TRIPWIRE_HOOK.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.NORTH).withProperty(BlockTripWireHook.ATTACHED, Boolean.valueOf(true)), 7, -3, 1, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.TRIPWIRE_HOOK.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.SOUTH).withProperty(BlockTripWireHook.ATTACHED, Boolean.valueOf(true)), 7, -3, 5, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.TRIPWIRE.getDefaultState().withProperty(BlockTripWire.ATTACHED, Boolean.valueOf(true)), 7, -3, 2, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.TRIPWIRE.getDefaultState().withProperty(BlockTripWire.ATTACHED, Boolean.valueOf(true)), 7, -3, 3, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.TRIPWIRE.getDefaultState().withProperty(BlockTripWire.ATTACHED, Boolean.valueOf(true)), 7, -3, 4, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 8, -3, 6, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 9, -3, 6, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 9, -3, 5, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 9, -3, 4, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 9, -2, 4, structureBoundingBoxIn);

				if (!this.placedTrap2)
				{
					this.placedTrap2 = this.createDispenser(worldIn, structureBoundingBoxIn, randomIn, 9, -2, 3, EnumFacing.WEST, LootTableList.CHESTS_JUNGLE_TEMPLE_DISPENSER);
				}

				this.setBlockState(worldIn, Blocks.VINE.getDefaultState().withProperty(BlockVine.EAST, Boolean.valueOf(true)), 8, -1, 3, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.VINE.getDefaultState().withProperty(BlockVine.EAST, Boolean.valueOf(true)), 8, -2, 3, structureBoundingBoxIn);

				if (!this.placedMainChest)
				{
					this.placedMainChest = this.generateChest(worldIn, structureBoundingBoxIn, randomIn, 8, -3, 3, LootTableList.CHESTS_JUNGLE_TEMPLE);
				}

				this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 9, -3, 2, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 8, -3, 1, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 4, -3, 5, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 5, -2, 5, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 5, -1, 5, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 6, -3, 5, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 7, -2, 5, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 7, -1, 5, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 8, -3, 5, structureBoundingBoxIn);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, -1, 1, 9, -1, 5, false, randomIn, cobblestoneSelector);
				this.fillWithAir(worldIn, structureBoundingBoxIn, 8, -3, 8, 10, -1, 10);
				this.setBlockState(worldIn, Blocks.STONEBRICK.getStateFromMeta(BlockStoneBrick.CHISELED_META), 8, -2, 11, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STONEBRICK.getStateFromMeta(BlockStoneBrick.CHISELED_META), 9, -2, 11, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STONEBRICK.getStateFromMeta(BlockStoneBrick.CHISELED_META), 10, -2, 11, structureBoundingBoxIn);
				IBlockState iblockstate4 = Blocks.LEVER.getDefaultState().withProperty(BlockLever.FACING, BlockLever.EnumOrientation.NORTH);
				this.setBlockState(worldIn, iblockstate4, 8, -2, 12, structureBoundingBoxIn);
				this.setBlockState(worldIn, iblockstate4, 9, -2, 12, structureBoundingBoxIn);
				this.setBlockState(worldIn, iblockstate4, 10, -2, 12, structureBoundingBoxIn);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 8, -3, 8, 8, -3, 10, false, randomIn, cobblestoneSelector);
				this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 10, -3, 8, 10, -3, 10, false, randomIn, cobblestoneSelector);
				this.setBlockState(worldIn, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 10, -2, 9, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 8, -2, 9, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 8, -2, 10, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.REDSTONE_WIRE.getDefaultState(), 10, -1, 9, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STICKY_PISTON.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.UP), 9, -2, 8, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STICKY_PISTON.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.WEST), 10, -2, 8, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.STICKY_PISTON.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.WEST), 10, -1, 8, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.UNPOWERED_REPEATER.getDefaultState().withProperty(BlockRedstoneRepeater.FACING, EnumFacing.NORTH), 10, -2, 10, structureBoundingBoxIn);

				if (!this.placedHiddenChest)
				{
					this.placedHiddenChest = this.generateChest(worldIn, structureBoundingBoxIn, randomIn, 9, -3, 10, LootTableList.CHESTS_JUNGLE_TEMPLE);
				}

				return true;
			}
		}

		static class Stones extends StructureComponent.BlockSelector
		{
			private Stones()
			{
			}

			/**
			 * picks Block Ids and Metadata (Silverfish)
			 */
			public void selectBlocks(Random rand, int x, int y, int z, boolean wall)
			{
				if (rand.nextFloat() < 0.4F)
				{
					this.blockstate = Blocks.COBBLESTONE.getDefaultState();
				}
				else
				{
					this.blockstate = Blocks.MOSSY_COBBLESTONE.getDefaultState();
				}
			}
		}
	}
}
