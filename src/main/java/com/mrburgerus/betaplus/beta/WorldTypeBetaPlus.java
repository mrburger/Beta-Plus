package com.mrburgerus.betaplus.beta;

import net.minecraft.world.WorldType;

public class WorldTypeBetaPlus extends WorldType
{
	public static void register()
	{
		new WorldTypeBetaPlus();
	}

	private WorldTypeBetaPlus()
	{
		super("BETA_PLUS");
	}

	public WorldTypeBetaPlus(String name)
	{
		super(name);
	}


}
