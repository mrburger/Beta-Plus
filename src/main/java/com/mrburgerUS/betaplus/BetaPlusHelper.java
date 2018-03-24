package com.mrburgerUS.betaplus;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public final class BetaPlusHelper
{
	public static boolean isAirBlock(World world, int x, int y, int z)
	{
		return world.isAirBlock(new BlockPos(x, y, z));
	}

	public static IBlockState getBlockState(World world, int x, int y, int z)
	{
		return world.getBlockState(new BlockPos(x, y, z));
	}

	public static Block getBlock(World world, int x, int y, int z)
	{
		return BetaPlusHelper.getBlockState(world, x, y, z).getBlock();
	}

	public static Material getBlockMaterial(World world, int x, int y, int z)
	{
		return BetaPlusHelper.getBlockState(world, x, y, z).getMaterial();
	}

	public static TileEntity getBlockTileEntity(World world, int x, int y, int z)
	{
		return world.getTileEntity(new BlockPos(x, y, z));
	}

	public static int getSavedLightValue(World world, EnumSkyBlock type, int x, int y, int z)
	{
		return world.getLightFor(type, new BlockPos(x, y, z));
	}

	public static boolean canBlockStay(World world, int x, int y, int z, BlockBush block)
	{
		return BetaPlusHelper.canBlockStay(world, x, y, z, block, block.getDefaultState());
	}

	public static boolean canBlockStay(World world, int x, int y, int z, BlockBush block, IBlockState state)
	{
		return block.canBlockStay(world, new BlockPos(x, y, z), state);
	}

	public static void setBlock(World world, int x, int y, int z, Block block)
	{
		BetaPlusHelper.setBlockWithNotify(world, x, y, z, block);
	}

	public static void setBlock(World world, int x, int y, int z, Block block, BlockPlanks.EnumType type)
	{
		BetaPlusHelper.setBlockState(world, x, y, z, block.getStateFromMeta(type.getMetadata()));
	}

	public static void setBlockWithNotify(World world, int x, int y, int z, Block block)
	{
		BetaPlusHelper.setBlockStateWithNotify(world, new BlockPos(x, y, z), block.getDefaultState());
	}

	public static void setBlockWithNotify(World world, BlockPos pos, Block block)
	{
		BetaPlusHelper.setBlockStateWithNotify(world, pos, block.getDefaultState());
	}

	public static void setBlockStateWithNotify(World world, BlockPos pos, IBlockState state)
	{
		BetaPlusHelper.setBlockState(world, pos, state);
	}

	public static void setBlockState(World world, int x, int y, int z, IBlockState state)
	{
		BetaPlusHelper.setBlockState(world, new BlockPos(x, y, z), state);
	}

	public static void setBlockState(World world, BlockPos pos, IBlockState state)
	{
		world.setBlockState(pos, state, 2);
	}

	public static int getHeightValue(World world, int x, int z)
	{
		return world.getHeight(new BlockPos(x, 0, z)).getY();
	}
}
