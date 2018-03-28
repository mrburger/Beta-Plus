package com.mrburgerUS.betaplus.beta.biome.color;

import com.mrburgerUS.betaplus.beta.BiomeProviderBeta;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeProvider;

import javax.annotation.Nullable;

public class LeavesColorBeta implements IBlockColor
{

	@Override
	public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)
	{
		BlockPlanks.EnumType variantType = state.getValue(BlockOldLeaf.VARIANT);
		if (variantType == BlockPlanks.EnumType.BIRCH)
		{
			return ColorizerFoliage.getFoliageColorBirch();
		}
		else if (variantType == BlockPlanks.EnumType.SPRUCE)
		{
			return ColorizerFoliage.getFoliageColorPine();
		}
		else if (variantType == BlockPlanks.EnumType.OAK)
		{
			BiomeProvider provider = Minecraft.getMinecraft().world.getBiomeProvider();
			if (provider instanceof BiomeProviderBeta && pos != null)
			{
				return ColorizerFoliage.getFoliageColor(0.5, 0.5);
			}
		}
		else
		{
			return ColorizerFoliage.getFoliageColorBasic();
		}
		return 0;
	}
}
