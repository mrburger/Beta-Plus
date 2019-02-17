package net.minecraft.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TagsProvider<T> implements IDataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   protected final DataGenerator generator;
   /** The registry which things of this tag use (e.g. {@link Block#REGISTRY}) */
   protected final IRegistry<T> registry;
   /** A map from each tag to its builder */
   protected final Map<Tag<T>, Tag.Builder<T>> tagToBuilder = Maps.newLinkedHashMap();

   protected TagsProvider(DataGenerator p_i49827_1_, IRegistry<T> p_i49827_2_) {
      this.generator = p_i49827_1_;
      this.registry = p_i49827_2_;
   }

   protected abstract void registerTags();

   /**
    * Performs this provider's action.
    */
   public void act(DirectoryCache cache) throws IOException {
      this.tagToBuilder.clear();
      this.registerTags();
      TagCollection<T> tagcollection = new TagCollection<>((p_200428_0_) -> {
         return false;
      }, (p_200430_0_) -> {
         return (T)null;
      }, "", false, "generated");

      for(Entry<Tag<T>, Tag.Builder<T>> entry : this.tagToBuilder.entrySet()) {
         ResourceLocation resourcelocation = entry.getKey().getId();
         if (!entry.getValue().resolve(tagcollection::get)) {
            throw new UnsupportedOperationException("Unsupported referencing of tags!");
         }

         Tag<T> tag = entry.getValue().build(resourcelocation);
         JsonObject jsonobject = tag.serialize(this.registry::getKey);
         Path path = this.makePath(resourcelocation);
         tagcollection.register(tag);
         this.setCollection(tagcollection);

         try {
            String s = GSON.toJson((JsonElement)jsonobject);
            String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();
            if (!Objects.equals(cache.getPreviousHash(path), s1) || !Files.exists(path)) {
               Files.createDirectories(path.getParent());

               try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
                  bufferedwriter.write(s);
               }
            }

            cache.func_208316_a(path, s1);
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't save tags to {}", path, ioexception);
         }
      }

   }

   protected abstract void setCollection(TagCollection<T> colectionIn);

   /**
    * Resolves a Path for the location to save the given tag.
    *  
    * @param id ID of the tag
    */
   protected abstract Path makePath(ResourceLocation id);

   /**
    * Creates (or finds) the builder for the given tag
    */
   protected Tag.Builder<T> getBuilder(Tag<T> tagIn) {
      return this.tagToBuilder.computeIfAbsent(tagIn, (p_200427_0_) -> {
         return Tag.Builder.create();
      });
   }
}