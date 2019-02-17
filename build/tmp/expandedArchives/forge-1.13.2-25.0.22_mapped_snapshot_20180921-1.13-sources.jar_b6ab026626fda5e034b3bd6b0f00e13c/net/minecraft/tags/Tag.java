package net.minecraft.tags;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class Tag<T> {
   private final ResourceLocation resourceLocation;
   private final Set<T> taggedItems;
   private final Collection<Tag.ITagEntry<T>> entries;

   public Tag(ResourceLocation resourceLocationIn) {
      this.resourceLocation = resourceLocationIn;
      this.taggedItems = Collections.emptySet();
      this.entries = Collections.emptyList();
   }

   public Tag(ResourceLocation resourceLocationIn, Collection<Tag.ITagEntry<T>> entriesIn, boolean preserveOrder) {
      this.resourceLocation = resourceLocationIn;
      this.taggedItems = (Set<T>)(preserveOrder ? Sets.newLinkedHashSet() : Sets.newHashSet());
      this.entries = entriesIn;

      for(Tag.ITagEntry<T> itagentry : entriesIn) {
         itagentry.populate(this.taggedItems);
      }

   }

   public JsonObject serialize(Function<T, ResourceLocation> getNameForObject) {
      JsonObject jsonobject = new JsonObject();
      JsonArray jsonarray = new JsonArray();

      for(Tag.ITagEntry<T> itagentry : this.entries) {
         itagentry.serialize(jsonarray, getNameForObject);
      }

      jsonobject.addProperty("replace", false);
      jsonobject.add("values", jsonarray);
      return jsonobject;
   }

   public boolean contains(T itemIn) {
      return this.taggedItems.contains(itemIn);
   }

   public Collection<T> getAllElements() {
      return this.taggedItems;
   }

   public Collection<Tag.ITagEntry<T>> getEntries() {
      return this.entries;
   }

   public T getRandomElement(Random random) {
      List<T> list = Lists.newArrayList(this.getAllElements());
      return list.get(random.nextInt(list.size()));
   }

   public ResourceLocation getId() {
      return this.resourceLocation;
   }

   public static class Builder<T> {
      private final Set<Tag.ITagEntry<T>> entries = Sets.newLinkedHashSet();
      private boolean preserveOrder;

      public static <T> Tag.Builder<T> create() {
         return new Tag.Builder<>();
      }

      public Tag.Builder<T> add(Tag.ITagEntry<T> entry) {
         this.entries.add(entry);
         return this;
      }

      public Tag.Builder<T> add(T itemIn) {
         this.entries.add(new Tag.ListEntry<>(Collections.singleton(itemIn)));
         return this;
      }

      @SafeVarargs
      public final Tag.Builder<T> add(T... itemsIn) {
         this.entries.add(new Tag.ListEntry<>(Lists.newArrayList(itemsIn)));
         return this;
      }

      public Tag.Builder<T> addAll(Collection<T> itemsIn) {
         this.entries.add(new Tag.ListEntry<>(itemsIn));
         return this;
      }

      public Tag.Builder<T> add(ResourceLocation resourceLocationIn) {
         this.entries.add(new Tag.TagEntry<>(resourceLocationIn));
         return this;
      }

      public Tag.Builder<T> add(Tag<T> tagIn) {
         this.entries.add(new Tag.TagEntry<>(tagIn));
         return this;
      }

      public Tag.Builder<T> ordered(boolean preserveOrderIn) {
         this.preserveOrder = preserveOrderIn;
         return this;
      }

      public boolean resolve(Function<ResourceLocation, Tag<T>> resourceLocationToTag) {
         for(Tag.ITagEntry<T> itagentry : this.entries) {
            if (!itagentry.resolve(resourceLocationToTag)) {
               return false;
            }
         }

         return true;
      }

      public Tag<T> build(ResourceLocation resourceLocationIn) {
         return new Tag<>(resourceLocationIn, this.entries, this.preserveOrder);
      }

      public Tag.Builder<T> deserialize(Predicate<ResourceLocation> isValueKnownPredicate, Function<ResourceLocation, T> objectGetter, JsonObject json) {
         JsonArray jsonarray = JsonUtils.getJsonArray(json, "values");
         if (JsonUtils.getBoolean(json, "replace", false)) {
            this.entries.clear();
         }

         for(JsonElement jsonelement : jsonarray) {
            String s = JsonUtils.getString(jsonelement, "value");
            if (!s.startsWith("#")) {
               ResourceLocation resourcelocation = new ResourceLocation(s);
               T t = objectGetter.apply(resourcelocation);
               if (t == null || !isValueKnownPredicate.test(resourcelocation)) {
                  throw new JsonParseException("Unknown value '" + resourcelocation + "'");
               }

               this.add(t);
            } else {
               this.add(new ResourceLocation(s.substring(1)));
            }
         }
         net.minecraftforge.common.ForgeHooks.deserializeTagAdditions(this, isValueKnownPredicate, objectGetter, json);

         return this;
      }
      public Tag.Builder<T> remove(Tag.ITagEntry<T> e) { this.entries.remove(e); return this; }
   }

   public interface ITagEntry<T> {
      default boolean resolve(Function<ResourceLocation, Tag<T>> resourceLocationToTag) {
         return true;
      }

      void populate(Collection<T> itemsIn);

      void serialize(JsonArray array, Function<T, ResourceLocation> getNameForObject);
   }

   public static class ListEntry<T> implements Tag.ITagEntry<T> {
      private final Collection<T> taggedItems;

      public ListEntry(Collection<T> taggedItemsIn) {
         this.taggedItems = taggedItemsIn;
      }

      public void populate(Collection<T> itemsIn) {
         itemsIn.addAll(this.taggedItems);
      }

      public void serialize(JsonArray array, Function<T, ResourceLocation> getNameForObject) {
         for(T t : this.taggedItems) {
            ResourceLocation resourcelocation = getNameForObject.apply(t);
            if (resourcelocation == null) {
               throw new IllegalStateException("Unable to serialize an anonymous value to json!");
            }

            array.add(resourcelocation.toString());
         }

      }

      public Collection<T> getTaggedItems() {
         return this.taggedItems;
      }
      @Override public int hashCode() { return this.taggedItems.hashCode(); }
      @Override public boolean equals(Object o) { return o == this || (o instanceof Tag.ListEntry && this.taggedItems.equals(((Tag.ListEntry) o).taggedItems)); }
   }

   public static class TagEntry<T> implements Tag.ITagEntry<T> {
      @Nullable
      private final ResourceLocation id;
      @Nullable
      private Tag<T> tag;

      public TagEntry(ResourceLocation resourceLocationIn) {
         this.id = resourceLocationIn;
      }

      public TagEntry(Tag<T> tagIn) {
         this.id = tagIn.getId();
         this.tag = tagIn;
      }

      public boolean resolve(Function<ResourceLocation, Tag<T>> resourceLocationToTag) {
         if (this.tag == null) {
            this.tag = resourceLocationToTag.apply(this.id);
         }

         return this.tag != null;
      }

      public void populate(Collection<T> itemsIn) {
         if (this.tag == null) {
            throw new IllegalStateException("Cannot build unresolved tag entry");
         } else {
            itemsIn.addAll(this.tag.getAllElements());
         }
      }

      public ResourceLocation getSerializedId() {
         if (this.tag != null) {
            return this.tag.getId();
         } else if (this.id != null) {
            return this.id;
         } else {
            throw new IllegalStateException("Cannot serialize an anonymous tag to json!");
         }
      }

      public void serialize(JsonArray array, Function<T, ResourceLocation> getNameForObject) {
         array.add("#" + this.getSerializedId());
      }
      @Override public int hashCode() { return java.util.Objects.hashCode(this.id); }
      @Override public boolean equals(Object o) { return o == this || (o instanceof Tag.TagEntry && java.util.Objects.equals(this.id, ((Tag.TagEntry) o).id)); }
   }
}