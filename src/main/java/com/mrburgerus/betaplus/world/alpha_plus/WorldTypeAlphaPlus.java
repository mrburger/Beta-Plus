package com.mrburgerus.betaplus.world.alpha_plus;

import com.mrburgerus.betaplus.world.biome.BiomeProviderAlphaPlus;
import net.minecraft.init.Biomes;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.provider.BiomeProvider;
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
		AlphaPlusGenSettings settings = new AlphaPlusGenSettings();
		settings.setSnowy(true);
		return new ChunkGeneratorAlphaPlus(world, new BiomeProviderAlphaPlus(settings.getSnowy()), settings);
	}

	/* Since we have snow as an option, It has custom Options */
	@Override
	public boolean hasCustomOptions()
	{
		return true;
	}

	@Override
	public boolean hasInfoNotice()
	{
		return true;
	}
}
