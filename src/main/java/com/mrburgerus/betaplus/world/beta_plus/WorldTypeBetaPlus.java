package com.mrburgerus.betaplus.world.beta_plus;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;

public class WorldTypeBetaPlus extends WorldType
{
	public WorldTypeBetaPlus(String name)
	{
		super(name);
	}

	// Register World Type
	public static void register()
	{
		new WorldTypeBetaPlus("BETA_PLUS");
	}


	@Override
	@SuppressWarnings("deprecation")
	public ChunkGenerator<?> createChunkGenerator(World world)
	{
		BetaPlusGenSettings settings = new BetaPlusGenSettings();
		// Overworld
		if (world.dimension.getType() != DimensionType.field_223227_a_)
		{
			return world.dimension.createChunkGenerator();
		}
		return new ChunkGeneratorBetaPlus(world, new BiomeProviderBetaPlus(world, settings), settings);
	}


	@Override
	public boolean hasInfoNotice()
	{
		return true;
	}
}
