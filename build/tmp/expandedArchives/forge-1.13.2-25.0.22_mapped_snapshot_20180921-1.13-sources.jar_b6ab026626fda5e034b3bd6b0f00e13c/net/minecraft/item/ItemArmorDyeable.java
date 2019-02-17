package net.minecraft.item;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;

public class ItemArmorDyeable extends ItemArmor {
   public ItemArmorDyeable(IArmorMaterial materialIn, EntityEquipmentSlot slots, Item.Properties builder) {
      super(materialIn, slots, builder);
   }

   public boolean hasColor(ItemStack stack) {
      NBTTagCompound nbttagcompound = stack.getChildTag("display");
      return nbttagcompound != null && nbttagcompound.contains("color", 99);
   }

   public int getColor(ItemStack stack) {
      NBTTagCompound nbttagcompound = stack.getChildTag("display");
      return nbttagcompound != null && nbttagcompound.contains("color", 99) ? nbttagcompound.getInt("color") : 10511680;
   }

   public void removeColor(ItemStack stack) {
      NBTTagCompound nbttagcompound = stack.getChildTag("display");
      if (nbttagcompound != null && nbttagcompound.hasKey("color")) {
         nbttagcompound.removeTag("color");
      }

   }

   public void setColor(ItemStack stack, int color) {
      stack.getOrCreateChildTag("display").setInt("color", color);
   }
}