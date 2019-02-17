package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class EntityFireball extends Entity {
   public EntityLivingBase shootingEntity;
   private int ticksAlive;
   private int ticksInAir;
   public double accelerationX;
   public double accelerationY;
   public double accelerationZ;

   protected EntityFireball(EntityType<?> type, World p_i48543_2_, float p_i48543_3_, float p_i48543_4_) {
      super(type, p_i48543_2_);
      this.setSize(p_i48543_3_, p_i48543_4_);
   }

   public EntityFireball(EntityType<?> type, double p_i48544_2_, double p_i48544_4_, double p_i48544_6_, double p_i48544_8_, double p_i48544_10_, double p_i48544_12_, World p_i48544_14_, float p_i48544_15_, float p_i48544_16_) {
      this(type, p_i48544_14_, p_i48544_15_, p_i48544_16_);
      this.setLocationAndAngles(p_i48544_2_, p_i48544_4_, p_i48544_6_, this.rotationYaw, this.rotationPitch);
      this.setPosition(p_i48544_2_, p_i48544_4_, p_i48544_6_);
      double d0 = (double)MathHelper.sqrt(p_i48544_8_ * p_i48544_8_ + p_i48544_10_ * p_i48544_10_ + p_i48544_12_ * p_i48544_12_);
      this.accelerationX = p_i48544_8_ / d0 * 0.1D;
      this.accelerationY = p_i48544_10_ / d0 * 0.1D;
      this.accelerationZ = p_i48544_12_ / d0 * 0.1D;
   }

   public EntityFireball(EntityType<?> type, EntityLivingBase p_i48545_2_, double p_i48545_3_, double p_i48545_5_, double p_i48545_7_, World p_i48545_9_, float p_i48545_10_, float p_i48545_11_) {
      this(type, p_i48545_9_, p_i48545_10_, p_i48545_11_);
      this.shootingEntity = p_i48545_2_;
      this.setLocationAndAngles(p_i48545_2_.posX, p_i48545_2_.posY, p_i48545_2_.posZ, p_i48545_2_.rotationYaw, p_i48545_2_.rotationPitch);
      this.setPosition(this.posX, this.posY, this.posZ);
      this.motionX = 0.0D;
      this.motionY = 0.0D;
      this.motionZ = 0.0D;
      p_i48545_3_ = p_i48545_3_ + this.rand.nextGaussian() * 0.4D;
      p_i48545_5_ = p_i48545_5_ + this.rand.nextGaussian() * 0.4D;
      p_i48545_7_ = p_i48545_7_ + this.rand.nextGaussian() * 0.4D;
      double d0 = (double)MathHelper.sqrt(p_i48545_3_ * p_i48545_3_ + p_i48545_5_ * p_i48545_5_ + p_i48545_7_ * p_i48545_7_);
      this.accelerationX = p_i48545_3_ / d0 * 0.1D;
      this.accelerationY = p_i48545_5_ / d0 * 0.1D;
      this.accelerationZ = p_i48545_7_ / d0 * 0.1D;
   }

   protected void registerData() {
   }

   /**
    * Checks if the entity is in range to render.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double distance) {
      double d0 = this.getBoundingBox().getAverageEdgeLength() * 4.0D;
      if (Double.isNaN(d0)) {
         d0 = 4.0D;
      }

      d0 = d0 * 64.0D;
      return distance < d0 * d0;
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      if (this.world.isRemote || (this.shootingEntity == null || !this.shootingEntity.removed) && this.world.isBlockLoaded(new BlockPos(this))) {
         super.tick();
         if (this.isFireballFiery()) {
            this.setFire(1);
         }

         ++this.ticksInAir;
         RayTraceResult raytraceresult = ProjectileHelper.forwardsRaycast(this, true, this.ticksInAir >= 25, this.shootingEntity);
         if (raytraceresult != null && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            this.onImpact(raytraceresult);
         }

         this.posX += this.motionX;
         this.posY += this.motionY;
         this.posZ += this.motionZ;
         ProjectileHelper.rotateTowardsMovement(this, 0.2F);
         float f = this.getMotionFactor();
         if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
               float f1 = 0.25F;
               this.world.spawnParticle(Particles.BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ);
            }

            f = 0.8F;
         }

         this.motionX += this.accelerationX;
         this.motionY += this.accelerationY;
         this.motionZ += this.accelerationZ;
         this.motionX *= (double)f;
         this.motionY *= (double)f;
         this.motionZ *= (double)f;
         this.world.spawnParticle(this.func_195057_f(), this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
         this.setPosition(this.posX, this.posY, this.posZ);
      } else {
         this.remove();
      }
   }

   protected boolean isFireballFiery() {
      return true;
   }

   protected IParticleData func_195057_f() {
      return Particles.SMOKE;
   }

   /**
    * Return the motion factor for this projectile. The factor is multiplied by the original motion.
    */
   protected float getMotionFactor() {
      return 0.95F;
   }

   /**
    * Called when this EntityFireball hits a block or entity.
    */
   protected abstract void onImpact(RayTraceResult result);

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   public void writeAdditional(NBTTagCompound compound) {
      compound.setTag("direction", this.newDoubleNBTList(new double[]{this.motionX, this.motionY, this.motionZ}));
      compound.setTag("power", this.newDoubleNBTList(new double[]{this.accelerationX, this.accelerationY, this.accelerationZ}));
      compound.setInt("life", this.ticksAlive);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(NBTTagCompound compound) {
      if (compound.contains("power", 9)) {
         NBTTagList nbttaglist = compound.getList("power", 6);
         if (nbttaglist.size() == 3) {
            this.accelerationX = nbttaglist.getDouble(0);
            this.accelerationY = nbttaglist.getDouble(1);
            this.accelerationZ = nbttaglist.getDouble(2);
         }
      }

      this.ticksAlive = compound.getInt("life");
      if (compound.contains("direction", 9) && compound.getList("direction", 6).size() == 3) {
         NBTTagList nbttaglist1 = compound.getList("direction", 6);
         this.motionX = nbttaglist1.getDouble(0);
         this.motionY = nbttaglist1.getDouble(1);
         this.motionZ = nbttaglist1.getDouble(2);
      } else {
         this.remove();
      }

   }

   /**
    * Returns true if other Entities should be prevented from moving through this Entity.
    */
   public boolean canBeCollidedWith() {
      return true;
   }

   public float getCollisionBorderSize() {
      return 1.0F;
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else {
         this.markVelocityChanged();
         if (source.getTrueSource() != null) {
            Vec3d vec3d = source.getTrueSource().getLookVec();
            if (vec3d != null) {
               this.motionX = vec3d.x;
               this.motionY = vec3d.y;
               this.motionZ = vec3d.z;
               this.accelerationX = this.motionX * 0.1D;
               this.accelerationY = this.motionY * 0.1D;
               this.accelerationZ = this.motionZ * 0.1D;
            }

            if (source.getTrueSource() instanceof EntityLivingBase) {
               this.shootingEntity = (EntityLivingBase)source.getTrueSource();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   /**
    * Gets how bright this entity is.
    */
   public float getBrightness() {
      return 1.0F;
   }

   @OnlyIn(Dist.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }
}