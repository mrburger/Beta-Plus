package com.mrburgerUS.betaplus.beta.feature.decoration;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class WorldGenSnowLayerBeta
{
	public static void generateSnow(World world, ChunkPos chunkPos)
	{
		for (int x = 0; x < 16; x++)
		{
			for (int z = 0; z < 16; z++)
			{
				BlockPos pos = new BlockPos(chunkPos.x * 16 + x, 0, chunkPos.z * 16 + z);
				Biome biome = world.getBiome(pos);
				if (biome.isSnowyBiome())
				{
					int yVal = world.getPrecipitationHeight(new BlockPos(pos.getX(), 0, pos.getZ())).getY();
					world.setBlockState(new BlockPos(pos.getX(), yVal, pos.getZ()), Blocks.SNOW_LAYER.getDefaultState());
				}
			}
		}
	}

	//Ripped From Vanilla
	public boolean canSnowAtBlock(World world, BlockPos pos, boolean checkLight)
	{
		Biome biome = world.getBiome(pos);
		float f = biome.getTemperature(pos);

		if (f >= 0.15F)
		{
			return false;
		}
		else if (!checkLight)
		{
			return true;
		}
		else
		{
			if (pos.getY() >= 0 && pos.getY() < 256 && world.getLightFor(EnumSkyBlock.BLOCK, pos) < 10)
			{
				IBlockState iblockstate1 = world.getBlockState(pos);

				if (iblockstate1.getBlock().isAir(iblockstate1, world, pos) && Blocks.SNOW_LAYER.canPlaceBlockAt(world, pos))
				{
					return true;
				}
			}

			return false;
		}
	}
}
