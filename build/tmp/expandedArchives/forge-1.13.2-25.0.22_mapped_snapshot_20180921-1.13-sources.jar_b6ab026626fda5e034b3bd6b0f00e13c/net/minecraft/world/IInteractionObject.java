package net.minecraft.world;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.INameable;

public interface IInteractionObject extends INameable {
   Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn);

   String getGuiID();
}