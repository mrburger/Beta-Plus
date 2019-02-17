package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class ItemPredicate {
   private static final Map<ResourceLocation, java.util.function.Function<JsonObject, ItemPredicate>> custom_predicates = new java.util.HashMap<>();
   private static final Map<ResourceLocation, java.util.function.Function<JsonObject, ItemPredicate>> unmod_predicates = java.util.Collections.unmodifiableMap(custom_predicates);
   public static final ItemPredicate ANY = new ItemPredicate();
   @Nullable
   private final Tag<Item> tag;
   @Nullable
   private final Item item;
   private final MinMaxBounds.IntBound count;
   private final MinMaxBounds.IntBound durability;
   private final EnchantmentPredicate[] enchantments;
   @Nullable
   private final PotionType potion;
   private final NBTPredicate nbt;

   public ItemPredicate() {
      this.tag = null;
      this.item = null;
      this.potion = null;
      this.count = MinMaxBounds.IntBound.UNBOUNDED;
      this.durability = MinMaxBounds.IntBound.UNBOUNDED;
      this.enchantments = new EnchantmentPredicate[0];
      this.nbt = NBTPredicate.ANY;
   }

   public ItemPredicate(@Nullable Tag<Item> p_i49722_1_, @Nullable Item p_i49722_2_, MinMaxBounds.IntBound p_i49722_3_, MinMaxBounds.IntBound p_i49722_4_, EnchantmentPredicate[] p_i49722_5_, @Nullable PotionType p_i49722_6_, NBTPredicate p_i49722_7_) {
      this.tag = p_i49722_1_;
      this.item = p_i49722_2_;
      this.count = p_i49722_3_;
      this.durability = p_i49722_4_;
      this.enchantments = p_i49722_5_;
      this.potion = p_i49722_6_;
      this.nbt = p_i49722_7_;
   }

   public boolean test(ItemStack item) {
      if (this.tag != null && !this.tag.contains(item.getItem())) {
         return false;
      } else if (this.item != null && item.getItem() != this.item) {
         return false;
      } else if (!this.count.test(item.getCount())) {
         return false;
      } else if (!this.durability.isUnbounded() && !item.isDamageable()) {
         return false;
      } else if (!this.durability.test(item.getMaxDamage() - item.getDamage())) {
         return false;
      } else if (!this.nbt.test(item)) {
         return false;
      } else {
         Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(item);

         for(int i = 0; i < this.enchantments.length; ++i) {
            if (!this.enchantments[i].test(map)) {
               return false;
            }
         }

         PotionType potiontype = PotionUtils.getPotionFromItem(item);
         if (this.potion != null && this.potion != potiontype) {
            return false;
         } else {
            return true;
         }
      }
   }

   public static ItemPredicate deserialize(@Nullable JsonElement element) {
      if (element != null && !element.isJsonNull()) {
         JsonObject jsonobject = JsonUtils.getJsonObject(element, "item");
         if (jsonobject.has("type")) {
              final ResourceLocation rl = new ResourceLocation(JsonUtils.getString(jsonobject, "type"));
              if (custom_predicates.containsKey(rl)) return custom_predicates.get(rl).apply(jsonobject);
              else throw new JsonSyntaxException("There is no ItemPredicate of type "+rl);
         }
         MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(jsonobject.get("count"));
         MinMaxBounds.IntBound minmaxbounds$intbound1 = MinMaxBounds.IntBound.fromJson(jsonobject.get("durability"));
         if (jsonobject.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
         } else {
            NBTPredicate nbtpredicate = NBTPredicate.deserialize(jsonobject.get("nbt"));
            Item item = null;
            if (jsonobject.has("item")) {
               ResourceLocation resourcelocation = new ResourceLocation(JsonUtils.getString(jsonobject, "item"));
               item = IRegistry.field_212630_s.func_212608_b(resourcelocation);
               if (item == null) {
                  throw new JsonSyntaxException("Unknown item id '" + resourcelocation + "'");
               }
            }

            Tag<Item> tag = null;
            if (jsonobject.has("tag")) {
               ResourceLocation resourcelocation1 = new ResourceLocation(JsonUtils.getString(jsonobject, "tag"));
               tag = ItemTags.getCollection().get(resourcelocation1);
               if (tag == null) {
                  throw new JsonSyntaxException("Unknown item tag '" + resourcelocation1 + "'");
               }
            }

            EnchantmentPredicate[] aenchantmentpredicate = EnchantmentPredicate.deserializeArray(jsonobject.get("enchantments"));
            PotionType potiontype = null;
            if (jsonobject.has("potion")) {
               ResourceLocation resourcelocation2 = new ResourceLocation(JsonUtils.getString(jsonobject, "potion"));
               if (!IRegistry.field_212621_j.func_212607_c(resourcelocation2)) {
                  throw new JsonSyntaxException("Unknown potion '" + resourcelocation2 + "'");
               }

               potiontype = IRegistry.field_212621_j.get(resourcelocation2);
            }

            return new ItemPredicate(tag, item, minmaxbounds$intbound, minmaxbounds$intbound1, aenchantmentpredicate, potiontype, nbtpredicate);
         }
      } else {
         return ANY;
      }
   }

   public JsonElement serialize() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (this.item != null) {
            jsonobject.addProperty("item", IRegistry.field_212630_s.getKey(this.item).toString());
         }

         if (this.tag != null) {
            jsonobject.addProperty("tag", this.tag.getId().toString());
         }

         jsonobject.add("count", this.count.serialize());
         jsonobject.add("durability", this.durability.serialize());
         jsonobject.add("nbt", this.nbt.serialize());
         if (this.enchantments.length > 0) {
            JsonArray jsonarray = new JsonArray();

            for(EnchantmentPredicate enchantmentpredicate : this.enchantments) {
               jsonarray.add(enchantmentpredicate.serialize());
            }

            jsonobject.add("enchantments", jsonarray);
         }

         if (this.potion != null) {
            jsonobject.addProperty("potion", IRegistry.field_212621_j.getKey(this.potion).toString());
         }

         return jsonobject;
      }
   }

   public static ItemPredicate[] deserializeArray(@Nullable JsonElement element) {
      if (element != null && !element.isJsonNull()) {
         JsonArray jsonarray = JsonUtils.getJsonArray(element, "items");
         ItemPredicate[] aitempredicate = new ItemPredicate[jsonarray.size()];

         for(int i = 0; i < aitempredicate.length; ++i) {
            aitempredicate[i] = deserialize(jsonarray.get(i));
         }

         return aitempredicate;
      } else {
         return new ItemPredicate[0];
      }
   }

   public static void register(ResourceLocation name, java.util.function.Function<JsonObject, ItemPredicate> deserializer) {
      custom_predicates.put(name, deserializer);
   }

   public static Map<ResourceLocation, java.util.function.Function<JsonObject, ItemPredicate>> getPredicates() {
       return unmod_predicates;
   }

   public static class Builder {
      private final List<EnchantmentPredicate> enchantments = Lists.newArrayList();
      @Nullable
      private Item item;
      @Nullable
      private Tag<Item> tag;
      private MinMaxBounds.IntBound count = MinMaxBounds.IntBound.UNBOUNDED;
      private MinMaxBounds.IntBound durability = MinMaxBounds.IntBound.UNBOUNDED;
      @Nullable
      private PotionType potion;
      private NBTPredicate nbt = NBTPredicate.ANY;

      public static ItemPredicate.Builder create() {
         return new ItemPredicate.Builder();
      }

      public ItemPredicate.Builder func_200308_a(IItemProvider provider) {
         this.item = provider.asItem();
         return this;
      }

      public ItemPredicate.Builder tag(Tag<Item> tag) {
         this.tag = tag;
         return this;
      }

      public ItemPredicate.Builder count(MinMaxBounds.IntBound bound) {
         this.count = bound;
         return this;
      }

      public ItemPredicate build() {
         return new ItemPredicate(this.tag, this.item, this.count, this.durability, this.enchantments.toArray(new EnchantmentPredicate[0]), this.potion, this.nbt);
      }
   }
}