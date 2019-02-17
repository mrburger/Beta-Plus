package net.minecraft.dispenser;

import net.minecraft.item.ItemStack;

public interface IBehaviorDispenseItem {
   IBehaviorDispenseItem NOOP = (p_210297_0_, p_210297_1_) -> {
      return p_210297_1_;
   };

   ItemStack dispense(IBlockSource p_dispense_1_, ItemStack p_dispense_2_);
}