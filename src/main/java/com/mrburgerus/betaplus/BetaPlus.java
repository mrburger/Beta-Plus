package com.mrburgerus.betaplus;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mrburgerus.betaplus.client.color.ColorRegister;
import com.mrburgerus.betaplus.client.renderer.GrassModelLoader;
import com.mrburgerus.betaplus.client.renderer.ModelsCache;
import com.mrburgerus.betaplus.client.renderer.model.ModelAlphaGrass;
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
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.function.Function;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("betaplus")
public class BetaPlus
{
	//Fields
	public static final String MOD_NAME = "betaplus";

	// model/alpha_grass_block
	public static final ResourceLocation ALPHA_LOCATION = new ResourceLocation(ResourceHelper.getResourceStringBetaPlus("alpha_grass_block"));
	public static final Function<ResourceLocation, IUnbakedModel> DEFAULT_GETTER =  ModelLoaderRegistry::getModelOrMissing;

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
		//BetaPlus.LOGGER.info("Finished Beta+ Creation");
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
		BetaPlus.LOGGER.info("(BetaPlus) ModelBake");
		// Adds model, I think.
		// Causes Recursion Loop.
		//event.getModelRegistry().put(new ModelResourceLocation(ALPHA_LOCATION.toString()), ModelsCache.INSTANCE.getBakedModel(ALPHA_LOCATION));

		IBlockState grassState = Blocks.GRASS_BLOCK.getDefaultState();
		// Variant is not snowy.
		ModelResourceLocation grassLocation = BlockModelShapes.getModelLocation(grassState);

		/* Built with Help from @Cadiboo from Minecraft Forge, Thanks! */
		// Gets an Object (Works)
		Object object =  event.getModelRegistry().get(grassLocation);
		// If the object is Non-null
		if (object != null)
		{
			IBakedModel existingModel = (IBakedModel)object; // Existing Grass Model
			BetaPlus.LOGGER.info("(BetaPlus) INTO THE BREACH!");
			IBakedModel modelNew = ModelsCache.INSTANCE.getBakedModel(ALPHA_LOCATION);

			//Moved up here
			BetaPlus.LOGGER.info("(BetaPlus) New Grass: " + modelNew.toString());
			BetaPlus.LOGGER.info("(BetaPlus) Replacing Grass With Tex: " + modelNew.getQuads(grassState, EnumFacing.UP,  new Random()).get(0).getSprite());

			event.getModelRegistry().replace(grassLocation, modelNew);
		}

	}

	@SubscribeEvent
	public void createAlphaGrass(final ModelRegistryEvent event)
	{
		BetaPlus.LOGGER.info("(BetaPlus) ModelRegistry");
		ModelLoaderRegistry.registerLoader(new GrassModelLoader());
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void addTextures (TextureStitchEvent.Post event)
	{
		BetaPlus.LOGGER.info("(BetaPlus) TextureStitch");


		IResourceManager manager = Minecraft.getInstance().getResourceManager();
		try
		{

			ResourceLocation test = ResourceLocation.read(new com.mojang.brigadier.StringReader(ModelAlphaGrass.JSON_STRING));
			BetaPlus.LOGGER.info("ResourceLocation of Tex: " + test.toString());
			event.getMap().registerSprite(manager, test);
		}
		catch (CommandSyntaxException e)
		{
			BetaPlus.LOGGER.error("Couldnt Load Tex!");
			e.printStackTrace();
		}

		//blockLocation = Blocks.BONE_BLOCK.getRegistryName(); //Testing Vanilla (NOT LOADED)
		//Never Loaded
		//BetaPlus.LOGGER.info("(BetaPlus) Trying to get Models for: " + ALPHA_LOCATION.toString() + " ; " + ModelsCache.INSTANCE.getModel(ALPHA_LOCATION).toString());
		/*
		IUnbakedModel model;
		try
		{
			//model = ModelLoaderRegistry.getModel(ALPHA_LOCATION);
			model = ModelsCache.INSTANCE.getModel(ALPHA_LOCATION);
		}
		catch (Exception e)
		{
			BetaPlus.LOGGER.error("(BetaPlus) NO MODEL FOUND");
			model = ModelLoaderRegistry.getMissingModel();
		}


		// DEBUG
		TextureMap map = event.getMap();
		IResourceManager manager = Minecraft.getInstance().getResourceManager();
		BetaPlus.LOGGER.info("DUMP BEGIN:");
		BetaPlus.LOGGER.info("Location: " + ALPHA_LOCATION.toString());
		BetaPlus.LOGGER.info("BASE PATH: " + map.getBasePath());
		BetaPlus.LOGGER.info("Atlas Sprite: " + map.getAtlasSprite(ALPHA_LOCATION.toString()));
		map.registerSprite(manager, ALPHA_LOCATION);
		BetaPlus.LOGGER.info("Atlas Sprite New: " + map.getAtlasSprite(ALPHA_LOCATION.toString()));
		BetaPlus.LOGGER.info("Atlas Sprite New2: " + map.getSprite(ALPHA_LOCATION));
		*/


		/*
		for (final ResourceLocation textureLocation : model.getTextures(ModelLoader.defaultModelGetter(), new HashSet<>()))
		{
			BetaPlus.LOGGER.info("(BetaPlus) Register Tex: " + textureLocation);
			event.getMap().registerSprite(Minecraft.getInstance().getResourceManager(), textureLocation);

			// To verify the texture is loaded.
			BetaPlus.LOGGER.info("(BetaPlus) Tex Sprite: " + event.getMap().getSprite(textureLocation));
		}
		*/

	}


}
