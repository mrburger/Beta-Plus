package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTrident;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemTrident extends Item {
   public ItemTrident(Item.Properties builder) {
      super(builder);
      this.addPropertyOverride(new ResourceLocation("throwing"), (p_210315_0_, p_210315_1_, p_210315_2_) -> {
         return p_210315_2_ != null && p_210315_2_.isHandActive() && p_210315_2_.getActiveItemStack() == p_210315_0_ ? 1.0F : 0.0F;
      });
   }

   public boolean canPlayerBreakBlockWhileHolding(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player) {
      return !player.isCreative();
   }

   /**
    * returns the action that specifies what animation to play when the items is being used
    */
   public EnumAction getUseAction(ItemStack stack) {
      return EnumAction.SPEAR;
   }

   /**
    * How long it takes to use or consume an item
    */
   public int getUseDuration(ItemStack stack) {
      return 72000;
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
      return false;
   }

   /**
    * Called when the player stops using an Item (stops holding the right mouse button).
    */
   public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
      if (entityLiving instanceof EntityPlayer) {
         EntityPlayer entityplayer = (EntityPlayer)entityLiving;
         int i = this.getUseDuration(stack) - timeLeft;
         if (i >= 10) {
            int j = EnchantmentHelper.getRiptideModifier(stack);
            if (j <= 0 || entityplayer.isWet()) {
               if (!worldIn.isRemote) {
                  stack.damageItem(1, entityplayer);
                  if (j == 0) {
                     EntityTrident entitytrident = new EntityTrident(worldIn, entityplayer, stack);
                     entitytrident.shoot(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, 2.5F + (float)j * 0.5F, 1.0F);
                     if (entityplayer.abilities.isCreativeMode) {
                        entitytrident.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                     }

                     worldIn.spawnEntity(entitytrident);
                     if (!entityplayer.abilities.isCreativeMode) {
                        entityplayer.inventory.deleteStack(stack);
                     }
                  }
               }

               entityplayer.addStat(StatList.ITEM_USED.get(this));
               SoundEvent soundevent = SoundEvents.ITEM_TRIDENT_THROW;
               if (j > 0) {
                  float f = entityplayer.rotationYaw;
                  float f1 = entityplayer.rotationPitch;
                  float f2 = -MathHelper.sin(f * ((float)Math.PI / 180F)) * MathHelper.cos(f1 * ((float)Math.PI / 180F));
                  float f3 = -MathHelper.sin(f1 * ((float)Math.PI / 180F));
                  float f4 = MathHelper.cos(f * ((float)Math.PI / 180F)) * MathHelper.cos(f1 * ((float)Math.PI / 180F));
                  float f5 = MathHelper.sqrt(f2 * f2 + f3 * f3 + f4 * f4);
                  float f6 = 3.0F * ((1.0F + (float)j) / 4.0F);
                  f2 = f2 * (f6 / f5);
                  f3 = f3 * (f6 / f5);
                  f4 = f4 * (f6 / f5);
                  entityplayer.addVelocity((double)f2, (double)f3, (double)f4);
                  if (j >= 3) {
                     soundevent = SoundEvents.ITEM_TRIDENT_RIPTIDE_3;
                  } else if (j == 2) {
                     soundevent = SoundEvents.ITEM_TRIDENT_RIPTIDE_2;
                  } else {
                     soundevent = SoundEvents.ITEM_TRIDENT_RIPTIDE_1;
                  }

                  entityplayer.startSpinAttack(20);
                  if (entityplayer.onGround) {
                     float f7 = 1.1999999F;
                     entityplayer.move(MoverType.SELF, 0.0D, (double)1.1999999F, 0.0D);
                  }
               }

               worldIn.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, soundevent, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
         }
      }
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      if (itemstack.getDamage() >= itemstack.getMaxDamage()) {
         return new ActionResult<>(EnumActionResult.FAIL, itemstack);
      } else if (EnchantmentHelper.getRiptideModifier(itemstack) > 0 && !playerIn.isWet()) {
         return new ActionResult<>(EnumActionResult.FAIL, itemstack);
      } else {
         playerIn.setActiveHand(handIn);
         return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
      }
   }

   /**
    * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
    * the damage on the stack.
    */
   public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
      stack.damageItem(1, attacker);
      return true;
   }

   /**
    * Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
    */
   public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
      if ((double)state.getBlockHardness(worldIn, pos) != 0.0D) {
         stack.damageItem(2, entityLiving);
      }

      return true;
   }

   /**
    * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
    */
   public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
      Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot);
      if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
         multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 8.0D, 0));
         multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double)-2.9F, 0));
      }

      return multimap;
   }

   /**
    * Return the enchantability factor of the item, most of the time is based on material.
    */
   public int getItemEnchantability() {
      return 1;
   }
}