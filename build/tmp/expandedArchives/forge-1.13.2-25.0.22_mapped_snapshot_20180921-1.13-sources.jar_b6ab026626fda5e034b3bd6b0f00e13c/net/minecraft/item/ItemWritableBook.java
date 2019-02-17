package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemWritableBook extends Item {
   public ItemWritableBook(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      playerIn.openBook(itemstack, handIn);
      playerIn.addStat(StatList.ITEM_USED.get(this));
      return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
   }

   /**
    * this method returns true if the book's NBT Tag List "pages" is valid
    */
   public static boolean isNBTValid(@Nullable NBTTagCompound nbt) {
      if (nbt == null) {
         return false;
      } else if (!nbt.contains("pages", 9)) {
         return false;
      } else {
         NBTTagList nbttaglist = nbt.getList("pages", 8);

         for(int i = 0; i < nbttaglist.size(); ++i) {
            String s = nbttaglist.getString(i);
            if (s.length() > 32767) {
               return false;
            }
         }

         return true;
      }
   }
}