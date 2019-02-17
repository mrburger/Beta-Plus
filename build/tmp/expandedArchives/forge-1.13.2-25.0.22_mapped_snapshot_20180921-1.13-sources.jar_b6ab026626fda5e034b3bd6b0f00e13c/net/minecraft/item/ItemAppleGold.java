package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemAppleGold extends ItemFood {
   public ItemAppleGold(int healAmountIn, float saturation, boolean meat, Item.Properties builder) {
      super(healAmountIn, saturation, meat, builder);
   }

   protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
      if (!worldIn.isRemote) {
         player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 1));
         player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 0));
      }

   }
}