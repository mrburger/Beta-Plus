package com.mrburgerus.betaplus.client;

import com.mrburgerus.betaplus.ServerProxy;
import com.mrburgerus.betaplus.client.color.ColorRegister;
import com.mrburgerus.betaplus.client.gui.GuiBetaNumber;
import com.mrburgerus.betaplus.client.renderer.AlphaModelLoader;
import com.mrburgerus.betaplus.client.renderer.ModelsCache;
import com.mrburgerus.betaplus.client.renderer.model.BakedModelWrapperAlpha;
import com.mrburgerus.betaplus.util.ResourceHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.HashSet;

public class ClientProxy extends ServerProxy
{
	public ClientProxy()
	{}

	@Override
	public void init()
	{
		//FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerAlphaLoader);
		//FMLJavaModLoadingContext.get().getModEventBus().addListener(this::addTextures);

		// Client Color
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorRegister::clientSide);

		// Register Number
		//FMLJavaModLoadingContext.get().getModEventBus().addListener(GuiBetaNumber::overlayEvent);

		// Register Client Features
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(GuiBetaNumber.class);
	}

	//TODO: ADD FOR EACH LOOP TO SIMPLIFY ADDING RESOURCES
	@SubscribeEvent
	public void bakeAlphaModels(final ModelBakeEvent event)
	{
		final ResourceLocation GRASS_BLOCK_LOCATION = new ResourceLocation(ResourceHelper.getResourceStringBetaPlus("block/alpha_grass_block"));
		final ResourceLocation GRASS_LOCATION = new ResourceLocation(ResourceHelper.getResourceStringBetaPlus("block/alpha_grass"));
		final ResourceLocation LEAVES_LOCATION = new ResourceLocation(ResourceHelper.getResourceStringBetaPlus("block/alpha_oak_leaves"));


		AlphaModelLoader.INSTANCE.setLoader(event.getModelLoader());

		IBlockState grassBlockState = Blocks.GRASS_BLOCK.getDefaultState();
		IBlockState grassState = Blocks.GRASS.getDefaultState();

		// Variant is not snowy.
		ModelResourceLocation grassBlockLocation = BlockModelShapes.getModelLocation(grassBlockState);
		ModelResourceLocation grassLocation = BlockModelShapes.getModelLocation(grassState);


		// Built with Help from @Cadiboo from Minecraft Forge, Thanks!
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
			event.getModelRegistry().replace(grassLocation, new BakedModelWrapperAlpha(existingModel, modelNew));
		}

		object = event.getModelRegistry().get(BlockModelShapes.getModelLocation(Blocks.OAK_LEAVES.getDefaultState()));

		// Do some gymnastics to get leaves to override.
		if (object != null)
		{
			IBakedModel existingModel = (IBakedModel) object; // Existing Leaves Model

			// There will be a bunch of model locations, because leaves have "distance" and "persistent" States
			for(IBlockState leavesState : Blocks.OAK_LEAVES.getStateContainer().getValidStates())
			{
				ModelResourceLocation leavesLocation = BlockModelShapes.getModelLocation(leavesState);

				IBakedModel modelNew = ModelsCache.INSTANCE.getBakedModel(LEAVES_LOCATION);

				event.getModelRegistry().replace(leavesLocation, new BakedModelWrapperAlpha(existingModel, modelNew));
			}
		}
	}

	@SubscribeEvent
	public void registerAlphaLoader(final ModelRegistryEvent event)
	{
		ModelLoaderRegistry.registerLoader(AlphaModelLoader.INSTANCE);
	}

	//TODO: Find a better way to do this
	@SubscribeEvent
	public void addTextures(TextureStitchEvent.Pre event)
	{
		final ResourceLocation GRASS_BLOCK_LOCATION = new ResourceLocation(ResourceHelper.getResourceStringBetaPlus("block/alpha_grass_block"));
		final ResourceLocation GRASS_LOCATION = new ResourceLocation(ResourceHelper.getResourceStringBetaPlus("block/alpha_grass"));
		final ResourceLocation LEAVES_LOCATION = new ResourceLocation(ResourceHelper.getResourceStringBetaPlus("block/alpha_oak_leaves"));

		IResourceManager manager = Minecraft.getInstance().getResourceManager();
		IUnbakedModel model = ModelsCache.INSTANCE.getOrLoadModel(GRASS_BLOCK_LOCATION);
		for(ResourceLocation location : model.getTextures(ModelLoader.defaultModelGetter(), new HashSet<>()))
		{
			event.getMap().registerSprite(manager, location);
		}

		model = ModelsCache.INSTANCE.getOrLoadModel(GRASS_LOCATION);
		for(ResourceLocation location : model.getTextures(ModelLoader.defaultModelGetter(), new HashSet<>()))
		{
			event.getMap().registerSprite(manager, location);
		}

		model = ModelsCache.INSTANCE.getOrLoadModel(LEAVES_LOCATION);
		//BetaPlus.LOGGER.info("Leaves Model: " + model.toString());
		for(ResourceLocation location : model.getTextures(ModelLoader.defaultModelGetter(), new HashSet<>()))
		{
			event.getMap().registerSprite(manager, location);
		}

	}
}
