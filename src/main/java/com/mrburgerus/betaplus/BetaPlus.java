package com.mrburgerus.betaplus;

import com.mrburgerus.betaplus.client.ClientProxy;
import com.mrburgerus.betaplus.util.ConfigBetaPlus;
import com.mrburgerus.betaplus.world.alpha_plus.WorldTypeAlphaPlus;
import com.mrburgerus.betaplus.world.beta_plus.WorldTypeBetaPlus;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml_old file
@Mod("betaplus")
public class BetaPlus
{
	//Fields
	public static final String MOD_NAME = "betaplus";

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public static ServerProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public BetaPlus()
	{
		// Register Biomes
		//FMLJavaModLoadingContext.get().getModEventBus().addListener(RegisterAlphaBiomes::register);

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::config);

		// Register ourselves for server, registry and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);

		// Register the configuration file
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigBetaPlus.SPEC);

		proxy.init();

		// TESTING BIOMES O PLENTY (WORKS)
		/*
		if (ModList.get().isLoaded("biomesoplenty"))
		{
			LOGGER.debug("BOP LOADED");
		}
		else
		{
			LOGGER.debug("BOP MISSING");
		}
		*/
    }

    public void config(ModConfig.ModConfigEvent event)
	{
		if (event.getConfig().getSpec() == ConfigBetaPlus.SPEC)
		{
			ConfigBetaPlus.bake();
		}
	}

    private void setup(final FMLCommonSetupEvent event)
    {
		WorldTypeBetaPlus.register();
		WorldTypeAlphaPlus.register();
    }

    /* Turns on Perpetual Snow for Snowy Alpha Worlds */
    public void setAlphaSnow(final WorldEvent.Load event)
    {
        /* If World is Snowy */
		WorldType worldType = event.getWorld().getWorldInfo().getGenerator();
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
