package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RecipeTippedArrow extends IRecipeHidden {
   public RecipeTippedArrow(ResourceLocation p_i48184_1_) {
      super(p_i48184_1_);
   }

   /**
    * Used to check if a recipe matches current crafting inventory
    */
   public boolean matches(IInventory inv, World worldIn) {
      if (inv.getWidth() == 3 && inv.getHeight() == 3) {
         for(int i = 0; i < inv.getWidth(); ++i) {
            for(int j = 0; j < inv.getHeight(); ++j) {
               ItemStack itemstack = inv.getStackInSlot(i + j * inv.getWidth());
               if (itemstack.isEmpty()) {
                  return false;
               }

               Item item = itemstack.getItem();
               if (i == 1 && j == 1) {
                  if (item != Items.LINGERING_POTION) {
                     return false;
                  }
               } else if (item != Items.ARROW) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   /**
    * Returns an Item that is the result of this recipe
    */
   public ItemStack getCraftingResult(IInventory inv) {
      ItemStack itemstack = inv.getStackInSlot(1 + inv.getWidth());
      if (itemstack.getItem() != Items.LINGERING_POTION) {
         return ItemStack.EMPTY;
      } else {
         ItemStack itemstack1 = new ItemStack(Items.TIPPED_ARROW, 8);
         PotionUtils.addPotionToItemStack(itemstack1, PotionUtils.getPotionFromItem(itemstack));
         PotionUtils.appendEffects(itemstack1, PotionUtils.getFullEffectsFromItem(itemstack));
         return itemstack1;
      }
   }

   /**
    * Used to determine if this recipe can fit in a grid of the given width/height
    */
   public boolean canFit(int width, int height) {
      return width >= 2 && height >= 2;
   }

   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CRAFTING_SPECIAL_TIPPEDARROW;
   }
}