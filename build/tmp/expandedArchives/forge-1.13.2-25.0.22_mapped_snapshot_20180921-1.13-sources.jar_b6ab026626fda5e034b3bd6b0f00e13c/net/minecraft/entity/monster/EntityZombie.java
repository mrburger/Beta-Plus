package net.minecraft.entity.monster;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBreakBlock;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIZombieAttack;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityTurtle;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityZombie extends EntityMob {
   /** The attribute which determines the chance that this mob will spawn reinforcements */
   protected static final IAttribute SPAWN_REINFORCEMENTS_CHANCE = (new RangedAttribute((IAttribute)null, "zombie.spawnReinforcements", 0.0D, 0.0D, 1.0D)).setDescription("Spawn Reinforcements Chance");
   private static final UUID BABY_SPEED_BOOST_ID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
   private static final AttributeModifier BABY_SPEED_BOOST = new AttributeModifier(BABY_SPEED_BOOST_ID, "Baby speed boost", 0.5D, 1);
   private static final DataParameter<Boolean> IS_CHILD = EntityDataManager.createKey(EntityZombie.class, DataSerializers.BOOLEAN);
   /**
    * Was the type of villager for zombie villagers prior to 1.11. Now unused. Use {@link
    * EntityZombieVillager#PROFESSION} instead.
    */
   private static final DataParameter<Integer> VILLAGER_TYPE = EntityDataManager.createKey(EntityZombie.class, DataSerializers.VARINT);
   private static final DataParameter<Boolean> ARMS_RAISED = EntityDataManager.createKey(EntityZombie.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> DROWNING = EntityDataManager.createKey(EntityZombie.class, DataSerializers.BOOLEAN);
   private final EntityAIBreakDoor breakDoor = new EntityAIBreakDoor(this);
   private boolean isBreakDoorsTaskSet;
   private int inWaterTime;
   private int drownedConversionTime;
   /** The width of the entity */
   private float zombieWidth = -1.0F;
   /** The height of the the entity. */
   private float zombieHeight;

   public EntityZombie(EntityType<?> type, World p_i48549_2_) {
      super(type, p_i48549_2_);
      this.setSize(0.6F, 1.95F);
   }

   public EntityZombie(World worldIn) {
      this(EntityType.ZOMBIE, worldIn);
   }

   protected void initEntityAI() {
      this.tasks.addTask(4, new EntityZombie.AIAttackTurtleEgg(Blocks.TURTLE_EGG, this, 1.0D, 3));
      this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.applyEntityAI();
   }

   protected void applyEntityAI() {
      this.tasks.addTask(2, new EntityAIZombieAttack(this, 1.0D, false));
      this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
      this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, EntityPigZombie.class));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityVillager.class, false));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityIronGolem.class, true));
      this.targetTasks.addTask(5, new EntityAINearestAttackableTarget<>(this, EntityTurtle.class, 10, true, false, EntityTurtle.TARGET_DRY_BABY));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.23F);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
      this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
      this.getAttributeMap().registerAttribute(SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(this.rand.nextDouble() * net.minecraftforge.common.ForgeConfig.SERVER.zombieBaseSummonChance.get());
   }

   protected void registerData() {
      super.registerData();
      this.getDataManager().register(IS_CHILD, false);
      this.getDataManager().register(VILLAGER_TYPE, 0);
      this.getDataManager().register(ARMS_RAISED, false);
      this.getDataManager().register(DROWNING, false);
   }

   public boolean isDrowning() {
      return this.getDataManager().get(DROWNING);
   }

   public void setSwingingArms(boolean swingingArms) {
      this.getDataManager().set(ARMS_RAISED, swingingArms);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isArmsRaised() {
      return this.getDataManager().get(ARMS_RAISED);
   }

   public boolean isBreakDoorsTaskSet() {
      return this.isBreakDoorsTaskSet;
   }

   /**
    * Sets or removes EntityAIBreakDoor task
    */
   public void setBreakDoorsAItask(boolean enabled) {
      if (this.canBreakDoors()) {
         if (this.isBreakDoorsTaskSet != enabled) {
            this.isBreakDoorsTaskSet = enabled;
            ((PathNavigateGround)this.getNavigator()).setBreakDoors(enabled);
            if (enabled) {
               this.tasks.addTask(1, this.breakDoor);
            } else {
               this.tasks.removeTask(this.breakDoor);
            }
         }
      } else if (this.isBreakDoorsTaskSet) {
         this.tasks.removeTask(this.breakDoor);
         this.isBreakDoorsTaskSet = false;
      }

   }

   protected boolean canBreakDoors() {
      return true;
   }

   /**
    * If Animal, checks if the age timer is negative
    */
   public boolean isChild() {
      return this.getDataManager().get(IS_CHILD);
   }

   /**
    * Get the experience points the entity currently has.
    */
   protected int getExperiencePoints(EntityPlayer player) {
      if (this.isChild()) {
         this.experienceValue = (int)((float)this.experienceValue * 2.5F);
      }

      return super.getExperiencePoints(player);
   }

   /**
    * Set whether this zombie is a child.
    */
   public void setChild(boolean childZombie) {
      this.getDataManager().set(IS_CHILD, childZombie);
      if (this.world != null && !this.world.isRemote) {
         IAttributeInstance iattributeinstance = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
         iattributeinstance.removeModifier(BABY_SPEED_BOOST);
         if (childZombie) {
            iattributeinstance.applyModifier(BABY_SPEED_BOOST);
         }
      }

      this.setChildSize(childZombie);
   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      if (IS_CHILD.equals(key)) {
         this.setChildSize(this.isChild());
      }

      super.notifyDataManagerChange(key);
   }

   protected boolean shouldDrown() {
      return true;
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      if (!this.world.isRemote) {
         if (this.isDrowning()) {
            --this.drownedConversionTime;
            if (this.drownedConversionTime < 0) {
               this.onDrowned();
            }
         } else if (this.shouldDrown()) {
            if (this.areEyesInFluid(FluidTags.WATER)) {
               ++this.inWaterTime;
               if (this.inWaterTime >= 600) {
                  this.startDrowning(300);
               }
            } else {
               this.inWaterTime = -1;
            }
         }
      }

      super.tick();
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      boolean flag = this.shouldBurnInDay() && this.isInDaylight();
      if (flag) {
         ItemStack itemstack = this.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
         if (!itemstack.isEmpty()) {
            if (itemstack.isDamageable()) {
               itemstack.setDamage(itemstack.getDamage() + this.rand.nextInt(2));
               if (itemstack.getDamage() >= itemstack.getMaxDamage()) {
                  this.renderBrokenItemStack(itemstack);
                  this.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
               }
            }

            flag = false;
         }

         if (flag) {
            this.setFire(8);
         }
      }

      super.livingTick();
   }

   private void startDrowning(int p_204704_1_) {
      this.drownedConversionTime = p_204704_1_;
      this.getDataManager().set(DROWNING, true);
   }

   protected void onDrowned() {
      this.convertInto(new EntityDrowned(this.world));
      this.world.playEvent((EntityPlayer)null, 1040, new BlockPos((int)this.posX, (int)this.posY, (int)this.posZ), 0);
   }

   protected void convertInto(EntityZombie p_207305_1_) {
      if (!this.world.isRemote && !this.removed) {
         p_207305_1_.copyLocationAndAnglesFrom(this);
         p_207305_1_.func_207301_a(this.canPickUpLoot(), this.isBreakDoorsTaskSet(), this.isChild(), this.isAIDisabled());

         for(EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values()) {
            ItemStack itemstack = this.getItemStackFromSlot(entityequipmentslot);
            if (!itemstack.isEmpty()) {
               p_207305_1_.setItemStackToSlot(entityequipmentslot, itemstack);
               p_207305_1_.setDropChance(entityequipmentslot, this.getDropChance(entityequipmentslot));
            }
         }

         if (this.hasCustomName()) {
            p_207305_1_.setCustomName(this.getCustomName());
            p_207305_1_.setCustomNameVisible(this.isCustomNameVisible());
         }

         this.world.spawnEntity(p_207305_1_);
         this.remove();
      }
   }

   protected boolean shouldBurnInDay() {
      return true;
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (super.attackEntityFrom(source, amount)) {
         EntityLivingBase entitylivingbase = this.getAttackTarget();
         if (entitylivingbase == null && source.getTrueSource() instanceof EntityLivingBase) {
            entitylivingbase = (EntityLivingBase)source.getTrueSource();
         }

            int i = MathHelper.floor(this.posX);
            int j = MathHelper.floor(this.posY);
            int k = MathHelper.floor(this.posZ);
        net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent event = net.minecraftforge.event.ForgeEventFactory.fireZombieSummonAid(this, world, i, j, k, entitylivingbase, this.getAttribute(SPAWN_REINFORCEMENTS_CHANCE).getValue());
        if (event.getResult() == net.minecraftforge.eventbus.api.Event.Result.DENY) return true;

        if (event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW  ||
            entitylivingbase != null && this.world.getDifficulty() == EnumDifficulty.HARD && (double)this.rand.nextFloat() < this.getAttribute(SPAWN_REINFORCEMENTS_CHANCE).getValue() && this.world.getGameRules().getBoolean("doMobSpawning")) {
            EntityZombie entityzombie = event.getCustomSummonedAid() != null && event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW ? event.getCustomSummonedAid() : new EntityZombie(this.world);

            for(int l = 0; l < 50; ++l) {
               int i1 = i + MathHelper.nextInt(this.rand, 7, 40) * MathHelper.nextInt(this.rand, -1, 1);
               int j1 = j + MathHelper.nextInt(this.rand, 7, 40) * MathHelper.nextInt(this.rand, -1, 1);
               int k1 = k + MathHelper.nextInt(this.rand, 7, 40) * MathHelper.nextInt(this.rand, -1, 1);
               if (this.world.isTopSolid(new BlockPos(i1, j1 - 1, k1)) && this.world.getLight(new BlockPos(i1, j1, k1)) < 10) {
                  entityzombie.setPosition((double)i1, (double)j1, (double)k1);
                  if (!this.world.isAnyPlayerWithinRangeAt((double)i1, (double)j1, (double)k1, 7.0D) && this.world.checkNoEntityCollision(entityzombie, entityzombie.getBoundingBox()) && this.world.isCollisionBoxesEmpty(entityzombie, entityzombie.getBoundingBox()) && !this.world.containsAnyLiquid(entityzombie.getBoundingBox())) {
                     this.world.spawnEntity(entityzombie);
                     if (entitylivingbase != null)
                     entityzombie.setAttackTarget(entitylivingbase);
                     entityzombie.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(entityzombie)), (IEntityLivingData)null, (NBTTagCompound)null);
                     this.getAttribute(SPAWN_REINFORCEMENTS_CHANCE).applyModifier(new AttributeModifier("Zombie reinforcement caller charge", (double)-0.05F, 0));
                     entityzombie.getAttribute(SPAWN_REINFORCEMENTS_CHANCE).applyModifier(new AttributeModifier("Zombie reinforcement callee charge", (double)-0.05F, 0));
                     break;
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag) {
         float f = this.world.getDifficultyForLocation(new BlockPos(this)).getAdditionalDifficulty();
         if (this.getHeldItemMainhand().isEmpty() && this.isBurning() && this.rand.nextFloat() < f * 0.3F) {
            entityIn.setFire(2 * (int)f);
         }
      }

      return flag;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_ZOMBIE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ZOMBIE_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_ZOMBIE_STEP;
   }

   protected void playStepSound(BlockPos pos, IBlockState blockIn) {
      this.playSound(this.getStepSound(), 0.15F, 1.0F);
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.UNDEAD;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_ZOMBIE;
   }

   /**
    * Gives armor or weapon for entity based on given DifficultyInstance
    */
   protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
      super.setEquipmentBasedOnDifficulty(difficulty);
      if (this.rand.nextFloat() < (this.world.getDifficulty() == EnumDifficulty.HARD ? 0.05F : 0.01F)) {
         int i = this.rand.nextInt(3);
         if (i == 0) {
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
         } else {
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
         }
      }

   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      if (this.isChild()) {
         compound.setBoolean("IsBaby", true);
      }

      compound.setBoolean("CanBreakDoors", this.isBreakDoorsTaskSet());
      compound.setInt("InWaterTime", this.isInWater() ? this.inWaterTime : -1);
      compound.setInt("DrownedConversionTime", this.isDrowning() ? this.drownedConversionTime : -1);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      if (compound.getBoolean("IsBaby")) {
         this.setChild(true);
      }

      this.setBreakDoorsAItask(compound.getBoolean("CanBreakDoors"));
      this.inWaterTime = compound.getInt("InWaterTime");
      if (compound.contains("DrownedConversionTime", 99) && compound.getInt("DrownedConversionTime") > -1) {
         this.startDrowning(compound.getInt("DrownedConversionTime"));
      }

   }

   /**
    * This method gets called when the entity kills another one.
    */
   public void onKillEntity(EntityLivingBase entityLivingIn) {
      super.onKillEntity(entityLivingIn);
      if ((this.world.getDifficulty() == EnumDifficulty.NORMAL || this.world.getDifficulty() == EnumDifficulty.HARD) && entityLivingIn instanceof EntityVillager) {
         if (this.world.getDifficulty() != EnumDifficulty.HARD && this.rand.nextBoolean()) {
            return;
         }

         EntityVillager entityvillager = (EntityVillager)entityLivingIn;
         EntityZombieVillager entityzombievillager = new EntityZombieVillager(this.world);
         entityzombievillager.copyLocationAndAnglesFrom(entityvillager);
         this.world.removeEntity(entityvillager);
         entityzombievillager.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(entityzombievillager)), new EntityZombie.GroupData(false), (NBTTagCompound)null);
         entityzombievillager.setProfession(entityvillager.getProfessionForge());
         entityzombievillager.setChild(entityvillager.isChild());
         entityzombievillager.setNoAI(entityvillager.isAIDisabled());
         if (entityvillager.hasCustomName()) {
            entityzombievillager.setCustomName(entityvillager.getCustomName());
            entityzombievillager.setCustomNameVisible(entityvillager.isCustomNameVisible());
         }

         this.world.spawnEntity(entityzombievillager);
         this.world.playEvent((EntityPlayer)null, 1026, new BlockPos(this), 0);
      }

   }

   public float getEyeHeight() {
      float f = 1.74F;
      if (this.isChild()) {
         f = (float)((double)f - 0.81D);
      }

      return f;
   }

   protected boolean canEquipItem(ItemStack stack) {
      return stack.getItem() == Items.EGG && this.isChild() && this.isPassenger() ? false : super.canEquipItem(stack);
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData entityLivingData, @Nullable NBTTagCompound itemNbt) {
      entityLivingData = super.onInitialSpawn(difficulty, entityLivingData, itemNbt);
      float f = difficulty.getClampedAdditionalDifficulty();
      this.setCanPickUpLoot(this.rand.nextFloat() < 0.55F * f);
      if (entityLivingData == null) {
         entityLivingData = new EntityZombie.GroupData(this.world.rand.nextFloat() < net.minecraftforge.common.ForgeConfig.SERVER.zombieBabyChance.get());
      }

      if (entityLivingData instanceof EntityZombie.GroupData) {
         EntityZombie.GroupData entityzombie$groupdata = (EntityZombie.GroupData)entityLivingData;
         if (entityzombie$groupdata.isChild) {
            this.setChild(true);
            if ((double)this.world.rand.nextFloat() < 0.05D) {
               List<EntityChicken> list = this.world.getEntitiesWithinAABB(EntityChicken.class, this.getBoundingBox().grow(5.0D, 3.0D, 5.0D), EntitySelectors.IS_STANDALONE);
               if (!list.isEmpty()) {
                  EntityChicken entitychicken = list.get(0);
                  entitychicken.setChickenJockey(true);
                  this.startRiding(entitychicken);
               }
            } else if ((double)this.world.rand.nextFloat() < 0.05D) {
               EntityChicken entitychicken1 = new EntityChicken(this.world);
               entitychicken1.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
               entitychicken1.onInitialSpawn(difficulty, (IEntityLivingData)null, (NBTTagCompound)null);
               entitychicken1.setChickenJockey(true);
               this.world.spawnEntity(entitychicken1);
               this.startRiding(entitychicken1);
            }
         }

         this.setBreakDoorsAItask(this.canBreakDoors() && this.rand.nextFloat() < f * 0.1F);
         this.setEquipmentBasedOnDifficulty(difficulty);
         this.setEnchantmentBasedOnDifficulty(difficulty);
      }

      if (this.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty()) {
         LocalDate localdate = LocalDate.now();
         int i = localdate.get(ChronoField.DAY_OF_MONTH);
         int j = localdate.get(ChronoField.MONTH_OF_YEAR);
         if (j == 10 && i == 31 && this.rand.nextFloat() < 0.25F) {
            this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(this.rand.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
            this.inventoryArmorDropChances[EntityEquipmentSlot.HEAD.getIndex()] = 0.0F;
         }
      }

      this.applyAttributeBonuses(f);
      return entityLivingData;
   }

   protected void func_207301_a(boolean p_207301_1_, boolean p_207301_2_, boolean p_207301_3_, boolean p_207301_4_) {
      this.setCanPickUpLoot(p_207301_1_);
      this.setBreakDoorsAItask(this.canBreakDoors() && p_207301_2_);
      this.applyAttributeBonuses(this.world.getDifficultyForLocation(new BlockPos(this)).getClampedAdditionalDifficulty());
      this.setChild(p_207301_3_);
      this.setNoAI(p_207301_4_);
   }

   protected void applyAttributeBonuses(float difficulty) {
      this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextDouble() * (double)0.05F, 0));
      double d0 = this.rand.nextDouble() * 1.5D * (double)difficulty;
      if (d0 > 1.0D) {
         this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(new AttributeModifier("Random zombie-spawn bonus", d0, 2));
      }

      if (this.rand.nextFloat() < difficulty * 0.05F) {
         this.getAttribute(SPAWN_REINFORCEMENTS_CHANCE).applyModifier(new AttributeModifier("Leader zombie bonus", this.rand.nextDouble() * 0.25D + 0.5D, 0));
         this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier("Leader zombie bonus", this.rand.nextDouble() * 3.0D + 1.0D, 2));
         this.setBreakDoorsAItask(this.canBreakDoors());
      }

   }

   /**
    * sets the size of the entity to be half of its current size if true.
    */
   public void setChildSize(boolean isChild) {
      this.multiplySize(isChild ? 0.5F : 1.0F);
   }

   /**
    * Sets the width and height of the entity.
    */
   protected final void setSize(float width, float height) {
      boolean flag = this.zombieWidth > 0.0F && this.zombieHeight > 0.0F;
      this.zombieWidth = width;
      this.zombieHeight = height;
      if (!flag) {
         this.multiplySize(1.0F);
      }

   }

   /**
    * Multiplies the height and width by the provided float.
    */
   protected final void multiplySize(float size) {
      super.setSize(this.zombieWidth * size, this.zombieHeight * size);
   }

   /**
    * Returns the Y Offset of this entity.
    */
   public double getYOffset() {
      return this.isChild() ? 0.0D : -0.45D;
   }

   /**
    * Called when the mob's health reaches 0.
    */
   public void onDeath(DamageSource cause) {
      super.onDeath(cause);
      if (cause.getTrueSource() instanceof EntityCreeper) {
         EntityCreeper entitycreeper = (EntityCreeper)cause.getTrueSource();
         if (entitycreeper.getPowered() && entitycreeper.ableToCauseSkullDrop()) {
            entitycreeper.incrementDroppedSkulls();
            ItemStack itemstack = this.getSkullDrop();
            if (!itemstack.isEmpty()) {
               this.entityDropItem(itemstack);
            }
         }
      }

   }

   protected ItemStack getSkullDrop() {
      return new ItemStack(Items.ZOMBIE_HEAD);
   }

   class AIAttackTurtleEgg extends EntityAIBreakBlock {
      AIAttackTurtleEgg(Block p_i48792_2_, EntityCreature p_i48792_3_, double p_i48792_4_, int p_i48792_6_) {
         super(p_i48792_2_, p_i48792_3_, p_i48792_4_, p_i48792_6_);
      }

      public void playBreakingSound(IWorld p_203114_1_, BlockPos p_203114_2_) {
         p_203114_1_.playSound((EntityPlayer)null, p_203114_2_, SoundEvents.ENTITY_ZOMBIE_DESTROY_EGG, SoundCategory.HOSTILE, 0.5F, 0.9F + EntityZombie.this.rand.nextFloat() * 0.2F);
      }

      public void playBrokenSound(World p_203116_1_, BlockPos p_203116_2_) {
         p_203116_1_.playSound((EntityPlayer)null, p_203116_2_, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + p_203116_1_.rand.nextFloat() * 0.2F);
      }

      public double getTargetDistanceSq() {
         return 1.3D;
      }
   }

   public class GroupData implements IEntityLivingData {
      public boolean isChild;

      private GroupData(boolean p_i47328_2_) {
         this.isChild = p_i47328_2_;
      }
   }
}