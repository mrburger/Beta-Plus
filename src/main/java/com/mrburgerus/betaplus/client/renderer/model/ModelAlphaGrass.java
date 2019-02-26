package com.mrburgerus.betaplus.client.renderer.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.util.ResourceHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;


@OnlyIn(Dist.CLIENT)
public class ModelAlphaGrass implements IUnbakedModel
{
	//Fields
	private ResourceLocation alphaLocation;


	public ModelAlphaGrass(ResourceLocation alphaResourceLocation)
	{
		alphaLocation = alphaResourceLocation;
	}

	@Nullable
	@Override
	public IBakedModel bake(Function modelGetter, Function spriteGetter, IModelState state, boolean uvlock, VertexFormat format)
	{
		//TODO: INJECT THE TEXTURES
		BetaPlus.LOGGER.info("Baking!");

		try
		{
			//THIS WORKS
			IBakedModel existing = Minecraft.getInstance().getModelManager().
					getModel(new ModelResourceLocation(Blocks.GRASS_BLOCK.getRegistryName(), ""));

			return new AlphaGrassBakedWrapper(existing, new BakedModelAlphaGrass());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return ModelLoaderRegistry.getMissingModel().bake(modelGetter, spriteGetter, state, uvlock, format);
		}
	}

	@Override
	public Collection<ResourceLocation> getOverrideLocations()
	{
		return Collections.emptyList();
	}

	/* Probably where Textures are injected */
	@Override
	public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors)
	{
		BetaPlus.LOGGER.info("Called Texture");
		ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
		if (alphaLocation != null)
		{
			// Modify Alpha Location (Pretty Hacky)
			ResourceLocation newAlphaLoc = new ResourceLocation(alphaLocation.getNamespace(), alphaLocation.getPath().replace("models","block"));
			BetaPlus.LOGGER.info("Adding: " + newAlphaLoc);
			builder.add(alphaLocation);
		}
		return builder.build();
	}

}
