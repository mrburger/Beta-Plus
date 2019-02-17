package net.minecraft.client.audio;

import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuardianSound extends MovingSound {
   private final EntityGuardian guardian;

   public GuardianSound(EntityGuardian guardian) {
      super(SoundEvents.ENTITY_GUARDIAN_ATTACK, SoundCategory.HOSTILE);
      this.guardian = guardian;
      this.attenuationType = ISound.AttenuationType.NONE;
      this.repeat = true;
      this.repeatDelay = 0;
   }

   public void tick() {
      if (!this.guardian.removed && this.guardian.hasTargetedEntity()) {
         this.x = (float)this.guardian.posX;
         this.y = (float)this.guardian.posY;
         this.z = (float)this.guardian.posZ;
         float f = this.guardian.getAttackAnimationScale(0.0F);
         this.volume = 0.0F + 1.0F * f * f;
         this.pitch = 0.7F + 0.5F * f;
      } else {
         this.donePlaying = true;
      }
   }
}