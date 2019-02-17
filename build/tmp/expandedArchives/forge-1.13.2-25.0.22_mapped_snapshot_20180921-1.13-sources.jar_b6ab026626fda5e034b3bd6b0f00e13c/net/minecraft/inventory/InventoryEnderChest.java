package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryEnderChest extends InventoryBasic {
   private TileEntityEnderChest associatedChest;

   public InventoryEnderChest() {
      super(new TextComponentTranslation("container.enderchest"), 27);
   }

   public void setChestTileEntity(TileEntityEnderChest chestTileEntity) {
      this.associatedChest = chestTileEntity;
   }

   public void read(NBTTagList p_70486_1_) {
      for(int i = 0; i < this.getSizeInventory(); ++i) {
         this.setInventorySlotContents(i, ItemStack.EMPTY);
      }

      for(int k = 0; k < p_70486_1_.size(); ++k) {
         NBTTagCompound nbttagcompound = p_70486_1_.getCompound(k);
         int j = nbttagcompound.getByte("Slot") & 255;
         if (j >= 0 && j < this.getSizeInventory()) {
            this.setInventorySlotContents(j, ItemStack.read(nbttagcompound));
         }
      }

   }

   public NBTTagList write() {
      NBTTagList nbttaglist = new NBTTagList();

      for(int i = 0; i < this.getSizeInventory(); ++i) {
         ItemStack itemstack = this.getStackInSlot(i);
         if (!itemstack.isEmpty()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setByte("Slot", (byte)i);
            itemstack.write(nbttagcompound);
            nbttaglist.add((INBTBase)nbttagcompound);
         }
      }

      return nbttaglist;
   }

   /**
    * Don't rename this method to canInteractWith due to conflicts with Container
    */
   public boolean isUsableByPlayer(EntityPlayer player) {
      return this.associatedChest != null && !this.associatedChest.canBeUsed(player) ? false : super.isUsableByPlayer(player);
   }

   public void openInventory(EntityPlayer player) {
      if (this.associatedChest != null) {
         this.associatedChest.openChest();
      }

      super.openInventory(player);
   }

   public void closeInventory(EntityPlayer player) {
      if (this.associatedChest != null) {
         this.associatedChest.closeChest();
      }

      super.closeInventory(player);
      this.associatedChest = null;
   }
}