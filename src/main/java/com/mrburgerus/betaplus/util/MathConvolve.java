package com.mrburgerus.betaplus.util;

public class MathConvolve
{
	/* Convolves a Square 2D Matrix. */
	/* KernelSize MUST BE ODD */
	/* baseMult is typically 2 for Gaussian */

	public static double[][] convolve2DSquare(double[][] arrayIn, int kernelSize, double baseMult)
	{
		//Verify it is square AND kernel size ODD
		if(!verifyRect(arrayIn, true) || kernelSize % 2 == 0)
		{
			return new double[0][0];
		}
		double[][] gaussArr = initGaussSquare(kernelSize, baseMult);
		double[][] processedArr = new double[arrayIn.length][arrayIn.length];
		double[][] expandedArr = buildExpandedArray(arrayIn, kernelSize);
		int centerK = (kernelSize - 1) / 2;
		// Process the values
		for (int r = 0; r < processedArr.length; r++)
		{
			for (int c = 0; c < processedArr.length; c++)
			{
				processedArr[r][c] = sumKernel(expandedArr, gaussArr, r + centerK, c + centerK);
			}
		}
		return processedArr;
	}

	/* Verifies Array is Rectangular, or Square if isSquare is True */
	public static boolean verifyRect(double[][] arrayIn, boolean isSquare)
	{
		// Iterate through the second array to verify the length of each column array
		int columnLength = arrayIn[0].length; // Set to first column value
		if (isSquare)
		{
			columnLength = arrayIn.length; // Reset so we can verify square by comparing the two values.
		}
		for (double[] colArr : arrayIn)
		{
			if (colArr.length != columnLength) // There is a discrepancy
			{
				return false;
			}
		}
		return true;
	}

	/* Initializes a Gaussian Blur Square */
	/* Fixed Feb 20, 2019 (Incorrect Implementation) */
	private static double[][] initGaussSquare(int kernelSize, double sigma)
	{
		int centerK = (kernelSize - 1) / 2; // Center Position of Kernel
		double[][] gaussRet = new double[kernelSize][kernelSize];
		double sumElem = 0;
		for (int r = 0; r < gaussRet.length; r++)
		{
			for (int c = 0; c < gaussRet[r].length; c++)
			{
				// X and Y distance
				int dX2 = (r - centerK) * (r - centerK);
				int dY2 = (c - centerK) * (c - centerK);
				int dist = dX2 + dY2;
				// Standard Gaussian Distribution
				gaussRet[r][c] = Math.exp(-dist / (2 * sigma * sigma));
				sumElem += gaussRet[r][c];
			}
		}

		double[][] out = new double[gaussRet.length][gaussRet[0].length];
		for (int r = 0; r < gaussRet.length; r++)
		{
			for (int c = 0; c < gaussRet[r].length; c++)
			{
				out[r][c] = gaussRet[r][c] / sumElem;
			}
		}

		return out;
	}

	/* Expands a Square Array with Edges */
	private static double[][] buildExpandedArray(double[][] squareArrayIn, int kernelSize)
	{
		int centerK = (kernelSize - 1) / 2;
		// Multiply centerK by 2 since we have 2 sides to expand
		double[][] expandRet = new double[squareArrayIn.length + centerK * 2][squareArrayIn.length + centerK * 2];
		// Fill original array into center of Expanded
		int sideLength = squareArrayIn.length;
		for (int r = 0; r < sideLength; r++) // Can use the Original Array as the bounds
		{
			for (int c = 0; c < sideLength; c++)
			{
				expandRet[r + centerK][c + centerK] = squareArrayIn[r][c];
			}
		}
		// Now fill edges based on original
		for (int r = 0; r < expandRet.length; r++)
		{
			for (int c = 0; c < expandRet[r].length; c++)
			{
				int oldR = r - centerK;
				int oldC = c - centerK;
				try
				{
					expandRet[r][c] = squareArrayIn[oldR][oldC];
				}
				catch (ArrayIndexOutOfBoundsException e) // If value undefined
				{
					if (oldR < 0) // Top
					{
						if (oldC < 0) // Top Left
						{
							expandRet[r][c] = squareArrayIn[0][0];
						}
						else if (oldC >= sideLength) // Top Right (Greater or equal instead of -1)
						{
							expandRet[r][c] = squareArrayIn[0][sideLength - 1]; // Top Right Cell
						}
						else
						{
							expandRet[r][c] = squareArrayIn[0][oldC];
						}
					}
					else if (oldR >= sideLength) // Bottom
					{
						if (oldC < 0) // Bottom Left
						{
							expandRet[r][c] = squareArrayIn[sideLength - 1][0];
						}
						else if (oldC >= sideLength) // Bottom Right (Greater or equal instead of -1)
						{
							expandRet[r][c] = squareArrayIn[sideLength - 1][sideLength - 1]; // Bottom Right Cell
						}
						else
						{
							expandRet[r][c] = squareArrayIn[sideLength - 1][oldC];
						}
					}
					else if (oldC < 0) // Left
					{
						// Skip Corner Cases because they're already handled.
						expandRet[r][c] = squareArrayIn[oldR][0];
					}
					else if (oldC >= sideLength) // Right
					{
						expandRet[r][c] = squareArrayIn[oldR][sideLength - 1];
					}
					else // Catches Invalid
					{
						return new double[0][0];
					}
				}
			}
		}
		return expandRet;
	}

	/* Sums values in a Kernel */
	private static double sumKernel(double[][] totalArray, double[][] gauss, int rPos, int cPos)
	{
		// Center on rPos, cPos
		double summed = 0;
		int centerK = (gauss.length - 1) / 2; // EXACTLY THE SAME AS kernelSize
		int rV = 0; // Position in gauss matrix
		int cV = 0; // Position in gauss matrix, column
		for (int r = rPos - centerK; r <= rPos + centerK; r++) // Must be <= to reach edge
		{
			cV = 0;
			for (int c = cPos - centerK; c <= cPos + centerK; c++)
			{
				summed += (totalArray[r][c] * gauss[rV][cV]);
				cV++;
			}
			rV++;
		}
		return summed;
	}
}
