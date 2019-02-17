package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ResourceIndex {
   protected static final Logger LOGGER = LogManager.getLogger();
   private final Map<String, File> resourceMap = Maps.newHashMap();

   protected ResourceIndex() {
   }

   public ResourceIndex(File assetsFolder, String indexName) {
      File file1 = new File(assetsFolder, "objects");
      File file2 = new File(assetsFolder, "indexes/" + indexName + ".json");
      BufferedReader bufferedreader = null;

      try {
         bufferedreader = Files.newReader(file2, StandardCharsets.UTF_8);
         JsonObject jsonobject = JsonUtils.func_212743_a(bufferedreader);
         JsonObject jsonobject1 = JsonUtils.getJsonObject(jsonobject, "objects", (JsonObject)null);
         if (jsonobject1 != null) {
            for(Entry<String, JsonElement> entry : jsonobject1.entrySet()) {
               JsonObject jsonobject2 = (JsonObject)entry.getValue();
               String s = entry.getKey();
               String[] astring = s.split("/", 2);
               String s1 = astring.length == 1 ? astring[0] : astring[0] + ":" + astring[1];
               String s2 = JsonUtils.getString(jsonobject2, "hash");
               File file3 = new File(file1, s2.substring(0, 2) + "/" + s2);
               this.resourceMap.put(s1, file3);
            }
         }
      } catch (JsonParseException var20) {
         LOGGER.error("Unable to parse resource index file: {}", (Object)file2);
      } catch (FileNotFoundException var21) {
         LOGGER.error("Can't find the resource index file: {}", (Object)file2);
      } finally {
         IOUtils.closeQuietly((Reader)bufferedreader);
      }

   }

   @Nullable
   public File getFile(ResourceLocation location) {
      String s = location.toString();
      return this.resourceMap.get(s);
   }

   @Nullable
   public File getFile(String p_200009_1_) {
      return this.resourceMap.get(p_200009_1_);
   }

   public Collection<String> getFiles(String p_211685_1_, int p_211685_2_, Predicate<String> p_211685_3_) {
      return this.resourceMap.keySet().stream().filter((p_211684_0_) -> {
         return !p_211684_0_.endsWith(".mcmeta");
      }).map(ResourceLocation::new).map(ResourceLocation::getPath).filter((p_211683_1_) -> {
         return p_211683_1_.startsWith(p_211685_1_ + "/");
      }).filter(p_211685_3_).collect(Collectors.toList());
   }
}