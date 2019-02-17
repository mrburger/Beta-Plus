package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryCrafting implements IInventory, IRecipeHelperPopulator {
   /** List of the stacks in the crafting matrix. */
   private final NonNullList<ItemStack> stackList;
   /** the width of the crafting inventory */
   private final int width;
   private final int height;
   /** Class containing the callbacks for the events on_GUIClosed and on_CraftMaxtrixChanged. */
   private final Container eventHandler;

   public InventoryCrafting(Container eventHandlerIn, int width, int height) {
      this.stackList = NonNullList.withSize(width * height, ItemStack.EMPTY);
      this.eventHandler = eventHandlerIn;
      this.width = width;
      this.height = height;
   }

   /**
    * Returns the number of slots in the inventory.
    */
   public int getSizeInventory() {
      return this.stackList.size();
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.stackList) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   /**
    * Returns the stack in the given slot.
    */
   public ItemStack getStackInSlot(int index) {
      return index >= this.getSizeInventory() ? ItemStack.EMPTY : this.stackList.get(index);
   }

   public ITextComponent getName() {
      return new TextComponentTranslation("container.crafting");
   }

   public boolean hasCustomName() {
      return false;
   }

   @Nullable
   public ITextComponent getCustomName() {
      return null;
   }

   /**
    * Removes a stack from the given slot and returns it.
    */
   public ItemStack removeStackFromSlot(int index) {
      return ItemStackHelper.getAndRemove(this.stackList, index);
   }

   /**
    * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
    */
   public ItemStack decrStackSize(int index, int count) {
      ItemStack itemstack = ItemStackHelper.getAndSplit(this.stackList, index, count);
      if (!itemstack.isEmpty()) {
         this.eventHandler.onCraftMatrixChanged(this);
      }

      return itemstack;
   }

   /**
    * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
    */
   public void setInventorySlotContents(int index, ItemStack stack) {
      this.stackList.set(index, stack);
      this.eventHandler.onCraftMatrixChanged(this);
   }

   /**
    * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
    */
   public int getInventoryStackLimit() {
      return 64;
   }

   /**
    * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
    * hasn't changed and skip it.
    */
   public void markDirty() {
   }

   /**
    * Don't rename this method to canInteractWith due to conflicts with Container
    */
   public boolean isUsableByPlayer(EntityPlayer player) {
      return true;
   }

   public void openInventory(EntityPlayer player) {
   }

   public void closeInventory(EntityPlayer player) {
   }

   /**
    * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
    * guis use Slot.isItemValid
    */
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   public int getField(int id) {
      return 0;
   }

   public void setField(int id, int value) {
   }

   public int getFieldCount() {
      return 0;
   }

   public void clear() {
      this.stackList.clear();
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   public void fillStackedContents(RecipeItemHelper helper) {
      for(ItemStack itemstack : this.stackList) {
         helper.accountPlainStack(itemstack);
      }

   }
}