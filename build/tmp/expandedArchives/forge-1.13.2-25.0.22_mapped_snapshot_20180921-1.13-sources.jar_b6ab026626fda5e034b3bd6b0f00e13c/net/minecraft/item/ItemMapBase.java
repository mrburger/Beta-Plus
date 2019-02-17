package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

public class ItemMapBase extends Item {
   public ItemMapBase(Item.Properties builder) {
      super(builder);
   }

   /**
    * Returns {@code} true if this is a complex item.
    */
   public boolean isComplex() {
      return true;
   }

   @Nullable
   public Packet<?> getUpdatePacket(ItemStack stack, World worldIn, EntityPlayer player) {
      return null;
   }
}