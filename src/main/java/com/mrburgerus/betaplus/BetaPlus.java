package com.mrburgerus.betaplus;

import com.mrburgerus.betaplus.client.ClientProxy;
import com.mrburgerus.betaplus.client.color.ColorRegister;
import com.mrburgerus.betaplus.client.gui.GuiBetaNumber;
import com.mrburgerus.betaplus.client.renderer.AlphaModelLoader;
import com.mrburgerus.betaplus.client.renderer.ModelsCache;
import com.mrburgerus.betaplus.client.renderer.model.BakedModelWrapperAlpha;
import com.mrburgerus.betaplus.util.ResourceHelper;
import com.mrburgerus.betaplus.world.alpha_plus.WorldTypeAlphaPlus;
import com.mrburgerus.betaplus.world.beta_plus.WorldTypeBetaPlus;
import com.mrburgerus.betaplus.world.biome.alpha.RegisterAlphaBiomes;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.function.Function;

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
		FMLJavaModLoadingContext.get().getModEventBus().addListener(RegisterAlphaBiomes::register);

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

		// Register ourselves for server, registry and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);

		proxy.init();
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
		WorldType worldType = event.getWorld().getWorldInfo().getTerrainType();
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
