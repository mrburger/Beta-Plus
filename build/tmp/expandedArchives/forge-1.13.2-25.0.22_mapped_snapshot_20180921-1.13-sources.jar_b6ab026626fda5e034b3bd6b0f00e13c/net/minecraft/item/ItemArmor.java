package net.minecraft.item;

import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemArmor extends Item {
   private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
   public static final IBehaviorDispenseItem DISPENSER_BEHAVIOR = new BehaviorDefaultDispenseItem() {
      /**
       * Dispense the specified stack, play the dispense sound and spawn particles.
       */
      protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
         ItemStack itemstack = ItemArmor.dispenseArmor(source, stack);
         return itemstack.isEmpty() ? super.dispenseStack(source, stack) : itemstack;
      }
   };
   /** Stores the armor type: 0 is helmet, 1 is plate, 2 is legs and 3 is boots */
   protected final EntityEquipmentSlot armorType;
   /** Holds the amount of damage that the armor reduces at full durability. */
   protected final int damageReduceAmount;
   protected final float toughness;
   protected final IArmorMaterial material;

   public static ItemStack dispenseArmor(IBlockSource blockSource, ItemStack stack) {
      BlockPos blockpos = blockSource.getBlockPos().offset(blockSource.getBlockState().get(BlockDispenser.FACING));
      List<EntityLivingBase> list = blockSource.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(blockpos), EntitySelectors.NOT_SPECTATING.and(new EntitySelectors.ArmoredMob(stack)));
      if (list.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         EntityLivingBase entitylivingbase = list.get(0);
         EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(stack);
         ItemStack itemstack = stack.split(1);
         entitylivingbase.setItemStackToSlot(entityequipmentslot, itemstack);
         if (entitylivingbase instanceof EntityLiving) {
            ((EntityLiving)entitylivingbase).setDropChance(entityequipmentslot, 2.0F);
            ((EntityLiving)entitylivingbase).enablePersistence();
         }

         return stack;
      }
   }

   public ItemArmor(IArmorMaterial materialIn, EntityEquipmentSlot slots, Item.Properties builder) {
      super(builder.defaultMaxDamage(materialIn.getDurability(slots)));
      this.material = materialIn;
      this.armorType = slots;
      this.damageReduceAmount = materialIn.getDamageReductionAmount(slots);
      this.toughness = materialIn.getToughness();
      BlockDispenser.registerDispenseBehavior(this, DISPENSER_BEHAVIOR);
   }

   /**
    * Gets the equipment slot of this armor piece (formerly known as armor type)
    */
   public EntityEquipmentSlot getEquipmentSlot() {
      return this.armorType;
   }

   /**
    * Return the enchantability factor of the item, most of the time is based on material.
    */
   public int getItemEnchantability() {
      return this.material.getEnchantability();
   }

   public IArmorMaterial getArmorMaterial() {
      return this.material;
   }

   /**
    * Return whether this item is repairable in an anvil.
    */
   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return this.material.getRepairMaterial().test(repair) || super.getIsRepairable(toRepair, repair);
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

   /**
    * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
    */
   public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
      Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot);
      if (equipmentSlot == this.armorType) {
         multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor modifier", (double)this.damageReduceAmount, 0));
         multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor toughness", (double)this.toughness, 0));
      }

      return multimap;
   }

   public int getDamageReduceAmount() {
      return this.damageReduceAmount;
   }

   public float getToughness() {
       return this.toughness;
   }
}