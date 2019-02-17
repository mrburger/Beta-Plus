package net.minecraft.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.SoundEvents;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemFishingRod extends Item {
   public ItemFishingRod(Item.Properties builder) {
      super(builder);
      this.addPropertyOverride(new ResourceLocation("cast"), (p_210313_0_, p_210313_1_, p_210313_2_) -> {
         if (p_210313_2_ == null) {
            return 0.0F;
         } else {
            boolean flag = p_210313_2_.getHeldItemMainhand() == p_210313_0_;
            boolean flag1 = p_210313_2_.getHeldItemOffhand() == p_210313_0_;
            if (p_210313_2_.getHeldItemMainhand().getItem() instanceof ItemFishingRod) {
               flag1 = false;
            }

            return (flag || flag1) && p_210313_2_ instanceof EntityPlayer && ((EntityPlayer)p_210313_2_).fishEntity != null ? 1.0F : 0.0F;
         }
      });
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      if (playerIn.fishEntity != null) {
         int i = playerIn.fishEntity.handleHookRetraction(itemstack);
         itemstack.damageItem(i, playerIn);
         playerIn.swingArm(handIn);
         worldIn.playSound((EntityPlayer)null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      } else {
         worldIn.playSound((EntityPlayer)null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
         if (!worldIn.isRemote) {
            EntityFishHook entityfishhook = new EntityFishHook(worldIn, playerIn);
            int j = EnchantmentHelper.getFishingSpeedBonus(itemstack);
            if (j > 0) {
               entityfishhook.setLureSpeed(j);
            }

            int k = EnchantmentHelper.getFishingLuckBonus(itemstack);
            if (k > 0) {
               entityfishhook.setLuck(k);
            }

            worldIn.spawnEntity(entityfishhook);
         }

         playerIn.swingArm(handIn);
         playerIn.addStat(StatList.ITEM_USED.get(this));
      }

      return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
   }

   /**
    * Return the enchantability factor of the item, most of the time is based on material.
    */
   public int getItemEnchantability() {
      return 1;
   }
}