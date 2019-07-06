package com.mrburgerus.betaplus.client;

import com.mrburgerus.betaplus.ServerProxy;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientProxy extends ServerProxy
{
	public ClientProxy()
	{}

	@Override
	public void init()
	{
	}

	//TODO: ADD FOR EACH LOOP TO SIMPLIFY ADDING RESOURCES
	@SubscribeEvent
	public void bakeAlphaModels(final ModelBakeEvent event)
	{
		// DO NOTHING
	}
}
