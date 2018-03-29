package com.mrburgerUS.betaplus.beta.biome.color;

import com.mrburgerUS.betaplus.beta.BiomeProviderBeta;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeProvider;

import javax.annotation.Nullable;

public class GrassColorBeta implements IBlockColor
{
	@Override
	public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)
	{
		if (worldIn == null || pos == null)
			return ColorizerFoliage.getFoliageColor(0.5D, 1.0D);
		BiomeProvider provider = Minecraft.getMinecraft().world.getBiomeProvider();
		if (provider instanceof BiomeProviderBeta)
		{
			return ((BiomeProviderBeta) provider).getGrassColor2(pos);
		}

		return 0;
	}
}
