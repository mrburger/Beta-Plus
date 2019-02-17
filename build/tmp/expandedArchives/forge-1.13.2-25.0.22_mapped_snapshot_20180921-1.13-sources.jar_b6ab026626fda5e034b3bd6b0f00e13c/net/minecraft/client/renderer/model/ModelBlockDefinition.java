package net.minecraft.client.renderer.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.model.multipart.Multipart;
import net.minecraft.client.renderer.model.multipart.Selector;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelBlockDefinition {
   private final Map<String, VariantList> mapVariants = Maps.newLinkedHashMap();
   private Multipart multipart;

   @Deprecated
   public static ModelBlockDefinition fromJson(ModelBlockDefinition.ContainerHolder containerHolderIn, Reader readerIn) {
      return fromJson(containerHolderIn, readerIn, null);
   }

   public static ModelBlockDefinition fromJson(ModelBlockDefinition.ContainerHolder containerHolderIn, Reader readerIn, @Nullable net.minecraft.util.ResourceLocation location) {
      return net.minecraftforge.client.model.BlockStateLoader.load(readerIn, location, containerHolderIn.gson);
   }

   public ModelBlockDefinition(Map<String, VariantList> variants, Multipart multipartIn) {
      this.multipart = multipartIn;
      this.mapVariants.putAll(variants);
   }

   public ModelBlockDefinition(List<ModelBlockDefinition> definitions) {
      ModelBlockDefinition modelblockdefinition = null;

      for(ModelBlockDefinition modelblockdefinition1 : definitions) {
         if (modelblockdefinition1.hasMultipartData()) {
            this.mapVariants.clear();
            modelblockdefinition = modelblockdefinition1;
         }

         this.mapVariants.putAll(modelblockdefinition1.mapVariants);
      }

      if (modelblockdefinition != null) {
         this.multipart = modelblockdefinition.multipart;
      }

   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         if (p_equals_1_ instanceof ModelBlockDefinition) {
            ModelBlockDefinition modelblockdefinition = (ModelBlockDefinition)p_equals_1_;
            if (this.mapVariants.equals(modelblockdefinition.mapVariants)) {
               return this.hasMultipartData() ? this.multipart.equals(modelblockdefinition.multipart) : !modelblockdefinition.hasMultipartData();
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return 31 * this.mapVariants.hashCode() + (this.hasMultipartData() ? this.multipart.hashCode() : 0);
   }

   public Map<String, VariantList> func_209578_a() {
      return this.mapVariants;
   }

   public boolean hasMultipartData() {
      return this.multipart != null;
   }

   public Multipart getMultipartData() {
      return this.multipart;
   }

   @OnlyIn(Dist.CLIENT)
   public static final class ContainerHolder {
      @VisibleForTesting
      final Gson gson = (new GsonBuilder()).registerTypeAdapter(ModelBlockDefinition.class, new ModelBlockDefinition.Deserializer()).registerTypeAdapter(Variant.class, new Variant.Deserializer()).registerTypeAdapter(VariantList.class, new VariantList.Deserializer()).registerTypeAdapter(Multipart.class, new Multipart.Deserializer(this)).registerTypeAdapter(Selector.class, new Selector.Deserializer()).create();
      private StateContainer<Block, IBlockState> stateContainer;

      public StateContainer<Block, IBlockState> getStateContainer() {
         return this.stateContainer;
      }

      public void setStateContainer(StateContainer<Block, IBlockState> stateContainerIn) {
         this.stateContainer = stateContainerIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<ModelBlockDefinition> {
      public ModelBlockDefinition deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         Map<String, VariantList> map = this.parseMapVariants(p_deserialize_3_, jsonobject);
         Multipart multipart = this.parseMultipart(p_deserialize_3_, jsonobject);
         if (!map.isEmpty() || multipart != null && !multipart.getVariants().isEmpty()) {
            return new ModelBlockDefinition(map, multipart);
         } else {
            throw new JsonParseException("Neither 'variants' nor 'multipart' found");
         }
      }

      protected Map<String, VariantList> parseMapVariants(JsonDeserializationContext deserializationContext, JsonObject object) {
         Map<String, VariantList> map = Maps.newHashMap();
         if (object.has("variants")) {
            JsonObject jsonobject = JsonUtils.getJsonObject(object, "variants");

            for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
               map.put(entry.getKey(), deserializationContext.deserialize(entry.getValue(), VariantList.class));
            }
         }

         return map;
      }

      @Nullable
      protected Multipart parseMultipart(JsonDeserializationContext deserializationContext, JsonObject object) {
         if (!object.has("multipart")) {
            return null;
         } else {
            JsonArray jsonarray = JsonUtils.getJsonArray(object, "multipart");
            return deserializationContext.deserialize(jsonarray, Multipart.class);
         }
      }
   }
}