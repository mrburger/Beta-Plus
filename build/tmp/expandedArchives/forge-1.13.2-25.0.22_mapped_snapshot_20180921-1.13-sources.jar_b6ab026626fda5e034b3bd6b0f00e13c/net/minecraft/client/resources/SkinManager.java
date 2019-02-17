package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkinManager {
   private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
   private final TextureManager textureManager;
   private final File skinCacheDir;
   private final MinecraftSessionService sessionService;
   private final LoadingCache<GameProfile, Map<Type, MinecraftProfileTexture>> skinCacheLoader;

   public SkinManager(TextureManager textureManagerInstance, File skinCacheDirectory, MinecraftSessionService sessionService) {
      this.textureManager = textureManagerInstance;
      this.skinCacheDir = skinCacheDirectory;
      this.sessionService = sessionService;
      this.skinCacheLoader = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader<GameProfile, Map<Type, MinecraftProfileTexture>>() {
         public Map<Type, MinecraftProfileTexture> load(GameProfile p_load_1_) throws Exception {
            try {
               return Minecraft.getInstance().getSessionService().getTextures(p_load_1_, false);
            } catch (Throwable var3) {
               return Maps.newHashMap();
            }
         }
      });
   }

   /**
    * Used in the Skull renderer to fetch a skin. May download the skin if it's not in the cache
    */
   public ResourceLocation loadSkin(MinecraftProfileTexture profileTexture, Type textureType) {
      return this.loadSkin(profileTexture, textureType, (SkinManager.SkinAvailableCallback)null);
   }

   /**
    * May download the skin if its not in the cache, can be passed a SkinManager#SkinAvailableCallback for handling
    */
   public ResourceLocation loadSkin(final MinecraftProfileTexture profileTexture, final Type textureType, @Nullable final SkinManager.SkinAvailableCallback skinAvailableCallback) {
      String s = Hashing.sha1().hashUnencodedChars(profileTexture.getHash()).toString();
      final ResourceLocation resourcelocation = new ResourceLocation("skins/" + s);
      ITextureObject itextureobject = this.textureManager.getTexture(resourcelocation);
      if (itextureobject != null) {
         if (skinAvailableCallback != null) {
            skinAvailableCallback.onSkinTextureAvailable(textureType, resourcelocation, profileTexture);
         }
      } else {
         File file1 = new File(this.skinCacheDir, s.length() > 2 ? s.substring(0, 2) : "xx");
         File file2 = new File(file1, s);
         final IImageBuffer iimagebuffer = textureType == Type.SKIN ? new ImageBufferDownload() : null;
         ThreadDownloadImageData threaddownloadimagedata = new ThreadDownloadImageData(file2, profileTexture.getUrl(), DefaultPlayerSkin.getDefaultSkinLegacy(), new IImageBuffer() {
            public NativeImage parseUserSkin(NativeImage nativeImageIn) {
               return iimagebuffer != null ? iimagebuffer.parseUserSkin(nativeImageIn) : nativeImageIn;
            }

            public void skinAvailable() {
               if (iimagebuffer != null) {
                  iimagebuffer.skinAvailable();
               }

               if (skinAvailableCallback != null) {
                  skinAvailableCallback.onSkinTextureAvailable(textureType, resourcelocation, profileTexture);
               }

            }
         });
         this.textureManager.loadTexture(resourcelocation, threaddownloadimagedata);
      }

      return resourcelocation;
   }

   public void loadProfileTextures(GameProfile profile, SkinManager.SkinAvailableCallback skinAvailableCallback, boolean requireSecure) {
      THREAD_POOL.submit(() -> {
         Map<Type, MinecraftProfileTexture> map = Maps.newHashMap();

         try {
            map.putAll(this.sessionService.getTextures(profile, requireSecure));
         } catch (InsecureTextureException var7) {
            ;
         }

         if (map.isEmpty()) {
            profile.getProperties().clear();
            if (profile.getId().equals(Minecraft.getInstance().getSession().getProfile().getId())) {
               profile.getProperties().putAll(Minecraft.getInstance().getProfileProperties());
               map.putAll(this.sessionService.getTextures(profile, false));
            } else {
               this.sessionService.fillProfileProperties(profile, requireSecure);

               try {
                  map.putAll(this.sessionService.getTextures(profile, requireSecure));
               } catch (InsecureTextureException var6) {
                  ;
               }
            }
         }

         Minecraft.getInstance().addScheduledTask(() -> {
            if (map.containsKey(Type.SKIN)) {
               this.loadSkin(map.get(Type.SKIN), Type.SKIN, skinAvailableCallback);
            }

            if (map.containsKey(Type.CAPE)) {
               this.loadSkin(map.get(Type.CAPE), Type.CAPE, skinAvailableCallback);
            }

         });
      });
   }

   public Map<Type, MinecraftProfileTexture> loadSkinFromCache(GameProfile profile) {
      return this.skinCacheLoader.getUnchecked(profile);
   }

   @OnlyIn(Dist.CLIENT)
   public interface SkinAvailableCallback {
      void onSkinTextureAvailable(Type p_onSkinTextureAvailable_1_, ResourceLocation p_onSkinTextureAvailable_2_, MinecraftProfileTexture p_onSkinTextureAvailable_3_);
   }
}