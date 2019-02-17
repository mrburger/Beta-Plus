package net.minecraft.entity.monster;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityEvoker extends EntitySpellcasterIllager {
   private EntitySheep wololoTarget;

   public EntityEvoker(World worldIn) {
      super(EntityType.EVOKER, worldIn);
      this.setSize(0.6F, 1.95F);
      this.experienceValue = 10;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityEvoker.AICastingSpell());
      this.tasks.addTask(2, new EntityAIAvoidEntity<>(this, EntityPlayer.class, 8.0F, 0.6D, 1.0D));
      this.tasks.addTask(4, new EntityEvoker.AISummonSpell());
      this.tasks.addTask(5, new EntityEvoker.AIAttackSpell());
      this.tasks.addTask(6, new EntityEvoker.AIWololoSpell());
      this.tasks.addTask(8, new EntityAIWander(this, 0.6D));
      this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
      this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, EntityEvoker.class));
      this.targetTasks.addTask(2, (new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true)).setUnseenMemoryTicks(300));
      this.targetTasks.addTask(3, (new EntityAINearestAttackableTarget<>(this, EntityVillager.class, false)).setUnseenMemoryTicks(300));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityIronGolem.class, false));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(12.0D);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(24.0D);
   }

   protected void registerData() {
      super.registerData();
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
   }

   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_EVOCATION_ILLAGER;
   }

   protected void updateAITasks() {
      super.updateAITasks();
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
   }

   /**
    * Returns whether this Entity is on the same team as the given Entity.
    */
   public boolean isOnSameTeam(Entity entityIn) {
      if (entityIn == null) {
         return false;
      } else if (entityIn == this) {
         return true;
      } else if (super.isOnSameTeam(entityIn)) {
         return true;
      } else if (entityIn instanceof EntityVex) {
         return this.isOnSameTeam(((EntityVex)entityIn).getOwner());
      } else if (entityIn instanceof EntityLivingBase && ((EntityLivingBase)entityIn).getCreatureAttribute() == CreatureAttribute.ILLAGER) {
         return this.getTeam() == null && entityIn.getTeam() == null;
      } else {
         return false;
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_EVOKER_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_EVOKER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_EVOKER_HURT;
   }

   private void setWololoTarget(@Nullable EntitySheep wololoTargetIn) {
      this.wololoTarget = wololoTargetIn;
   }

   @Nullable
   private EntitySheep getWololoTarget() {
      return this.wololoTarget;
   }

   protected SoundEvent getSpellSound() {
      return SoundEvents.ENTITY_EVOKER_CAST_SPELL;
   }

   class AIAttackSpell extends EntitySpellcasterIllager.AIUseSpell {
      private AIAttackSpell() {
         super();
      }

      protected int getCastingTime() {
         return 40;
      }

      protected int getCastingInterval() {
         return 100;
      }

      protected void castSpell() {
         EntityLivingBase entitylivingbase = EntityEvoker.this.getAttackTarget();
         double d0 = Math.min(entitylivingbase.posY, EntityEvoker.this.posY);
         double d1 = Math.max(entitylivingbase.posY, EntityEvoker.this.posY) + 1.0D;
         float f = (float)MathHelper.atan2(entitylivingbase.posZ - EntityEvoker.this.posZ, entitylivingbase.posX - EntityEvoker.this.posX);
         if (EntityEvoker.this.getDistanceSq(entitylivingbase) < 9.0D) {
            for(int i = 0; i < 5; ++i) {
               float f1 = f + (float)i * (float)Math.PI * 0.4F;
               this.spawnFangs(EntityEvoker.this.posX + (double)MathHelper.cos(f1) * 1.5D, EntityEvoker.this.posZ + (double)MathHelper.sin(f1) * 1.5D, d0, d1, f1, 0);
            }

            for(int k = 0; k < 8; ++k) {
               float f2 = f + (float)k * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
               this.spawnFangs(EntityEvoker.this.posX + (double)MathHelper.cos(f2) * 2.5D, EntityEvoker.this.posZ + (double)MathHelper.sin(f2) * 2.5D, d0, d1, f2, 3);
            }
         } else {
            for(int l = 0; l < 16; ++l) {
               double d2 = 1.25D * (double)(l + 1);
               int j = 1 * l;
               this.spawnFangs(EntityEvoker.this.posX + (double)MathHelper.cos(f) * d2, EntityEvoker.this.posZ + (double)MathHelper.sin(f) * d2, d0, d1, f, j);
            }
         }

      }

      private void spawnFangs(double p_190876_1_, double p_190876_3_, double p_190876_5_, double p_190876_7_, float p_190876_9_, int p_190876_10_) {
         BlockPos blockpos = new BlockPos(p_190876_1_, p_190876_7_, p_190876_3_);
         boolean flag = false;
         double d0 = 0.0D;

         while(true) {
            if (!EntityEvoker.this.world.isTopSolid(blockpos) && EntityEvoker.this.world.isTopSolid(blockpos.down())) {
               if (!EntityEvoker.this.world.isAirBlock(blockpos)) {
                  IBlockState iblockstate = EntityEvoker.this.world.getBlockState(blockpos);
                  VoxelShape voxelshape = iblockstate.getCollisionShape(EntityEvoker.this.world, blockpos);
                  if (!voxelshape.isEmpty()) {
                     d0 = voxelshape.getEnd(EnumFacing.Axis.Y);
                  }
               }

               flag = true;
               break;
            }

            blockpos = blockpos.down();
            if (blockpos.getY() < MathHelper.floor(p_190876_5_) - 1) {
               break;
            }
         }

         if (flag) {
            EntityEvokerFangs entityevokerfangs = new EntityEvokerFangs(EntityEvoker.this.world, p_190876_1_, (double)blockpos.getY() + d0, p_190876_3_, p_190876_9_, p_190876_10_, EntityEvoker.this);
            EntityEvoker.this.world.spawnEntity(entityevokerfangs);
         }

      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK;
      }

      protected EntitySpellcasterIllager.SpellType getSpellType() {
         return EntitySpellcasterIllager.SpellType.FANGS;
      }
   }

   class AICastingSpell extends EntitySpellcasterIllager.AICastingApell {
      private AICastingSpell() {
         super();
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         if (EntityEvoker.this.getAttackTarget() != null) {
            EntityEvoker.this.getLookHelper().setLookPositionWithEntity(EntityEvoker.this.getAttackTarget(), (float)EntityEvoker.this.getHorizontalFaceSpeed(), (float)EntityEvoker.this.getVerticalFaceSpeed());
         } else if (EntityEvoker.this.getWololoTarget() != null) {
            EntityEvoker.this.getLookHelper().setLookPositionWithEntity(EntityEvoker.this.getWololoTarget(), (float)EntityEvoker.this.getHorizontalFaceSpeed(), (float)EntityEvoker.this.getVerticalFaceSpeed());
         }

      }
   }

   class AISummonSpell extends EntitySpellcasterIllager.AIUseSpell {
      private AISummonSpell() {
         super();
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         if (!super.shouldExecute()) {
            return false;
         } else {
            int i = EntityEvoker.this.world.getEntitiesWithinAABB(EntityVex.class, EntityEvoker.this.getBoundingBox().grow(16.0D)).size();
            return EntityEvoker.this.rand.nextInt(8) + 1 > i;
         }
      }

      protected int getCastingTime() {
         return 100;
      }

      protected int getCastingInterval() {
         return 340;
      }

      protected void castSpell() {
         for(int i = 0; i < 3; ++i) {
            BlockPos blockpos = (new BlockPos(EntityEvoker.this)).add(-2 + EntityEvoker.this.rand.nextInt(5), 1, -2 + EntityEvoker.this.rand.nextInt(5));
            EntityVex entityvex = new EntityVex(EntityEvoker.this.world);
            entityvex.moveToBlockPosAndAngles(blockpos, 0.0F, 0.0F);
            entityvex.onInitialSpawn(EntityEvoker.this.world.getDifficultyForLocation(blockpos), (IEntityLivingData)null, (NBTTagCompound)null);
            entityvex.setOwner(EntityEvoker.this);
            entityvex.setBoundOrigin(blockpos);
            entityvex.setLimitedLife(20 * (30 + EntityEvoker.this.rand.nextInt(90)));
            EntityEvoker.this.world.spawnEntity(entityvex);
         }

      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON;
      }

      protected EntitySpellcasterIllager.SpellType getSpellType() {
         return EntitySpellcasterIllager.SpellType.SUMMON_VEX;
      }
   }

   public class AIWololoSpell extends EntitySpellcasterIllager.AIUseSpell {
      private final Predicate<EntitySheep> wololoSelector = (p_200827_0_) -> {
         return p_200827_0_.getFleeceColor() == EnumDyeColor.BLUE;
      };

      public AIWololoSpell() {
         super();
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         if (EntityEvoker.this.getAttackTarget() != null) {
            return false;
         } else if (EntityEvoker.this.isSpellcasting()) {
            return false;
         } else if (EntityEvoker.this.ticksExisted < this.spellCooldown) {
            return false;
         } else if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(EntityEvoker.this.world, EntityEvoker.this)) {
            return false;
         } else {
            List<EntitySheep> list = EntityEvoker.this.world.getEntitiesWithinAABB(EntitySheep.class, EntityEvoker.this.getBoundingBox().grow(16.0D, 4.0D, 16.0D), this.wololoSelector);
            if (list.isEmpty()) {
               return false;
            } else {
               EntityEvoker.this.setWololoTarget(list.get(EntityEvoker.this.rand.nextInt(list.size())));
               return true;
            }
         }
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return EntityEvoker.this.getWololoTarget() != null && this.spellWarmup > 0;
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         super.resetTask();
         EntityEvoker.this.setWololoTarget((EntitySheep)null);
      }

      protected void castSpell() {
         EntitySheep entitysheep = EntityEvoker.this.getWololoTarget();
         if (entitysheep != null && entitysheep.isAlive()) {
            entitysheep.setFleeceColor(EnumDyeColor.RED);
         }

      }

      protected int getCastWarmupTime() {
         return 40;
      }

      protected int getCastingTime() {
         return 60;
      }

      protected int getCastingInterval() {
         return 140;
      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO;
      }

      protected EntitySpellcasterIllager.SpellType getSpellType() {
         return EntitySpellcasterIllager.SpellType.WOLOLO;
      }
   }
}