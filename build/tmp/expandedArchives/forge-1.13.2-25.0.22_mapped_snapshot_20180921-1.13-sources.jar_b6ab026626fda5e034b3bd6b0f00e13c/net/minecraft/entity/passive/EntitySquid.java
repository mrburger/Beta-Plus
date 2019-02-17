package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntitySquid extends EntityWaterMob {
   public float squidPitch;
   public float prevSquidPitch;
   public float squidYaw;
   public float prevSquidYaw;
   /** appears to be rotation in radians; we already have pitch & yaw, so this completes the triumvirate. */
   public float squidRotation;
   /** previous squidRotation in radians */
   public float prevSquidRotation;
   /** angle of the tentacles in radians */
   public float tentacleAngle;
   /** the last calculated angle of the tentacles in radians */
   public float lastTentacleAngle;
   private float randomMotionSpeed;
   /** change in squidRotation in radians. */
   private float rotationVelocity;
   private float rotateSpeed;
   private float randomMotionVecX;
   private float randomMotionVecY;
   private float randomMotionVecZ;

   public EntitySquid(World worldIn) {
      super(EntityType.SQUID, worldIn);
      this.setSize(0.8F, 0.8F);
      this.rand.setSeed((long)(1 + this.getEntityId()));
      this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntitySquid.AIMoveRandom(this));
      this.tasks.addTask(1, new EntitySquid.AIFlee());
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
   }

   public float getEyeHeight() {
      return this.height * 0.5F;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SQUID_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_SQUID_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SQUID_DEATH;
   }

   /**
    * Returns the volume for the sounds this mob makes.
    */
   protected float getSoundVolume() {
      return 0.4F;
   }

   /**
    * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
    * prevent them from trampling crops
    */
   protected boolean canTriggerWalking() {
      return false;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_SQUID;
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      super.livingTick();
      this.prevSquidPitch = this.squidPitch;
      this.prevSquidYaw = this.squidYaw;
      this.prevSquidRotation = this.squidRotation;
      this.lastTentacleAngle = this.tentacleAngle;
      this.squidRotation += this.rotationVelocity;
      if ((double)this.squidRotation > (Math.PI * 2D)) {
         if (this.world.isRemote) {
            this.squidRotation = ((float)Math.PI * 2F);
         } else {
            this.squidRotation = (float)((double)this.squidRotation - (Math.PI * 2D));
            if (this.rand.nextInt(10) == 0) {
               this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
            }

            this.world.setEntityState(this, (byte)19);
         }
      }

      if (this.isInWaterOrBubbleColumn()) {
         if (this.squidRotation < (float)Math.PI) {
            float f = this.squidRotation / (float)Math.PI;
            this.tentacleAngle = MathHelper.sin(f * f * (float)Math.PI) * (float)Math.PI * 0.25F;
            if ((double)f > 0.75D) {
               this.randomMotionSpeed = 1.0F;
               this.rotateSpeed = 1.0F;
            } else {
               this.rotateSpeed *= 0.8F;
            }
         } else {
            this.tentacleAngle = 0.0F;
            this.randomMotionSpeed *= 0.9F;
            this.rotateSpeed *= 0.99F;
         }

         if (!this.world.isRemote) {
            this.motionX = (double)(this.randomMotionVecX * this.randomMotionSpeed);
            this.motionY = (double)(this.randomMotionVecY * this.randomMotionSpeed);
            this.motionZ = (double)(this.randomMotionVecZ * this.randomMotionSpeed);
         }

         float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.renderYawOffset += (-((float)MathHelper.atan2(this.motionX, this.motionZ)) * (180F / (float)Math.PI) - this.renderYawOffset) * 0.1F;
         this.rotationYaw = this.renderYawOffset;
         this.squidYaw = (float)((double)this.squidYaw + Math.PI * (double)this.rotateSpeed * 1.5D);
         this.squidPitch += (-((float)MathHelper.atan2((double)f1, this.motionY)) * (180F / (float)Math.PI) - this.squidPitch) * 0.1F;
      } else {
         this.tentacleAngle = MathHelper.abs(MathHelper.sin(this.squidRotation)) * (float)Math.PI * 0.25F;
         if (!this.world.isRemote) {
            this.motionX = 0.0D;
            this.motionZ = 0.0D;
            if (this.isPotionActive(MobEffects.LEVITATION)) {
               this.motionY += 0.05D * (double)(this.getActivePotionEffect(MobEffects.LEVITATION).getAmplifier() + 1) - this.motionY;
            } else if (!this.hasNoGravity()) {
               this.motionY -= 0.08D;
            }

            this.motionY *= (double)0.98F;
         }

         this.squidPitch = (float)((double)this.squidPitch + (double)(-90.0F - this.squidPitch) * 0.02D);
      }

   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (super.attackEntityFrom(source, amount) && this.getRevengeTarget() != null) {
         this.func_203039_dq();
         return true;
      } else {
         return false;
      }
   }

   private Vec3d func_207400_b(Vec3d p_207400_1_) {
      Vec3d vec3d = p_207400_1_.rotatePitch(this.prevSquidPitch * ((float)Math.PI / 180F));
      vec3d = vec3d.rotateYaw(-this.prevRenderYawOffset * ((float)Math.PI / 180F));
      return vec3d;
   }

   private void func_203039_dq() {
      this.playSound(SoundEvents.ENTITY_SQUID_SQUIRT, this.getSoundVolume(), this.getSoundPitch());
      Vec3d vec3d = this.func_207400_b(new Vec3d(0.0D, -1.0D, 0.0D)).add(this.posX, this.posY, this.posZ);

      for(int i = 0; i < 30; ++i) {
         Vec3d vec3d1 = this.func_207400_b(new Vec3d((double)this.rand.nextFloat() * 0.6D - 0.3D, -1.0D, (double)this.rand.nextFloat() * 0.6D - 0.3D));
         Vec3d vec3d2 = vec3d1.scale(0.3D + (double)(this.rand.nextFloat() * 2.0F));
         ((WorldServer)this.world).spawnParticle(Particles.SQUID_INK, vec3d.x, vec3d.y + 0.5D, vec3d.z, 0, vec3d2.x, vec3d2.y, vec3d2.z, (double)0.1F);
      }

   }

   public void travel(float strafe, float vertical, float forward) {
      this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
   }

   public boolean canSpawn(IWorld worldIn, boolean p_205020_2_) {
      return this.posY > 45.0D && this.posY < (double)worldIn.getSeaLevel();
   }

   /**
    * Handler for {@link World#setEntityState}
    */
   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 19) {
         this.squidRotation = 0.0F;
      } else {
         super.handleStatusUpdate(id);
      }

   }

   public void setMovementVector(float randomMotionVecXIn, float randomMotionVecYIn, float randomMotionVecZIn) {
      this.randomMotionVecX = randomMotionVecXIn;
      this.randomMotionVecY = randomMotionVecYIn;
      this.randomMotionVecZ = randomMotionVecZIn;
   }

   public boolean hasMovementVector() {
      return this.randomMotionVecX != 0.0F || this.randomMotionVecY != 0.0F || this.randomMotionVecZ != 0.0F;
   }

   class AIFlee extends EntityAIBase {
      private int field_203125_b;

      private AIFlee() {
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         EntityLivingBase entitylivingbase = EntitySquid.this.getRevengeTarget();
         if (EntitySquid.this.isInWater() && entitylivingbase != null) {
            return EntitySquid.this.getDistanceSq(entitylivingbase) < 100.0D;
         } else {
            return false;
         }
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.field_203125_b = 0;
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         ++this.field_203125_b;
         EntityLivingBase entitylivingbase = EntitySquid.this.getRevengeTarget();
         if (entitylivingbase != null) {
            Vec3d vec3d = new Vec3d(EntitySquid.this.posX - entitylivingbase.posX, EntitySquid.this.posY - entitylivingbase.posY, EntitySquid.this.posZ - entitylivingbase.posZ);
            IBlockState iblockstate = EntitySquid.this.world.getBlockState(new BlockPos(EntitySquid.this.posX + vec3d.x, EntitySquid.this.posY + vec3d.y, EntitySquid.this.posZ + vec3d.z));
            IFluidState ifluidstate = EntitySquid.this.world.getFluidState(new BlockPos(EntitySquid.this.posX + vec3d.x, EntitySquid.this.posY + vec3d.y, EntitySquid.this.posZ + vec3d.z));
            if (ifluidstate.isTagged(FluidTags.WATER) || iblockstate.isAir()) {
               double d0 = vec3d.length();
               if (d0 > 0.0D) {
                  vec3d.normalize();
                  float f = 3.0F;
                  if (d0 > 5.0D) {
                     f = (float)((double)f - (d0 - 5.0D) / 5.0D);
                  }

                  if (f > 0.0F) {
                     vec3d = vec3d.scale((double)f);
                  }
               }

               if (iblockstate.isAir()) {
                  vec3d = vec3d.subtract(0.0D, vec3d.y, 0.0D);
               }

               EntitySquid.this.setMovementVector((float)vec3d.x / 20.0F, (float)vec3d.y / 20.0F, (float)vec3d.z / 20.0F);
            }

            if (this.field_203125_b % 10 == 5) {
               EntitySquid.this.world.spawnParticle(Particles.BUBBLE, EntitySquid.this.posX, EntitySquid.this.posY, EntitySquid.this.posZ, 0.0D, 0.0D, 0.0D);
            }

         }
      }
   }

   class AIMoveRandom extends EntityAIBase {
      private final EntitySquid squid;

      public AIMoveRandom(EntitySquid p_i48823_2_) {
         this.squid = p_i48823_2_;
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         return true;
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         int i = this.squid.getIdleTime();
         if (i > 100) {
            this.squid.setMovementVector(0.0F, 0.0F, 0.0F);
         } else if (this.squid.getRNG().nextInt(50) == 0 || !this.squid.inWater || !this.squid.hasMovementVector()) {
            float f = this.squid.getRNG().nextFloat() * ((float)Math.PI * 2F);
            float f1 = MathHelper.cos(f) * 0.2F;
            float f2 = -0.1F + this.squid.getRNG().nextFloat() * 0.2F;
            float f3 = MathHelper.sin(f) * 0.2F;
            this.squid.setMovementVector(f1, f2, f3);
         }

      }
   }
}