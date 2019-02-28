package com.mrburgerus.betaplus.client.renderer;

import com.mrburgerus.betaplus.BetaPlus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;

import java.io.IOException;
import java.io.InputStreamReader;


/* UNUSED, FOR LATER USAGE IF IT DOESN'T WORK */
public class ModelLoader implements ICustomModelLoader
{
	// Fields
	public static final String MODEL_BASE_LOCATION = "models/block/";

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{

	}

	@Override
	public boolean accepts(ResourceLocation modelLocation)
	{
		BetaPlus.LOGGER.info("Model Location Raw: " + modelLocation);
		String modelPath = modelLocation.getPath();
		if( modelLocation.getPath().startsWith( "models/" ) )
		{
			modelPath = modelPath.substring( "models/".length() );
		}

		ResourceLocation location = new ResourceLocation( modelLocation.getNamespace(), "models/" + modelPath + ".json" );

		BetaPlus.LOGGER.info("Trying to accept: " + location.toString());

		try(InputStreamReader io = new InputStreamReader( Minecraft.getInstance().getResourceManager()
				.getResource(location).getInputStream()))
		{
			return true;
		}
		catch( IOException e)
		{
			BetaPlus.LOGGER.info("DID NOT ACCEPT :(");
		}
		return false;
	}

	@Override
	public IUnbakedModel loadModel(ResourceLocation modelLocation) throws Exception
	{
		final String resourcePath = modelLocation.getPath().replace(MODEL_BASE_LOCATION, "");
		return null;
	}
}
