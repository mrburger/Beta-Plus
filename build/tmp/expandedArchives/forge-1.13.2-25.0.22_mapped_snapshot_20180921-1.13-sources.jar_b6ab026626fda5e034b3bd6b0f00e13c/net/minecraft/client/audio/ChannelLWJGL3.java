package net.minecraft.client.audio;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import javax.sound.sampled.AudioFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import paulscode.sound.Channel;

@OnlyIn(Dist.CLIENT)
public class ChannelLWJGL3 extends Channel {
   public IntBuffer buffer;
   public int bufferFormat;
   public int sampleRate;
   public float field_195854_d;

   public ChannelLWJGL3(int typeIn, IntBuffer bufferIn) {
      super(typeIn);
      this.libraryType = LibraryLWJGL3.class;
      this.buffer = bufferIn;
   }

   public void cleanup() {
      if (this.buffer != null) {
         try {
            AL10.alSourceStop(this.buffer.get(0));
            AL10.alGetError();
         } catch (Exception var3) {
            ;
         }

         try {
            AL10.alDeleteSources(this.buffer);
            AL10.alGetError();
         } catch (Exception var2) {
            ;
         }

         this.buffer.clear();
      }

      this.buffer = null;
      super.cleanup();
   }

   public boolean attachSoundBuffer(IntBuffer bufferIn) {
      if (this.errorCheck(this.channelType != 0, "Sound buffers may only be attached to normal sources.")) {
         return false;
      } else {
         AL10.alSourcei(this.buffer.get(0), 4105, bufferIn.get(0));
         if (this.attachedSource != null && this.attachedSource.soundBuffer != null && this.attachedSource.soundBuffer.audioFormat != null) {
            this.setAudioFormat(this.attachedSource.soundBuffer.audioFormat);
         }

         return this.checkForError();
      }
   }

   public void setAudioFormat(AudioFormat p_setAudioFormat_1_) {
      int i;
      if (p_setAudioFormat_1_.getChannels() == 1) {
         if (p_setAudioFormat_1_.getSampleSizeInBits() == 8) {
            i = 4352;
         } else {
            if (p_setAudioFormat_1_.getSampleSizeInBits() != 16) {
               this.errorMessage("Illegal sample size in method 'setAudioFormat'");
               return;
            }

            i = 4353;
         }
      } else {
         if (p_setAudioFormat_1_.getChannels() != 2) {
            this.errorMessage("Audio data neither mono nor stereo in method 'setAudioFormat'");
            return;
         }

         if (p_setAudioFormat_1_.getSampleSizeInBits() == 8) {
            i = 4354;
         } else {
            if (p_setAudioFormat_1_.getSampleSizeInBits() != 16) {
               this.errorMessage("Illegal sample size in method 'setAudioFormat'");
               return;
            }

            i = 4355;
         }
      }

      this.bufferFormat = i;
      this.sampleRate = (int)p_setAudioFormat_1_.getSampleRate();
   }

   public void setFormatAndSampleRate(int bufferFormatIn, int sampleRateIn) {
      this.bufferFormat = bufferFormatIn;
      this.sampleRate = sampleRateIn;
   }

   public boolean preLoadBuffers(LinkedList<byte[]> p_preLoadBuffers_1_) {
      if (this.errorCheck(this.channelType != 1, "Buffers may only be queued for streaming sources.")) {
         return false;
      } else if (this.errorCheck(p_preLoadBuffers_1_ == null, "Buffer List null in method 'preLoadBuffers'")) {
         return false;
      } else {
         boolean flag = this.playing();
         if (flag) {
            AL10.alSourceStop(this.buffer.get(0));
            this.checkForError();
         }

         int i = AL10.alGetSourcei(this.buffer.get(0), 4118);
         if (i > 0) {
            IntBuffer intbuffer = BufferUtils.createIntBuffer(i);
            AL10.alGenBuffers(intbuffer);
            if (this.errorCheck(this.checkForError(), "Error clearing stream buffers in method 'preLoadBuffers'")) {
               return false;
            }

            AL10.alSourceUnqueueBuffers(this.buffer.get(0), intbuffer);
            if (this.errorCheck(this.checkForError(), "Error unqueuing stream buffers in method 'preLoadBuffers'")) {
               return false;
            }
         }

         if (flag) {
            AL10.alSourcePlay(this.buffer.get(0));
            this.checkForError();
         }

         IntBuffer intbuffer1 = BufferUtils.createIntBuffer(p_preLoadBuffers_1_.size());
         AL10.alGenBuffers(intbuffer1);
         if (this.errorCheck(this.checkForError(), "Error generating stream buffers in method 'preLoadBuffers'")) {
            return false;
         } else {
            for(int j = 0; j < p_preLoadBuffers_1_.size(); ++j) {
               ByteBuffer bytebuffer = (ByteBuffer)BufferUtils.createByteBuffer(((byte[])p_preLoadBuffers_1_.get(j)).length).put(p_preLoadBuffers_1_.get(j)).flip();

               try {
                  AL10.alBufferData(intbuffer1.get(j), this.bufferFormat, bytebuffer, this.sampleRate);
               } catch (Exception exception1) {
                  this.errorMessage("Error creating buffers in method 'preLoadBuffers'");
                  this.printStackTrace(exception1);
                  return false;
               }

               if (this.errorCheck(this.checkForError(), "Error creating buffers in method 'preLoadBuffers'")) {
                  return false;
               }
            }

            try {
               AL10.alSourceQueueBuffers(this.buffer.get(0), intbuffer1);
            } catch (Exception exception) {
               this.errorMessage("Error queuing buffers in method 'preLoadBuffers'");
               this.printStackTrace(exception);
               return false;
            }

            if (this.errorCheck(this.checkForError(), "Error queuing buffers in method 'preLoadBuffers'")) {
               return false;
            } else {
               AL10.alSourcePlay(this.buffer.get(0));
               return !this.errorCheck(this.checkForError(), "Error playing source in method 'preLoadBuffers'");
            }
         }
      }
   }

   public boolean queueBuffer(byte[] p_queueBuffer_1_) {
      if (this.errorCheck(this.channelType != 1, "Buffers may only be queued for streaming sources.")) {
         return false;
      } else {
         ByteBuffer bytebuffer = (ByteBuffer)BufferUtils.createByteBuffer(p_queueBuffer_1_.length).put(p_queueBuffer_1_).flip();
         IntBuffer intbuffer = BufferUtils.createIntBuffer(1);
         AL10.alSourceUnqueueBuffers(this.buffer.get(0), intbuffer);
         if (this.checkForError()) {
            return false;
         } else {
            if (AL10.alIsBuffer(intbuffer.get(0))) {
               this.field_195854_d += this.getPlayTimeMs(intbuffer.get(0));
            }

            this.checkForError();
            AL10.alBufferData(intbuffer.get(0), this.bufferFormat, bytebuffer, this.sampleRate);
            if (this.checkForError()) {
               return false;
            } else {
               AL10.alSourceQueueBuffers(this.buffer.get(0), intbuffer);
               return !this.checkForError();
            }
         }
      }
   }

   public int feedRawAudioData(byte[] p_feedRawAudioData_1_) {
      if (this.errorCheck(this.channelType != 1, "Raw audio data can only be fed to streaming sources.")) {
         return -1;
      } else {
         ByteBuffer bytebuffer = (ByteBuffer)BufferUtils.createByteBuffer(p_feedRawAudioData_1_.length).put(p_feedRawAudioData_1_).flip();
         int i = AL10.alGetSourcei(this.buffer.get(0), 4118);
         IntBuffer intbuffer;
         if (i > 0) {
            intbuffer = BufferUtils.createIntBuffer(i);
            AL10.alGenBuffers(intbuffer);
            if (this.errorCheck(this.checkForError(), "Error clearing stream buffers in method 'feedRawAudioData'")) {
               return -1;
            }

            AL10.alSourceUnqueueBuffers(this.buffer.get(0), intbuffer);
            if (this.errorCheck(this.checkForError(), "Error unqueuing stream buffers in method 'feedRawAudioData'")) {
               return -1;
            }

            if (AL10.alIsBuffer(intbuffer.get(0))) {
               this.field_195854_d += this.getPlayTimeMs(intbuffer.get(0));
            }

            this.checkForError();
         } else {
            intbuffer = BufferUtils.createIntBuffer(1);
            AL10.alGenBuffers(intbuffer);
            if (this.errorCheck(this.checkForError(), "Error generating stream buffers in method 'preLoadBuffers'")) {
               return -1;
            }
         }

         AL10.alBufferData(intbuffer.get(0), this.bufferFormat, bytebuffer, this.sampleRate);
         if (this.checkForError()) {
            return -1;
         } else {
            AL10.alSourceQueueBuffers(this.buffer.get(0), intbuffer);
            if (this.checkForError()) {
               return -1;
            } else {
               if (this.attachedSource != null && this.attachedSource.channel == this && this.attachedSource.active() && !this.playing()) {
                  AL10.alSourcePlay(this.buffer.get(0));
                  this.checkForError();
               }

               return i;
            }
         }
      }
   }

   /**
    * Returns how long it takes to play the entire buffer specified, in ms
    */
   public float getPlayTimeMs(int bufferIn) {
      return (float)(1000 * AL10.alGetBufferi(bufferIn, 8196) / AL10.alGetBufferi(bufferIn, 8195)) / ((float)AL10.alGetBufferi(bufferIn, 8194) / 8.0F) / (float)this.sampleRate;
   }

   public float millisecondsPlayed() {
      float f = (float)AL10.alGetSourcei(this.buffer.get(0), 4134);
      float f1 = 1.0F;
      switch(this.bufferFormat) {
      case 4352:
         f1 = 1.0F;
         break;
      case 4353:
         f1 = 2.0F;
         break;
      case 4354:
         f1 = 2.0F;
         break;
      case 4355:
         f1 = 4.0F;
      }

      f = f / f1 / (float)this.sampleRate * 1000.0F;
      if (this.channelType == 1) {
         f += this.field_195854_d;
      }

      return f;
   }

   public int buffersProcessed() {
      if (this.channelType != 1) {
         return 0;
      } else {
         int i = AL10.alGetSourcei(this.buffer.get(0), 4118);
         return this.checkForError() ? 0 : i;
      }
   }

   public void flush() {
      if (this.channelType == 1) {
         int i = AL10.alGetSourcei(this.buffer.get(0), 4117);
         if (!this.checkForError()) {
            for(IntBuffer intbuffer = BufferUtils.createIntBuffer(1); i > 0; --i) {
               try {
                  AL10.alSourceUnqueueBuffers(this.buffer.get(0), intbuffer);
               } catch (Exception var4) {
                  return;
               }

               if (this.checkForError()) {
                  return;
               }
            }

            this.field_195854_d = 0.0F;
         }
      }
   }

   public void close() {
      try {
         AL10.alSourceStop(this.buffer.get(0));
         AL10.alGetError();
      } catch (Exception var2) {
         ;
      }

      if (this.channelType == 1) {
         this.flush();
      }

   }

   public void play() {
      AL10.alSourcePlay(this.buffer.get(0));
      this.checkForError();
   }

   public void pause() {
      AL10.alSourcePause(this.buffer.get(0));
      this.checkForError();
   }

   public void stop() {
      AL10.alSourceStop(this.buffer.get(0));
      if (!this.checkForError()) {
         this.field_195854_d = 0.0F;
      }

   }

   public void rewind() {
      if (this.channelType != 1) {
         AL10.alSourceRewind(this.buffer.get(0));
         if (!this.checkForError()) {
            this.field_195854_d = 0.0F;
         }

      }
   }

   public boolean playing() {
      int i = AL10.alGetSourcei(this.buffer.get(0), 4112);
      if (this.checkForError()) {
         return false;
      } else {
         return i == 4114;
      }
   }

   private boolean checkForError() {
      switch(AL10.alGetError()) {
      case 0:
         return false;
      case 40961:
         this.errorMessage("Invalid name parameter.");
         return true;
      case 40962:
         this.errorMessage("Invalid parameter.");
         return true;
      case 40963:
         this.errorMessage("Invalid enumerated parameter value.");
         return true;
      case 40964:
         this.errorMessage("Illegal call.");
         return true;
      case 40965:
         this.errorMessage("Unable to allocate memory.");
         return true;
      default:
         this.errorMessage("An unrecognized error occurred.");
         return true;
      }
   }
}