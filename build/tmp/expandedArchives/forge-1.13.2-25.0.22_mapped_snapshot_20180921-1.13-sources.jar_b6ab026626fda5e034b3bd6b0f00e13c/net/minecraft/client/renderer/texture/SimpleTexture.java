package net.minecraft.client.renderer.texture;

import java.io.IOException;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SimpleTexture extends AbstractTexture {
   private static final Logger LOGGER = LogManager.getLogger();
   protected final ResourceLocation textureLocation;

   public SimpleTexture(ResourceLocation textureResourceLocation) {
      this.textureLocation = textureResourceLocation;
   }

   public void loadTexture(IResourceManager manager) throws IOException {
      try (
         IResource iresource = manager.getResource(this.textureLocation);
         NativeImage nativeimage = NativeImage.read(iresource.getInputStream());
      ) {
         boolean flag = false;
         boolean flag1 = false;
         if (iresource.hasMetadata()) {
            try {
               TextureMetadataSection texturemetadatasection = iresource.getMetadata(TextureMetadataSection.SERIALIZER);
               if (texturemetadatasection != null) {
                  flag = texturemetadatasection.getTextureBlur();
                  flag1 = texturemetadatasection.getTextureClamp();
               }
            } catch (RuntimeException runtimeexception) {
               LOGGER.warn("Failed reading metadata of: {}", this.textureLocation, runtimeexception);
            }
         }

         this.bindTexture();
         TextureUtil.allocateTextureImpl(this.getGlTextureId(), 0, nativeimage.getWidth(), nativeimage.getHeight());
         nativeimage.uploadTextureSub(0, 0, 0, 0, 0, nativeimage.getWidth(), nativeimage.getHeight(), flag, flag1, false);
      }

   }
}