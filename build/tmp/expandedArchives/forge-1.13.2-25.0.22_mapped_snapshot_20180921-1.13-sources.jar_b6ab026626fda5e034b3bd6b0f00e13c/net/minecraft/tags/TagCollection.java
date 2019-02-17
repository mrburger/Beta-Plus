package net.minecraft.tags;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagCollection<T> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = new Gson();
   private static final int JSON_EXTENSION_LENGTH = ".json".length();
   private final Map<ResourceLocation, Tag<T>> tagMap = Maps.newHashMap();
   private final Function<ResourceLocation, T> resourceLocationToItem;
   private final Predicate<ResourceLocation> isValueKnownPredicate;
   private final String resourceLocationPrefix;
   private final boolean preserveOrder;
   private final String itemTypeName;

   public TagCollection(Predicate<ResourceLocation> isValueKnownPredicateIn, Function<ResourceLocation, T> resourceLocationToItemIn, String resourceLocationPrefixIn, boolean preserveOrderIn, String itemTypeNameIn) {
      this.isValueKnownPredicate = isValueKnownPredicateIn;
      this.resourceLocationToItem = resourceLocationToItemIn;
      this.resourceLocationPrefix = resourceLocationPrefixIn;
      this.preserveOrder = preserveOrderIn;
      this.itemTypeName = itemTypeNameIn;
   }

   public void register(Tag<T> tagIn) {
      if (this.tagMap.containsKey(tagIn.getId())) {
         throw new IllegalArgumentException("Duplicate " + this.itemTypeName + " tag '" + tagIn.getId() + "'");
      } else {
         this.tagMap.put(tagIn.getId(), tagIn);
      }
   }

   @Nullable
   public Tag<T> get(ResourceLocation resourceLocationIn) {
      return this.tagMap.get(resourceLocationIn);
   }

   public Tag<T> getOrCreate(ResourceLocation resourceLocationIn) {
      Tag<T> tag = this.tagMap.get(resourceLocationIn);
      return tag == null ? new Tag<>(resourceLocationIn) : tag;
   }

   public Collection<ResourceLocation> getRegisteredTags() {
      return this.tagMap.keySet();
   }

   @OnlyIn(Dist.CLIENT)
   public Collection<ResourceLocation> getOwningTags(T itemIn) {
      List<ResourceLocation> list = Lists.newArrayList();

      for(Entry<ResourceLocation, Tag<T>> entry : this.tagMap.entrySet()) {
         if (entry.getValue().contains(itemIn)) {
            list.add(entry.getKey());
         }
      }

      return list;
   }

   public void clear() {
      this.tagMap.clear();
   }

   public void reload(IResourceManager resourceManager) {
      Map<ResourceLocation, Tag.Builder<T>> map = Maps.newHashMap();

      for(ResourceLocation resourcelocation : resourceManager.getAllResourceLocations(this.resourceLocationPrefix, (p_199916_0_) -> {
         return p_199916_0_.endsWith(".json");
      })) {
         String s = resourcelocation.getPath();
         ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(this.resourceLocationPrefix.length() + 1, s.length() - JSON_EXTENSION_LENGTH));

         try {
            for(IResource iresource : resourceManager.getAllResources(resourcelocation)) {
               try {
                  JsonObject jsonobject = JsonUtils.fromJson(GSON, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                  if (jsonobject == null) {
                     LOGGER.error("Couldn't load {} tag list {} from {} in data pack {} as it's empty or null", this.itemTypeName, resourcelocation1, resourcelocation, iresource.getPackName());
                  } else {
                     Tag.Builder<T> builder = map.getOrDefault(resourcelocation1, Tag.Builder.create());
                     builder.deserialize(this.isValueKnownPredicate, this.resourceLocationToItem, jsonobject);
                     map.put(resourcelocation1, builder);
                  }
               } catch (RuntimeException | IOException ioexception) {
                  LOGGER.error("Couldn't read {} tag list {} from {} in data pack {}", this.itemTypeName, resourcelocation1, resourcelocation, iresource.getPackName(), ioexception);
               } finally {
                  IOUtils.closeQuietly((Closeable)iresource);
               }
            }
         } catch (IOException ioexception1) {
            LOGGER.error("Couldn't read {} tag list {} from {}", this.itemTypeName, resourcelocation1, resourcelocation, ioexception1);
         }
      }

      while(!map.isEmpty()) {
         boolean flag = false;
         Iterator<Entry<ResourceLocation, Tag.Builder<T>>> iterator = map.entrySet().iterator();

         while(iterator.hasNext()) {
            Entry<ResourceLocation, Tag.Builder<T>> entry1 = iterator.next();
            if (entry1.getValue().resolve(this::get)) {
               flag = true;
               this.register(entry1.getValue().build(entry1.getKey()));
               iterator.remove();
            }
         }

         if (!flag) {
            for(Entry<ResourceLocation, Tag.Builder<T>> entry2 : map.entrySet()) {
               LOGGER.error("Couldn't load {} tag {} as it either references another tag that doesn't exist, or ultimately references itself", this.itemTypeName, entry2.getKey());
            }
            break;
         }
      }

      for(Entry<ResourceLocation, Tag.Builder<T>> entry : map.entrySet()) {
         this.register(entry.getValue().ordered(this.preserveOrder).build(entry.getKey()));
      }

   }

   public Map<ResourceLocation, Tag<T>> getTagMap() {
      return this.tagMap;
   }
}