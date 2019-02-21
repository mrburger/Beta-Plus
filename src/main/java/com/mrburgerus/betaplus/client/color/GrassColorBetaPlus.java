package com.mrburgerus.betaplus.client.color;

import com.mrburgerus.betaplus.world.biome.BiomeProviderBetaPlusOld;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.dimension.DimensionType;

import javax.annotation.Nullable;

public class GrassColorBetaPlus implements IBlockColor
{
	@Override
	public int getColor(IBlockState iBlockState, @Nullable IWorldReaderBase worldIn, @Nullable BlockPos pos, int i)
	{
		if (worldIn == null || pos == null)
			return GrassColors.get(0.5D, 1.0D);
		// Modified (It's so long!)
		BiomeProvider provider = Minecraft.getInstance().getIntegratedServer().getWorld(DimensionType.OVERWORLD).getChunkProvider().chunkGenerator.getBiomeProvider();
		// If we are in a Beta+ world
		if (provider instanceof BiomeProviderBetaPlusOld)
		{
			//return ((BiomeProviderBetaPlusOld) provider).getGrassColor(pos);
			return -1;
		}

		return 0;
	}

}
