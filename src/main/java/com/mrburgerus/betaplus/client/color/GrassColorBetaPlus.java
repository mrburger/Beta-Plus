package com.mrburgerus.betaplus.client.color;

import com.mrburgerus.betaplus.world.beta_plus.BiomeProviderBetaPlus;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.dimension.DimensionType;

public class GrassColorBetaPlus implements IBlockColor
{
	@Override
	public int getColor(BlockState blockState, IEnviromentBlockReader iEnviromentBlockReader, BlockPos blockPos, int i)
	{
		if (iEnviromentBlockReader == null || blockPos == null)
			return GrassColors.get(0.5D, 1.0D);
		// Modified (It's so long!)
		BiomeProvider provider = Minecraft.getInstance().getIntegratedServer().getWorld(DimensionType.OVERWORLD).getChunkProvider().generator.getBiomeProvider();
		// If we are in a Beta+ world
		if (provider instanceof BiomeProviderBetaPlus)
		{
			//return ((BiomeProviderBetaPlusOld) provider).getGrassColor(pos);
			return -1;
		}

		return 0;
	}
}
