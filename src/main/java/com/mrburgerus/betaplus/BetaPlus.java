package com.mrburgerus.betaplus;

import com.google.gson.Gson;
import com.mrburgerus.betaplus.client.color.ColorRegister;
import com.mrburgerus.betaplus.client.renderer.RenderAlphaBlocks;
import com.mrburgerus.betaplus.util.ResourceHelper;
import com.mrburgerus.betaplus.world.alpha_plus.WorldTypeAlphaPlus;
import com.mrburgerus.betaplus.world.beta_plus.WorldTypeBetaPlus;
import com.mrburgerus.betaplus.world.biome.alpha.RegisterAlphaBiomes;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.Random;

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
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::renderAlpha);


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
	public void renderAlpha(final ModelBakeEvent event)
	{
		ResourceLocation grassLoc = Blocks.GRASS_BLOCK.getRegistryName();
		ResourceLocation newGrass = new ResourceLocation(ResourceHelper.getResourceStringBetaPlus("alpha_grass_block"));
		IBlockState grassState = Blocks.GRASS_BLOCK.getDefaultState();
		// Variant is not snowy.
		ModelResourceLocation grassLocation = BlockModelShapes.getModelLocation(grassState);
		//TextureAtlasSprite sprite = new TextureAtlasSprite();
		BetaPlus.LOGGER.info("We made it here");
		ModelBlock block = new ModelBlock(grassLoc);
		block.textures.clear();
		IBakedModel baked = block.bake(ModelLoader.defaultModelGetter(), ModelLoader.defaultTextureGetter(), part -> Optional.empty(), false, DefaultVertexFormats.BLOCK);
		//new ItemModelGenerator().makeItemModel(newGrass, block);
		//BlockPart bPart = new BlockPart(new Vector3f(0, 0, 7.5F));
		//ModelBlock blockModel = new ItemModelGenerator().makeItemModel();
		//ModelBlock modelBlock = event.getModelLoader().getUnbakedModel(grassLoc).bake();
		//event.getModelManager().getModel(Blocks.GRASS_BLOCK.getRenderLayer())
		//event.getModelManager().getBlockModelShapes().getModel(Blocks.GRASS_BLOCK.getDefaultState())
		//event.getModelRegistry().replace(new ModelResourceLocation(""))
		//s = event.getModelManager().getModel(new ModelResourceLocation(grassLoc, "")).getQuads(grassState, EnumFacing.UP, new Random()).toString();
		//s = event.getModelRegistry().get(grassLocation).toString();
		//s = event.getModelManager().getModel(grassLocation).
		//GameRegistry.findRegistry(Block.class);
		//event.getModelLoader().getUnbakedModel(grassLoc);
		// Definitely incorrect.
		//Blocks.GRASS_BLOCK.setRegistryName(newGrass);
		//event.getModelLoader().getUnbakedModel(grassLoc).getOverrideLocations().clear();
		//event.getModelRegistry().replace(grassLocation, );
		//ModelBlock.makeBakedQuad()
		event.getModelLoader().setupModelRegistry().replace(grassLocation, baked);

		BetaPlus.LOGGER.info("Model Name: " + grassLoc.toString());
	}




}
