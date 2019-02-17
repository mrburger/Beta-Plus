package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;

public class EntityDolphinHelper extends EntityLookHelper {
   private final int field_205139_h;

   public EntityDolphinHelper(EntityLiving p_i48942_1_, int p_i48942_2_) {
      super(p_i48942_1_);
      this.field_205139_h = p_i48942_2_;
   }

   /**
    * Updates look
    */
   public void tick() {
      if (this.isLooking) {
         this.isLooking = false;
         double d0 = this.posX - this.entity.posX;
         double d1 = this.posY - (this.entity.posY + (double)this.entity.getEyeHeight());
         double d2 = this.posZ - this.entity.posZ;
         double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
         float f = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F + 20.0F;
         float f1 = (float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI))) + 10.0F;
         this.entity.rotationPitch = this.updateRotation(this.entity.rotationPitch, f1, this.deltaLookPitch);
         this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, f, this.deltaLookYaw);
      } else {
         if (this.entity.getNavigator().noPath()) {
            this.entity.rotationPitch = this.updateRotation(this.entity.rotationPitch, 0.0F, 5.0F);
         }

         this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, this.entity.renderYawOffset, this.deltaLookYaw);
      }

      float f2 = MathHelper.wrapDegrees(this.entity.rotationYawHead - this.entity.renderYawOffset);
      if (f2 < (float)(-this.field_205139_h)) {
         this.entity.renderYawOffset -= 4.0F;
      } else if (f2 > (float)this.field_205139_h) {
         this.entity.renderYawOffset += 4.0F;
      }

   }
}