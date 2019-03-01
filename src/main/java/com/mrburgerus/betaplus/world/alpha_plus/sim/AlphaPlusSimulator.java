package com.mrburgerus.betaplus.world.alpha_plus.sim;

import com.mojang.datafixers.util.Pair;
import com.mrburgerus.betaplus.util.AbstractWorldSimulator;
import com.mrburgerus.betaplus.world.noise.NoiseGeneratorOctavesAlpha;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class AlphaPlusSimulator extends AbstractWorldSimulator
{

	protected AlphaPlusSimulator(World world)
	{
		super(world);
		this.octaves1 = new NoiseGeneratorOctavesAlpha(this.rand, 16);
		this.octaves2 = new NoiseGeneratorOctavesAlpha(this.rand, 16);
		this.octaves3 = new NoiseGeneratorOctavesAlpha(this.rand, 8);
		// These are created ONLY to make sure the generation is precisely the same
		// Could be used later to implement the "Only Spawn on Sand" feature.
		new NoiseGeneratorOctavesAlpha(this.rand, 4);
		new NoiseGeneratorOctavesAlpha(this.rand, 4);
		this.octaves4 = new NoiseGeneratorOctavesAlpha(this.rand, 10);
		this.octaves5 = new NoiseGeneratorOctavesAlpha(this.rand, 16);
	}

	@Override
	protected double[] generateOctaves(double[] values, int xChunkMult, int yValueZero, int zChunkMult, int size1, int size2, int size3)
	{
		return new double[0];
	}

	@Override
	protected int simulateYZeroZeroChunk(ChunkPos pos)
	{
		return 0;
	}

	@Override
	protected Pair<Integer[][], Boolean> simulateChunkYFast(ChunkPos pos)
	{
		return null;
	}

	@Override
	public Pair<Integer, Boolean> simulateYChunk(BlockPos pos)
	{
		return null;
	}

	@Override
	public Pair<Integer, Boolean> simulateYAvg(BlockPos pos)
	{
		return null;
	}
}
