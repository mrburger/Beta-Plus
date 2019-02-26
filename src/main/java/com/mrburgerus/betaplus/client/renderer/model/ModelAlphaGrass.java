package com.mrburgerus.betaplus.client.renderer.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mrburgerus.betaplus.BetaPlus;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.VertexFormat;
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
	private ResourceLocation baseLocation;
	private ResourceLocation alphaLocation;


	public ModelAlphaGrass(ResourceLocation grassLocation, ResourceLocation alphaResourceLocation)
	{
		baseLocation = grassLocation;
		alphaLocation = alphaResourceLocation;
	}

	@Nullable
	@Override
	public IBakedModel bake(Function modelGetter, Function spriteGetter, IModelState state, boolean uvlock, VertexFormat format)
	{
		//TODO: INJECT THE TEXTURES
		try
		{
			ModelResourceLocation location = new ModelResourceLocation("builtin/missing", "missing");
			/* Progress... */
			location = new ModelResourceLocation("betaplus:alpha_grass_block", "");
			//location = new ModelResourceLocation("grass_block", "");
			BetaPlus.LOGGER.info("Making model2: " + location);
			IUnbakedModel unbakedModel = ModelLoaderRegistry.getModel(location);//ModelLoaderRegistry.getModelOrMissing(actualLoc); //ModelLoader.defaultModelGetter().apply(baseLocation);
			BetaPlus.LOGGER.info("Making 1: " + unbakedModel);
			//IUnbakedModel newModel = unbakedModel.retexture(ImmutableMap.<String, String>builder().build());
			//unbakedModel.retexture();
			IBakedModel bakedModel = unbakedModel.bake(modelGetter, spriteGetter, state, uvlock, format);
			BetaPlus.LOGGER.info("Making 2: " + bakedModel.toString());
			return bakedModel;
		}
		catch (final Exception e)
		{
			BetaPlus.LOGGER.error("Could Not Bake Alpha Model!");
			return ModelLoaderRegistry.getMissingModel().bake(modelGetter, spriteGetter, state, uvlock, format);
		}
	}

	@Override
	public Collection<ResourceLocation> getOverrideLocations()
	{
		return Collections.emptyList();
	}

	@Override
	public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors)
	{
		BetaPlus.LOGGER.info("Called Texture");
		ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
		if (alphaLocation != null)
		{
			builder.add(alphaLocation);
		}
		return builder.build();
	}

}
