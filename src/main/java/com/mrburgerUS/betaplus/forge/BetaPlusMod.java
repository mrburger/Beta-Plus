package com.mrburgerUS.betaplus.forge;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = BetaPlusMod.MODID, name = BetaPlusMod.NAME, version = BetaPlusMod.VERSION)
public class BetaPlusMod
{
	//Definitions
	static final String MODID = "betaplus";
	static final String NAME = "Beta Terrain Plus";
	static final String VERSION = "0.1";

	//Event Loaders
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		WorldTypeBetaPlus.register();
	}
}
