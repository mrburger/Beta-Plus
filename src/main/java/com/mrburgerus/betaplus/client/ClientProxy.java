package com.mrburgerus.betaplus.client;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mrburgerus.betaplus.ServerProxy;
import com.mrburgerus.betaplus.client.render.BakedModelWrapperAlpha;
import com.mrburgerus.betaplus.client.render.ModelCache;
import com.mrburgerus.betaplus.client.render.ModelLoaderAlpha;
import com.mrburgerus.betaplus.util.ResourceHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
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
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.HashSet;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientProxy extends ServerProxy
{
	// FIELDS //
	final ResourceLocation GRASS_BLOCK_LOCATION = new ResourceLocation(ResourceHelper.getResourceStringBetaPlus("block/alpha_grass_block"));
	final ResourceLocation GRASS_LOCATION = new ResourceLocation(ResourceHelper.getResourceStringBetaPlus("block/alpha_grass"));
	final ResourceLocation OAK_LEAVES_LOCATION = new ResourceLocation(ResourceHelper.getResourceStringBetaPlus("block/alpha_oak_leaves"));
	final ResourceLocation SPRUCE_LEAVES_LOCATION = new ResourceLocation(ResourceHelper.getResourceStringBetaPlus("block/alpha_spruce_leaves"));


	public ClientProxy()
	{}

	@Override
	public void init()
	{
		// Register Client
		MinecraftForge.EVENT_BUS.register(this);
		// Added
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::addTextures);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::bakeAlphaModels);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerAlphaLoader);

	}

	@SubscribeEvent
	public void bakeAlphaModels(final ModelBakeEvent event)
	{

		ModelLoaderAlpha.INSTANCE.setLoader(event.getModelLoader());

		ModelResourceLocation grassBlockLoc = BlockModelShapes.getModelLocation(Blocks.GRASS_BLOCK.getDefaultState());
		ModelResourceLocation grassLoc = BlockModelShapes.getModelLocation(Blocks.GRASS.getDefaultState());
		ModelResourceLocation leavesLoc = BlockModelShapes.getModelLocation(Blocks.OAK_LEAVES.getDefaultState());

		List<Pair<ResourceLocation, ModelResourceLocation>> locList = Lists.newArrayList(
				Pair.of(GRASS_BLOCK_LOCATION, grassBlockLoc),
				Pair.of(GRASS_LOCATION, grassLoc)
				//Pair.of(OAK_LEAVES_LOCATION, leavesLoc)
		);

		for (Pair<ResourceLocation, ModelResourceLocation> loc : locList)
		{
			Object object = event.getModelRegistry().get(loc.getSecond());
			if (object != null)
			{
				IBakedModel existingModel = (IBakedModel) object; // Existing Grass Model
				IBakedModel modelNew = ModelCache.INSTANCE.getBakedModel(loc.getFirst(), event.getModelLoader());

				event.getModelRegistry().replace(loc.getSecond(), new BakedModelWrapperAlpha(existingModel, modelNew));
			}
		}

		// Leaves require special handling.
		List<Pair<ResourceLocation, Block>> leavesList = Lists.newArrayList(
				Pair.of(OAK_LEAVES_LOCATION, Blocks.OAK_LEAVES),
				Pair.of(SPRUCE_LEAVES_LOCATION, Blocks.SPRUCE_LEAVES)
		);

		for (Pair<ResourceLocation, Block> blockPair : leavesList)
		{
			ModelResourceLocation loc = BlockModelShapes.getModelLocation(blockPair.getSecond().getDefaultState());
			Object object = event.getModelRegistry().get(loc);
			if (object != null)
			{
				IBakedModel existingModel = (IBakedModel) object; // Existing Leaves Model
				// There will be a bunch of model locations, because leaves have "distance" and "persistent" States
				for(BlockState leavesState : blockPair.getSecond().getStateContainer().getValidStates())
				{
					ModelResourceLocation leavesLocation = BlockModelShapes.getModelLocation(leavesState);

					IBakedModel modelNew = ModelCache.INSTANCE.getBakedModel(blockPair.getFirst(), event.getModelLoader());

					event.getModelRegistry().replace(leavesLocation, new BakedModelWrapperAlpha(existingModel, modelNew));
				}
			}
		}
	}

	// Registers the Alpha loader
	@SubscribeEvent
	public void registerAlphaLoader(final ModelRegistryEvent event)
	{
		ModelLoaderRegistry.registerLoader(ModelLoaderAlpha.INSTANCE);
	}

	// Add the necessary textures
	@SubscribeEvent
	public void addTextures(TextureStitchEvent.Pre event)
	{
		List<ResourceLocation> locationList = Lists.newArrayList(GRASS_BLOCK_LOCATION, GRASS_LOCATION, OAK_LEAVES_LOCATION, SPRUCE_LEAVES_LOCATION);
		for (ResourceLocation loc : locationList)
		{
			IUnbakedModel model = ModelLoaderAlpha.INSTANCE.loadModel(loc);
			for (ResourceLocation l : model.getTextures(ModelLoader.defaultModelGetter(), new HashSet<>()))
			{
				event.addSprite(l);
			}
		}
	}


	// Called on world load, for Alpha Grass.
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event)
	{
		World world = event.getWorld().getWorld();
		//Set worldType on ModelLoader to the appropriate value
		BakedModelWrapperAlpha.worldType = world.getWorld().getWorldType();
	}
}
