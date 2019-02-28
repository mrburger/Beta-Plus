package com.mrburgerus.betaplus.util;

import net.minecraft.util.math.BlockPos;

/* Just an Interface so I remember to implement Methods */
public interface IWorldSimulator
{

	/* Simulates a Single Y (Quickly) in chunk */
	public int simulateYSingle(BlockPos pos);

	/* Simulates Y at (0, 0) in Chunk. */
	public int simulateYZeroZeroChunk(int chunkX, int chunkZ);

	/* Generate the Noise Octaves for the Generator to Use */
	/* yValueZero is ALWAYS ZERO */
	public double[] generateOctaves(double[] values, int xChunkMult, int yValueZero, int zChunkMult, int size1, int size2, int size3);
}
