package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerRecipeBook;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketPlaceGhostRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerRecipePlacer implements IRecipePlacer<Integer> {
   protected static final Logger LOGGER = LogManager.getLogger();
   protected final RecipeItemHelper recipeItemHelper = new RecipeItemHelper();
   protected InventoryPlayer playerInventory;
   protected ContainerRecipeBook recipeBookContainer;

   public void place(EntityPlayerMP player, @Nullable IRecipe recipe, boolean placeAll) {
      if (recipe != null && player.getRecipeBook().isUnlocked(recipe)) {
         this.playerInventory = player.inventory;
         this.recipeBookContainer = (ContainerRecipeBook)player.openContainer;
         if (this.func_194328_c() || player.isCreative()) {
            this.recipeItemHelper.clear();
            player.inventory.func_201571_a(this.recipeItemHelper);
            this.recipeBookContainer.func_201771_a(this.recipeItemHelper);
            if (this.recipeItemHelper.canCraft(recipe, (IntList)null)) {
               this.tryPlaceRecipe(recipe, placeAll);
            } else {
               this.clear();
               player.connection.sendPacket(new SPacketPlaceGhostRecipe(player.openContainer.windowId, recipe));
            }

            player.inventory.markDirty();
         }
      }
   }

   protected void clear() {
      for(int i = 0; i < this.recipeBookContainer.getWidth() * this.recipeBookContainer.getHeight() + 1; ++i) {
         if (i != this.recipeBookContainer.getOutputSlot() || !(this.recipeBookContainer instanceof ContainerWorkbench) && !(this.recipeBookContainer instanceof ContainerPlayer)) {
            this.giveToPlayer(i);
         }
      }

      this.recipeBookContainer.clear();
   }

   protected void giveToPlayer(int slotIn) {
      ItemStack itemstack = this.recipeBookContainer.getSlot(slotIn).getStack();
      if (!itemstack.isEmpty()) {
         for(; itemstack.getCount() > 0; this.recipeBookContainer.getSlot(slotIn).decrStackSize(1)) {
            int i = this.playerInventory.storeItemStack(itemstack);
            if (i == -1) {
               i = this.playerInventory.getFirstEmptyStack();
            }

            ItemStack itemstack1 = itemstack.copy();
            itemstack1.setCount(1);
            if (!this.playerInventory.add(i, itemstack1)) {
               LOGGER.error("Can't find any space for item in the inventory");
            }
         }

      }
   }

   protected void tryPlaceRecipe(IRecipe p_201508_1_, boolean placeAll) {
      boolean flag = this.recipeBookContainer.matches(p_201508_1_);
      int i = this.recipeItemHelper.getBiggestCraftableStack(p_201508_1_, (IntList)null);
      if (flag) {
         for(int j = 0; j < this.recipeBookContainer.getHeight() * this.recipeBookContainer.getWidth() + 1; ++j) {
            if (j != this.recipeBookContainer.getOutputSlot()) {
               ItemStack itemstack = this.recipeBookContainer.getSlot(j).getStack();
               if (!itemstack.isEmpty() && Math.min(i, itemstack.getMaxStackSize()) < itemstack.getCount() + 1) {
                  return;
               }
            }
         }
      }

      int j1 = this.getMaxAmount(placeAll, i, flag);
      IntList intlist = new IntArrayList();
      if (this.recipeItemHelper.canCraft(p_201508_1_, intlist, j1)) {
         int k = j1;

         for(int l : intlist) {
            int i1 = RecipeItemHelper.unpack(l).getMaxStackSize();
            if (i1 < k) {
               k = i1;
            }
         }

         if (this.recipeItemHelper.canCraft(p_201508_1_, intlist, k)) {
            this.clear();
            this.placeRecipe(this.recipeBookContainer.getWidth(), this.recipeBookContainer.getHeight(), this.recipeBookContainer.getOutputSlot(), p_201508_1_, intlist.iterator(), k);
         }
      }

   }

   public void setSlotContents(Iterator<Integer> ingredients, int slotIn, int maxAmount, int y, int x) {
      Slot slot = this.recipeBookContainer.getSlot(slotIn);
      ItemStack itemstack = RecipeItemHelper.unpack(ingredients.next());
      if (!itemstack.isEmpty()) {
         for(int i = 0; i < maxAmount; ++i) {
            this.consumeIngredient(slot, itemstack);
         }
      }

   }

   protected int getMaxAmount(boolean placeAll, int maxPossible, boolean recipeMatches) {
      int i = 1;
      if (placeAll) {
         i = maxPossible;
      } else if (recipeMatches) {
         i = 64;

         for(int j = 0; j < this.recipeBookContainer.getWidth() * this.recipeBookContainer.getHeight() + 1; ++j) {
            if (j != this.recipeBookContainer.getOutputSlot()) {
               ItemStack itemstack = this.recipeBookContainer.getSlot(j).getStack();
               if (!itemstack.isEmpty() && i > itemstack.getCount()) {
                  i = itemstack.getCount();
               }
            }
         }

         if (i < 64) {
            ++i;
         }
      }

      return i;
   }

   protected void consumeIngredient(Slot slotToFill, ItemStack ingredientIn) {
      int i = this.playerInventory.findSlotMatchingUnusedItem(ingredientIn);
      if (i != -1) {
         ItemStack itemstack = this.playerInventory.getStackInSlot(i).copy();
         if (!itemstack.isEmpty()) {
            if (itemstack.getCount() > 1) {
               this.playerInventory.decrStackSize(i, 1);
            } else {
               this.playerInventory.removeStackFromSlot(i);
            }

            itemstack.setCount(1);
            if (slotToFill.getStack().isEmpty()) {
               slotToFill.putStack(itemstack);
            } else {
               slotToFill.getStack().grow(1);
            }

         }
      }
   }

   private boolean func_194328_c() {
      List<ItemStack> list = Lists.newArrayList();
      int i = this.getEmptyPlayerSlots();

      for(int j = 0; j < this.recipeBookContainer.getWidth() * this.recipeBookContainer.getHeight() + 1; ++j) {
         if (j != this.recipeBookContainer.getOutputSlot()) {
            ItemStack itemstack = this.recipeBookContainer.getSlot(j).getStack().copy();
            if (!itemstack.isEmpty()) {
               int k = this.playerInventory.storeItemStack(itemstack);
               if (k == -1 && list.size() <= i) {
                  for(ItemStack itemstack1 : list) {
                     if (itemstack1.isItemEqual(itemstack) && itemstack1.getCount() != itemstack1.getMaxStackSize() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) {
                        itemstack1.grow(itemstack.getCount());
                        itemstack.setCount(0);
                        break;
                     }
                  }

                  if (!itemstack.isEmpty()) {
                     if (list.size() >= i) {
                        return false;
                     }

                     list.add(itemstack);
                  }
               } else if (k == -1) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private int getEmptyPlayerSlots() {
      int i = 0;

      for(ItemStack itemstack : this.playerInventory.mainInventory) {
         if (itemstack.isEmpty()) {
            ++i;
         }
      }

      return i;
   }
}