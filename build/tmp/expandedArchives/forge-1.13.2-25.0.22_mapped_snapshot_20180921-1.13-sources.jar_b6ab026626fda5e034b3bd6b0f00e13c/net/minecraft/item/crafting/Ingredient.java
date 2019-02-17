package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class Ingredient implements Predicate<ItemStack> {
   //Because Mojang caches things... we need to invalidate them.. so... here we go..
   private static final java.util.Set<Ingredient> INSTANCES = java.util.Collections.newSetFromMap(new java.util.WeakHashMap<Ingredient, Boolean>());
   public static void invalidateAll() {
      INSTANCES.stream().filter(e -> e != null).forEach(i -> i.invalidate());
   }

   private static final Predicate<? super Ingredient.IItemList> IS_EMPTY = (p_209361_0_) -> {
      return !p_209361_0_.getStacks().stream().allMatch(ItemStack::isEmpty);
   };
   public static final Ingredient EMPTY = new Ingredient(Stream.empty());
   private final Ingredient.IItemList[] acceptedItems;
   private ItemStack[] matchingStacks;
   private IntList matchingStacksPacked;
   private final boolean isSimple;

   protected Ingredient(Stream<? extends Ingredient.IItemList> itemLists) {
      this.acceptedItems = itemLists.filter(IS_EMPTY).toArray((p_209360_0_) -> {
         return new Ingredient.IItemList[p_209360_0_];
      });
      this.isSimple = !Arrays.stream(acceptedItems).anyMatch(list -> list.getStacks().stream().anyMatch(stack -> stack.getItem().isDamageable()));
      Ingredient.INSTANCES.add(this);
   }

   public ItemStack[] getMatchingStacks() {
      this.determineMatchingStacks();
      return this.matchingStacks;
   }

   private void determineMatchingStacks() {
      if (this.matchingStacks == null) {
         this.matchingStacks = Arrays.stream(this.acceptedItems).flatMap((p_209359_0_) -> {
            return p_209359_0_.getStacks().stream();
         }).distinct().toArray((p_209358_0_) -> {
            return new ItemStack[p_209358_0_];
         });
      }

   }

   public boolean test(@Nullable ItemStack p_test_1_) {
      if (p_test_1_ == null) {
         return false;
      } else if (this.acceptedItems.length == 0) {
         return p_test_1_.isEmpty();
      } else {
         this.determineMatchingStacks();

         for(ItemStack itemstack : this.matchingStacks) {
            if (itemstack.getItem() == p_test_1_.getItem()) {
               return true;
            }
         }

         return false;
      }
   }

   public IntList getValidItemStacksPacked() {
      if (this.matchingStacksPacked == null) {
         this.determineMatchingStacks();
         this.matchingStacksPacked = new IntArrayList(this.matchingStacks.length);

         for(ItemStack itemstack : this.matchingStacks) {
            this.matchingStacksPacked.add(RecipeItemHelper.pack(itemstack));
         }

         this.matchingStacksPacked.sort(IntComparators.NATURAL_COMPARATOR);
      }

      return this.matchingStacksPacked;
   }

   public final void writeToBuffer(PacketBuffer buffer) {
      this.determineMatchingStacks();
      if (!this.isVanilla()) {
         net.minecraftforge.common.crafting.CraftingHelper.write(buffer, this);
         return;
      }
      buffer.writeVarInt(this.matchingStacks.length);

      for(int i = 0; i < this.matchingStacks.length; ++i) {
         buffer.writeItemStack(this.matchingStacks[i]);
      }

   }

   public JsonElement toJson() {
      if (this.acceptedItems.length == 1) {
         return this.acceptedItems[0].toJson();
      } else {
         JsonArray jsonarray = new JsonArray();

         for(Ingredient.IItemList ingredient$iitemlist : this.acceptedItems) {
            jsonarray.add(ingredient$iitemlist.toJson());
         }

         return jsonarray;
      }
   }

   public boolean hasNoMatchingItems() {
      return this.acceptedItems.length == 0 && (this.matchingStacks == null || this.matchingStacks.length == 0) && (this.matchingStacksPacked == null || this.matchingStacksPacked.isEmpty());
   }

   protected void invalidate() {
      this.matchingStacks = null;
      this.matchingStacksPacked = null;
   }

   public boolean isSimple() {
      return isSimple || this == EMPTY;
   }

   private final boolean isVanilla = this.getClass() == Ingredient.class;
   public final boolean isVanilla() {
       return isVanilla;
   }

   public net.minecraftforge.common.crafting.IIngredientSerializer<? extends Ingredient> getSerializer() {
      if (!isVanilla()) throw new IllegalStateException("Modderrs must implement Ingredient.getSerializer in their custom Ingredients: " + this);
      return net.minecraftforge.common.crafting.CraftingHelper.INGREDIENT_VANILLA;
   }

   public static Ingredient fromItemListStream(Stream<? extends Ingredient.IItemList> stream) {
      Ingredient ingredient = new Ingredient(stream);
      return ingredient.acceptedItems.length == 0 ? EMPTY : ingredient;
   }

   public static Ingredient fromItems(IItemProvider... itemsIn) {
      return fromItemListStream(Arrays.stream(itemsIn).map((p_209353_0_) -> {
         return new Ingredient.SingleItemList(new ItemStack(p_209353_0_));
      }));
   }

   public static Ingredient fromStacks(ItemStack... stacks) {
      return fromItemListStream(Arrays.stream(stacks).map((p_209356_0_) -> {
         return new Ingredient.SingleItemList(p_209356_0_);
      }));
   }

   public static Ingredient fromTag(Tag<Item> tagIn) {
      return fromItemListStream(Stream.of(new Ingredient.TagList(tagIn)));
   }

   public static Ingredient fromBuffer(PacketBuffer buffer) {
      buffer.markReaderIndex();
      int i = buffer.readVarInt();
      if (i == -1) {
         buffer.resetReaderIndex();
         return net.minecraftforge.common.crafting.CraftingHelper.getIngredient(buffer.readResourceLocation(), buffer);
      }
      return fromItemListStream(Stream.generate(() -> {
         return new Ingredient.SingleItemList(buffer.readItemStack());
      }).limit((long)i));
   }

   public static Ingredient fromJson(@Nullable JsonElement json) {
      if (json != null && !json.isJsonNull()) {
         Ingredient ret = net.minecraftforge.common.crafting.CraftingHelper.getIngredient(json);
         if (ret != null) return ret;
         if (json.isJsonObject()) {
            return fromItemListStream(Stream.of(deserializeItemList(json.getAsJsonObject())));
         } else if (json.isJsonArray()) {
            JsonArray jsonarray = json.getAsJsonArray();
            if (jsonarray.size() == 0) {
               throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
            } else {
               return fromItemListStream(StreamSupport.stream(jsonarray.spliterator(), false).map((p_209355_0_) -> {
                  return deserializeItemList(JsonUtils.getJsonObject(p_209355_0_, "item"));
               }));
            }
         } else {
            throw new JsonSyntaxException("Expected item to be object or array of objects");
         }
      } else {
         throw new JsonSyntaxException("Item cannot be null");
      }
   }

   public static Ingredient.IItemList deserializeItemList(JsonObject json) {
      if (json.has("constant")) {
         Ingredient.IItemList ret = net.minecraftforge.common.crafting.CraftingHelper.getConstant(new ResourceLocation(JsonUtils.getString(json, "constant")));
         if (ret == null) throw new JsonSyntaxException("Ingredient referenced invalid constant: " + JsonUtils.getString(json, "constant"));
         return ret;
      }
      if (json.has("item") && json.has("tag")) {
         throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
      } else if (json.has("item")) {
         ResourceLocation resourcelocation1 = new ResourceLocation(JsonUtils.getString(json, "item"));
         Item item = IRegistry.field_212630_s.func_212608_b(resourcelocation1);
         if (item == null) {
            throw new JsonSyntaxException("Unknown item '" + resourcelocation1 + "'");
         } else {
            return new Ingredient.SingleItemList(new ItemStack(item));
         }
      } else if (json.has("tag")) {
         ResourceLocation resourcelocation = new ResourceLocation(JsonUtils.getString(json, "tag"));
         Tag<Item> tag = ItemTags.getCollection().get(resourcelocation);
         if (tag == null) {
            throw new JsonSyntaxException("Unknown item tag '" + resourcelocation + "'");
         } else {
            return new Ingredient.TagList(tag);
         }
      } else {
         throw new JsonParseException("An ingredient entry needs either a tag or an item");
      }
   }

   //Merges several vanilla Ingredients together. As a qwerk of how the json is structured, we can't tell if its a single Ingredient type or multiple so we split per item and remerge here.
   //Only public for internal use, so we can access a private field in here.
   public static Ingredient merge(Collection<Ingredient> parts) {
      return fromItemListStream(parts.stream().flatMap(i -> Arrays.stream(i.acceptedItems)));
   }

   public interface IItemList {
      Collection<ItemStack> getStacks();

      JsonObject toJson();
   }

   public static class SingleItemList implements Ingredient.IItemList {
      private final ItemStack stack;

      public SingleItemList(ItemStack stackIn) {
         this.stack = stackIn;
      }

      public Collection<ItemStack> getStacks() {
         return Collections.singleton(this.stack);
      }

      public JsonObject toJson() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("item", IRegistry.field_212630_s.getKey(this.stack.getItem()).toString());
         return jsonobject;
      }
   }

   public static class TagList implements Ingredient.IItemList {
      private final Tag<Item> tag;

      public TagList(Tag<Item> tagIn) {
         this.tag = tagIn;
      }

      public Collection<ItemStack> getStacks() {
         List<ItemStack> list = Lists.newArrayList();

         for(Item item : this.tag.getAllElements()) {
            list.add(new ItemStack(item));
         }

         return list;
      }

      public JsonObject toJson() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("tag", this.tag.getId().toString());
         return jsonobject;
      }
   }
}