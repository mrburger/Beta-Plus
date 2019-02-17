package net.minecraft.client.renderer.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ModelBlock implements IUnbakedModel {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
   private static final FaceBakery FACE_BAKERY = new FaceBakery();
   @VisibleForTesting
   static final Gson SERIALIZER = (new GsonBuilder()).registerTypeAdapter(ModelBlock.class, new ModelBlock.Deserializer()).registerTypeAdapter(BlockPart.class, new BlockPart.Deserializer()).registerTypeAdapter(BlockPartFace.class, new BlockPartFace.Deserializer()).registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer()).registerTypeAdapter(ItemTransformVec3f.class, new ItemTransformVec3f.Deserializer()).registerTypeAdapter(ItemCameraTransforms.class, new ItemCameraTransforms.Deserializer()).registerTypeAdapter(ItemOverride.class, new ItemOverride.Deserializer()).create();
   private final List<BlockPart> elements;
   private final boolean gui3d;
   public final boolean ambientOcclusion;
   private final ItemCameraTransforms cameraTransforms;
   private final List<ItemOverride> overrides;
   public String name = "";
   @VisibleForTesting
   public final Map<String, String> textures;
   @VisibleForTesting
   public ModelBlock parent;
   @VisibleForTesting
   ResourceLocation parentLocation;

   public static ModelBlock deserialize(Reader readerIn) {
      return JsonUtils.fromJson(SERIALIZER, readerIn, ModelBlock.class);
   }

   public static ModelBlock deserialize(String jsonString) {
      return deserialize(new StringReader(jsonString));
   }

   public ModelBlock(@Nullable ResourceLocation parentLocationIn, List<BlockPart> elementsIn, Map<String, String> texturesIn, boolean ambientOcclusionIn, boolean gui3dIn, ItemCameraTransforms cameraTransformsIn, List<ItemOverride> overridesIn) {
      this.elements = elementsIn;
      this.ambientOcclusion = ambientOcclusionIn;
      this.gui3d = gui3dIn;
      this.textures = texturesIn;
      this.parentLocation = parentLocationIn;
      this.cameraTransforms = cameraTransformsIn;
      this.overrides = overridesIn;
   }

   public List<BlockPart> getElements() {
      return this.elements.isEmpty() && this.hasParent() ? this.parent.getElements() : this.elements;
   }

   private boolean hasParent() {
      return this.parent != null;
   }

   @Nullable
   public ResourceLocation getParentLocation() { return parentLocation; }

   public boolean isAmbientOcclusion() {
      return this.hasParent() ? this.parent.isAmbientOcclusion() : this.ambientOcclusion;
   }

   public boolean isGui3d() {
      return this.gui3d;
   }

   public boolean isResolved() {
      return this.parentLocation == null || this.parent != null && this.parent.isResolved();
   }

   private void resolveParent(Function<ResourceLocation, IUnbakedModel> modelGetter) {
      if (this.parentLocation != null) {
         IUnbakedModel iunbakedmodel = modelGetter.apply(this.parentLocation);
         if (iunbakedmodel != null) {
            if (!(iunbakedmodel instanceof ModelBlock)) {
               throw new IllegalStateException("BlockModel parent has to be a block model.");
            }

            this.parent = (ModelBlock)iunbakedmodel;
         }
      }

   }

   public List<ItemOverride> getOverrides() {
      return this.overrides;
   }

   public ItemOverrideList getOverrides(ModelBlock model, Function<ResourceLocation, IUnbakedModel> modelGetter, Function<ResourceLocation, TextureAtlasSprite> spriteGetter) {
      return this.overrides.isEmpty() ? ItemOverrideList.EMPTY : new ItemOverrideList(model, modelGetter, spriteGetter, this.overrides);
   }

   public Collection<ResourceLocation> getOverrideLocations() {
      Set<ResourceLocation> set = Sets.newHashSet();

      for(ItemOverride itemoverride : this.overrides) {
         set.add(itemoverride.getLocation());
      }

      if (this.parentLocation != null) {
         set.add(this.parentLocation);
      }

      return set;
   }

   public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
      if (!this.isResolved()) {
         Set<ModelBlock> set = Sets.newLinkedHashSet();
         ModelBlock modelblock = this;

         while(true) {
            set.add(modelblock);
            modelblock.resolveParent(modelGetter);
            if (set.contains(modelblock.parent)) {
               LOGGER.warn("Found 'parent' loop while loading model '{}' in chain: {} -> {}", modelblock.name, set.stream().map((p_209570_0_) -> {
                  return p_209570_0_.name;
               }).collect(Collectors.joining(" -> ")), modelblock.parent.name);
               modelblock.parentLocation = ModelBakery.MODEL_MISSING;
               modelblock.resolveParent(modelGetter);
            }

            modelblock = modelblock.parent;
            if (modelblock.isResolved()) {
               break;
            }
         }
      }

      Set<ResourceLocation> set1 = Sets.newHashSet(new ResourceLocation(this.resolveTextureName("particle")));

      for(BlockPart blockpart : this.getElements()) {
         for(BlockPartFace blockpartface : blockpart.mapFaces.values()) {
            String s = this.resolveTextureName(blockpartface.texture);
            if (Objects.equals(s, MissingTextureSprite.getSprite().getName().toString())) {
               missingTextureErrors.add(String.format("%s in %s", blockpartface.texture, this.name));
            }

            set1.add(new ResourceLocation(s));
         }
      }

      this.overrides.forEach((p_209564_4_) -> {
         IUnbakedModel iunbakedmodel = modelGetter.apply(p_209564_4_.getLocation());
         if (!Objects.equals(iunbakedmodel, this)) {
            set1.addAll(iunbakedmodel.getTextures(modelGetter, missingTextureErrors));
         }
      });
      if (this.getRootModel() == ModelBakery.MODEL_GENERATED) {
         ItemModelGenerator.LAYERS.forEach((p_209569_2_) -> {
            set1.add(new ResourceLocation(this.resolveTextureName(p_209569_2_)));
         });
      }

      return set1;
   }

   @Override
   public IBakedModel bake(Function<ResourceLocation, IUnbakedModel> modelGetter, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, net.minecraftforge.common.model.IModelState rotationIn, boolean uvlock, net.minecraft.client.renderer.vertex.VertexFormat format) {
       if (!net.minecraftforge.client.model.Attributes.moreSpecific(format, net.minecraftforge.client.model.Attributes.DEFAULT_BAKED_FORMAT)) {
           throw new IllegalArgumentException("Cannot bake vanilla model to format other than BLOCK");
   }
       return bake(this, modelGetter, spriteGetter, rotationIn, uvlock);
   }

   private IBakedModel bake(ModelBlock model, Function<ResourceLocation, IUnbakedModel> modelGetter, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, net.minecraftforge.common.model.IModelState rotationIn, boolean uvlock) {
      ModelBlock modelblock = this.getRootModel();
      if (modelblock == ModelBakery.MODEL_GENERATED) {
         return ITEM_MODEL_GENERATOR.makeItemModel(spriteGetter, this).bake(model, modelGetter, spriteGetter, rotationIn, uvlock);
      } else if (modelblock == ModelBakery.MODEL_ENTITY) {
         return new BuiltInModel(this.getAllTransforms(), this.getOverrides(model, modelGetter, spriteGetter));
      } else {
         TextureAtlasSprite textureatlassprite = spriteGetter.apply(new ResourceLocation(this.resolveTextureName("particle")));
         SimpleBakedModel.Builder simplebakedmodel$builder = (new SimpleBakedModel.Builder(this, this.getOverrides(model, modelGetter, spriteGetter))).setTexture(textureatlassprite);

         for(BlockPart blockpart : this.getElements()) {
            for(EnumFacing enumfacing : blockpart.mapFaces.keySet()) {
               BlockPartFace blockpartface = blockpart.mapFaces.get(enumfacing);
               TextureAtlasSprite textureatlassprite1 = spriteGetter.apply(new ResourceLocation(this.resolveTextureName(blockpartface.texture)));
               if (blockpartface.cullFace == null) {
                  simplebakedmodel$builder.addGeneralQuad(makeBakedQuad(blockpart, blockpartface, textureatlassprite1, enumfacing, rotationIn, uvlock));
               } else {

                  simplebakedmodel$builder.addFaceQuad(rotationIn.apply(java.util.Optional.empty()).map(trsr -> trsr.rotate(enumfacing)).orElse(enumfacing), makeBakedQuad(blockpart, blockpartface, textureatlassprite1, enumfacing, rotationIn, uvlock));
               }
            }
         }

         return simplebakedmodel$builder.build();
      }
   }

   private static BakedQuad makeFaceQuad(BlockPart part, BlockPartFace partFace, TextureAtlasSprite sprite, EnumFacing face, ModelRotation rotationIn, boolean uvlock) {
      return makeBakedQuad(part, partFace, sprite, face, (net.minecraftforge.common.model.IModelState) rotationIn, uvlock);
   }

   public static BakedQuad makeBakedQuad(BlockPart p_209567_0, BlockPartFace partFace, TextureAtlasSprite sprite, EnumFacing face, net.minecraftforge.common.model.IModelState rotationIn, boolean uvlock) {
      return FACE_BAKERY.makeBakedQuad(p_209567_0.positionFrom, p_209567_0.positionTo, partFace, sprite, face, rotationIn.apply(java.util.Optional.empty()).orElse(net.minecraftforge.common.model.TRSRTransformation.identity()), p_209567_0.partRotation, uvlock, p_209567_0.shade);
   }

   public boolean isTexturePresent(String textureName) {
      return !MissingTextureSprite.getSprite().getName().toString().equals(this.resolveTextureName(textureName));
   }

   public String resolveTextureName(String textureName) {
      if (!this.startsWithHash(textureName)) {
         textureName = '#' + textureName;
      }

      return this.resolveTextureName(textureName, new ModelBlock.Bookkeep(this));
   }

   private String resolveTextureName(String textureName, ModelBlock.Bookkeep p_178302_2_) {
      if (this.startsWithHash(textureName)) {
         if (this == p_178302_2_.modelExt) {
            LOGGER.warn("Unable to resolve texture due to upward reference: {} in {}", textureName, this.name);
            return MissingTextureSprite.getSprite().getName().toString();
         } else {
            String s = this.textures.get(textureName.substring(1));
            if (s == null && this.hasParent()) {
               s = this.parent.resolveTextureName(textureName, p_178302_2_);
            }

            p_178302_2_.modelExt = this;
            if (s != null && this.startsWithHash(s)) {
               s = p_178302_2_.model.resolveTextureName(s, p_178302_2_);
            }

            return s != null && !this.startsWithHash(s) ? s : MissingTextureSprite.getSprite().getName().toString();
         }
      } else {
         return textureName;
      }
   }

   private boolean startsWithHash(String hash) {
      return hash.charAt(0) == '#';
   }

   public ModelBlock getRootModel() {
      return this.hasParent() ? this.parent.getRootModel() : this;
   }

   public ItemCameraTransforms getAllTransforms() {
      ItemTransformVec3f itemtransformvec3f = this.getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
      ItemTransformVec3f itemtransformvec3f1 = this.getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
      ItemTransformVec3f itemtransformvec3f2 = this.getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
      ItemTransformVec3f itemtransformvec3f3 = this.getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
      ItemTransformVec3f itemtransformvec3f4 = this.getTransform(ItemCameraTransforms.TransformType.HEAD);
      ItemTransformVec3f itemtransformvec3f5 = this.getTransform(ItemCameraTransforms.TransformType.GUI);
      ItemTransformVec3f itemtransformvec3f6 = this.getTransform(ItemCameraTransforms.TransformType.GROUND);
      ItemTransformVec3f itemtransformvec3f7 = this.getTransform(ItemCameraTransforms.TransformType.FIXED);
      return new ItemCameraTransforms(itemtransformvec3f, itemtransformvec3f1, itemtransformvec3f2, itemtransformvec3f3, itemtransformvec3f4, itemtransformvec3f5, itemtransformvec3f6, itemtransformvec3f7);
   }

   private ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType type) {
      return this.parent != null && !this.cameraTransforms.hasCustomTransform(type) ? this.parent.getTransform(type) : this.cameraTransforms.getTransform(type);
   }

   @OnlyIn(Dist.CLIENT)
   static final class Bookkeep {
      public final ModelBlock model;
      public ModelBlock modelExt;

      private Bookkeep(ModelBlock modelIn) {
         this.model = modelIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<ModelBlock> {
      public ModelBlock deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         List<BlockPart> list = this.getModelElements(p_deserialize_3_, jsonobject);
         String s = this.getParent(jsonobject);
         Map<String, String> map = this.getTextures(jsonobject);
         boolean flag = this.getAmbientOcclusionEnabled(jsonobject);
         ItemCameraTransforms itemcameratransforms = ItemCameraTransforms.DEFAULT;
         if (jsonobject.has("display")) {
            JsonObject jsonobject1 = JsonUtils.getJsonObject(jsonobject, "display");
            itemcameratransforms = p_deserialize_3_.deserialize(jsonobject1, ItemCameraTransforms.class);
         }

         List<ItemOverride> list1 = this.getItemOverrides(p_deserialize_3_, jsonobject);
         ResourceLocation resourcelocation = s.isEmpty() ? null : new ResourceLocation(s);
         return new ModelBlock(resourcelocation, list, map, flag, true, itemcameratransforms, list1);
      }

      protected List<ItemOverride> getItemOverrides(JsonDeserializationContext deserializationContext, JsonObject object) {
         List<ItemOverride> list = Lists.newArrayList();
         if (object.has("overrides")) {
            for(JsonElement jsonelement : JsonUtils.getJsonArray(object, "overrides")) {
               list.add(deserializationContext.deserialize(jsonelement, ItemOverride.class));
            }
         }

         return list;
      }

      private Map<String, String> getTextures(JsonObject object) {
         Map<String, String> map = Maps.newHashMap();
         if (object.has("textures")) {
            JsonObject jsonobject = object.getAsJsonObject("textures");

            for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
               map.put(entry.getKey(), entry.getValue().getAsString());
            }
         }

         return map;
      }

      private String getParent(JsonObject object) {
         return JsonUtils.getString(object, "parent", "");
      }

      protected boolean getAmbientOcclusionEnabled(JsonObject object) {
         return JsonUtils.getBoolean(object, "ambientocclusion", true);
      }

      protected List<BlockPart> getModelElements(JsonDeserializationContext deserializationContext, JsonObject object) {
         List<BlockPart> list = Lists.newArrayList();
         if (object.has("elements")) {
            for(JsonElement jsonelement : JsonUtils.getJsonArray(object, "elements")) {
               list.add(deserializationContext.deserialize(jsonelement, BlockPart.class));
            }
         }

         return list;
      }
   }
}