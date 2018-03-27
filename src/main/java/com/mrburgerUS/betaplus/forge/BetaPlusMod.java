package com.mrburgerUS.betaplus.forge;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = BetaPlusMod.MODID, name = BetaPlusMod.NAME, version = BetaPlusMod.VERSION, acceptableRemoteVersions = "*")
public class BetaPlusMod
{
	//Definitions
	static final String MODID = "betaplus";
	static final String NAME = "Beta+";
	static final String VERSION = "0.2";

	//Event Loaders
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		WorldTypeBetaPlus.register();
	}
}
