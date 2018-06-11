package com.mrburgerus.betaplus;

import com.mrburgerus.betaplus.proxy.CommonProxy;
import com.mrburgerus.betaplus.util.BetaPlusValues;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = BetaPlusValues.MODID, name = BetaPlusValues.NAME, acceptableRemoteVersions = "*")
public class BetaPlus
{
	@SidedProxy(clientSide = "com.mrburgerus.betaplus.proxy.ClientProxy", serverSide = "com.mrburgerus.betaplus.proxy.ServerProxy")
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
