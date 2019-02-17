package net.minecraft.entity.ai;

import java.util.function.Predicate;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAITargetNonTamed<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T> {
   private final EntityTameable tameable;

   public EntityAITargetNonTamed(EntityTameable p_i48571_1_, Class<T> p_i48571_2_, boolean p_i48571_3_, Predicate<? super T> p_i48571_4_) {
      super(p_i48571_1_, p_i48571_2_, 10, p_i48571_3_, false, p_i48571_4_);
      this.tameable = p_i48571_1_;
   }

   /**
    * Returns whether the EntityAIBase should begin execution.
    */
   public boolean shouldExecute() {
      return !this.tameable.isTamed() && super.shouldExecute();
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return this.targetEntitySelector != null ? this.targetEntitySelector.test(this.targetEntity) : super.shouldContinueExecuting();
   }
}