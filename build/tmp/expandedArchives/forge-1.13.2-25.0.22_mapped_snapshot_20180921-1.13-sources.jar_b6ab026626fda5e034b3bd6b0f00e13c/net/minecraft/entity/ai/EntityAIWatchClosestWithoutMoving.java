package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class EntityAIWatchClosestWithoutMoving extends EntityAIWatchClosest {
   public EntityAIWatchClosestWithoutMoving(EntityLiving entitylivingIn, Class<? extends Entity> watchTargetClass, float maxDistance, float chanceIn) {
      super(entitylivingIn, watchTargetClass, maxDistance, chanceIn);
      this.setMutexBits(3);
   }
}