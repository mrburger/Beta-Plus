package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RecipesMapCloning extends IRecipeHidden {
   public RecipesMapCloning(ResourceLocation p_i48165_1_) {
      super(p_i48165_1_);
   }

   /**
    * Used to check if a recipe matches current crafting inventory
    */
   public boolean matches(IInventory inv, World worldIn) {
      if (!(inv instanceof InventoryCrafting)) {
         return false;
      } else {
         int i = 0;
         ItemStack itemstack = ItemStack.EMPTY;

         for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack1 = inv.getStackInSlot(j);
            if (!itemstack1.isEmpty()) {
               if (itemstack1.getItem() == Items.FILLED_MAP) {
                  if (!itemstack.isEmpty()) {
                     return false;
                  }

                  itemstack = itemstack1;
               } else {
                  if (itemstack1.getItem() != Items.MAP) {
                     return false;
                  }

                  ++i;
               }
            }
         }

         return !itemstack.isEmpty() && i > 0;
      }
   }

   /**
    * Returns an Item that is the result of this recipe
    */
   public ItemStack getCraftingResult(IInventory inv) {
      int i = 0;
      ItemStack itemstack = ItemStack.EMPTY;

      for(int j = 0; j < inv.getSizeInventory(); ++j) {
         ItemStack itemstack1 = inv.getStackInSlot(j);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.getItem() == Items.FILLED_MAP) {
               if (!itemstack.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               itemstack = itemstack1;
            } else {
               if (itemstack1.getItem() != Items.MAP) {
                  return ItemStack.EMPTY;
               }

               ++i;
            }
         }
      }

      if (!itemstack.isEmpty() && i >= 1) {
         ItemStack itemstack2 = itemstack.copy();
         itemstack2.setCount(i + 1);
         return itemstack2;
      } else {
         return ItemStack.EMPTY;
      }
   }

   /**
    * Used to determine if this recipe can fit in a grid of the given width/height
    */
   public boolean canFit(int width, int height) {
      return width >= 3 && height >= 3;
   }

   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CRAFTING_SPECIAL_MAPCLONING;
   }
}