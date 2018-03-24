package com.mrburgerUS.betaplus.forge;

import com.mrburgerUS.betaplus.beta.ChunkProviderBeta;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldTypeBetaPlus extends WorldType
{
	public static void register()
	{
		new WorldTypeBetaPlus();
	}

	private WorldTypeBetaPlus()
	{
		super("beta_plus");
	}

	protected WorldTypeBetaPlus(String name)
	{
		super(name);
	}

	public IChunkGenerator getChunkGenerator(World world, String generatorOptions)
	{
		return new ChunkProviderBeta(world, world.getSeed(), true);
	}
}
