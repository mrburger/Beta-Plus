package com.mrburgerus.betaplus.client.renderer;

import com.mojang.datafixers.types.Func;
import com.mrburgerus.betaplus.BetaPlus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.init.Blocks;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * modified from code made by Draco18s. Information in <a href= "http://www.minecraftforge.net/forum/topic/42318-1102-water-liquid-block-model/?tab=comments#comment-228067">this thread</a>
 * @author Elix_x (August 2016), modified by Draco18s (October 2016), Cadiboo (August 2018), and finally mrburgerUS (February 2019)
 */
@OnlyIn(Dist.CLIENT)
public class ModelsCache implements ISelectiveResourceReloadListener
{

	public static final ModelsCache INSTANCE = new ModelsCache();
	public static final IModelState DEFAULT_MODEL_STATE = part -> java.util.Optional.empty();
	public static final VertexFormat DEFAULT_VERTEX_FORMAT = DefaultVertexFormats.BLOCK;
	public static final Function<ResourceLocation, TextureAtlasSprite> DEFAULT_TEXTURE_GETTER = texture ->
			Minecraft.getInstance().getTextureMap().getAtlasSprite(texture.toString());
	public static final Function<ResourceLocation, IUnbakedModel> MODEL_GETTER = ModelLoader.defaultModelGetter();

	private final Map<ResourceLocation, IUnbakedModel> modelCache	= new HashMap<>();
	private final Map<ResourceLocation, IBakedModel> bakedCache	= new HashMap<>();

	public IUnbakedModel getModel(final ResourceLocation location)
	{
		IUnbakedModel model = this.modelCache.get(location);
		if (model != null)
		{
			BetaPlus.LOGGER.info("(ModelsCache) Found Model");
		}
		if (model == null) {
			try
			{
				model = ModelLoaderRegistry.getModel(location);
			}
			catch (final Exception e)
			{
				BetaPlus.LOGGER.error("(ModelsCache): Couldnt Load Model!");
				e.printStackTrace();
				model = ModelLoaderRegistry.getMissingModel();

			}
			//Moved
			this.modelCache.put(location, model);
		}
		return model;
	}

	public IBakedModel getBakedModel(final ResourceLocation location)
	{
		BetaPlus.LOGGER.info("(ModelsCache) Resource: " + location.toString());
		return this.getBakedModel(location, DEFAULT_MODEL_STATE, DEFAULT_VERTEX_FORMAT, DEFAULT_TEXTURE_GETTER);
	}

	public IBakedModel getBakedModel(final ResourceLocation locationIn, final IModelState state, final VertexFormat format, final Function<ResourceLocation, TextureAtlasSprite> textureGetter) {

		ResourceLocation location = new ResourceLocation("grass_block");
		location = new ModelResourceLocation(locationIn.toString());
		IBakedModel bakedModel = this.bakedCache.get(locationIn);
		if (bakedModel != null)
		{
			BetaPlus.LOGGER.info("Found Baked Model");
		}
		if (bakedModel == null)
		{
			BetaPlus.LOGGER.info("(ModelsCache) Baking: " + location.toString());
			// Problem Line
			IUnbakedModel model = this.getModel(location);
			bakedModel = model.bake(MODEL_GETTER,textureGetter, state, false, format);
			this.bakedCache.put(location, bakedModel);
		}
		return bakedModel;
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate)
	{
		this.modelCache.clear();
		this.bakedCache.clear();
	}
}