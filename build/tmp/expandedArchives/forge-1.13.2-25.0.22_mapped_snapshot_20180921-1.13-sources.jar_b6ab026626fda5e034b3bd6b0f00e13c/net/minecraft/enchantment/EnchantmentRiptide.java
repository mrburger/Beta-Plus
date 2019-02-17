package net.minecraft.enchantment;

import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentRiptide extends Enchantment {
   public EnchantmentRiptide(Enchantment.Rarity rarityIn, EntityEquipmentSlot... slots) {
      super(rarityIn, EnumEnchantmentType.TRIDENT, slots);
   }

   /**
    * Returns the minimal value of enchantability needed on the enchantment level passed.
    */
   public int getMinEnchantability(int enchantmentLevel) {
      return 10 + enchantmentLevel * 7;
   }

   /**
    * Returns the maximum value of enchantability nedded on the enchantment level passed.
    */
   public int getMaxEnchantability(int enchantmentLevel) {
      return 50;
   }

   /**
    * Returns the maximum level that the enchantment can have.
    */
   public int getMaxLevel() {
      return 3;
   }

   /**
    * Determines if the enchantment passed can be applyied together with this enchantment.
    */
   public boolean canApplyTogether(Enchantment ench) {
      return super.canApplyTogether(ench) && ench != Enchantments.LOYALTY && ench != Enchantments.CHANNELING;
   }
}