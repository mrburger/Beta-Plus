package com.mrburgerus.betaplus.world;

import com.mrburgerus.betaplus.util.BetaConvolve;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

public class BetaPlusDeepenOcean
{
	public static void deepenOcean(IChunk chunk)
	{
		// Get X and Z start
		int xStart = chunk.getPos().getXStart();
		int zStart = chunk.getPos().getZStart();

		// Create 2-D Map of Y-Depth in Chunk
		double[][] depthValues = new double[16][16];
		for (int xV = 0; xV < depthValues.length; ++xV)
		{
			for (int zV = 0; zV < depthValues[xV].length; ++zV)
			{
				int y = ChunkGeneratorBetaPlus.getSolidHeightY(xStart + xV, zStart + zV, chunk);
				int depth = (ChunkGeneratorBetaPlus.seaLevel - y) - 1; // Depth is -1 because of lowered sea level.
				depthValues[xV][zV] = depth;
			}
		}
		// Process these values by applying a transform, with no randomness
		for (int xV = 0; xV < depthValues.length; ++xV)
		{
			for (int zV = 0; zV < depthValues[xV].length; ++zV)
			{
				depthValues[xV][zV] = depthValues[xV][zV] * 1.55;
			}
		}
		// Gaussian BLUR
		double[][] newDepths = BetaConvolve.convolve2DSquare(depthValues, 5, 2f);


		// Now Process These Values
		for (int xV = 0; xV < newDepths.length; ++xV)
		{
			for (int zV = 0; zV < newDepths[xV].length; ++zV)
			{
				int y = ChunkGeneratorBetaPlus.getSolidHeightY(xStart + xV, zStart + zV, chunk);
				int yNew = ChunkGeneratorBetaPlus.seaLevel - (int) newDepths[xV][zV];
				if (yNew < y && y < ChunkGeneratorBetaPlus.seaLevel) // We are Deep, yo.
				{
					// We Are "Underwater"
					for(int yV = y; yV > yNew; --yV)
					{
						chunk.setBlockState(new BlockPos(xStart + xV, yV, zStart + zV), Blocks.WATER.getDefaultState(), false);
					}
				}
			}
		}
	}
}
