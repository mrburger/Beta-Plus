package net.minecraft.entity.passive;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBeg;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityWolf extends EntityTameable {
   private static final DataParameter<Float> DATA_HEALTH_ID = EntityDataManager.createKey(EntityWolf.class, DataSerializers.FLOAT);
   private static final DataParameter<Boolean> BEGGING = EntityDataManager.createKey(EntityWolf.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(EntityWolf.class, DataSerializers.VARINT);
   /** Float used to smooth the rotation of the wolf head */
   private float headRotationCourse;
   private float headRotationCourseOld;
   /** true is the wolf is wet else false */
   private boolean isWet;
   /** True if the wolf is shaking else False */
   private boolean isShaking;
   /** This time increases while wolf is shaking and emitting water particles. */
   private float timeWolfIsShaking;
   private float prevTimeWolfIsShaking;

   public EntityWolf(World worldIn) {
      super(EntityType.WOLF, worldIn);
      this.setSize(0.6F, 0.85F);
      this.setTamed(false);
   }

   protected void initEntityAI() {
      this.aiSit = new EntityAISit(this);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, this.aiSit);
      this.tasks.addTask(3, new EntityWolf.AIAvoidEntity(this, EntityLlama.class, 24.0F, 1.5D, 1.5D));
      this.tasks.addTask(4, new EntityAILeapAtTarget(this, 0.4F));
      this.tasks.addTask(5, new EntityAIAttackMelee(this, 1.0D, true));
      this.tasks.addTask(6, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
      this.tasks.addTask(7, new EntityAIMate(this, 1.0D));
      this.tasks.addTask(8, new EntityAIWanderAvoidWater(this, 1.0D));
      this.tasks.addTask(9, new EntityAIBeg(this, 8.0F));
      this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(10, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
      this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
      this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
      this.targetTasks.addTask(4, new EntityAITargetNonTamed<>(this, EntityAnimal.class, false, (p_210130_0_) -> {
         return p_210130_0_ instanceof EntitySheep || p_210130_0_ instanceof EntityRabbit;
      }));
      this.targetTasks.addTask(4, new EntityAITargetNonTamed<>(this, EntityTurtle.class, false, EntityTurtle.TARGET_DRY_BABY));
      this.targetTasks.addTask(5, new EntityAINearestAttackableTarget<>(this, AbstractSkeleton.class, false));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.3F);
      if (this.isTamed()) {
         this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
      } else {
         this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
      }

      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
   }

   /**
    * Sets the active target the Task system uses for tracking
    */
   public void setAttackTarget(@Nullable EntityLivingBase entitylivingbaseIn) {
      super.setAttackTarget(entitylivingbaseIn);
      if (entitylivingbaseIn == null) {
         this.setAngry(false);
      } else if (!this.isTamed()) {
         this.setAngry(true);
      }

   }

   protected void updateAITasks() {
      this.dataManager.set(DATA_HEALTH_ID, this.getHealth());
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(DATA_HEALTH_ID, this.getHealth());
      this.dataManager.register(BEGGING, false);
      this.dataManager.register(COLLAR_COLOR, EnumDyeColor.RED.getId());
   }

   protected void playStepSound(BlockPos pos, IBlockState blockIn) {
      this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15F, 1.0F);
   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      compound.setBoolean("Angry", this.isAngry());
      compound.setByte("CollarColor", (byte)this.getCollarColor().getId());
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      this.setAngry(compound.getBoolean("Angry"));
      if (compound.contains("CollarColor", 99)) {
         this.setCollarColor(EnumDyeColor.byId(compound.getInt("CollarColor")));
      }

   }

   protected SoundEvent getAmbientSound() {
      if (this.isAngry()) {
         return SoundEvents.ENTITY_WOLF_GROWL;
      } else if (this.rand.nextInt(3) == 0) {
         return this.isTamed() && this.dataManager.get(DATA_HEALTH_ID) < 10.0F ? SoundEvents.ENTITY_WOLF_WHINE : SoundEvents.ENTITY_WOLF_PANT;
      } else {
         return SoundEvents.ENTITY_WOLF_AMBIENT;
      }
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_WOLF_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_WOLF_DEATH;
   }

   /**
    * Returns the volume for the sounds this mob makes.
    */
   protected float getSoundVolume() {
      return 0.4F;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_WOLF;
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      super.livingTick();
      if (!this.world.isRemote && this.isWet && !this.isShaking && !this.hasPath() && this.onGround) {
         this.isShaking = true;
         this.timeWolfIsShaking = 0.0F;
         this.prevTimeWolfIsShaking = 0.0F;
         this.world.setEntityState(this, (byte)8);
      }

      if (!this.world.isRemote && this.getAttackTarget() == null && this.isAngry()) {
         this.setAngry(false);
      }

   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      this.headRotationCourseOld = this.headRotationCourse;
      if (this.isBegging()) {
         this.headRotationCourse += (1.0F - this.headRotationCourse) * 0.4F;
      } else {
         this.headRotationCourse += (0.0F - this.headRotationCourse) * 0.4F;
      }

      if (this.isInWaterRainOrBubbleColumn()) {
         this.isWet = true;
         this.isShaking = false;
         this.timeWolfIsShaking = 0.0F;
         this.prevTimeWolfIsShaking = 0.0F;
      } else if ((this.isWet || this.isShaking) && this.isShaking) {
         if (this.timeWolfIsShaking == 0.0F) {
            this.playSound(SoundEvents.ENTITY_WOLF_SHAKE, this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         }

         this.prevTimeWolfIsShaking = this.timeWolfIsShaking;
         this.timeWolfIsShaking += 0.05F;
         if (this.prevTimeWolfIsShaking >= 2.0F) {
            this.isWet = false;
            this.isShaking = false;
            this.prevTimeWolfIsShaking = 0.0F;
            this.timeWolfIsShaking = 0.0F;
         }

         if (this.timeWolfIsShaking > 0.4F) {
            float f = (float)this.getBoundingBox().minY;
            int i = (int)(MathHelper.sin((this.timeWolfIsShaking - 0.4F) * (float)Math.PI) * 7.0F);

            for(int j = 0; j < i; ++j) {
               float f1 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
               float f2 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
               this.world.spawnParticle(Particles.SPLASH, this.posX + (double)f1, (double)(f + 0.8F), this.posZ + (double)f2, this.motionX, this.motionY, this.motionZ);
            }
         }
      }

   }

   /**
    * True if the wolf is wet
    */
   @OnlyIn(Dist.CLIENT)
   public boolean isWolfWet() {
      return this.isWet;
   }

   /**
    * Used when calculating the amount of shading to apply while the wolf is wet.
    */
   @OnlyIn(Dist.CLIENT)
   public float getShadingWhileWet(float p_70915_1_) {
      return 0.75F + (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * p_70915_1_) / 2.0F * 0.25F;
   }

   @OnlyIn(Dist.CLIENT)
   public float getShakeAngle(float p_70923_1_, float p_70923_2_) {
      float f = (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * p_70923_1_ + p_70923_2_) / 1.8F;
      if (f < 0.0F) {
         f = 0.0F;
      } else if (f > 1.0F) {
         f = 1.0F;
      }

      return MathHelper.sin(f * (float)Math.PI) * MathHelper.sin(f * (float)Math.PI * 11.0F) * 0.15F * (float)Math.PI;
   }

   @OnlyIn(Dist.CLIENT)
   public float getInterestedAngle(float p_70917_1_) {
      return (this.headRotationCourseOld + (this.headRotationCourse - this.headRotationCourseOld) * p_70917_1_) * 0.15F * (float)Math.PI;
   }

   public float getEyeHeight() {
      return this.height * 0.8F;
   }

   /**
    * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
    * use in wolves.
    */
   public int getVerticalFaceSpeed() {
      return this.isSitting() ? 20 : super.getVerticalFaceSpeed();
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else {
         Entity entity = source.getTrueSource();
         if (this.aiSit != null) {
            this.aiSit.setSitting(false);
         }

         if (entity != null && !(entity instanceof EntityPlayer) && !(entity instanceof EntityArrow)) {
            amount = (amount + 1.0F) / 2.0F;
         }

         return super.attackEntityFrom(source, amount);
      }
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue()));
      if (flag) {
         this.applyEnchantments(this, entityIn);
      }

      return flag;
   }

   public void setTamed(boolean tamed) {
      super.setTamed(tamed);
      if (tamed) {
         this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
      } else {
         this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
      }

      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
   }

   public boolean processInteract(EntityPlayer player, EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      Item item = itemstack.getItem();
      if (this.isTamed()) {
         if (!itemstack.isEmpty()) {
            if (item instanceof ItemFood) {
               ItemFood itemfood = (ItemFood)item;
               if (itemfood.isMeat() && this.dataManager.get(DATA_HEALTH_ID) < 20.0F) {
                  if (!player.abilities.isCreativeMode) {
                     itemstack.shrink(1);
                  }

                  this.heal((float)itemfood.getHealAmount(itemstack));
                  return true;
               }
            } else if (item instanceof ItemDye) {
               EnumDyeColor enumdyecolor = ((ItemDye)item).getDyeColor();
               if (enumdyecolor != this.getCollarColor()) {
                  this.setCollarColor(enumdyecolor);
                  if (!player.abilities.isCreativeMode) {
                     itemstack.shrink(1);
                  }

                  return true;
               }
            }
         }

         if (this.isOwner(player) && !this.world.isRemote && !this.isBreedingItem(itemstack)) {
            this.aiSit.setSitting(!this.isSitting());
            this.isJumping = false;
            this.navigator.clearPath();
            this.setAttackTarget((EntityLivingBase)null);
         }
      } else if (item == Items.BONE && !this.isAngry()) {
         if (!player.abilities.isCreativeMode) {
            itemstack.shrink(1);
         }

         if (!this.world.isRemote) {
            if (this.rand.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
               this.setTamedBy(player);
               this.navigator.clearPath();
               this.setAttackTarget((EntityLivingBase)null);
               this.aiSit.setSitting(true);
               this.setHealth(20.0F);
               this.playTameEffect(true);
               this.world.setEntityState(this, (byte)7);
            } else {
               this.playTameEffect(false);
               this.world.setEntityState(this, (byte)6);
            }
         }

         return true;
      }

      return super.processInteract(player, hand);
   }

   /**
    * Handler for {@link World#setEntityState}
    */
   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 8) {
         this.isShaking = true;
         this.timeWolfIsShaking = 0.0F;
         this.prevTimeWolfIsShaking = 0.0F;
      } else {
         super.handleStatusUpdate(id);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float getTailRotation() {
      if (this.isAngry()) {
         return 1.5393804F;
      } else {
         return this.isTamed() ? (0.55F - (this.getMaxHealth() - this.dataManager.get(DATA_HEALTH_ID)) * 0.02F) * (float)Math.PI : ((float)Math.PI / 5F);
      }
   }

   /**
    * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
    * the animal type)
    */
   public boolean isBreedingItem(ItemStack stack) {
      Item item = stack.getItem();
      return item instanceof ItemFood && ((ItemFood)item).isMeat();
   }

   /**
    * Will return how many at most can spawn in a chunk at once.
    */
   public int getMaxSpawnedInChunk() {
      return 8;
   }

   /**
    * Determines whether this wolf is angry or not.
    */
   public boolean isAngry() {
      return (this.dataManager.get(TAMED) & 2) != 0;
   }

   /**
    * Sets whether this wolf is angry or not.
    */
   public void setAngry(boolean angry) {
      byte b0 = this.dataManager.get(TAMED);
      if (angry) {
         this.dataManager.set(TAMED, (byte)(b0 | 2));
      } else {
         this.dataManager.set(TAMED, (byte)(b0 & -3));
      }

   }

   public EnumDyeColor getCollarColor() {
      return EnumDyeColor.byId(this.dataManager.get(COLLAR_COLOR));
   }

   public void setCollarColor(EnumDyeColor collarcolor) {
      this.dataManager.set(COLLAR_COLOR, collarcolor.getId());
   }

   public EntityWolf createChild(EntityAgeable ageable) {
      EntityWolf entitywolf = new EntityWolf(this.world);
      UUID uuid = this.getOwnerId();
      if (uuid != null) {
         entitywolf.setOwnerId(uuid);
         entitywolf.setTamed(true);
      }

      return entitywolf;
   }

   public void setBegging(boolean beg) {
      this.dataManager.set(BEGGING, beg);
   }

   /**
    * Returns true if the mob is currently able to mate with the specified mob.
    */
   public boolean canMateWith(EntityAnimal otherAnimal) {
      if (otherAnimal == this) {
         return false;
      } else if (!this.isTamed()) {
         return false;
      } else if (!(otherAnimal instanceof EntityWolf)) {
         return false;
      } else {
         EntityWolf entitywolf = (EntityWolf)otherAnimal;
         if (!entitywolf.isTamed()) {
            return false;
         } else if (entitywolf.isSitting()) {
            return false;
         } else {
            return this.isInLove() && entitywolf.isInLove();
         }
      }
   }

   public boolean isBegging() {
      return this.dataManager.get(BEGGING);
   }

   public boolean shouldAttackEntity(EntityLivingBase target, EntityLivingBase owner) {
      if (!(target instanceof EntityCreeper) && !(target instanceof EntityGhast)) {
         if (target instanceof EntityWolf) {
            EntityWolf entitywolf = (EntityWolf)target;
            if (entitywolf.isTamed() && entitywolf.getOwner() == owner) {
               return false;
            }
         }

         if (target instanceof EntityPlayer && owner instanceof EntityPlayer && !((EntityPlayer)owner).canAttackPlayer((EntityPlayer)target)) {
            return false;
         } else {
            return !(target instanceof AbstractHorse) || !((AbstractHorse)target).isTame();
         }
      } else {
         return false;
      }
   }

   public boolean canBeLeashedTo(EntityPlayer player) {
      return !this.isAngry() && super.canBeLeashedTo(player);
   }

   class AIAvoidEntity<T extends Entity> extends EntityAIAvoidEntity<T> {
      private final EntityWolf wolf;

      public AIAvoidEntity(EntityWolf wolfIn, Class<T> p_i47251_3_, float p_i47251_4_, double p_i47251_5_, double p_i47251_7_) {
         super(wolfIn, p_i47251_3_, p_i47251_4_, p_i47251_5_, p_i47251_7_);
         this.wolf = wolfIn;
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         if (super.shouldExecute() && this.closestLivingEntity instanceof EntityLlama) {
            return !this.wolf.isTamed() && this.avoidLlama((EntityLlama)this.closestLivingEntity);
         } else {
            return false;
         }
      }

      private boolean avoidLlama(EntityLlama p_190854_1_) {
         return p_190854_1_.getStrength() >= EntityWolf.this.rand.nextInt(5);
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         EntityWolf.this.setAttackTarget((EntityLivingBase)null);
         super.startExecuting();
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         EntityWolf.this.setAttackTarget((EntityLivingBase)null);
         super.tick();
      }
   }
}