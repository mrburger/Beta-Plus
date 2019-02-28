package com.mrburgerus.betaplus;

import com.mrburgerus.betaplus.client.color.ColorRegister;
import com.mrburgerus.betaplus.client.renderer.AlphaModelLoader;
import com.mrburgerus.betaplus.client.renderer.ModelsCache;
import com.mrburgerus.betaplus.client.renderer.model.BakedModelWrapperAlpha;
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
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemUseContext;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.function.Function;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("betaplus")
public class BetaPlus
{
	//Fields
	public static final String MOD_NAME = "betaplus";

	private static final ResourceLocation GRASS_BLOCK_LOCATION = new ResourceLocation(ResourceHelper.getResourceStringBetaPlus("block/alpha_grass_block"));
	private static final ResourceLocation GRASS_LOCATION = new ResourceLocation(ResourceHelper.getResourceStringBetaPlus("block/alpha_grass"));
	private static final ResourceLocation LEAVES_LOCATION = new ResourceLocation(ResourceHelper.getResourceStringBetaPlus("block/alpha_oak_leaves"));

	private static final Function<ResourceLocation, IUnbakedModel> DEFAULT_GETTER =  ModelLoaderRegistry::getModelOrMissing;

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
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerAlphaLoader);
		//FMLJavaModLoadingContext.get().getModEventBus().addListener(AlphaGrassModelLoader);

		// Register Block Replacement for Alpha Green Grass
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::bakeAlphaGrass);


		// Register ourselves for server, registry and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
		WorldTypeBetaPlus.register();
		WorldTypeAlphaPlus.register();
    }

    /* Turns on Perpetual Snow for Snowy Alpha Worlds */
    @SubscribeEvent
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
			BetaPlus.LOGGER.info("Not an alpha world.");
		}
    }


    //TODO: ADD FOR EACH LOOP TO SIMPLIFY ADDING RESOURCES
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
	public void bakeAlphaGrass(final ModelBakeEvent event)
	{
		AlphaModelLoader.INSTANCE.setLoader(event.getModelLoader());

		IBlockState grassBlockState = Blocks.GRASS_BLOCK.getDefaultState();
		IBlockState grassState = Blocks.GRASS.getDefaultState();
		// Messing around
		IBlockState leavesState = Blocks.OAK_LEAVES.getStateContainer().getBaseState();
		BetaPlus.LOGGER.info("Leaves have State: " + leavesState);
		// Variant is not snowy.
		ModelResourceLocation grassBlockLocation = BlockModelShapes.getModelLocation(grassBlockState);
		ModelResourceLocation grassLocation = BlockModelShapes.getModelLocation(grassState);
		ModelResourceLocation leavesLocation = BlockModelShapes.getModelLocation(leavesState);

		/* Built with Help from @Cadiboo from Minecraft Forge, Thanks! */
		Object object = event.getModelRegistry().get(grassBlockLocation);

		if (object != null)
		{
			// Test (Could be Hard-coded, cause issues)
			IBakedModel existingModel = (IBakedModel)object; // Existing Grass Model
			IBakedModel modelNew = ModelsCache.INSTANCE.getBakedModel(GRASS_BLOCK_LOCATION);

			// Replace
			event.getModelRegistry().replace(grassBlockLocation, new BakedModelWrapperAlpha(existingModel, modelNew));
		}

		object = event.getModelRegistry().get(grassLocation);

		if (object != null)
		{
			IBakedModel existingModel = (IBakedModel)object; // Existing Grass Model
			IBakedModel modelNew = ModelsCache.INSTANCE.getBakedModel(GRASS_LOCATION);

			// Replace
			BetaPlus.LOGGER.debug("(BetaPlus) Working Location: " + grassLocation.toString());
			event.getModelRegistry().replace(grassLocation, new BakedModelWrapperAlpha(existingModel, modelNew));
		}

		object = event.getModelRegistry().get(leavesLocation);

		// Do some gymnastics to get leaves to override.
		if (object != null)
		{
			BetaPlus.LOGGER.info("(BetaPlus) Looking for: " + leavesLocation);
			IBakedModel existingModel = (IBakedModel)object; // Existing Leaves Model
			BetaPlus.LOGGER.info("(BetaPlus) Existing: " + existingModel.getOverrides().toString());

			IBakedModel modelNew = ModelsCache.INSTANCE.getBakedModel(LEAVES_LOCATION);

			// Replace, so we can replace the "root" Baked Model (DOES NOT WORK YET)
			ModelResourceLocation modelLocation = leavesLocation; //new ModelResourceLocation(leavesLocation.getPath(), "");
			BetaPlus.LOGGER.debug("Found (DEBUG): "  + event.getModelRegistry().get(leavesLocation).toString());
			BetaPlus.LOGGER.info("Resource Location Replaced: " + modelLocation);
			event.getModelRegistry().replace(modelLocation, new BakedModelWrapperAlpha(existingModel, modelNew));

			//Produces NULL!
			BetaPlus.LOGGER.debug("Replaced With (DEBUG): "  + event.getModelRegistry().get(modelLocation).toString());

		}
	}

	@SubscribeEvent
	public void registerAlphaLoader(final ModelRegistryEvent event)
	{
		ModelLoaderRegistry.registerLoader(AlphaModelLoader.INSTANCE);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void addTextures(TextureStitchEvent.Pre event)
	{
		IResourceManager manager = Minecraft.getInstance().getResourceManager();
		IUnbakedModel model = ModelsCache.INSTANCE.getOrLoadModel(GRASS_BLOCK_LOCATION);
		for(ResourceLocation location : model.getTextures(DEFAULT_GETTER, new HashSet<>()))
		{
			event.getMap().registerSprite(manager, location);
		}

		model = ModelsCache.INSTANCE.getOrLoadModel(GRASS_LOCATION);
		for(ResourceLocation location : model.getTextures(DEFAULT_GETTER, new HashSet<>()))
		{
			event.getMap().registerSprite(manager, location);
		}

		model = ModelsCache.INSTANCE.getOrLoadModel(LEAVES_LOCATION);
		//BetaPlus.LOGGER.info("Leaves Model: " + model.toString());
		for(ResourceLocation location : model.getTextures(DEFAULT_GETTER, new HashSet<>()))
		{
			event.getMap().registerSprite(manager, location);
		}

	}


}
