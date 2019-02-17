package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;

public class EntityAISwimming extends EntityAIBase {
   private final EntityLiving entity;

   public EntityAISwimming(EntityLiving entityIn) {
      this.entity = entityIn;
      this.setMutexBits(4);
      entityIn.getNavigator().setCanSwim(true);
   }

   /**
    * Returns whether the EntityAIBase should begin execution.
    */
   public boolean shouldExecute() {
      return this.entity.isInWater() && this.entity.getSubmergedHeight() > 0.4D || this.entity.isInLava();
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      if (this.entity.getRNG().nextFloat() < 0.8F) {
         this.entity.getJumpHelper().setJumping();
      }

   }
}