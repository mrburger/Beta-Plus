package com.mrburgerus.betaplus.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.math.BlockPos;

/* Just an Interface so I remember to implement Methods */
public interface IWorldSimulator
{


	/* Simulates a Y (Quickly) in chunk, usually by generating values every 4 blocks and Averaging */
	/* Return: The Y average and whether any values fall above sea level */
	Pair<Integer, Boolean> simulateYChunk(BlockPos pos);

	/* Simulates an "Averaged" Value, which is usually 3x3 chunks. */
	/* Return: The Y average and whether any values fall above sea level */
	Pair<Integer, Boolean> simulateYAvg(BlockPos pos);

	/* Simulates Y at (0, 0) in Chunk. */
	int simulateYZeroZeroChunk(int chunkX, int chunkZ);

	/* Generate the Noise Octaves for the Generator to Use */
	/* yValueZero is ALWAYS ZERO */
	double[] generateOctaves(double[] values, int xChunkMult, int yValueZero, int zChunkMult, int size1, int size2, int size3);

	/* Simulate Every 4 Blocks in a Chunk */
	int[][] simulateChunkYFast(int chunkX, int chunkZ);

}
