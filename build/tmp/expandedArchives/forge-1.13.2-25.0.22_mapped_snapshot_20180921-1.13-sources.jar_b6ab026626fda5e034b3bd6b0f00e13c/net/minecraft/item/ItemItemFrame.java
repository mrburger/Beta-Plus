package net.minecraft.item;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemItemFrame extends ItemHangingEntity {
   public ItemItemFrame(Item.Properties builder) {
      super(EntityItemFrame.class, builder);
   }

   protected boolean canPlace(EntityPlayer p_200127_1_, EnumFacing p_200127_2_, ItemStack p_200127_3_, BlockPos p_200127_4_) {
      return !World.isOutsideBuildHeight(p_200127_4_) && p_200127_1_.canPlayerEdit(p_200127_4_, p_200127_2_, p_200127_3_);
   }
}