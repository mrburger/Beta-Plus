package net.minecraft.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.world.World;

public class ItemArrow extends Item {
   public ItemArrow(Item.Properties builder) {
      super(builder);
   }

   public EntityArrow createArrow(World p_200887_1_, ItemStack p_200887_2_, EntityLivingBase p_200887_3_) {
      EntityTippedArrow entitytippedarrow = new EntityTippedArrow(p_200887_1_, p_200887_3_);
      entitytippedarrow.setPotionEffect(p_200887_2_);
      return entitytippedarrow;
   }

   public boolean isInfinite(ItemStack stack, ItemStack bow, net.minecraft.entity.player.EntityPlayer player) {
      int enchant = net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.init.Enchantments.INFINITY, bow);
      return enchant <= 0 ? false : this.getClass() == ItemArrow.class;
   }
}