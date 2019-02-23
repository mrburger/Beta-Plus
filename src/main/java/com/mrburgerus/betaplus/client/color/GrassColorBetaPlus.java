package com.mrburgerus.betaplus.client.color;

import com.mrburgerus.betaplus.world.alpha_plus.WorldTypeAlphaPlus;
import com.mrburgerus.betaplus.world.beta_plus.WorldTypeBetaPlus;
import com.mrburgerus.betaplus.world.biome.BiomeProviderBetaPlus;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.biome.BiomeColors;
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
		if (worldIn == null || pos == null)
			return GrassColors.get(0.5D, 1.0D);
		// If we are in an Alpha World
		if (Minecraft.getInstance().getIntegratedServer().getWorld(DimensionType.OVERWORLD).getWorldType() instanceof WorldTypeAlphaPlus)
		{
			//return ((BiomeProviderBetaPlusOld) provider).getGrassColor(pos);
			//return 9026389;
			//return 9043797;
			//return 9895680;
			//return 10802036;
			//return 7712841;
			//return 11131001;
			//return -65281;
			return 0xABFF67;
			/* Return -1 Makes grass gray */
		}
		if (Minecraft.getInstance().getIntegratedServer().getWorld(DimensionType.OVERWORLD).getWorldType() instanceof WorldTypeBetaPlus)
		{
			BiomeProviderBetaPlus provider = (BiomeProviderBetaPlus) Minecraft.getInstance().getIntegratedServer().getWorld(DimensionType.OVERWORLD).getWorld().getChunkProvider().getChunkGenerator().getBiomeProvider();
			/* Working */
			return provider.getGrassColorBeta(pos);
		}

		// Otherwise we are on default.
		return BiomeColors.getGrassColor(worldIn, pos);
	}

}
