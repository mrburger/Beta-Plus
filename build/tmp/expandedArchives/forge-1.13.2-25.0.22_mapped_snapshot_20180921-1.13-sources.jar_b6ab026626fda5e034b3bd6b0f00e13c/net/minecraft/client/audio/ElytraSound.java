package net.minecraft.client.audio;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElytraSound extends MovingSound {
   private final EntityPlayerSP player;
   private int time;

   public ElytraSound(EntityPlayerSP playerIn) {
      super(SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.PLAYERS);
      this.player = playerIn;
      this.repeat = true;
      this.repeatDelay = 0;
      this.volume = 0.1F;
   }

   public void tick() {
      ++this.time;
      if (!this.player.removed && (this.time <= 20 || this.player.isElytraFlying())) {
         this.x = (float)this.player.posX;
         this.y = (float)this.player.posY;
         this.z = (float)this.player.posZ;
         float f = MathHelper.sqrt(this.player.motionX * this.player.motionX + this.player.motionZ * this.player.motionZ + this.player.motionY * this.player.motionY);
         float f1 = f / 2.0F;
         if ((double)f >= 0.01D) {
            this.volume = MathHelper.clamp(f1 * f1, 0.0F, 1.0F);
         } else {
            this.volume = 0.0F;
         }

         if (this.time < 20) {
            this.volume = 0.0F;
         } else if (this.time < 40) {
            this.volume = (float)((double)this.volume * ((double)(this.time - 20) / 20.0D));
         }

         float f2 = 0.8F;
         if (this.volume > 0.8F) {
            this.pitch = 1.0F + (this.volume - 0.8F);
         } else {
            this.pitch = 1.0F;
         }

      } else {
         this.donePlaying = true;
      }
   }
}