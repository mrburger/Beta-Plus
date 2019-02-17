package com.mrburgerus.betaplus.world;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Arrays;

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
				int depth = ChunkGeneratorBetaPlus.seaLevel - y;
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
		double[][] newDepths = gaussian2D(depthValues, 1.65f	);


		// Now Process These Values
		for (int xV = 0; xV < newDepths.length; ++xV)
		{
			for (int zV = 0; zV < newDepths[xV].length; ++zV)
			{
				int y = ChunkGeneratorBetaPlus.getSolidHeightY(xStart + xV, zStart + zV, chunk);
				int yNew = ChunkGeneratorBetaPlus.seaLevel - (int) newDepths[xV][zV];
				if (yNew < y && y < ChunkGeneratorBetaPlus.seaLevel) //It is Deeper
				{
					// We Are "Underwater"
					for(int yV = y; yV > yNew; yV--)
					{
						chunk.setBlockState(new BlockPos(xStart + xV, yV, zStart + zV), Blocks.WATER.getDefaultState(), false);
					}
				}
			}
		}
		//System.out.println(Arrays.deepToString(depthValues));
	}

	// Working FEB 16, 2019.
	public static double[][] gaussian2D(double [][] arrayIn, float m)
	{
		double[][] arrayP = new double[arrayIn.length][arrayIn[0].length];
		float[][] matrix = { {1/(m * m * m * m), 1/(m * m * m), 1/(m * m * m * m)}, {1/(m * m * m), 1/(m * m), 1/(m * m * m)}, {1/(m * m * m * m), 1/(m * m * m), 1/(m * m * m * m)}};
		for (int r = 0; r < arrayIn.length; r++)
		{
			double topLeft = 0;
			double left = 0;
			double bottomLeft = 0;
			double top = 0;
			double bottom = 0;
			double topRight = 0;
			double right = 0;
			double bottomRight = 0;
			for (int c = 0; c < arrayIn[r].length; c++)
			{
				double center = arrayIn[r][c];
				// SIMPLE IF AND NASTY
				if (r == 0 && c == 0) // Top Left
				{
					topLeft = center;
					left = center;
					bottomLeft = center;
					top = center;
					topRight = center;
					bottom = arrayIn[r + 1][c];
					right = arrayIn[r][c + 1];
					bottomRight = arrayIn[r + 1][c + 1];
				}
				else if (r == 0 && c == arrayIn[r].length - 1) // Top Right
				{
					topLeft = center;
					top = center;
					topRight = center;
					right = center;
					bottomRight = center;
					left = arrayIn[r][c - 1];
					bottomLeft = arrayIn[r + 1][c - 1];
					bottom = arrayIn[r + 1][c];
				}
				else if (r == arrayIn.length - 1 && c == 0) // Bottom Left
				{
					topLeft = center;
					left = center;
					bottomLeft = center;
					bottom = center;
					bottomRight = center;
					top = arrayIn[r-1][c];
					topRight = arrayIn[r-1][c+1];
					right = arrayIn[r][c+1];
				}
				else if (r == arrayIn.length - 1 && c == arrayIn[r].length - 1) // Bottom Right
				{
					topLeft = arrayIn[r-1][c-1];
					left = arrayIn[r][c-1];
					bottomLeft = center;
					top = arrayIn[r-1][c];
					bottom = center;
					topRight = center;
					right = center;
					bottomRight = center;
				}
				else if (r == 0) // Top Row
				{
					topLeft = center;
					left = arrayIn[r][c-1];
					bottomLeft = arrayIn[r+1][c-1];
					top = center;
					bottom = arrayIn[r+1][c];
					topRight = center;
					right = arrayIn[r][c+1];
					bottomRight = arrayIn[r+1][c+1];
				}
				else if (r == arrayIn.length - 1) // Bottom Row
				{
					topLeft = arrayIn[r-1][c-1];
					left = arrayIn[r][c-1];
					bottomLeft = center;
					top = arrayIn[r-1][c];
					bottom = center;
					topRight = arrayIn[r-1][c+1];
					right = arrayIn[r][c+1];
					bottomRight = center;
				}
				else if (c == 0) // Left Column
				{
					topLeft = center;
					left = center;
					bottomLeft = center;
					top = arrayIn[r-1][c];
					bottom = arrayIn[r+1][c];
					topRight = arrayIn[r-1][c+1];
					right = arrayIn[r][c+1];
					bottomRight = arrayIn[r+1][c+1];
				}
				else if (c == arrayIn[r].length - 1) // Right Column
				{
					topLeft = arrayIn[r-1][c-1];
					left = arrayIn[r][c-1];
					bottomLeft = arrayIn[r+1][c-1];
					top = arrayIn[r-1][c];
					bottom = arrayIn[r+1][c];
					topRight = center;
					right = center;
					bottomRight = center;
				}
				else if (r > 0 && r < arrayIn.length - 1 && c > 0 && c < arrayIn[r].length - 1) // Center Value
				{
					topLeft = arrayIn[r-1][c-1];
					left = arrayIn[r][c-1];
					bottomLeft = arrayIn[r+1][c-1];
					top = arrayIn[r-1][c];
					bottom = arrayIn[r+1][c];
					topRight = arrayIn[r-1][c+1];
					right = arrayIn[r][c+1];
					bottomRight = arrayIn[r+1][c+1];
				}
				else
				{
					System.out.println("Encountered Gauss Error!");
				}
				// Now Process! (MAGIC NUMBERS OKAY, I DON'T CARE!)
				double processed = matrix[0][0] * topLeft + matrix[1][0] * left + matrix[2][0] * bottomLeft + matrix[0][1] * top + matrix[1][1] * center + matrix[2][1] * bottom + matrix[0][2] * topRight + matrix[1][2] * right + matrix[2][2] * bottomRight;
				arrayP[r][c] = processed;
			}
		}

		return arrayP;
	}
}
