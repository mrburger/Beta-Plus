package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ShieldRecipes extends IRecipeHidden {
   public ShieldRecipes(ResourceLocation p_i48160_1_) {
      super(p_i48160_1_);
   }

   /**
    * Used to check if a recipe matches current crafting inventory
    */
   public boolean matches(IInventory inv, World worldIn) {
      if (!(inv instanceof InventoryCrafting)) {
         return false;
      } else {
         ItemStack itemstack = ItemStack.EMPTY;
         ItemStack itemstack1 = ItemStack.EMPTY;

         for(int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack2 = inv.getStackInSlot(i);
            if (!itemstack2.isEmpty()) {
               if (itemstack2.getItem() instanceof ItemBanner) {
                  if (!itemstack1.isEmpty()) {
                     return false;
                  }

                  itemstack1 = itemstack2;
               } else {
                  if (itemstack2.getItem() != Items.SHIELD) {
                     return false;
                  }

                  if (!itemstack.isEmpty()) {
                     return false;
                  }

                  if (itemstack2.getChildTag("BlockEntityTag") != null) {
                     return false;
                  }

                  itemstack = itemstack2;
               }
            }
         }

         if (!itemstack.isEmpty() && !itemstack1.isEmpty()) {
            return true;
         } else {
            return false;
         }
      }
   }

   /**
    * Returns an Item that is the result of this recipe
    */
   public ItemStack getCraftingResult(IInventory inv) {
      ItemStack itemstack = ItemStack.EMPTY;
      ItemStack itemstack1 = ItemStack.EMPTY;

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack itemstack2 = inv.getStackInSlot(i);
         if (!itemstack2.isEmpty()) {
            if (itemstack2.getItem() instanceof ItemBanner) {
               itemstack = itemstack2;
            } else if (itemstack2.getItem() == Items.SHIELD) {
               itemstack1 = itemstack2.copy();
            }
         }
      }

      if (itemstack1.isEmpty()) {
         return itemstack1;
      } else {
         NBTTagCompound nbttagcompound = itemstack.getChildTag("BlockEntityTag");
         NBTTagCompound nbttagcompound1 = nbttagcompound == null ? new NBTTagCompound() : nbttagcompound.copy();
         nbttagcompound1.setInt("Base", ((ItemBanner)itemstack.getItem()).getColor().getId());
         itemstack1.setTagInfo("BlockEntityTag", nbttagcompound1);
         return itemstack1;
      }
   }

   /**
    * Used to determine if this recipe can fit in a grid of the given width/height
    */
   public boolean canFit(int width, int height) {
      return width * height >= 2;
   }

   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CRAFTING_SPECIAL_SHIELDDECORATION;
   }
}