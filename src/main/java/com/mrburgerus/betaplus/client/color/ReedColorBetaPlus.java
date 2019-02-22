package com.mrburgerus.betaplus.client.color;

import com.mrburgerus.betaplus.world.beta_plus.WorldTypeBetaPlus;
import com.mrburgerus.betaplus.world.biome.BiomeProviderBetaPlus;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.biome.BiomeColors;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.dimension.DimensionType;

import javax.annotation.Nullable;

public class ReedColorBetaPlus implements IBlockColor
{
	/* It really is that simple */
	@Override
	public int getColor(IBlockState iBlockState, @Nullable IWorldReaderBase iWorldReaderBase, @Nullable BlockPos blockPos, int i)
	{
		if (Minecraft.getInstance().getIntegratedServer().getWorld(DimensionType.OVERWORLD).getWorldType() instanceof WorldTypeBetaPlus)
		{
			return -1;
		}
		else
		{
			return BiomeColors.getGrassColor(iWorldReaderBase, blockPos);
		}
	}
}
