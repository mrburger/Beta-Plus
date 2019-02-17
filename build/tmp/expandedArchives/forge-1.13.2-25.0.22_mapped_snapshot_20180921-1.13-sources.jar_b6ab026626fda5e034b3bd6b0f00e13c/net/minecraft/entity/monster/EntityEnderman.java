package net.minecraft.entity.monster;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityEnderman extends EntityMob {
   private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
   private static final AttributeModifier ATTACKING_SPEED_BOOST = (new AttributeModifier(ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", (double)0.15F, 0)).setSaved(false);
   private static final DataParameter<Optional<IBlockState>> CARRIED_BLOCK = EntityDataManager.createKey(EntityEnderman.class, DataSerializers.OPTIONAL_BLOCK_STATE);
   private static final DataParameter<Boolean> SCREAMING = EntityDataManager.createKey(EntityEnderman.class, DataSerializers.BOOLEAN);
   private int lastCreepySound;
   private int targetChangeTime;

   public EntityEnderman(World worldIn) {
      super(EntityType.ENDERMAN, worldIn);
      this.setSize(0.6F, 2.9F);
      this.stepHeight = 1.0F;
      this.setPathPriority(PathNodeType.WATER, -1.0F);
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, false));
      this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D, 0.0F));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.tasks.addTask(10, new EntityEnderman.AIPlaceBlock(this));
      this.tasks.addTask(11, new EntityEnderman.AITakeBlock(this));
      this.targetTasks.addTask(1, new EntityEnderman.AIFindPlayer(this));
      this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityEndermite.class, 10, true, false, EntityEndermite::isSpawnedByPlayer));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.3F);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
   }

   /**
    * Sets the active target the Task system uses for tracking
    */
   public void setAttackTarget(@Nullable EntityLivingBase entitylivingbaseIn) {
      super.setAttackTarget(entitylivingbaseIn);
      IAttributeInstance iattributeinstance = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      if (entitylivingbaseIn == null) {
         this.targetChangeTime = 0;
         this.dataManager.set(SCREAMING, false);
         iattributeinstance.removeModifier(ATTACKING_SPEED_BOOST);
      } else {
         this.targetChangeTime = this.ticksExisted;
         this.dataManager.set(SCREAMING, true);
         if (!iattributeinstance.hasModifier(ATTACKING_SPEED_BOOST)) {
            iattributeinstance.applyModifier(ATTACKING_SPEED_BOOST);
         }
      }

   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(CARRIED_BLOCK, Optional.empty());
      this.dataManager.register(SCREAMING, false);
   }

   public void playEndermanSound() {
      if (this.ticksExisted >= this.lastCreepySound + 400) {
         this.lastCreepySound = this.ticksExisted;
         if (!this.isSilent()) {
            this.world.playSound(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, SoundEvents.ENTITY_ENDERMAN_STARE, this.getSoundCategory(), 2.5F, 1.0F, false);
         }
      }

   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      if (SCREAMING.equals(key) && this.isScreaming() && this.world.isRemote) {
         this.playEndermanSound();
      }

      super.notifyDataManagerChange(key);
   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      IBlockState iblockstate = this.func_195405_dq();
      if (iblockstate != null) {
         compound.setTag("carriedBlockState", NBTUtil.writeBlockState(iblockstate));
      }

   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      IBlockState iblockstate = null;
      if (compound.contains("carriedBlockState", 10)) {
         iblockstate = NBTUtil.readBlockState(compound.getCompound("carriedBlockState"));
         if (iblockstate.isAir()) {
            iblockstate = null;
         }
      }

      this.func_195406_b(iblockstate);
   }

   /**
    * Checks to see if this enderman should be attacking this player
    */
   private boolean shouldAttackPlayer(EntityPlayer player) {
      ItemStack itemstack = player.inventory.armorInventory.get(3);
      if (itemstack.getItem() == Blocks.CARVED_PUMPKIN.asItem()) {
         return false;
      } else {
         Vec3d vec3d = player.getLook(1.0F).normalize();
         Vec3d vec3d1 = new Vec3d(this.posX - player.posX, this.getBoundingBox().minY + (double)this.getEyeHeight() - (player.posY + (double)player.getEyeHeight()), this.posZ - player.posZ);
         double d0 = vec3d1.length();
         vec3d1 = vec3d1.normalize();
         double d1 = vec3d.dotProduct(vec3d1);
         return d1 > 1.0D - 0.025D / d0 ? player.canEntityBeSeen(this) : false;
      }
   }

   public float getEyeHeight() {
      return 2.55F;
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      if (this.world.isRemote) {
         for(int i = 0; i < 2; ++i) {
            this.world.spawnParticle(Particles.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height - 0.25D, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D);
         }
      }

      this.isJumping = false;
      super.livingTick();
   }

   protected void updateAITasks() {
      if (this.isInWaterRainOrBubbleColumn()) {
         this.attackEntityFrom(DamageSource.DROWN, 1.0F);
      }

      if (this.world.isDaytime() && this.ticksExisted >= this.targetChangeTime + 600) {
         float f = this.getBrightness();
         if (f > 0.5F && this.world.canSeeSky(new BlockPos(this)) && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
            this.setAttackTarget((EntityLivingBase)null);
            this.teleportRandomly();
         }
      }

      super.updateAITasks();
   }

   /**
    * Teleport the enderman to a random nearby position
    */
   protected boolean teleportRandomly() {
      double d0 = this.posX + (this.rand.nextDouble() - 0.5D) * 64.0D;
      double d1 = this.posY + (double)(this.rand.nextInt(64) - 32);
      double d2 = this.posZ + (this.rand.nextDouble() - 0.5D) * 64.0D;
      return this.teleportTo(d0, d1, d2);
   }

   /**
    * Teleport the enderman to another entity
    */
   protected boolean teleportToEntity(Entity p_70816_1_) {
      Vec3d vec3d = new Vec3d(this.posX - p_70816_1_.posX, this.getBoundingBox().minY + (double)(this.height / 2.0F) - p_70816_1_.posY + (double)p_70816_1_.getEyeHeight(), this.posZ - p_70816_1_.posZ);
      vec3d = vec3d.normalize();
      double d0 = 16.0D;
      double d1 = this.posX + (this.rand.nextDouble() - 0.5D) * 8.0D - vec3d.x * 16.0D;
      double d2 = this.posY + (double)(this.rand.nextInt(16) - 8) - vec3d.y * 16.0D;
      double d3 = this.posZ + (this.rand.nextDouble() - 0.5D) * 8.0D - vec3d.z * 16.0D;
      return this.teleportTo(d1, d2, d3);
   }

   /**
    * Teleport the enderman
    */
   private boolean teleportTo(double x, double y, double z) {
      net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(this, x, y, z, 0);
      if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return false;
      boolean flag = this.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ());
      if (flag) {
         this.world.playSound((EntityPlayer)null, this.prevPosX, this.prevPosY, this.prevPosZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
         this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
      }

      return flag;
   }

   protected SoundEvent getAmbientSound() {
      return this.isScreaming() ? SoundEvents.ENTITY_ENDERMAN_SCREAM : SoundEvents.ENTITY_ENDERMAN_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_ENDERMAN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ENDERMAN_DEATH;
   }

   /**
    * Drop the equipment for this entity.
    */
   protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
      super.dropEquipment(wasRecentlyHit, lootingModifier);
      IBlockState iblockstate = this.func_195405_dq();
      if (iblockstate != null) {
         this.entityDropItem(iblockstate.getBlock());
      }

   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_ENDERMAN;
   }

   public void func_195406_b(@Nullable IBlockState p_195406_1_) {
      this.dataManager.set(CARRIED_BLOCK, Optional.ofNullable(p_195406_1_));
   }

   @Nullable
   public IBlockState func_195405_dq() {
      return this.dataManager.get(CARRIED_BLOCK).orElse((IBlockState)null);
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else if (source instanceof EntityDamageSourceIndirect) {
         for(int i = 0; i < 64; ++i) {
            if (this.teleportRandomly()) {
               return true;
            }
         }

         return false;
      } else {
         boolean flag = super.attackEntityFrom(source, amount);
         if (source.isUnblockable() && this.rand.nextInt(10) != 0) {
            this.teleportRandomly();
         }

         return flag;
      }
   }

   public boolean isScreaming() {
      return this.dataManager.get(SCREAMING);
   }

   static class AIFindPlayer extends EntityAINearestAttackableTarget<EntityPlayer> {
      private final EntityEnderman enderman;
      /** The player */
      private EntityPlayer player;
      private int aggroTime;
      private int teleportTime;

      public AIFindPlayer(EntityEnderman p_i45842_1_) {
         super(p_i45842_1_, EntityPlayer.class, false);
         this.enderman = p_i45842_1_;
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         double d0 = this.getTargetDistance();
         this.player = this.enderman.world.getNearestAttackablePlayer(this.enderman.posX, this.enderman.posY, this.enderman.posZ, d0, d0, (Function<EntityPlayer, Double>)null, (p_210295_1_) -> {
            return p_210295_1_ != null && this.enderman.shouldAttackPlayer(p_210295_1_);
         });
         return this.player != null;
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.aggroTime = 5;
         this.teleportTime = 0;
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         this.player = null;
         super.resetTask();
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         if (this.player != null) {
            if (!this.enderman.shouldAttackPlayer(this.player)) {
               return false;
            } else {
               this.enderman.faceEntity(this.player, 10.0F, 10.0F);
               return true;
            }
         } else {
            return this.targetEntity != null && this.targetEntity.isAlive() ? true : super.shouldContinueExecuting();
         }
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         if (this.player != null) {
            if (--this.aggroTime <= 0) {
               this.targetEntity = this.player;
               this.player = null;
               super.startExecuting();
            }
         } else {
            if (this.targetEntity != null) {
               if (this.enderman.shouldAttackPlayer(this.targetEntity)) {
                  if (this.targetEntity.getDistanceSq(this.enderman) < 16.0D) {
                     this.enderman.teleportRandomly();
                  }

                  this.teleportTime = 0;
               } else if (this.targetEntity.getDistanceSq(this.enderman) > 256.0D && this.teleportTime++ >= 30 && this.enderman.teleportToEntity(this.targetEntity)) {
                  this.teleportTime = 0;
               }
            }

            super.tick();
         }

      }
   }

   static class AIPlaceBlock extends EntityAIBase {
      private final EntityEnderman enderman;

      public AIPlaceBlock(EntityEnderman p_i45843_1_) {
         this.enderman = p_i45843_1_;
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         if (this.enderman.func_195405_dq() == null) {
            return false;
         } else if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.enderman.world, this.enderman)) {
            return false;
         } else {
            return this.enderman.getRNG().nextInt(2000) == 0;
         }
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         Random random = this.enderman.getRNG();
         IWorld iworld = this.enderman.world;
         int i = MathHelper.floor(this.enderman.posX - 1.0D + random.nextDouble() * 2.0D);
         int j = MathHelper.floor(this.enderman.posY + random.nextDouble() * 2.0D);
         int k = MathHelper.floor(this.enderman.posZ - 1.0D + random.nextDouble() * 2.0D);
         BlockPos blockpos = new BlockPos(i, j, k);
         IBlockState iblockstate = iworld.getBlockState(blockpos);
         IBlockState iblockstate1 = iworld.getBlockState(blockpos.down());
         IBlockState iblockstate2 = this.enderman.func_195405_dq();
         if (iblockstate2 != null && this.func_195924_a(iworld, blockpos, iblockstate2, iblockstate, iblockstate1)) {
            iworld.setBlockState(blockpos, iblockstate2, 3);
            this.enderman.func_195406_b((IBlockState)null);
         }

      }

      private boolean func_195924_a(IWorldReaderBase p_195924_1_, BlockPos p_195924_2_, IBlockState p_195924_3_, IBlockState p_195924_4_, IBlockState p_195924_5_) {
         return p_195924_4_.isAir() && !p_195924_5_.isAir() && p_195924_5_.isFullCube() && p_195924_3_.isValidPosition(p_195924_1_, p_195924_2_);
      }
   }

   static class AITakeBlock extends EntityAIBase {
      private final EntityEnderman enderman;

      public AITakeBlock(EntityEnderman p_i45841_1_) {
         this.enderman = p_i45841_1_;
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         if (this.enderman.func_195405_dq() != null) {
            return false;
         } else if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.enderman.world, this.enderman)) {
            return false;
         } else {
            return this.enderman.getRNG().nextInt(20) == 0;
         }
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         Random random = this.enderman.getRNG();
         World world = this.enderman.world;
         int i = MathHelper.floor(this.enderman.posX - 2.0D + random.nextDouble() * 4.0D);
         int j = MathHelper.floor(this.enderman.posY + random.nextDouble() * 3.0D);
         int k = MathHelper.floor(this.enderman.posZ - 2.0D + random.nextDouble() * 4.0D);
         BlockPos blockpos = new BlockPos(i, j, k);
         IBlockState iblockstate = world.getBlockState(blockpos);
         Block block = iblockstate.getBlock();
         RayTraceResult raytraceresult = world.rayTraceBlocks(new Vec3d((double)((float)MathHelper.floor(this.enderman.posX) + 0.5F), (double)((float)j + 0.5F), (double)((float)MathHelper.floor(this.enderman.posZ) + 0.5F)), new Vec3d((double)((float)i + 0.5F), (double)((float)j + 0.5F), (double)((float)k + 0.5F)), RayTraceFluidMode.NEVER, true, false);
         boolean flag = raytraceresult != null && raytraceresult.getBlockPos().equals(blockpos);
         if (block.isIn(BlockTags.ENDERMAN_HOLDABLE) && flag) {
            this.enderman.func_195406_b(iblockstate);
            world.removeBlock(blockpos);
         }

      }
   }
}