package com.mrburgerus.betaplus;

import com.mrburgerus.betaplus.client.gui.CreateAlphaWorldScreen;
import com.mrburgerus.betaplus.world.alpha_plus.WorldTypeAlphaPlus;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ServerProxy
{
	public void init()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

}
