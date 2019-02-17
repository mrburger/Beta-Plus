package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class FireworkRocketRecipe extends IRecipeHidden {
   private static final Ingredient INGREDIENT_PAPER = Ingredient.fromItems(Items.PAPER);
   private static final Ingredient INGREDIENT_GUNPOWDER = Ingredient.fromItems(Items.GUNPOWDER);
   private static final Ingredient INGREDIENT_FIREWORK_STAR = Ingredient.fromItems(Items.FIREWORK_STAR);

   public FireworkRocketRecipe(ResourceLocation p_i48168_1_) {
      super(p_i48168_1_);
   }

   /**
    * Used to check if a recipe matches current crafting inventory
    */
   public boolean matches(IInventory inv, World worldIn) {
      if (!(inv instanceof InventoryCrafting)) {
         return false;
      } else {
         boolean flag = false;
         int i = 0;

         for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
               if (INGREDIENT_PAPER.test(itemstack)) {
                  if (flag) {
                     return false;
                  }

                  flag = true;
               } else if (INGREDIENT_GUNPOWDER.test(itemstack)) {
                  ++i;
                  if (i > 3) {
                     return false;
                  }
               } else if (!INGREDIENT_FIREWORK_STAR.test(itemstack)) {
                  return false;
               }
            }
         }

         return flag && i >= 1;
      }
   }

   /**
    * Returns an Item that is the result of this recipe
    */
   public ItemStack getCraftingResult(IInventory inv) {
      ItemStack itemstack = new ItemStack(Items.FIREWORK_ROCKET, 3);
      NBTTagCompound nbttagcompound = itemstack.getOrCreateChildTag("Fireworks");
      NBTTagList nbttaglist = new NBTTagList();
      int i = 0;

      for(int j = 0; j < inv.getSizeInventory(); ++j) {
         ItemStack itemstack1 = inv.getStackInSlot(j);
         if (!itemstack1.isEmpty()) {
            if (INGREDIENT_GUNPOWDER.test(itemstack1)) {
               ++i;
            } else if (INGREDIENT_FIREWORK_STAR.test(itemstack1)) {
               NBTTagCompound nbttagcompound1 = itemstack1.getChildTag("Explosion");
               if (nbttagcompound1 != null) {
                  nbttaglist.add((INBTBase)nbttagcompound1);
               }
            }
         }
      }

      nbttagcompound.setByte("Flight", (byte)i);
      if (!nbttaglist.isEmpty()) {
         nbttagcompound.setTag("Explosions", nbttaglist);
      }

      return itemstack;
   }

   /**
    * Used to determine if this recipe can fit in a grid of the given width/height
    */
   public boolean canFit(int width, int height) {
      return width * height >= 2;
   }

   /**
    * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
    * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
    */
   public ItemStack getRecipeOutput() {
      return new ItemStack(Items.FIREWORK_ROCKET);
   }

   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CRAFTING_SPECIAL_FIREWORK_ROCKET;
   }
}