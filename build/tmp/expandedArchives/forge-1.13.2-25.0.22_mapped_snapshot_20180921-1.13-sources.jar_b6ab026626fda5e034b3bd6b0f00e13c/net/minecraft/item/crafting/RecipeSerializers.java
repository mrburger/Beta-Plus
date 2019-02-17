package net.minecraft.item.crafting;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class RecipeSerializers {
   private static final Map<ResourceLocation, IRecipeSerializer<?>> REGISTRY = Maps.newHashMap();
   public static final IRecipeSerializer<ShapedRecipe> CRAFTING_SHAPED = register(new ShapedRecipe.Serializer());
   public static final IRecipeSerializer<ShapelessRecipe> CRAFTING_SHAPELESS = register(new ShapelessRecipe.Serializer());
   public static final RecipeSerializers.SimpleSerializer<RecipesArmorDyes> CRAFTING_SPECIAL_ARMORDYE = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_armordye", RecipesArmorDyes::new));
   public static final RecipeSerializers.SimpleSerializer<RecipeBookCloning> CRAFTING_SPECIAL_BOOKCLONING = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_bookcloning", RecipeBookCloning::new));
   public static final RecipeSerializers.SimpleSerializer<RecipesMapCloning> CRAFTING_SPECIAL_MAPCLONING = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_mapcloning", RecipesMapCloning::new));
   public static final RecipeSerializers.SimpleSerializer<RecipesMapExtending> CRAFTING_SPECIAL_MAPEXTENDING = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_mapextending", RecipesMapExtending::new));
   public static final RecipeSerializers.SimpleSerializer<FireworkRocketRecipe> CRAFTING_SPECIAL_FIREWORK_ROCKET = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_firework_rocket", FireworkRocketRecipe::new));
   public static final RecipeSerializers.SimpleSerializer<FireworkStarRecipe> CRAFTING_SPECIAL_FIREWORK_STAR = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_firework_star", FireworkStarRecipe::new));
   public static final RecipeSerializers.SimpleSerializer<FireworkStarFadeRecipe> CRAFTING_SPECIAL_FIREWORK_STAR_FADE = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_firework_star_fade", FireworkStarFadeRecipe::new));
   public static final RecipeSerializers.SimpleSerializer<RecipeRepairItem> CRAFTING_SPECIAL_REPAIRITEM = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_repairitem", RecipeRepairItem::new));
   public static final RecipeSerializers.SimpleSerializer<RecipeTippedArrow> CRAFTING_SPECIAL_TIPPEDARROW = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_tippedarrow", RecipeTippedArrow::new));
   public static final RecipeSerializers.SimpleSerializer<BannerDuplicateRecipe> CRAFTING_SPECIAL_BANNERDUPLICATE = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_bannerduplicate", BannerDuplicateRecipe::new));
   public static final RecipeSerializers.SimpleSerializer<BannerAddPatternRecipe> CRAFTING_SPECIAL_BANNERADDPATTERN = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_banneraddpattern", BannerAddPatternRecipe::new));
   public static final RecipeSerializers.SimpleSerializer<ShieldRecipes> CRAFTING_SPECIAL_SHIELDDECORATION = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_shielddecoration", ShieldRecipes::new));
   public static final RecipeSerializers.SimpleSerializer<ShulkerBoxColoringRecipe> CRAFTING_SPECIAL_SHULKERBOXCOLORING = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_shulkerboxcoloring", ShulkerBoxColoringRecipe::new));
   public static final IRecipeSerializer<FurnaceRecipe> SMELTING = register(new FurnaceRecipe.Serializer());
   private static final java.util.Set<ResourceLocation> VANILLA_TYPES = new java.util.HashSet<>(REGISTRY.keySet()); //Copy of vanilla so we can keep track for compatibility.

   public static <S extends IRecipeSerializer<T>, T extends IRecipe> S register(S recipeSerializer) {
      if (REGISTRY.containsKey(recipeSerializer.getName())) {
         throw new IllegalArgumentException("Duplicate recipe serializer " + recipeSerializer.getName());
      } else {
         REGISTRY.put(recipeSerializer.getName(), recipeSerializer);
         return recipeSerializer;
      }
   }

   public static IRecipe deserialize(ResourceLocation p_199572_0_, JsonObject p_199572_1_) {
      ResourceLocation s = new ResourceLocation(JsonUtils.getString(p_199572_1_, "type"));
      IRecipeSerializer<?> irecipeserializer = REGISTRY.get(s);
      if (irecipeserializer == null) {
         throw new JsonSyntaxException("Invalid or unsupported recipe type '" + s + "'");
      } else {
         return irecipeserializer.read(p_199572_0_, p_199572_1_);
      }
   }

   public static IRecipe read(PacketBuffer p_199571_0_) {
      ResourceLocation resourcelocation = p_199571_0_.readResourceLocation();
      ResourceLocation s = new ResourceLocation(p_199571_0_.readString(32767));
      IRecipeSerializer<?> irecipeserializer = REGISTRY.get(s);
      if (irecipeserializer == null) {
         throw new IllegalArgumentException("Unknown recipe serializer " + s);
      } else {
         return irecipeserializer.read(resourcelocation, p_199571_0_);
      }
   }

   public static <T extends IRecipe> void write(T p_199574_0_, PacketBuffer p_199574_1_) {
      p_199574_1_.writeResourceLocation(p_199574_0_.getId());
      p_199574_1_.writeString(p_199574_0_.getSerializer().getId());
      IRecipeSerializer<T> irecipeserializer = (IRecipeSerializer<T>)p_199574_0_.getSerializer();
      irecipeserializer.write(p_199574_1_, p_199574_0_);
   }

   public static final class SimpleSerializer<T extends IRecipe> implements IRecipeSerializer<T> {
      private final String id;
      private final Function<ResourceLocation, T> function;
      private final ResourceLocation name;

      public SimpleSerializer(String idIn, Function<ResourceLocation, T> functionIn) {
         this.id = idIn;
         this.function = functionIn;
         this.name = new ResourceLocation(id);
      }

      public T read(ResourceLocation recipeId, JsonObject json) {
         return (T)(this.function.apply(recipeId));
      }

      public T read(ResourceLocation recipeId, PacketBuffer buffer) {
         return (T)(this.function.apply(recipeId));
      }

      public void write(PacketBuffer buffer, T recipe) {
      }

      @Override
      public ResourceLocation getName() {
         return this.name;
      }
   }
}