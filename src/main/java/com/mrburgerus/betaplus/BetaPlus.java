package com.mrburgerus.betaplus;

import com.mrburgerus.betaplus.world.beta_plus.WorldTypeBetaPlus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("betaplus")
public class BetaPlus
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public BetaPlus() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);


        // Register ourselves for server, registry and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
		WorldTypeBetaPlus.register();
    }


}
