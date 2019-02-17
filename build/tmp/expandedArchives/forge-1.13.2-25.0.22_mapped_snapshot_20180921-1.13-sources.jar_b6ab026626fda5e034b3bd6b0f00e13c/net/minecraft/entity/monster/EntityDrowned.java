package net.minecraft.entity.monster;

import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIZombieAttack;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.passive.EntityTurtle;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityTrident;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityDrowned extends EntityZombie implements IRangedAttackMob {
   private boolean field_204718_bx;
   protected final PathNavigateSwimmer waterNavigator;
   protected final PathNavigateGround groundNavigator;

   public EntityDrowned(World p_i48903_1_) {
      super(EntityType.DROWNED, p_i48903_1_);
      this.stepHeight = 1.0F;
      this.moveHelper = new EntityDrowned.MoveHelper(this);
      this.setPathPriority(PathNodeType.WATER, 0.0F);
      this.waterNavigator = new PathNavigateSwimmer(this, p_i48903_1_);
      this.groundNavigator = new PathNavigateGround(this, p_i48903_1_);
   }

   protected void applyEntityAI() {
      this.tasks.addTask(1, new EntityDrowned.AIGoToWater(this, 1.0D));
      this.tasks.addTask(2, new EntityDrowned.AITridentAttack(this, 1.0D, 40, 10.0F));
      this.tasks.addTask(2, new EntityDrowned.AIAttack(this, 1.0D, false));
      this.tasks.addTask(5, new EntityDrowned.AIGoToBeach(this, 1.0D));
      this.tasks.addTask(6, new EntityDrowned.AISwimUp(this, 1.0D, this.world.getSeaLevel()));
      this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, EntityDrowned.class));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 10, true, false, new EntityDrowned.AttackTargetPredicate(this)));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityVillager.class, false));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityIronGolem.class, true));
      this.targetTasks.addTask(5, new EntityAINearestAttackableTarget<>(this, EntityTurtle.class, 10, true, false, EntityTurtle.TARGET_DRY_BABY));
   }

   /**
    * Returns new PathNavigateGround instance
    */
   protected PathNavigate createNavigator(World worldIn) {
      return super.createNavigator(worldIn);
   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData entityLivingData, @Nullable NBTTagCompound itemNbt) {
      entityLivingData = super.onInitialSpawn(difficulty, entityLivingData, itemNbt);
      if (this.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).isEmpty() && this.rand.nextFloat() < 0.03F) {
         this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(Items.NAUTILUS_SHELL));
         this.inventoryHandsDropChances[EntityEquipmentSlot.OFFHAND.getIndex()] = 2.0F;
      }

      return entityLivingData;
   }

   public boolean canSpawn(IWorld worldIn, boolean p_205020_2_) {
      Biome biome = worldIn.getBiome(new BlockPos(this.posX, this.posY, this.posZ));
      if (biome != Biomes.RIVER && biome != Biomes.FROZEN_RIVER) {
         return this.rand.nextInt(40) == 0 && this.func_204712_dC() && super.canSpawn(worldIn, p_205020_2_);
      } else {
         return this.rand.nextInt(15) == 0 && super.canSpawn(worldIn, p_205020_2_);
      }
   }

   private boolean func_204712_dC() {
      return this.getBoundingBox().minY < (double)(this.world.getSeaLevel() - 5);
   }

   protected boolean canBreakDoors() {
      return false;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_DROWNED;
   }

   protected SoundEvent getAmbientSound() {
      return this.isInWater() ? SoundEvents.ENTITY_DROWNED_AMBIENT_WATER : SoundEvents.ENTITY_DROWNED_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.isInWater() ? SoundEvents.ENTITY_DROWNED_HURT_WATER : SoundEvents.ENTITY_DROWNED_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isInWater() ? SoundEvents.ENTITY_DROWNED_DEATH_WATER : SoundEvents.ENTITY_DROWNED_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_DROWNED_STEP;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.ENTITY_DROWNED_SWIM;
   }

   protected ItemStack getSkullDrop() {
      return ItemStack.EMPTY;
   }

   /**
    * Gives armor or weapon for entity based on given DifficultyInstance
    */
   protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
      if ((double)this.rand.nextFloat() > 0.9D) {
         int i = this.rand.nextInt(16);
         if (i < 10) {
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
         } else {
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
         }
      }

   }

   protected boolean shouldExchangeEquipment(ItemStack candidate, ItemStack existing, EntityEquipmentSlot p_208003_3_) {
      if (existing.getItem() == Items.NAUTILUS_SHELL) {
         return false;
      } else if (existing.getItem() == Items.TRIDENT) {
         if (candidate.getItem() == Items.TRIDENT) {
            return candidate.getDamage() < existing.getDamage();
         } else {
            return false;
         }
      } else {
         return candidate.getItem() == Items.TRIDENT ? true : super.shouldExchangeEquipment(candidate, existing, p_208003_3_);
      }
   }

   protected boolean shouldDrown() {
      return false;
   }

   public boolean isNotColliding(IWorldReaderBase worldIn) {
      return worldIn.checkNoEntityCollision(this, this.getBoundingBox()) && worldIn.isCollisionBoxesEmpty(this, this.getBoundingBox());
   }

   public boolean shouldAttack(@Nullable EntityLivingBase p_204714_1_) {
      if (p_204714_1_ != null) {
         return !this.world.isDaytime() || p_204714_1_.isInWater();
      } else {
         return false;
      }
   }

   public boolean isPushedByWater() {
      return !this.isSwimming();
   }

   private boolean func_204715_dF() {
      if (this.field_204718_bx) {
         return true;
      } else {
         EntityLivingBase entitylivingbase = this.getAttackTarget();
         return entitylivingbase != null && entitylivingbase.isInWater();
      }
   }

   public void travel(float strafe, float vertical, float forward) {
      if (this.isServerWorld() && this.isInWater() && this.func_204715_dF()) {
         this.moveRelative(strafe, vertical, forward, 0.01F);
         this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
         this.motionX *= (double)0.9F;
         this.motionY *= (double)0.9F;
         this.motionZ *= (double)0.9F;
      } else {
         super.travel(strafe, vertical, forward);
      }

   }

   public void updateSwimming() {
      if (!this.world.isRemote) {
         if (this.isServerWorld() && this.isInWater() && this.func_204715_dF()) {
            this.navigator = this.waterNavigator;
            this.setSwimming(true);
         } else {
            this.navigator = this.groundNavigator;
            this.setSwimming(false);
         }
      }

   }

   protected boolean isCloseToPathTarget() {
      Path path = this.getNavigator().getPath();
      if (path != null) {
         PathPoint pathpoint = path.getTarget();
         if (pathpoint != null) {
            double d0 = this.getDistanceSq((double)pathpoint.x, (double)pathpoint.y, (double)pathpoint.z);
            if (d0 < 4.0D) {
               return true;
            }
         }
      }

      return false;
   }

   /**
    * Attack the specified entity using a ranged attack.
    */
   public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
      EntityTrident entitytrident = new EntityTrident(this.world, this, new ItemStack(Items.TRIDENT));
      double d0 = target.posX - this.posX;
      double d1 = target.getBoundingBox().minY + (double)(target.height / 3.0F) - entitytrident.posY;
      double d2 = target.posZ - this.posZ;
      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      entitytrident.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.world.getDifficulty().getId() * 4));
      this.playSound(SoundEvents.ENTITY_DROWNED_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
      this.world.spawnEntity(entitytrident);
   }

   public void func_204713_s(boolean p_204713_1_) {
      this.field_204718_bx = p_204713_1_;
   }

   static class AIAttack extends EntityAIZombieAttack {
      private final EntityDrowned field_204726_g;

      public AIAttack(EntityDrowned p_i48913_1_, double p_i48913_2_, boolean p_i48913_4_) {
         super(p_i48913_1_, p_i48913_2_, p_i48913_4_);
         this.field_204726_g = p_i48913_1_;
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         return super.shouldExecute() && this.field_204726_g.shouldAttack(this.field_204726_g.getAttackTarget());
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return super.shouldContinueExecuting() && this.field_204726_g.shouldAttack(this.field_204726_g.getAttackTarget());
      }
   }

   static class AIGoToBeach extends EntityAIMoveToBlock {
      private final EntityDrowned drowned;

      public AIGoToBeach(EntityDrowned p_i48911_1_, double p_i48911_2_) {
         super(p_i48911_1_, p_i48911_2_, 8, 2);
         this.drowned = p_i48911_1_;
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         return super.shouldExecute() && !this.drowned.world.isDaytime() && this.drowned.isInWater() && this.drowned.posY >= (double)(this.drowned.world.getSeaLevel() - 3);
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return super.shouldContinueExecuting();
      }

      /**
       * Return true to set given position as destination
       */
      protected boolean shouldMoveTo(IWorldReaderBase worldIn, BlockPos pos) {
         BlockPos blockpos = pos.up();
         return worldIn.isAirBlock(blockpos) && worldIn.isAirBlock(blockpos.up()) ? worldIn.getBlockState(pos).isTopSolid() : false;
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.drowned.func_204713_s(false);
         this.drowned.navigator = this.drowned.groundNavigator;
         super.startExecuting();
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         super.resetTask();
      }
   }

   static class AIGoToWater extends EntityAIBase {
      private final EntityCreature field_204730_a;
      private double field_204731_b;
      private double field_204732_c;
      private double field_204733_d;
      private final double field_204734_e;
      private final World field_204735_f;

      public AIGoToWater(EntityCreature p_i48910_1_, double p_i48910_2_) {
         this.field_204730_a = p_i48910_1_;
         this.field_204734_e = p_i48910_2_;
         this.field_204735_f = p_i48910_1_.world;
         this.setMutexBits(1);
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         if (!this.field_204735_f.isDaytime()) {
            return false;
         } else if (this.field_204730_a.isInWater()) {
            return false;
         } else {
            Vec3d vec3d = this.func_204729_f();
            if (vec3d == null) {
               return false;
            } else {
               this.field_204731_b = vec3d.x;
               this.field_204732_c = vec3d.y;
               this.field_204733_d = vec3d.z;
               return true;
            }
         }
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return !this.field_204730_a.getNavigator().noPath();
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.field_204730_a.getNavigator().tryMoveToXYZ(this.field_204731_b, this.field_204732_c, this.field_204733_d, this.field_204734_e);
      }

      @Nullable
      private Vec3d func_204729_f() {
         Random random = this.field_204730_a.getRNG();
         BlockPos blockpos = new BlockPos(this.field_204730_a.posX, this.field_204730_a.getBoundingBox().minY, this.field_204730_a.posZ);

         for(int i = 0; i < 10; ++i) {
            BlockPos blockpos1 = blockpos.add(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);
            if (this.field_204735_f.getBlockState(blockpos1).getBlock() == Blocks.WATER) {
               return new Vec3d((double)blockpos1.getX(), (double)blockpos1.getY(), (double)blockpos1.getZ());
            }
         }

         return null;
      }
   }

   static class AISwimUp extends EntityAIBase {
      private final EntityDrowned field_204736_a;
      private final double field_204737_b;
      private final int targetY;
      private boolean obstructed;

      public AISwimUp(EntityDrowned p_i48908_1_, double p_i48908_2_, int p_i48908_4_) {
         this.field_204736_a = p_i48908_1_;
         this.field_204737_b = p_i48908_2_;
         this.targetY = p_i48908_4_;
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         return !this.field_204736_a.world.isDaytime() && this.field_204736_a.isInWater() && this.field_204736_a.posY < (double)(this.targetY - 2);
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return this.shouldExecute() && !this.obstructed;
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         if (this.field_204736_a.posY < (double)(this.targetY - 1) && (this.field_204736_a.getNavigator().noPath() || this.field_204736_a.isCloseToPathTarget())) {
            Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.field_204736_a, 4, 8, new Vec3d(this.field_204736_a.posX, (double)(this.targetY - 1), this.field_204736_a.posZ));
            if (vec3d == null) {
               this.obstructed = true;
               return;
            }

            this.field_204736_a.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, this.field_204737_b);
         }

      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.field_204736_a.func_204713_s(true);
         this.obstructed = false;
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         this.field_204736_a.func_204713_s(false);
      }
   }

   static class AITridentAttack extends EntityAIAttackRanged {
      private final EntityDrowned field_204728_a;

      public AITridentAttack(IRangedAttackMob p_i48907_1_, double p_i48907_2_, int p_i48907_4_, float p_i48907_5_) {
         super(p_i48907_1_, p_i48907_2_, p_i48907_4_, p_i48907_5_);
         this.field_204728_a = (EntityDrowned)p_i48907_1_;
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         return super.shouldExecute() && this.field_204728_a.getHeldItemMainhand().getItem() == Items.TRIDENT;
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         super.startExecuting();
         this.field_204728_a.setSwingingArms(true);
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         super.resetTask();
         this.field_204728_a.setSwingingArms(false);
      }
   }

   static class AttackTargetPredicate implements Predicate<EntityPlayer> {
      private final EntityDrowned field_204740_a;

      public AttackTargetPredicate(EntityDrowned p_i48912_1_) {
         this.field_204740_a = p_i48912_1_;
      }

      public boolean test(@Nullable EntityPlayer p_test_1_) {
         return this.field_204740_a.shouldAttack(p_test_1_);
      }
   }

   static class MoveHelper extends EntityMoveHelper {
      private final EntityDrowned drowned;

      public MoveHelper(EntityDrowned p_i48909_1_) {
         super(p_i48909_1_);
         this.drowned = p_i48909_1_;
      }

      public void tick() {
         EntityLivingBase entitylivingbase = this.drowned.getAttackTarget();
         if (this.drowned.func_204715_dF() && this.drowned.isInWater()) {
            if (entitylivingbase != null && entitylivingbase.posY > this.drowned.posY || this.drowned.field_204718_bx) {
               this.drowned.motionY += 0.002D;
            }

            if (this.action != EntityMoveHelper.Action.MOVE_TO || this.drowned.getNavigator().noPath()) {
               this.drowned.setAIMoveSpeed(0.0F);
               return;
            }

            double d0 = this.posX - this.drowned.posX;
            double d1 = this.posY - this.drowned.posY;
            double d2 = this.posZ - this.drowned.posZ;
            double d3 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
            d1 = d1 / d3;
            float f = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
            this.drowned.rotationYaw = this.limitAngle(this.drowned.rotationYaw, f, 90.0F);
            this.drowned.renderYawOffset = this.drowned.rotationYaw;
            float f1 = (float)(this.speed * this.drowned.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
            this.drowned.setAIMoveSpeed(this.drowned.getAIMoveSpeed() + (f1 - this.drowned.getAIMoveSpeed()) * 0.125F);
            this.drowned.motionY += (double)this.drowned.getAIMoveSpeed() * d1 * 0.1D;
            this.drowned.motionX += (double)this.drowned.getAIMoveSpeed() * d0 * 0.005D;
            this.drowned.motionZ += (double)this.drowned.getAIMoveSpeed() * d2 * 0.005D;
         } else {
            if (!this.drowned.onGround) {
               this.drowned.motionY -= 0.008D;
            }

            super.tick();
         }

      }
   }
}