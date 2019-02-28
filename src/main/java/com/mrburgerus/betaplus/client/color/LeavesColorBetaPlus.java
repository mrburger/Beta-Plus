package com.mrburgerus.betaplus.client.color;

import com.mrburgerus.betaplus.world.alpha_plus.WorldTypeAlphaPlus;
import com.mrburgerus.betaplus.world.beta_plus.WorldTypeBetaPlus;
import com.mrburgerus.betaplus.world.beta_plus.BiomeProviderBetaPlus;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeColors;
import net.minecraft.world.dimension.DimensionType;

import javax.annotation.Nullable;

public class LeavesColorBetaPlus implements IBlockColor
{
	@Override
	public int getColor(IBlockState iBlockState, @Nullable IWorldReaderBase worldIn, @Nullable BlockPos pos, int i)
	{
		if (worldIn == null || pos == null)
			return GrassColors.get(0.5D, 1.0D);

		WorldType worldType = worldIn.getDimension().getWorld().getWorldType();

		// If we are in an Alpha World
		if (worldType instanceof WorldTypeAlphaPlus)
		{
			//return 0xA9D879;
			return -1;
			/* Return -1 Makes grass gray, which means no tint! */
		}
		if (worldType instanceof WorldTypeBetaPlus)
		{
			BiomeProviderBetaPlus provider = (BiomeProviderBetaPlus) worldIn.getDimension().getWorld().getChunkProvider().getChunkGenerator().getBiomeProvider();
			/* Working */
			return provider.getGrassColorBeta(pos);
		}

		// Otherwise we are on default.
		return BiomeColors.getFoliageColor(worldIn, pos);
	}
}
