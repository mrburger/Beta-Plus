package com.mrburgerus.betaplus.client.render;

import com.google.common.base.Charsets;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mrburgerus.betaplus.BetaPlus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.animation.ModelBlockAnimation;
import net.minecraftforge.common.util.JsonUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

/* Graciously Provided by Cadiboo */
/* UPDATING FOR 1.14.4 */
/* SPECIAL MODELS?? */
@OnlyIn(Dist.CLIENT)
public enum ModelLoaderAlpha implements ICustomModelLoader
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
		//BetaPlus.LOGGER.info("Model Location Raw: " + modelLocation);
		if( modelLocation.getPath().startsWith( "models/" ) )
		{
			modelPath = modelPath.substring( "models/".length() );
		}
		ResourceLocation location = new ResourceLocation( "betaplus:models/" + modelPath + ".json" );
		//BetaPlus.LOGGER.info("Trying to accept: " + modelPath);

		try(InputStreamReader io = new InputStreamReader( Minecraft.getInstance().getResourceManager()
				.getResource(location).getInputStream()))
		{
			return true;
		}
		catch( IOException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/* REFLECTION */
	@Override
	public IUnbakedModel loadModel(ResourceLocation modelLocation)
	{
		return new AlphaModel(modelLocation);
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
			vanillaModelWrapper = modelClass.getDeclaredConstructor(ModelLoader.class, ResourceLocation.class, BlockModel.class, boolean.class, ModelBlockAnimation.class);
			vanillaModelWrapper.setAccessible(true);
		}
		catch (ClassNotFoundException | NoSuchMethodException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	// Extends Unbaked instead of IModel?
	static <M extends IUnbakedModel> M vanillaModelWrapper(ModelLoader loader, ResourceLocation location, BlockModel model, boolean uvlock, ModelBlockAnimation animation)
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

	class AlphaModel implements IUnbakedModel
	{
		// Fields
		private final Gson SERIALIZER = ( new GsonBuilder() ).registerTypeAdapter( BlockModel.class, deserializer( BlockModel.class ) ).registerTypeAdapter( BlockPart.class, deserializer( BlockPart.class ) ).registerTypeAdapter( BlockPartFace.class, new BlockPartFaceOverrideSerializer() ).registerTypeAdapter( BlockFaceUV.class, deserializer( BlockFaceUV.class ) ).registerTypeAdapter( ItemTransformVec3f.class, deserializer( ItemTransformVec3f.class ) ).registerTypeAdapter( ItemCameraTransforms.class, deserializer( ItemCameraTransforms.class ) ).registerTypeAdapter( ItemOverride.class, deserializer( ItemOverride.class ) ).create();

		private final IUnbakedModel parent;


		AlphaModel(ResourceLocation resourceLocation)
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
			BlockModel model;
			Reader reader = null;
			IResource iresource = null;
			BlockModel modelBlock = null;

			try
			{
				iresource = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(resourceLocation.getNamespace(), "models/" + modelPath + ".json"));

				//BetaPlus.LOGGER.info("Ires: " + iresource.getLocation());
				reader = new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8);

				JsonReader jsonRead = new JsonReader(reader);

				modelBlock = SERIALIZER.getAdapter(TypeToken.get(BlockModel.class)).read(jsonRead);
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
			this.parent = ModelLoaderAlpha.vanillaModelWrapper(getLoader(), resourceLocation, model, false, animation);
		}

		@Override
		public Collection<ResourceLocation> getDependencies()
		{
			return parent.getDependencies();
		}

		@Override
		public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors)
		{
			return parent.getTextures(modelGetter, missingTextureErrors);
		}

		@Nullable
		@Override
		public IBakedModel bake(ModelBakery modelBakery, Function<ResourceLocation, TextureAtlasSprite> function, ISprite iSprite, VertexFormat vertexFormat)
		{
			return parent.bake(modelBakery, function, iSprite, vertexFormat);
		}
	}


	/* Not quite figured out */
	public class BlockPartFaceOverrideSerializer implements JsonDeserializer<BlockPartFace>
	{
		private Map<BlockPartFace, Pair<Float, Float>> uvlightmap = new HashMap<>();
		public BlockPartFace deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context ) throws JsonParseException
		{
			JsonObject jsonobject = jsonElement.getAsJsonObject();

			Pair uvPair = parseUVL(jsonobject);
			String tex = this.parseTexture(jsonobject);
			Direction direction = this.parseCullFace(jsonobject);
			int tint = this.parseTintIndex(jsonobject);
			BlockFaceUV blockFaceUV = context.deserialize(jsonobject, BlockFaceUV.class);
			BlockPartFace blockFace = new BlockPartFace(direction, tint, tex, blockFaceUV);
			uvlightmap.put( blockFace, uvPair);
			/*
			Direction enumfacing = this.parseCullFace( jsonobject );
			int i = this.parseTintIndex( jsonobject );
			String s = this.parseTexture( jsonobject );
			BlockFaceUV blockfaceuv = context.deserialize( jsonobject, BlockFaceUV.class );
			BlockPartFace blockFace = new BlockPartFace( enumfacing, i, s, blockfaceuv );
			uvlightmap.put( blockFace, parseUVL( jsonobject ) );
			*/
			return blockFace;
		}

		protected int parseTintIndex( JsonObject object )
		{
			return net.minecraft.util.JSONUtils.getInt( object, "tintindex", -1);
		}

		private String parseTexture( JsonObject object )
		{
			String s = "";
			if (object.has("texture"))
			{
				s = net.minecraft.util.JSONUtils.getString(object, "texture");
			}
			return s;
		}

		@Nullable
		private Direction parseCullFace(JsonObject object )
		{
			String s = "";
			if (object.has("cullface"))
			{
				s = net.minecraft.util.JSONUtils.getString(object, "cullface");
			}
			return Direction.byName(s);
		}

		protected Pair<Float, Float> parseUVL(JsonObject object )
		{
			if( !object.has( "uvlightmap" ) )
			{
				return null;
			}
			object = object.get( "uvlightmap" ).getAsJsonObject();
			return new ImmutablePair<Float, Float>( JsonUtils.readNBT( object, "sky").getFloat("sky"), JsonUtils.readNBT( object, "block").getFloat("block") );
		}
	}
}

