package com.mrburgerus.betaplus.world.beta_plus;

import com.mrburgerus.betaplus.world.biome.BiomeProviderBetaPlus;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
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
	public IChunkGenerator<?> createChunkGenerator(World world)
	{
		BetaPlusGenSettings settings = new BetaPlusGenSettings();
		return new ChunkGeneratorBetaPlus(world, new BiomeProviderBetaPlus(world), settings);
	}

	@Override
	public boolean hasInfoNotice()
	{
		return true;
	}
}
