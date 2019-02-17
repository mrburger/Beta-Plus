package net.minecraft.entity.item;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityArmorStand extends EntityLivingBase {
   private static final Rotations DEFAULT_HEAD_ROTATION = new Rotations(0.0F, 0.0F, 0.0F);
   private static final Rotations DEFAULT_BODY_ROTATION = new Rotations(0.0F, 0.0F, 0.0F);
   private static final Rotations DEFAULT_LEFTARM_ROTATION = new Rotations(-10.0F, 0.0F, -10.0F);
   private static final Rotations DEFAULT_RIGHTARM_ROTATION = new Rotations(-15.0F, 0.0F, 10.0F);
   private static final Rotations DEFAULT_LEFTLEG_ROTATION = new Rotations(-1.0F, 0.0F, -1.0F);
   private static final Rotations DEFAULT_RIGHTLEG_ROTATION = new Rotations(1.0F, 0.0F, 1.0F);
   public static final DataParameter<Byte> STATUS = EntityDataManager.createKey(EntityArmorStand.class, DataSerializers.BYTE);
   public static final DataParameter<Rotations> HEAD_ROTATION = EntityDataManager.createKey(EntityArmorStand.class, DataSerializers.ROTATIONS);
   public static final DataParameter<Rotations> BODY_ROTATION = EntityDataManager.createKey(EntityArmorStand.class, DataSerializers.ROTATIONS);
   public static final DataParameter<Rotations> LEFT_ARM_ROTATION = EntityDataManager.createKey(EntityArmorStand.class, DataSerializers.ROTATIONS);
   public static final DataParameter<Rotations> RIGHT_ARM_ROTATION = EntityDataManager.createKey(EntityArmorStand.class, DataSerializers.ROTATIONS);
   public static final DataParameter<Rotations> LEFT_LEG_ROTATION = EntityDataManager.createKey(EntityArmorStand.class, DataSerializers.ROTATIONS);
   public static final DataParameter<Rotations> RIGHT_LEG_ROTATION = EntityDataManager.createKey(EntityArmorStand.class, DataSerializers.ROTATIONS);
   private static final Predicate<Entity> IS_RIDEABLE_MINECART = (p_200617_0_) -> {
      return p_200617_0_ instanceof EntityMinecart && ((EntityMinecart)p_200617_0_).canBeRidden();
   };
   private final NonNullList<ItemStack> handItems = NonNullList.withSize(2, ItemStack.EMPTY);
   private final NonNullList<ItemStack> armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
   private boolean canInteract;
   /** After punching the stand, the cooldown before you can punch it again without breaking it. */
   public long punchCooldown;
   private int disabledSlots;
   private boolean wasMarker;
   private Rotations headRotation = DEFAULT_HEAD_ROTATION;
   private Rotations bodyRotation = DEFAULT_BODY_ROTATION;
   private Rotations leftArmRotation = DEFAULT_LEFTARM_ROTATION;
   private Rotations rightArmRotation = DEFAULT_RIGHTARM_ROTATION;
   private Rotations leftLegRotation = DEFAULT_LEFTLEG_ROTATION;
   private Rotations rightLegRotation = DEFAULT_RIGHTLEG_ROTATION;

   public EntityArmorStand(World worldIn) {
      super(EntityType.ARMOR_STAND, worldIn);
      this.noClip = this.hasNoGravity();
      this.setSize(0.5F, 1.975F);
      this.stepHeight = 0.0F;
   }

   public EntityArmorStand(World worldIn, double posX, double posY, double posZ) {
      this(worldIn);
      this.setPosition(posX, posY, posZ);
   }

   /**
    * Sets the width and height of the entity.
    */
   protected final void setSize(float width, float height) {
      double d0 = this.posX;
      double d1 = this.posY;
      double d2 = this.posZ;
      float f = this.hasMarker() ? 0.0F : (this.isChild() ? 0.5F : 1.0F);
      super.setSize(width * f, height * f);
      this.setPosition(d0, d1, d2);
   }

   /**
    * Returns whether the entity is in a server world
    */
   public boolean isServerWorld() {
      return super.isServerWorld() && !this.hasNoGravity();
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(STATUS, (byte)0);
      this.dataManager.register(HEAD_ROTATION, DEFAULT_HEAD_ROTATION);
      this.dataManager.register(BODY_ROTATION, DEFAULT_BODY_ROTATION);
      this.dataManager.register(LEFT_ARM_ROTATION, DEFAULT_LEFTARM_ROTATION);
      this.dataManager.register(RIGHT_ARM_ROTATION, DEFAULT_RIGHTARM_ROTATION);
      this.dataManager.register(LEFT_LEG_ROTATION, DEFAULT_LEFTLEG_ROTATION);
      this.dataManager.register(RIGHT_LEG_ROTATION, DEFAULT_RIGHTLEG_ROTATION);
   }

   public Iterable<ItemStack> getHeldEquipment() {
      return this.handItems;
   }

   public Iterable<ItemStack> getArmorInventoryList() {
      return this.armorItems;
   }

   public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
      switch(slotIn.getSlotType()) {
      case HAND:
         return this.handItems.get(slotIn.getIndex());
      case ARMOR:
         return this.armorItems.get(slotIn.getIndex());
      default:
         return ItemStack.EMPTY;
      }
   }

   public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {
      switch(slotIn.getSlotType()) {
      case HAND:
         this.playEquipSound(stack);
         this.handItems.set(slotIn.getIndex(), stack);
         break;
      case ARMOR:
         this.playEquipSound(stack);
         this.armorItems.set(slotIn.getIndex(), stack);
      }

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

      if (!itemStackIn.isEmpty() && !EntityLiving.isItemStackInSlot(entityequipmentslot, itemStackIn) && entityequipmentslot != EntityEquipmentSlot.HEAD) {
         return false;
      } else {
         this.setItemStackToSlot(entityequipmentslot, itemStackIn);
         return true;
      }
   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      NBTTagList nbttaglist = new NBTTagList();

      for(ItemStack itemstack : this.armorItems) {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         if (!itemstack.isEmpty()) {
            itemstack.write(nbttagcompound);
         }

         nbttaglist.add((INBTBase)nbttagcompound);
      }

      compound.setTag("ArmorItems", nbttaglist);
      NBTTagList nbttaglist1 = new NBTTagList();

      for(ItemStack itemstack1 : this.handItems) {
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();
         if (!itemstack1.isEmpty()) {
            itemstack1.write(nbttagcompound1);
         }

         nbttaglist1.add((INBTBase)nbttagcompound1);
      }

      compound.setTag("HandItems", nbttaglist1);
      compound.setBoolean("Invisible", this.isInvisible());
      compound.setBoolean("Small", this.isSmall());
      compound.setBoolean("ShowArms", this.getShowArms());
      compound.setInt("DisabledSlots", this.disabledSlots);
      compound.setBoolean("NoBasePlate", this.hasNoBasePlate());
      if (this.hasMarker()) {
         compound.setBoolean("Marker", this.hasMarker());
      }

      compound.setTag("Pose", this.readPoseFromNBT());
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      if (compound.contains("ArmorItems", 9)) {
         NBTTagList nbttaglist = compound.getList("ArmorItems", 10);

         for(int i = 0; i < this.armorItems.size(); ++i) {
            this.armorItems.set(i, ItemStack.read(nbttaglist.getCompound(i)));
         }
      }

      if (compound.contains("HandItems", 9)) {
         NBTTagList nbttaglist1 = compound.getList("HandItems", 10);

         for(int j = 0; j < this.handItems.size(); ++j) {
            this.handItems.set(j, ItemStack.read(nbttaglist1.getCompound(j)));
         }
      }

      this.setInvisible(compound.getBoolean("Invisible"));
      this.setSmall(compound.getBoolean("Small"));
      this.setShowArms(compound.getBoolean("ShowArms"));
      this.disabledSlots = compound.getInt("DisabledSlots");
      this.setNoBasePlate(compound.getBoolean("NoBasePlate"));
      this.setMarker(compound.getBoolean("Marker"));
      this.wasMarker = !this.hasMarker();
      this.noClip = this.hasNoGravity();
      NBTTagCompound nbttagcompound = compound.getCompound("Pose");
      this.writePoseToNBT(nbttagcompound);
   }

   /**
    * Saves the pose to an NBTTagCompound.
    */
   private void writePoseToNBT(NBTTagCompound tagCompound) {
      NBTTagList nbttaglist = tagCompound.getList("Head", 5);
      this.setHeadRotation(nbttaglist.isEmpty() ? DEFAULT_HEAD_ROTATION : new Rotations(nbttaglist));
      NBTTagList nbttaglist1 = tagCompound.getList("Body", 5);
      this.setBodyRotation(nbttaglist1.isEmpty() ? DEFAULT_BODY_ROTATION : new Rotations(nbttaglist1));
      NBTTagList nbttaglist2 = tagCompound.getList("LeftArm", 5);
      this.setLeftArmRotation(nbttaglist2.isEmpty() ? DEFAULT_LEFTARM_ROTATION : new Rotations(nbttaglist2));
      NBTTagList nbttaglist3 = tagCompound.getList("RightArm", 5);
      this.setRightArmRotation(nbttaglist3.isEmpty() ? DEFAULT_RIGHTARM_ROTATION : new Rotations(nbttaglist3));
      NBTTagList nbttaglist4 = tagCompound.getList("LeftLeg", 5);
      this.setLeftLegRotation(nbttaglist4.isEmpty() ? DEFAULT_LEFTLEG_ROTATION : new Rotations(nbttaglist4));
      NBTTagList nbttaglist5 = tagCompound.getList("RightLeg", 5);
      this.setRightLegRotation(nbttaglist5.isEmpty() ? DEFAULT_RIGHTLEG_ROTATION : new Rotations(nbttaglist5));
   }

   private NBTTagCompound readPoseFromNBT() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      if (!DEFAULT_HEAD_ROTATION.equals(this.headRotation)) {
         nbttagcompound.setTag("Head", this.headRotation.writeToNBT());
      }

      if (!DEFAULT_BODY_ROTATION.equals(this.bodyRotation)) {
         nbttagcompound.setTag("Body", this.bodyRotation.writeToNBT());
      }

      if (!DEFAULT_LEFTARM_ROTATION.equals(this.leftArmRotation)) {
         nbttagcompound.setTag("LeftArm", this.leftArmRotation.writeToNBT());
      }

      if (!DEFAULT_RIGHTARM_ROTATION.equals(this.rightArmRotation)) {
         nbttagcompound.setTag("RightArm", this.rightArmRotation.writeToNBT());
      }

      if (!DEFAULT_LEFTLEG_ROTATION.equals(this.leftLegRotation)) {
         nbttagcompound.setTag("LeftLeg", this.leftLegRotation.writeToNBT());
      }

      if (!DEFAULT_RIGHTLEG_ROTATION.equals(this.rightLegRotation)) {
         nbttagcompound.setTag("RightLeg", this.rightLegRotation.writeToNBT());
      }

      return nbttagcompound;
   }

   /**
    * Returns true if this entity should push and be pushed by other entities when colliding.
    */
   public boolean canBePushed() {
      return false;
   }

   protected void collideWithEntity(Entity entityIn) {
   }

   protected void collideWithNearbyEntities() {
      List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox(), IS_RIDEABLE_MINECART);

      for(int i = 0; i < list.size(); ++i) {
         Entity entity = list.get(i);
         if (this.getDistanceSq(entity) <= 0.2D) {
            entity.applyEntityCollision(this);
         }
      }

   }

   /**
    * Applies the given player interaction to this Entity.
    */
   public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (!this.hasMarker() && itemstack.getItem() != Items.NAME_TAG) {
         if (!this.world.isRemote && !player.isSpectator()) {
            EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
            if (itemstack.isEmpty()) {
               EntityEquipmentSlot entityequipmentslot1 = this.getClickedSlot(vec);
               EntityEquipmentSlot entityequipmentslot2 = this.isDisabled(entityequipmentslot1) ? entityequipmentslot : entityequipmentslot1;
               if (this.hasItemInSlot(entityequipmentslot2)) {
                  this.swapItem(player, entityequipmentslot2, itemstack, hand);
               }
            } else {
               if (this.isDisabled(entityequipmentslot)) {
                  return EnumActionResult.FAIL;
               }

               if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.HAND && !this.getShowArms()) {
                  return EnumActionResult.FAIL;
               }

               this.swapItem(player, entityequipmentslot, itemstack, hand);
            }

            return EnumActionResult.SUCCESS;
         } else {
            return EnumActionResult.SUCCESS;
         }
      } else {
         return EnumActionResult.PASS;
      }
   }

   protected EntityEquipmentSlot getClickedSlot(Vec3d p_190772_1_) {
      EntityEquipmentSlot entityequipmentslot = EntityEquipmentSlot.MAINHAND;
      boolean flag = this.isSmall();
      double d0 = flag ? p_190772_1_.y * 2.0D : p_190772_1_.y;
      EntityEquipmentSlot entityequipmentslot1 = EntityEquipmentSlot.FEET;
      if (d0 >= 0.1D && d0 < 0.1D + (flag ? 0.8D : 0.45D) && this.hasItemInSlot(entityequipmentslot1)) {
         entityequipmentslot = EntityEquipmentSlot.FEET;
      } else if (d0 >= 0.9D + (flag ? 0.3D : 0.0D) && d0 < 0.9D + (flag ? 1.0D : 0.7D) && this.hasItemInSlot(EntityEquipmentSlot.CHEST)) {
         entityequipmentslot = EntityEquipmentSlot.CHEST;
      } else if (d0 >= 0.4D && d0 < 0.4D + (flag ? 1.0D : 0.8D) && this.hasItemInSlot(EntityEquipmentSlot.LEGS)) {
         entityequipmentslot = EntityEquipmentSlot.LEGS;
      } else if (d0 >= 1.6D && this.hasItemInSlot(EntityEquipmentSlot.HEAD)) {
         entityequipmentslot = EntityEquipmentSlot.HEAD;
      } else if (!this.hasItemInSlot(EntityEquipmentSlot.MAINHAND) && this.hasItemInSlot(EntityEquipmentSlot.OFFHAND)) {
         entityequipmentslot = EntityEquipmentSlot.OFFHAND;
      }

      return entityequipmentslot;
   }

   public boolean isDisabled(EntityEquipmentSlot slotIn) {
      return (this.disabledSlots & 1 << slotIn.getSlotIndex()) != 0 || slotIn.getSlotType() == EntityEquipmentSlot.Type.HAND && !this.getShowArms();
   }

   private void swapItem(EntityPlayer player, EntityEquipmentSlot p_184795_2_, ItemStack p_184795_3_, EnumHand hand) {
      ItemStack itemstack = this.getItemStackFromSlot(p_184795_2_);
      if (itemstack.isEmpty() || (this.disabledSlots & 1 << p_184795_2_.getSlotIndex() + 8) == 0) {
         if (!itemstack.isEmpty() || (this.disabledSlots & 1 << p_184795_2_.getSlotIndex() + 16) == 0) {
            if (player.abilities.isCreativeMode && itemstack.isEmpty() && !p_184795_3_.isEmpty()) {
               ItemStack itemstack2 = p_184795_3_.copy();
               itemstack2.setCount(1);
               this.setItemStackToSlot(p_184795_2_, itemstack2);
            } else if (!p_184795_3_.isEmpty() && p_184795_3_.getCount() > 1) {
               if (itemstack.isEmpty()) {
                  ItemStack itemstack1 = p_184795_3_.copy();
                  itemstack1.setCount(1);
                  this.setItemStackToSlot(p_184795_2_, itemstack1);
                  p_184795_3_.shrink(1);
               }
            } else {
               this.setItemStackToSlot(p_184795_2_, p_184795_3_);
               player.setHeldItem(hand, itemstack);
            }
         }
      }
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (!this.world.isRemote && !this.removed) {
         if (DamageSource.OUT_OF_WORLD.equals(source)) {
            this.remove();
            return false;
         } else if (!this.isInvulnerableTo(source) && !this.canInteract && !this.hasMarker()) {
            if (source.isExplosion()) {
               this.dropContents();
               this.remove();
               return false;
            } else if (DamageSource.IN_FIRE.equals(source)) {
               if (this.isBurning()) {
                  this.damageArmorStand(0.15F);
               } else {
                  this.setFire(5);
               }

               return false;
            } else if (DamageSource.ON_FIRE.equals(source) && this.getHealth() > 0.5F) {
               this.damageArmorStand(4.0F);
               return false;
            } else {
               boolean flag = source.getImmediateSource() instanceof EntityArrow;
               boolean flag1 = "player".equals(source.getDamageType());
               if (!flag1 && !flag) {
                  return false;
               } else if (source.getTrueSource() instanceof EntityPlayer && !((EntityPlayer)source.getTrueSource()).abilities.allowEdit) {
                  return false;
               } else if (source.isCreativePlayer()) {
                  this.playBrokenSound();
                  this.playParticles();
                  this.remove();
                  return false;
               } else {
                  long i = this.world.getGameTime();
                  if (i - this.punchCooldown > 5L && !flag) {
                     this.world.setEntityState(this, (byte)32);
                     this.punchCooldown = i;
                  } else {
                     this.dropBlock();
                     this.playParticles();
                     this.remove();
                  }

                  return true;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   /**
    * Handler for {@link World#setEntityState}
    */
   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 32) {
         if (this.world.isRemote) {
            this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_ARMOR_STAND_HIT, this.getSoundCategory(), 0.3F, 1.0F, false);
            this.punchCooldown = this.world.getGameTime();
         }
      } else {
         super.handleStatusUpdate(id);
      }

   }

   /**
    * Checks if the entity is in range to render.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double distance) {
      double d0 = this.getBoundingBox().getAverageEdgeLength() * 4.0D;
      if (Double.isNaN(d0) || d0 == 0.0D) {
         d0 = 4.0D;
      }

      d0 = d0 * 64.0D;
      return distance < d0 * d0;
   }

   private void playParticles() {
      if (this.world instanceof WorldServer) {
         ((WorldServer)this.world).spawnParticle(new BlockParticleData(Particles.BLOCK, Blocks.OAK_PLANKS.getDefaultState()), this.posX, this.posY + (double)this.height / 1.5D, this.posZ, 10, (double)(this.width / 4.0F), (double)(this.height / 4.0F), (double)(this.width / 4.0F), 0.05D);
      }

   }

   private void damageArmorStand(float damage) {
      float f = this.getHealth();
      f = f - damage;
      if (f <= 0.5F) {
         this.dropContents();
         this.remove();
      } else {
         this.setHealth(f);
      }

   }

   private void dropBlock() {
      Block.spawnAsEntity(this.world, new BlockPos(this), new ItemStack(Items.ARMOR_STAND));
      this.dropContents();
   }

   private void dropContents() {
      this.playBrokenSound();

      for(int i = 0; i < this.handItems.size(); ++i) {
         ItemStack itemstack = this.handItems.get(i);
         if (!itemstack.isEmpty()) {
            Block.spawnAsEntity(this.world, (new BlockPos(this)).up(), itemstack);
            this.handItems.set(i, ItemStack.EMPTY);
         }
      }

      for(int j = 0; j < this.armorItems.size(); ++j) {
         ItemStack itemstack1 = this.armorItems.get(j);
         if (!itemstack1.isEmpty()) {
            Block.spawnAsEntity(this.world, (new BlockPos(this)).up(), itemstack1);
            this.armorItems.set(j, ItemStack.EMPTY);
         }
      }

   }

   private void playBrokenSound() {
      this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_ARMORSTAND_BREAK, this.getSoundCategory(), 1.0F, 1.0F);
   }

   protected float updateDistance(float p_110146_1_, float p_110146_2_) {
      this.prevRenderYawOffset = this.prevRotationYaw;
      this.renderYawOffset = this.rotationYaw;
      return 0.0F;
   }

   public float getEyeHeight() {
      return this.isChild() ? this.height * 0.5F : this.height * 0.9F;
   }

   /**
    * Returns the Y Offset of this entity.
    */
   public double getYOffset() {
      return this.hasMarker() ? 0.0D : (double)0.1F;
   }

   public void travel(float strafe, float vertical, float forward) {
      if (!this.hasNoGravity()) {
         super.travel(strafe, vertical, forward);
      }
   }

   /**
    * Set the render yaw offset
    */
   public void setRenderYawOffset(float offset) {
      this.prevRenderYawOffset = this.prevRotationYaw = offset;
      this.prevRotationYawHead = this.rotationYawHead = offset;
   }

   /**
    * Sets the head's yaw rotation of the entity.
    */
   public void setRotationYawHead(float rotation) {
      this.prevRenderYawOffset = this.prevRotationYaw = rotation;
      this.prevRotationYawHead = this.rotationYawHead = rotation;
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      Rotations rotations = this.dataManager.get(HEAD_ROTATION);
      if (!this.headRotation.equals(rotations)) {
         this.setHeadRotation(rotations);
      }

      Rotations rotations1 = this.dataManager.get(BODY_ROTATION);
      if (!this.bodyRotation.equals(rotations1)) {
         this.setBodyRotation(rotations1);
      }

      Rotations rotations2 = this.dataManager.get(LEFT_ARM_ROTATION);
      if (!this.leftArmRotation.equals(rotations2)) {
         this.setLeftArmRotation(rotations2);
      }

      Rotations rotations3 = this.dataManager.get(RIGHT_ARM_ROTATION);
      if (!this.rightArmRotation.equals(rotations3)) {
         this.setRightArmRotation(rotations3);
      }

      Rotations rotations4 = this.dataManager.get(LEFT_LEG_ROTATION);
      if (!this.leftLegRotation.equals(rotations4)) {
         this.setLeftLegRotation(rotations4);
      }

      Rotations rotations5 = this.dataManager.get(RIGHT_LEG_ROTATION);
      if (!this.rightLegRotation.equals(rotations5)) {
         this.setRightLegRotation(rotations5);
      }

      boolean flag = this.hasMarker();
      if (this.wasMarker != flag) {
         this.updateBoundingBox(flag);
         this.preventEntitySpawning = !flag;
         this.wasMarker = flag;
      }

   }

   private void updateBoundingBox(boolean p_181550_1_) {
      if (p_181550_1_) {
         this.setSize(0.0F, 0.0F);
      } else {
         this.setSize(0.5F, 1.975F);
      }

   }

   /**
    * Clears potion metadata values if the entity has no potion effects. Otherwise, updates potion effect color,
    * ambience, and invisibility metadata values
    */
   protected void updatePotionMetadata() {
      this.setInvisible(this.canInteract);
   }

   public void setInvisible(boolean invisible) {
      this.canInteract = invisible;
      super.setInvisible(invisible);
   }

   /**
    * If Animal, checks if the age timer is negative
    */
   public boolean isChild() {
      return this.isSmall();
   }

   /**
    * Called by the /kill command.
    */
   public void onKillCommand() {
      this.remove();
   }

   public boolean isImmuneToExplosions() {
      return this.isInvisible();
   }

   public EnumPushReaction getPushReaction() {
      return this.hasMarker() ? EnumPushReaction.IGNORE : super.getPushReaction();
   }

   private void setSmall(boolean small) {
      this.dataManager.set(STATUS, this.setBit(this.dataManager.get(STATUS), 1, small));
      this.setSize(0.5F, 1.975F);
   }

   public boolean isSmall() {
      return (this.dataManager.get(STATUS) & 1) != 0;
   }

   private void setShowArms(boolean showArms) {
      this.dataManager.set(STATUS, this.setBit(this.dataManager.get(STATUS), 4, showArms));
   }

   public boolean getShowArms() {
      return (this.dataManager.get(STATUS) & 4) != 0;
   }

   private void setNoBasePlate(boolean noBasePlate) {
      this.dataManager.set(STATUS, this.setBit(this.dataManager.get(STATUS), 8, noBasePlate));
   }

   public boolean hasNoBasePlate() {
      return (this.dataManager.get(STATUS) & 8) != 0;
   }

   /**
    * Marker defines where if true, the size is 0 and will not be rendered or intractable.
    */
   private void setMarker(boolean marker) {
      this.dataManager.set(STATUS, this.setBit(this.dataManager.get(STATUS), 16, marker));
      this.setSize(0.5F, 1.975F);
   }

   /**
    * Gets whether the armor stand has marker enabled. If true, the armor stand's bounding box is set to zero and cannot
    * be interacted with.
    */
   public boolean hasMarker() {
      return (this.dataManager.get(STATUS) & 16) != 0;
   }

   private byte setBit(byte p_184797_1_, int p_184797_2_, boolean p_184797_3_) {
      if (p_184797_3_) {
         p_184797_1_ = (byte)(p_184797_1_ | p_184797_2_);
      } else {
         p_184797_1_ = (byte)(p_184797_1_ & ~p_184797_2_);
      }

      return p_184797_1_;
   }

   public void setHeadRotation(Rotations vec) {
      this.headRotation = vec;
      this.dataManager.set(HEAD_ROTATION, vec);
   }

   public void setBodyRotation(Rotations vec) {
      this.bodyRotation = vec;
      this.dataManager.set(BODY_ROTATION, vec);
   }

   public void setLeftArmRotation(Rotations vec) {
      this.leftArmRotation = vec;
      this.dataManager.set(LEFT_ARM_ROTATION, vec);
   }

   public void setRightArmRotation(Rotations vec) {
      this.rightArmRotation = vec;
      this.dataManager.set(RIGHT_ARM_ROTATION, vec);
   }

   public void setLeftLegRotation(Rotations vec) {
      this.leftLegRotation = vec;
      this.dataManager.set(LEFT_LEG_ROTATION, vec);
   }

   public void setRightLegRotation(Rotations vec) {
      this.rightLegRotation = vec;
      this.dataManager.set(RIGHT_LEG_ROTATION, vec);
   }

   public Rotations getHeadRotation() {
      return this.headRotation;
   }

   public Rotations getBodyRotation() {
      return this.bodyRotation;
   }

   @OnlyIn(Dist.CLIENT)
   public Rotations getLeftArmRotation() {
      return this.leftArmRotation;
   }

   @OnlyIn(Dist.CLIENT)
   public Rotations getRightArmRotation() {
      return this.rightArmRotation;
   }

   @OnlyIn(Dist.CLIENT)
   public Rotations getLeftLegRotation() {
      return this.leftLegRotation;
   }

   @OnlyIn(Dist.CLIENT)
   public Rotations getRightLegRotation() {
      return this.rightLegRotation;
   }

   /**
    * Returns true if other Entities should be prevented from moving through this Entity.
    */
   public boolean canBeCollidedWith() {
      return super.canBeCollidedWith() && !this.hasMarker();
   }

   public EnumHandSide getPrimaryHand() {
      return EnumHandSide.RIGHT;
   }

   protected SoundEvent getFallSound(int heightIn) {
      return SoundEvents.ENTITY_ARMOR_STAND_FALL;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_ARMOR_STAND_HIT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ARMORSTAND_BREAK;
   }

   /**
    * Called when a lightning bolt hits the entity.
    */
   public void onStruckByLightning(EntityLightningBolt lightningBolt) {
   }

   /**
    * Returns false if the entity is an armor stand. Returns true for all other entity living bases.
    */
   public boolean canBeHitWithPotion() {
      return false;
   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      if (STATUS.equals(key)) {
         this.setSize(0.5F, 1.975F);
      }

      super.notifyDataManagerChange(key);
   }

   public boolean attackable() {
      return false;
   }
}