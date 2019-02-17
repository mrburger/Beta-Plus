package net.minecraft.tileentity;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class TileEntityDispenser extends TileEntityLockableLoot {
   private static final Random RNG = new Random();
   private NonNullList<ItemStack> stacks = NonNullList.withSize(9, ItemStack.EMPTY);

   protected TileEntityDispenser(TileEntityType<?> p_i48286_1_) {
      super(p_i48286_1_);
   }

   public TileEntityDispenser() {
      this(TileEntityType.DISPENSER);
   }

   /**
    * Returns the number of slots in the inventory.
    */
   public int getSizeInventory() {
      return 9;
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.stacks) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public int getDispenseSlot() {
      this.fillWithLoot((EntityPlayer)null);
      int i = -1;
      int j = 1;

      for(int k = 0; k < this.stacks.size(); ++k) {
         if (!this.stacks.get(k).isEmpty() && RNG.nextInt(j++) == 0) {
            i = k;
         }
      }

      return i;
   }

   /**
    * Add the given ItemStack to this Dispenser. Return the Slot the Item was placed in or -1 if no free slot is
    * available.
    */
   public int addItemStack(ItemStack stack) {
      for(int i = 0; i < this.stacks.size(); ++i) {
         if (this.stacks.get(i).isEmpty()) {
            this.setInventorySlotContents(i, stack);
            return i;
         }
      }

      return -1;
   }

   public ITextComponent getName() {
      ITextComponent itextcomponent = this.getCustomName();
      return (ITextComponent)(itextcomponent != null ? itextcomponent : new TextComponentTranslation("container.dispenser"));
   }

   public void read(NBTTagCompound compound) {
      super.read(compound);
      this.stacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      if (!this.checkLootAndRead(compound)) {
         ItemStackHelper.loadAllItems(compound, this.stacks);
      }

      if (compound.contains("CustomName", 8)) {
         this.customName = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
      }

   }

   public NBTTagCompound write(NBTTagCompound compound) {
      super.write(compound);
      if (!this.checkLootAndWrite(compound)) {
         ItemStackHelper.saveAllItems(compound, this.stacks);
      }

      ITextComponent itextcomponent = this.getCustomName();
      if (itextcomponent != null) {
         compound.setString("CustomName", ITextComponent.Serializer.toJson(itextcomponent));
      }

      return compound;
   }

   /**
    * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
    */
   public int getInventoryStackLimit() {
      return 64;
   }

   public String getGuiID() {
      return "minecraft:dispenser";
   }

   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      this.fillWithLoot(playerIn);
      return new ContainerDispenser(playerInventory, this);
   }

   protected NonNullList<ItemStack> getItems() {
      return this.stacks;
   }

   protected void setItems(NonNullList<ItemStack> itemsIn) {
      this.stacks = itemsIn;
   }
}