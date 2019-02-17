package net.minecraft.client.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeBookClient extends RecipeBook {
   private final RecipeManager recipeManager;
   private final Map<RecipeBookCategories, List<RecipeList>> recipesByCategory = Maps.newHashMap();
   private final List<RecipeList> allRecipes = Lists.newArrayList();

   public RecipeBookClient(RecipeManager p_i48186_1_) {
      this.recipeManager = p_i48186_1_;
   }

   public void rebuildTable() {
      this.allRecipes.clear();
      this.recipesByCategory.clear();
      Table<RecipeBookCategories, String, RecipeList> table = HashBasedTable.create();

      for(IRecipe irecipe : this.recipeManager.getRecipes()) {
         if (!irecipe.isDynamic()) {
            RecipeBookCategories recipebookcategories = getCategory(irecipe);
            String s = irecipe.getGroup();
            RecipeList recipelist;
            if (s.isEmpty()) {
               recipelist = this.newRecipeList(recipebookcategories);
            } else {
               recipelist = table.get(recipebookcategories, s);
               if (recipelist == null) {
                  recipelist = this.newRecipeList(recipebookcategories);
                  table.put(recipebookcategories, s, recipelist);
               }
            }

            recipelist.add(irecipe);
         }
      }

   }

   private RecipeList newRecipeList(RecipeBookCategories p_202889_1_) {
      RecipeList recipelist = new RecipeList();
      this.allRecipes.add(recipelist);
      this.recipesByCategory.computeIfAbsent(p_202889_1_, (p_202890_0_) -> {
         return Lists.newArrayList();
      }).add(recipelist);
      if (p_202889_1_ != RecipeBookCategories.FURNACE_BLOCKS && p_202889_1_ != RecipeBookCategories.FURNACE_FOOD && p_202889_1_ != RecipeBookCategories.FURNACE_MISC) {
         this.recipesByCategory.computeIfAbsent(RecipeBookCategories.SEARCH, (p_202893_0_) -> {
            return Lists.newArrayList();
         }).add(recipelist);
      } else {
         this.recipesByCategory.computeIfAbsent(RecipeBookCategories.FURNACE_SEARCH, (p_202892_0_) -> {
            return Lists.newArrayList();
         }).add(recipelist);
      }

      return recipelist;
   }

   private static RecipeBookCategories getCategory(IRecipe recipe) {
      if (recipe instanceof FurnaceRecipe) {
         if (recipe.getRecipeOutput().getItem() instanceof ItemFood) {
            return RecipeBookCategories.FURNACE_FOOD;
         } else {
            return recipe.getRecipeOutput().getItem() instanceof ItemBlock ? RecipeBookCategories.FURNACE_BLOCKS : RecipeBookCategories.FURNACE_MISC;
         }
      } else {
         ItemStack itemstack = recipe.getRecipeOutput();
         ItemGroup itemgroup = itemstack.getItem().getGroup();
         if (itemgroup == ItemGroup.BUILDING_BLOCKS) {
            return RecipeBookCategories.BUILDING_BLOCKS;
         } else if (itemgroup != ItemGroup.TOOLS && itemgroup != ItemGroup.COMBAT) {
            return itemgroup == ItemGroup.REDSTONE ? RecipeBookCategories.REDSTONE : RecipeBookCategories.MISC;
         } else {
            return RecipeBookCategories.EQUIPMENT;
         }
      }
   }

   public static List<RecipeBookCategories> getCategoriesForContainer(Container p_202888_0_) {
      if (!(p_202888_0_ instanceof ContainerWorkbench) && !(p_202888_0_ instanceof ContainerPlayer)) {
         return p_202888_0_ instanceof ContainerFurnace ? Lists.newArrayList(RecipeBookCategories.FURNACE_SEARCH, RecipeBookCategories.FURNACE_FOOD, RecipeBookCategories.FURNACE_BLOCKS, RecipeBookCategories.FURNACE_MISC) : Lists.newArrayList();
      } else {
         return Lists.newArrayList(RecipeBookCategories.SEARCH, RecipeBookCategories.EQUIPMENT, RecipeBookCategories.BUILDING_BLOCKS, RecipeBookCategories.MISC, RecipeBookCategories.REDSTONE);
      }
   }

   public List<RecipeList> getRecipes() {
      return this.allRecipes;
   }

   public List<RecipeList> getRecipes(RecipeBookCategories p_202891_1_) {
      return this.recipesByCategory.getOrDefault(p_202891_1_, Collections.emptyList());
   }
}