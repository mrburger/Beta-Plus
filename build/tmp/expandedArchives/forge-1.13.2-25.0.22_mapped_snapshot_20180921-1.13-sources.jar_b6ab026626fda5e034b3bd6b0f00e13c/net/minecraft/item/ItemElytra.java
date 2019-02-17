package net.minecraft.item;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ItemElytra extends Item {
   public ItemElytra(Item.Properties builder) {
      super(builder);
      this.addPropertyOverride(new ResourceLocation("broken"), (p_210312_0_, p_210312_1_, p_210312_2_) -> {
         return isUsable(p_210312_0_) ? 0.0F : 1.0F;
      });
      BlockDispenser.registerDispenseBehavior(this, ItemArmor.DISPENSER_BEHAVIOR);
   }

   public static boolean isUsable(ItemStack stack) {
      return stack.getDamage() < stack.getMaxDamage() - 1;
   }

   /**
    * Return whether this item is repairable in an anvil.
    */
   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return repair.getItem() == Items.PHANTOM_MEMBRANE;
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
      ItemStack itemstack1 = playerIn.getItemStackFromSlot(entityequipmentslot);
      if (itemstack1.isEmpty()) {
         playerIn.setItemStackToSlot(entityequipmentslot, itemstack.copy());
         itemstack.setCount(0);
         return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
      } else {
         return new ActionResult<>(EnumActionResult.FAIL, itemstack);
      }
   }
}