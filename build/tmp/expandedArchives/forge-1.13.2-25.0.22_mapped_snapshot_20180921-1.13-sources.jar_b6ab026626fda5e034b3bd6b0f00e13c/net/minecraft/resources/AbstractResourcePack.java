package net.minecraft.resources;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractResourcePack implements IResourcePack {
   private static final Logger LOGGER = LogManager.getLogger();
   public final File file;

   public AbstractResourcePack(File resourcePackFileIn) {
      this.file = resourcePackFileIn;
   }

   private static String getFullPath(ResourcePackType type, ResourceLocation location) {
      return String.format("%s/%s/%s", type.getDirectoryName(), location.getNamespace(), location.getPath());
   }

   protected static String getRelativeString(File file1, File file2) {
      return file1.toURI().relativize(file2.toURI()).getPath();
   }

   public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException {
      return this.getInputStream(getFullPath(type, location));
   }

   public boolean resourceExists(ResourcePackType type, ResourceLocation location) {
      return this.resourceExists(getFullPath(type, location));
   }

   protected abstract InputStream getInputStream(String resourcePath) throws IOException;

   @OnlyIn(Dist.CLIENT)
   public InputStream getRootResourceStream(String fileName) throws IOException {
      if (!fileName.contains("/") && !fileName.contains("\\")) {
         return this.getInputStream(fileName);
      } else {
         throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
      }
   }

   protected abstract boolean resourceExists(String resourcePath);

   protected void onIgnoreNonLowercaseNamespace(String namespace) {
      LOGGER.warn("ResourcePack: ignored non-lowercase namespace: {} in {}", namespace, this.file);
   }

   @Nullable
   public <T> T getMetadata(IMetadataSectionSerializer<T> deserializer) throws IOException {
      return getResourceMetadata(deserializer, this.getInputStream("pack.mcmeta"));
   }

   @Nullable
   public static <T> T getResourceMetadata(IMetadataSectionSerializer<T> deserializer, InputStream inputStream) {
      JsonObject jsonobject;
      try (BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
         jsonobject = JsonUtils.func_212743_a(bufferedreader);
      } catch (JsonParseException | IOException ioexception) {
         LOGGER.error("Couldn't load {} metadata", deserializer.getSectionName(), ioexception);
         return (T)null;
      }

      if (!jsonobject.has(deserializer.getSectionName())) {
         return (T)null;
      } else {
         try {
            return deserializer.deserialize(JsonUtils.getJsonObject(jsonobject, deserializer.getSectionName()));
         } catch (JsonParseException jsonparseexception) {
            LOGGER.error("Couldn't load {} metadata", deserializer.getSectionName(), jsonparseexception);
            return (T)null;
         }
      }
   }

   public String getName() {
      return this.file.getName();
   }
}