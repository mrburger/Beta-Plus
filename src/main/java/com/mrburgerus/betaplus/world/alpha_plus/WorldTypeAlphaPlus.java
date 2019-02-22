package com.mrburgerus.betaplus.world.alpha_plus;

import com.mrburgerus.betaplus.world.biome.BiomeProviderAlphaPlus;
import net.minecraft.init.Biomes;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldTypeAlphaPlus extends WorldType
{
	public WorldTypeAlphaPlus(String alpha)
	{
		super(alpha);
	}

	// Register World Type
	public static void register()
	{
		new WorldTypeAlphaPlus("ALPHA");
	}

	@Override
	public IChunkGenerator<?> createChunkGenerator(World world)
	{
		// For Testing
		return new ChunkGeneratorAlphaPlus(world, new BiomeProviderAlphaPlus(Biomes.PLAINS));
	}

	@Override
	public boolean hasInfoNotice()
	{
		return true;
	}
}
