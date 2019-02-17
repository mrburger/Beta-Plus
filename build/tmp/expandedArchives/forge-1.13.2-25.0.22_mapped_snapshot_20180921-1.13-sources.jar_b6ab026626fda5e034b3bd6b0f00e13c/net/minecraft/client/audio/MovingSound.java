package net.minecraft.client.audio;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class MovingSound extends AbstractSound implements ITickableSound {
   protected boolean donePlaying;

   protected MovingSound(SoundEvent soundIn, SoundCategory categoryIn) {
      super(soundIn, categoryIn);
   }

   public boolean isDonePlaying() {
      return this.donePlaying;
   }
}