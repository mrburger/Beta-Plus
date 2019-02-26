package com.mrburgerus.betaplus.client.renderer.model;

import com.mrburgerus.betaplus.BetaPlus;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public class AlphaGrassModelLoader implements ICustomModelLoader
{
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{

	}

	@Override
	public boolean accepts(ResourceLocation modelLocation)
	{
		boolean doesAccept = (modelLocation.getNamespace().equals(BetaPlus.MOD_NAME) && modelLocation.getPath().startsWith("alpha_grass"));
		if (modelLocation.toString().contains("alpha_grass"))
		{
			BetaPlus.LOGGER.info("Found Alpha Grass: " + doesAccept);
		}
		return doesAccept;
	}

	@Override
	public IUnbakedModel loadModel(ResourceLocation modelLocation) throws Exception
	{
		final String resPath = modelLocation.getPath();
		BetaPlus.LOGGER.info("Path: " + resPath);
		try
		{
			ModelResourceLocation loc = new ModelResourceLocation(TextureMap.LOCATION_BLOCKS_TEXTURE.toString());
			BetaPlus.LOGGER.info("Location Model: " + loc);
			if (resPath.contains("alpha_grass"))
			{
				loc = new ModelResourceLocation("betaplus:alpha_grass_block", "");
			}

			return new ModelAlphaGrass(ModelHelper.NORMAL_GRASS_BLOCK, )
		}
		catch (Exception e)
		{
			BetaPlus.LOGGER.warn("Could Not Load Model!");
			return ModelLoaderRegistry.getMissingModel();
		}
	}
}
