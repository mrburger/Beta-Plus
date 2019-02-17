package net.minecraft.client.renderer.texture;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class TextureUtil {
   private static final Logger LOGGER = LogManager.getLogger();

   public static int glGenTextures() {
      return GlStateManager.generateTexture();
   }

   public static void deleteTexture(int textureId) {
      GlStateManager.deleteTexture(textureId);
   }

   public static void allocateTexture(int textureId, int width, int height) {
      allocateTextureImpl(NativeImage.PixelFormatGLCode.RGBA, textureId, 0, width, height);
   }

   public static void allocateTexture(NativeImage.PixelFormatGLCode pixelFormat, int glTextureId, int width, int height) {
      allocateTextureImpl(pixelFormat, glTextureId, 0, width, height);
   }

   public static void allocateTextureImpl(int glTextureId, int mipmapLevels, int width, int height) {
      allocateTextureImpl(NativeImage.PixelFormatGLCode.RGBA, glTextureId, mipmapLevels, width, height);
   }

   public static void allocateTextureImpl(NativeImage.PixelFormatGLCode internalFormat, int glTextureId, int mipmapLevels, int width, int height) {
      synchronized (net.minecraftforge.fml.client.SplashProgress.class)
      {
      bindTexture(glTextureId);
      }
      if (mipmapLevels >= 0) {
         GlStateManager.texParameteri(3553, 33085, mipmapLevels);
         GlStateManager.texParameteri(3553, 33082, 0);
         GlStateManager.texParameteri(3553, 33083, mipmapLevels);
         GlStateManager.texParameterf(3553, 34049, 0.0F);
      }

      for(int i = 0; i <= mipmapLevels; ++i) {
         GlStateManager.texImage2D(3553, i, internalFormat.getGlFormat(), width >> i, height >> i, 0, 6408, 5121, (IntBuffer)null);
      }

   }

   private static void bindTexture(int glTextureId) {
      GlStateManager.bindTexture(glTextureId);
   }

   @Deprecated
   public static int[] makePixelArray(IResourceManager resourceManager, ResourceLocation imageLocation) throws IOException {
      Object object;
      try (
         IResource iresource = resourceManager.getResource(imageLocation);
         NativeImage nativeimage = NativeImage.read(iresource.getInputStream());
      ) {
         object = nativeimage.makePixelArray();
      }

      return (int[])object;
   }

   /**
    * Reads the given InputStream into a buffer backed by native memory, for usage with OpenGL. Note: The returned
    * buffer MUST be freed with {@code MemoryUtil.memFree}, or the memory will be leaked.
    */
   public static ByteBuffer readToNativeBuffer(InputStream inputStream) throws IOException {
      ByteBuffer bytebuffer;
      if (inputStream instanceof FileInputStream) {
         FileInputStream fileinputstream = (FileInputStream)inputStream;
         FileChannel filechannel = fileinputstream.getChannel();
         bytebuffer = MemoryUtil.memAlloc((int)filechannel.size() + 1);

         while(filechannel.read(bytebuffer) != -1) {
            ;
         }
      } else {
         bytebuffer = MemoryUtil.memAlloc(8192);
         ReadableByteChannel readablebytechannel = Channels.newChannel(inputStream);

         while(readablebytechannel.read(bytebuffer) != -1) {
            if (bytebuffer.remaining() == 0) {
               bytebuffer = MemoryUtil.memRealloc(bytebuffer, bytebuffer.capacity() * 2);
            }
         }
      }

      return bytebuffer;
   }
}