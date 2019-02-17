package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerFurnace extends ContainerRecipeBook {
   private final IInventory tileFurnace;
   private final World world;
   private int cookTime;
   private int totalCookTime;
   private int furnaceBurnTime;
   private int currentItemBurnTime;

   public ContainerFurnace(InventoryPlayer playerInventory, IInventory furnaceInventory) {
      this.tileFurnace = furnaceInventory;
      this.world = playerInventory.player.world;
      this.addSlot(new Slot(furnaceInventory, 0, 56, 17));
      this.addSlot(new SlotFurnaceFuel(furnaceInventory, 1, 56, 53));
      this.addSlot(new SlotFurnaceOutput(playerInventory.player, furnaceInventory, 2, 116, 35));

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
      }

   }

   public void addListener(IContainerListener listener) {
      super.addListener(listener);
      listener.sendAllWindowProperties(this, this.tileFurnace);
   }

   public void func_201771_a(RecipeItemHelper p_201771_1_) {
      if (this.tileFurnace instanceof IRecipeHelperPopulator) {
         ((IRecipeHelperPopulator)this.tileFurnace).fillStackedContents(p_201771_1_);
      }

   }

   public void clear() {
      this.tileFurnace.clear();
   }

   public boolean matches(IRecipe p_201769_1_) {
      return p_201769_1_.matches(this.tileFurnace, this.world);
   }

   public int getOutputSlot() {
      return 2;
   }

   public int getWidth() {
      return 1;
   }

   public int getHeight() {
      return 1;
   }

   @OnlyIn(Dist.CLIENT)
   public int getSize() {
      return 3;
   }

   /**
    * Looks for changes made in the container, sends them to every listener.
    */
   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for(IContainerListener icontainerlistener : this.listeners) {
         if (this.cookTime != this.tileFurnace.getField(2)) {
            icontainerlistener.sendWindowProperty(this, 2, this.tileFurnace.getField(2));
         }

         if (this.furnaceBurnTime != this.tileFurnace.getField(0)) {
            icontainerlistener.sendWindowProperty(this, 0, this.tileFurnace.getField(0));
         }

         if (this.currentItemBurnTime != this.tileFurnace.getField(1)) {
            icontainerlistener.sendWindowProperty(this, 1, this.tileFurnace.getField(1));
         }

         if (this.totalCookTime != this.tileFurnace.getField(3)) {
            icontainerlistener.sendWindowProperty(this, 3, this.tileFurnace.getField(3));
         }
      }

      this.cookTime = this.tileFurnace.getField(2);
      this.furnaceBurnTime = this.tileFurnace.getField(0);
      this.currentItemBurnTime = this.tileFurnace.getField(1);
      this.totalCookTime = this.tileFurnace.getField(3);
   }

   @OnlyIn(Dist.CLIENT)
   public void updateProgressBar(int id, int data) {
      this.tileFurnace.setField(id, data);
   }

   /**
    * Determines whether supplied player can use this container
    */
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.tileFurnace.isUsableByPlayer(playerIn);
   }

   /**
    * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
    * inventory and the other inventory(s).
    */
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (index == 2) {
            if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            slot.onSlotChange(itemstack1, itemstack);
         } else if (index != 1 && index != 0) {
            if (this.canSmelt(itemstack1)) {
               if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (TileEntityFurnace.isItemFuel(itemstack1)) {
               if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (index >= 3 && index < 30) {
               if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }

         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(playerIn, itemstack1);
      }

      return itemstack;
   }

   private boolean canSmelt(ItemStack p_206253_1_) {
      for(IRecipe irecipe : this.world.getRecipeManager().getRecipes(net.minecraftforge.common.crafting.VanillaRecipeTypes.SMELTING)) {
         if (irecipe.getIngredients().get(0).test(p_206253_1_)) {
            return true;
         }
      }

      return false;
   }
}