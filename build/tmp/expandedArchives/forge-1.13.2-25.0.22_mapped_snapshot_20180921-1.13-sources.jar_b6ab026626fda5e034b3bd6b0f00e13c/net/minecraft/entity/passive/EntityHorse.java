package net.minecraft.entity.passive;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityHorse extends AbstractHorse {
   private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
   private static final DataParameter<Integer> HORSE_VARIANT = EntityDataManager.createKey(EntityHorse.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> HORSE_ARMOR = EntityDataManager.createKey(EntityHorse.class, DataSerializers.VARINT);
   private static final DataParameter<ItemStack> HORSE_ARMOR_STACK = EntityDataManager.<ItemStack>createKey(EntityHorse.class, DataSerializers.ITEM_STACK);
   private static final String[] HORSE_TEXTURES = new String[]{"textures/entity/horse/horse_white.png", "textures/entity/horse/horse_creamy.png", "textures/entity/horse/horse_chestnut.png", "textures/entity/horse/horse_brown.png", "textures/entity/horse/horse_black.png", "textures/entity/horse/horse_gray.png", "textures/entity/horse/horse_darkbrown.png"};
   private static final String[] HORSE_TEXTURES_ABBR = new String[]{"hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
   private static final String[] HORSE_MARKING_TEXTURES = new String[]{null, "textures/entity/horse/horse_markings_white.png", "textures/entity/horse/horse_markings_whitefield.png", "textures/entity/horse/horse_markings_whitedots.png", "textures/entity/horse/horse_markings_blackdots.png"};
   private static final String[] HORSE_MARKING_TEXTURES_ABBR = new String[]{"", "wo_", "wmo", "wdo", "bdo"};
   private String texturePrefix;
   private final String[] horseTexturesArray = new String[3];

   public EntityHorse(World worldIn) {
      super(EntityType.HORSE, worldIn);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(HORSE_VARIANT, 0);
      this.dataManager.register(HORSE_ARMOR, HorseArmorType.NONE.getOrdinal());
      this.dataManager.register(HORSE_ARMOR_STACK, ItemStack.EMPTY);
   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      compound.setInt("Variant", this.getHorseVariant());
      if (!this.horseChest.getStackInSlot(1).isEmpty()) {
         compound.setTag("ArmorItem", this.horseChest.getStackInSlot(1).write(new NBTTagCompound()));
      }

   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      this.setHorseVariant(compound.getInt("Variant"));
      if (compound.contains("ArmorItem", 10)) {
         ItemStack itemstack = ItemStack.read(compound.getCompound("ArmorItem"));
         if (!itemstack.isEmpty() && isArmor(itemstack)) {
            this.horseChest.setInventorySlotContents(1, itemstack);
         }
      }

      this.updateHorseSlots();
   }

   public void setHorseVariant(int variant) {
      this.dataManager.set(HORSE_VARIANT, variant);
      this.resetTexturePrefix();
   }

   public int getHorseVariant() {
      return this.dataManager.get(HORSE_VARIANT);
   }

   private void resetTexturePrefix() {
      this.texturePrefix = null;
   }

   @OnlyIn(Dist.CLIENT)
   private void setHorseTexturePaths() {
      int i = this.getHorseVariant();
      int j = (i & 255) % 7;
      int k = ((i & '\uff00') >> 8) % 5;
      HorseArmorType horsearmortype = this.getHorseArmorType();
      this.horseTexturesArray[0] = HORSE_TEXTURES[j];
      this.horseTexturesArray[1] = HORSE_MARKING_TEXTURES[k];
      this.horseTexturesArray[2] = horsearmortype.getTextureName();
      this.texturePrefix = "horse/" + HORSE_TEXTURES_ABBR[j] + HORSE_MARKING_TEXTURES_ABBR[k] + horsearmortype.getHash();
   }

   @OnlyIn(Dist.CLIENT)
   public String getHorseTexture() {
      if (this.texturePrefix == null) {
         this.setHorseTexturePaths();
      }

      return this.texturePrefix;
   }

   @OnlyIn(Dist.CLIENT)
   public String[] getVariantTexturePaths() {
      if (this.texturePrefix == null) {
         this.setHorseTexturePaths();
      }

      return this.horseTexturesArray;
   }

   /**
    * Updates the items in the saddle and armor slots of the horse's inventory.
    */
   protected void updateHorseSlots() {
      super.updateHorseSlots();
      this.setHorseArmorStack(this.horseChest.getStackInSlot(1));
   }

   /**
    * Set horse armor stack (for example: new ItemStack(Items.iron_horse_armor))
    */
   public void setHorseArmorStack(ItemStack itemStackIn) {
      HorseArmorType horsearmortype = HorseArmorType.getByItemStack(itemStackIn);
      this.dataManager.set(HORSE_ARMOR, horsearmortype.getOrdinal());
      this.dataManager.set(HORSE_ARMOR_STACK, itemStackIn);
      this.resetTexturePrefix();
      if (!this.world.isRemote) {
         this.getAttribute(SharedMonsterAttributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
         int i = horsearmortype.getProtection();
         if (i != 0) {
            this.getAttribute(SharedMonsterAttributes.ARMOR).applyModifier((new AttributeModifier(ARMOR_MODIFIER_UUID, "Horse armor bonus", (double)i, 0)).setSaved(false));
         }
      }

   }

   public HorseArmorType getHorseArmorType() {
      ItemStack stack = this.dataManager.get(HORSE_ARMOR_STACK);
      if (!stack.isEmpty()) return stack.getHorseArmorType();
      return HorseArmorType.getByOrdinal(this.dataManager.get(HORSE_ARMOR));
   }

   /**
    * Called by InventoryBasic.onInventoryChanged() on a array that is never filled.
    */
   public void onInventoryChanged(IInventory invBasic) {
      HorseArmorType horsearmortype = this.getHorseArmorType();
      super.onInventoryChanged(invBasic);
      HorseArmorType horsearmortype1 = this.getHorseArmorType();
      if (this.ticksExisted > 20 && horsearmortype != horsearmortype1 && horsearmortype1 != HorseArmorType.NONE) {
         this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
      }

   }

   protected void playGallopSound(SoundType p_190680_1_) {
      super.playGallopSound(p_190680_1_);
      if (this.rand.nextInt(10) == 0) {
         this.playSound(SoundEvents.ENTITY_HORSE_BREATHE, p_190680_1_.getVolume() * 0.6F, p_190680_1_.getPitch());
      }

   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)this.getModifiedMaxHealth());
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.getModifiedMovementSpeed());
      this.getAttribute(JUMP_STRENGTH).setBaseValue(this.getModifiedJumpStrength());
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (this.world.isRemote && this.dataManager.isDirty()) {
         this.dataManager.setClean();
         this.resetTexturePrefix();
      }
      ItemStack stack = this.horseChest.getStackInSlot(1);
      if (isArmor(stack)) stack.onHorseArmorTick(world, this);
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.ENTITY_HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.ENTITY_HORSE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      super.getHurtSound(damageSourceIn);
      return SoundEvents.ENTITY_HORSE_HURT;
   }

   protected SoundEvent getAngrySound() {
      super.getAngrySound();
      return SoundEvents.ENTITY_HORSE_ANGRY;
   }

   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_HORSE;
   }

   public boolean processInteract(EntityPlayer player, EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      boolean flag = !itemstack.isEmpty();
      if (flag && itemstack.getItem() instanceof ItemSpawnEgg) {
         return super.processInteract(player, hand);
      } else {
         if (!this.isChild()) {
            if (this.isTame() && player.isSneaking()) {
               this.openGUI(player);
               return true;
            }

            if (this.isBeingRidden()) {
               return super.processInteract(player, hand);
            }
         }

         if (flag) {
            if (this.handleEating(player, itemstack)) {
               if (!player.abilities.isCreativeMode) {
                  itemstack.shrink(1);
               }

               return true;
            }

            if (itemstack.interactWithEntity(player, this, hand)) {
               return true;
            }

            if (!this.isTame()) {
               this.makeMad();
               return true;
            }

            boolean flag1 = HorseArmorType.getByItemStack(itemstack) != HorseArmorType.NONE;
            boolean flag2 = !this.isChild() && !this.isHorseSaddled() && itemstack.getItem() == Items.SADDLE;
            if (flag1 || flag2) {
               this.openGUI(player);
               return true;
            }
         }

         if (this.isChild()) {
            return super.processInteract(player, hand);
         } else {
            this.mountTo(player);
            return true;
         }
      }
   }

   /**
    * Returns true if the mob is currently able to mate with the specified mob.
    */
   public boolean canMateWith(EntityAnimal otherAnimal) {
      if (otherAnimal == this) {
         return false;
      } else if (!(otherAnimal instanceof EntityDonkey) && !(otherAnimal instanceof EntityHorse)) {
         return false;
      } else {
         return this.canMate() && ((AbstractHorse)otherAnimal).canMate();
      }
   }

   public EntityAgeable createChild(EntityAgeable ageable) {
      AbstractHorse abstracthorse;
      if (ageable instanceof EntityDonkey) {
         abstracthorse = new EntityMule(this.world);
      } else {
         EntityHorse entityhorse = (EntityHorse)ageable;
         abstracthorse = new EntityHorse(this.world);
         int j = this.rand.nextInt(9);
         int i;
         if (j < 4) {
            i = this.getHorseVariant() & 255;
         } else if (j < 8) {
            i = entityhorse.getHorseVariant() & 255;
         } else {
            i = this.rand.nextInt(7);
         }

         int k = this.rand.nextInt(5);
         if (k < 2) {
            i = i | this.getHorseVariant() & '\uff00';
         } else if (k < 4) {
            i = i | entityhorse.getHorseVariant() & '\uff00';
         } else {
            i = i | this.rand.nextInt(5) << 8 & '\uff00';
         }

         ((EntityHorse)abstracthorse).setHorseVariant(i);
      }

      this.setOffspringAttributes(ageable, abstracthorse);
      return abstracthorse;
   }

   public boolean wearsArmor() {
      return true;
   }

   public boolean isArmor(ItemStack stack) {
      return HorseArmorType.isHorseArmor(stack);
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData entityLivingData, @Nullable NBTTagCompound itemNbt) {
      entityLivingData = super.onInitialSpawn(difficulty, entityLivingData, itemNbt);
      int i;
      if (entityLivingData instanceof EntityHorse.GroupData) {
         i = ((EntityHorse.GroupData)entityLivingData).variant;
      } else {
         i = this.rand.nextInt(7);
         entityLivingData = new EntityHorse.GroupData(i);
      }

      this.setHorseVariant(i | this.rand.nextInt(5) << 8);
      return entityLivingData;
   }

   public static class GroupData implements IEntityLivingData {
      public int variant;

      public GroupData(int variantIn) {
         this.variant = variantIn;
      }
   }
}