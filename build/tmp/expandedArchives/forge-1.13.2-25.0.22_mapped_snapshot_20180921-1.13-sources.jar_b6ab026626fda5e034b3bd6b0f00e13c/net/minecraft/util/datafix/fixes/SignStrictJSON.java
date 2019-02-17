package net.minecraft.util.datafix.fixes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.StringUtils;

public class SignStrictJSON extends NamedEntityFix {
   public static final Gson GSON_INSTANCE = (new GsonBuilder()).registerTypeAdapter(ITextComponent.class, new JsonDeserializer<ITextComponent>() {
      public ITextComponent deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         if (p_deserialize_1_.isJsonPrimitive()) {
            return new TextComponentString(p_deserialize_1_.getAsString());
         } else if (p_deserialize_1_.isJsonArray()) {
            JsonArray jsonarray = p_deserialize_1_.getAsJsonArray();
            ITextComponent itextcomponent = null;

            for(JsonElement jsonelement : jsonarray) {
               ITextComponent itextcomponent1 = this.deserialize(jsonelement, jsonelement.getClass(), p_deserialize_3_);
               if (itextcomponent == null) {
                  itextcomponent = itextcomponent1;
               } else {
                  itextcomponent.appendSibling(itextcomponent1);
               }
            }

            return itextcomponent;
         } else {
            throw new JsonParseException("Don't know how to turn " + p_deserialize_1_ + " into a Component");
         }
      }
   }).create();

   public SignStrictJSON(Schema p_i49680_1_, boolean p_i49680_2_) {
      super(p_i49680_1_, p_i49680_2_, "BlockEntitySignTextStrictJsonFix", TypeReferences.BLOCK_ENTITY, "Sign");
   }

   private Dynamic<?> updateLine(Dynamic<?> p_209647_1_, String p_209647_2_) {
      String s = p_209647_1_.getString(p_209647_2_);
      ITextComponent itextcomponent = null;
      if (!"null".equals(s) && !StringUtils.isEmpty(s)) {
         if (s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"' || s.charAt(0) == '{' && s.charAt(s.length() - 1) == '}') {
            try {
               itextcomponent = JsonUtils.fromJson(GSON_INSTANCE, s, ITextComponent.class, true);
               if (itextcomponent == null) {
                  itextcomponent = new TextComponentString("");
               }
            } catch (JsonParseException var8) {
               ;
            }

            if (itextcomponent == null) {
               try {
                  itextcomponent = ITextComponent.Serializer.fromJson(s);
               } catch (JsonParseException var7) {
                  ;
               }
            }

            if (itextcomponent == null) {
               try {
                  itextcomponent = ITextComponent.Serializer.fromJsonLenient(s);
               } catch (JsonParseException var6) {
                  ;
               }
            }

            if (itextcomponent == null) {
               itextcomponent = new TextComponentString(s);
            }
         } else {
            itextcomponent = new TextComponentString(s);
         }
      } else {
         itextcomponent = new TextComponentString("");
      }

      return p_209647_1_.set(p_209647_2_, p_209647_1_.createString(ITextComponent.Serializer.toJson(itextcomponent)));
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      return p_207419_1_.update(DSL.remainderFinder(), (p_206380_1_) -> {
         p_206380_1_ = this.updateLine(p_206380_1_, "Text1");
         p_206380_1_ = this.updateLine(p_206380_1_, "Text2");
         p_206380_1_ = this.updateLine(p_206380_1_, "Text3");
         p_206380_1_ = this.updateLine(p_206380_1_, "Text4");
         return p_206380_1_;
      });
   }
}