package net.minecraft.item.crafting;

import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ShulkerBoxColoringRecipe extends IRecipeHidden {
   public ShulkerBoxColoringRecipe(ResourceLocation p_i48159_1_) {
      super(p_i48159_1_);
   }

   /**
    * Used to check if a recipe matches current crafting inventory
    */
   public boolean matches(IInventory inv, World worldIn) {
      if (!(inv instanceof InventoryCrafting)) {
         return false;
      } else {
         int i = 0;
         int j = 0;

         for(int k = 0; k < inv.getSizeInventory(); ++k) {
            ItemStack itemstack = inv.getStackInSlot(k);
            if (!itemstack.isEmpty()) {
               if (Block.getBlockFromItem(itemstack.getItem()) instanceof BlockShulkerBox) {
                  ++i;
               } else {
                  if (!itemstack.getItem().isIn(net.minecraftforge.common.Tags.Items.DYES)) {
                     return false;
                  }

                  ++j;
               }

               if (j > 1 || i > 1) {
                  return false;
               }
            }
         }

         return i == 1 && j == 1;
      }
   }

   /**
    * Returns an Item that is the result of this recipe
    */
   public ItemStack getCraftingResult(IInventory inv) {
      ItemStack itemstack = ItemStack.EMPTY;
      net.minecraft.item.EnumDyeColor color = net.minecraft.item.EnumDyeColor.WHITE;

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack itemstack1 = inv.getStackInSlot(i);
         if (!itemstack1.isEmpty()) {
            Item item = itemstack1.getItem();
            if (Block.getBlockFromItem(item) instanceof BlockShulkerBox) {
               itemstack = itemstack1;
            } else {
               net.minecraft.item.EnumDyeColor tmp = net.minecraft.item.EnumDyeColor.getColor(itemstack1);
               if (tmp != null) color = tmp;
            }
         }
      }

      ItemStack itemstack2 = BlockShulkerBox.getColoredItemStack(color);
      if (itemstack.hasTag()) {
         itemstack2.setTag(itemstack.getTag().copy());
      }

      return itemstack2;
   }

   /**
    * Used to determine if this recipe can fit in a grid of the given width/height
    */
   public boolean canFit(int width, int height) {
      return width * height >= 2;
   }

   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CRAFTING_SPECIAL_SHULKERBOXCOLORING;
   }
}