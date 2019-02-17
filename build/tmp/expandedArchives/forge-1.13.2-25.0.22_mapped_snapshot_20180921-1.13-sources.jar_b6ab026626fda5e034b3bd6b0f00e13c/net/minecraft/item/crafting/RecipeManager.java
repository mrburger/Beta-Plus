package net.minecraft.item.crafting;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeManager extends net.minecraftforge.common.extensions.ForgeRecipeManager {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final int PATH_PREFIX_LENGTH = "recipes/".length();
   public static final int PATH_SUFFIX_LENGTH = ".json".length();
   private final Map<ResourceLocation, IRecipe> recipes = Maps.newHashMap();
   private boolean someRecipesErrored;

   public void onResourceManagerReload(IResourceManager resourceManager) {
      Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
      this.someRecipesErrored = false;
      this.recipes.clear();
      super.onResourceManagerReload(resourceManager);

      for(ResourceLocation resourcelocation : resourceManager.getAllResourceLocations("recipes", (p_199516_0_) -> {
         return p_199516_0_.endsWith(".json") && !p_199516_0_.startsWith("_"); //Forge filter anything beginning with "_" as it's used for metadata.
      })) {
         String s = resourcelocation.getPath();
         ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(PATH_PREFIX_LENGTH, s.length() - PATH_SUFFIX_LENGTH));

         try (IResource iresource = resourceManager.getResource(resourcelocation)) {
            JsonObject jsonobject = JsonUtils.fromJson(gson, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
            if (jsonobject == null) {
               LOGGER.error("Couldn't load recipe {} as it's null or empty", (Object)resourcelocation1);
            } else if (jsonobject.has("conditions") && !net.minecraftforge.common.crafting.CraftingHelper.processConditions(JsonUtils.getJsonArray(jsonobject, "conditions"))) {
               LOGGER.info("Skipping loading recipe {} as it's conditions were not met", resourcelocation1);
            } else {
               this.addRecipe(RecipeSerializers.deserialize(resourcelocation1, jsonobject));
            }
         } catch (IllegalArgumentException | JsonParseException jsonparseexception) {
            LOGGER.error("Parsing error loading recipe {}", resourcelocation1, jsonparseexception);
            this.someRecipesErrored = true;
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't read custom advancement {} from {}", resourcelocation1, resourcelocation, ioexception);
            this.someRecipesErrored = true;
         }
      }

      LOGGER.info("Loaded {} recipes", (int)this.recipes.size());
   }

   public void addRecipe(IRecipe recipe) {
      if (this.recipes.containsKey(recipe.getId())) {
         throw new IllegalStateException("Duplicate recipe ignored with ID " + recipe.getId());
      } else {
         this.recipes.put(recipe.getId(), recipe);
         if(!recipe.getType().getBaseClass().isAssignableFrom(recipe.getClass())) throw new IllegalStateException(String.format("Recipe type %s is not valid for class %s", recipe.getType().getBaseClass().getName(), recipe.getClass().getName()));
         ((java.util.List) getRecipes(recipe.getType())).add(recipe);
      }
   }

   @Deprecated //Forge Use getResult(IInventory, World, RecipeType)
   public ItemStack getResult(IInventory input, World worldIn) {
      for(IRecipe irecipe : this.recipes.values()) {
         if (irecipe.matches(input, worldIn)) {
            return irecipe.getCraftingResult(input);
         }
      }

      return ItemStack.EMPTY;
   }

   @Nullable
   @Deprecated //Forge: Use getRecipe(IInventory, World, RecipeType)
   public IRecipe getRecipe(IInventory input, World worldIn) {
      for(IRecipe irecipe : this.recipes.values()) {
         if (irecipe.matches(input, worldIn)) {
            return irecipe;
         }
      }

      return null;
   }

   @Deprecated //Forge: Use getRemainingItems(IInventory, World, RecipeType)
   public NonNullList<ItemStack> getRemainingItems(IInventory input, World worldIn) {
      for(IRecipe irecipe : this.recipes.values()) {
         if (irecipe.matches(input, worldIn)) {
            return irecipe.getRemainingItems(input);
         }
      }

      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(input.getSizeInventory(), ItemStack.EMPTY);

      for(int i = 0; i < nonnulllist.size(); ++i) {
         nonnulllist.set(i, input.getStackInSlot(i));
      }

      return nonnulllist;
   }

   @Nullable
   public IRecipe getRecipe(ResourceLocation id) {
      return this.recipes.get(id);
   }

   public Collection<IRecipe> getRecipes() {
      return this.recipes.values();
   }

   public Collection<ResourceLocation> getIds() {
      return this.recipes.keySet();
   }

   @OnlyIn(Dist.CLIENT)
   public void clear() {
      this.recipes.clear();
      this.sortedRecipes.clear();
   }

}