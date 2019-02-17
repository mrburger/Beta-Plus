package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ItemEmptyMap extends ItemMapBase {
   public ItemEmptyMap(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = ItemMap.setupNewMap(worldIn, MathHelper.floor(playerIn.posX), MathHelper.floor(playerIn.posZ), (byte)0, true, false);
      ItemStack itemstack1 = playerIn.getHeldItem(handIn);
      itemstack1.shrink(1);
      if (itemstack1.isEmpty()) {
         return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
      } else {
         if (!playerIn.inventory.addItemStackToInventory(itemstack.copy())) {
            playerIn.dropItem(itemstack, false);
         }

         playerIn.addStat(StatList.ITEM_USED.get(this));
         return new ActionResult<>(EnumActionResult.SUCCESS, itemstack1);
      }
   }
}