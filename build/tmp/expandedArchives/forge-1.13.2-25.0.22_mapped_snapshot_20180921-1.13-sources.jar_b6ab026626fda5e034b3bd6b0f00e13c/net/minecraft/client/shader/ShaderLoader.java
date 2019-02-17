package net.minecraft.client.shader;

import com.google.common.collect.Maps;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.util.JsonException;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class ShaderLoader {
   private final ShaderLoader.ShaderType shaderType;
   private final String shaderFilename;
   private final int shader;
   private int shaderAttachCount;

   private ShaderLoader(ShaderLoader.ShaderType type, int shaderId, String filename) {
      this.shaderType = type;
      this.shader = shaderId;
      this.shaderFilename = filename;
   }

   public void attachShader(ShaderManager manager) {
      ++this.shaderAttachCount;
      OpenGlHelper.glAttachShader(manager.getProgram(), this.shader);
   }

   public void func_195656_a() {
      --this.shaderAttachCount;
      if (this.shaderAttachCount <= 0) {
         OpenGlHelper.glDeleteShader(this.shader);
         this.shaderType.getLoadedShaders().remove(this.shaderFilename);
      }

   }

   public String getShaderFilename() {
      return this.shaderFilename;
   }

   public static ShaderLoader loadShader(IResourceManager p_195655_0_, ShaderLoader.ShaderType p_195655_1_, String p_195655_2_) throws IOException {
      ShaderLoader shaderloader = p_195655_1_.getLoadedShaders().get(p_195655_2_);
      if (shaderloader == null) {
         ResourceLocation rl = ResourceLocation.makeResourceLocation(p_195655_2_);
         ResourceLocation resourcelocation = new ResourceLocation(rl.getNamespace(), "shaders/program/" + rl.getPath() + p_195655_1_.getShaderExtension());
         IResource iresource = p_195655_0_.getResource(resourcelocation);
         ByteBuffer bytebuffer = null;

         try {
            bytebuffer = TextureUtil.readToNativeBuffer(iresource.getInputStream());
            int i = bytebuffer.position();
            bytebuffer.rewind();
            int j = OpenGlHelper.glCreateShader(p_195655_1_.getShaderMode());
            String s = MemoryUtil.memASCII(bytebuffer, i);
            OpenGlHelper.glShaderSource(j, s);
            OpenGlHelper.glCompileShader(j);
            if (OpenGlHelper.glGetShaderi(j, OpenGlHelper.GL_COMPILE_STATUS) == 0) {
               String s1 = StringUtils.trim(OpenGlHelper.glGetShaderInfoLog(j, 32768));
               JsonException jsonexception = new JsonException("Couldn't compile " + p_195655_1_.getShaderName() + " program: " + s1);
               jsonexception.setFilenameAndFlush(resourcelocation.getPath());
               throw jsonexception;
            }

            shaderloader = new ShaderLoader(p_195655_1_, j, p_195655_2_);
            p_195655_1_.getLoadedShaders().put(p_195655_2_, shaderloader);
         } finally {
            IOUtils.closeQuietly((Closeable)iresource);
            if (bytebuffer != null) {
               MemoryUtil.memFree(bytebuffer);
            }

         }
      }

      return shaderloader;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum ShaderType {
      VERTEX("vertex", ".vsh", OpenGlHelper.GL_VERTEX_SHADER),
      FRAGMENT("fragment", ".fsh", OpenGlHelper.GL_FRAGMENT_SHADER);

      private final String shaderName;
      private final String shaderExtension;
      private final int shaderMode;
      private final Map<String, ShaderLoader> loadedShaders = Maps.newHashMap();

      private ShaderType(String shaderNameIn, String shaderExtensionIn, int shaderModeIn) {
         this.shaderName = shaderNameIn;
         this.shaderExtension = shaderExtensionIn;
         this.shaderMode = shaderModeIn;
      }

      public String getShaderName() {
         return this.shaderName;
      }

      private String getShaderExtension() {
         return this.shaderExtension;
      }

      private int getShaderMode() {
         return this.shaderMode;
      }

      /**
       * gets a map of loaded shaders for the ShaderType.
       */
      private Map<String, ShaderLoader> getLoadedShaders() {
         return this.loadedShaders;
      }
   }
}