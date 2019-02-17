package net.minecraft.item.crafting;

import javax.annotation.Nullable;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BannerAddPatternRecipe extends IRecipeHidden {
   public BannerAddPatternRecipe(ResourceLocation p_i48172_1_) {
      super(p_i48172_1_);
   }

   /**
    * Used to check if a recipe matches current crafting inventory
    */
   public boolean matches(IInventory inv, World worldIn) {
      if (!(inv instanceof InventoryCrafting)) {
         return false;
      } else {
         boolean flag = false;

         for(int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (itemstack.getItem() instanceof ItemBanner) {
               if (flag) {
                  return false;
               }

               if (TileEntityBanner.getPatterns(itemstack) >= 6) {
                  return false;
               }

               flag = true;
            }
         }

         return flag && this.func_201838_c(inv) != null;
      }
   }

   /**
    * Returns an Item that is the result of this recipe
    */
   public ItemStack getCraftingResult(IInventory inv) {
      ItemStack itemstack = ItemStack.EMPTY;

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack itemstack1 = inv.getStackInSlot(i);
         if (!itemstack1.isEmpty() && itemstack1.getItem() instanceof ItemBanner) {
            itemstack = itemstack1.copy();
            itemstack.setCount(1);
            break;
         }
      }

      BannerPattern bannerpattern = this.func_201838_c(inv);
      if (bannerpattern != null) {
         EnumDyeColor enumdyecolor = EnumDyeColor.WHITE;

         for(int j = 0; j < inv.getSizeInventory(); ++j) {
            Item item = inv.getStackInSlot(j).getItem();
            if (item instanceof ItemDye) {
               enumdyecolor = ((ItemDye)item).getDyeColor();
               break;
            }
         }

         NBTTagCompound nbttagcompound1 = itemstack.getOrCreateChildTag("BlockEntityTag");
         NBTTagList nbttaglist;
         if (nbttagcompound1.contains("Patterns", 9)) {
            nbttaglist = nbttagcompound1.getList("Patterns", 10);
         } else {
            nbttaglist = new NBTTagList();
            nbttagcompound1.setTag("Patterns", nbttaglist);
         }

         NBTTagCompound nbttagcompound = new NBTTagCompound();
         nbttagcompound.setString("Pattern", bannerpattern.getHashname());
         nbttagcompound.setInt("Color", enumdyecolor.getId());
         nbttaglist.add((INBTBase)nbttagcompound);
      }

      return itemstack;
   }

   @Nullable
   private BannerPattern func_201838_c(IInventory p_201838_1_) {
      for(BannerPattern bannerpattern : BannerPattern.values()) {
         if (bannerpattern.hasPattern()) {
            boolean flag = true;
            if (bannerpattern.hasPatternItem()) {
               boolean flag1 = false;
               boolean flag2 = false;

               for(int i = 0; i < p_201838_1_.getSizeInventory() && flag; ++i) {
                  ItemStack itemstack = p_201838_1_.getStackInSlot(i);
                  if (!itemstack.isEmpty() && !(itemstack.getItem() instanceof ItemBanner)) {
                     if (itemstack.getItem() instanceof ItemDye) {
                        if (flag2) {
                           flag = false;
                           break;
                        }

                        flag2 = true;
                     } else {
                        if (flag1 || !itemstack.isItemEqual(bannerpattern.getPatternItem())) {
                           flag = false;
                           break;
                        }

                        flag1 = true;
                     }
                  }
               }

               if (!flag1 || !flag2) {
                  flag = false;
               }
            } else if (p_201838_1_.getSizeInventory() == bannerpattern.getPatterns().length * bannerpattern.getPatterns()[0].length()) {
               EnumDyeColor enumdyecolor1 = null;

               for(int j = 0; j < p_201838_1_.getSizeInventory() && flag; ++j) {
                  int k = j / 3;
                  int l = j % 3;
                  ItemStack itemstack1 = p_201838_1_.getStackInSlot(j);
                  Item item = itemstack1.getItem();
                  if (!itemstack1.isEmpty() && !(item instanceof ItemBanner)) {
                     if (!(item instanceof ItemDye)) {
                        flag = false;
                        break;
                     }

                     EnumDyeColor enumdyecolor = ((ItemDye)item).getDyeColor();
                     if (enumdyecolor1 != null && enumdyecolor1 != enumdyecolor) {
                        flag = false;
                        break;
                     }

                     if (bannerpattern.getPatterns()[k].charAt(l) == ' ') {
                        flag = false;
                        break;
                     }

                     enumdyecolor1 = enumdyecolor;
                  } else if (bannerpattern.getPatterns()[k].charAt(l) != ' ') {
                     flag = false;
                     break;
                  }
               }
            } else {
               flag = false;
            }

            if (flag) {
               return bannerpattern;
            }
         }
      }

      return null;
   }

   /**
    * Used to determine if this recipe can fit in a grid of the given width/height
    */
   public boolean canFit(int width, int height) {
      return width >= 3 && height >= 3;
   }

   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CRAFTING_SPECIAL_BANNERADDPATTERN;
   }
}