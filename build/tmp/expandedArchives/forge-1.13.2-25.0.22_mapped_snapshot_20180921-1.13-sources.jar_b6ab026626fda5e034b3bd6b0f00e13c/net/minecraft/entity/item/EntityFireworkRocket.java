package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityFireworkRocket extends Entity {
   private static final DataParameter<ItemStack> FIREWORK_ITEM = EntityDataManager.createKey(EntityFireworkRocket.class, DataSerializers.ITEM_STACK);
   private static final DataParameter<Integer> BOOSTED_ENTITY_ID = EntityDataManager.createKey(EntityFireworkRocket.class, DataSerializers.VARINT);
   /** The age of the firework in ticks. */
   private int fireworkAge;
   /** The lifetime of the firework in ticks. When the age reaches the lifetime the firework explodes. */
   private int lifetime;
   private EntityLivingBase boostedEntity;

   public EntityFireworkRocket(World worldIn) {
      super(EntityType.FIREWORK_ROCKET, worldIn);
      this.setSize(0.25F, 0.25F);
   }

   protected void registerData() {
      this.dataManager.register(FIREWORK_ITEM, ItemStack.EMPTY);
      this.dataManager.register(BOOSTED_ENTITY_ID, 0);
   }

   /**
    * Checks if the entity is in range to render.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double distance) {
      return distance < 4096.0D && !this.isAttachedToEntity();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRender3d(double x, double y, double z) {
      return super.isInRangeToRender3d(x, y, z) && !this.isAttachedToEntity();
   }

   public EntityFireworkRocket(World worldIn, double x, double y, double z, ItemStack givenItem) {
      super(EntityType.FIREWORK_ROCKET, worldIn);
      this.fireworkAge = 0;
      this.setSize(0.25F, 0.25F);
      this.setPosition(x, y, z);
      int i = 1;
      if (!givenItem.isEmpty() && givenItem.hasTag()) {
         this.dataManager.set(FIREWORK_ITEM, givenItem.copy());
         i += givenItem.getOrCreateChildTag("Fireworks").getByte("Flight");
      }

      this.motionX = this.rand.nextGaussian() * 0.001D;
      this.motionZ = this.rand.nextGaussian() * 0.001D;
      this.motionY = 0.05D;
      this.lifetime = 10 * i + this.rand.nextInt(6) + this.rand.nextInt(7);
   }

   public EntityFireworkRocket(World p_i47367_1_, ItemStack p_i47367_2_, EntityLivingBase p_i47367_3_) {
      this(p_i47367_1_, p_i47367_3_.posX, p_i47367_3_.posY, p_i47367_3_.posZ, p_i47367_2_);
      this.dataManager.set(BOOSTED_ENTITY_ID, p_i47367_3_.getEntityId());
      this.boostedEntity = p_i47367_3_;
   }

   /**
    * Updates the entity motion clientside, called by packets from the server
    */
   @OnlyIn(Dist.CLIENT)
   public void setVelocity(double x, double y, double z) {
      this.motionX = x;
      this.motionY = y;
      this.motionZ = z;
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float f = MathHelper.sqrt(x * x + z * z);
         this.rotationYaw = (float)(MathHelper.atan2(x, z) * (double)(180F / (float)Math.PI));
         this.rotationPitch = (float)(MathHelper.atan2(y, (double)f) * (double)(180F / (float)Math.PI));
         this.prevRotationYaw = this.rotationYaw;
         this.prevRotationPitch = this.rotationPitch;
      }

   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      this.lastTickPosX = this.posX;
      this.lastTickPosY = this.posY;
      this.lastTickPosZ = this.posZ;
      super.tick();
      if (this.isAttachedToEntity()) {
         if (this.boostedEntity == null) {
            Entity entity = this.world.getEntityByID(this.dataManager.get(BOOSTED_ENTITY_ID));
            if (entity instanceof EntityLivingBase) {
               this.boostedEntity = (EntityLivingBase)entity;
            }
         }

         if (this.boostedEntity != null) {
            if (this.boostedEntity.isElytraFlying()) {
               Vec3d vec3d = this.boostedEntity.getLookVec();
               double d0 = 1.5D;
               double d1 = 0.1D;
               this.boostedEntity.motionX += vec3d.x * 0.1D + (vec3d.x * 1.5D - this.boostedEntity.motionX) * 0.5D;
               this.boostedEntity.motionY += vec3d.y * 0.1D + (vec3d.y * 1.5D - this.boostedEntity.motionY) * 0.5D;
               this.boostedEntity.motionZ += vec3d.z * 0.1D + (vec3d.z * 1.5D - this.boostedEntity.motionZ) * 0.5D;
            }

            this.setPosition(this.boostedEntity.posX, this.boostedEntity.posY, this.boostedEntity.posZ);
            this.motionX = this.boostedEntity.motionX;
            this.motionY = this.boostedEntity.motionY;
            this.motionZ = this.boostedEntity.motionZ;
         }
      } else {
         this.motionX *= 1.15D;
         this.motionZ *= 1.15D;
         this.motionY += 0.04D;
         this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
      }

      float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
      this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * (double)(180F / (float)Math.PI));

      for(this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f) * (double)(180F / (float)Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
         ;
      }

      while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
         this.prevRotationPitch += 360.0F;
      }

      while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
         this.prevRotationYaw -= 360.0F;
      }

      while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
         this.prevRotationYaw += 360.0F;
      }

      this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
      this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
      if (this.fireworkAge == 0 && !this.isSilent()) {
         this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 3.0F, 1.0F);
      }

      ++this.fireworkAge;
      if (this.world.isRemote && this.fireworkAge % 2 < 2) {
         this.world.spawnParticle(Particles.FIREWORK, this.posX, this.posY - 0.3D, this.posZ, this.rand.nextGaussian() * 0.05D, -this.motionY * 0.5D, this.rand.nextGaussian() * 0.05D);
      }

      if (!this.world.isRemote && this.fireworkAge > this.lifetime) {
         this.world.setEntityState(this, (byte)17);
         this.dealExplosionDamage();
         this.remove();
      }

   }

   private void dealExplosionDamage() {
      float f = 0.0F;
      ItemStack itemstack = this.dataManager.get(FIREWORK_ITEM);
      NBTTagCompound nbttagcompound = itemstack.isEmpty() ? null : itemstack.getChildTag("Fireworks");
      NBTTagList nbttaglist = nbttagcompound != null ? nbttagcompound.getList("Explosions", 10) : null;
      if (nbttaglist != null && !nbttaglist.isEmpty()) {
         f = (float)(5 + nbttaglist.size() * 2);
      }

      if (f > 0.0F) {
         if (this.boostedEntity != null) {
            this.boostedEntity.attackEntityFrom(DamageSource.FIREWORKS, (float)(5 + nbttaglist.size() * 2));
         }

         double d0 = 5.0D;
         Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);

         for(EntityLivingBase entitylivingbase : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getBoundingBox().grow(5.0D))) {
            if (entitylivingbase != this.boostedEntity && !(this.getDistanceSq(entitylivingbase) > 25.0D)) {
               boolean flag = false;

               for(int i = 0; i < 2; ++i) {
                  RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d, new Vec3d(entitylivingbase.posX, entitylivingbase.posY + (double)entitylivingbase.height * 0.5D * (double)i, entitylivingbase.posZ), RayTraceFluidMode.NEVER, true, false);
                  if (raytraceresult == null || raytraceresult.type == RayTraceResult.Type.MISS) {
                     flag = true;
                     break;
                  }
               }

               if (flag) {
                  float f1 = f * (float)Math.sqrt((5.0D - (double)this.getDistance(entitylivingbase)) / 5.0D);
                  entitylivingbase.attackEntityFrom(DamageSource.FIREWORKS, f1);
               }
            }
         }
      }

   }

   public boolean isAttachedToEntity() {
      return this.dataManager.get(BOOSTED_ENTITY_ID) > 0;
   }

   /**
    * Handler for {@link World#setEntityState}
    */
   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 17 && this.world.isRemote) {
         ItemStack itemstack = this.dataManager.get(FIREWORK_ITEM);
         NBTTagCompound nbttagcompound = itemstack.isEmpty() ? null : itemstack.getChildTag("Fireworks");
         this.world.makeFireworks(this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ, nbttagcompound);
      }

      super.handleStatusUpdate(id);
   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   public void writeAdditional(NBTTagCompound compound) {
      compound.setInt("Life", this.fireworkAge);
      compound.setInt("LifeTime", this.lifetime);
      ItemStack itemstack = this.dataManager.get(FIREWORK_ITEM);
      if (!itemstack.isEmpty()) {
         compound.setTag("FireworksItem", itemstack.write(new NBTTagCompound()));
      }

   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(NBTTagCompound compound) {
      this.fireworkAge = compound.getInt("Life");
      this.lifetime = compound.getInt("LifeTime");
      ItemStack itemstack = ItemStack.read(compound.getCompound("FireworksItem"));
      if (!itemstack.isEmpty()) {
         this.dataManager.set(FIREWORK_ITEM, itemstack);
      }

   }

   /**
    * Returns true if it's possible to attack this entity with an item.
    */
   public boolean canBeAttackedWithItem() {
      return false;
   }
}