package net.minecraft.client.audio;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MovingSoundMinecartRiding extends MovingSound {
   private final EntityPlayer player;
   private final EntityMinecart minecart;

   public MovingSoundMinecartRiding(EntityPlayer playerIn, EntityMinecart minecartIn) {
      super(SoundEvents.ENTITY_MINECART_INSIDE, SoundCategory.NEUTRAL);
      this.player = playerIn;
      this.minecart = minecartIn;
      this.attenuationType = ISound.AttenuationType.NONE;
      this.repeat = true;
      this.repeatDelay = 0;
   }

   public void tick() {
      if (!this.minecart.removed && this.player.isPassenger() && this.player.getRidingEntity() == this.minecart) {
         float f = MathHelper.sqrt(this.minecart.motionX * this.minecart.motionX + this.minecart.motionZ * this.minecart.motionZ);
         if ((double)f >= 0.01D) {
            this.volume = 0.0F + MathHelper.clamp(f, 0.0F, 1.0F) * 0.75F;
         } else {
            this.volume = 0.0F;
         }

      } else {
         this.donePlaying = true;
      }
   }
}