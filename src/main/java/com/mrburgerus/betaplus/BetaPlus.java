package com.mrburgerus.betaplus;

import com.mrburgerus.betaplus.client.color.GrassColorBetaPlus;
import com.mrburgerus.betaplus.client.color.ReedColorBetaPlus;
import com.mrburgerus.betaplus.world.alpha_plus.WorldTypeAlphaPlus;
import com.mrburgerus.betaplus.world.beta_plus.WorldTypeBetaPlus;
import com.mrburgerus.betaplus.world.biome.BiomeProviderAlphaPlus;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
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

        // Register Client-side features
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSide);


		// Register ourselves for server, registry and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
		WorldTypeBetaPlus.register();
		WorldTypeAlphaPlus.register();
    }


    private void clientSide(final FMLLoadCompleteEvent event)
    {
    	/* Do Not Enable the Grass Color until Kinks worked out */
        Minecraft.getInstance().getBlockColors().register(new GrassColorBetaPlus(), Blocks.GRASS_BLOCK, Blocks.GRASS, Blocks.TALL_GRASS, Blocks.FERN, Blocks.LARGE_FERN);
        Minecraft.getInstance().getBlockColors().register(new ReedColorBetaPlus(), Blocks.SUGAR_CANE);
    }

    /* Turns on Perpetual Snow for Snowy Alpha Worlds */
    @SubscribeEvent
    public void setSnowy (final WorldEvent.Load event)
    {
        /* If World is Snowy */
        if (event.getWorld().getWorld().getWorldType() instanceof WorldTypeAlphaPlus)
        {
			BlockPos spawnBlock = event.getWorld().getWorld().getSpawnPoint();

        	if (event.getWorld().getWorld().getBiome(spawnBlock) == BiomeProviderAlphaPlus.snowBiome)
			{
				/* Turn off Weather, so it snows forever */
				event.getWorld().getWorld().getGameRules().setOrCreateGameRule("doWeatherCycle", "false", Minecraft.getInstance().getIntegratedServer());
				/* Turn on Snow! */
				event.getWorld().getWorldInfo().setRaining(true);
			}
        }
    }






}
