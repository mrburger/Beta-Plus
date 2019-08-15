package com.mrburgerus.betaplus.client.color;

import com.mrburgerus.betaplus.world.beta_plus.BiomeProviderBetaPlus;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.biome.BiomeColors;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.dimension.DimensionType;

public class ReedColorBetaPlus implements IBlockColor
{
	/* It really is that simple */
	@Override
	public int getColor(BlockState blockState, IEnviromentBlockReader iEnviromentBlockReader, BlockPos blockPos, int i)
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
}
