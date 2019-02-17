package net.minecraft.item.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public interface IRecipe extends net.minecraftforge.common.extensions.IForgeRecipe {
   /**
    * Used to check if a recipe matches current crafting inventory
    */
   boolean matches(IInventory inv, World worldIn);

   /**
    * Returns an Item that is the result of this recipe
    */
   ItemStack getCraftingResult(IInventory inv);

   /**
    * Used to determine if this recipe can fit in a grid of the given width/height
    */
   boolean canFit(int width, int height);

   /**
    * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
    * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
    */
   ItemStack getRecipeOutput();

   default NonNullList<ItemStack> getRemainingItems(IInventory inv) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

      for(int i = 0; i < nonnulllist.size(); ++i) {
         ItemStack item = inv.getStackInSlot(i);
         if (item.hasContainerItem()) {
            nonnulllist.set(i, item.getContainerItem());
         }
      }

      return nonnulllist;
   }

   default NonNullList<Ingredient> getIngredients() {
      return NonNullList.create();
   }

   /**
    * If true, this recipe does not appear in the recipe book and does not respect recipe unlocking (and the
    * doLimitedCrafting gamerule)
    */
   default boolean isDynamic() {
      return false;
   }

   /**
    * Recipes with equal group are combined into one button in the recipe book
    */
   default String getGroup() {
      return "";
   }

   ResourceLocation getId();

   IRecipeSerializer<?> getSerializer();
}