package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackRangedBow;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityIllusionIllager extends EntitySpellcasterIllager implements IRangedAttackMob {
   private int ghostTime;
   private final Vec3d[][] renderLocations;

   public EntityIllusionIllager(World worldIn) {
      super(EntityType.ILLUSIONER, worldIn);
      this.setSize(0.6F, 1.95F);
      this.experienceValue = 5;
      this.renderLocations = new Vec3d[2][4];

      for(int i = 0; i < 4; ++i) {
         this.renderLocations[0][i] = new Vec3d(0.0D, 0.0D, 0.0D);
         this.renderLocations[1][i] = new Vec3d(0.0D, 0.0D, 0.0D);
      }

   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntitySpellcasterIllager.AICastingApell());
      this.tasks.addTask(4, new EntityIllusionIllager.AIMirriorSpell());
      this.tasks.addTask(5, new EntityIllusionIllager.AIBlindnessSpell());
      this.tasks.addTask(6, new EntityAIAttackRangedBow<>(this, 0.5D, 20, 15.0F));
      this.tasks.addTask(8, new EntityAIWander(this, 0.6D));
      this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
      this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, EntityIllusionIllager.class));
      this.targetTasks.addTask(2, (new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true)).setUnseenMemoryTicks(300));
      this.targetTasks.addTask(3, (new EntityAINearestAttackableTarget<>(this, EntityVillager.class, false)).setUnseenMemoryTicks(300));
      this.targetTasks.addTask(3, (new EntityAINearestAttackableTarget<>(this, EntityIronGolem.class, false)).setUnseenMemoryTicks(300));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(18.0D);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(32.0D);
   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData entityLivingData, @Nullable NBTTagCompound itemNbt) {
      this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
      return super.onInitialSpawn(difficulty, entityLivingData, itemNbt);
   }

   protected void registerData() {
      super.registerData();
   }

   protected ResourceLocation getLootTable() {
      return LootTableList.EMPTY;
   }

   /**
    * Gets the bounding box of this Entity, adjusted to take auxiliary entities into account (e.g. the tile contained by
    * a minecart, such as a command block).
    */
   @OnlyIn(Dist.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      return this.getBoundingBox().grow(3.0D, 0.0D, 3.0D);
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      super.livingTick();
      if (this.world.isRemote && this.isInvisible()) {
         --this.ghostTime;
         if (this.ghostTime < 0) {
            this.ghostTime = 0;
         }

         if (this.hurtTime != 1 && this.ticksExisted % 1200 != 0) {
            if (this.hurtTime == this.maxHurtTime - 1) {
               this.ghostTime = 3;

               for(int k = 0; k < 4; ++k) {
                  this.renderLocations[0][k] = this.renderLocations[1][k];
                  this.renderLocations[1][k] = new Vec3d(0.0D, 0.0D, 0.0D);
               }
            }
         } else {
            this.ghostTime = 3;
            float f = -6.0F;
            int i = 13;

            for(int j = 0; j < 4; ++j) {
               this.renderLocations[0][j] = this.renderLocations[1][j];
               this.renderLocations[1][j] = new Vec3d((double)(-6.0F + (float)this.rand.nextInt(13)) * 0.5D, (double)Math.max(0, this.rand.nextInt(6) - 4), (double)(-6.0F + (float)this.rand.nextInt(13)) * 0.5D);
            }

            for(int l = 0; l < 16; ++l) {
               this.world.spawnParticle(Particles.CLOUD, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
            }

            this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, this.getSoundCategory(), 1.0F, 1.0F, false);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public Vec3d[] getRenderLocations(float p_193098_1_) {
      if (this.ghostTime <= 0) {
         return this.renderLocations[1];
      } else {
         double d0 = (double)(((float)this.ghostTime - p_193098_1_) / 3.0F);
         d0 = Math.pow(d0, 0.25D);
         Vec3d[] avec3d = new Vec3d[4];

         for(int i = 0; i < 4; ++i) {
            avec3d[i] = this.renderLocations[1][i].scale(1.0D - d0).add(this.renderLocations[0][i].scale(d0));
         }

         return avec3d;
      }
   }

   /**
    * Returns whether this Entity is on the same team as the given Entity.
    */
   public boolean isOnSameTeam(Entity entityIn) {
      if (super.isOnSameTeam(entityIn)) {
         return true;
      } else if (entityIn instanceof EntityLivingBase && ((EntityLivingBase)entityIn).getCreatureAttribute() == CreatureAttribute.ILLAGER) {
         return this.getTeam() == null && entityIn.getTeam() == null;
      } else {
         return false;
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_ILLUSIONER_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ILLUSIONER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_ILLUSIONER_HURT;
   }

   protected SoundEvent getSpellSound() {
      return SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL;
   }

   /**
    * Attack the specified entity using a ranged attack.
    */
   public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
      EntityArrow entityarrow = this.createArrowEntity(distanceFactor);
      double d0 = target.posX - this.posX;
      double d1 = target.getBoundingBox().minY + (double)(target.height / 3.0F) - entityarrow.posY;
      double d2 = target.posZ - this.posZ;
      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      entityarrow.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.world.getDifficulty().getId() * 4));
      this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
      this.world.spawnEntity(entityarrow);
   }

   protected EntityArrow createArrowEntity(float p_193097_1_) {
      EntityTippedArrow entitytippedarrow = new EntityTippedArrow(this.world, this);
      entitytippedarrow.setEnchantmentEffectsFromEntity(this, p_193097_1_);
      return entitytippedarrow;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isAggressive() {
      return this.isAggressive(1);
   }

   public void setSwingingArms(boolean swingingArms) {
      this.setAggressive(1, swingingArms);
   }

   @OnlyIn(Dist.CLIENT)
   public AbstractIllager.IllagerArmPose getArmPose() {
      if (this.isSpellcasting()) {
         return AbstractIllager.IllagerArmPose.SPELLCASTING;
      } else {
         return this.isAggressive() ? AbstractIllager.IllagerArmPose.BOW_AND_ARROW : AbstractIllager.IllagerArmPose.CROSSED;
      }
   }

   class AIBlindnessSpell extends EntitySpellcasterIllager.AIUseSpell {
      private int lastTargetId;

      private AIBlindnessSpell() {
         super();
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         if (!super.shouldExecute()) {
            return false;
         } else if (EntityIllusionIllager.this.getAttackTarget() == null) {
            return false;
         } else if (EntityIllusionIllager.this.getAttackTarget().getEntityId() == this.lastTargetId) {
            return false;
         } else {
            return EntityIllusionIllager.this.world.getDifficultyForLocation(new BlockPos(EntityIllusionIllager.this)).isHarderThan((float)EnumDifficulty.NORMAL.ordinal());
         }
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         super.startExecuting();
         this.lastTargetId = EntityIllusionIllager.this.getAttackTarget().getEntityId();
      }

      protected int getCastingTime() {
         return 20;
      }

      protected int getCastingInterval() {
         return 180;
      }

      protected void castSpell() {
         EntityIllusionIllager.this.getAttackTarget().addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 400));
      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ENTITY_ILLUSIONER_PREPARE_BLINDNESS;
      }

      protected EntitySpellcasterIllager.SpellType getSpellType() {
         return EntitySpellcasterIllager.SpellType.BLINDNESS;
      }
   }

   class AIMirriorSpell extends EntitySpellcasterIllager.AIUseSpell {
      private AIMirriorSpell() {
         super();
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         if (!super.shouldExecute()) {
            return false;
         } else {
            return !EntityIllusionIllager.this.isPotionActive(MobEffects.INVISIBILITY);
         }
      }

      protected int getCastingTime() {
         return 20;
      }

      protected int getCastingInterval() {
         return 340;
      }

      protected void castSpell() {
         EntityIllusionIllager.this.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 1200));
      }

      @Nullable
      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ENTITY_ILLUSIONER_PREPARE_MIRROR;
      }

      protected EntitySpellcasterIllager.SpellType getSpellType() {
         return EntitySpellcasterIllager.SpellType.DISAPPEAR;
      }
   }
}