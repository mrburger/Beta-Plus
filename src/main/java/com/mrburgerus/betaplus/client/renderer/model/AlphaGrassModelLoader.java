package com.mrburgerus.betaplus.client.renderer.model;

import com.mrburgerus.betaplus.BetaPlus;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelBlock;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public class AlphaGrassModelLoader implements ICustomModelLoader
{
	public AlphaGrassModelLoader()
	{
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{

	}

	/* Falls back to Vanilla if this returns FALSE */
	@Override
	public boolean accepts(ResourceLocation modelLocation)
	{
		boolean doesAccept = (modelLocation.getNamespace().equals(BetaPlus.MOD_NAME) && modelLocation.getPath().startsWith("models/alpha_grass_block"));
		if (doesAccept)
		{
			// This HAS to fire otherwise something is wrong...
			BetaPlus.LOGGER.info("Accepts Alpha Grass: " + modelLocation.toString());
		}
		else
		{
			BetaPlus.LOGGER.error("Not accepting! " + modelLocation.getPath());
		}
		return doesAccept;
	}

	@Override
	public IUnbakedModel loadModel(ResourceLocation modelLocation) throws Exception
	{
		BetaPlus.LOGGER.info("ModelLoader: Load Model: " + modelLocation.toString() + "; " + modelLocation.getNamespace() + ":" + modelLocation.getPath());

		// Returns MissingBlock always right now, how to fix?
		return new ModelAlphaGrass(modelLocation);
	}
}
