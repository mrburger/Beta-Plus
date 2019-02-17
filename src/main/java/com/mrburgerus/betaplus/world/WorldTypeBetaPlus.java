package com.mrburgerus.betaplus.world;

import com.mrburgerus.betaplus.world.biome.BiomeProviderBetaPlus;
import net.minecraft.init.Biomes;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;
import net.minecraft.world.gen.ChunkGeneratorFlat;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;

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
		// TODO: BETTER BIOME PROVIDER
		return new ChunkGeneratorBetaPlus(world, new BiomeProviderBetaPlus(world));
	}

}
