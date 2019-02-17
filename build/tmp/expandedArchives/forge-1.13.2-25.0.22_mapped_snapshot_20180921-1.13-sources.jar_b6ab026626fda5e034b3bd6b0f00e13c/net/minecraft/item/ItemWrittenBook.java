package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemWrittenBook extends Item {
   public ItemWrittenBook(Item.Properties builder) {
      super(builder);
   }

   public static boolean validBookTagContents(@Nullable NBTTagCompound nbt) {
      if (!ItemWritableBook.isNBTValid(nbt)) {
         return false;
      } else if (!nbt.contains("title", 8)) {
         return false;
      } else {
         String s = nbt.getString("title");
         return s.length() > 32 ? false : nbt.contains("author", 8);
      }
   }

   /**
    * Gets the generation of the book (how many times it has been cloned)
    */
   public static int getGeneration(ItemStack book) {
      return book.getTag().getInt("generation");
   }

   public ITextComponent getDisplayName(ItemStack stack) {
      if (stack.hasTag()) {
         NBTTagCompound nbttagcompound = stack.getTag();
         String s = nbttagcompound.getString("title");
         if (!StringUtils.isNullOrEmpty(s)) {
            return new TextComponentString(s);
         }
      }

      return super.getDisplayName(stack);
   }

   /**
    * allows items to add custom lines of information to the mouseover description
    */
   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      if (stack.hasTag()) {
         NBTTagCompound nbttagcompound = stack.getTag();
         String s = nbttagcompound.getString("author");
         if (!StringUtils.isNullOrEmpty(s)) {
            tooltip.add((new TextComponentTranslation("book.byAuthor", s)).applyTextStyle(TextFormatting.GRAY));
         }

         tooltip.add((new TextComponentTranslation("book.generation." + nbttagcompound.getInt("generation"))).applyTextStyle(TextFormatting.GRAY));
      }

   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      if (!worldIn.isRemote) {
         this.resolveContents(itemstack, playerIn);
      }

      playerIn.openBook(itemstack, handIn);
      playerIn.addStat(StatList.ITEM_USED.get(this));
      return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
   }

   private void resolveContents(ItemStack stack, EntityPlayer player) {
      NBTTagCompound nbttagcompound = stack.getTag();
      if (nbttagcompound != null && !nbttagcompound.getBoolean("resolved")) {
         nbttagcompound.setBoolean("resolved", true);
         if (validBookTagContents(nbttagcompound)) {
            NBTTagList nbttaglist = nbttagcompound.getList("pages", 8);

            for(int i = 0; i < nbttaglist.size(); ++i) {
               String s = nbttaglist.getString(i);

               ITextComponent itextcomponent;
               try {
                  itextcomponent = ITextComponent.Serializer.fromJsonLenient(s);
                  itextcomponent = TextComponentUtils.updateForEntity(player.getCommandSource(), itextcomponent, player);
               } catch (Exception var9) {
                  itextcomponent = new TextComponentString(s);
               }

               nbttaglist.set(i, (INBTBase)(new NBTTagString(ITextComponent.Serializer.toJson(itextcomponent))));
            }

            nbttagcompound.setTag("pages", nbttaglist);
            if (player instanceof EntityPlayerMP && player.getHeldItemMainhand() == stack) {
               Slot slot = player.openContainer.getSlotFromInventory(player.inventory, player.inventory.currentItem);
               ((EntityPlayerMP)player).connection.sendPacket(new SPacketSetSlot(0, slot.slotNumber, stack));
            }

         }
      }
   }

   /**
    * Returns true if this item has an enchantment glint. By default, this returns <code>stack.isItemEnchanted()</code>,
    * but other items can override it (for instance, written books always return true).
    *  
    * Note that if you override this method, you generally want to also call the super version (on {@link Item}) to get
    * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean hasEffect(ItemStack stack) {
      return true;
   }
}