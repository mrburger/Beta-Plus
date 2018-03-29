package com.mrburgerUS.betaplus.beta.feature.structure;

import com.mrburgerUS.betaplus.beta.biome.BiomeGenBeta;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Blocks;
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

import javax.annotation.Nullable;
import java.util.Random;

public class WorldGenSwampHut extends MapGenStructure
{
	//Fields
	private int maxDistanceBetweenPyramids;
	private String structureName = "Swamp_Hut";

	public WorldGenSwampHut(int distance)
	{
		MapGenStructureIO.registerStructure(WorldGenSwampHut.Start.class, structureName);
		MapGenStructureIO.registerStructureComponent(SwampHut.class, structureName);
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
			if (world.getBiome(new BlockPos(i * 16 + 8, 0, j * 16 + 8)) == BiomeGenBeta.swampland.handle)
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
			if (biomeIn == BiomeGenBeta.swampland.handle)
			{
				SwampHut swampPieces = new SwampHut(random, chunkX * 16, chunkZ * 16);
				components.add(swampPieces);
				this.updateBoundingBox();
			}
		}
	}

	public static class SwampHut extends FeatureBeta
	{
		private boolean hasWitch;

		public SwampHut(Random random, int xPos, int zPos)
		{
			super(random, xPos, 64, zPos, 7, 7, 9);
		}

		protected void writeStructureToNBT(NBTTagCompound tagCompound)
		{
			super.writeStructureToNBT(tagCompound);
			tagCompound.setBoolean("Witch", this.hasWitch);
		}

		protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_)
		{
			super.readStructureFromNBT(tagCompound, p_143011_2_);
			this.hasWitch = tagCompound.getBoolean("Witch");
		}

		public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
		{
			if (!this.offsetToAverageGroundLevel(worldIn, structureBoundingBoxIn, 0))
			{
				return false;
			}
			else
			{
				this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 5, 1, 7, Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), false);
				this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 2, 5, 4, 7, Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), false);
				this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 0, 4, 1, 0, Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), false);
				this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 2, 2, 3, 3, 2, Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), false);
				this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 3, 1, 3, 6, Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), false);
				this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 2, 3, 5, 3, 6, Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), false);
				this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 2, 7, 4, 3, 7, Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), Blocks.PLANKS.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()), false);
				this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 2, 1, 3, 2, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
				this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 0, 2, 5, 3, 2, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
				this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 7, 1, 3, 7, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
				this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 0, 7, 5, 3, 7, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
				this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 2, 3, 2, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 3, 3, 7, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 3, 4, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 5, 3, 4, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 5, 3, 5, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.FLOWER_POT.getDefaultState().withProperty(BlockFlowerPot.CONTENTS, BlockFlowerPot.EnumFlowerType.MUSHROOM_RED), 1, 3, 5, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.CRAFTING_TABLE.getDefaultState(), 3, 2, 6, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.CAULDRON.getDefaultState(), 4, 2, 6, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 1, 2, 1, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 5, 2, 1, structureBoundingBoxIn);
				IBlockState iblockstate = Blocks.SPRUCE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH);
				IBlockState iblockstate1 = Blocks.SPRUCE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
				IBlockState iblockstate2 = Blocks.SPRUCE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
				IBlockState iblockstate3 = Blocks.SPRUCE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
				this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 1, 6, 4, 1, iblockstate, iblockstate, false);
				this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 2, 0, 4, 7, iblockstate1, iblockstate1, false);
				this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 4, 2, 6, 4, 7, iblockstate2, iblockstate2, false);
				this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 8, 6, 4, 8, iblockstate3, iblockstate3, false);

				for (int i = 2; i <= 7; i += 5)
				{
					for (int j = 1; j <= 5; j += 4)
					{
						this.replaceAirAndLiquidDownwards(worldIn, Blocks.LOG.getDefaultState(), j, -1, i, structureBoundingBoxIn);
					}
				}

				if (!this.hasWitch)
				{
					int l = this.getXWithOffset(2, 5);
					int i1 = this.getYWithOffset(2);
					int k = this.getZWithOffset(2, 5);

					if (structureBoundingBoxIn.isVecInside(new BlockPos(l, i1, k)))
					{
						this.hasWitch = true;
						EntityWitch entitywitch = new EntityWitch(worldIn);
						entitywitch.enablePersistence();
						entitywitch.setLocationAndAngles((double) l + 0.5D, (double) i1, (double) k + 0.5D, 0.0F, 0.0F);
						entitywitch.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(l, i1, k)), (IEntityLivingData) null);
						worldIn.spawnEntity(entitywitch);
					}
				}

				return true;
			}
		}
	}
}
