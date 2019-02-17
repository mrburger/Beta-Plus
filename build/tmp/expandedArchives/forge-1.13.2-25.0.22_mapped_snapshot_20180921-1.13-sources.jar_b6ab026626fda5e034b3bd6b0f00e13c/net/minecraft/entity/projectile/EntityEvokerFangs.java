package net.minecraft.entity.projectile;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityEvokerFangs extends Entity {
   private int warmupDelayTicks;
   private boolean sentSpikeEvent;
   private int lifeTicks = 22;
   private boolean clientSideAttackStarted;
   private EntityLivingBase caster;
   private UUID casterUuid;

   public EntityEvokerFangs(World worldIn) {
      super(EntityType.EVOKER_FANGS, worldIn);
      this.setSize(0.5F, 0.8F);
   }

   public EntityEvokerFangs(World worldIn, double x, double y, double z, float p_i47276_8_, int p_i47276_9_, EntityLivingBase casterIn) {
      this(worldIn);
      this.warmupDelayTicks = p_i47276_9_;
      this.setCaster(casterIn);
      this.rotationYaw = p_i47276_8_ * (180F / (float)Math.PI);
      this.setPosition(x, y, z);
   }

   protected void registerData() {
   }

   public void setCaster(@Nullable EntityLivingBase p_190549_1_) {
      this.caster = p_190549_1_;
      this.casterUuid = p_190549_1_ == null ? null : p_190549_1_.getUniqueID();
   }

   @Nullable
   public EntityLivingBase getCaster() {
      if (this.caster == null && this.casterUuid != null && this.world instanceof WorldServer) {
         Entity entity = ((WorldServer)this.world).getEntityFromUuid(this.casterUuid);
         if (entity instanceof EntityLivingBase) {
            this.caster = (EntityLivingBase)entity;
         }
      }

      return this.caster;
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   protected void readAdditional(NBTTagCompound compound) {
      this.warmupDelayTicks = compound.getInt("Warmup");
      if (compound.hasUniqueId("OwnerUUID")) {
         this.casterUuid = compound.getUniqueId("OwnerUUID");
      }

   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   protected void writeAdditional(NBTTagCompound compound) {
      compound.setInt("Warmup", this.warmupDelayTicks);
      if (this.casterUuid != null) {
         compound.setUniqueId("OwnerUUID", this.casterUuid);
      }

   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (this.world.isRemote) {
         if (this.clientSideAttackStarted) {
            --this.lifeTicks;
            if (this.lifeTicks == 14) {
               for(int i = 0; i < 12; ++i) {
                  double d0 = this.posX + (this.rand.nextDouble() * 2.0D - 1.0D) * (double)this.width * 0.5D;
                  double d1 = this.posY + 0.05D + this.rand.nextDouble();
                  double d2 = this.posZ + (this.rand.nextDouble() * 2.0D - 1.0D) * (double)this.width * 0.5D;
                  double d3 = (this.rand.nextDouble() * 2.0D - 1.0D) * 0.3D;
                  double d4 = 0.3D + this.rand.nextDouble() * 0.3D;
                  double d5 = (this.rand.nextDouble() * 2.0D - 1.0D) * 0.3D;
                  this.world.spawnParticle(Particles.CRIT, d0, d1 + 1.0D, d2, d3, d4, d5);
               }
            }
         }
      } else if (--this.warmupDelayTicks < 0) {
         if (this.warmupDelayTicks == -8) {
            for(EntityLivingBase entitylivingbase : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getBoundingBox().grow(0.2D, 0.0D, 0.2D))) {
               this.damage(entitylivingbase);
            }
         }

         if (!this.sentSpikeEvent) {
            this.world.setEntityState(this, (byte)4);
            this.sentSpikeEvent = true;
         }

         if (--this.lifeTicks < 0) {
            this.remove();
         }
      }

   }

   private void damage(EntityLivingBase p_190551_1_) {
      EntityLivingBase entitylivingbase = this.getCaster();
      if (p_190551_1_.isAlive() && !p_190551_1_.isInvulnerable() && p_190551_1_ != entitylivingbase) {
         if (entitylivingbase == null) {
            p_190551_1_.attackEntityFrom(DamageSource.MAGIC, 6.0F);
         } else {
            if (entitylivingbase.isOnSameTeam(p_190551_1_)) {
               return;
            }

            p_190551_1_.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, entitylivingbase), 6.0F);
         }

      }
   }

   /**
    * Handler for {@link World#setEntityState}
    */
   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      super.handleStatusUpdate(id);
      if (id == 4) {
         this.clientSideAttackStarted = true;
         if (!this.isSilent()) {
            this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_EVOKER_FANGS_ATTACK, this.getSoundCategory(), 1.0F, this.rand.nextFloat() * 0.2F + 0.85F, false);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float getAnimationProgress(float partialTicks) {
      if (!this.clientSideAttackStarted) {
         return 0.0F;
      } else {
         int i = this.lifeTicks - 2;
         return i <= 0 ? 1.0F : 1.0F - ((float)i - partialTicks) / 20.0F;
      }
   }
}