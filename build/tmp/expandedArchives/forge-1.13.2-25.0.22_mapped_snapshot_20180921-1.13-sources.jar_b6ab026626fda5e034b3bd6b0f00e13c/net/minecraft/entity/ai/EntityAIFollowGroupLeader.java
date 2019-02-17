package net.minecraft.entity.ai;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.passive.AbstractGroupFish;

public class EntityAIFollowGroupLeader extends EntityAIBase {
   private final AbstractGroupFish taskOwner;
   private int navigateTimer;
   private int field_212826_c;

   public EntityAIFollowGroupLeader(AbstractGroupFish p_i49857_1_) {
      this.taskOwner = p_i49857_1_;
      this.field_212826_c = this.func_212825_a(p_i49857_1_);
   }

   protected int func_212825_a(AbstractGroupFish p_212825_1_) {
      return 200 + p_212825_1_.getRNG().nextInt(200) % 20;
   }

   /**
    * Returns whether the EntityAIBase should begin execution.
    */
   public boolean shouldExecute() {
      if (this.taskOwner.func_212812_dE()) {
         return false;
      } else if (this.taskOwner.func_212802_dB()) {
         return true;
      } else if (this.field_212826_c > 0) {
         --this.field_212826_c;
         return false;
      } else {
         this.field_212826_c = this.func_212825_a(this.taskOwner);
         Predicate<AbstractGroupFish> predicate = (p_212824_0_) -> {
            return p_212824_0_.func_212811_dD() || !p_212824_0_.func_212802_dB();
         };
         List<AbstractGroupFish> list = this.taskOwner.world.getEntitiesWithinAABB(this.taskOwner.getClass(), this.taskOwner.getBoundingBox().grow(8.0D, 8.0D, 8.0D), predicate);
         AbstractGroupFish abstractgroupfish = list.stream().filter(AbstractGroupFish::func_212811_dD).findAny().orElse(this.taskOwner);
         abstractgroupfish.func_212810_a(list.stream().filter((p_212823_0_) -> {
            return !p_212823_0_.func_212802_dB();
         }));
         return this.taskOwner.func_212802_dB();
      }
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return this.taskOwner.func_212802_dB() && this.taskOwner.func_212809_dF();
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.navigateTimer = 0;
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      this.taskOwner.func_212808_dC();
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      if (--this.navigateTimer <= 0) {
         this.navigateTimer = 10;
         this.taskOwner.func_212805_dG();
      }
   }
}