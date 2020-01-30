package com.mrburgerus.betaplus.client.color;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraft.world.biome.provider.BiomeProvider;

import javax.annotation.Nullable;

public class ReedColorBetaPlus implements IBlockColor
{
	/* This will work for now */
	@Override
	public int getColor(BlockState blockState, @Nullable ILightReader environment, @Nullable BlockPos blockPos, int i)
	{
		return -1;
	}


	/* It really is that simple */
	/*
	@Override
	public int getColor(BlockState blockState, EnvironmentBlock iEnviromentBlockReader, BlockPos blockPos, int i)
	{
		BiomeProvider provider = Minecraft.getInstance().getIntegratedServer().getWorld(DimensionType.OVERWORLD).getChunkProvider().generator.getBiomeProvider();
		if (provider instanceof BiomeProviderBetaPlus)
		{
			return -1;
		}
		else
		{
			return BiomeColors.getGrassColor(iEnviromentBlockReader, blockPos);
		}
	}
	*/

}
