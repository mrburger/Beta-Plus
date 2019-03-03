package com.mrburgerus.betaplus.client.color;

import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.world.alpha_plus.WorldTypeAlphaPlus;
import com.mrburgerus.betaplus.world.beta_plus.WorldTypeBetaPlus;
import com.mrburgerus.betaplus.world.beta_plus.BiomeProviderBetaPlus;
import com.mrburgerus.betaplus.world.beta_plus.sim.BetaPlusClimate;
import com.mrburgerus.betaplus.world.biome.BetaPlusBiome;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.biome.BiomeColors;
import net.minecraft.world.biome.provider.BiomeProvider;
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
			return -1; // Don't tint since we are using a pre-colored model.
			/* Return -1 Makes grass gray */
		}
		if (worldType instanceof WorldTypeBetaPlus)
		{
			/* Potential issues with Non-overworld Providers */
			/* Errors last I Checked */
			//BiomeProviderBetaPlus provider = (BiomeProviderBetaPlus) worldIn.getDimension().getWorld().getChunkProvider().getChunkGenerator().getBiomeProvider();
			//BiomeProvider provider = worldIn.getDimension().getWorld().getChunkProvider().getChunkGenerator().getBiomeProvider();
			if (true)
			{

				// FOR TESTING ONLY
				BetaPlusClimate climate = new BetaPlusClimate(worldIn.getDimension().getWorld(), 0.02500000037252903, 2);
				/* Working */
				double[] vals = climate.getClimateValuesatPos(pos);
				//return GrassColors.get(vals[0], vals[1]);
			}
		}

		return BiomeColors.getGrassColor(worldIn, pos);
	}

}
