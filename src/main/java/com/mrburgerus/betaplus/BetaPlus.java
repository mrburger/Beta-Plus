package com.mrburgerus.betaplus;

import com.google.common.annotations.Beta;
import com.mrburgerus.betaplus.client.color.ColorRegister;
import com.mrburgerus.betaplus.client.color.GrassColorBetaPlus;
import com.mrburgerus.betaplus.client.color.ReedColorBetaPlus;
import com.mrburgerus.betaplus.client.renderer.RenderAlphaBlocks;
import com.mrburgerus.betaplus.world.alpha_plus.AlphaPlusGenSettings;
import com.mrburgerus.betaplus.world.alpha_plus.ChunkGeneratorAlphaPlus;
import com.mrburgerus.betaplus.world.alpha_plus.WorldTypeAlphaPlus;
import com.mrburgerus.betaplus.world.beta_plus.WorldTypeBetaPlus;
import com.mrburgerus.betaplus.world.biome.BiomeProviderAlphaPlus;
import com.mrburgerus.betaplus.world.biome.alpha.BiomeAlphaFrozenOcean;
import com.mrburgerus.betaplus.world.biome.alpha.RegisterAlphaBiomes;
import net.minecraft.block.BlockGrass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ModelBlock;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("betaplus")
public class BetaPlus
{
	//Fields
	public static final String MOD_NAME = "betaplus";

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public BetaPlus()
	{
		// Register Biomes
		FMLJavaModLoadingContext.get().getModEventBus().addListener(RegisterAlphaBiomes::register);

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Register Client-side features
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorRegister::clientSide);

		// Register Block Model Replacement
		FMLJavaModLoadingContext.get().getModEventBus().addListener(RenderAlphaBlocks::register);




		// Register ourselves for server, registry and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
		BetaPlus.LOGGER.info("Finished Beta+ Creation");
    }

    private void setup(final FMLCommonSetupEvent event)
    {
		WorldTypeBetaPlus.register();
		WorldTypeAlphaPlus.register();
    }

    /* Turns on Perpetual Snow for Snowy Alpha Worlds */
    @SubscribeEvent
    public void setAlpha (final WorldEvent.Load event)
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
			//Blocks.GRASS_BLOCK.getRenderLayer()
			//ModelLoaderRegistry.getModel(Blocks.GRASS_BLOCK.getRegistryName()).getTextures().clear();
			//ModelLoader.getInventoryVariant()
        }
        else
		{
			BetaPlus.LOGGER.info("Not an alpha world.");
		}
    }

    @SubscribeEvent
	public static void renderAlpha(final ModelBakeEvent event)
	{
		String s = event.getModelManager().getBlockModelShapes().getTexture(Blocks.GRASS_BLOCK.getDefaultState()).getName().toString();
		BetaPlus.LOGGER.info("Model Name: " + s);
	}






}
