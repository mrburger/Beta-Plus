package net.minecraft.client.renderer.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockPart {
   public final Vector3f positionFrom;
   public final Vector3f positionTo;
   public final Map<EnumFacing, BlockPartFace> mapFaces;
   public final BlockPartRotation partRotation;
   public final boolean shade;

   public BlockPart(Vector3f p_i47624_1_, Vector3f p_i47624_2_, Map<EnumFacing, BlockPartFace> p_i47624_3_, @Nullable BlockPartRotation p_i47624_4_, boolean p_i47624_5_) {
      this.positionFrom = p_i47624_1_;
      this.positionTo = p_i47624_2_;
      this.mapFaces = p_i47624_3_;
      this.partRotation = p_i47624_4_;
      this.shade = p_i47624_5_;
      this.setDefaultUvs();
   }

   private void setDefaultUvs() {
      for(Entry<EnumFacing, BlockPartFace> entry : this.mapFaces.entrySet()) {
         float[] afloat = this.getFaceUvs(entry.getKey());
         (entry.getValue()).blockFaceUV.setUvs(afloat);
      }

   }

   private float[] getFaceUvs(EnumFacing facing) {
      switch(facing) {
      case DOWN:
         return new float[]{this.positionFrom.getX(), 16.0F - this.positionTo.getZ(), this.positionTo.getX(), 16.0F - this.positionFrom.getZ()};
      case UP:
         return new float[]{this.positionFrom.getX(), this.positionFrom.getZ(), this.positionTo.getX(), this.positionTo.getZ()};
      case NORTH:
      default:
         return new float[]{16.0F - this.positionTo.getX(), 16.0F - this.positionTo.getY(), 16.0F - this.positionFrom.getX(), 16.0F - this.positionFrom.getY()};
      case SOUTH:
         return new float[]{this.positionFrom.getX(), 16.0F - this.positionTo.getY(), this.positionTo.getX(), 16.0F - this.positionFrom.getY()};
      case WEST:
         return new float[]{this.positionFrom.getZ(), 16.0F - this.positionTo.getY(), this.positionTo.getZ(), 16.0F - this.positionFrom.getY()};
      case EAST:
         return new float[]{16.0F - this.positionTo.getZ(), 16.0F - this.positionTo.getY(), 16.0F - this.positionFrom.getZ(), 16.0F - this.positionFrom.getY()};
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Deserializer implements JsonDeserializer<BlockPart> {
      public BlockPart deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         Vector3f vector3f = this.func_199330_e(jsonobject);
         Vector3f vector3f1 = this.func_199329_d(jsonobject);
         BlockPartRotation blockpartrotation = this.parseRotation(jsonobject);
         Map<EnumFacing, BlockPartFace> map = this.parseFacesCheck(p_deserialize_3_, jsonobject);
         if (jsonobject.has("shade") && !JsonUtils.isBoolean(jsonobject, "shade")) {
            throw new JsonParseException("Expected shade to be a Boolean");
         } else {
            boolean flag = JsonUtils.getBoolean(jsonobject, "shade", true);
            return new BlockPart(vector3f, vector3f1, map, blockpartrotation, flag);
         }
      }

      @Nullable
      private BlockPartRotation parseRotation(JsonObject object) {
         BlockPartRotation blockpartrotation = null;
         if (object.has("rotation")) {
            JsonObject jsonobject = JsonUtils.getJsonObject(object, "rotation");
            Vector3f vector3f = this.func_199328_a(jsonobject, "origin");
            vector3f.mul(0.0625F);
            EnumFacing.Axis enumfacing$axis = this.parseAxis(jsonobject);
            float f = this.parseAngle(jsonobject);
            boolean flag = JsonUtils.getBoolean(jsonobject, "rescale", false);
            blockpartrotation = new BlockPartRotation(vector3f, enumfacing$axis, f, flag);
         }

         return blockpartrotation;
      }

      private float parseAngle(JsonObject object) {
         float f = JsonUtils.getFloat(object, "angle");
         if (f != 0.0F && MathHelper.abs(f) != 22.5F && MathHelper.abs(f) != 45.0F) {
            throw new JsonParseException("Invalid rotation " + f + " found, only -45/-22.5/0/22.5/45 allowed");
         } else {
            return f;
         }
      }

      private EnumFacing.Axis parseAxis(JsonObject object) {
         String s = JsonUtils.getString(object, "axis");
         EnumFacing.Axis enumfacing$axis = EnumFacing.Axis.byName(s.toLowerCase(Locale.ROOT));
         if (enumfacing$axis == null) {
            throw new JsonParseException("Invalid rotation axis: " + s);
         } else {
            return enumfacing$axis;
         }
      }

      private Map<EnumFacing, BlockPartFace> parseFacesCheck(JsonDeserializationContext deserializationContext, JsonObject object) {
         Map<EnumFacing, BlockPartFace> map = this.parseFaces(deserializationContext, object);
         if (map.isEmpty()) {
            throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
         } else {
            return map;
         }
      }

      private Map<EnumFacing, BlockPartFace> parseFaces(JsonDeserializationContext deserializationContext, JsonObject object) {
         Map<EnumFacing, BlockPartFace> map = Maps.newEnumMap(EnumFacing.class);
         JsonObject jsonobject = JsonUtils.getJsonObject(object, "faces");

         for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
            EnumFacing enumfacing = this.parseEnumFacing(entry.getKey());
            map.put(enumfacing, deserializationContext.deserialize(entry.getValue(), BlockPartFace.class));
         }

         return map;
      }

      private EnumFacing parseEnumFacing(String name) {
         EnumFacing enumfacing = EnumFacing.byName(name);
         if (enumfacing == null) {
            throw new JsonParseException("Unknown facing: " + name);
         } else {
            return enumfacing;
         }
      }

      private Vector3f func_199329_d(JsonObject p_199329_1_) {
         Vector3f vector3f = this.func_199328_a(p_199329_1_, "to");
         if (!(vector3f.getX() < -16.0F) && !(vector3f.getY() < -16.0F) && !(vector3f.getZ() < -16.0F) && !(vector3f.getX() > 32.0F) && !(vector3f.getY() > 32.0F) && !(vector3f.getZ() > 32.0F)) {
            return vector3f;
         } else {
            throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + vector3f);
         }
      }

      private Vector3f func_199330_e(JsonObject p_199330_1_) {
         Vector3f vector3f = this.func_199328_a(p_199330_1_, "from");
         if (!(vector3f.getX() < -16.0F) && !(vector3f.getY() < -16.0F) && !(vector3f.getZ() < -16.0F) && !(vector3f.getX() > 32.0F) && !(vector3f.getY() > 32.0F) && !(vector3f.getZ() > 32.0F)) {
            return vector3f;
         } else {
            throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + vector3f);
         }
      }

      private Vector3f func_199328_a(JsonObject p_199328_1_, String p_199328_2_) {
         JsonArray jsonarray = JsonUtils.getJsonArray(p_199328_1_, p_199328_2_);
         if (jsonarray.size() != 3) {
            throw new JsonParseException("Expected 3 " + p_199328_2_ + " values, found: " + jsonarray.size());
         } else {
            float[] afloat = new float[3];

            for(int i = 0; i < afloat.length; ++i) {
               afloat[i] = JsonUtils.getFloat(jsonarray.get(i), p_199328_2_ + "[" + i + "]");
            }

            return new Vector3f(afloat[0], afloat[1], afloat[2]);
         }
      }
   }
}