package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;

public class EntityAIOpenDoor extends EntityAIDoorInteract {
   /** If the entity close the door */
   private final boolean closeDoor;
   /** The temporisation before the entity close the door (in ticks, always 20 = 1 second) */
   private int closeDoorTemporisation;

   public EntityAIOpenDoor(EntityLiving entitylivingIn, boolean shouldClose) {
      super(entitylivingIn);
      this.entity = entitylivingIn;
      this.closeDoor = shouldClose;
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return this.closeDoor && this.closeDoorTemporisation > 0 && super.shouldContinueExecuting();
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.closeDoorTemporisation = 20;
      this.func_195921_a(true);
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      this.func_195921_a(false);
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      --this.closeDoorTemporisation;
      super.tick();
   }
}