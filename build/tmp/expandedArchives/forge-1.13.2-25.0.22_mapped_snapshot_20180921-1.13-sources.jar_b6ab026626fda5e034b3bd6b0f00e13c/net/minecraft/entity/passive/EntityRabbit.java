package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCarrot;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityRabbit extends EntityAnimal {
   private static final DataParameter<Integer> RABBIT_TYPE = EntityDataManager.createKey(EntityRabbit.class, DataSerializers.VARINT);
   private static final ResourceLocation field_200611_bx = new ResourceLocation("killer_bunny");
   private int jumpTicks;
   private int jumpDuration;
   private boolean wasOnGround;
   private int currentMoveTypeDuration;
   private int carrotTicks;

   public EntityRabbit(World worldIn) {
      super(EntityType.RABBIT, worldIn);
      this.setSize(0.4F, 0.5F);
      this.jumpHelper = new EntityRabbit.RabbitJumpHelper(this);
      this.moveHelper = new EntityRabbit.RabbitMoveHelper(this);
      this.setMovementSpeed(0.0D);
   }

   protected void initEntityAI() {
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityRabbit.AIPanic(this, 2.2D));
      this.tasks.addTask(2, new EntityAIMate(this, 0.8D));
      this.tasks.addTask(3, new EntityAITempt(this, 1.0D, Ingredient.fromItems(Items.CARROT, Items.GOLDEN_CARROT, Blocks.DANDELION), false));
      this.tasks.addTask(4, new EntityRabbit.AIAvoidEntity<>(this, EntityPlayer.class, 8.0F, 2.2D, 2.2D));
      this.tasks.addTask(4, new EntityRabbit.AIAvoidEntity<>(this, EntityWolf.class, 10.0F, 2.2D, 2.2D));
      this.tasks.addTask(4, new EntityRabbit.AIAvoidEntity<>(this, EntityMob.class, 4.0F, 2.2D, 2.2D));
      this.tasks.addTask(5, new EntityRabbit.AIRaidFarm(this));
      this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 0.6D));
      this.tasks.addTask(11, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
   }

   protected float getJumpUpwardsMotion() {
      if (!this.collidedHorizontally && (!this.moveHelper.isUpdating() || !(this.moveHelper.getY() > this.posY + 0.5D))) {
         Path path = this.navigator.getPath();
         if (path != null && path.getCurrentPathIndex() < path.getCurrentPathLength()) {
            Vec3d vec3d = path.getPosition(this);
            if (vec3d.y > this.posY + 0.5D) {
               return 0.5F;
            }
         }

         return this.moveHelper.getSpeed() <= 0.6D ? 0.2F : 0.3F;
      } else {
         return 0.5F;
      }
   }

   /**
    * Causes this entity to do an upwards motion (jumping).
    */
   protected void jump() {
      super.jump();
      double d0 = this.moveHelper.getSpeed();
      if (d0 > 0.0D) {
         double d1 = this.motionX * this.motionX + this.motionZ * this.motionZ;
         if (d1 < 0.010000000000000002D) {
            this.moveRelative(0.0F, 0.0F, 1.0F, 0.1F);
         }
      }

      if (!this.world.isRemote) {
         this.world.setEntityState(this, (byte)1);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float getJumpCompletion(float p_175521_1_) {
      return this.jumpDuration == 0 ? 0.0F : ((float)this.jumpTicks + p_175521_1_) / (float)this.jumpDuration;
   }

   public void setMovementSpeed(double newSpeed) {
      this.getNavigator().setSpeed(newSpeed);
      this.moveHelper.setMoveTo(this.moveHelper.getX(), this.moveHelper.getY(), this.moveHelper.getZ(), newSpeed);
   }

   public void setJumping(boolean jumping) {
      super.setJumping(jumping);
      if (jumping) {
         this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
      }

   }

   public void startJumping() {
      this.setJumping(true);
      this.jumpDuration = 10;
      this.jumpTicks = 0;
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(RABBIT_TYPE, 0);
   }

   public void updateAITasks() {
      if (this.currentMoveTypeDuration > 0) {
         --this.currentMoveTypeDuration;
      }

      if (this.carrotTicks > 0) {
         this.carrotTicks -= this.rand.nextInt(3);
         if (this.carrotTicks < 0) {
            this.carrotTicks = 0;
         }
      }

      if (this.onGround) {
         if (!this.wasOnGround) {
            this.setJumping(false);
            this.checkLandingDelay();
         }

         if (this.getRabbitType() == 99 && this.currentMoveTypeDuration == 0) {
            EntityLivingBase entitylivingbase = this.getAttackTarget();
            if (entitylivingbase != null && this.getDistanceSq(entitylivingbase) < 16.0D) {
               this.calculateRotationYaw(entitylivingbase.posX, entitylivingbase.posZ);
               this.moveHelper.setMoveTo(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, this.moveHelper.getSpeed());
               this.startJumping();
               this.wasOnGround = true;
            }
         }

         EntityRabbit.RabbitJumpHelper entityrabbit$rabbitjumphelper = (EntityRabbit.RabbitJumpHelper)this.jumpHelper;
         if (!entityrabbit$rabbitjumphelper.getIsJumping()) {
            if (this.moveHelper.isUpdating() && this.currentMoveTypeDuration == 0) {
               Path path = this.navigator.getPath();
               Vec3d vec3d = new Vec3d(this.moveHelper.getX(), this.moveHelper.getY(), this.moveHelper.getZ());
               if (path != null && path.getCurrentPathIndex() < path.getCurrentPathLength()) {
                  vec3d = path.getPosition(this);
               }

               this.calculateRotationYaw(vec3d.x, vec3d.z);
               this.startJumping();
            }
         } else if (!entityrabbit$rabbitjumphelper.canJump()) {
            this.enableJumpControl();
         }
      }

      this.wasOnGround = this.onGround;
   }

   /**
    * Attempts to create sprinting particles if the entity is sprinting and not in water.
    */
   public void spawnRunningParticles() {
   }

   private void calculateRotationYaw(double x, double z) {
      this.rotationYaw = (float)(MathHelper.atan2(z - this.posZ, x - this.posX) * (double)(180F / (float)Math.PI)) - 90.0F;
   }

   private void enableJumpControl() {
      ((EntityRabbit.RabbitJumpHelper)this.jumpHelper).setCanJump(true);
   }

   private void disableJumpControl() {
      ((EntityRabbit.RabbitJumpHelper)this.jumpHelper).setCanJump(false);
   }

   private void updateMoveTypeDuration() {
      if (this.moveHelper.getSpeed() < 2.2D) {
         this.currentMoveTypeDuration = 10;
      } else {
         this.currentMoveTypeDuration = 1;
      }

   }

   private void checkLandingDelay() {
      this.updateMoveTypeDuration();
      this.disableJumpControl();
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      super.livingTick();
      if (this.jumpTicks != this.jumpDuration) {
         ++this.jumpTicks;
      } else if (this.jumpDuration != 0) {
         this.jumpTicks = 0;
         this.jumpDuration = 0;
         this.setJumping(false);
      }

   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.3F);
   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      compound.setInt("RabbitType", this.getRabbitType());
      compound.setInt("MoreCarrotTicks", this.carrotTicks);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      this.setRabbitType(compound.getInt("RabbitType"));
      this.carrotTicks = compound.getInt("MoreCarrotTicks");
   }

   protected SoundEvent getJumpSound() {
      return SoundEvents.ENTITY_RABBIT_JUMP;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_RABBIT_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_RABBIT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_RABBIT_DEATH;
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      if (this.getRabbitType() == 99) {
         this.playSound(SoundEvents.ENTITY_RABBIT_ATTACK, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 8.0F);
      } else {
         return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
      }
   }

   public SoundCategory getSoundCategory() {
      return this.getRabbitType() == 99 ? SoundCategory.HOSTILE : SoundCategory.NEUTRAL;
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      return this.isInvulnerableTo(source) ? false : super.attackEntityFrom(source, amount);
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_RABBIT;
   }

   private boolean isRabbitBreedingItem(Item itemIn) {
      return itemIn == Items.CARROT || itemIn == Items.GOLDEN_CARROT || itemIn == Blocks.DANDELION.asItem();
   }

   public EntityRabbit createChild(EntityAgeable ageable) {
      EntityRabbit entityrabbit = new EntityRabbit(this.world);
      int i = this.getRandomRabbitType();
      if (this.rand.nextInt(20) != 0) {
         if (ageable instanceof EntityRabbit && this.rand.nextBoolean()) {
            i = ((EntityRabbit)ageable).getRabbitType();
         } else {
            i = this.getRabbitType();
         }
      }

      entityrabbit.setRabbitType(i);
      return entityrabbit;
   }

   /**
    * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
    * the animal type)
    */
   public boolean isBreedingItem(ItemStack stack) {
      return this.isRabbitBreedingItem(stack.getItem());
   }

   public int getRabbitType() {
      return this.dataManager.get(RABBIT_TYPE);
   }

   public void setRabbitType(int rabbitTypeId) {
      if (rabbitTypeId == 99) {
         this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(8.0D);
         this.tasks.addTask(4, new EntityRabbit.AIEvilAttack(this));
         this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
         this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
         this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityWolf.class, true));
         if (!this.hasCustomName()) {
            this.setCustomName(new TextComponentTranslation(Util.makeTranslationKey("entity", field_200611_bx)));
         }
      }

      this.dataManager.set(RABBIT_TYPE, rabbitTypeId);
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData entityLivingData, @Nullable NBTTagCompound itemNbt) {
      entityLivingData = super.onInitialSpawn(difficulty, entityLivingData, itemNbt);
      int i = this.getRandomRabbitType();
      boolean flag = false;
      if (entityLivingData instanceof EntityRabbit.RabbitTypeData) {
         i = ((EntityRabbit.RabbitTypeData)entityLivingData).typeData;
         flag = true;
      } else {
         entityLivingData = new EntityRabbit.RabbitTypeData(i);
      }

      this.setRabbitType(i);
      if (flag) {
         this.setGrowingAge(-24000);
      }

      return entityLivingData;
   }

   private int getRandomRabbitType() {
      Biome biome = this.world.getBiome(new BlockPos(this));
      int i = this.rand.nextInt(100);
      if (biome.getPrecipitation() == Biome.RainType.SNOW) {
         return i < 80 ? 1 : 3;
      } else if (biome.getCategory() == Biome.Category.DESERT) {
         return 4;
      } else {
         return i < 50 ? 0 : (i < 90 ? 5 : 2);
      }
   }

   public boolean canSpawn(IWorld worldIn, boolean p_205020_2_) {
      int i = MathHelper.floor(this.posX);
      int j = MathHelper.floor(this.getBoundingBox().minY);
      int k = MathHelper.floor(this.posZ);
      BlockPos blockpos = new BlockPos(i, j, k);
      Block block = worldIn.getBlockState(blockpos.down()).getBlock();
      return block != Blocks.GRASS_BLOCK && block != Blocks.SNOW && block != Blocks.SAND ? super.canSpawn(worldIn, p_205020_2_) : true;
   }

   /**
    * Returns true if {@link net.minecraft.entity.passive.EntityRabbit#carrotTicks carrotTicks} has reached zero
    */
   private boolean isCarrotEaten() {
      return this.carrotTicks == 0;
   }

   /**
    * Handler for {@link World#setEntityState}
    */
   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 1) {
         this.createRunningParticles();
         this.jumpDuration = 10;
         this.jumpTicks = 0;
      } else {
         super.handleStatusUpdate(id);
      }

   }

   static class AIAvoidEntity<T extends Entity> extends EntityAIAvoidEntity<T> {
      private final EntityRabbit rabbit;

      public AIAvoidEntity(EntityRabbit rabbit, Class<T> p_i46403_2_, float p_i46403_3_, double p_i46403_4_, double p_i46403_6_) {
         super(rabbit, p_i46403_2_, p_i46403_3_, p_i46403_4_, p_i46403_6_);
         this.rabbit = rabbit;
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         return this.rabbit.getRabbitType() != 99 && super.shouldExecute();
      }
   }

   static class AIEvilAttack extends EntityAIAttackMelee {
      public AIEvilAttack(EntityRabbit rabbit) {
         super(rabbit, 1.4D, true);
      }

      protected double getAttackReachSqr(EntityLivingBase attackTarget) {
         return (double)(4.0F + attackTarget.width);
      }
   }

   static class AIPanic extends EntityAIPanic {
      private final EntityRabbit rabbit;

      public AIPanic(EntityRabbit rabbit, double speedIn) {
         super(rabbit, speedIn);
         this.rabbit = rabbit;
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         super.tick();
         this.rabbit.setMovementSpeed(this.speed);
      }
   }

   static class AIRaidFarm extends EntityAIMoveToBlock {
      private final EntityRabbit rabbit;
      private boolean wantsToRaid;
      private boolean canRaid;

      public AIRaidFarm(EntityRabbit rabbitIn) {
         super(rabbitIn, (double)0.7F, 16);
         this.rabbit = rabbitIn;
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         if (this.runDelay <= 0) {
            if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.rabbit.world, this.rabbit)) {
               return false;
            }

            this.canRaid = false;
            this.wantsToRaid = this.rabbit.isCarrotEaten();
            this.wantsToRaid = true;
         }

         return super.shouldExecute();
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return this.canRaid && super.shouldContinueExecuting();
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         super.tick();
         this.rabbit.getLookHelper().setLookPosition((double)this.destinationBlock.getX() + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)this.destinationBlock.getZ() + 0.5D, 10.0F, (float)this.rabbit.getVerticalFaceSpeed());
         if (this.getIsAboveDestination()) {
            World world = this.rabbit.world;
            BlockPos blockpos = this.destinationBlock.up();
            IBlockState iblockstate = world.getBlockState(blockpos);
            Block block = iblockstate.getBlock();
            if (this.canRaid && block instanceof BlockCarrot) {
               Integer integer = iblockstate.get(BlockCarrot.AGE);
               if (integer == 0) {
                  world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 2);
                  world.destroyBlock(blockpos, true);
               } else {
                  world.setBlockState(blockpos, iblockstate.with(BlockCarrot.AGE, Integer.valueOf(integer - 1)), 2);
                  world.playEvent(2001, blockpos, Block.getStateId(iblockstate));
               }

               this.rabbit.carrotTicks = 40;
            }

            this.canRaid = false;
            this.runDelay = 10;
         }

      }

      /**
       * Return true to set given position as destination
       */
      protected boolean shouldMoveTo(IWorldReaderBase worldIn, BlockPos pos) {
         Block block = worldIn.getBlockState(pos).getBlock();
         if (block == Blocks.FARMLAND && this.wantsToRaid && !this.canRaid) {
            pos = pos.up();
            IBlockState iblockstate = worldIn.getBlockState(pos);
            block = iblockstate.getBlock();
            if (block instanceof BlockCarrot && ((BlockCarrot)block).isMaxAge(iblockstate)) {
               this.canRaid = true;
               return true;
            }
         }

         return false;
      }
   }

   public class RabbitJumpHelper extends EntityJumpHelper {
      private final EntityRabbit rabbit;
      private boolean canJump;

      public RabbitJumpHelper(EntityRabbit rabbit) {
         super(rabbit);
         this.rabbit = rabbit;
      }

      public boolean getIsJumping() {
         return this.isJumping;
      }

      public boolean canJump() {
         return this.canJump;
      }

      public void setCanJump(boolean canJumpIn) {
         this.canJump = canJumpIn;
      }

      /**
       * Called to actually make the entity jump if isJumping is true.
       */
      public void tick() {
         if (this.isJumping) {
            this.rabbit.startJumping();
            this.isJumping = false;
         }

      }
   }

   static class RabbitMoveHelper extends EntityMoveHelper {
      private final EntityRabbit rabbit;
      private double nextJumpSpeed;

      public RabbitMoveHelper(EntityRabbit rabbit) {
         super(rabbit);
         this.rabbit = rabbit;
      }

      public void tick() {
         if (this.rabbit.onGround && !this.rabbit.isJumping && !((EntityRabbit.RabbitJumpHelper)this.rabbit.jumpHelper).getIsJumping()) {
            this.rabbit.setMovementSpeed(0.0D);
         } else if (this.isUpdating()) {
            this.rabbit.setMovementSpeed(this.nextJumpSpeed);
         }

         super.tick();
      }

      /**
       * Sets the speed and location to move to
       */
      public void setMoveTo(double x, double y, double z, double speedIn) {
         if (this.rabbit.isInWater()) {
            speedIn = 1.5D;
         }

         super.setMoveTo(x, y, z, speedIn);
         if (speedIn > 0.0D) {
            this.nextJumpSpeed = speedIn;
         }

      }
   }

   public static class RabbitTypeData implements IEntityLivingData {
      public int typeData;

      public RabbitTypeData(int type) {
         this.typeData = type;
      }
   }
}