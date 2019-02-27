package com.mrburgerus.betaplus;

import com.mrburgerus.betaplus.client.color.ColorRegister;
import com.mrburgerus.betaplus.client.renderer.model.*;
import com.mrburgerus.betaplus.util.ResourceHelper;
import com.mrburgerus.betaplus.world.alpha_plus.WorldTypeAlphaPlus;
import com.mrburgerus.betaplus.world.beta_plus.WorldTypeBetaPlus;
import com.mrburgerus.betaplus.world.biome.alpha.RegisterAlphaBiomes;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.Models;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;

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

		// Register Alpha Textures
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::addTextures);

		// Register Alpha Grass Model
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::createAlphaGrass);
		//FMLJavaModLoadingContext.get().getModEventBus().addListener(AlphaGrassModelLoader);

		// Register Block Replacement for Alpha Green Grass
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::bakeAlphaGrass);


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
        }
        else
		{
			BetaPlus.LOGGER.info("Not an alpha world.");
		}
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
	public void bakeAlphaGrass(final ModelBakeEvent event)
	{
		//Added, hopefully will fix something...
		ModelResourceLocation modelLoc = new ModelResourceLocation(ResourceHelper.getResourceStringBetaPlus("alpha_grass_block"));
		event.getModelRegistry().put(modelLoc, new BakedModelAlphaGrass());

		IBlockState grassState = Blocks.GRASS_BLOCK.getDefaultState();
		// Variant is not snowy.
		ModelResourceLocation grassLocation = BlockModelShapes.getModelLocation(grassState);

		/* Built with Help from @Cadiboo from Minecraft Forge, Thanks! */
		// Gets an Object
		Object object =  event.getModelRegistry().get(grassLocation);
		// If the object is Non-null
		if (object != null) {
			IBakedModel existingModel = (IBakedModel)object; // Existing Grass Model

			//BetaPlus.LOGGER.info("Getting Baked Model From: " + newLoc);
			ResourceLocation newLoc = new ResourceLocation(ResourceHelper.getResourceStringBetaPlus("alpha_grass_block"));
			IBakedModel newModel = ModelsCache.INSTANCE.getBakedModel(newLoc);
			BetaPlus.LOGGER.info("Replacing Grass: " + newLoc.toString() + " ; " + newModel.toString());

			event.getModelRegistry().replace(grassLocation, new AlphaGrassBakedWrapper(existingModel, newModel));
			BetaPlus.LOGGER.info("Registered Grass Override");
		}

	}

	@SubscribeEvent
	public void createAlphaGrass(final ModelRegistryEvent event)
	{
		ModelLoaderRegistry.registerLoader(new AlphaGrassModelLoader());
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void addTextures (TextureStitchEvent.Pre event)
	{
		// Model will be appended to beginning, somewhere in pipeline.
		// Working?
		ResourceLocation blockLocation = new ResourceLocation(ResourceHelper.getResourceStringBetaPlus("block/alpha_grass_block"));
		BetaPlus.LOGGER.info("Trying to get Models for: " + blockLocation.toString());
		final IUnbakedModel model;
		try
		{
			model = ModelLoaderRegistry.getModel(blockLocation);
		}
		catch (Exception e)
		{
			BetaPlus.LOGGER.error("NO MODEL FOUND");
			e.printStackTrace();
			return;
		}

		// Does not properly register
		for (final ResourceLocation textureLocation : model.getTextures(ModelLoader.defaultModelGetter(), new HashSet<>()))
		{
			//Previously added "block/", caused a double directory issue
			ResourceLocation loc2 = new ResourceLocation(textureLocation.getNamespace(), blockLocation.getPath());
			BetaPlus.LOGGER.info("Register Tex: " + loc2);
			event.getMap().registerSprite(Minecraft.getInstance().getResourceManager(), loc2);
		}
	}


}
