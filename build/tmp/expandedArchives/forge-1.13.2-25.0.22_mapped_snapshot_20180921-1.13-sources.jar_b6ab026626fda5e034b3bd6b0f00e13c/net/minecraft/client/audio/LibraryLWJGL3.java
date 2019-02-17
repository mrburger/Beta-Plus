package net.minecraft.client.audio;

import com.google.common.collect.Maps;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.sound.sampled.AudioFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import paulscode.sound.Channel;
import paulscode.sound.FilenameURL;
import paulscode.sound.ICodec;
import paulscode.sound.Library;
import paulscode.sound.ListenerData;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.Source;

@OnlyIn(Dist.CLIENT)
public class LibraryLWJGL3 extends Library {
   private FloatBuffer positionBuffer;
   private FloatBuffer orientationBuffer;
   private FloatBuffer velocityBuffer;
   private Map<String, IntBuffer> openAlBufferMap;
   private static boolean pitchSupported = true;
   private String state = "PreInit";
   private long deviceHandle;
   private long context;

   public LibraryLWJGL3() throws SoundSystemException {
      this.openAlBufferMap = Maps.newHashMap();
      this.reverseByteOrder = true;
   }

   public void init() throws SoundSystemException {
      boolean flag = false;
      long i = ALC10.alcOpenDevice((ByteBuffer)null);
      if (i == 0L) {
         throw new LibraryLWJGL3.LWJGL3SoundSystemException("Failed to open default device", 101);
      } else {
         ALCCapabilities alccapabilities = ALC.createCapabilities(i);
         if (!alccapabilities.OpenALC10) {
            throw new LibraryLWJGL3.LWJGL3SoundSystemException("OpenAL 1.0 not supported", 101);
         } else {
            this.context = ALC10.alcCreateContext(i, (IntBuffer)null);
            ALC10.alcMakeContextCurrent(this.context);
            AL.createCapabilities(alccapabilities);
            this.message("OpenAL initialized.");
            this.positionBuffer = BufferUtils.createFloatBuffer(3).put(new float[]{this.listener.position.x, this.listener.position.y, this.listener.position.z});
            this.orientationBuffer = BufferUtils.createFloatBuffer(6).put(new float[]{this.listener.lookAt.x, this.listener.lookAt.y, this.listener.lookAt.z, this.listener.up.x, this.listener.up.y, this.listener.up.z});
            this.velocityBuffer = BufferUtils.createFloatBuffer(3).put(new float[]{0.0F, 0.0F, 0.0F});
            this.positionBuffer.flip();
            this.orientationBuffer.flip();
            this.velocityBuffer.flip();
            this.state = "Post Init";
            AL10.alListenerfv(4100, this.positionBuffer);
            flag = this.checkError() || flag;
            AL10.alListenerfv(4111, this.orientationBuffer);
            flag = this.checkError() || flag;
            AL10.alListenerfv(4102, this.velocityBuffer);
            flag = this.checkError() || flag;
            AL10.alDopplerFactor(SoundSystemConfig.getDopplerFactor());
            flag = this.checkError() || flag;
            AL10.alDopplerVelocity(SoundSystemConfig.getDopplerVelocity());
            flag = this.checkError() || flag;
            if (flag) {
               this.importantMessage("OpenAL did not initialize properly!");
               throw new LibraryLWJGL3.LWJGL3SoundSystemException("Problem encountered while loading OpenAL or creating the listener. Probable cause: OpenAL not supported", 101);
            } else {
               super.init();
               ChannelLWJGL3 channellwjgl3 = (ChannelLWJGL3)this.normalChannels.get(1);

               try {
                  AL10.alSourcef(channellwjgl3.buffer.get(0), 4099, 1.0F);
                  if (this.checkError()) {
                     setPitchSupported(true, false);
                     throw new LibraryLWJGL3.LWJGL3SoundSystemException("OpenAL: AL_PITCH not supported.", 108);
                  }

                  setPitchSupported(true, true);
               } catch (Exception var7) {
                  setPitchSupported(true, false);
                  throw new LibraryLWJGL3.LWJGL3SoundSystemException("OpenAL: AL_PITCH not supported.", 108);
               }

               this.state = "Running";
            }
         }
      }
   }

   protected Channel createChannel(int p_createChannel_1_) {
      IntBuffer intbuffer = BufferUtils.createIntBuffer(1);

      try {
         AL10.alGenSources(intbuffer);
      } catch (Exception var4) {
         AL10.alGetError();
         return null;
      }

      return AL10.alGetError() != 0 ? null : new ChannelLWJGL3(p_createChannel_1_, intbuffer);
   }

   public void cleanup() {
      super.cleanup();

      for(String s : this.bufferMap.keySet()) {
         IntBuffer intbuffer = this.openAlBufferMap.get(s);
         if (intbuffer != null) {
            AL10.alDeleteBuffers(intbuffer);
            this.checkError();
            intbuffer.clear();
         }
      }

      this.bufferMap.clear();
      ALC10.alcDestroyContext(this.context);
      if (this.deviceHandle != 0L) {
         ALC10.alcCloseDevice(this.deviceHandle);
      }

      this.bufferMap = null;
      this.positionBuffer = null;
      this.orientationBuffer = null;
      this.velocityBuffer = null;
   }

   public boolean loadSound(FilenameURL p_loadSound_1_) {
      if (this.bufferMap == null) {
         this.bufferMap = Maps.newHashMap();
         this.importantMessage("Buffer Map was null in method 'loadSound'");
      }

      if (this.openAlBufferMap == null) {
         this.openAlBufferMap = Maps.newHashMap();
         this.importantMessage("Open AL Buffer Map was null in method 'loadSound'");
      }

      if (this.errorCheck(p_loadSound_1_ == null, "Filename/URL not specified in method 'loadSound'")) {
         return false;
      } else if (this.bufferMap.get(p_loadSound_1_.getFilename()) != null) {
         return true;
      } else {
         ICodec icodec = SoundSystemConfig.getCodec(p_loadSound_1_.getFilename());
         if (this.errorCheck(icodec == null, "No codec found for file '" + p_loadSound_1_.getFilename() + "' in method 'loadSound'")) {
            return false;
         } else {
            icodec.reverseByteOrder(true);
            URL url = p_loadSound_1_.getURL();
            if (this.errorCheck(url == null, "Unable to open file '" + p_loadSound_1_.getFilename() + "' in method 'loadSound'")) {
               return false;
            } else {
               icodec.initialize(url);
               SoundBuffer soundbuffer = icodec.readAll();
               icodec.cleanup();
               icodec = null;
               if (this.errorCheck(soundbuffer == null, "Sound buffer null in method 'loadSound'")) {
                  return false;
               } else {
                  this.bufferMap.put(p_loadSound_1_.getFilename(), soundbuffer);
                  AudioFormat audioformat = soundbuffer.audioFormat;
                  int i;
                  if (audioformat.getChannels() == 1) {
                     if (audioformat.getSampleSizeInBits() == 8) {
                        i = 4352;
                     } else {
                        if (audioformat.getSampleSizeInBits() != 16) {
                           this.errorMessage("Illegal sample size in method 'loadSound'");
                           return false;
                        }

                        i = 4353;
                     }
                  } else {
                     if (audioformat.getChannels() != 2) {
                        this.errorMessage("File neither mono nor stereo in method 'loadSound'");
                        return false;
                     }

                     if (audioformat.getSampleSizeInBits() == 8) {
                        i = 4354;
                     } else {
                        if (audioformat.getSampleSizeInBits() != 16) {
                           this.errorMessage("Illegal sample size in method 'loadSound'");
                           return false;
                        }

                        i = 4355;
                     }
                  }

                  IntBuffer intbuffer = BufferUtils.createIntBuffer(1);
                  AL10.alGenBuffers(intbuffer);
                  if (this.errorCheck(AL10.alGetError() != 0, "alGenBuffers error when loading " + p_loadSound_1_.getFilename())) {
                     return false;
                  } else {
                     AL10.alBufferData(intbuffer.get(0), i, (ByteBuffer)BufferUtils.createByteBuffer(soundbuffer.audioData.length).put(soundbuffer.audioData).flip(), (int)audioformat.getSampleRate());
                     if (this.errorCheck(AL10.alGetError() != 0, "alBufferData error when loading " + p_loadSound_1_.getFilename()) && this.errorCheck(intbuffer == null, "Sound buffer was not created for " + p_loadSound_1_.getFilename())) {
                        return false;
                     } else {
                        this.openAlBufferMap.put(p_loadSound_1_.getFilename(), intbuffer);
                        return true;
                     }
                  }
               }
            }
         }
      }
   }

   public boolean loadSound(SoundBuffer p_loadSound_1_, String p_loadSound_2_) {
      if (this.bufferMap == null) {
         this.bufferMap = Maps.newHashMap();
         this.importantMessage("Buffer Map was null in method 'loadSound'");
      }

      if (this.openAlBufferMap == null) {
         this.openAlBufferMap = Maps.newHashMap();
         this.importantMessage("Open AL Buffer Map was null in method 'loadSound'");
      }

      if (this.errorCheck(p_loadSound_2_ == null, "Identifier not specified in method 'loadSound'")) {
         return false;
      } else if (this.bufferMap.get(p_loadSound_2_) != null) {
         return true;
      } else if (this.errorCheck(p_loadSound_1_ == null, "Sound buffer null in method 'loadSound'")) {
         return false;
      } else {
         this.bufferMap.put(p_loadSound_2_, p_loadSound_1_);
         AudioFormat audioformat = p_loadSound_1_.audioFormat;
         int i;
         if (audioformat.getChannels() == 1) {
            if (audioformat.getSampleSizeInBits() == 8) {
               i = 4352;
            } else {
               if (audioformat.getSampleSizeInBits() != 16) {
                  this.errorMessage("Illegal sample size in method 'loadSound'");
                  return false;
               }

               i = 4353;
            }
         } else {
            if (audioformat.getChannels() != 2) {
               this.errorMessage("File neither mono nor stereo in method 'loadSound'");
               return false;
            }

            if (audioformat.getSampleSizeInBits() == 8) {
               i = 4354;
            } else {
               if (audioformat.getSampleSizeInBits() != 16) {
                  this.errorMessage("Illegal sample size in method 'loadSound'");
                  return false;
               }

               i = 4355;
            }
         }

         IntBuffer intbuffer = BufferUtils.createIntBuffer(1);
         AL10.alGenBuffers(intbuffer);
         if (this.errorCheck(AL10.alGetError() != 0, "alGenBuffers error when saving " + p_loadSound_2_)) {
            return false;
         } else {
            AL10.alBufferData(intbuffer.get(0), i, (ByteBuffer)BufferUtils.createByteBuffer(p_loadSound_1_.audioData.length).put(p_loadSound_1_.audioData).flip(), (int)audioformat.getSampleRate());
            if (this.errorCheck(AL10.alGetError() != 0, "alBufferData error when saving " + p_loadSound_2_) && this.errorCheck(intbuffer == null, "Sound buffer was not created for " + p_loadSound_2_)) {
               return false;
            } else {
               this.openAlBufferMap.put(p_loadSound_2_, intbuffer);
               return true;
            }
         }
      }
   }

   public void unloadSound(String p_unloadSound_1_) {
      this.openAlBufferMap.remove(p_unloadSound_1_);
      super.unloadSound(p_unloadSound_1_);
   }

   public void setMasterVolume(float p_setMasterVolume_1_) {
      super.setMasterVolume(p_setMasterVolume_1_);
      AL10.alListenerf(4106, p_setMasterVolume_1_);
      this.checkError();
   }

   public void newSource(boolean p_newSource_1_, boolean p_newSource_2_, boolean p_newSource_3_, String p_newSource_4_, FilenameURL p_newSource_5_, float p_newSource_6_, float p_newSource_7_, float p_newSource_8_, int p_newSource_9_, float p_newSource_10_) {
      IntBuffer intbuffer = null;
      if (!p_newSource_2_) {
         intbuffer = this.openAlBufferMap.get(p_newSource_5_.getFilename());
         if (intbuffer == null && !this.loadSound(p_newSource_5_)) {
            this.errorMessage(String.format("Source '%s' was not created because an error occurred while loading %s", p_newSource_4_, p_newSource_5_.getFilename()));
            return;
         }

         intbuffer = this.openAlBufferMap.get(p_newSource_5_.getFilename());
         if (intbuffer == null) {
            this.errorMessage(String.format("Source '%s' was not created because a sound buffer was not found for %s", p_newSource_4_, p_newSource_5_.getFilename()));
            return;
         }
      }

      SoundBuffer soundbuffer = null;
      if (!p_newSource_2_) {
         soundbuffer = this.bufferMap.get(p_newSource_5_.getFilename());
         if (soundbuffer == null && !this.loadSound(p_newSource_5_)) {
            this.errorMessage(String.format("Source '%s' was not created because an error occurred while loading %s", p_newSource_4_, p_newSource_5_.getFilename()));
            return;
         }

         soundbuffer = this.bufferMap.get(p_newSource_5_.getFilename());
         if (soundbuffer == null) {
            this.errorMessage(String.format("Source '%s' was not created because audio data was not found for %s", p_newSource_4_, p_newSource_5_.getFilename()));
            return;
         }
      }

      this.sourceMap.put(p_newSource_4_, new SourceLWJGL3(this.positionBuffer, intbuffer, p_newSource_1_, p_newSource_2_, p_newSource_3_, p_newSource_4_, p_newSource_5_, soundbuffer, p_newSource_6_, p_newSource_7_, p_newSource_8_, p_newSource_9_, p_newSource_10_, false));
   }

   public void rawDataStream(AudioFormat p_rawDataStream_1_, boolean p_rawDataStream_2_, String p_rawDataStream_3_, float p_rawDataStream_4_, float p_rawDataStream_5_, float p_rawDataStream_6_, int p_rawDataStream_7_, float p_rawDataStream_8_) {
      this.sourceMap.put(p_rawDataStream_3_, new SourceLWJGL3(this.positionBuffer, p_rawDataStream_1_, p_rawDataStream_2_, p_rawDataStream_3_, p_rawDataStream_4_, p_rawDataStream_5_, p_rawDataStream_6_, p_rawDataStream_7_, p_rawDataStream_8_));
   }

   public void quickPlay(boolean p_quickPlay_1_, boolean p_quickPlay_2_, boolean p_quickPlay_3_, String p_quickPlay_4_, FilenameURL p_quickPlay_5_, float p_quickPlay_6_, float p_quickPlay_7_, float p_quickPlay_8_, int p_quickPlay_9_, float p_quickPlay_10_, boolean p_quickPlay_11_) {
      IntBuffer intbuffer = null;
      if (!p_quickPlay_2_) {
         intbuffer = this.openAlBufferMap.get(p_quickPlay_5_.getFilename());
         if (intbuffer == null) {
            this.loadSound(p_quickPlay_5_);
         }

         intbuffer = this.openAlBufferMap.get(p_quickPlay_5_.getFilename());
         if (intbuffer == null) {
            this.errorMessage("Sound buffer was not created for " + p_quickPlay_5_.getFilename());
            return;
         }
      }

      SoundBuffer soundbuffer = null;
      if (!p_quickPlay_2_) {
         soundbuffer = this.bufferMap.get(p_quickPlay_5_.getFilename());
         if (soundbuffer == null && !this.loadSound(p_quickPlay_5_)) {
            this.errorMessage(String.format("Source '%s' was not created because an error occurred while loading %s", p_quickPlay_4_, p_quickPlay_5_.getFilename()));
            return;
         }

         soundbuffer = this.bufferMap.get(p_quickPlay_5_.getFilename());
         if (soundbuffer == null) {
            this.errorMessage(String.format("Source '%s' was not created because audio data was not found for %s", p_quickPlay_4_, p_quickPlay_5_.getFilename()));
            return;
         }
      }

      SourceLWJGL3 sourcelwjgl3 = new SourceLWJGL3(this.positionBuffer, intbuffer, p_quickPlay_1_, p_quickPlay_2_, p_quickPlay_3_, p_quickPlay_4_, p_quickPlay_5_, soundbuffer, p_quickPlay_6_, p_quickPlay_7_, p_quickPlay_8_, p_quickPlay_9_, p_quickPlay_10_, false);
      this.sourceMap.put(p_quickPlay_4_, sourcelwjgl3);
      this.play(sourcelwjgl3);
      if (p_quickPlay_11_) {
         sourcelwjgl3.setTemporary(true);
      }

   }

   public void copySources(HashMap<String, Source> p_copySources_1_) {
      if (p_copySources_1_ != null) {
         Set<String> set = p_copySources_1_.keySet();
         Iterator<String> iterator = set.iterator();
         if (this.bufferMap == null) {
            this.bufferMap = Maps.newHashMap();
            this.importantMessage("Buffer Map was null in method 'copySources'");
         }

         if (this.openAlBufferMap == null) {
            this.openAlBufferMap = Maps.newHashMap();
            this.importantMessage("Open AL Buffer Map was null in method 'copySources'");
         }

         this.sourceMap.clear();

         while(iterator.hasNext()) {
            String s = iterator.next();
            Source source = p_copySources_1_.get(s);
            if (source != null) {
               SoundBuffer soundbuffer = null;
               if (!source.toStream) {
                  this.loadSound(source.filenameURL);
                  soundbuffer = this.bufferMap.get(source.filenameURL.getFilename());
               }

               if (source.toStream || soundbuffer != null) {
                  this.sourceMap.put(s, new SourceLWJGL3(this.positionBuffer, this.openAlBufferMap.get(source.filenameURL.getFilename()), source, soundbuffer));
               }
            }
         }

      }
   }

   public void setListenerPosition(float p_setListenerPosition_1_, float p_setListenerPosition_2_, float p_setListenerPosition_3_) {
      super.setListenerPosition(p_setListenerPosition_1_, p_setListenerPosition_2_, p_setListenerPosition_3_);
      this.positionBuffer.put(0, p_setListenerPosition_1_);
      this.positionBuffer.put(1, p_setListenerPosition_2_);
      this.positionBuffer.put(2, p_setListenerPosition_3_);
      AL10.alListenerfv(4100, this.positionBuffer);
      this.checkError();
   }

   public void setListenerAngle(float p_setListenerAngle_1_) {
      super.setListenerAngle(p_setListenerAngle_1_);
      this.orientationBuffer.put(0, this.listener.lookAt.x);
      this.orientationBuffer.put(2, this.listener.lookAt.z);
      AL10.alListenerfv(4111, this.orientationBuffer);
      this.checkError();
   }

   public void setListenerOrientation(float p_setListenerOrientation_1_, float p_setListenerOrientation_2_, float p_setListenerOrientation_3_, float p_setListenerOrientation_4_, float p_setListenerOrientation_5_, float p_setListenerOrientation_6_) {
      super.setListenerOrientation(p_setListenerOrientation_1_, p_setListenerOrientation_2_, p_setListenerOrientation_3_, p_setListenerOrientation_4_, p_setListenerOrientation_5_, p_setListenerOrientation_6_);
      this.orientationBuffer.put(0, p_setListenerOrientation_1_);
      this.orientationBuffer.put(1, p_setListenerOrientation_2_);
      this.orientationBuffer.put(2, p_setListenerOrientation_3_);
      this.orientationBuffer.put(3, p_setListenerOrientation_4_);
      this.orientationBuffer.put(4, p_setListenerOrientation_5_);
      this.orientationBuffer.put(5, p_setListenerOrientation_6_);
      AL10.alListenerfv(4111, this.orientationBuffer);
      this.checkError();
   }

   public void setListenerData(ListenerData p_setListenerData_1_) {
      super.setListenerData(p_setListenerData_1_);
      this.positionBuffer.put(0, p_setListenerData_1_.position.x);
      this.positionBuffer.put(1, p_setListenerData_1_.position.y);
      this.positionBuffer.put(2, p_setListenerData_1_.position.z);
      AL10.alListenerfv(4100, this.positionBuffer);
      this.checkError();
      this.orientationBuffer.put(0, p_setListenerData_1_.lookAt.x);
      this.orientationBuffer.put(1, p_setListenerData_1_.lookAt.y);
      this.orientationBuffer.put(2, p_setListenerData_1_.lookAt.z);
      this.orientationBuffer.put(3, p_setListenerData_1_.up.x);
      this.orientationBuffer.put(4, p_setListenerData_1_.up.y);
      this.orientationBuffer.put(5, p_setListenerData_1_.up.z);
      AL10.alListenerfv(4111, this.orientationBuffer);
      this.checkError();
      this.velocityBuffer.put(0, p_setListenerData_1_.velocity.x);
      this.velocityBuffer.put(1, p_setListenerData_1_.velocity.y);
      this.velocityBuffer.put(2, p_setListenerData_1_.velocity.z);
      AL10.alListenerfv(4102, this.velocityBuffer);
      this.checkError();
   }

   public void setListenerVelocity(float p_setListenerVelocity_1_, float p_setListenerVelocity_2_, float p_setListenerVelocity_3_) {
      super.setListenerVelocity(p_setListenerVelocity_1_, p_setListenerVelocity_2_, p_setListenerVelocity_3_);
      this.velocityBuffer.put(0, this.listener.velocity.x);
      this.velocityBuffer.put(1, this.listener.velocity.y);
      this.velocityBuffer.put(2, this.listener.velocity.z);
      AL10.alListenerfv(4102, this.velocityBuffer);
   }

   public void dopplerChanged() {
      super.dopplerChanged();
      AL10.alDopplerFactor(SoundSystemConfig.getDopplerFactor());
      this.checkError();
      AL10.alDopplerVelocity(SoundSystemConfig.getDopplerVelocity());
      this.checkError();
   }

   private boolean checkError() {
      switch(AL10.alGetError()) {
      case 0:
         return false;
      case 40961:
         this.errorMessage("Invalid name parameter: " + this.state);
         return true;
      case 40962:
         this.errorMessage("Invalid parameter: " + this.state);
         return true;
      case 40963:
         this.errorMessage("Invalid enumerated parameter value: " + this.state);
         return true;
      case 40964:
         this.errorMessage("Illegal call: " + this.state);
         return true;
      case 40965:
         this.errorMessage("Unable to allocate memory: " + this.state);
         return true;
      default:
         this.errorMessage("An unrecognized error occurred: " + this.state);
         return true;
      }
   }

   public static boolean isPitchSupported() {
      return setPitchSupported(false, false);
   }

   private static synchronized boolean setPitchSupported(boolean doSet, boolean value) {
      if (doSet) {
         pitchSupported = value;
      }

      return pitchSupported;
   }

   public String getClassName() {
      return "LibraryLWJGL3";
   }

   @OnlyIn(Dist.CLIENT)
   public static class LWJGL3SoundSystemException extends SoundSystemException {
      public LWJGL3SoundSystemException(String p_i47627_1_) {
         super(p_i47627_1_);
      }

      public LWJGL3SoundSystemException(String p_i47628_1_, int p_i47628_2_) {
         super(p_i47628_1_, p_i47628_2_);
      }
   }
}