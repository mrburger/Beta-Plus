package com.mrburgerus.betaplus.client.renderer;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.gson.*;
import com.mrburgerus.betaplus.BetaPlus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.animation.ModelBlockAnimation;
import net.minecraftforge.common.model.IModelState;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

/* Graciously Provided by Cadiboo */
@OnlyIn(Dist.CLIENT)
public enum  GrassModelLoader implements ICustomModelLoader
{
	// Fields
	INSTANCE;

	public static final Constructor vanillaModelWrapper;
	private static final Gson gson = new Gson();
	private ModelLoader loader;

	public void setLoader(ModelLoader loader)
	{
		this.loader = loader;
	}

	public ModelLoader getLoader()
	{
		return loader;
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{

	}

	@Override
	public boolean accepts(ResourceLocation modelLocation)
	{
		String modelPath = modelLocation.getPath();
		BetaPlus.LOGGER.info("Model Location Raw: " + modelLocation);
		if( modelLocation.getPath().startsWith( "models/" ) )
		{
			modelPath = modelPath.substring( "models/".length() );
		}
		ResourceLocation location = new ResourceLocation( "betaplus:models/" + modelPath + ".json" );
		BetaPlus.LOGGER.info("Trying to accept: " + modelPath);

		try(InputStreamReader io = new InputStreamReader( Minecraft.getInstance().getResourceManager()
				.getResource(location).getInputStream()))
		{
			return true;
		}
		catch( IOException ignored)
		{

		}
		return false;
	}

	/* REFLECTION */
	@Override
	public IUnbakedModel loadModel(ResourceLocation modelLocation)
	{
		return new ModelAlphaGrass(modelLocation);
	}

	private static Object deserializer( Class clas )
	{
		try
		{
			clas = Class.forName( clas.getName() + "$Deserializer" );
			Constructor constr = clas.getDeclaredConstructor();
			constr.setAccessible( true );
			return constr.newInstance();
		}
		catch( Exception e )
		{
			throw new RuntimeException(e);
		}
	}

	static
	{
		try
		{
			Class modelClass = Class.forName(ModelLoader.class.getName() + "$VanillaModelWrapper");
			vanillaModelWrapper = modelClass.getDeclaredConstructor(ModelLoader.class, ResourceLocation.class, ModelBlock.class, boolean.class, ModelBlockAnimation.class);
			vanillaModelWrapper.setAccessible(true);
		}
		catch (ClassNotFoundException | NoSuchMethodException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	// Extends Unbaked instead of IModel?
	static <M extends IUnbakedModel> M vanillaModelWrapper(ModelLoader loader, ResourceLocation location, ModelBlock model, boolean uvlock, ModelBlockAnimation animation)
	{
		try
		{
			return (M) vanillaModelWrapper.newInstance(loader, location, model, uvlock, animation);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	class ModelAlphaGrass implements IUnbakedModel
	{
		// Fields
		private final Gson SERIALIZER = ( new GsonBuilder() ).registerTypeAdapter( ModelBlock.class, deserializer( ModelBlock.class ) ).registerTypeAdapter( BlockPart.class, deserializer( BlockPart.class ) ).registerTypeAdapter( BlockPartFace.class, new BlockPartFaceOverrideSerializer() ).registerTypeAdapter( BlockFaceUV.class, deserializer( BlockFaceUV.class ) ).registerTypeAdapter( ItemTransformVec3f.class, deserializer( ItemTransformVec3f.class ) ).registerTypeAdapter( ItemCameraTransforms.class, deserializer( ItemCameraTransforms.class ) ).registerTypeAdapter( ItemOverride.class, deserializer( ItemOverride.class ) ).create();

		private final IUnbakedModel parent;


		ModelAlphaGrass(ResourceLocation resourceLocation)
		{
			// Get the path
			String modelPath = resourceLocation.getPath();
			// If it starts with models, remove that segment.
			if (resourceLocation.getPath().startsWith("models/"))
			{
				modelPath = modelPath.substring("models/".length());
			}

			ResourceLocation armatureLocation = new ResourceLocation(resourceLocation.getNamespace(), "armatures/" + modelPath + ".json");
			ModelBlockAnimation animation = ModelBlockAnimation.loadVanillaAnimation(Minecraft.getInstance().getResourceManager(), armatureLocation);
			ModelBlock model;
			{
				Reader reader = null;
				IResource iresource = null;
				ModelBlock modelBlock = null;

				try
				{
					String s = resourceLocation.getPath();

					iresource = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(resourceLocation.getNamespace(), "models/" + modelPath + ".json"));
					reader = new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8);

					modelBlock = JsonUtils.fromJson(SERIALIZER, reader, ModelBlock.class, false);
					modelBlock.name = resourceLocation.toString();
				}
				catch (IOException e)
				{
					BetaPlus.LOGGER.error("Couldn't Read Model!");
					e.printStackTrace();
				}
				finally
				{
					IOUtils.closeQuietly(reader);
					IOUtils.closeQuietly(iresource);
				}

				model = modelBlock;
			}
			this.parent = GrassModelLoader.vanillaModelWrapper(getLoader(), resourceLocation, model, false, animation);
		}


		@Override
		public Collection<ResourceLocation> getOverrideLocations()
		{
			return parent.getOverrideLocations();
		}

		@Override
		public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors)
		{
			return parent.getTextures(modelGetter, missingTextureErrors);
		}

		@Nullable
		@Override
		public IBakedModel bake(Function<ResourceLocation, IUnbakedModel> modelGetter, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, IModelState state, boolean uvlock, VertexFormat format)
		{
			/*
			setFaceBakery( getLoader(), new FaceBakeryOverride() );
			IBakedModel model = parent.bake(state, format, spriteGetter);
			setFaceBakery(getLoader(), new FaceBakery() );
			*/
			BetaPlus.LOGGER.info("Baking!!!");
			IBakedModel model = parent.bake(modelGetter, spriteGetter, state, uvlock, format);
			return model;
		}
	}

	public class BlockPartFaceOverrideSerializer implements JsonDeserializer<BlockPartFace>
	{
		private Map<BlockPartFace, Pair<Float, Float>> uvlightmap = new HashMap<>();
		public BlockPartFace deserialize(JsonElement jsonElement, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_ ) throws JsonParseException
		{
			JsonObject jsonobject = jsonElement.getAsJsonObject();
			EnumFacing enumfacing = this.parseCullFace( jsonobject );
			int i = this.parseTintIndex( jsonobject );
			String s = this.parseTexture( jsonobject );
			BlockFaceUV blockfaceuv = (BlockFaceUV) p_deserialize_3_.deserialize( jsonobject, BlockFaceUV.class );
			BlockPartFace blockFace = new BlockPartFace( enumfacing, i, s, blockfaceuv );
			uvlightmap.put( blockFace, parseUVL( jsonobject ) );
			return blockFace;
		}

		protected int parseTintIndex( JsonObject object )
		{
			return JsonUtils.getInt( object, "tintindex", -1 );
		}

		private String parseTexture( JsonObject object )
		{
			return JsonUtils.getString( object, "texture" );
		}

		@Nullable
		private EnumFacing parseCullFace( JsonObject object )
		{
			String s = JsonUtils.getString( object, "cullface", "" );
			return EnumFacing.byName( s );
		}

		protected Pair<Float, Float> parseUVL(JsonObject object )
		{
			if( !object.has( "uvlightmap" ) )
			{
				return null;
			}
			object = object.get( "uvlightmap" ).getAsJsonObject();
			return new ImmutablePair<Float, Float>( JsonUtils.getFloat( object, "sky", 0 ), JsonUtils.getFloat( object, "block", 0 ) );
		}
	}
}

