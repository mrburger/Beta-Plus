package net.minecraft.entity.monster;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIZombieAttack;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityPigZombie extends EntityZombie {
   private static final UUID ATTACK_SPEED_BOOST_MODIFIER_UUID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
   private static final AttributeModifier ATTACK_SPEED_BOOST_MODIFIER = (new AttributeModifier(ATTACK_SPEED_BOOST_MODIFIER_UUID, "Attacking speed boost", 0.05D, 0)).setSaved(false);
   /** Above zero if this PigZombie is Angry. */
   private int angerLevel;
   /** A random delay until this PigZombie next makes a sound. */
   private int randomSoundDelay;
   private UUID angerTargetUUID;

   public EntityPigZombie(World worldIn) {
      super(EntityType.ZOMBIE_PIGMAN, worldIn);
      this.isImmuneToFire = true;
   }

   /**
    * Hint to AI tasks that we were attacked by the passed EntityLivingBase and should retaliate. Is not guaranteed to
    * change our actual active target (for example if we are currently busy attacking someone else)
    */
   public void setRevengeTarget(@Nullable EntityLivingBase livingBase) {
      super.setRevengeTarget(livingBase);
      if (livingBase != null) {
         this.angerTargetUUID = livingBase.getUniqueID();
      }

   }

   protected void applyEntityAI() {
      this.tasks.addTask(2, new EntityAIZombieAttack(this, 1.0D, false));
      this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
      this.targetTasks.addTask(1, new EntityPigZombie.AIHurtByAggressor(this));
      this.targetTasks.addTask(2, new EntityPigZombie.AITargetAggressor(this));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.23F);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
   }

   protected boolean shouldDrown() {
      return false;
   }

   protected void updateAITasks() {
      IAttributeInstance iattributeinstance = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      if (this.isAngry()) {
         if (!this.isChild() && !iattributeinstance.hasModifier(ATTACK_SPEED_BOOST_MODIFIER)) {
            iattributeinstance.applyModifier(ATTACK_SPEED_BOOST_MODIFIER);
         }

         --this.angerLevel;
      } else if (iattributeinstance.hasModifier(ATTACK_SPEED_BOOST_MODIFIER)) {
         iattributeinstance.removeModifier(ATTACK_SPEED_BOOST_MODIFIER);
      }

      if (this.randomSoundDelay > 0 && --this.randomSoundDelay == 0) {
         this.playSound(SoundEvents.ENTITY_ZOMBIE_PIGMAN_ANGRY, this.getSoundVolume() * 2.0F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 1.8F);
      }

      if (this.angerLevel > 0 && this.angerTargetUUID != null && this.getRevengeTarget() == null) {
         EntityPlayer entityplayer = this.world.getPlayerEntityByUUID(this.angerTargetUUID);
         this.setRevengeTarget(entityplayer);
         this.attackingPlayer = entityplayer;
         this.recentlyHit = this.getRevengeTimer();
      }

      super.updateAITasks();
   }

   public boolean canSpawn(IWorld worldIn, boolean p_205020_2_) {
      return worldIn.getDifficulty() != EnumDifficulty.PEACEFUL;
   }

   public boolean isNotColliding(IWorldReaderBase worldIn) {
      return worldIn.checkNoEntityCollision(this, this.getBoundingBox()) && worldIn.isCollisionBoxesEmpty(this, this.getBoundingBox()) && !worldIn.containsAnyLiquid(this.getBoundingBox());
   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      compound.setShort("Anger", (short)this.angerLevel);
      if (this.angerTargetUUID != null) {
         compound.setString("HurtBy", this.angerTargetUUID.toString());
      } else {
         compound.setString("HurtBy", "");
      }

   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      this.angerLevel = compound.getShort("Anger");
      String s = compound.getString("HurtBy");
      if (!s.isEmpty()) {
         this.angerTargetUUID = UUID.fromString(s);
         EntityPlayer entityplayer = this.world.getPlayerEntityByUUID(this.angerTargetUUID);
         this.setRevengeTarget(entityplayer);
         if (entityplayer != null) {
            this.attackingPlayer = entityplayer;
            this.recentlyHit = this.getRevengeTimer();
         }
      }

   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else {
         Entity entity = source.getTrueSource();
         if (entity instanceof EntityPlayer && !((EntityPlayer)entity).isCreative()) {
            this.becomeAngryAt(entity);
         }

         return super.attackEntityFrom(source, amount);
      }
   }

   /**
    * Causes this PigZombie to become angry at the supplied Entity (which will be a player).
    */
   private void becomeAngryAt(Entity p_70835_1_) {
      this.angerLevel = 400 + this.rand.nextInt(400);
      this.randomSoundDelay = this.rand.nextInt(40);
      if (p_70835_1_ instanceof EntityLivingBase) {
         this.setRevengeTarget((EntityLivingBase)p_70835_1_);
      }

   }

   public boolean isAngry() {
      return this.angerLevel > 0;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_ZOMBIE_PIGMAN_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_ZOMBIE_PIGMAN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ZOMBIE_PIGMAN_DEATH;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_ZOMBIE_PIGMAN;
   }

   public boolean processInteract(EntityPlayer player, EnumHand hand) {
      return false;
   }

   /**
    * Gives armor or weapon for entity based on given DifficultyInstance
    */
   protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
      this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
   }

   protected ItemStack getSkullDrop() {
      return ItemStack.EMPTY;
   }

   public boolean isPreventingPlayerRest(EntityPlayer playerIn) {
      return this.isAngry();
   }

   static class AIHurtByAggressor extends EntityAIHurtByTarget {
      public AIHurtByAggressor(EntityPigZombie p_i45828_1_) {
         super(p_i45828_1_, true);
      }

      protected void setEntityAttackTarget(EntityCreature creatureIn, EntityLivingBase entityLivingBaseIn) {
         super.setEntityAttackTarget(creatureIn, entityLivingBaseIn);
         if (creatureIn instanceof EntityPigZombie) {
            ((EntityPigZombie)creatureIn).becomeAngryAt(entityLivingBaseIn);
         }

      }
   }

   static class AITargetAggressor extends EntityAINearestAttackableTarget<EntityPlayer> {
      public AITargetAggressor(EntityPigZombie p_i45829_1_) {
         super(p_i45829_1_, EntityPlayer.class, true);
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         return ((EntityPigZombie)this.taskOwner).isAngry() && super.shouldExecute();
      }
   }
}