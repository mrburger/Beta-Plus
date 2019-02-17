package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class EntityAIFindWater extends EntityAIBase {
   private final EntityCreature field_205152_a;

   public EntityAIFindWater(EntityCreature p_i48936_1_) {
      this.field_205152_a = p_i48936_1_;
   }

   /**
    * Returns whether the EntityAIBase should begin execution.
    */
   public boolean shouldExecute() {
      return this.field_205152_a.onGround && !this.field_205152_a.world.getFluidState(new BlockPos(this.field_205152_a)).isTagged(FluidTags.WATER);
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      BlockPos blockpos = null;

      for(BlockPos blockpos1 : BlockPos.MutableBlockPos.getAllInBoxMutable(MathHelper.floor(this.field_205152_a.posX - 2.0D), MathHelper.floor(this.field_205152_a.posY - 2.0D), MathHelper.floor(this.field_205152_a.posZ - 2.0D), MathHelper.floor(this.field_205152_a.posX + 2.0D), MathHelper.floor(this.field_205152_a.posY), MathHelper.floor(this.field_205152_a.posZ + 2.0D))) {
         if (this.field_205152_a.world.getFluidState(blockpos1).isTagged(FluidTags.WATER)) {
            blockpos = blockpos1;
            break;
         }
      }

      if (blockpos != null) {
         this.field_205152_a.getMoveHelper().setMoveTo((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), 1.0D);
      }

   }
}