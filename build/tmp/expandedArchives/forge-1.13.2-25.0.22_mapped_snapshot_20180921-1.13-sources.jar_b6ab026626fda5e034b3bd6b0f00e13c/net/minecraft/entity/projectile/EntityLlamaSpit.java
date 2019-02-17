package net.minecraft.entity.projectile;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.init.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityLlamaSpit extends Entity implements IProjectile {
   public EntityLlama owner;
   private NBTTagCompound ownerNbt;

   public EntityLlamaSpit(World worldIn) {
      super(EntityType.LLAMA_SPIT, worldIn);
      this.setSize(0.25F, 0.25F);
   }

   public EntityLlamaSpit(World worldIn, EntityLlama p_i47273_2_) {
      this(worldIn);
      this.owner = p_i47273_2_;
      this.setPosition(p_i47273_2_.posX - (double)(p_i47273_2_.width + 1.0F) * 0.5D * (double)MathHelper.sin(p_i47273_2_.renderYawOffset * ((float)Math.PI / 180F)), p_i47273_2_.posY + (double)p_i47273_2_.getEyeHeight() - (double)0.1F, p_i47273_2_.posZ + (double)(p_i47273_2_.width + 1.0F) * 0.5D * (double)MathHelper.cos(p_i47273_2_.renderYawOffset * ((float)Math.PI / 180F)));
   }

   @OnlyIn(Dist.CLIENT)
   public EntityLlamaSpit(World worldIn, double x, double y, double z, double p_i47274_8_, double p_i47274_10_, double p_i47274_12_) {
      this(worldIn);
      this.setPosition(x, y, z);

      for(int i = 0; i < 7; ++i) {
         double d0 = 0.4D + 0.1D * (double)i;
         worldIn.spawnParticle(Particles.SPIT, x, y, z, p_i47274_8_ * d0, p_i47274_10_, p_i47274_12_ * d0);
      }

      this.motionX = p_i47274_8_;
      this.motionY = p_i47274_10_;
      this.motionZ = p_i47274_12_;
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (this.ownerNbt != null) {
         this.restoreOwnerFromSave();
      }

      Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
      Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
      RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d, vec3d1);
      vec3d = new Vec3d(this.posX, this.posY, this.posZ);
      vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
      if (raytraceresult != null) {
         vec3d1 = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
      }

      Entity entity = this.getHitEntity(vec3d, vec3d1);
      if (entity != null) {
         raytraceresult = new RayTraceResult(entity);
      }

      if (raytraceresult != null && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
         this.onHit(raytraceresult);
      }

      this.posX += this.motionX;
      this.posY += this.motionY;
      this.posZ += this.motionZ;
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
      float f1 = 0.99F;
      float f2 = 0.06F;
      if (!this.world.isMaterialInBB(this.getBoundingBox(), Material.AIR)) {
         this.remove();
      } else if (this.isInWaterOrBubbleColumn()) {
         this.remove();
      } else {
         this.motionX *= (double)0.99F;
         this.motionY *= (double)0.99F;
         this.motionZ *= (double)0.99F;
         if (!this.hasNoGravity()) {
            this.motionY -= (double)0.06F;
         }

         this.setPosition(this.posX, this.posY, this.posZ);
      }
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
         this.rotationPitch = (float)(MathHelper.atan2(y, (double)f) * (double)(180F / (float)Math.PI));
         this.rotationYaw = (float)(MathHelper.atan2(x, z) * (double)(180F / (float)Math.PI));
         this.prevRotationPitch = this.rotationPitch;
         this.prevRotationYaw = this.rotationYaw;
         this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
      }

   }

   @Nullable
   private Entity getHitEntity(Vec3d p_190538_1_, Vec3d p_190538_2_) {
      Entity entity = null;
      List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D));
      double d0 = 0.0D;

      for(Entity entity1 : list) {
         if (entity1 != this.owner) {
            AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow((double)0.3F);
            RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(p_190538_1_, p_190538_2_);
            if (raytraceresult != null) {
               double d1 = p_190538_1_.squareDistanceTo(raytraceresult.hitVec);
               if (d1 < d0 || d0 == 0.0D) {
                  entity = entity1;
                  d0 = d1;
               }
            }
         }
      }

      return entity;
   }

   /**
    * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
    */
   public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
      float f = MathHelper.sqrt(x * x + y * y + z * z);
      x = x / (double)f;
      y = y / (double)f;
      z = z / (double)f;
      x = x + this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy;
      y = y + this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy;
      z = z + this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy;
      x = x * (double)velocity;
      y = y * (double)velocity;
      z = z * (double)velocity;
      this.motionX = x;
      this.motionY = y;
      this.motionZ = z;
      float f1 = MathHelper.sqrt(x * x + z * z);
      this.rotationYaw = (float)(MathHelper.atan2(x, z) * (double)(180F / (float)Math.PI));
      this.rotationPitch = (float)(MathHelper.atan2(y, (double)f1) * (double)(180F / (float)Math.PI));
      this.prevRotationYaw = this.rotationYaw;
      this.prevRotationPitch = this.rotationPitch;
   }

   public void onHit(RayTraceResult p_190536_1_) {
      if (p_190536_1_.entity != null && this.owner != null) {
         p_190536_1_.entity.attackEntityFrom(DamageSource.causeIndirectDamage(this, this.owner).setProjectile(), 1.0F);
      }

      if (!this.world.isRemote) {
         this.remove();
      }

   }

   protected void registerData() {
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   protected void readAdditional(NBTTagCompound compound) {
      if (compound.contains("Owner", 10)) {
         this.ownerNbt = compound.getCompound("Owner");
      }

   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   protected void writeAdditional(NBTTagCompound compound) {
      if (this.owner != null) {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         UUID uuid = this.owner.getUniqueID();
         nbttagcompound.setUniqueId("OwnerUUID", uuid);
         compound.setTag("Owner", nbttagcompound);
      }

   }

   private void restoreOwnerFromSave() {
      if (this.ownerNbt != null && this.ownerNbt.hasUniqueId("OwnerUUID")) {
         UUID uuid = this.ownerNbt.getUniqueId("OwnerUUID");

         for(EntityLlama entityllama : this.world.getEntitiesWithinAABB(EntityLlama.class, this.getBoundingBox().grow(15.0D))) {
            if (entityllama.getUniqueID().equals(uuid)) {
               this.owner = entityllama;
               break;
            }
         }
      }

      this.ownerNbt = null;
   }
}