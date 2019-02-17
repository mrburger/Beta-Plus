package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class TextureMap extends AbstractTexture implements ITickableTextureObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ResourceLocation LOCATION_BLOCKS_TEXTURE = new ResourceLocation("textures/atlas/blocks.png");
   private final List<TextureAtlasSprite> listAnimatedSprites = Lists.newArrayList();
   private final Set<ResourceLocation> sprites = Sets.newHashSet();
   private final Map<ResourceLocation, TextureAtlasSprite> mapUploadedSprites = Maps.newHashMap();
   private final String basePath;
   private int mipmapLevels;
   private final TextureAtlasSprite missingImage = MissingTextureSprite.getSprite();

   public TextureMap(String basePathIn) {
      this.basePath = basePathIn;
   }

   public void loadTexture(IResourceManager manager) throws IOException {
   }

   public void stitch(IResourceManager manager, Iterable<ResourceLocation> locations) {
      this.sprites.clear();
      net.minecraftforge.client.ForgeHooksClient.onTextureStitchedPre(this);
      locations.forEach((p_195423_2_) -> {
         this.registerSprite(manager, p_195423_2_);
      });
      this.stitch(manager);
   }

   public void stitch(IResourceManager manager) {
      int i = Minecraft.getGLMaximumTextureSize();
      Stitcher stitcher = new Stitcher(i, i, 0, this.mipmapLevels);
      this.clear();
      int j = Integer.MAX_VALUE;
      int k = 1 << this.mipmapLevels;

      LOGGER.info("Max texture size: {}", i);
      try (net.minecraftforge.fml.common.progress.ProgressBar textureLoadingBar = net.minecraftforge.fml.common.progress.StartupProgressManager.start("Texture loading", this.sprites.size())) {
      loadedSprites.clear();
      for(ResourceLocation resourcelocation : Sets.newHashSet(this.sprites)) {
         textureLoadingBar.step(this.getSpritePath(resourcelocation).toString());
         if (!this.missingImage.getName().equals(resourcelocation)) {
            j = loadTexture(stitcher, manager, resourcelocation, j, k);
            if (true) continue; // Forge: skip the rest of this if statement, we're using loadTexture instead
            ResourceLocation resourcelocation1 = this.getSpritePath(resourcelocation);

            TextureAtlasSprite textureatlassprite;
            try (IResource iresource = manager.getResource(resourcelocation1)) {
               PngSizeInfo pngsizeinfo = new PngSizeInfo(iresource);
               AnimationMetadataSection animationmetadatasection = iresource.getMetadata(AnimationMetadataSection.SERIALIZER);
               textureatlassprite = new TextureAtlasSprite(resourcelocation, pngsizeinfo, animationmetadatasection);
            } catch (RuntimeException runtimeexception) {
               LOGGER.error("Unable to parse metadata from {} : {}", resourcelocation1, runtimeexception);
               continue;
            } catch (IOException ioexception) {
               LOGGER.error("Using missing texture, unable to load {} : {}", resourcelocation1, ioexception);
               continue;
            }

            j = Math.min(j, Math.min(textureatlassprite.getWidth(), textureatlassprite.getHeight()));
            int j1 = Math.min(Integer.lowestOneBit(textureatlassprite.getWidth()), Integer.lowestOneBit(textureatlassprite.getHeight()));
            if (j1 < k) {
               LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", resourcelocation1, textureatlassprite.getWidth(), textureatlassprite.getHeight(), MathHelper.log2(k), MathHelper.log2(j1));
               k = j1;
            }

            stitcher.addSprite(textureatlassprite);
         }
      }
      } // Forge: end progress bar

      int l = Math.min(j, k);
      int i1 = MathHelper.log2(l);
      if (false) // FORGE: do not lower the mipmap level
      if (i1 < this.mipmapLevels) {
         LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.basePath, this.mipmapLevels, i1, l);
         this.mipmapLevels = i1;
      }

      this.missingImage.generateMipmaps(this.mipmapLevels);
      stitcher.addSprite(this.missingImage);
      try (net.minecraftforge.fml.common.progress.ProgressBar bar = net.minecraftforge.fml.common.progress.StartupProgressManager.start("Texture creation", 2)) {

      try {
         bar.step("Stitching");
         stitcher.doStitch();
      } catch (StitcherException stitcherexception) {
         throw stitcherexception;
      }

      LOGGER.info("Created: {}x{} {}-atlas", stitcher.getCurrentWidth(), stitcher.getCurrentHeight(), this.basePath);
      bar.step("Allocating GL texture");
      TextureUtil.allocateTextureImpl(this.getGlTextureId(), this.mipmapLevels, stitcher.getCurrentWidth(), stitcher.getCurrentHeight());

      }; // Forge: end progress bar
      try (net.minecraftforge.fml.common.progress.ProgressBar bar = net.minecraftforge.fml.common.progress.StartupProgressManager.start("Texture mipmap and upload", stitcher.getStichSlots().size())) {
      for(TextureAtlasSprite textureatlassprite1 : stitcher.getStichSlots()) {
         bar.step(textureatlassprite1.getName().toString());
         // FORGE: Sprite loading is now done during stitching, short-circuit this check
         if (true || textureatlassprite1 == this.missingImage || this.loadSprite(manager, textureatlassprite1)) {
            this.mapUploadedSprites.put(textureatlassprite1.getName(), textureatlassprite1);

            try {
               textureatlassprite1.uploadMipmaps();
            } catch (Throwable throwable) {
               CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Stitching texture atlas");
               CrashReportCategory crashreportcategory = crashreport.makeCategory("Texture being stitched together");
               crashreportcategory.addDetail("Atlas path", this.basePath);
               crashreportcategory.addDetail("Sprite", textureatlassprite1);
               throw new ReportedException(crashreport);
            }

            if (textureatlassprite1.hasAnimationMetadata()) {
               this.listAnimatedSprites.add(textureatlassprite1);
            }
         }
      }

      net.minecraftforge.client.ForgeHooksClient.onTextureStitchedPost(this);
      }; // Forge: end progress bar
   }

   private boolean loadSprite(IResourceManager manager, TextureAtlasSprite sprite) {
      ResourceLocation resourcelocation = this.getSpritePath(sprite.getName());
      IResource iresource = null;

      label62: {
         boolean flag;
         if (sprite.hasCustomLoader(manager, resourcelocation)) break label62;
         try {
            iresource = manager.getResource(resourcelocation);
            sprite.loadSpriteFrames(iresource, this.mipmapLevels + 1);
            break label62;
         } catch (RuntimeException runtimeexception) {
            LOGGER.error("Unable to parse metadata from {}", resourcelocation, runtimeexception);
            flag = false;
         } catch (IOException ioexception) {
            LOGGER.error("Using missing texture, unable to load {}", resourcelocation, ioexception);
            flag = false;
            return flag;
         } finally {
            IOUtils.closeQuietly((Closeable)iresource);
         }

         return flag;
      }

      try {
         sprite.generateMipmaps(this.mipmapLevels);
         return true;
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Applying mipmap");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Sprite being mipmapped");
         crashreportcategory.addDetail("Sprite name", () -> {
            return sprite.getName().toString();
         });
         crashreportcategory.addDetail("Sprite size", () -> {
            return sprite.getWidth() + " x " + sprite.getHeight();
         });
         crashreportcategory.addDetail("Sprite frames", () -> {
            return sprite.getFrameCount() + " frames";
         });
         crashreportcategory.addDetail("Mipmap levels", this.mipmapLevels);
         throw new ReportedException(crashreport);
      }
   }

   private ResourceLocation getSpritePath(ResourceLocation location) {
      return new ResourceLocation(location.getNamespace(), String.format("%s/%s%s", this.basePath, location.getPath(), ".png"));
   }

   public TextureAtlasSprite getAtlasSprite(String iconName) {
      return this.getSprite(new ResourceLocation(iconName));
   }

   public void updateAnimations() {
      this.bindTexture();

      for(TextureAtlasSprite textureatlassprite : this.listAnimatedSprites) {
         textureatlassprite.updateAnimation();
      }

   }

   public void registerSprite(IResourceManager manager, ResourceLocation location) {
      if (location == null) {
         throw new IllegalArgumentException("Location cannot be null!");
      } else {
         this.sprites.add(location);
      }
   }

   public void tick() {
      this.updateAnimations();
   }

   public void setMipmapLevels(int mipmapLevelsIn) {
      this.mipmapLevels = mipmapLevelsIn;
   }

   public TextureAtlasSprite getSprite(ResourceLocation location) {
      TextureAtlasSprite textureatlassprite = this.mapUploadedSprites.get(location);
      return textureatlassprite == null ? this.missingImage : textureatlassprite;
   }

   public void clear() {
      for(TextureAtlasSprite textureatlassprite : this.mapUploadedSprites.values()) {
         textureatlassprite.clearFramesTextureData();
      }

      this.mapUploadedSprites.clear();
      this.listAnimatedSprites.clear();
   }
   
   //===================================================================================================
   //                                           Forge Start
   //===================================================================================================

   private final java.util.Deque<ResourceLocation> loadingSprites = new java.util.ArrayDeque<>();
   private final java.util.Set<ResourceLocation> loadedSprites = new java.util.HashSet<>();

   public String getBasePath()
   {
       return basePath;
   }

   public int getMipmapLevels()
   {
       return mipmapLevels;
   }

   private int loadTexture(Stitcher stitcher, IResourceManager manager, ResourceLocation resourcelocation, int j, int k)
   {
      if (loadedSprites.contains(resourcelocation))
      {
         return j;
      }
      TextureAtlasSprite textureatlassprite;
      ResourceLocation resourcelocation1 = this.getSpritePath(resourcelocation);
      for (ResourceLocation loading : loadingSprites)
      {
         if (resourcelocation1.equals(loading))
         {
            final String error = "circular model dependencies, stack: [" + com.google.common.base.Joiner.on(", ").join(loadingSprites) + "]";
            net.minecraftforge.fml.client.ClientHooks.trackBrokenTexture(resourcelocation, error);
         }
      }
      loadingSprites.addLast(resourcelocation1);
      try (IResource iresource = manager.getResource(resourcelocation1))
      {
         PngSizeInfo pngsizeinfo = new PngSizeInfo(iresource);
         AnimationMetadataSection animationmetadatasection = iresource.getMetadata(AnimationMetadataSection.SERIALIZER);
         textureatlassprite = new TextureAtlasSprite(resourcelocation, pngsizeinfo, animationmetadatasection);

         for (ResourceLocation dependency : textureatlassprite.getDependencies())
         {
            if (!sprites.contains(dependency))
            {
               registerSprite(manager, dependency);
            }
            j = loadTexture(stitcher, manager, dependency, j, k);
         }
         if (textureatlassprite.hasCustomLoader(manager, resourcelocation))
         {
            if (textureatlassprite.load(manager, resourcelocation, mapUploadedSprites::get))
            {
               return j;
            }
         }
         j = Math.min(j, Math.min(textureatlassprite.getWidth(), textureatlassprite.getHeight()));
         int j1 = Math.min(Integer.lowestOneBit(textureatlassprite.getWidth()), Integer.lowestOneBit(textureatlassprite.getHeight()));
         if (j1 < k)
         {
            // FORGE: do not lower the mipmap level, just log the problematic textures
            LOGGER.warn("Texture {} with size {}x{} will have visual artifacts at mip level {}, it can only support level {}." +
                    "Please report to the mod author that the texture should be some multiple of 16x16.",
                    resourcelocation1, textureatlassprite.getWidth(), textureatlassprite.getHeight(), MathHelper.log2(k), MathHelper.log2(j1));
         }
         if (loadSprite(manager, textureatlassprite))
         {
            stitcher.addSprite(textureatlassprite);
         }
         return j;
      }
      catch (RuntimeException runtimeexception)
      {
         net.minecraftforge.fml.client.ClientHooks.trackBrokenTexture(resourcelocation, runtimeexception.getMessage());
         return j;
      }
      catch (IOException ioexception)
      {
         net.minecraftforge.fml.client.ClientHooks.trackMissingTexture(resourcelocation);
         return j;
      }
      finally
      {
         loadingSprites.removeLast();
         sprites.add(resourcelocation1);
      }
   }
}