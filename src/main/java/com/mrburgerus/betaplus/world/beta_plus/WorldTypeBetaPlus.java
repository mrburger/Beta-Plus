package com.mrburgerus.betaplus.world.beta_plus;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.IChunkGenerator;

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
	public IChunkGenerator<?> createChunkGenerator(World world)
	{
		BetaPlusGenSettings settings = new BetaPlusGenSettings();
		if (world.dimension.getType() != DimensionType.OVERWORLD)
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
