package net.minecraft.item;

import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemCarrotOnAStick extends Item {
   public ItemCarrotOnAStick(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      if (worldIn.isRemote) {
         return new ActionResult<>(EnumActionResult.PASS, itemstack);
      } else {
         if (playerIn.isPassenger() && playerIn.getRidingEntity() instanceof EntityPig) {
            EntityPig entitypig = (EntityPig)playerIn.getRidingEntity();
            if (itemstack.getMaxDamage() - itemstack.getDamage() >= 7 && entitypig.boost()) {
               itemstack.damageItem(7, playerIn);
               if (itemstack.isEmpty()) {
                  ItemStack itemstack1 = new ItemStack(Items.FISHING_ROD);
                  itemstack1.setTag(itemstack.getTag());
                  return new ActionResult<>(EnumActionResult.SUCCESS, itemstack1);
               }

               return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
            }
         }

         playerIn.addStat(StatList.ITEM_USED.get(this));
         return new ActionResult<>(EnumActionResult.PASS, itemstack);
      }
   }
}