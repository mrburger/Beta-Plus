package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import javax.sound.sampled.AudioFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import paulscode.sound.Channel;
import paulscode.sound.FilenameURL;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.Source;

@OnlyIn(Dist.CLIENT)
public class SourceLWJGL3 extends Source {
   private ChannelLWJGL3 lwjgl3Channel;
   private IntBuffer soundData;
   private FloatBuffer listenerPositionBuffer;
   private FloatBuffer sourcePositionBuffer;
   private FloatBuffer velocityBuffer;

   public SourceLWJGL3(FloatBuffer positionBuffer, IntBuffer p_i48107_2_, boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, SoundBuffer soundBuffer, float x, float y, float z, int attModel, float distOrRoll, boolean temporary) {
      super(priority, toStream, toLoop, sourcename, filenameURL, soundBuffer, x, y, z, attModel, distOrRoll, temporary);
      this.lwjgl3Channel = (ChannelLWJGL3)this.channel;
      if (this.codec != null) {
         this.codec.reverseByteOrder(true);
      }

      this.listenerPositionBuffer = positionBuffer;
      this.soundData = p_i48107_2_;
      this.libraryType = LibraryLWJGL3.class;
      this.pitch = 1.0F;
      this.initBuffers();
   }

   public SourceLWJGL3(FloatBuffer p_i48108_1_, IntBuffer p_i48108_2_, Source p_i48108_3_, SoundBuffer p_i48108_4_) {
      super(p_i48108_3_, p_i48108_4_);
      this.lwjgl3Channel = (ChannelLWJGL3)this.channel;
      if (this.codec != null) {
         this.codec.reverseByteOrder(true);
      }

      this.listenerPositionBuffer = p_i48108_1_;
      this.soundData = p_i48108_2_;
      this.libraryType = LibraryLWJGL3.class;
      this.pitch = 1.0F;
      this.initBuffers();
   }

   public SourceLWJGL3(FloatBuffer p_i48109_1_, AudioFormat p_i48109_2_, boolean p_i48109_3_, String p_i48109_4_, float p_i48109_5_, float p_i48109_6_, float p_i48109_7_, int p_i48109_8_, float p_i48109_9_) {
      super(p_i48109_2_, p_i48109_3_, p_i48109_4_, p_i48109_5_, p_i48109_6_, p_i48109_7_, p_i48109_8_, p_i48109_9_);
      this.lwjgl3Channel = (ChannelLWJGL3)this.channel;
      this.listenerPositionBuffer = p_i48109_1_;
      this.libraryType = LibraryLWJGL3.class;
      this.pitch = 1.0F;
      this.initBuffers();
   }

   public boolean incrementSoundSequence() {
      if (!this.toStream) {
         this.errorMessage("Method 'incrementSoundSequence' may only be used for streaming sources.");
         return false;
      } else {
         synchronized(this.soundSequenceLock) {
            if (this.soundSequenceQueue != null && !this.soundSequenceQueue.isEmpty()) {
               this.filenameURL = this.soundSequenceQueue.remove(0);
               if (this.codec != null) {
                  this.codec.cleanup();
               }

               this.codec = SoundSystemConfig.getCodec(this.filenameURL.getFilename());
               if (this.codec == null) {
                  return true;
               } else {
                  this.codec.reverseByteOrder(true);
                  if (this.codec.getAudioFormat() == null) {
                     this.codec.initialize(this.filenameURL.getURL());
                  }

                  AudioFormat audioformat = this.codec.getAudioFormat();
                  if (audioformat == null) {
                     this.errorMessage("Audio Format null in method 'incrementSoundSequence'");
                     return false;
                  } else {
                     int i;
                     if (audioformat.getChannels() == 1) {
                        if (audioformat.getSampleSizeInBits() == 8) {
                           i = 4352;
                        } else {
                           if (audioformat.getSampleSizeInBits() != 16) {
                              this.errorMessage("Illegal sample size in method 'incrementSoundSequence'");
                              return false;
                           }

                           i = 4353;
                        }
                     } else {
                        if (audioformat.getChannels() != 2) {
                           this.errorMessage("Audio data neither mono nor stereo in method 'incrementSoundSequence'");
                           return false;
                        }

                        if (audioformat.getSampleSizeInBits() == 8) {
                           i = 4354;
                        } else {
                           if (audioformat.getSampleSizeInBits() != 16) {
                              this.errorMessage("Illegal sample size in method 'incrementSoundSequence'");
                              return false;
                           }

                           i = 4355;
                        }
                     }

                     this.lwjgl3Channel.setFormatAndSampleRate(i, (int)audioformat.getSampleRate());
                     this.preLoad = true;
                     return true;
                  }
               }
            } else {
               return false;
            }
         }
      }
   }

   public void listenerMoved() {
      this.positionChanged();
   }

   public void setPosition(float p_setPosition_1_, float p_setPosition_2_, float p_setPosition_3_) {
      super.setPosition(p_setPosition_1_, p_setPosition_2_, p_setPosition_3_);
      if (this.sourcePositionBuffer == null) {
         this.initBuffers();
      } else {
         this.positionChanged();
      }

      this.sourcePositionBuffer.put(0, p_setPosition_1_);
      this.sourcePositionBuffer.put(1, p_setPosition_2_);
      this.sourcePositionBuffer.put(2, p_setPosition_3_);
      if (this.channel != null && this.channel.attachedSource == this && this.lwjgl3Channel != null && this.lwjgl3Channel.buffer != null) {
         AL10.alSourcefv(this.lwjgl3Channel.buffer.get(0), 4100, this.sourcePositionBuffer);
         this.checkError();
      }

   }

   public void positionChanged() {
      this.updateDistance();
      this.updateGain();
      if (this.channel != null && this.channel.attachedSource == this && this.lwjgl3Channel != null && this.lwjgl3Channel.buffer != null) {
         AL10.alSourcef(this.lwjgl3Channel.buffer.get(0), 4106, this.gain * this.sourceVolume * Math.abs(this.fadeOutGain) * this.fadeInGain);
         this.checkError();
      }

      this.setPitch();
   }

   private void setPitch() {
      if (this.channel != null && this.channel.attachedSource == this && LibraryLWJGL3.isPitchSupported() && this.lwjgl3Channel != null && this.lwjgl3Channel.buffer != null) {
         AL10.alSourcef(this.lwjgl3Channel.buffer.get(0), 4099, this.pitch);
         this.checkError();
      }

   }

   public void setLooping(boolean p_setLooping_1_) {
      super.setLooping(p_setLooping_1_);
      if (this.channel != null && this.channel.attachedSource == this && this.lwjgl3Channel != null && this.lwjgl3Channel.buffer != null) {
         AL10.alSourcei(this.lwjgl3Channel.buffer.get(0), 4103, p_setLooping_1_ ? 1 : 0);
         this.checkError();
      }

   }

   public void setAttenuation(int p_setAttenuation_1_) {
      super.setAttenuation(p_setAttenuation_1_);
      if (this.channel != null && this.channel.attachedSource == this && this.lwjgl3Channel != null && this.lwjgl3Channel.buffer != null) {
         if (p_setAttenuation_1_ == 1) {
            AL10.alSourcef(this.lwjgl3Channel.buffer.get(0), 4129, this.distOrRoll);
         } else {
            AL10.alSourcef(this.lwjgl3Channel.buffer.get(0), 4129, 0.0F);
         }

         this.checkError();
      }

   }

   public void setDistOrRoll(float p_setDistOrRoll_1_) {
      super.setDistOrRoll(p_setDistOrRoll_1_);
      if (this.channel != null && this.channel.attachedSource == this && this.lwjgl3Channel != null && this.lwjgl3Channel.buffer != null) {
         if (this.attModel == 1) {
            AL10.alSourcef(this.lwjgl3Channel.buffer.get(0), 4129, p_setDistOrRoll_1_);
         } else {
            AL10.alSourcef(this.lwjgl3Channel.buffer.get(0), 4129, 0.0F);
         }

         this.checkError();
      }

   }

   public void setVelocity(float p_setVelocity_1_, float p_setVelocity_2_, float p_setVelocity_3_) {
      super.setVelocity(p_setVelocity_1_, p_setVelocity_2_, p_setVelocity_3_);
      this.velocityBuffer = BufferUtils.createFloatBuffer(3).put(new float[]{p_setVelocity_1_, p_setVelocity_2_, p_setVelocity_3_});
      this.velocityBuffer.flip();
      if (this.channel != null && this.channel.attachedSource == this && this.lwjgl3Channel != null && this.lwjgl3Channel.buffer != null) {
         AL10.alSourcefv(this.lwjgl3Channel.buffer.get(0), 4102, this.velocityBuffer);
         this.checkError();
      }

   }

   public void setPitch(float p_setPitch_1_) {
      super.setPitch(p_setPitch_1_);
      this.setPitch();
   }

   public void play(Channel p_play_1_) {
      if (!this.active()) {
         if (this.toLoop) {
            this.toPlay = true;
         }

      } else if (p_play_1_ == null) {
         this.errorMessage("Unable to play source, because channel was null");
      } else {
         boolean flag = this.channel != p_play_1_;
         if (this.channel != null && this.channel.attachedSource != this) {
            flag = true;
         }

         boolean flag1 = this.paused();
         super.play(p_play_1_);
         this.lwjgl3Channel = (ChannelLWJGL3)this.channel;
         if (flag) {
            this.setPosition(this.position.x, this.position.y, this.position.z);
            this.setPitch();
            if (this.lwjgl3Channel != null && this.lwjgl3Channel.buffer != null) {
               if (LibraryLWJGL3.isPitchSupported()) {
                  AL10.alSourcef(this.lwjgl3Channel.buffer.get(0), 4099, this.pitch);
                  this.checkError();
               }

               AL10.alSourcefv(this.lwjgl3Channel.buffer.get(0), 4100, this.sourcePositionBuffer);
               this.checkError();
               AL10.alSourcefv(this.lwjgl3Channel.buffer.get(0), 4102, this.velocityBuffer);
               this.checkError();
               if (this.attModel == 1) {
                  AL10.alSourcef(this.lwjgl3Channel.buffer.get(0), 4129, this.distOrRoll);
               } else {
                  AL10.alSourcef(this.lwjgl3Channel.buffer.get(0), 4129, 0.0F);
               }

               this.checkError();
               if (this.toLoop && !this.toStream) {
                  AL10.alSourcei(this.lwjgl3Channel.buffer.get(0), 4103, 1);
               } else {
                  AL10.alSourcei(this.lwjgl3Channel.buffer.get(0), 4103, 0);
               }

               this.checkError();
            }

            if (!this.toStream) {
               if (this.soundData == null) {
                  this.errorMessage("No sound buffer to play");
                  return;
               }

               this.lwjgl3Channel.attachSoundBuffer(this.soundData);
            }
         }

         if (!this.playing()) {
            if (this.toStream && !flag1) {
               if (this.codec == null) {
                  this.errorMessage("Decoder null in method 'play'");
                  return;
               }

               if (this.codec.getAudioFormat() == null) {
                  this.codec.initialize(this.filenameURL.getURL());
               }

               AudioFormat audioformat = this.codec.getAudioFormat();
               if (audioformat == null) {
                  this.errorMessage("Audio Format null in method 'play'");
                  return;
               }

               int i;
               if (audioformat.getChannels() == 1) {
                  if (audioformat.getSampleSizeInBits() == 8) {
                     i = 4352;
                  } else {
                     if (audioformat.getSampleSizeInBits() != 16) {
                        this.errorMessage("Illegal sample size in method 'play'");
                        return;
                     }

                     i = 4353;
                  }
               } else {
                  if (audioformat.getChannels() != 2) {
                     this.errorMessage("Audio data neither mono nor stereo in method 'play'");
                     return;
                  }

                  if (audioformat.getSampleSizeInBits() == 8) {
                     i = 4354;
                  } else {
                     if (audioformat.getSampleSizeInBits() != 16) {
                        this.errorMessage("Illegal sample size in method 'play'");
                        return;
                     }

                     i = 4355;
                  }
               }

               this.lwjgl3Channel.setFormatAndSampleRate(i, (int)audioformat.getSampleRate());
               this.preLoad = true;
            }

            this.channel.play();
            if (this.pitch != 1.0F) {
               this.setPitch();
            }
         }

      }
   }

   public boolean preLoad() {
      if (this.codec == null) {
         return false;
      } else {
         this.codec.initialize(this.filenameURL.getURL());
         LinkedList<byte[]> linkedlist = Lists.newLinkedList();

         for(int i = 0; i < SoundSystemConfig.getNumberStreamingBuffers(); ++i) {
            this.soundBuffer = this.codec.read();
            if (this.soundBuffer == null || this.soundBuffer.audioData == null) {
               break;
            }

            linkedlist.add(this.soundBuffer.audioData);
         }

         this.positionChanged();
         this.channel.preLoadBuffers(linkedlist);
         this.preLoad = false;
         return true;
      }
   }

   private void initBuffers() {
      this.sourcePositionBuffer = BufferUtils.createFloatBuffer(3).put(new float[]{this.position.x, this.position.y, this.position.z});
      this.velocityBuffer = BufferUtils.createFloatBuffer(3).put(new float[]{this.velocity.x, this.velocity.y, this.velocity.z});
      this.sourcePositionBuffer.flip();
      this.velocityBuffer.flip();
      this.positionChanged();
   }

   private void updateDistance() {
      if (this.listenerPositionBuffer != null) {
         double d0 = (double)(this.position.x - this.listenerPositionBuffer.get(0));
         double d1 = (double)(this.position.y - this.listenerPositionBuffer.get(1));
         double d2 = (double)(this.position.z - this.listenerPositionBuffer.get(2));
         this.distanceFromListener = (float)Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
      }

   }

   private void updateGain() {
      if (this.attModel == 2) {
         if (this.distanceFromListener <= 0.0F) {
            this.gain = 1.0F;
         } else if (this.distanceFromListener >= this.distOrRoll) {
            this.gain = 0.0F;
         } else {
            this.gain = 1.0F - this.distanceFromListener / this.distOrRoll;
         }

         if (this.gain > 1.0F) {
            this.gain = 1.0F;
         }

         if (this.gain < 0.0F) {
            this.gain = 0.0F;
         }
      } else {
         this.gain = 1.0F;
      }

   }

   private boolean checkError() {
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