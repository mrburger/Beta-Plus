package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemGlassBottle extends Item {
   public ItemGlassBottle(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      List<EntityAreaEffectCloud> list = worldIn.getEntitiesWithinAABB(EntityAreaEffectCloud.class, playerIn.getBoundingBox().grow(2.0D), (p_210311_0_) -> {
         return p_210311_0_ != null && p_210311_0_.isAlive() && p_210311_0_.getOwner() instanceof EntityDragon;
      });
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      if (!list.isEmpty()) {
         EntityAreaEffectCloud entityareaeffectcloud = list.get(0);
         entityareaeffectcloud.setRadius(entityareaeffectcloud.getRadius() - 0.5F);
         worldIn.playSound((EntityPlayer)null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
         return new ActionResult<>(EnumActionResult.SUCCESS, this.turnBottleIntoItem(itemstack, playerIn, new ItemStack(Items.DRAGON_BREATH)));
      } else {
         RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, true);
         if (raytraceresult == null) {
            return new ActionResult<>(EnumActionResult.PASS, itemstack);
         } else {
            if (raytraceresult.type == RayTraceResult.Type.BLOCK) {
               BlockPos blockpos = raytraceresult.getBlockPos();
               if (!worldIn.isBlockModifiable(playerIn, blockpos)) {
                  return new ActionResult<>(EnumActionResult.PASS, itemstack);
               }

               if (worldIn.getFluidState(blockpos).isTagged(FluidTags.WATER)) {
                  worldIn.playSound(playerIn, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                  return new ActionResult<>(EnumActionResult.SUCCESS, this.turnBottleIntoItem(itemstack, playerIn, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), PotionTypes.WATER)));
               }
            }

            return new ActionResult<>(EnumActionResult.PASS, itemstack);
         }
      }
   }

   protected ItemStack turnBottleIntoItem(ItemStack p_185061_1_, EntityPlayer player, ItemStack stack) {
      p_185061_1_.shrink(1);
      player.addStat(StatList.ITEM_USED.get(this));
      if (p_185061_1_.isEmpty()) {
         return stack;
      } else {
         if (!player.inventory.addItemStackToInventory(stack)) {
            player.dropItem(stack, false);
         }

         return p_185061_1_;
      }
   }
}