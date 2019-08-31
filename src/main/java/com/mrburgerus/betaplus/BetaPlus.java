package com.mrburgerus.betaplus;

import com.mrburgerus.betaplus.client.ClientProxy;
import com.mrburgerus.betaplus.util.ConfigRetroPlus;
import com.mrburgerus.betaplus.world.alpha_plus.WorldTypeAlphaPlus;
import com.mrburgerus.betaplus.world.beta_plus.WorldTypeBetaPlus;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO
// Combine Beta+ and Alpha+ Methods more effectively
// Create a BiomeProviderRetro that houses all common functions
// Create a ChunkGeneratorRetro that houses all common functions


// The value here should match an entry in the META-INF/mods.toml_old file
@Mod(BetaPlus.MOD_NAME)
@Mod.EventBusSubscriber
public class BetaPlus
{
	//Fields
	public static final String MOD_NAME = "betaplus";

	// Directly reference a log4j logger.
	public static final Logger LOGGER = LogManager.getLogger();

	public static ServerProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

	public BetaPlus()
	{
		proxy.init();
		// Register ourselves for server, registry and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);

		// Register the setup method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setAlphaSnow);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::config);

		// Register the configuration file
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigRetroPlus.SPEC);
	}

	@SubscribeEvent
	public void config(final ModConfig.ModConfigEvent event)
	{
		if (event.getConfig().getSpec() == ConfigRetroPlus.SPEC)
		{
			ConfigRetroPlus.bake();
		}
	}

	@SubscribeEvent
	public void setup(final FMLCommonSetupEvent event)
	{
		WorldTypeBetaPlus.register();
		WorldTypeAlphaPlus.register();
	}

	/* Turns on Perpetual Snow for Snowy Alpha Worlds */
	@SubscribeEvent
	public void setAlphaSnow(final WorldEvent.Load event)
	{
		/* If World is Snowy */
		WorldType worldType = event.getWorld().getWorldInfo().getGenerator(); //event.getWorld().getWorld().getWorldType();
		if (worldType instanceof WorldTypeAlphaPlus)
		{
			WorldInfo info = event.getWorld().getWorldInfo();
			if (info.getGeneratorOptions().getBoolean(WorldTypeAlphaPlus.SNOW_WORLD_TAG))
			{
				// Check if this is perpetual.
				info.setRaining(true);
				// Added
				info.setThundering(false);
				//info.setRainTime(0);
			}
		}
	}
}
