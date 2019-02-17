package net.minecraft.item.crafting;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerRecipeBook;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RecipeBook {
   protected final Set<ResourceLocation> recipes = Sets.newHashSet();
   /** Recipes the player has not yet seen, so the GUI can play an animation */
   protected final Set<ResourceLocation> newRecipes = Sets.newHashSet();
   protected boolean isGuiOpen;
   protected boolean isFilteringCraftable;
   protected boolean isFurnaceGuiOpen;
   protected boolean isFurnaceFilteringCraftable;

   public void copyFrom(RecipeBook that) {
      this.recipes.clear();
      this.newRecipes.clear();
      this.recipes.addAll(that.recipes);
      this.newRecipes.addAll(that.newRecipes);
   }

   public void unlock(IRecipe recipe) {
      if (!recipe.isDynamic()) {
         this.unlock(recipe.getId());
      }

   }

   protected void unlock(ResourceLocation p_209118_1_) {
      this.recipes.add(p_209118_1_);
   }

   public boolean isUnlocked(@Nullable IRecipe recipe) {
      return recipe == null ? false : this.recipes.contains(recipe.getId());
   }

   @OnlyIn(Dist.CLIENT)
   public void lock(IRecipe recipe) {
      this.lock(recipe.getId());
   }

   protected void lock(ResourceLocation p_209119_1_) {
      this.recipes.remove(p_209119_1_);
      this.newRecipes.remove(p_209119_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isNew(IRecipe recipe) {
      return this.newRecipes.contains(recipe.getId());
   }

   public void markSeen(IRecipe recipe) {
      this.newRecipes.remove(recipe.getId());
   }

   public void markNew(IRecipe recipe) {
      this.markNew(recipe.getId());
   }

   protected void markNew(ResourceLocation p_209120_1_) {
      this.newRecipes.add(p_209120_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isGuiOpen() {
      return this.isGuiOpen;
   }

   public void setGuiOpen(boolean open) {
      this.isGuiOpen = open;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_203432_a(ContainerRecipeBook p_203432_1_) {
      return p_203432_1_ instanceof ContainerFurnace ? this.isFurnaceFilteringCraftable : this.isFilteringCraftable;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isFilteringCraftable() {
      return this.isFilteringCraftable;
   }

   public void setFilteringCraftable(boolean shouldFilter) {
      this.isFilteringCraftable = shouldFilter;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_202883_c() {
      return this.isFurnaceGuiOpen;
   }

   public void setFurnaceGuiOpen(boolean p_202881_1_) {
      this.isFurnaceGuiOpen = p_202881_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_202884_d() {
      return this.isFurnaceFilteringCraftable;
   }

   public void setFurnaceFilteringCraftable(boolean p_202882_1_) {
      this.isFurnaceFilteringCraftable = p_202882_1_;
   }
}