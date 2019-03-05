package com.mrburgerUS.betaplus.forge;

import com.mrburgerUS.betaplus.beta_plus.BiomeProviderBeta;
import com.mrburgerUS.betaplus.beta_plus.ChunkGeneratorBeta;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldTypeBetaPlus extends WorldType
{
	public static void register()
	{
		System.out.println("Registering Beta+");
		new WorldTypeBetaPlus();
	}

	private WorldTypeBetaPlus()
	{
		super("BETA_PLUS");
	}

	protected WorldTypeBetaPlus(String name)
	{
		super(name);
	}

	public IChunkGenerator getChunkGenerator(World world, String generatorOptions)
	{
		return new ChunkGeneratorBeta(world, world.getSeed(), generatorOptions);
	}

	//Added to fix biome structure issues
	@Override
	public BiomeProvider getBiomeProvider(World world)
	{
		return new BiomeProviderBeta(world);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasInfoNotice()
	{
		return true;
	}

	@Override
	public boolean isCustomizable()
	{
		return false;
	}
}
