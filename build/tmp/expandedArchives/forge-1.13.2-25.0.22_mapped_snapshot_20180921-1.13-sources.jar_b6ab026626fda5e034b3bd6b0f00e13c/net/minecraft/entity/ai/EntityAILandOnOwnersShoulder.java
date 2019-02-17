package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityShoulderRiding;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAILandOnOwnersShoulder extends EntityAIBase {
   private final EntityShoulderRiding entity;
   private EntityPlayer owner;
   private boolean isSittingOnShoulder;

   public EntityAILandOnOwnersShoulder(EntityShoulderRiding entityIn) {
      this.entity = entityIn;
   }

   /**
    * Returns whether the EntityAIBase should begin execution.
    */
   public boolean shouldExecute() {
      EntityLivingBase entitylivingbase = this.entity.getOwner();
      boolean flag = entitylivingbase != null && !((EntityPlayer)entitylivingbase).isSpectator() && !((EntityPlayer)entitylivingbase).abilities.isFlying && !entitylivingbase.isInWater();
      return !this.entity.isSitting() && flag && this.entity.canSitOnShoulder();
   }

   /**
    * Determine if this AI Task is interruptible by a higher (= lower value) priority task. All vanilla AITask have this
    * value set to true.
    */
   public boolean isInterruptible() {
      return !this.isSittingOnShoulder;
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.owner = (EntityPlayer)this.entity.getOwner();
      this.isSittingOnShoulder = false;
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      if (!this.isSittingOnShoulder && !this.entity.isSitting() && !this.entity.getLeashed()) {
         if (this.entity.getBoundingBox().intersects(this.owner.getBoundingBox())) {
            this.isSittingOnShoulder = this.entity.setEntityOnShoulder(this.owner);
         }

      }
   }
}