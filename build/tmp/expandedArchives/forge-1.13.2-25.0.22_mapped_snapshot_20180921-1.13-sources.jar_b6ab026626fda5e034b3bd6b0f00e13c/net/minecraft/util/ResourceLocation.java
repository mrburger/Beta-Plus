package net.minecraft.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.util.text.TextComponentTranslation;

public class ResourceLocation implements Comparable<ResourceLocation> {
   private static final SimpleCommandExceptionType field_200118_c = new SimpleCommandExceptionType(new TextComponentTranslation("argument.id.invalid"));
   protected final String namespace;
   protected final String path;

   protected ResourceLocation(String[] p_i47923_1_) {
      this.namespace = org.apache.commons.lang3.StringUtils.isEmpty(p_i47923_1_[0]) ? "minecraft" : p_i47923_1_[0];
      this.path = p_i47923_1_[1];
      if (!this.namespace.chars().allMatch((p_195825_0_) -> {
         return p_195825_0_ == 95 || p_195825_0_ == 45 || p_195825_0_ >= 97 && p_195825_0_ <= 122 || p_195825_0_ >= 48 && p_195825_0_ <= 57 || p_195825_0_ == 46;
      })) {
         throw new ResourceLocationException("Non [a-z0-9_.-] character in namespace of location: " + this.namespace + ':' + this.path);
      } else if (!this.path.chars().allMatch((p_195827_0_) -> {
         return p_195827_0_ == 95 || p_195827_0_ == 45 || p_195827_0_ >= 97 && p_195827_0_ <= 122 || p_195827_0_ >= 48 && p_195827_0_ <= 57 || p_195827_0_ == 47 || p_195827_0_ == 46;
      })) {
         throw new ResourceLocationException("Non [a-z0-9/._-] character in path of location: " + this.namespace + ':' + this.path);
      }
   }

   public ResourceLocation(String resourceName) {
      this(decompose(resourceName, ':'));
   }

   public ResourceLocation(String namespaceIn, String pathIn) {
      this(new String[]{namespaceIn, pathIn});
   }

   public static ResourceLocation of(String p_195828_0_, char p_195828_1_) {
      return new ResourceLocation(decompose(p_195828_0_, p_195828_1_));
   }

   @Nullable
   public static ResourceLocation makeResourceLocation(String string) {
      try {
         return new ResourceLocation(string);
      } catch (ResourceLocationException var2) {
         return null;
      }
   }

   protected static String[] decompose(String p_195823_0_, char p_195823_1_) {
      String[] astring = new String[]{"minecraft", p_195823_0_};
      int i = p_195823_0_.indexOf(p_195823_1_);
      if (i >= 0) {
         astring[1] = p_195823_0_.substring(i + 1, p_195823_0_.length());
         if (i >= 1) {
            astring[0] = p_195823_0_.substring(0, i);
         }
      }

      return astring;
   }

   public String getPath() {
      return this.path;
   }

   public String getNamespace() {
      return this.namespace;
   }

   public String toString() {
      return this.namespace + ':' + this.path;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof ResourceLocation)) {
         return false;
      } else {
         ResourceLocation resourcelocation = (ResourceLocation)p_equals_1_;
         return this.namespace.equals(resourcelocation.namespace) && this.path.equals(resourcelocation.path);
      }
   }

   public int hashCode() {
      return 31 * this.namespace.hashCode() + this.path.hashCode();
   }

   public int compareTo(ResourceLocation p_compareTo_1_) {
      int i = this.path.compareTo(p_compareTo_1_.path);
      if (i == 0) {
         i = this.namespace.compareTo(p_compareTo_1_.namespace);
      }

      return i;
   }

   public static ResourceLocation read(StringReader reader) throws CommandSyntaxException {
      int i = reader.getCursor();

      while(reader.canRead() && isValidPathCharacter(reader.peek())) {
         reader.skip();
      }

      String s = reader.getString().substring(i, reader.getCursor());

      try {
         return new ResourceLocation(s);
      } catch (ResourceLocationException var4) {
         reader.setCursor(i);
         throw field_200118_c.createWithContext(reader);
      }
   }

   public static boolean isValidPathCharacter(char charIn) {
      return charIn >= '0' && charIn <= '9' || charIn >= 'a' && charIn <= 'z' || charIn == '_' || charIn == ':' || charIn == '/' || charIn == '.' || charIn == '-';
   }

   public static class Serializer implements JsonDeserializer<ResourceLocation>, JsonSerializer<ResourceLocation> {
      public ResourceLocation deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return new ResourceLocation(JsonUtils.getString(p_deserialize_1_, "location"));
      }

      public JsonElement serialize(ResourceLocation p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         return new JsonPrimitive(p_serialize_1_.toString());
      }
   }
}