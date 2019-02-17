package net.minecraft.client.audio;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ITickable;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SoundHandler implements ITickable, IResourceManagerReloadListener {
   public static final Sound MISSING_SOUND = new Sound("meta:missing_sound", 1.0F, 1.0F, 1, Sound.Type.FILE, false, false, 16);
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer()).registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
   private static final ParameterizedType TYPE = new ParameterizedType() {
      public Type[] getActualTypeArguments() {
         return new Type[]{String.class, SoundList.class};
      }

      public Type getRawType() {
         return Map.class;
      }

      public Type getOwnerType() {
         return null;
      }
   };
   private final Map<ResourceLocation, SoundEventAccessor> soundRegistry = Maps.newHashMap();
   private final SoundManager sndManager;
   private final IResourceManager resourceManager;

   public SoundHandler(IResourceManager manager, GameSettings gameSettingsIn) {
      this.resourceManager = manager;
      this.sndManager = new SoundManager(this, gameSettingsIn);
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.soundRegistry.clear();

      java.util.List<net.minecraft.util.Tuple<ResourceLocation, SoundList>> resources = new java.util.LinkedList<>();
      for(String s : resourceManager.getResourceNamespaces()) {
         try {
            for(IResource iresource : resourceManager.getAllResources(new ResourceLocation(s, "sounds.json"))) {
               try {
                  Map<String, SoundList> map = this.getSoundMap(iresource.getInputStream());

                  for(Entry<String, SoundList> entry : map.entrySet()) {
                     resources.add(new net.minecraft.util.Tuple<>(new ResourceLocation(s, entry.getKey()), entry.getValue()));
                  }
               } catch (RuntimeException runtimeexception) {
                  LOGGER.warn("Invalid sounds.json in resourcepack: '{}'", iresource.getPackName(), runtimeexception);
               }
            }
         } catch (IOException var11) {
            ;
         }
      }
      try (net.minecraftforge.fml.common.progress.ProgressBar resourcesBar = net.minecraftforge.fml.common.progress.StartupProgressManager.start("Loading sounds", resources.size())) {
      resources.forEach(entry -> {
          resourcesBar.step(entry.getA().toString());
          try {
              this.loadSoundResource(entry.getA(), entry.getB());
          } catch (RuntimeException e) {
              LOGGER.warn("Invalid sounds.json", e);
          }
      });
      }; // Forge: end progress bar


      for(ResourceLocation resourcelocation : this.soundRegistry.keySet()) {
         SoundEventAccessor soundeventaccessor = this.soundRegistry.get(resourcelocation);
         if (soundeventaccessor.getSubtitle() instanceof TextComponentTranslation) {
            String s1 = ((TextComponentTranslation)soundeventaccessor.getSubtitle()).getKey();
            if (!I18n.hasKey(s1)) {
               LOGGER.debug("Missing subtitle {} for event: {}", s1, resourcelocation);
            }
         }
      }

      for(ResourceLocation resourcelocation1 : this.soundRegistry.keySet()) {
         if (IRegistry.field_212633_v.func_212608_b(resourcelocation1) == null) {
            LOGGER.debug("Not having sound event for: {}", (Object)resourcelocation1);
         }
      }

      this.sndManager.reload();
   }

   @Nullable
   protected Map<String, SoundList> getSoundMap(InputStream stream) {
      Map map;
      try {
         map = JsonUtils.fromJson(GSON, new InputStreamReader(stream, StandardCharsets.UTF_8), TYPE);
      } finally {
         IOUtils.closeQuietly(stream);
      }

      return map;
   }

   private void loadSoundResource(ResourceLocation location, SoundList sounds) {
      SoundEventAccessor soundeventaccessor = this.soundRegistry.get(location);
      boolean flag = soundeventaccessor == null;
      if (flag || sounds.canReplaceExisting()) {
         if (!flag) {
            LOGGER.debug("Replaced sound event location {}", (Object)location);
         }

         soundeventaccessor = new SoundEventAccessor(location, sounds.getSubtitle());
         this.soundRegistry.put(location, soundeventaccessor);
      }

      for(final Sound sound : sounds.getSounds()) {
         final ResourceLocation resourcelocation = sound.getSoundLocation();
         ISoundEventAccessor<Sound> isoundeventaccessor;
         switch(sound.getType()) {
         case FILE:
            if (!this.validateSoundResource(sound, location)) {
               continue;
            }

            isoundeventaccessor = sound;
            break;
         case SOUND_EVENT:
            isoundeventaccessor = new ISoundEventAccessor<Sound>() {
               public int getWeight() {
                  SoundEventAccessor soundeventaccessor1 = SoundHandler.this.soundRegistry.get(resourcelocation);
                  return soundeventaccessor1 == null ? 0 : soundeventaccessor1.getWeight();
               }

               public Sound cloneEntry() {
                  SoundEventAccessor soundeventaccessor1 = SoundHandler.this.soundRegistry.get(resourcelocation);
                  if (soundeventaccessor1 == null) {
                     return SoundHandler.MISSING_SOUND;
                  } else {
                     Sound sound1 = soundeventaccessor1.cloneEntry();
                     return new Sound(sound1.getSoundLocation().toString(), sound1.getVolume() * sound.getVolume(), sound1.getPitch() * sound.getPitch(), sound.getWeight(), Sound.Type.FILE, sound1.isStreaming() || sound.isStreaming(), sound1.shouldPreload(), sound1.getAttenuationDistance());
                  }
               }
            };
            break;
         default:
            throw new IllegalStateException("Unknown SoundEventRegistration type: " + sound.getType());
         }

         if (isoundeventaccessor.cloneEntry().shouldPreload()) {
            this.sndManager.enqueuePreload(isoundeventaccessor.cloneEntry());
         }

         soundeventaccessor.addSound(isoundeventaccessor);
      }

   }

   private boolean validateSoundResource(Sound soundIn, ResourceLocation p_184401_2_) {
      ResourceLocation resourcelocation = soundIn.getSoundAsOggLocation();
      IResource iresource = null;

      boolean flag;
      try {
         iresource = this.resourceManager.getResource(resourcelocation);
         iresource.getInputStream();
         return true;
      } catch (FileNotFoundException var11) {
         LOGGER.warn("File {} does not exist, cannot add it to event {}", resourcelocation, p_184401_2_);
         flag = false;
      } catch (IOException ioexception) {
         LOGGER.warn("Could not load sound file {}, cannot add it to event {}", resourcelocation, p_184401_2_, ioexception);
         flag = false;
         return flag;
      } finally {
         IOUtils.closeQuietly((Closeable)iresource);
      }

      return flag;
   }

   @Nullable
   public SoundEventAccessor getAccessor(ResourceLocation location) {
      return this.soundRegistry.get(location);
   }

   public Collection<ResourceLocation> getAvailableSounds() {
      return this.soundRegistry.keySet();
   }

   /**
    * Play a sound
    */
   public void play(ISound sound) {
      this.sndManager.play(sound);
   }

   /**
    * Plays the sound in n ticks
    */
   public void playDelayed(ISound sound, int delay) {
      this.sndManager.playDelayed(sound, delay);
   }

   public void setListener(EntityPlayer player, float partialTicks) {
      this.sndManager.setListener(player, partialTicks);
   }

   public void setListener(net.minecraft.entity.Entity entity, float partialTicks) {
      this.sndManager.setListener(entity, partialTicks);
   }

   public void pause() {
      this.sndManager.pause();
   }

   public void stop() {
      this.sndManager.stopAllSounds();
   }

   public void unloadSounds() {
      this.sndManager.unload();
   }

   public void tick() {
      this.sndManager.tick();
   }

   public void resume() {
      this.sndManager.resume();
   }

   public void setSoundLevel(SoundCategory category, float volume) {
      if (category == SoundCategory.MASTER && volume <= 0.0F) {
         this.stop();
      }

      this.sndManager.setVolume(category, volume);
   }

   public void stop(ISound soundIn) {
      this.sndManager.stop(soundIn);
   }

   public boolean isPlaying(ISound sound) {
      return this.sndManager.isPlaying(sound);
   }

   public void addListener(ISoundEventListener listener) {
      this.sndManager.addListener(listener);
   }

   public void removeListener(ISoundEventListener listener) {
      this.sndManager.removeListener(listener);
   }

   public void stop(@Nullable ResourceLocation id, @Nullable SoundCategory category) {
      this.sndManager.stop(id, category);
   }

   @Override
   public net.minecraftforge.resource.IResourceType getResourceType() {
      return net.minecraftforge.resource.VanillaResourceType.SOUNDS;
   }
}