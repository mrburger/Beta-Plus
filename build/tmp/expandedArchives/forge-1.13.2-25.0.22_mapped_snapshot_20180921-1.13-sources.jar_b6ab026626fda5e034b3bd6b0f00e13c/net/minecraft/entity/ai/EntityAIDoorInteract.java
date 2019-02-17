package net.minecraft.entity.ai;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;

public abstract class EntityAIDoorInteract extends EntityAIBase {
   protected EntityLiving entity;
   protected BlockPos doorPosition = BlockPos.ORIGIN;
   protected boolean field_195923_c;
   /** If is true then the Entity has stopped Door Interaction and compoleted the task. */
   private boolean hasStoppedDoorInteraction;
   private float entityPositionX;
   private float entityPositionZ;

   public EntityAIDoorInteract(EntityLiving entityIn) {
      this.entity = entityIn;
      if (!(entityIn.getNavigator() instanceof PathNavigateGround)) {
         throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
      }
   }

   protected boolean func_195922_f() {
      if (!this.field_195923_c) {
         return false;
      } else {
         IBlockState iblockstate = this.entity.world.getBlockState(this.doorPosition);
         if (!(iblockstate.getBlock() instanceof BlockDoor)) {
            this.field_195923_c = false;
            return false;
         } else {
            return iblockstate.get(BlockDoor.OPEN);
         }
      }
   }

   protected void func_195921_a(boolean p_195921_1_) {
      if (this.field_195923_c) {
         IBlockState iblockstate = this.entity.world.getBlockState(this.doorPosition);
         if (iblockstate.getBlock() instanceof BlockDoor) {
            ((BlockDoor)iblockstate.getBlock()).toggleDoor(this.entity.world, this.doorPosition, p_195921_1_);
         }
      }

   }

   /**
    * Returns whether the EntityAIBase should begin execution.
    */
   public boolean shouldExecute() {
      if (!this.entity.collidedHorizontally) {
         return false;
      } else {
         PathNavigateGround pathnavigateground = (PathNavigateGround)this.entity.getNavigator();
         Path path = pathnavigateground.getPath();
         if (path != null && !path.isFinished() && pathnavigateground.getEnterDoors()) {
            for(int i = 0; i < Math.min(path.getCurrentPathIndex() + 2, path.getCurrentPathLength()); ++i) {
               PathPoint pathpoint = path.getPathPointFromIndex(i);
               this.doorPosition = new BlockPos(pathpoint.x, pathpoint.y + 1, pathpoint.z);
               if (!(this.entity.getDistanceSq((double)this.doorPosition.getX(), this.entity.posY, (double)this.doorPosition.getZ()) > 2.25D)) {
                  this.field_195923_c = this.func_195920_a(this.doorPosition);
                  if (this.field_195923_c) {
                     return true;
                  }
               }
            }

            this.doorPosition = (new BlockPos(this.entity)).up();
            this.field_195923_c = this.func_195920_a(this.doorPosition);
            return this.field_195923_c;
         } else {
            return false;
         }
      }
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return !this.hasStoppedDoorInteraction;
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.hasStoppedDoorInteraction = false;
      this.entityPositionX = (float)((double)((float)this.doorPosition.getX() + 0.5F) - this.entity.posX);
      this.entityPositionZ = (float)((double)((float)this.doorPosition.getZ() + 0.5F) - this.entity.posZ);
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      float f = (float)((double)((float)this.doorPosition.getX() + 0.5F) - this.entity.posX);
      float f1 = (float)((double)((float)this.doorPosition.getZ() + 0.5F) - this.entity.posZ);
      float f2 = this.entityPositionX * f + this.entityPositionZ * f1;
      if (f2 < 0.0F) {
         this.hasStoppedDoorInteraction = true;
      }

   }

   private boolean func_195920_a(BlockPos p_195920_1_) {
      IBlockState iblockstate = this.entity.world.getBlockState(p_195920_1_);
      return iblockstate.getBlock() instanceof BlockDoor && iblockstate.getMaterial() == Material.WOOD;
   }
}