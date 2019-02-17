package net.minecraft.client.player.inventory;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LocalBlockIntercommunication implements IInteractionObject {
   private final String guiID;
   private final ITextComponent displayName;

   public LocalBlockIntercommunication(String guiIdIn, ITextComponent displayNameIn) {
      this.guiID = guiIdIn;
      this.displayName = displayNameIn;
   }

   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      throw new UnsupportedOperationException();
   }

   public ITextComponent getName() {
      return this.displayName;
   }

   public boolean hasCustomName() {
      return false;
   }

   public String getGuiID() {
      return this.guiID;
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.displayName;
   }
}