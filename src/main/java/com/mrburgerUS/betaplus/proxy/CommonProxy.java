package com.mrburgerUS.betaplus.proxy;

import com.mrburgerUS.betaplus.forge.WorldTypeBetaPlus;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
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
}