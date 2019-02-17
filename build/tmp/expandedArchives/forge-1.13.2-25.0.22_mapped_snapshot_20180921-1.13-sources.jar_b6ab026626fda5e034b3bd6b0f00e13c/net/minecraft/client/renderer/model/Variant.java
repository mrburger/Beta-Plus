package net.minecraft.client.renderer.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Variant implements net.minecraftforge.client.model.ISmartVariant {
   private final ResourceLocation modelLocation;
   private final ModelRotation rotation;
   private final boolean uvLock;
   private final int weight;

   public Variant(ResourceLocation modelLocationIn, ModelRotation rotationIn, boolean uvLockIn, int weightIn) {
      this.modelLocation = modelLocationIn;
      this.rotation = rotationIn;
      this.uvLock = uvLockIn;
      this.weight = weightIn;
   }

   public ResourceLocation getModelLocation() {
      return this.modelLocation;
   }

   @Deprecated
   public ModelRotation getRotation() {
      return this.rotation;
   }

   public net.minecraftforge.common.model.IModelState getState() {
      return this.rotation;
   }

   public boolean isUvLock() {
      return this.uvLock;
   }

   public int getWeight() {
      return this.weight;
   }

   public String toString() {
      return "Variant{modelLocation=" + this.modelLocation + ", rotation=" + this.rotation + ", uvLock=" + this.uvLock + ", weight=" + this.weight + '}';
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof Variant)) {
         return false;
      } else {
         Variant variant = (Variant)p_equals_1_;
         return this.modelLocation.equals(variant.modelLocation) && this.rotation == variant.rotation && this.uvLock == variant.uvLock && this.weight == variant.weight;
      }
   }

   public int hashCode() {
      int i = this.modelLocation.hashCode();
      i = 31 * i + this.rotation.hashCode();
      i = 31 * i + Boolean.valueOf(this.uvLock).hashCode();
      i = 31 * i + this.weight;
      return i;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<Variant> {
      public Variant deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         ResourceLocation resourcelocation = this.getStringModel(jsonobject);
         ModelRotation modelrotation = this.parseModelRotation(jsonobject);
         boolean flag = this.parseUvLock(jsonobject);
         int i = this.parseWeight(jsonobject);
         return new Variant(resourcelocation, modelrotation, flag, i);
      }

      private boolean parseUvLock(JsonObject json) {
         return JsonUtils.getBoolean(json, "uvlock", false);
      }

      protected ModelRotation parseModelRotation(JsonObject json) {
         int i = JsonUtils.getInt(json, "x", 0);
         int j = JsonUtils.getInt(json, "y", 0);
         ModelRotation modelrotation = ModelRotation.getModelRotation(i, j);
         if (modelrotation == null) {
            throw new JsonParseException("Invalid BlockModelRotation x: " + i + ", y: " + j);
         } else {
            return modelrotation;
         }
      }

      protected ResourceLocation getStringModel(JsonObject json) {
         return new ResourceLocation(JsonUtils.getString(json, "model"));
      }

      protected int parseWeight(JsonObject json) {
         int i = JsonUtils.getInt(json, "weight", 1);
         if (i < 1) {
            throw new JsonParseException("Invalid weight " + i + " found, expected integer >= 1");
         } else {
            return i;
         }
      }
   }
}