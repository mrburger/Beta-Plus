package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockAbstractSkull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.Tag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class EntityLiving extends EntityLivingBase {
   /** First bit used for AI disabling, second bit used for marking entity as left handed */
   private static final DataParameter<Byte> AI_FLAGS = EntityDataManager.createKey(EntityLiving.class, DataSerializers.BYTE);
   /** Number of ticks since this EntityLiving last produced its sound */
   public int livingSoundTime;
   /** The experience points the Entity gives. */
   protected int experienceValue;
   protected EntityLookHelper lookHelper;
   protected EntityMoveHelper moveHelper;
   /** Entity jumping helper */
   protected EntityJumpHelper jumpHelper;
   private final EntityBodyHelper bodyHelper;
   protected PathNavigate navigator;
   /** Active AI tasks (moving, looking, attack the target selected by {@link #targetTasks}, etc.) */
   public final EntityAITasks tasks;
   /** (Usually one-shot) tasks used to select an attack target */
   public final EntityAITasks targetTasks;
   /** The active target the Task system uses for tracking */
   private EntityLivingBase attackTarget;
   private final EntitySenses senses;
   private final NonNullList<ItemStack> inventoryHands = NonNullList.withSize(2, ItemStack.EMPTY);
   /** Chances for equipment in hands dropping when this entity dies. */
   protected float[] inventoryHandsDropChances = new float[2];
   private final NonNullList<ItemStack> inventoryArmor = NonNullList.withSize(4, ItemStack.EMPTY);
   /** Chances for armor dropping when this entity dies. */
   protected float[] inventoryArmorDropChances = new float[4];
   /** Whether this entity can pick up items from the ground. */
   private boolean canPickUpLoot;
   /** Whether this entity should NOT despawn. */
   private boolean persistenceRequired;
   private final Map<PathNodeType, Float> mapPathPriority = Maps.newEnumMap(PathNodeType.class);
   private ResourceLocation deathLootTable;
   private long deathLootTableSeed;
   private boolean isLeashed;
   private Entity leashHolder;
   private NBTTagCompound leashNBTTag;

   protected EntityLiving(EntityType<?> type, World worldIn) {
      super(type, worldIn);
      this.tasks = new EntityAITasks(worldIn != null && worldIn.profiler != null ? worldIn.profiler : null);
      this.targetTasks = new EntityAITasks(worldIn != null && worldIn.profiler != null ? worldIn.profiler : null);
      this.lookHelper = new EntityLookHelper(this);
      this.moveHelper = new EntityMoveHelper(this);
      this.jumpHelper = new EntityJumpHelper(this);
      this.bodyHelper = this.createBodyHelper();
      this.navigator = this.createNavigator(worldIn);
      this.senses = new EntitySenses(this);
      Arrays.fill(this.inventoryArmorDropChances, 0.085F);
      Arrays.fill(this.inventoryHandsDropChances, 0.085F);
      if (worldIn != null && !worldIn.isRemote) {
         this.initEntityAI();
      }

   }

   protected void initEntityAI() {
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
   }

   /**
    * Returns new PathNavigateGround instance
    */
   protected PathNavigate createNavigator(World worldIn) {
      return new PathNavigateGround(this, worldIn);
   }

   public float getPathPriority(PathNodeType nodeType) {
      Float f = this.mapPathPriority.get(nodeType);
      return f == null ? nodeType.getPriority() : f;
   }

   public void setPathPriority(PathNodeType nodeType, float priority) {
      this.mapPathPriority.put(nodeType, priority);
   }

   protected EntityBodyHelper createBodyHelper() {
      return new EntityBodyHelper(this);
   }

   public EntityLookHelper getLookHelper() {
      return this.lookHelper;
   }

   public EntityMoveHelper getMoveHelper() {
      return this.moveHelper;
   }

   public EntityJumpHelper getJumpHelper() {
      return this.jumpHelper;
   }

   public PathNavigate getNavigator() {
      return this.navigator;
   }

   /**
    * returns the EntitySenses Object for the EntityLiving
    */
   public EntitySenses getEntitySenses() {
      return this.senses;
   }

   /**
    * Gets the active target the Task system uses for tracking
    */
   @Nullable
   public EntityLivingBase getAttackTarget() {
      return this.attackTarget;
   }

   /**
    * Sets the active target the Task system uses for tracking
    */
   public void setAttackTarget(@Nullable EntityLivingBase entitylivingbaseIn) {
      this.attackTarget = entitylivingbaseIn;
      net.minecraftforge.common.ForgeHooks.onLivingSetAttackTarget(this, entitylivingbaseIn);
   }

   /**
    * Returns true if this entity can attack entities of the specified class.
    */
   public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
      return cls != EntityGhast.class;
   }

   /**
    * This function applies the benefits of growing back wool and faster growing up to the acting entity. (This function
    * is used in the AIEatGrass)
    */
   public void eatGrassBonus() {
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(AI_FLAGS, (byte)0);
   }

   /**
    * Get number of ticks, at least during which the living entity will be silent.
    */
   public int getTalkInterval() {
      return 80;
   }

   /**
    * Plays living's sound at its position
    */
   public void playAmbientSound() {
      SoundEvent soundevent = this.getAmbientSound();
      if (soundevent != null) {
         this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
      }

   }

   /**
    * Gets called every tick from main Entity class
    */
   public void baseTick() {
      super.baseTick();
      this.world.profiler.startSection("mobBaseTick");
      if (this.isAlive() && this.rand.nextInt(1000) < this.livingSoundTime++) {
         this.applyEntityAI();
         this.playAmbientSound();
      }

      this.world.profiler.endSection();
   }

   protected void playHurtSound(DamageSource source) {
      this.applyEntityAI();
      super.playHurtSound(source);
   }

   private void applyEntityAI() {
      this.livingSoundTime = -this.getTalkInterval();
   }

   /**
    * Get the experience points the entity currently has.
    */
   protected int getExperiencePoints(EntityPlayer player) {
      if (this.experienceValue > 0) {
         int i = this.experienceValue;

         for(int j = 0; j < this.inventoryArmor.size(); ++j) {
            if (!this.inventoryArmor.get(j).isEmpty() && this.inventoryArmorDropChances[j] <= 1.0F) {
               i += 1 + this.rand.nextInt(3);
            }
         }

         for(int k = 0; k < this.inventoryHands.size(); ++k) {
            if (!this.inventoryHands.get(k).isEmpty() && this.inventoryHandsDropChances[k] <= 1.0F) {
               i += 1 + this.rand.nextInt(3);
            }
         }

         return i;
      } else {
         return this.experienceValue;
      }
   }

   /**
    * Spawns an explosion particle around the Entity's location
    */
   public void spawnExplosionParticle() {
      if (this.world.isRemote) {
         for(int i = 0; i < 20; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            double d3 = 10.0D;
            this.world.spawnParticle(Particles.POOF, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width - d0 * 10.0D, this.posY + (double)(this.rand.nextFloat() * this.height) - d1 * 10.0D, this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width - d2 * 10.0D, d0, d1, d2);
         }
      } else {
         this.world.setEntityState(this, (byte)20);
      }

   }

   /**
    * Handler for {@link World#setEntityState}
    */
   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 20) {
         this.spawnExplosionParticle();
      } else {
         super.handleStatusUpdate(id);
      }

   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (!this.world.isRemote) {
         this.updateLeashedState();
         if (this.ticksExisted % 5 == 0) {
            boolean flag = !(this.getControllingPassenger() instanceof EntityLiving);
            boolean flag1 = !(this.getRidingEntity() instanceof EntityBoat);
            this.tasks.setControlFlag(1, flag);
            this.tasks.setControlFlag(4, flag && flag1);
            this.tasks.setControlFlag(2, flag);
         }
      }

   }

   protected float updateDistance(float p_110146_1_, float p_110146_2_) {
      this.bodyHelper.updateRenderAngles();
      return p_110146_2_;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return null;
   }

   @Nullable
   protected Item getDropItem() {
      return null;
   }

   /**
    * Drop 0-2 items of this living's type
    */
   protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      Item item = this.getDropItem();
      if (item != null) {
         int i = this.rand.nextInt(3);
         if (lootingModifier > 0) {
            i += this.rand.nextInt(lootingModifier + 1);
         }

         for(int j = 0; j < i; ++j) {
            this.entityDropItem(item);
         }
      }

   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      compound.setBoolean("CanPickUpLoot", this.canPickUpLoot());
      compound.setBoolean("PersistenceRequired", this.persistenceRequired);
      NBTTagList nbttaglist = new NBTTagList();

      for(ItemStack itemstack : this.inventoryArmor) {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         if (!itemstack.isEmpty()) {
            itemstack.write(nbttagcompound);
         }

         nbttaglist.add((INBTBase)nbttagcompound);
      }

      compound.setTag("ArmorItems", nbttaglist);
      NBTTagList nbttaglist1 = new NBTTagList();

      for(ItemStack itemstack1 : this.inventoryHands) {
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();
         if (!itemstack1.isEmpty()) {
            itemstack1.write(nbttagcompound1);
         }

         nbttaglist1.add((INBTBase)nbttagcompound1);
      }

      compound.setTag("HandItems", nbttaglist1);
      NBTTagList nbttaglist2 = new NBTTagList();

      for(float f : this.inventoryArmorDropChances) {
         nbttaglist2.add((INBTBase)(new NBTTagFloat(f)));
      }

      compound.setTag("ArmorDropChances", nbttaglist2);
      NBTTagList nbttaglist3 = new NBTTagList();

      for(float f1 : this.inventoryHandsDropChances) {
         nbttaglist3.add((INBTBase)(new NBTTagFloat(f1)));
      }

      compound.setTag("HandDropChances", nbttaglist3);
      compound.setBoolean("Leashed", this.isLeashed);
      if (this.leashHolder != null) {
         NBTTagCompound nbttagcompound2 = new NBTTagCompound();
         if (this.leashHolder instanceof EntityLivingBase) {
            UUID uuid = this.leashHolder.getUniqueID();
            nbttagcompound2.setUniqueId("UUID", uuid);
         } else if (this.leashHolder instanceof EntityHanging) {
            BlockPos blockpos = ((EntityHanging)this.leashHolder).getHangingPosition();
            nbttagcompound2.setInt("X", blockpos.getX());
            nbttagcompound2.setInt("Y", blockpos.getY());
            nbttagcompound2.setInt("Z", blockpos.getZ());
         }

         compound.setTag("Leash", nbttagcompound2);
      }

      compound.setBoolean("LeftHanded", this.isLeftHanded());
      if (this.deathLootTable != null) {
         compound.setString("DeathLootTable", this.deathLootTable.toString());
         if (this.deathLootTableSeed != 0L) {
            compound.setLong("DeathLootTableSeed", this.deathLootTableSeed);
         }
      }

      if (this.isAIDisabled()) {
         compound.setBoolean("NoAI", this.isAIDisabled());
      }

   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      if (compound.contains("CanPickUpLoot", 1)) {
         this.setCanPickUpLoot(compound.getBoolean("CanPickUpLoot"));
      }

      this.persistenceRequired = compound.getBoolean("PersistenceRequired");
      if (compound.contains("ArmorItems", 9)) {
         NBTTagList nbttaglist = compound.getList("ArmorItems", 10);

         for(int i = 0; i < this.inventoryArmor.size(); ++i) {
            this.inventoryArmor.set(i, ItemStack.read(nbttaglist.getCompound(i)));
         }
      }

      if (compound.contains("HandItems", 9)) {
         NBTTagList nbttaglist1 = compound.getList("HandItems", 10);

         for(int j = 0; j < this.inventoryHands.size(); ++j) {
            this.inventoryHands.set(j, ItemStack.read(nbttaglist1.getCompound(j)));
         }
      }

      if (compound.contains("ArmorDropChances", 9)) {
         NBTTagList nbttaglist2 = compound.getList("ArmorDropChances", 5);

         for(int k = 0; k < nbttaglist2.size(); ++k) {
            this.inventoryArmorDropChances[k] = nbttaglist2.getFloat(k);
         }
      }

      if (compound.contains("HandDropChances", 9)) {
         NBTTagList nbttaglist3 = compound.getList("HandDropChances", 5);

         for(int l = 0; l < nbttaglist3.size(); ++l) {
            this.inventoryHandsDropChances[l] = nbttaglist3.getFloat(l);
         }
      }

      this.isLeashed = compound.getBoolean("Leashed");
      if (this.isLeashed && compound.contains("Leash", 10)) {
         this.leashNBTTag = compound.getCompound("Leash");
      }

      this.setLeftHanded(compound.getBoolean("LeftHanded"));
      if (compound.contains("DeathLootTable", 8)) {
         this.deathLootTable = new ResourceLocation(compound.getString("DeathLootTable"));
         this.deathLootTableSeed = compound.getLong("DeathLootTableSeed");
      }

      this.setNoAI(compound.getBoolean("NoAI"));
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return null;
   }

   /**
    * drops the loot of this entity upon death
    */
   protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
      ResourceLocation resourcelocation = this.deathLootTable;
      if (resourcelocation == null) {
         resourcelocation = this.getLootTable();
      }

      if (resourcelocation != null) {
         LootTable loottable = this.world.getServer().getLootTableManager().getLootTableFromLocation(resourcelocation);
         this.deathLootTable = null;
         LootContext.Builder lootcontext$builder = (new LootContext.Builder((WorldServer)this.world)).withLootedEntity(this).withDamageSource(source).withPosition(new BlockPos(this));
         if (wasRecentlyHit && this.attackingPlayer != null) {
            lootcontext$builder = lootcontext$builder.withPlayer(this.attackingPlayer).withLuck(this.attackingPlayer.getLuck());
         }

         for(ItemStack itemstack : loottable.generateLootForPools(this.deathLootTableSeed == 0L ? this.rand : new Random(this.deathLootTableSeed), lootcontext$builder.build())) {
            this.entityDropItem(itemstack);
         }

         this.dropEquipment(wasRecentlyHit, lootingModifier);
      } else {
         super.dropLoot(wasRecentlyHit, lootingModifier, source);
      }

   }

   public void setMoveForward(float amount) {
      this.moveForward = amount;
   }

   public void setMoveVertical(float amount) {
      this.moveVertical = amount;
   }

   public void setMoveStrafing(float amount) {
      this.moveStrafing = amount;
   }

   /**
    * set the movespeed used for the new AI system
    */
   public void setAIMoveSpeed(float speedIn) {
      super.setAIMoveSpeed(speedIn);
      this.setMoveForward(speedIn);
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      super.livingTick();
      this.world.profiler.startSection("looting");
      if (!this.world.isRemote && this.canPickUpLoot() && !this.dead && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this)) {
         for(EntityItem entityitem : this.world.getEntitiesWithinAABB(EntityItem.class, this.getBoundingBox().grow(1.0D, 0.0D, 1.0D))) {
            if (!entityitem.removed && !entityitem.getItem().isEmpty() && !entityitem.cannotPickup()) {
               this.updateEquipmentIfNeeded(entityitem);
            }
         }
      }

      this.world.profiler.endSection();
   }

   /**
    * Tests if this entity should pickup a weapon or an armor. Entity drops current weapon or armor if the new one is
    * better.
    */
   protected void updateEquipmentIfNeeded(EntityItem itemEntity) {
      ItemStack itemstack = itemEntity.getItem();
      EntityEquipmentSlot entityequipmentslot = getSlotForItemStack(itemstack);
      ItemStack itemstack1 = this.getItemStackFromSlot(entityequipmentslot);
      boolean flag = this.shouldExchangeEquipment(itemstack, itemstack1, entityequipmentslot);
      if (flag && this.canEquipItem(itemstack)) {
         double d0 = (double)this.getDropChance(entityequipmentslot);
         if (!itemstack1.isEmpty() && (double)(this.rand.nextFloat() - 0.1F) < d0) {
            this.entityDropItem(itemstack1);
         }

         this.setItemStackToSlot(entityequipmentslot, itemstack);
         switch(entityequipmentslot.getSlotType()) {
         case HAND:
            this.inventoryHandsDropChances[entityequipmentslot.getIndex()] = 2.0F;
            break;
         case ARMOR:
            this.inventoryArmorDropChances[entityequipmentslot.getIndex()] = 2.0F;
         }

         this.persistenceRequired = true;
         this.onItemPickup(itemEntity, itemstack.getCount());
         itemEntity.remove();
      }

   }

   protected boolean shouldExchangeEquipment(ItemStack candidate, ItemStack existing, EntityEquipmentSlot p_208003_3_) {
      boolean flag = true;
      if (!existing.isEmpty()) {
         if (p_208003_3_.getSlotType() == EntityEquipmentSlot.Type.HAND) {
            if (candidate.getItem() instanceof ItemSword && !(existing.getItem() instanceof ItemSword)) {
               flag = true;
            } else if (candidate.getItem() instanceof ItemSword && existing.getItem() instanceof ItemSword) {
               ItemSword itemsword = (ItemSword)candidate.getItem();
               ItemSword itemsword1 = (ItemSword)existing.getItem();
               if (itemsword.getAttackDamage() == itemsword1.getAttackDamage()) {
                  flag = candidate.getDamage() < existing.getDamage() || candidate.hasTag() && !existing.hasTag();
               } else {
                  flag = itemsword.getAttackDamage() > itemsword1.getAttackDamage();
               }
            } else if (candidate.getItem() instanceof ItemBow && existing.getItem() instanceof ItemBow) {
               flag = candidate.hasTag() && !existing.hasTag();
            } else {
               flag = false;
            }
         } else if (candidate.getItem() instanceof ItemArmor && !(existing.getItem() instanceof ItemArmor)) {
            flag = true;
         } else if (candidate.getItem() instanceof ItemArmor && existing.getItem() instanceof ItemArmor && !EnchantmentHelper.hasBindingCurse(existing)) {
            ItemArmor itemarmor = (ItemArmor)candidate.getItem();
            ItemArmor itemarmor1 = (ItemArmor)existing.getItem();
            if (itemarmor.getDamageReduceAmount() == itemarmor1.getDamageReduceAmount()) {
               flag = candidate.getDamage() < existing.getDamage() || candidate.hasTag() && !existing.hasTag();
            } else {
               flag = itemarmor.getDamageReduceAmount() > itemarmor1.getDamageReduceAmount();
            }
         } else {
            flag = false;
         }
      }

      return flag;
   }

   protected boolean canEquipItem(ItemStack stack) {
      return true;
   }

   /**
    * Determines if an entity can be despawned, used on idle far away entities
    */
   public boolean canDespawn() {
      return true;
   }

   /**
    * Makes the entity despawn if requirements are reached
    */
   protected void checkDespawn() {
      net.minecraftforge.eventbus.api.Event.Result result = null;
      if (this.persistenceRequired) {
         this.idleTime = 0;
      } else if ((this.idleTime & 0x1F) == 0x1F && (result = net.minecraftforge.event.ForgeEventFactory.canEntityDespawn(this)) != net.minecraftforge.eventbus.api.Event.Result.DEFAULT) {
         if (result == net.minecraftforge.eventbus.api.Event.Result.DENY)
            this.idleTime = 0;
         else
            this.remove();
      } else {
         Entity entity = this.world.getClosestPlayerToEntity(this, -1.0D);
         if (entity != null) {
            double d0 = entity.posX - this.posX;
            double d1 = entity.posY - this.posY;
            double d2 = entity.posZ - this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            if (this.canDespawn() && d3 > 16384.0D) {
               this.remove();
            }

            if (this.idleTime > 600 && this.rand.nextInt(800) == 0 && d3 > 1024.0D && this.canDespawn()) {
               this.remove();
            } else if (d3 < 1024.0D) {
               this.idleTime = 0;
            }
         }

      }
   }

   protected final void updateEntityActionState() {
      ++this.idleTime;
      this.world.profiler.startSection("checkDespawn");
      this.checkDespawn();
      this.world.profiler.endSection();
      this.world.profiler.startSection("sensing");
      this.senses.tick();
      this.world.profiler.endSection();
      this.world.profiler.startSection("targetSelector");
      this.targetTasks.tick();
      this.world.profiler.endSection();
      this.world.profiler.startSection("goalSelector");
      this.tasks.tick();
      this.world.profiler.endSection();
      this.world.profiler.startSection("navigation");
      this.navigator.tick();
      this.world.profiler.endSection();
      this.world.profiler.startSection("mob tick");
      this.updateAITasks();
      this.world.profiler.endSection();
      if (this.isPassenger() && this.getRidingEntity() instanceof EntityLiving) {
         EntityLiving entityliving = (EntityLiving)this.getRidingEntity();
         entityliving.getNavigator().setPath(this.getNavigator().getPath(), 1.5D);
         entityliving.getMoveHelper().read(this.getMoveHelper());
      }

      this.world.profiler.startSection("controls");
      this.world.profiler.startSection("move");
      this.moveHelper.tick();
      this.world.profiler.endStartSection("look");
      this.lookHelper.tick();
      this.world.profiler.endStartSection("jump");
      this.jumpHelper.tick();
      this.world.profiler.endSection();
      this.world.profiler.endSection();
   }

   protected void updateAITasks() {
   }

   /**
    * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
    * use in wolves.
    */
   public int getVerticalFaceSpeed() {
      return 40;
   }

   public int getHorizontalFaceSpeed() {
      return 10;
   }

   /**
    * Changes pitch and yaw so that the entity calling the function is facing the entity provided as an argument.
    */
   public void faceEntity(Entity entityIn, float maxYawIncrease, float maxPitchIncrease) {
      double d0 = entityIn.posX - this.posX;
      double d2 = entityIn.posZ - this.posZ;
      double d1;
      if (entityIn instanceof EntityLivingBase) {
         EntityLivingBase entitylivingbase = (EntityLivingBase)entityIn;
         d1 = entitylivingbase.posY + (double)entitylivingbase.getEyeHeight() - (this.posY + (double)this.getEyeHeight());
      } else {
         d1 = (entityIn.getBoundingBox().minY + entityIn.getBoundingBox().maxY) / 2.0D - (this.posY + (double)this.getEyeHeight());
      }

      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      float f = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
      float f1 = (float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI)));
      this.rotationPitch = this.updateRotation(this.rotationPitch, f1, maxPitchIncrease);
      this.rotationYaw = this.updateRotation(this.rotationYaw, f, maxYawIncrease);
   }

   /**
    * Arguments: current rotation, intended rotation, max increment.
    */
   private float updateRotation(float angle, float targetAngle, float maxIncrease) {
      float f = MathHelper.wrapDegrees(targetAngle - angle);
      if (f > maxIncrease) {
         f = maxIncrease;
      }

      if (f < -maxIncrease) {
         f = -maxIncrease;
      }

      return angle + f;
   }

   public boolean canSpawn(IWorld worldIn, boolean p_205020_2_) {
      IBlockState iblockstate = worldIn.getBlockState((new BlockPos(this)).down());
      return iblockstate.canEntitySpawn(this);
   }

   /**
    * Checks that the entity is not colliding with any blocks / liquids
    */
   public final boolean isNotColliding() {
      return this.isNotColliding(this.world);
   }

   public boolean isNotColliding(IWorldReaderBase worldIn) {
      return !worldIn.containsAnyLiquid(this.getBoundingBox()) && worldIn.isCollisionBoxesEmpty(this, this.getBoundingBox()) && worldIn.checkNoEntityCollision(this, this.getBoundingBox());
   }

   /**
    * Returns render size modifier
    */
   public float getRenderSizeModifier() {
      return 1.0F;
   }

   /**
    * Will return how many at most can spawn in a chunk at once.
    */
   public int getMaxSpawnedInChunk() {
      return 4;
   }

   public boolean func_204209_c(int p_204209_1_) {
      return false;
   }

   /**
    * The maximum height from where the entity is alowed to jump (used in pathfinder)
    */
   public int getMaxFallHeight() {
      if (this.getAttackTarget() == null) {
         return 3;
      } else {
         int i = (int)(this.getHealth() - this.getMaxHealth() * 0.33F);
         i = i - (3 - this.world.getDifficulty().getId()) * 4;
         if (i < 0) {
            i = 0;
         }

         return i + 3;
      }
   }

   public Iterable<ItemStack> getHeldEquipment() {
      return this.inventoryHands;
   }

   public Iterable<ItemStack> getArmorInventoryList() {
      return this.inventoryArmor;
   }

   public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
      switch(slotIn.getSlotType()) {
      case HAND:
         return this.inventoryHands.get(slotIn.getIndex());
      case ARMOR:
         return this.inventoryArmor.get(slotIn.getIndex());
      default:
         return ItemStack.EMPTY;
      }
   }

   public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {
      switch(slotIn.getSlotType()) {
      case HAND:
         this.inventoryHands.set(slotIn.getIndex(), stack);
         break;
      case ARMOR:
         this.inventoryArmor.set(slotIn.getIndex(), stack);
      }

   }

   /**
    * Drop the equipment for this entity.
    */
   protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
      for(EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values()) {
         ItemStack itemstack = this.getItemStackFromSlot(entityequipmentslot);
         float f = this.getDropChance(entityequipmentslot);
         boolean flag = f > 1.0F;
         if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack) && (wasRecentlyHit || flag) && this.rand.nextFloat() - (float)lootingModifier * 0.01F < f) {
            if (!flag && itemstack.isDamageable()) {
               itemstack.setDamage(itemstack.getMaxDamage() - this.rand.nextInt(1 + this.rand.nextInt(Math.max(itemstack.getMaxDamage() - 3, 1))));
            }

            this.entityDropItem(itemstack);
         }
      }

   }

   protected float getDropChance(EntityEquipmentSlot p_205712_1_) {
      float f;
      switch(p_205712_1_.getSlotType()) {
      case HAND:
         f = this.inventoryHandsDropChances[p_205712_1_.getIndex()];
         break;
      case ARMOR:
         f = this.inventoryArmorDropChances[p_205712_1_.getIndex()];
         break;
      default:
         f = 0.0F;
      }

      return f;
   }

   /**
    * Gives armor or weapon for entity based on given DifficultyInstance
    */
   protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
      if (this.rand.nextFloat() < 0.15F * difficulty.getClampedAdditionalDifficulty()) {
         int i = this.rand.nextInt(2);
         float f = this.world.getDifficulty() == EnumDifficulty.HARD ? 0.1F : 0.25F;
         if (this.rand.nextFloat() < 0.095F) {
            ++i;
         }

         if (this.rand.nextFloat() < 0.095F) {
            ++i;
         }

         if (this.rand.nextFloat() < 0.095F) {
            ++i;
         }

         boolean flag = true;

         for(EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values()) {
            if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR) {
               ItemStack itemstack = this.getItemStackFromSlot(entityequipmentslot);
               if (!flag && this.rand.nextFloat() < f) {
                  break;
               }

               flag = false;
               if (itemstack.isEmpty()) {
                  Item item = getArmorByChance(entityequipmentslot, i);
                  if (item != null) {
                     this.setItemStackToSlot(entityequipmentslot, new ItemStack(item));
                  }
               }
            }
         }
      }

   }

   public static EntityEquipmentSlot getSlotForItemStack(ItemStack stack) {
      final EntityEquipmentSlot slot = stack.getEquipmentSlot();
      if (slot != null) return slot; // FORGE: Allow modders to set a non-default equipment slot for a stack; e.g. a non-armor chestplate-slot item
      Item item = stack.getItem();
      if (item != Blocks.CARVED_PUMPKIN.asItem() && (!(item instanceof ItemBlock) || !(((ItemBlock)item).getBlock() instanceof BlockAbstractSkull))) {
         if (item instanceof ItemArmor) {
            return ((ItemArmor)item).getEquipmentSlot();
         } else if (item == Items.ELYTRA) {
            return EntityEquipmentSlot.CHEST;
         } else {
            return stack.isShield(null) ? EntityEquipmentSlot.OFFHAND : EntityEquipmentSlot.MAINHAND;
         }
      } else {
         return EntityEquipmentSlot.HEAD;
      }
   }

   @Nullable
   public static Item getArmorByChance(EntityEquipmentSlot slotIn, int chance) {
      switch(slotIn) {
      case HEAD:
         if (chance == 0) {
            return Items.LEATHER_HELMET;
         } else if (chance == 1) {
            return Items.GOLDEN_HELMET;
         } else if (chance == 2) {
            return Items.CHAINMAIL_HELMET;
         } else if (chance == 3) {
            return Items.IRON_HELMET;
         } else if (chance == 4) {
            return Items.DIAMOND_HELMET;
         }
      case CHEST:
         if (chance == 0) {
            return Items.LEATHER_CHESTPLATE;
         } else if (chance == 1) {
            return Items.GOLDEN_CHESTPLATE;
         } else if (chance == 2) {
            return Items.CHAINMAIL_CHESTPLATE;
         } else if (chance == 3) {
            return Items.IRON_CHESTPLATE;
         } else if (chance == 4) {
            return Items.DIAMOND_CHESTPLATE;
         }
      case LEGS:
         if (chance == 0) {
            return Items.LEATHER_LEGGINGS;
         } else if (chance == 1) {
            return Items.GOLDEN_LEGGINGS;
         } else if (chance == 2) {
            return Items.CHAINMAIL_LEGGINGS;
         } else if (chance == 3) {
            return Items.IRON_LEGGINGS;
         } else if (chance == 4) {
            return Items.DIAMOND_LEGGINGS;
         }
      case FEET:
         if (chance == 0) {
            return Items.LEATHER_BOOTS;
         } else if (chance == 1) {
            return Items.GOLDEN_BOOTS;
         } else if (chance == 2) {
            return Items.CHAINMAIL_BOOTS;
         } else if (chance == 3) {
            return Items.IRON_BOOTS;
         } else if (chance == 4) {
            return Items.DIAMOND_BOOTS;
         }
      default:
         return null;
      }
   }

   /**
    * Enchants Entity's current equipments based on given DifficultyInstance
    */
   protected void setEnchantmentBasedOnDifficulty(DifficultyInstance difficulty) {
      float f = difficulty.getClampedAdditionalDifficulty();
      if (!this.getHeldItemMainhand().isEmpty() && this.rand.nextFloat() < 0.25F * f) {
         this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, EnchantmentHelper.addRandomEnchantment(this.rand, this.getHeldItemMainhand(), (int)(5.0F + f * (float)this.rand.nextInt(18)), false));
      }

      for(EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values()) {
         if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR) {
            ItemStack itemstack = this.getItemStackFromSlot(entityequipmentslot);
            if (!itemstack.isEmpty() && this.rand.nextFloat() < 0.5F * f) {
               this.setItemStackToSlot(entityequipmentslot, EnchantmentHelper.addRandomEnchantment(this.rand, itemstack, (int)(5.0F + f * (float)this.rand.nextInt(18)), false));
            }
         }
      }

   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData entityLivingData, @Nullable NBTTagCompound itemNbt) {
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextGaussian() * 0.05D, 1));
      if (this.rand.nextFloat() < 0.05F) {
         this.setLeftHanded(true);
      } else {
         this.setLeftHanded(false);
      }

      return entityLivingData;
   }

   /**
    * returns true if all the conditions for steering the entity are met. For pigs, this is true if it is being ridden
    * by a player and the player is holding a carrot-on-a-stick
    */
   public boolean canBeSteered() {
      return false;
   }

   /**
    * Enable the Entity persistence
    */
   public void enablePersistence() {
      this.persistenceRequired = true;
   }

   public void setDropChance(EntityEquipmentSlot slotIn, float chance) {
      switch(slotIn.getSlotType()) {
      case HAND:
         this.inventoryHandsDropChances[slotIn.getIndex()] = chance;
         break;
      case ARMOR:
         this.inventoryArmorDropChances[slotIn.getIndex()] = chance;
      }

   }

   public boolean canPickUpLoot() {
      return this.canPickUpLoot;
   }

   public void setCanPickUpLoot(boolean canPickup) {
      this.canPickUpLoot = canPickup;
   }

   /**
    * Return the persistenceRequired field (whether this entity is allowed to naturally despawn)
    */
   public boolean isNoDespawnRequired() {
      return this.persistenceRequired;
   }

   public final boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
      if (this.getLeashed() && this.getLeashHolder() == player) {
         this.clearLeashed(true, !player.abilities.isCreativeMode);
         return true;
      } else {
         ItemStack itemstack = player.getHeldItem(hand);
         if (itemstack.getItem() == Items.LEAD && this.canBeLeashedTo(player)) {
            this.setLeashHolder(player, true);
            itemstack.shrink(1);
            return true;
         } else {
            return this.processInteract(player, hand) ? true : super.processInitialInteract(player, hand);
         }
      }
   }

   protected boolean processInteract(EntityPlayer player, EnumHand hand) {
      return false;
   }

   /**
    * Applies logic related to leashes, for example dragging the entity or breaking the leash.
    */
   protected void updateLeashedState() {
      if (this.leashNBTTag != null) {
         this.recreateLeash();
      }

      if (this.isLeashed) {
         if (!this.isAlive()) {
            this.clearLeashed(true, true);
         }

         if (this.leashHolder == null || this.leashHolder.removed) {
            this.clearLeashed(true, true);
         }
      }
   }

   /**
    * Removes the leash from this entity
    */
   public void clearLeashed(boolean sendPacket, boolean dropLead) {
      if (this.isLeashed) {
         this.isLeashed = false;
         this.leashHolder = null;
         if (!this.world.isRemote && dropLead) {
            this.entityDropItem(Items.LEAD);
         }

         if (!this.world.isRemote && sendPacket && this.world instanceof WorldServer) {
            ((WorldServer)this.world).getEntityTracker().sendToTracking(this, new SPacketEntityAttach(this, (Entity)null));
         }
      }

   }

   public boolean canBeLeashedTo(EntityPlayer player) {
      return !this.getLeashed() && !(this instanceof IMob);
   }

   public boolean getLeashed() {
      return this.isLeashed;
   }

   public Entity getLeashHolder() {
      return this.leashHolder;
   }

   /**
    * Sets the entity to be leashed to.
    */
   public void setLeashHolder(Entity entityIn, boolean sendAttachNotification) {
      this.isLeashed = true;
      this.leashHolder = entityIn;
      if (!this.world.isRemote && sendAttachNotification && this.world instanceof WorldServer) {
         ((WorldServer)this.world).getEntityTracker().sendToTracking(this, new SPacketEntityAttach(this, this.leashHolder));
      }

      if (this.isPassenger()) {
         this.stopRiding();
      }

   }

   public boolean startRiding(Entity entityIn, boolean force) {
      boolean flag = super.startRiding(entityIn, force);
      if (flag && this.getLeashed()) {
         this.clearLeashed(true, true);
      }

      return flag;
   }

   private void recreateLeash() {
      if (this.isLeashed && this.leashNBTTag != null) {
         if (this.leashNBTTag.hasUniqueId("UUID")) {
            UUID uuid = this.leashNBTTag.getUniqueId("UUID");

            for(EntityLivingBase entitylivingbase : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getBoundingBox().grow(10.0D))) {
               if (entitylivingbase.getUniqueID().equals(uuid)) {
                  this.setLeashHolder(entitylivingbase, true);
                  break;
               }
            }
         } else if (this.leashNBTTag.contains("X", 99) && this.leashNBTTag.contains("Y", 99) && this.leashNBTTag.contains("Z", 99)) {
            BlockPos blockpos = new BlockPos(this.leashNBTTag.getInt("X"), this.leashNBTTag.getInt("Y"), this.leashNBTTag.getInt("Z"));
            EntityLeashKnot entityleashknot = EntityLeashKnot.getKnotForPosition(this.world, blockpos);
            if (entityleashknot == null) {
               entityleashknot = EntityLeashKnot.createKnot(this.world, blockpos);
            }

            this.setLeashHolder(entityleashknot, true);
         } else {
            this.clearLeashed(false, true);
         }
      }

      this.leashNBTTag = null;
   }

   public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
      EntityEquipmentSlot entityequipmentslot;
      if (inventorySlot == 98) {
         entityequipmentslot = EntityEquipmentSlot.MAINHAND;
      } else if (inventorySlot == 99) {
         entityequipmentslot = EntityEquipmentSlot.OFFHAND;
      } else if (inventorySlot == 100 + EntityEquipmentSlot.HEAD.getIndex()) {
         entityequipmentslot = EntityEquipmentSlot.HEAD;
      } else if (inventorySlot == 100 + EntityEquipmentSlot.CHEST.getIndex()) {
         entityequipmentslot = EntityEquipmentSlot.CHEST;
      } else if (inventorySlot == 100 + EntityEquipmentSlot.LEGS.getIndex()) {
         entityequipmentslot = EntityEquipmentSlot.LEGS;
      } else {
         if (inventorySlot != 100 + EntityEquipmentSlot.FEET.getIndex()) {
            return false;
         }

         entityequipmentslot = EntityEquipmentSlot.FEET;
      }

      if (!itemStackIn.isEmpty() && !isItemStackInSlot(entityequipmentslot, itemStackIn) && entityequipmentslot != EntityEquipmentSlot.HEAD) {
         return false;
      } else {
         this.setItemStackToSlot(entityequipmentslot, itemStackIn);
         return true;
      }
   }

   public boolean canPassengerSteer() {
      return this.canBeSteered() && super.canPassengerSteer();
   }

   public static boolean isItemStackInSlot(EntityEquipmentSlot slotIn, ItemStack stack) {
      EntityEquipmentSlot entityequipmentslot = getSlotForItemStack(stack);
      return entityequipmentslot == slotIn || entityequipmentslot == EntityEquipmentSlot.MAINHAND && slotIn == EntityEquipmentSlot.OFFHAND || entityequipmentslot == EntityEquipmentSlot.OFFHAND && slotIn == EntityEquipmentSlot.MAINHAND;
   }

   /**
    * Returns whether the entity is in a server world
    */
   public boolean isServerWorld() {
      return super.isServerWorld() && !this.isAIDisabled();
   }

   /**
    * Set whether this Entity's AI is disabled
    */
   public void setNoAI(boolean disable) {
      byte b0 = this.dataManager.get(AI_FLAGS);
      this.dataManager.set(AI_FLAGS, disable ? (byte)(b0 | 1) : (byte)(b0 & -2));
   }

   public void setLeftHanded(boolean leftHanded) {
      byte b0 = this.dataManager.get(AI_FLAGS);
      this.dataManager.set(AI_FLAGS, leftHanded ? (byte)(b0 | 2) : (byte)(b0 & -3));
   }

   /**
    * Get whether this Entity's AI is disabled
    */
   public boolean isAIDisabled() {
      return (this.dataManager.get(AI_FLAGS) & 1) != 0;
   }

   public boolean isLeftHanded() {
      return (this.dataManager.get(AI_FLAGS) & 2) != 0;
   }

   public EnumHandSide getPrimaryHand() {
      return this.isLeftHanded() ? EnumHandSide.LEFT : EnumHandSide.RIGHT;
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      float f = (float)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
      int i = 0;
      if (entityIn instanceof EntityLivingBase) {
         f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase)entityIn).getCreatureAttribute());
         i += EnchantmentHelper.getKnockbackModifier(this);
      }

      boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f);
      if (flag) {
         if (i > 0 && entityIn instanceof EntityLivingBase) {
            ((EntityLivingBase)entityIn).knockBack(this, (float)i * 0.5F, (double)MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F))));
            this.motionX *= 0.6D;
            this.motionZ *= 0.6D;
         }

         int j = EnchantmentHelper.getFireAspectModifier(this);
         if (j > 0) {
            entityIn.setFire(j * 4);
         }

         if (entityIn instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)entityIn;
            ItemStack itemstack = this.getHeldItemMainhand();
            ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;
            if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack.canDisableShield(itemstack1, entityplayer, this) && itemstack1.isShield(entityplayer)) {
               float f1 = 0.25F + (float)EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;
               if (this.rand.nextFloat() < f1) {
                  entityplayer.getCooldownTracker().setCooldown(itemstack1.getItem(), 100);
                  this.world.setEntityState(entityplayer, (byte)30);
               }
            }
         }

         this.applyEnchantments(this, entityIn);
      }

      return flag;
   }

   protected boolean isInDaylight() {
      if (this.world.isDaytime() && !this.world.isRemote) {
         float f = this.getBrightness();
         BlockPos blockpos = this.getRidingEntity() instanceof EntityBoat ? (new BlockPos(this.posX, (double)Math.round(this.posY), this.posZ)).up() : new BlockPos(this.posX, (double)Math.round(this.posY), this.posZ);
         if (f > 0.5F && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.world.canSeeSky(blockpos)) {
            return true;
         }
      }

      return false;
   }

   protected void handleFluidJump(Tag<Fluid> p_180466_1_) {
      if (this.getNavigator().getCanSwim()) {
         super.handleFluidJump(p_180466_1_);
      } else {
         this.motionY += (double)0.3F;
      }

   }
}