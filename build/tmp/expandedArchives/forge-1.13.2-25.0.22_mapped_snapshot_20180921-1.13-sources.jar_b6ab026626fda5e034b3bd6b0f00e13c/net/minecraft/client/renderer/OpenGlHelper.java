package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.EXTBlendFuncSeparate;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLCapabilities;
import oshi.SystemInfo;
import oshi.hardware.Processor;

@OnlyIn(Dist.CLIENT)
public class OpenGlHelper {
   public static boolean nvidia;
   public static boolean ati;
   public static int GL_FRAMEBUFFER;
   public static int GL_RENDERBUFFER;
   public static int GL_COLOR_ATTACHMENT0;
   public static int GL_DEPTH_ATTACHMENT;
   public static int GL_FRAMEBUFFER_COMPLETE;
   public static int GL_FB_INCOMPLETE_ATTACHMENT;
   public static int GL_FB_INCOMPLETE_MISS_ATTACH;
   public static int GL_FB_INCOMPLETE_DRAW_BUFFER;
   public static int GL_FB_INCOMPLETE_READ_BUFFER;
   private static OpenGlHelper.FboMode framebufferType;
   public static boolean framebufferSupported;
   private static boolean shadersAvailable;
   private static boolean arbShaders;
   public static int GL_LINK_STATUS;
   public static int GL_COMPILE_STATUS;
   public static int GL_VERTEX_SHADER;
   public static int GL_FRAGMENT_SHADER;
   private static boolean arbMultitexture;
   public static int GL_TEXTURE0;
   public static int GL_TEXTURE1;
   public static int GL_TEXTURE2;
   private static boolean arbTextureEnvCombine;
   public static int GL_COMBINE;
   public static int GL_INTERPOLATE;
   public static int GL_PRIMARY_COLOR;
   public static int GL_CONSTANT;
   public static int GL_PREVIOUS;
   public static int GL_COMBINE_RGB;
   public static int GL_SOURCE0_RGB;
   public static int GL_SOURCE1_RGB;
   public static int GL_SOURCE2_RGB;
   public static int GL_OPERAND0_RGB;
   public static int GL_OPERAND1_RGB;
   public static int GL_OPERAND2_RGB;
   public static int GL_COMBINE_ALPHA;
   public static int GL_SOURCE0_ALPHA;
   public static int GL_SOURCE1_ALPHA;
   public static int GL_SOURCE2_ALPHA;
   public static int GL_OPERAND0_ALPHA;
   public static int GL_OPERAND1_ALPHA;
   public static int GL_OPERAND2_ALPHA;
   private static boolean openGL14;
   public static boolean extBlendFuncSeparate;
   public static boolean openGL21;
   public static boolean shadersSupported;
   private static String logText = "";
   private static String cpu;
   public static boolean vboSupported;
   public static boolean vboSupportedAti;
   private static boolean arbVbo;
   public static int GL_ARRAY_BUFFER;
   public static int GL_STATIC_DRAW;
   private static final Map<Integer, String> MAP_ERROR_MESSAGES = Util.make(Maps.newHashMap(), (p_203093_0_) -> {
      p_203093_0_.put(0, "No error");
      p_203093_0_.put(1280, "Enum parameter is invalid for this function");
      p_203093_0_.put(1281, "Parameter is invalid for this function");
      p_203093_0_.put(1282, "Current state is invalid for this function");
      p_203093_0_.put(1283, "Stack overflow");
      p_203093_0_.put(1284, "Stack underflow");
      p_203093_0_.put(1285, "Out of memory");
      p_203093_0_.put(1286, "Operation on incomplete framebuffer");
      p_203093_0_.put(1286, "Operation on incomplete framebuffer");
   });

   /* Stores the last values sent into glMultiTexCoord2f */
   public static float lastBrightnessX = 0.0f;
   public static float lastBrightnessY = 0.0f;

   /**
    * Initializes the texture constants to be used when rendering lightmap values
    */
   public static void init() {
      GLCapabilities glcapabilities = GL.getCapabilities();
      arbMultitexture = glcapabilities.GL_ARB_multitexture && !glcapabilities.OpenGL13;
      arbTextureEnvCombine = glcapabilities.GL_ARB_texture_env_combine && !glcapabilities.OpenGL13;
      if (arbMultitexture) {
         logText = logText + "Using ARB_multitexture.\n";
         GL_TEXTURE0 = 33984;
         GL_TEXTURE1 = 33985;
         GL_TEXTURE2 = 33986;
      } else {
         logText = logText + "Using GL 1.3 multitexturing.\n";
         GL_TEXTURE0 = 33984;
         GL_TEXTURE1 = 33985;
         GL_TEXTURE2 = 33986;
      }

      if (arbTextureEnvCombine) {
         logText = logText + "Using ARB_texture_env_combine.\n";
         GL_COMBINE = 34160;
         GL_INTERPOLATE = 34165;
         GL_PRIMARY_COLOR = 34167;
         GL_CONSTANT = 34166;
         GL_PREVIOUS = 34168;
         GL_COMBINE_RGB = 34161;
         GL_SOURCE0_RGB = 34176;
         GL_SOURCE1_RGB = 34177;
         GL_SOURCE2_RGB = 34178;
         GL_OPERAND0_RGB = 34192;
         GL_OPERAND1_RGB = 34193;
         GL_OPERAND2_RGB = 34194;
         GL_COMBINE_ALPHA = 34162;
         GL_SOURCE0_ALPHA = 34184;
         GL_SOURCE1_ALPHA = 34185;
         GL_SOURCE2_ALPHA = 34186;
         GL_OPERAND0_ALPHA = 34200;
         GL_OPERAND1_ALPHA = 34201;
         GL_OPERAND2_ALPHA = 34202;
      } else {
         logText = logText + "Using GL 1.3 texture combiners.\n";
         GL_COMBINE = 34160;
         GL_INTERPOLATE = 34165;
         GL_PRIMARY_COLOR = 34167;
         GL_CONSTANT = 34166;
         GL_PREVIOUS = 34168;
         GL_COMBINE_RGB = 34161;
         GL_SOURCE0_RGB = 34176;
         GL_SOURCE1_RGB = 34177;
         GL_SOURCE2_RGB = 34178;
         GL_OPERAND0_RGB = 34192;
         GL_OPERAND1_RGB = 34193;
         GL_OPERAND2_RGB = 34194;
         GL_COMBINE_ALPHA = 34162;
         GL_SOURCE0_ALPHA = 34184;
         GL_SOURCE1_ALPHA = 34185;
         GL_SOURCE2_ALPHA = 34186;
         GL_OPERAND0_ALPHA = 34200;
         GL_OPERAND1_ALPHA = 34201;
         GL_OPERAND2_ALPHA = 34202;
      }

      extBlendFuncSeparate = glcapabilities.GL_EXT_blend_func_separate && !glcapabilities.OpenGL14;
      openGL14 = glcapabilities.OpenGL14 || glcapabilities.GL_EXT_blend_func_separate;
      framebufferSupported = openGL14 && (glcapabilities.GL_ARB_framebuffer_object || glcapabilities.GL_EXT_framebuffer_object || glcapabilities.OpenGL30);
      if (framebufferSupported) {
         logText = logText + "Using framebuffer objects because ";
         if (glcapabilities.OpenGL30) {
            logText = logText + "OpenGL 3.0 is supported and separate blending is supported.\n";
            framebufferType = OpenGlHelper.FboMode.BASE;
            GL_FRAMEBUFFER = 36160;
            GL_RENDERBUFFER = 36161;
            GL_COLOR_ATTACHMENT0 = 36064;
            GL_DEPTH_ATTACHMENT = 36096;
            GL_FRAMEBUFFER_COMPLETE = 36053;
            GL_FB_INCOMPLETE_ATTACHMENT = 36054;
            GL_FB_INCOMPLETE_MISS_ATTACH = 36055;
            GL_FB_INCOMPLETE_DRAW_BUFFER = 36059;
            GL_FB_INCOMPLETE_READ_BUFFER = 36060;
         } else if (glcapabilities.GL_ARB_framebuffer_object) {
            logText = logText + "ARB_framebuffer_object is supported and separate blending is supported.\n";
            framebufferType = OpenGlHelper.FboMode.ARB;
            GL_FRAMEBUFFER = 36160;
            GL_RENDERBUFFER = 36161;
            GL_COLOR_ATTACHMENT0 = 36064;
            GL_DEPTH_ATTACHMENT = 36096;
            GL_FRAMEBUFFER_COMPLETE = 36053;
            GL_FB_INCOMPLETE_MISS_ATTACH = 36055;
            GL_FB_INCOMPLETE_ATTACHMENT = 36054;
            GL_FB_INCOMPLETE_DRAW_BUFFER = 36059;
            GL_FB_INCOMPLETE_READ_BUFFER = 36060;
         } else if (glcapabilities.GL_EXT_framebuffer_object) {
            logText = logText + "EXT_framebuffer_object is supported.\n";
            framebufferType = OpenGlHelper.FboMode.EXT;
            GL_FRAMEBUFFER = 36160;
            GL_RENDERBUFFER = 36161;
            GL_COLOR_ATTACHMENT0 = 36064;
            GL_DEPTH_ATTACHMENT = 36096;
            GL_FRAMEBUFFER_COMPLETE = 36053;
            GL_FB_INCOMPLETE_MISS_ATTACH = 36055;
            GL_FB_INCOMPLETE_ATTACHMENT = 36054;
            GL_FB_INCOMPLETE_DRAW_BUFFER = 36059;
            GL_FB_INCOMPLETE_READ_BUFFER = 36060;
         }
      } else {
         logText = logText + "Not using framebuffer objects because ";
         logText = logText + "OpenGL 1.4 is " + (glcapabilities.OpenGL14 ? "" : "not ") + "supported, ";
         logText = logText + "EXT_blend_func_separate is " + (glcapabilities.GL_EXT_blend_func_separate ? "" : "not ") + "supported, ";
         logText = logText + "OpenGL 3.0 is " + (glcapabilities.OpenGL30 ? "" : "not ") + "supported, ";
         logText = logText + "ARB_framebuffer_object is " + (glcapabilities.GL_ARB_framebuffer_object ? "" : "not ") + "supported, and ";
         logText = logText + "EXT_framebuffer_object is " + (glcapabilities.GL_EXT_framebuffer_object ? "" : "not ") + "supported.\n";
      }

      openGL21 = glcapabilities.OpenGL21;
      shadersAvailable = openGL21 || glcapabilities.GL_ARB_vertex_shader && glcapabilities.GL_ARB_fragment_shader && glcapabilities.GL_ARB_shader_objects;
      logText = logText + "Shaders are " + (shadersAvailable ? "" : "not ") + "available because ";
      if (shadersAvailable) {
         if (glcapabilities.OpenGL21) {
            logText = logText + "OpenGL 2.1 is supported.\n";
            arbShaders = false;
            GL_LINK_STATUS = 35714;
            GL_COMPILE_STATUS = 35713;
            GL_VERTEX_SHADER = 35633;
            GL_FRAGMENT_SHADER = 35632;
         } else {
            logText = logText + "ARB_shader_objects, ARB_vertex_shader, and ARB_fragment_shader are supported.\n";
            arbShaders = true;
            GL_LINK_STATUS = 35714;
            GL_COMPILE_STATUS = 35713;
            GL_VERTEX_SHADER = 35633;
            GL_FRAGMENT_SHADER = 35632;
         }
      } else {
         logText = logText + "OpenGL 2.1 is " + (glcapabilities.OpenGL21 ? "" : "not ") + "supported, ";
         logText = logText + "ARB_shader_objects is " + (glcapabilities.GL_ARB_shader_objects ? "" : "not ") + "supported, ";
         logText = logText + "ARB_vertex_shader is " + (glcapabilities.GL_ARB_vertex_shader ? "" : "not ") + "supported, and ";
         logText = logText + "ARB_fragment_shader is " + (glcapabilities.GL_ARB_fragment_shader ? "" : "not ") + "supported.\n";
      }

      shadersSupported = framebufferSupported && shadersAvailable;
      String s = GL11.glGetString(7936).toLowerCase(Locale.ROOT);
      nvidia = s.contains("nvidia");
      arbVbo = !glcapabilities.OpenGL15 && glcapabilities.GL_ARB_vertex_buffer_object;
      vboSupported = glcapabilities.OpenGL15 || arbVbo;
      logText = logText + "VBOs are " + (vboSupported ? "" : "not ") + "available because ";
      if (vboSupported) {
         if (arbVbo) {
            logText = logText + "ARB_vertex_buffer_object is supported.\n";
            GL_STATIC_DRAW = 35044;
            GL_ARRAY_BUFFER = 34962;
         } else {
            logText = logText + "OpenGL 1.5 is supported.\n";
            GL_STATIC_DRAW = 35044;
            GL_ARRAY_BUFFER = 34962;
         }
      }

      ati = s.contains("ati");
      if (ati) {
         if (vboSupported) {
            vboSupportedAti = true;
         } else {
            GameSettings.Options.RENDER_DISTANCE.setValueMax(16.0F);
         }
      }

      try {
         Processor[] aprocessor = (new SystemInfo()).getHardware().getProcessors();
         cpu = String.format("%dx %s", aprocessor.length, aprocessor[0]).replaceAll("\\s+", " ");
      } catch (Throwable var3) {
         ;
      }

   }

   public static boolean areShadersSupported() {
      return shadersSupported;
   }

   public static String getLogText() {
      return logText;
   }

   public static int glGetProgrami(int program, int pname) {
      return arbShaders ? ARBShaderObjects.glGetObjectParameteriARB(program, pname) : GL20.glGetProgrami(program, pname);
   }

   public static void glAttachShader(int program, int shaderIn) {
      if (arbShaders) {
         ARBShaderObjects.glAttachObjectARB(program, shaderIn);
      } else {
         GL20.glAttachShader(program, shaderIn);
      }

   }

   public static void glDeleteShader(int shaderIn) {
      if (arbShaders) {
         ARBShaderObjects.glDeleteObjectARB(shaderIn);
      } else {
         GL20.glDeleteShader(shaderIn);
      }

   }

   /**
    * creates a shader with the given mode and returns the GL id. params: mode
    */
   public static int glCreateShader(int type) {
      return arbShaders ? ARBShaderObjects.glCreateShaderObjectARB(type) : GL20.glCreateShader(type);
   }

   public static void glShaderSource(int shaderIn, CharSequence string) {
      if (arbShaders) {
         ARBShaderObjects.glShaderSourceARB(shaderIn, string);
      } else {
         GL20.glShaderSource(shaderIn, string);
      }

   }

   public static void glCompileShader(int shaderIn) {
      if (arbShaders) {
         ARBShaderObjects.glCompileShaderARB(shaderIn);
      } else {
         GL20.glCompileShader(shaderIn);
      }

   }

   public static int glGetShaderi(int shaderIn, int pname) {
      return arbShaders ? ARBShaderObjects.glGetObjectParameteriARB(shaderIn, pname) : GL20.glGetShaderi(shaderIn, pname);
   }

   public static String glGetShaderInfoLog(int shaderIn, int maxLength) {
      return arbShaders ? ARBShaderObjects.glGetInfoLogARB(shaderIn, maxLength) : GL20.glGetShaderInfoLog(shaderIn, maxLength);
   }

   public static String glGetProgramInfoLog(int program, int maxLength) {
      return arbShaders ? ARBShaderObjects.glGetInfoLogARB(program, maxLength) : GL20.glGetProgramInfoLog(program, maxLength);
   }

   public static void glUseProgram(int program) {
      if (arbShaders) {
         ARBShaderObjects.glUseProgramObjectARB(program);
      } else {
         GL20.glUseProgram(program);
      }

   }

   public static int glCreateProgram() {
      return arbShaders ? ARBShaderObjects.glCreateProgramObjectARB() : GL20.glCreateProgram();
   }

   public static void glDeleteProgram(int program) {
      if (arbShaders) {
         ARBShaderObjects.glDeleteObjectARB(program);
      } else {
         GL20.glDeleteProgram(program);
      }

   }

   public static void glLinkProgram(int program) {
      if (arbShaders) {
         ARBShaderObjects.glLinkProgramARB(program);
      } else {
         GL20.glLinkProgram(program);
      }

   }

   public static int glGetUniformLocation(int programObj, CharSequence name) {
      return arbShaders ? ARBShaderObjects.glGetUniformLocationARB(programObj, name) : GL20.glGetUniformLocation(programObj, name);
   }

   public static void glUniform1iv(int location, IntBuffer values) {
      if (arbShaders) {
         ARBShaderObjects.glUniform1ivARB(location, values);
      } else {
         GL20.glUniform1iv(location, values);
      }

   }

   public static void glUniform1i(int location, int v0) {
      if (arbShaders) {
         ARBShaderObjects.glUniform1iARB(location, v0);
      } else {
         GL20.glUniform1i(location, v0);
      }

   }

   public static void glUniform1fv(int location, FloatBuffer values) {
      if (arbShaders) {
         ARBShaderObjects.glUniform1fvARB(location, values);
      } else {
         GL20.glUniform1fv(location, values);
      }

   }

   public static void glUniform2iv(int location, IntBuffer values) {
      if (arbShaders) {
         ARBShaderObjects.glUniform2ivARB(location, values);
      } else {
         GL20.glUniform2iv(location, values);
      }

   }

   public static void glUniform2fv(int location, FloatBuffer values) {
      if (arbShaders) {
         ARBShaderObjects.glUniform2fvARB(location, values);
      } else {
         GL20.glUniform2fv(location, values);
      }

   }

   public static void glUniform3iv(int location, IntBuffer values) {
      if (arbShaders) {
         ARBShaderObjects.glUniform3ivARB(location, values);
      } else {
         GL20.glUniform3iv(location, values);
      }

   }

   public static void glUniform3fv(int location, FloatBuffer values) {
      if (arbShaders) {
         ARBShaderObjects.glUniform3fvARB(location, values);
      } else {
         GL20.glUniform3fv(location, values);
      }

   }

   public static void glUniform4iv(int location, IntBuffer values) {
      if (arbShaders) {
         ARBShaderObjects.glUniform4ivARB(location, values);
      } else {
         GL20.glUniform4iv(location, values);
      }

   }

   public static void glUniform4fv(int location, FloatBuffer values) {
      if (arbShaders) {
         ARBShaderObjects.glUniform4fvARB(location, values);
      } else {
         GL20.glUniform4fv(location, values);
      }

   }

   public static void glUniformMatrix2fv(int location, boolean transpose, FloatBuffer matrices) {
      if (arbShaders) {
         ARBShaderObjects.glUniformMatrix2fvARB(location, transpose, matrices);
      } else {
         GL20.glUniformMatrix2fv(location, transpose, matrices);
      }

   }

   public static void glUniformMatrix3fv(int location, boolean transpose, FloatBuffer matrices) {
      if (arbShaders) {
         ARBShaderObjects.glUniformMatrix3fvARB(location, transpose, matrices);
      } else {
         GL20.glUniformMatrix3fv(location, transpose, matrices);
      }

   }

   public static void glUniformMatrix4fv(int location, boolean transpose, FloatBuffer matrices) {
      if (arbShaders) {
         ARBShaderObjects.glUniformMatrix4fvARB(location, transpose, matrices);
      } else {
         GL20.glUniformMatrix4fv(location, transpose, matrices);
      }

   }

   public static int glGetAttribLocation(int program, CharSequence name) {
      return arbShaders ? ARBVertexShader.glGetAttribLocationARB(program, name) : GL20.glGetAttribLocation(program, name);
   }

   public static int glGenBuffers() {
      return arbVbo ? ARBVertexBufferObject.glGenBuffersARB() : GL15.glGenBuffers();
   }

   public static void glBindBuffer(int target, int buffer) {
      if (arbVbo) {
         ARBVertexBufferObject.glBindBufferARB(target, buffer);
      } else {
         GL15.glBindBuffer(target, buffer);
      }

   }

   public static void glBufferData(int target, ByteBuffer data, int usage) {
      if (arbVbo) {
         ARBVertexBufferObject.glBufferDataARB(target, data, usage);
      } else {
         GL15.glBufferData(target, data, usage);
      }

   }

   public static void glDeleteBuffers(int buffer) {
      if (arbVbo) {
         ARBVertexBufferObject.glDeleteBuffersARB(buffer);
      } else {
         GL15.glDeleteBuffers(buffer);
      }

   }

   public static boolean useVbo() {
      return vboSupported && Minecraft.getInstance().gameSettings.useVbo;
   }

   public static void glBindFramebuffer(int target, int framebufferIn) {
      if (framebufferSupported) {
         switch(framebufferType) {
         case BASE:
            GL30.glBindFramebuffer(target, framebufferIn);
            break;
         case ARB:
            ARBFramebufferObject.glBindFramebuffer(target, framebufferIn);
            break;
         case EXT:
            EXTFramebufferObject.glBindFramebufferEXT(target, framebufferIn);
         }

      }
   }

   public static void glBindRenderbuffer(int target, int renderbuffer) {
      if (framebufferSupported) {
         switch(framebufferType) {
         case BASE:
            GL30.glBindRenderbuffer(target, renderbuffer);
            break;
         case ARB:
            ARBFramebufferObject.glBindRenderbuffer(target, renderbuffer);
            break;
         case EXT:
            EXTFramebufferObject.glBindRenderbufferEXT(target, renderbuffer);
         }

      }
   }

   public static void glDeleteRenderbuffers(int renderbuffer) {
      if (framebufferSupported) {
         switch(framebufferType) {
         case BASE:
            GL30.glDeleteRenderbuffers(renderbuffer);
            break;
         case ARB:
            ARBFramebufferObject.glDeleteRenderbuffers(renderbuffer);
            break;
         case EXT:
            EXTFramebufferObject.glDeleteRenderbuffersEXT(renderbuffer);
         }

      }
   }

   public static void glDeleteFramebuffers(int framebufferIn) {
      if (framebufferSupported) {
         switch(framebufferType) {
         case BASE:
            GL30.glDeleteFramebuffers(framebufferIn);
            break;
         case ARB:
            ARBFramebufferObject.glDeleteFramebuffers(framebufferIn);
            break;
         case EXT:
            EXTFramebufferObject.glDeleteFramebuffersEXT(framebufferIn);
         }

      }
   }

   /**
    * Calls the appropriate glGenFramebuffers method and returns the newly created fbo, or returns -1 if not supported.
    */
   public static int glGenFramebuffers() {
      if (!framebufferSupported) {
         return -1;
      } else {
         switch(framebufferType) {
         case BASE:
            return GL30.glGenFramebuffers();
         case ARB:
            return ARBFramebufferObject.glGenFramebuffers();
         case EXT:
            return EXTFramebufferObject.glGenFramebuffersEXT();
         default:
            return -1;
         }
      }
   }

   public static int glGenRenderbuffers() {
      if (!framebufferSupported) {
         return -1;
      } else {
         switch(framebufferType) {
         case BASE:
            return GL30.glGenRenderbuffers();
         case ARB:
            return ARBFramebufferObject.glGenRenderbuffers();
         case EXT:
            return EXTFramebufferObject.glGenRenderbuffersEXT();
         default:
            return -1;
         }
      }
   }

   public static void glRenderbufferStorage(int target, int internalFormat, int width, int height) {
      if (framebufferSupported) {
         switch(framebufferType) {
         case BASE:
            GL30.glRenderbufferStorage(target, internalFormat, width, height);
            break;
         case ARB:
            ARBFramebufferObject.glRenderbufferStorage(target, internalFormat, width, height);
            break;
         case EXT:
            EXTFramebufferObject.glRenderbufferStorageEXT(target, internalFormat, width, height);
         }

      }
   }

   public static void glFramebufferRenderbuffer(int target, int attachment, int renderBufferTarget, int renderBuffer) {
      if (framebufferSupported) {
         switch(framebufferType) {
         case BASE:
            GL30.glFramebufferRenderbuffer(target, attachment, renderBufferTarget, renderBuffer);
            break;
         case ARB:
            ARBFramebufferObject.glFramebufferRenderbuffer(target, attachment, renderBufferTarget, renderBuffer);
            break;
         case EXT:
            EXTFramebufferObject.glFramebufferRenderbufferEXT(target, attachment, renderBufferTarget, renderBuffer);
         }

      }
   }

   public static int glCheckFramebufferStatus(int target) {
      if (!framebufferSupported) {
         return -1;
      } else {
         switch(framebufferType) {
         case BASE:
            return GL30.glCheckFramebufferStatus(target);
         case ARB:
            return ARBFramebufferObject.glCheckFramebufferStatus(target);
         case EXT:
            return EXTFramebufferObject.glCheckFramebufferStatusEXT(target);
         default:
            return -1;
         }
      }
   }

   public static void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
      if (framebufferSupported) {
         switch(framebufferType) {
         case BASE:
            GL30.glFramebufferTexture2D(target, attachment, textarget, texture, level);
            break;
         case ARB:
            ARBFramebufferObject.glFramebufferTexture2D(target, attachment, textarget, texture, level);
            break;
         case EXT:
            EXTFramebufferObject.glFramebufferTexture2DEXT(target, attachment, textarget, texture, level);
         }

      }
   }

   /**
    * Sets the current lightmap texture to the specified OpenGL constant
    */
   public static void glActiveTexture(int texture) {
      if (arbMultitexture) {
         ARBMultitexture.glActiveTextureARB(texture);
      } else {
         GL13.glActiveTexture(texture);
      }

   }

   /**
    * Sets the current lightmap texture to the specified OpenGL constant
    */
   public static void glClientActiveTexture(int texture) {
      if (arbMultitexture) {
         ARBMultitexture.glClientActiveTextureARB(texture);
      } else {
         GL13.glClientActiveTexture(texture);
      }

   }

   /**
    * Sets the current coordinates of the given lightmap texture
    */
   public static void glMultiTexCoord2f(int target, float x, float y) {
      if (arbMultitexture) {
         ARBMultitexture.glMultiTexCoord2fARB(target, x, y);
      } else {
         GL13.glMultiTexCoord2f(target, x, y);
      }

      if (target == GL_TEXTURE1) {
         lastBrightnessX = x;
         lastBrightnessY = y;
      }
   }

   public static void glBlendFuncSeparate(int sFactorRGB, int dFactorRGB, int sfactorAlpha, int dfactorAlpha) {
      if (openGL14) {
         if (extBlendFuncSeparate) {
            EXTBlendFuncSeparate.glBlendFuncSeparateEXT(sFactorRGB, dFactorRGB, sfactorAlpha, dfactorAlpha);
         } else {
            GL14.glBlendFuncSeparate(sFactorRGB, dFactorRGB, sfactorAlpha, dfactorAlpha);
         }
      } else {
         GL11.glBlendFunc(sFactorRGB, dFactorRGB);
      }

   }

   public static boolean isFramebufferEnabled() {
      return framebufferSupported && Minecraft.getInstance().gameSettings.fboEnable;
   }

   public static String getCpu() {
      return cpu == null ? "<unknown>" : cpu;
   }

   public static void renderDirections(int size) {
      renderDirections(size, true, true, true);
   }

   public static void renderDirections(int size, boolean renderX, boolean renderY, boolean renderZ) {
      GlStateManager.disableTexture2D();
      GlStateManager.depthMask(false);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      GL11.glLineWidth(4.0F);
      bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
      if (renderX) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.pos((double)size, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
      }

      if (renderY) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.pos(0.0D, (double)size, 0.0D).color(0, 0, 0, 255).endVertex();
      }

      if (renderZ) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.pos(0.0D, 0.0D, (double)size).color(0, 0, 0, 255).endVertex();
      }

      tessellator.draw();
      GL11.glLineWidth(2.0F);
      bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
      if (renderX) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(255, 0, 0, 255).endVertex();
         bufferbuilder.pos((double)size, 0.0D, 0.0D).color(255, 0, 0, 255).endVertex();
      }

      if (renderY) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 255, 0, 255).endVertex();
         bufferbuilder.pos(0.0D, (double)size, 0.0D).color(0, 255, 0, 255).endVertex();
      }

      if (renderZ) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(127, 127, 255, 255).endVertex();
         bufferbuilder.pos(0.0D, 0.0D, (double)size).color(127, 127, 255, 255).endVertex();
      }

      tessellator.draw();
      GL11.glLineWidth(1.0F);
      GlStateManager.depthMask(true);
      GlStateManager.enableTexture2D();
   }

   public static String getErrorMessage(int error) {
      return MAP_ERROR_MESSAGES.get(error);
   }

   @OnlyIn(Dist.CLIENT)
   static enum FboMode {
      BASE,
      ARB,
      EXT;
   }
}