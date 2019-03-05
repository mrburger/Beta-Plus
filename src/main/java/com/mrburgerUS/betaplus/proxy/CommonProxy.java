package com.mrburgerUS.betaplus.proxy;

import com.mrburgerUS.betaplus.beta_plus.biome.support.SupportBiome;
import com.mrburgerUS.betaplus.forge.WorldTypeBetaPlus;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy
{
	public void preInit(FMLPreInitializationEvent event)
	{
		WorldTypeBetaPlus.register();
	}

	public void init(FMLInitializationEvent event)
	{

	}

	public void postInit(FMLPostInitializationEvent event)
	{
		SupportBiome.init();
	}
}
