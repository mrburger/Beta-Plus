package net.minecraft.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityJukebox extends TileEntity {
   private ItemStack record = ItemStack.EMPTY;

   public TileEntityJukebox() {
      super(TileEntityType.JUKEBOX);
   }

   public void read(NBTTagCompound compound) {
      super.read(compound);
      if (compound.contains("RecordItem", 10)) {
         this.setRecord(ItemStack.read(compound.getCompound("RecordItem")));
      }

   }

   public NBTTagCompound write(NBTTagCompound compound) {
      super.write(compound);
      if (!this.getRecord().isEmpty()) {
         compound.setTag("RecordItem", this.getRecord().write(new NBTTagCompound()));
      }

      return compound;
   }

   public ItemStack getRecord() {
      return this.record;
   }

   public void setRecord(ItemStack p_195535_1_) {
      this.record = p_195535_1_;
      this.markDirty();
   }
}