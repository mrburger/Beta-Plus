package net.minecraft.entity.passive;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIBreathAir;
import net.minecraft.entity.ai.EntityAIFindWater;
import net.minecraft.entity.ai.EntityAIFollowBoat;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIJump;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWanderSwim;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityDolphinHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.pathfinding.PathType;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityDolphin extends EntityWaterMob {
   private static final DataParameter<BlockPos> TREASURE_POS = EntityDataManager.createKey(EntityDolphin.class, DataSerializers.BLOCK_POS);
   private static final DataParameter<Boolean> GOT_FISH = EntityDataManager.createKey(EntityDolphin.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> MOISTNESS = EntityDataManager.createKey(EntityDolphin.class, DataSerializers.VARINT);
   public static final Predicate<EntityItem> ITEM_SELECTOR = (p_205023_0_) -> {
      return !p_205023_0_.cannotPickup() && p_205023_0_.isAlive() && p_205023_0_.isInWater();
   };

   public EntityDolphin(World p_i48935_1_) {
      super(EntityType.DOLPHIN, p_i48935_1_);
      this.setSize(0.9F, 0.6F);
      this.moveHelper = new EntityDolphin.MoveHelper(this);
      this.lookHelper = new EntityDolphinHelper(this, 10);
      this.setCanPickUpLoot(true);
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData entityLivingData, @Nullable NBTTagCompound itemNbt) {
      this.setAir(this.getMaxAir());
      this.rotationPitch = 0.0F;
      return super.onInitialSpawn(difficulty, entityLivingData, itemNbt);
   }

   public boolean canBreatheUnderwater() {
      return false;
   }

   protected void updateAir(int p_209207_1_) {
   }

   public void setTreasurePos(BlockPos p_208012_1_) {
      this.dataManager.set(TREASURE_POS, p_208012_1_);
   }

   public BlockPos getTreasurePos() {
      return this.dataManager.get(TREASURE_POS);
   }

   public boolean hasGotFish() {
      return this.dataManager.get(GOT_FISH);
   }

   public void setGotFish(boolean p_208008_1_) {
      this.dataManager.set(GOT_FISH, p_208008_1_);
   }

   public int getMoistness() {
      return this.dataManager.get(MOISTNESS);
   }

   public void setMoistness(int p_211137_1_) {
      this.dataManager.set(MOISTNESS, p_211137_1_);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(TREASURE_POS, BlockPos.ORIGIN);
      this.dataManager.register(GOT_FISH, false);
      this.dataManager.register(MOISTNESS, 2400);
   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      compound.setInt("TreasurePosX", this.getTreasurePos().getX());
      compound.setInt("TreasurePosY", this.getTreasurePos().getY());
      compound.setInt("TreasurePosZ", this.getTreasurePos().getZ());
      compound.setBoolean("GotFish", this.hasGotFish());
      compound.setInt("Moistness", this.getMoistness());
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(NBTTagCompound compound) {
      int i = compound.getInt("TreasurePosX");
      int j = compound.getInt("TreasurePosY");
      int k = compound.getInt("TreasurePosZ");
      this.setTreasurePos(new BlockPos(i, j, k));
      super.readAdditional(compound);
      this.setGotFish(compound.getBoolean("GotFish"));
      this.setMoistness(compound.getInt("Moistness"));
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityAIBreathAir(this));
      this.tasks.addTask(0, new EntityAIFindWater(this));
      this.tasks.addTask(1, new EntityDolphin.AISwimToTreasure(this));
      this.tasks.addTask(2, new EntityDolphin.AISwimWithPlayer(this, 4.0D));
      this.tasks.addTask(4, new EntityAIWanderSwim(this, 1.0D, 10));
      this.tasks.addTask(4, new EntityAILookIdle(this));
      this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(5, new EntityAIJump(this, 10));
      this.tasks.addTask(6, new EntityAIAttackMelee(this, (double)1.2F, true));
      this.tasks.addTask(8, new EntityDolphin.AIPlayWithItems());
      this.tasks.addTask(8, new EntityAIFollowBoat(this));
      this.tasks.addTask(9, new EntityAIAvoidEntity<>(this, EntityGuardian.class, 8.0F, 1.0D, 1.0D));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, EntityGuardian.class));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)1.2F);
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
   }

   /**
    * Returns new PathNavigateGround instance
    */
   protected PathNavigate createNavigator(World worldIn) {
      return new PathNavigateSwimmer(this, worldIn);
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue()));
      if (flag) {
         this.applyEnchantments(this, entityIn);
         this.playSound(SoundEvents.ENTITY_DOLPHIN_ATTACK, 1.0F, 1.0F);
      }

      return flag;
   }

   public int getMaxAir() {
      return 4800;
   }

   protected int determineNextAir(int currentAir) {
      return this.getMaxAir();
   }

   public float getEyeHeight() {
      return 0.3F;
   }

   /**
    * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
    * use in wolves.
    */
   public int getVerticalFaceSpeed() {
      return 1;
   }

   public int getHorizontalFaceSpeed() {
      return 1;
   }

   protected boolean canBeRidden(Entity entityIn) {
      return true;
   }

   /**
    * Tests if this entity should pickup a weapon or an armor. Entity drops current weapon or armor if the new one is
    * better.
    */
   protected void updateEquipmentIfNeeded(EntityItem itemEntity) {
      if (this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).isEmpty()) {
         ItemStack itemstack = itemEntity.getItem();
         if (this.canEquipItem(itemstack)) {
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, itemstack);
            this.inventoryHandsDropChances[EntityEquipmentSlot.MAINHAND.getIndex()] = 2.0F;
            this.onItemPickup(itemEntity, itemstack.getCount());
            itemEntity.remove();
         }
      }

   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (!this.isAIDisabled()) {
         if (this.isInWaterRainOrBubbleColumn()) {
            this.setMoistness(2400);
         } else {
            this.setMoistness(this.getMoistness() - 1);
            if (this.getMoistness() <= 0) {
               this.attackEntityFrom(DamageSource.DRYOUT, 1.0F);
            }

            if (this.onGround) {
               this.motionY += 0.5D;
               this.motionX += (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 0.2F);
               this.motionZ += (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 0.2F);
               this.rotationYaw = this.rand.nextFloat() * 360.0F;
               this.onGround = false;
               this.isAirBorne = true;
            }
         }

         if (this.world.isRemote && this.isInWater() && this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ > 0.03D) {
            Vec3d vec3d = this.getLook(0.0F);
            float f = MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F)) * 0.3F;
            float f1 = MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F)) * 0.3F;
            float f2 = 1.2F - this.rand.nextFloat() * 0.7F;

            for(int i = 0; i < 2; ++i) {
               this.world.spawnParticle(Particles.DOLPHIN, this.posX - vec3d.x * (double)f2 + (double)f, this.posY - vec3d.y, this.posZ - vec3d.z * (double)f2 + (double)f1, 0.0D, 0.0D, 0.0D);
               this.world.spawnParticle(Particles.DOLPHIN, this.posX - vec3d.x * (double)f2 - (double)f, this.posY - vec3d.y, this.posZ - vec3d.z * (double)f2 - (double)f1, 0.0D, 0.0D, 0.0D);
            }
         }

      }
   }

   /**
    * Handler for {@link World#setEntityState}
    */
   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 38) {
         this.func_208401_a(Particles.HAPPY_VILLAGER);
      } else {
         super.handleStatusUpdate(id);
      }

   }

   @OnlyIn(Dist.CLIENT)
   private void func_208401_a(IParticleData p_208401_1_) {
      for(int i = 0; i < 7; ++i) {
         double d0 = this.rand.nextGaussian() * 0.01D;
         double d1 = this.rand.nextGaussian() * 0.01D;
         double d2 = this.rand.nextGaussian() * 0.01D;
         this.world.spawnParticle(p_208401_1_, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)0.2F + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
      }

   }

   protected boolean processInteract(EntityPlayer player, EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (!itemstack.isEmpty() && itemstack.getItem().isIn(ItemTags.FISHES)) {
         if (!this.world.isRemote) {
            this.playSound(SoundEvents.ENTITY_DOLPHIN_EAT, 1.0F, 1.0F);
         }

         this.setGotFish(true);
         if (!player.abilities.isCreativeMode) {
            itemstack.shrink(1);
         }

         return true;
      } else {
         return super.processInteract(player, hand);
      }
   }

   @Nullable
   public EntityItem throwItem(ItemStack p_205024_1_) {
      if (p_205024_1_.isEmpty()) {
         return null;
      } else {
         double d0 = this.posY - (double)0.3F + (double)this.getEyeHeight();
         EntityItem entityitem = new EntityItem(this.world, this.posX, d0, this.posZ, p_205024_1_);
         entityitem.setPickupDelay(40);
         entityitem.setThrowerId(this.getUniqueID());
         float f = 0.3F;
         entityitem.motionX = (double)(-MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F)) * MathHelper.cos(this.rotationPitch * ((float)Math.PI / 180F)) * f);
         entityitem.motionY = (double)(MathHelper.sin(this.rotationPitch * ((float)Math.PI / 180F)) * f * 1.5F);
         entityitem.motionZ = (double)(MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F)) * MathHelper.cos(this.rotationPitch * ((float)Math.PI / 180F)) * f);
         float f1 = this.rand.nextFloat() * ((float)Math.PI * 2F);
         f = 0.02F * this.rand.nextFloat();
         entityitem.motionX += (double)(MathHelper.cos(f1) * f);
         entityitem.motionZ += (double)(MathHelper.sin(f1) * f);
         this.world.spawnEntity(entityitem);
         return entityitem;
      }
   }

   public boolean canSpawn(IWorld worldIn, boolean p_205020_2_) {
      return this.posY > 45.0D && this.posY < (double)worldIn.getSeaLevel() && worldIn.getBiome(new BlockPos(this)) != Biomes.OCEAN || worldIn.getBiome(new BlockPos(this)) != Biomes.DEEP_OCEAN && super.canSpawn(worldIn, p_205020_2_);
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_DOLPHIN_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_DOLPHIN_DEATH;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return this.isInWater() ? SoundEvents.ENTITY_DOLPHIN_AMBIENT_WATER : SoundEvents.ENTITY_DOLPHIN_AMBIENT;
   }

   protected SoundEvent getSplashSound() {
      return SoundEvents.ENTITY_DOLPHIN_SPLASH;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.ENTITY_DOLPHIN_SWIM;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_DOLPHIN;
   }

   protected boolean func_208006_dE() {
      BlockPos blockpos = this.getNavigator().getTargetPos();
      if (blockpos != null) {
         return this.getDistanceSq(blockpos) < 144.0D;
      } else {
         return false;
      }
   }

   public void travel(float strafe, float vertical, float forward) {
      if (this.isServerWorld() && this.isInWater()) {
         this.moveRelative(strafe, vertical, forward, this.getAIMoveSpeed());
         this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
         this.motionX *= (double)0.9F;
         this.motionY *= (double)0.9F;
         this.motionZ *= (double)0.9F;
         if (this.getAttackTarget() == null) {
            this.motionY -= 0.005D;
         }
      } else {
         super.travel(strafe, vertical, forward);
      }

   }

   public boolean canBeLeashedTo(EntityPlayer player) {
      return true;
   }

   class AIPlayWithItems extends EntityAIBase {
      private int field_205154_b;

      private AIPlayWithItems() {
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         if (this.field_205154_b > EntityDolphin.this.ticksExisted) {
            return false;
         } else {
            List<EntityItem> list = EntityDolphin.this.world.getEntitiesWithinAABB(EntityItem.class, EntityDolphin.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), EntityDolphin.ITEM_SELECTOR);
            return !list.isEmpty() || !EntityDolphin.this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).isEmpty();
         }
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         List<EntityItem> list = EntityDolphin.this.world.getEntitiesWithinAABB(EntityItem.class, EntityDolphin.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), EntityDolphin.ITEM_SELECTOR);
         if (!list.isEmpty()) {
            EntityDolphin.this.getNavigator().tryMoveToEntityLiving(list.get(0), (double)1.2F);
            EntityDolphin.this.playSound(SoundEvents.ENTITY_DOLPHIN_PLAY, 1.0F, 1.0F);
         }

         this.field_205154_b = 0;
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         ItemStack itemstack = EntityDolphin.this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
         if (!itemstack.isEmpty()) {
            EntityDolphin.this.throwItem(itemstack);
            EntityDolphin.this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
            this.field_205154_b = EntityDolphin.this.ticksExisted + EntityDolphin.this.rand.nextInt(100);
         }

      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         List<EntityItem> list = EntityDolphin.this.world.getEntitiesWithinAABB(EntityItem.class, EntityDolphin.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), EntityDolphin.ITEM_SELECTOR);
         ItemStack itemstack = EntityDolphin.this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
         if (!itemstack.isEmpty()) {
            EntityDolphin.this.throwItem(itemstack);
            EntityDolphin.this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
         } else if (!list.isEmpty()) {
            EntityDolphin.this.getNavigator().tryMoveToEntityLiving(list.get(0), (double)1.2F);
         }

      }
   }

   static class AISwimToTreasure extends EntityAIBase {
      private final EntityDolphin dolphin;
      private boolean field_208058_b;

      AISwimToTreasure(EntityDolphin p_i49344_1_) {
         this.dolphin = p_i49344_1_;
         this.setMutexBits(3);
      }

      /**
       * Determine if this AI Task is interruptible by a higher (= lower value) priority task. All vanilla AITask have
       * this value set to true.
       */
      public boolean isInterruptible() {
         return false;
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         return this.dolphin.hasGotFish() && this.dolphin.getAir() >= 100;
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         BlockPos blockpos = this.dolphin.getTreasurePos();
         return this.dolphin.getDistanceSq(new BlockPos((double)blockpos.getX(), this.dolphin.posY, (double)blockpos.getZ())) > 16.0D && !this.field_208058_b && this.dolphin.getAir() >= 100;
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.field_208058_b = false;
         this.dolphin.getNavigator().clearPath();
         World world = this.dolphin.world;
         BlockPos blockpos = new BlockPos(this.dolphin);
         String s = (double)world.rand.nextFloat() >= 0.5D ? "Ocean_Ruin" : "Shipwreck";
         BlockPos blockpos1 = world.findNearestStructure(s, blockpos, 50, false);
         if (blockpos1 == null) {
            BlockPos blockpos2 = world.findNearestStructure(s.equals("Ocean_Ruin") ? "Shipwreck" : "Ocean_Ruin", blockpos, 50, false);
            if (blockpos2 == null) {
               this.field_208058_b = true;
               return;
            }

            this.dolphin.setTreasurePos(blockpos2);
         } else {
            this.dolphin.setTreasurePos(blockpos1);
         }

         world.setEntityState(this.dolphin, (byte)38);
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         BlockPos blockpos = this.dolphin.getTreasurePos();
         if (this.dolphin.getDistanceSq(new BlockPos((double)blockpos.getX(), this.dolphin.posY, (double)blockpos.getZ())) <= 16.0D || this.field_208058_b) {
            this.dolphin.setGotFish(false);
         }

      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         BlockPos blockpos = this.dolphin.getTreasurePos();
         World world = this.dolphin.world;
         if (this.dolphin.func_208006_dE() || this.dolphin.getNavigator().noPath()) {
            Vec3d vec3d = RandomPositionGenerator.func_203155_a(this.dolphin, 16, 1, new Vec3d((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ()), (double)((float)Math.PI / 8F));
            if (vec3d == null) {
               vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.dolphin, 8, 4, new Vec3d((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ()));
            }

            if (vec3d != null) {
               BlockPos blockpos1 = new BlockPos(vec3d);
               if (!world.getFluidState(blockpos1).isTagged(FluidTags.WATER) || !world.getBlockState(blockpos1).allowsMovement(world, blockpos1, PathType.WATER)) {
                  vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.dolphin, 8, 5, new Vec3d((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ()));
               }
            }

            if (vec3d == null) {
               this.field_208058_b = true;
               return;
            }

            this.dolphin.getLookHelper().setLookPosition(vec3d.x, vec3d.y, vec3d.z, (float)(this.dolphin.getHorizontalFaceSpeed() + 20), (float)this.dolphin.getVerticalFaceSpeed());
            this.dolphin.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 1.3D);
            if (world.rand.nextInt(80) == 0) {
               world.setEntityState(this.dolphin, (byte)38);
            }
         }

      }
   }

   static class AISwimWithPlayer extends EntityAIBase {
      private final EntityDolphin dolphin;
      private final double field_206835_b;
      private EntityPlayer field_206836_c;

      AISwimWithPlayer(EntityDolphin p_i48994_1_, double p_i48994_2_) {
         this.dolphin = p_i48994_1_;
         this.field_206835_b = p_i48994_2_;
         this.setMutexBits(3);
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         this.field_206836_c = this.dolphin.world.getClosestPlayerToEntity(this.dolphin, 10.0D);
         return this.field_206836_c == null ? false : this.field_206836_c.isSwimming();
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return this.field_206836_c != null && this.field_206836_c.isSwimming() && this.dolphin.getDistanceSq(this.field_206836_c) < 256.0D;
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.field_206836_c.addPotionEffect(new PotionEffect(MobEffects.DOLPHINS_GRACE, 100));
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         this.field_206836_c = null;
         this.dolphin.getNavigator().clearPath();
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         this.dolphin.getLookHelper().setLookPositionWithEntity(this.field_206836_c, (float)(this.dolphin.getHorizontalFaceSpeed() + 20), (float)this.dolphin.getVerticalFaceSpeed());
         if (this.dolphin.getDistanceSq(this.field_206836_c) < 6.25D) {
            this.dolphin.getNavigator().clearPath();
         } else {
            this.dolphin.getNavigator().tryMoveToEntityLiving(this.field_206836_c, this.field_206835_b);
         }

         if (this.field_206836_c.isSwimming() && this.field_206836_c.world.rand.nextInt(6) == 0) {
            this.field_206836_c.addPotionEffect(new PotionEffect(MobEffects.DOLPHINS_GRACE, 100));
         }

      }
   }

   static class MoveHelper extends EntityMoveHelper {
      private final EntityDolphin dolphin;

      public MoveHelper(EntityDolphin p_i48945_1_) {
         super(p_i48945_1_);
         this.dolphin = p_i48945_1_;
      }

      public void tick() {
         if (this.dolphin.isInWater()) {
            this.dolphin.motionY += 0.005D;
         }

         if (this.action == EntityMoveHelper.Action.MOVE_TO && !this.dolphin.getNavigator().noPath()) {
            double d0 = this.posX - this.dolphin.posX;
            double d1 = this.posY - this.dolphin.posY;
            double d2 = this.posZ - this.dolphin.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            if (d3 < (double)2.5000003E-7F) {
               this.entity.setMoveForward(0.0F);
            } else {
               float f = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
               this.dolphin.rotationYaw = this.limitAngle(this.dolphin.rotationYaw, f, 10.0F);
               this.dolphin.renderYawOffset = this.dolphin.rotationYaw;
               this.dolphin.rotationYawHead = this.dolphin.rotationYaw;
               float f1 = (float)(this.speed * this.dolphin.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
               if (this.dolphin.isInWater()) {
                  this.dolphin.setAIMoveSpeed(f1 * 0.02F);
                  float f2 = -((float)(MathHelper.atan2(d1, (double)MathHelper.sqrt(d0 * d0 + d2 * d2)) * (double)(180F / (float)Math.PI)));
                  f2 = MathHelper.clamp(MathHelper.wrapDegrees(f2), -85.0F, 85.0F);
                  this.dolphin.rotationPitch = this.limitAngle(this.dolphin.rotationPitch, f2, 5.0F);
                  float f3 = MathHelper.cos(this.dolphin.rotationPitch * ((float)Math.PI / 180F));
                  float f4 = MathHelper.sin(this.dolphin.rotationPitch * ((float)Math.PI / 180F));
                  this.dolphin.moveForward = f3 * f1;
                  this.dolphin.moveVertical = -f4 * f1;
               } else {
                  this.dolphin.setAIMoveSpeed(f1 * 0.1F);
               }

            }
         } else {
            this.dolphin.setAIMoveSpeed(0.0F);
            this.dolphin.setMoveStrafing(0.0F);
            this.dolphin.setMoveVertical(0.0F);
            this.dolphin.setMoveForward(0.0F);
         }
      }
   }
}