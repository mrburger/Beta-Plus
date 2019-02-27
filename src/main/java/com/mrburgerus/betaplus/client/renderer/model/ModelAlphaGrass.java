package com.mrburgerus.betaplus.client.renderer.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mrburgerus.betaplus.BetaPlus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class ModelAlphaGrass implements IUnbakedModel
{
	private final ResourceLocation grassLocation;
	private final ResourceLocation alphaLocation;
	public static final String JSON_STRING =
		"{ \"parent\": \"block/block\", \"textures\": { \"particle\": \"minecraft:block/dirt\", \"bottom\": \"minecraft:block/dirt\", \"top\": \"betaplus:block/alpha_grass_block_top\", \"side\": \"betaplus:block/grass_block_side\", \"overlay\": \"betaplus:block/alpha_grass_block_side_overlay\" }, \"elements\": [ { \"from\": [ 0, 0, 0 ], \"to\": [ 16, 16, 16 ], \"faces\": { \"down\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#bottom\", \"cullface\": \"down\" }, \"up\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#top\", \"cullface\": \"up\", \"tintindex\": 0 }, \"north\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#side\", \"cullface\": \"north\" }, \"south\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#side\", \"cullface\": \"south\" }, \"west\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#side\", \"cullface\": \"west\" }, \"east\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#side\", \"cullface\": \"east\" } } }, { \"from\": [ 0, 0, 0 ], \"to\": [ 16, 16, 16 ], \"faces\": { \"north\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#overlay\", \"tintindex\": 0, \"cullface\": \"north\" }, \"south\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#overlay\", \"tintindex\": 0, \"cullface\": \"south\" }, \"west\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#overlay\", \"tintindex\": 0, \"cullface\": \"west\" }, \"east\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#overlay\", \"tintindex\": 0, \"cullface\": \"east\" } } } ] }";


	public ModelAlphaGrass(ResourceLocation grass, ResourceLocation alpha)
	{
		grassLocation = grass;
		alphaLocation = alpha; //new ResourceLocation(alpha.getNamespace(), "test");
	}

	@Override
	public Collection<ResourceLocation> getOverrideLocations()
	{
		return Collections.emptyList();
	}

	@Override
	public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors)
	{
		BetaPlus.LOGGER.info("(ModelAlphaGrass) Getting Textures");
		//IBlockState grassState = Blocks.GRASS_BLOCK.getDefaultState();
		// Variant is not snowy.
		//ModelResourceLocation grassLocation = BlockModelShapes.getModelLocation(grassState);
		/*
		// Possibly cast? NOPE
		IUnbakedModel parent = ModelLoaderRegistry.getModelOrLogError(grassLocation, "WOW! AN ERROR");
		try
		{
			Minecraft.getInstance().getResourceManager().getResource(BetaPlus.ALPHA_LOCATION).getInputStream();
		}
		catch (IOException e)
		{
			BetaPlus.LOGGER.info("Error! Input Stream");
			e.printStackTrace();
		}
		// Possibly using the default will fix this issue
		// It doesn't, must be an issue!
		Function<ResourceLocation, IUnbakedModel> modelFunction = ModelLoader.defaultModelGetter();
		BetaPlus.LOGGER.info("(ModelAlphaGrass) modelFunc: " + modelFunction.toString());
		*/
		//Collection<ResourceLocation>  textureList = parent.getTextures(modelFunction, new HashSet<>());
		Collection<ResourceLocation> locations = Lists.newArrayList();
		try
		{
			locations.add(ResourceLocation.read(new com.mojang.brigadier.StringReader(JSON_STRING)));
		}
		catch (CommandSyntaxException e)
		{
			BetaPlus.LOGGER.info("THIS AINT IT");
			e.printStackTrace();
		}


		locations.add(BetaPlus.ALPHA_LOCATION);


		return locations;
	}

	/* Bake the Initial Grass Model */
	@Nullable
	@Override
	public IBakedModel bake(Function<ResourceLocation, IUnbakedModel> modelGetter, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, IModelState state, boolean uvlock, VertexFormat format)
	{
		// Causes INFINITE LOOP when models/ replaced .replace("models/","")

		ResourceLocation location = new ResourceLocation(alphaLocation.getNamespace(), alphaLocation.getPath());
		BetaPlus.LOGGER.info("(ModelAlphaGrass) Baking Model: " + location);
		//IBakedModel modelNew = ModelsCache.INSTANCE.getBakedModel(location, state, format, spriteGetter);
		// Original Grass Model
		IBakedModel original = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation(grassLocation.toString()));

		TRSRTransformation transform = state.apply(Optional.empty()).orElse(TRSRTransformation.identity());
		List<BakedQuad> quads = null;
		TextureAtlasSprite texture = null;
		Map<EnumFacing, List<BakedQuad>> facingListMap = Maps.newEnumMap(EnumFacing.class);

		// Put Empty lists
		final EnumFacing[] facings = {EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST};
		for (EnumFacing facing : facings)
		{
			facingListMap.put(facing, Lists.newArrayList());
		}


		try
		{
			//JsonElement element = gson.fromJson(JSON_STRING, ModelBlock.deserialize());
			IUnbakedModel unbaked = ModelBlock.deserialize(JSON_STRING);
			IUnbakedModel unbake2 = unbaked;
			//BetaPlus.LOGGER.info("Deserialized string: " + unbake2.getTextures(ModelLoader.defaultModelGetter(), new HashSet<>()));
			//+ unbaked.getTextures(modelGetter, new HashSet<>()).size());
			IBakedModel model = unbaked.bake(modelGetter, spriteGetter, state, uvlock, format);
			BetaPlus.LOGGER.info("IBaked: " + model.toString());
			return model;
			//new ModelBlock.Deserializer()(element, ModelBlock.clas).bake(modelGetter, spriteGetter, state, uvlock, format);
			//new SimpleBakedModel(quads, facingListMap, true, true, texture, );
			//new MultipartBakedModel.Builder().build();
			//new SimpleBakedModel.Builder(new ModelBlock(), ItemOverrideList.EMPTY).build();
		}
		catch (Exception e)
		{
			BetaPlus.LOGGER.info("(ModelAlphaGrass) Error!");
			e.printStackTrace();
		}

		// Works when (original, original)
		return original;
	}
}
