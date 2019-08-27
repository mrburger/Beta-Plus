package com.mrburgerus.betaplus;

import com.mrburgerus.betaplus.world.alpha_plus.WorldTypeAlphaPlus;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ServerProxy
{
	public void init()
	{
		MinecraftForge.EVENT_BUS.register(this);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setAlphaSnow);
	}

	/* Turns on Perpetual Snow for Snowy Alpha Worlds */
	public void setAlphaSnow(final WorldEvent.Load event)
	{
		/* If World is Snowy */
		WorldType worldType = event.getWorld().getWorld().getWorldType();
		if (worldType instanceof WorldTypeAlphaPlus)
		{
        	/*
        	BetaPlus.LOGGER.info("In Alpha");
        	if (event.getWorld().getWorld().getChunkProvider().getChunkGenerator() instanceof ChunkGeneratorAlphaPlus)
			{
				BetaPlus.LOGGER.info("Passed step 1");
				/* Turn off Weather, so it snows forever */
			//event.getWorld().getWorld().getWorldInfo().getGameRulesInstance().setOrCreateGameRule("doWeatherCycle", "false", null);
			/* Turn on Snow! */
			//event.getWorld().getWorldInfo().setRaining(true);
		}
		else
		{
			//BetaPlus.LOGGER.info("Not an alpha world.");
		}
	}
}
