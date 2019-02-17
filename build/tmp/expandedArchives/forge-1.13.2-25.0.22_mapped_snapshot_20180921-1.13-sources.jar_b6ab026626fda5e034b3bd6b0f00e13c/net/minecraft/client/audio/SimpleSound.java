package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SimpleSound extends AbstractSound {
   public SimpleSound(SoundEvent soundIn, SoundCategory categoryIn, float volumeIn, float pitchIn, BlockPos pos) {
      this(soundIn, categoryIn, volumeIn, pitchIn, (float)pos.getX() + 0.5F, (float)pos.getY() + 0.5F, (float)pos.getZ() + 0.5F);
   }

   public static SimpleSound getMasterRecord(SoundEvent soundIn, float pitchIn) {
      return getRecord(soundIn, pitchIn, 0.25F);
   }

   public static SimpleSound getRecord(SoundEvent soundIn, float pitchIn, float volumeIn) {
      return new SimpleSound(soundIn, SoundCategory.MASTER, volumeIn, pitchIn, false, 0, ISound.AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
   }

   public static SimpleSound getMusicRecord(SoundEvent soundIn) {
      return new SimpleSound(soundIn, SoundCategory.MUSIC, 1.0F, 1.0F, false, 0, ISound.AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
   }

   public static SimpleSound getRecordSoundRecord(SoundEvent soundIn, float xIn, float yIn, float zIn) {
      return new SimpleSound(soundIn, SoundCategory.RECORDS, 4.0F, 1.0F, false, 0, ISound.AttenuationType.LINEAR, xIn, yIn, zIn);
   }

   public SimpleSound(SoundEvent soundIn, SoundCategory categoryIn, float volumeIn, float pitchIn, float xIn, float yIn, float zIn) {
      this(soundIn, categoryIn, volumeIn, pitchIn, false, 0, ISound.AttenuationType.LINEAR, xIn, yIn, zIn);
   }

   private SimpleSound(SoundEvent soundIn, SoundCategory categoryIn, float volumeIn, float pitchIn, boolean repeatIn, int repeatDelayIn, ISound.AttenuationType attenuationTypeIn, float xIn, float yIn, float zIn) {
      this(soundIn.getName(), categoryIn, volumeIn, pitchIn, repeatIn, repeatDelayIn, attenuationTypeIn, xIn, yIn, zIn);
   }

   public SimpleSound(ResourceLocation soundId, SoundCategory categoryIn, float volumeIn, float pitchIn, boolean repeatIn, int repeatDelayIn, ISound.AttenuationType attenuationTypeIn, float xIn, float yIn, float zIn) {
      super(soundId, categoryIn);
      this.volume = volumeIn;
      this.pitch = pitchIn;
      this.x = xIn;
      this.y = yIn;
      this.z = zIn;
      this.repeat = repeatIn;
      this.repeatDelay = repeatDelayIn;
      this.attenuationType = attenuationTypeIn;
   }
}