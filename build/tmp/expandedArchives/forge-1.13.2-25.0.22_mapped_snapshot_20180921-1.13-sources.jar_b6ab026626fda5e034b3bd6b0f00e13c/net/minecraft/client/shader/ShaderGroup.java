package net.minecraft.client.shader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.util.JsonException;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;

@OnlyIn(Dist.CLIENT)
public class ShaderGroup implements AutoCloseable {
   private final Framebuffer mainFramebuffer;
   private final IResourceManager resourceManager;
   private final String shaderGroupName;
   private final List<Shader> listShaders = Lists.newArrayList();
   private final Map<String, Framebuffer> mapFramebuffers = Maps.newHashMap();
   private final List<Framebuffer> listFramebuffers = Lists.newArrayList();
   private Matrix4f projectionMatrix;
   private int mainFramebufferWidth;
   private int mainFramebufferHeight;
   private float time;
   private float lastStamp;

   public ShaderGroup(TextureManager p_i1050_1_, IResourceManager resourceManagerIn, Framebuffer mainFramebufferIn, ResourceLocation p_i1050_4_) throws IOException, JsonSyntaxException {
      this.resourceManager = resourceManagerIn;
      this.mainFramebuffer = mainFramebufferIn;
      this.time = 0.0F;
      this.lastStamp = 0.0F;
      this.mainFramebufferWidth = mainFramebufferIn.framebufferWidth;
      this.mainFramebufferHeight = mainFramebufferIn.framebufferHeight;
      this.shaderGroupName = p_i1050_4_.toString();
      this.resetProjectionMatrix();
      this.parseGroup(p_i1050_1_, p_i1050_4_);
   }

   private void parseGroup(TextureManager p_152765_1_, ResourceLocation p_152765_2_) throws IOException, JsonSyntaxException {
      IResource iresource = null;

      try {
         iresource = this.resourceManager.getResource(p_152765_2_);
         JsonObject jsonobject = JsonUtils.func_212743_a(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));
         if (JsonUtils.isJsonArray(jsonobject, "targets")) {
            JsonArray jsonarray = jsonobject.getAsJsonArray("targets");
            int i = 0;

            for(JsonElement jsonelement : jsonarray) {
               try {
                  this.initTarget(jsonelement);
               } catch (Exception exception1) {
                  JsonException jsonexception1 = JsonException.forException(exception1);
                  jsonexception1.prependJsonKey("targets[" + i + "]");
                  throw jsonexception1;
               }

               ++i;
            }
         }

         if (JsonUtils.isJsonArray(jsonobject, "passes")) {
            JsonArray jsonarray1 = jsonobject.getAsJsonArray("passes");
            int j = 0;

            for(JsonElement jsonelement1 : jsonarray1) {
               try {
                  this.parsePass(p_152765_1_, jsonelement1);
               } catch (Exception exception) {
                  JsonException jsonexception2 = JsonException.forException(exception);
                  jsonexception2.prependJsonKey("passes[" + j + "]");
                  throw jsonexception2;
               }

               ++j;
            }
         }
      } catch (Exception exception2) {
         JsonException jsonexception = JsonException.forException(exception2);
         jsonexception.setFilenameAndFlush(p_152765_2_.getPath());
         throw jsonexception;
      } finally {
         IOUtils.closeQuietly((Closeable)iresource);
      }

   }

   private void initTarget(JsonElement p_148027_1_) throws JsonException {
      if (JsonUtils.isString(p_148027_1_)) {
         this.addFramebuffer(p_148027_1_.getAsString(), this.mainFramebufferWidth, this.mainFramebufferHeight);
      } else {
         JsonObject jsonobject = JsonUtils.getJsonObject(p_148027_1_, "target");
         String s = JsonUtils.getString(jsonobject, "name");
         int i = JsonUtils.getInt(jsonobject, "width", this.mainFramebufferWidth);
         int j = JsonUtils.getInt(jsonobject, "height", this.mainFramebufferHeight);
         if (this.mapFramebuffers.containsKey(s)) {
            throw new JsonException(s + " is already defined");
         }

         this.addFramebuffer(s, i, j);
      }

   }

   private void parsePass(TextureManager p_152764_1_, JsonElement json) throws IOException {
      JsonObject jsonobject = JsonUtils.getJsonObject(json, "pass");
      String s = JsonUtils.getString(jsonobject, "name");
      String s1 = JsonUtils.getString(jsonobject, "intarget");
      String s2 = JsonUtils.getString(jsonobject, "outtarget");
      Framebuffer framebuffer = this.getFramebuffer(s1);
      Framebuffer framebuffer1 = this.getFramebuffer(s2);
      if (framebuffer == null) {
         throw new JsonException("Input target '" + s1 + "' does not exist");
      } else if (framebuffer1 == null) {
         throw new JsonException("Output target '" + s2 + "' does not exist");
      } else {
         Shader shader = this.addShader(s, framebuffer, framebuffer1);
         JsonArray jsonarray = JsonUtils.getJsonArray(jsonobject, "auxtargets", (JsonArray)null);
         if (jsonarray != null) {
            int i = 0;

            for(JsonElement jsonelement : jsonarray) {
               try {
                  JsonObject jsonobject1 = JsonUtils.getJsonObject(jsonelement, "auxtarget");
                  String s4 = JsonUtils.getString(jsonobject1, "name");
                  String s3 = JsonUtils.getString(jsonobject1, "id");
                  Framebuffer framebuffer2 = this.getFramebuffer(s3);
                  if (framebuffer2 == null) {
                     ResourceLocation rl = ResourceLocation.makeResourceLocation(s3);
                     ResourceLocation resourcelocation = new ResourceLocation(rl.getNamespace(), "textures/effect/" + rl.getPath() + ".png");
                     IResource iresource = null;

                     try {
                        iresource = this.resourceManager.getResource(resourcelocation);
                     } catch (FileNotFoundException var29) {
                        throw new JsonException("Render target or texture '" + s3 + "' does not exist");
                     } finally {
                        IOUtils.closeQuietly((Closeable)iresource);
                     }

                     p_152764_1_.bindTexture(resourcelocation);
                     ITextureObject lvt_20_2_ = p_152764_1_.getTexture(resourcelocation);
                     int lvt_21_1_ = JsonUtils.getInt(jsonobject1, "width");
                     int lvt_22_1_ = JsonUtils.getInt(jsonobject1, "height");
                     boolean lvt_23_1_ = JsonUtils.getBoolean(jsonobject1, "bilinear");
                     if (lvt_23_1_) {
                        GlStateManager.texParameteri(3553, 10241, 9729);
                        GlStateManager.texParameteri(3553, 10240, 9729);
                     } else {
                        GlStateManager.texParameteri(3553, 10241, 9728);
                        GlStateManager.texParameteri(3553, 10240, 9728);
                     }

                     shader.addAuxFramebuffer(s4, lvt_20_2_.getGlTextureId(), lvt_21_1_, lvt_22_1_);
                  } else {
                     shader.addAuxFramebuffer(s4, framebuffer2, framebuffer2.framebufferTextureWidth, framebuffer2.framebufferTextureHeight);
                  }
               } catch (Exception exception1) {
                  JsonException jsonexception = JsonException.forException(exception1);
                  jsonexception.prependJsonKey("auxtargets[" + i + "]");
                  throw jsonexception;
               }

               ++i;
            }
         }

         JsonArray jsonarray1 = JsonUtils.getJsonArray(jsonobject, "uniforms", (JsonArray)null);
         if (jsonarray1 != null) {
            int l = 0;

            for(JsonElement jsonelement1 : jsonarray1) {
               try {
                  this.initUniform(jsonelement1);
               } catch (Exception exception) {
                  JsonException jsonexception1 = JsonException.forException(exception);
                  jsonexception1.prependJsonKey("uniforms[" + l + "]");
                  throw jsonexception1;
               }

               ++l;
            }
         }

      }
   }

   private void initUniform(JsonElement json) throws JsonException {
      JsonObject jsonobject = JsonUtils.getJsonObject(json, "uniform");
      String s = JsonUtils.getString(jsonobject, "name");
      ShaderUniform shaderuniform = this.listShaders.get(this.listShaders.size() - 1).getShaderManager().getShaderUniform(s);
      if (shaderuniform == null) {
         throw new JsonException("Uniform '" + s + "' does not exist");
      } else {
         float[] afloat = new float[4];
         int i = 0;

         for(JsonElement jsonelement : JsonUtils.getJsonArray(jsonobject, "values")) {
            try {
               afloat[i] = JsonUtils.getFloat(jsonelement, "value");
            } catch (Exception exception) {
               JsonException jsonexception = JsonException.forException(exception);
               jsonexception.prependJsonKey("values[" + i + "]");
               throw jsonexception;
            }

            ++i;
         }

         switch(i) {
         case 0:
         default:
            break;
         case 1:
            shaderuniform.set(afloat[0]);
            break;
         case 2:
            shaderuniform.set(afloat[0], afloat[1]);
            break;
         case 3:
            shaderuniform.set(afloat[0], afloat[1], afloat[2]);
            break;
         case 4:
            shaderuniform.set(afloat[0], afloat[1], afloat[2], afloat[3]);
         }

      }
   }

   public Framebuffer getFramebufferRaw(String attributeName) {
      return this.mapFramebuffers.get(attributeName);
   }

   public void addFramebuffer(String name, int width, int height) {
      Framebuffer framebuffer = new Framebuffer(width, height, true);
      framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.mapFramebuffers.put(name, framebuffer);
      if (width == this.mainFramebufferWidth && height == this.mainFramebufferHeight) {
         this.listFramebuffers.add(framebuffer);
      }

   }

   public void close() {
      for(Framebuffer framebuffer : this.mapFramebuffers.values()) {
         framebuffer.deleteFramebuffer();
      }

      for(Shader shader : this.listShaders) {
         shader.close();
      }

      this.listShaders.clear();
   }

   public Shader addShader(String programName, Framebuffer framebufferIn, Framebuffer framebufferOut) throws IOException {
      Shader shader = new Shader(this.resourceManager, programName, framebufferIn, framebufferOut);
      this.listShaders.add(this.listShaders.size(), shader);
      return shader;
   }

   private void resetProjectionMatrix() {
      this.projectionMatrix = Matrix4f.orthographic((float)this.mainFramebuffer.framebufferTextureWidth, (float)this.mainFramebuffer.framebufferTextureHeight, 0.1F, 1000.0F);
   }

   public void createBindFramebuffers(int width, int height) {
      this.mainFramebufferWidth = this.mainFramebuffer.framebufferTextureWidth;
      this.mainFramebufferHeight = this.mainFramebuffer.framebufferTextureHeight;
      this.resetProjectionMatrix();

      for(Shader shader : this.listShaders) {
         shader.func_195654_a(this.projectionMatrix);
      }

      for(Framebuffer framebuffer : this.listFramebuffers) {
         framebuffer.createBindFramebuffer(width, height);
      }

   }

   public void render(float partialTicks) {
      if (partialTicks < this.lastStamp) {
         this.time += 1.0F - this.lastStamp;
         this.time += partialTicks;
      } else {
         this.time += partialTicks - this.lastStamp;
      }

      for(this.lastStamp = partialTicks; this.time > 20.0F; this.time -= 20.0F) {
         ;
      }

      for(Shader shader : this.listShaders) {
         shader.render(this.time / 20.0F);
      }

   }

   public final String getShaderGroupName() {
      return this.shaderGroupName;
   }

   private Framebuffer getFramebuffer(String p_148017_1_) {
      if (p_148017_1_ == null) {
         return null;
      } else {
         return p_148017_1_.equals("minecraft:main") ? this.mainFramebuffer : this.mapFramebuffers.get(p_148017_1_);
      }
   }
}