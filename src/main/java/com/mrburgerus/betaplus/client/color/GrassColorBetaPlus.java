package com.mrburgerus.betaplus.client.color;

import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.world.alpha_plus.WorldTypeAlphaPlus;
import com.mrburgerus.betaplus.world.beta_plus.WorldTypeBetaPlus;
import com.mrburgerus.betaplus.world.beta_plus.BiomeProviderBetaPlus;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeColors;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class GrassColorBetaPlus implements IBlockColor
{
	@Override
	public int getColor(IBlockState iBlockState, @Nullable IWorldReaderBase worldIn, @Nullable BlockPos pos, int i)
	{
		/* Catch all null cases */
		if (worldIn == null || pos == null)
		{
			return GrassColors.get(0.5D, 1.0D);
		}

		WorldType worldType = worldIn.getDimension().getWorld().getWorldType();

		// If we are in an Alpha World
		if (worldType instanceof WorldTypeAlphaPlus)
		{
			//return 9026389;
			//return 9043797;
			//return 9895680;
			//return 10802036;
			//return 7712841;
			//return 11131001;
			//return -65281;
			//return 0xABFF67;
			//return 0x32CD32;
			//return 0xA9D879;
			return -1; // Don't tint since we are using a pre-colored model2.
			/* Return -1 Makes grass gray */
		}
		if (worldType instanceof WorldTypeBetaPlus)
		{
			/* Potential issues with Non-overworld Providers */
			BiomeProviderBetaPlus provider = (BiomeProviderBetaPlus) worldIn.getDimension().getWorld().getChunkProvider().getChunkGenerator().getBiomeProvider();
			/* Working */
			return provider.getGrassColorBeta(pos);
		}

		return BiomeColors.getGrassColor(worldIn, pos);
	}

}
