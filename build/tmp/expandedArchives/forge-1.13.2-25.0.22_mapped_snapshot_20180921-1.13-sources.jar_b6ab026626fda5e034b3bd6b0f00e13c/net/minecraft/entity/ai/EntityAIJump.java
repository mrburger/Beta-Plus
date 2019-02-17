package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityDolphin;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class EntityAIJump extends EntityAIBase {
   private static final int[] field_211697_a = new int[]{0, 1, 4, 5, 6, 7};
   private final EntityDolphin dolphin;
   private final int field_205150_b;
   private boolean field_205151_c;

   public EntityAIJump(EntityDolphin p_i48938_1_, int p_i48938_2_) {
      this.dolphin = p_i48938_1_;
      this.field_205150_b = p_i48938_2_;
      this.setMutexBits(5);
   }

   /**
    * Returns whether the EntityAIBase should begin execution.
    */
   public boolean shouldExecute() {
      if (this.dolphin.getRNG().nextInt(this.field_205150_b) != 0) {
         return false;
      } else {
         EnumFacing enumfacing = this.dolphin.getAdjustedHorizontalFacing();
         int i = enumfacing.getXOffset();
         int j = enumfacing.getZOffset();
         BlockPos blockpos = new BlockPos(this.dolphin);

         for(int k : field_211697_a) {
            if (!this.func_211695_a(blockpos, i, j, k) || !this.func_211696_b(blockpos, i, j, k)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean func_211695_a(BlockPos p_211695_1_, int p_211695_2_, int p_211695_3_, int p_211695_4_) {
      BlockPos blockpos = p_211695_1_.add(p_211695_2_ * p_211695_4_, 0, p_211695_3_ * p_211695_4_);
      return this.dolphin.world.getFluidState(blockpos).isTagged(FluidTags.WATER) && !this.dolphin.world.getBlockState(blockpos).getMaterial().blocksMovement();
   }

   private boolean func_211696_b(BlockPos p_211696_1_, int p_211696_2_, int p_211696_3_, int p_211696_4_) {
      return this.dolphin.world.getBlockState(p_211696_1_.add(p_211696_2_ * p_211696_4_, 1, p_211696_3_ * p_211696_4_)).isAir() && this.dolphin.world.getBlockState(p_211696_1_.add(p_211696_2_ * p_211696_4_, 2, p_211696_3_ * p_211696_4_)).isAir();
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return (!(this.dolphin.motionY * this.dolphin.motionY < (double)0.03F) || this.dolphin.rotationPitch == 0.0F || !(Math.abs(this.dolphin.rotationPitch) < 10.0F) || !this.dolphin.isInWater()) && !this.dolphin.onGround;
   }

   /**
    * Determine if this AI Task is interruptible by a higher (= lower value) priority task. All vanilla AITask have this
    * value set to true.
    */
   public boolean isInterruptible() {
      return false;
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      EnumFacing enumfacing = this.dolphin.getAdjustedHorizontalFacing();
      this.dolphin.motionX += (double)enumfacing.getXOffset() * 0.6D;
      this.dolphin.motionY += 0.7D;
      this.dolphin.motionZ += (double)enumfacing.getZOffset() * 0.6D;
      this.dolphin.getNavigator().clearPath();
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      this.dolphin.rotationPitch = 0.0F;
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      boolean flag = this.field_205151_c;
      if (!flag) {
         IFluidState ifluidstate = this.dolphin.world.getFluidState(new BlockPos(this.dolphin));
         this.field_205151_c = ifluidstate.isTagged(FluidTags.WATER);
      }

      if (this.field_205151_c && !flag) {
         this.dolphin.playSound(SoundEvents.ENTITY_DOLPHIN_JUMP, 1.0F, 1.0F);
      }

      if (this.dolphin.motionY * this.dolphin.motionY < (double)0.03F && this.dolphin.rotationPitch != 0.0F) {
         this.dolphin.rotationPitch = this.func_205147_a(this.dolphin.rotationPitch, 0.0F, 0.2F);
      } else {
         double d2 = Math.sqrt(this.dolphin.motionX * this.dolphin.motionX + this.dolphin.motionY * this.dolphin.motionY + this.dolphin.motionZ * this.dolphin.motionZ);
         double d0 = Math.sqrt(this.dolphin.motionX * this.dolphin.motionX + this.dolphin.motionZ * this.dolphin.motionZ);
         double d1 = Math.signum(-this.dolphin.motionY) * Math.acos(d0 / d2) * (double)(180F / (float)Math.PI);
         this.dolphin.rotationPitch = (float)d1;
      }

   }

   protected float func_205147_a(float p_205147_1_, float p_205147_2_, float p_205147_3_) {
      float f;
      for(f = p_205147_2_ - p_205147_1_; f < -180.0F; f += 360.0F) {
         ;
      }

      while(f >= 180.0F) {
         f -= 360.0F;
      }

      return p_205147_1_ + p_205147_3_ * f;
   }
}