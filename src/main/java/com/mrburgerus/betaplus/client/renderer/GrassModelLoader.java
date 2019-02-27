package com.mrburgerus.betaplus.client.renderer;

import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.client.renderer.model.ModelAlphaGrass;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;

public class GrassModelLoader implements ICustomModelLoader
{
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{

	}

	@Override
	public boolean accepts(ResourceLocation modelLocation)
	{
		boolean doesAccept = (modelLocation.getNamespace().equals(BetaPlus.MOD_NAME));
		if (doesAccept)
		{
			// This HAS to fire otherwise something is wrong...
			BetaPlus.LOGGER.info("(AlphaGrassModelLoader) Accepts Alpha Grass: " + modelLocation.toString());
		}
		else
		{
			BetaPlus.LOGGER.error("(AlphaGrassModelLoader) Not accepting! " + modelLocation.getPath());
		}
		return doesAccept;
	}

	/* Called in ModelsCache recursively, causes issue */
	@Override
	public IUnbakedModel loadModel(ResourceLocation modelLocation)
	{
		//Minecraft.getInstance().getRenderManager().
		//return ModelBlock.deserialize("{ \"parent\": \"minecraft:block/block\", \"textures\": { \"particle\": \"minecraft:block/dirt\", \"bottom\": \"minecraft:block/dirt\", \"top\": \"betaplus:block/alpha_grass_block_top\", \"side\": \"betaplus:block/grass_block_side\", \"overlay\": \"betaplus:block/alpha_grass_block_side_overlay\" }, \"elements\": [ { \"from\": [ 0, 0, 0 ], \"to\": [ 16, 16, 16 ], \"faces\": { \"down\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#bottom\", \"cullface\": \"down\" }, \"up\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#top\", \"cullface\": \"up\", \"tintindex\": 0 }, \"north\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#side\", \"cullface\": \"north\" }, \"south\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#side\", \"cullface\": \"south\" }, \"west\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#side\", \"cullface\": \"west\" }, \"east\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#side\", \"cullface\": \"east\" } } }, { \"from\": [ 0, 0, 0 ], \"to\": [ 16, 16, 16 ], \"faces\": { \"north\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#overlay\", \"tintindex\": 0, \"cullface\": \"north\" }, \"south\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#overlay\", \"tintindex\": 0, \"cullface\": \"south\" }, \"west\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#overlay\", \"tintindex\": 0, \"cullface\": \"west\" }, \"east\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#overlay\", \"tintindex\": 0, \"cullface\": \"east\" } } } ] }");
		//return new BakedModelGrass();
		//ResourceLocation test = new ResourceLocation(modelLocation.toString() + "test1");
		// Loads Model, MUST pass value, but if it loops, what happens?
		return new ModelAlphaGrass(Blocks.GRASS_BLOCK.getRegistryName(), modelLocation);
	}
}
