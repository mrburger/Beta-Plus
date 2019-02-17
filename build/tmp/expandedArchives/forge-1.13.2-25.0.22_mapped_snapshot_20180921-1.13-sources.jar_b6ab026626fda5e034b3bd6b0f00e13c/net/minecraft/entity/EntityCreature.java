package net.minecraft.entity;

import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class EntityCreature extends EntityLiving {
   private BlockPos homePosition = BlockPos.ORIGIN;
   /** If -1 there is no maximum distance */
   private float maximumHomeDistance = -1.0F;

   protected EntityCreature(EntityType<?> type, World p_i48575_2_) {
      super(type, p_i48575_2_);
   }

   public float getBlockPathWeight(BlockPos pos) {
      return this.getBlockPathWeight(pos, this.world);
   }

   public float getBlockPathWeight(BlockPos p_205022_1_, IWorldReaderBase worldIn) {
      return 0.0F;
   }

   public boolean canSpawn(IWorld worldIn, boolean p_205020_2_) {
      return super.canSpawn(worldIn, p_205020_2_) && this.getBlockPathWeight(new BlockPos(this.posX, this.getBoundingBox().minY, this.posZ), worldIn) >= 0.0F;
   }

   /**
    * if the entity got a PathEntity it returns true, else false
    */
   public boolean hasPath() {
      return !this.navigator.noPath();
   }

   public boolean isWithinHomeDistanceCurrentPosition() {
      return this.isWithinHomeDistanceFromPosition(new BlockPos(this));
   }

   public boolean isWithinHomeDistanceFromPosition(BlockPos pos) {
      if (this.maximumHomeDistance == -1.0F) {
         return true;
      } else {
         return this.homePosition.distanceSq(pos) < (double)(this.maximumHomeDistance * this.maximumHomeDistance);
      }
   }

   /**
    * Sets home position and max distance for it
    */
   public void setHomePosAndDistance(BlockPos pos, int distance) {
      this.homePosition = pos;
      this.maximumHomeDistance = (float)distance;
   }

   public BlockPos getHomePosition() {
      return this.homePosition;
   }

   public float getMaximumHomeDistance() {
      return this.maximumHomeDistance;
   }

   public void detachHome() {
      this.maximumHomeDistance = -1.0F;
   }

   /**
    * Returns whether a home area is defined for this entity.
    */
   public boolean hasHome() {
      return this.maximumHomeDistance != -1.0F;
   }

   /**
    * Applies logic related to leashes, for example dragging the entity or breaking the leash.
    */
   protected void updateLeashedState() {
      super.updateLeashedState();
      if (this.getLeashed() && this.getLeashHolder() != null && this.getLeashHolder().world == this.world) {
         Entity entity = this.getLeashHolder();
         this.setHomePosAndDistance(new BlockPos((int)entity.posX, (int)entity.posY, (int)entity.posZ), 5);
         float f = this.getDistance(entity);
         if (this instanceof EntityTameable && ((EntityTameable)this).isSitting()) {
            if (f > 10.0F) {
               this.clearLeashed(true, true);
            }

            return;
         }

         this.onLeashDistance(f);
         if (f > 10.0F) {
            this.clearLeashed(true, true);
            this.tasks.disableControlFlag(1);
         } else if (f > 6.0F) {
            double d0 = (entity.posX - this.posX) / (double)f;
            double d1 = (entity.posY - this.posY) / (double)f;
            double d2 = (entity.posZ - this.posZ) / (double)f;
            this.motionX += d0 * Math.abs(d0) * 0.4D;
            this.motionY += d1 * Math.abs(d1) * 0.4D;
            this.motionZ += d2 * Math.abs(d2) * 0.4D;
         } else {
            this.tasks.enableControlFlag(1);
            float f1 = 2.0F;
            Vec3d vec3d = (new Vec3d(entity.posX - this.posX, entity.posY - this.posY, entity.posZ - this.posZ)).normalize().scale((double)Math.max(f - 2.0F, 0.0F));
            this.getNavigator().tryMoveToXYZ(this.posX + vec3d.x, this.posY + vec3d.y, this.posZ + vec3d.z, this.followLeashSpeed());
         }
      }

   }

   protected double followLeashSpeed() {
      return 1.0D;
   }

   protected void onLeashDistance(float p_142017_1_) {
   }
}