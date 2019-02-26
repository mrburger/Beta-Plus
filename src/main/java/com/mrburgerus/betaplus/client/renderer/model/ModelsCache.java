package com.mrburgerus.betaplus.client.renderer.model;

import com.mrburgerus.betaplus.BetaPlus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
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
	private static boolean hasRender = false;


	private final Map<ResourceLocation, IUnbakedModel> modelCache	= new HashMap<>();
	private final Map<ResourceLocation, IBakedModel> bakedCache	= new HashMap<>();

	public IUnbakedModel getModel(final ResourceLocation location)
	{
		BetaPlus.LOGGER.info("Location for getModel: " + location); //Debug purposes
		IUnbakedModel model = this.modelCache.get(location);
		if (model == null) {
			try
			{
				model = ModelLoaderRegistry.getModel(location); //replace with constant string for testing?
				BetaPlus.LOGGER.info("Successfully got model");
			}
			catch (final Exception e)
			{
				BetaPlus.LOGGER.error("ModelsCache: Couldn't get model! Loc: " + location);
				//e.printStackTrace();
				model = ModelLoaderRegistry.getMissingModel();
			}
			this.modelCache.put(location, model);
		}
		return model;
	}

	public IBakedModel getBakedModel(final ResourceLocation location) {
		return this.getBakedModel(location, ModelHelper.DEFAULT_MODEL_STATE, ModelHelper.DEFAULT_VERTEX_FORMAT, ModelHelper.DEFAULT_TEXTURE_GETTER);
	}

	public IBakedModel getBakedModel(final ResourceLocation location, final IModelState state, final VertexFormat format, final Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		BetaPlus.LOGGER.info("Location Cache Search: " + location.toString());

		IBakedModel bakedModel = this.bakedCache.get(location);
		if (bakedModel == null && !hasRender)
		{
			// We made it here, time to celebrate! (Don't!)
			IUnbakedModel model = this.getModel(location);
			//BetaPlus.LOGGER.info("Before Baked: " + location + "; " + model.toString());

			bakedModel = model.bake(ModelLoader.defaultModelGetter(), textureGetter, state, false, format);
			BetaPlus.LOGGER.info("Past Baked: " + bakedModel.toString());
			this.bakedCache.put(location, bakedModel);
			hasRender = true; //Debug
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
