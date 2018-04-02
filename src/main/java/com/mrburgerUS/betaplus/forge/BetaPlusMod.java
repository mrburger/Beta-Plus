package com.mrburgerUS.betaplus.forge;

import com.mrburgerUS.betaplus.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = BetaPlusMod.MODID, name = BetaPlusMod.NAME, version = BetaPlusMod.VERSION, acceptableRemoteVersions = "*")
public class BetaPlusMod
{
	//Definitions
	static final String MODID = "betaplus";
	static final String NAME = "Beta+";
	static final String VERSION = "0.2.1";

	@SidedProxy(clientSide = "com.mrburgerUS.betaplus.proxy.ClientProxy", serverSide = "com.mrburgerUS.betaplus.proxy.ServerProxy")
	public static CommonProxy proxy;

	//Event Loaders
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init(event);
	}

	@Mod.EventHandler
	public void init(FMLPostInitializationEvent event)
	{
		proxy.postInit(event);
	}

}
