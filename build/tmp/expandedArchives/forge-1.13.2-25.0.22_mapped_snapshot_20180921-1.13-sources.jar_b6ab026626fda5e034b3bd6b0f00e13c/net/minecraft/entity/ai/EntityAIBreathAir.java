package net.minecraft.entity.ai;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReaderBase;

public class EntityAIBreathAir extends EntityAIBase {
   private final EntityCreature field_205142_a;

   public EntityAIBreathAir(EntityCreature p_i48940_1_) {
      this.field_205142_a = p_i48940_1_;
      this.setMutexBits(3);
   }

   /**
    * Returns whether the EntityAIBase should begin execution.
    */
   public boolean shouldExecute() {
      return this.field_205142_a.getAir() < 140;
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return this.shouldExecute();
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
      this.func_205141_g();
   }

   private void func_205141_g() {
      Iterable<BlockPos.MutableBlockPos> iterable = BlockPos.MutableBlockPos.getAllInBoxMutable(MathHelper.floor(this.field_205142_a.posX - 1.0D), MathHelper.floor(this.field_205142_a.posY), MathHelper.floor(this.field_205142_a.posZ - 1.0D), MathHelper.floor(this.field_205142_a.posX + 1.0D), MathHelper.floor(this.field_205142_a.posY + 8.0D), MathHelper.floor(this.field_205142_a.posZ + 1.0D));
      BlockPos blockpos = null;

      for(BlockPos blockpos1 : iterable) {
         if (this.func_205140_a(this.field_205142_a.world, blockpos1)) {
            blockpos = blockpos1;
            break;
         }
      }

      if (blockpos == null) {
         blockpos = new BlockPos(this.field_205142_a.posX, this.field_205142_a.posY + 8.0D, this.field_205142_a.posZ);
      }

      this.field_205142_a.getNavigator().tryMoveToXYZ((double)blockpos.getX(), (double)(blockpos.getY() + 1), (double)blockpos.getZ(), 1.0D);
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      this.func_205141_g();
      this.field_205142_a.moveRelative(this.field_205142_a.moveStrafing, this.field_205142_a.moveVertical, this.field_205142_a.moveForward, 0.02F);
      this.field_205142_a.move(MoverType.SELF, this.field_205142_a.motionX, this.field_205142_a.motionY, this.field_205142_a.motionZ);
   }

   private boolean func_205140_a(IWorldReaderBase p_205140_1_, BlockPos p_205140_2_) {
      IBlockState iblockstate = p_205140_1_.getBlockState(p_205140_2_);
      return (p_205140_1_.getFluidState(p_205140_2_).isEmpty() || iblockstate.getBlock() == Blocks.BUBBLE_COLUMN) && iblockstate.allowsMovement(p_205140_1_, p_205140_2_, PathType.LAND);
   }
}