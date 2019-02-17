package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.LWJGLMemoryUntracker;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class GlStateManager {
   private static final FloatBuffer BUF_FLOAT_16 = Util.make(MemoryUtil.memAllocFloat(16), (p_209238_0_) -> {
      LWJGLMemoryUntracker.untrack(MemoryUtil.memAddress(p_209238_0_));
   });
   private static final FloatBuffer BUF_FLOAT_4 = Util.make(MemoryUtil.memAllocFloat(4), (p_209236_0_) -> {
      LWJGLMemoryUntracker.untrack(MemoryUtil.memAddress(p_209236_0_));
   });
   private static final GlStateManager.AlphaState ALPHA = new GlStateManager.AlphaState();
   private static final GlStateManager.BooleanState LIGHTING = new GlStateManager.BooleanState(2896);
   private static final GlStateManager.BooleanState[] LIGHTS = IntStream.range(0, 8).mapToObj((p_199933_0_) -> {
      return new GlStateManager.BooleanState(16384 + p_199933_0_);
   }).toArray((p_199930_0_) -> {
      return new GlStateManager.BooleanState[p_199930_0_];
   });
   private static final GlStateManager.ColorMaterialState COLOR_MATERIAL = new GlStateManager.ColorMaterialState();
   private static final GlStateManager.BlendState BLEND = new GlStateManager.BlendState();
   private static final GlStateManager.DepthState DEPTH = new GlStateManager.DepthState();
   private static final GlStateManager.FogState FOG = new GlStateManager.FogState();
   private static final GlStateManager.CullState CULL = new GlStateManager.CullState();
   private static final GlStateManager.PolygonOffsetState POLYGON_OFFSET = new GlStateManager.PolygonOffsetState();
   private static final GlStateManager.ColorLogicState COLOR_LOGIC = new GlStateManager.ColorLogicState();
   private static final GlStateManager.TexGenState TEX_GEN = new GlStateManager.TexGenState();
   private static final GlStateManager.ClearState CLEAR = new GlStateManager.ClearState();
   private static final GlStateManager.StencilState STENCIL = new GlStateManager.StencilState();
   private static final GlStateManager.BooleanState NORMALIZE = new GlStateManager.BooleanState(2977);
   private static int activeTexture;
   private static final GlStateManager.TextureState[] TEXTURES = IntStream.range(0, 8).mapToObj((p_199931_0_) -> {
      return new GlStateManager.TextureState();
   }).toArray((p_199932_0_) -> {
      return new GlStateManager.TextureState[p_199932_0_];
   });
   private static int activeShadeModel = 7425;
   private static final GlStateManager.BooleanState RESCALE_NORMAL = new GlStateManager.BooleanState(32826);
   private static final GlStateManager.ColorMask COLOR_MASK = new GlStateManager.ColorMask();
   private static final GlStateManager.Color COLOR = new GlStateManager.Color();

   /**
    * Do not use (see MinecraftForge issue #1637)
    */
   public static void pushLightingAttrib() {
      GL11.glPushAttrib(8256);
   }

   /**
    * Do not use (see MinecraftForge issue #1637)
    */
   public static void popAttrib() {
      GL11.glPopAttrib();
   }

   public static void disableAlphaTest() {
      ALPHA.test.setDisabled();
   }

   public static void enableAlphaTest() {
      ALPHA.test.setEnabled();
   }

   public static void alphaFunc(int func, float ref) {
      if (func != ALPHA.func || ref != ALPHA.ref) {
         ALPHA.func = func;
         ALPHA.ref = ref;
         GL11.glAlphaFunc(func, ref);
      }

   }

   public static void enableLighting() {
      LIGHTING.setEnabled();
   }

   public static void disableLighting() {
      LIGHTING.setDisabled();
   }

   public static void enableLight(int light) {
      LIGHTS[light].setEnabled();
   }

   public static void disableLight(int light) {
      LIGHTS[light].setDisabled();
   }

   public static void enableColorMaterial() {
      COLOR_MATERIAL.colorMaterial.setEnabled();
   }

   public static void disableColorMaterial() {
      COLOR_MATERIAL.colorMaterial.setDisabled();
   }

   public static void colorMaterial(int face, int mode) {
      if (face != COLOR_MATERIAL.face || mode != COLOR_MATERIAL.mode) {
         COLOR_MATERIAL.face = face;
         COLOR_MATERIAL.mode = mode;
         GL11.glColorMaterial(face, mode);
      }

   }

   public static void lightfv(int light, int pname, FloatBuffer params) {
      GL11.glLightfv(light, pname, params);
   }

   public static void lightModelfv(int pname, FloatBuffer params) {
      GL11.glLightModelfv(pname, params);
   }

   public static void normal3f(float nx, float ny, float nz) {
      GL11.glNormal3f(nx, ny, nz);
   }

   public static void disableDepthTest() {
      DEPTH.test.setDisabled();
   }

   public static void enableDepthTest() {
      DEPTH.test.setEnabled();
   }

   public static void depthFunc(int depthFunc) {
      if (depthFunc != DEPTH.func) {
         DEPTH.func = depthFunc;
         GL11.glDepthFunc(depthFunc);
      }

   }

   public static void depthMask(boolean flagIn) {
      if (flagIn != DEPTH.mask) {
         DEPTH.mask = flagIn;
         GL11.glDepthMask(flagIn);
      }

   }

   public static void disableBlend() {
      BLEND.blend.setDisabled();
   }

   public static void enableBlend() {
      BLEND.blend.setEnabled();
   }

   public static void blendFunc(GlStateManager.SourceFactor srcFactor, GlStateManager.DestFactor dstFactor) {
      blendFunc(srcFactor.factor, dstFactor.factor);
   }

   public static void blendFunc(int srcFactor, int dstFactor) {
      if (srcFactor != BLEND.srcFactor || dstFactor != BLEND.dstFactor) {
         BLEND.srcFactor = srcFactor;
         BLEND.dstFactor = dstFactor;
         GL11.glBlendFunc(srcFactor, dstFactor);
      }

   }

   public static void blendFuncSeparate(GlStateManager.SourceFactor srcFactor, GlStateManager.DestFactor dstFactor, GlStateManager.SourceFactor srcFactorAlpha, GlStateManager.DestFactor dstFactorAlpha) {
      blendFuncSeparate(srcFactor.factor, dstFactor.factor, srcFactorAlpha.factor, dstFactorAlpha.factor);
   }

   public static void blendFuncSeparate(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha) {
      if (srcFactor != BLEND.srcFactor || dstFactor != BLEND.dstFactor || srcFactorAlpha != BLEND.srcFactorAlpha || dstFactorAlpha != BLEND.dstFactorAlpha) {
         BLEND.srcFactor = srcFactor;
         BLEND.dstFactor = dstFactor;
         BLEND.srcFactorAlpha = srcFactorAlpha;
         BLEND.dstFactorAlpha = dstFactorAlpha;
         OpenGlHelper.glBlendFuncSeparate(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
      }

   }

   public static void blendEquation(int blendEquation) {
      GL14.glBlendEquation(blendEquation);
   }

   public static void enableOutlineMode(int color) {
      BUF_FLOAT_4.put(0, (float)(color >> 16 & 255) / 255.0F);
      BUF_FLOAT_4.put(1, (float)(color >> 8 & 255) / 255.0F);
      BUF_FLOAT_4.put(2, (float)(color >> 0 & 255) / 255.0F);
      BUF_FLOAT_4.put(3, (float)(color >> 24 & 255) / 255.0F);
      texEnvfv(8960, 8705, BUF_FLOAT_4);
      texEnvi(8960, 8704, 34160);
      texEnvi(8960, 34161, 7681);
      texEnvi(8960, 34176, 34166);
      texEnvi(8960, 34192, 768);
      texEnvi(8960, 34162, 7681);
      texEnvi(8960, 34184, 5890);
      texEnvi(8960, 34200, 770);
   }

   public static void disableOutlineMode() {
      texEnvi(8960, 8704, 8448);
      texEnvi(8960, 34161, 8448);
      texEnvi(8960, 34162, 8448);
      texEnvi(8960, 34176, 5890);
      texEnvi(8960, 34184, 5890);
      texEnvi(8960, 34192, 768);
      texEnvi(8960, 34200, 770);
   }

   public static void enableFog() {
      FOG.fog.setEnabled();
   }

   public static void disableFog() {
      FOG.fog.setDisabled();
   }

   public static void fogMode(GlStateManager.FogMode fogMode) {
      fogMode(fogMode.capabilityId);
   }

   private static void fogMode(int param) {
      if (param != FOG.mode) {
         FOG.mode = param;
         GL11.glFogi(2917, param);
      }

   }

   public static void fogDensity(float param) {
      if (param != FOG.density) {
         FOG.density = param;
         GL11.glFogf(2914, param);
      }

   }

   public static void fogStart(float param) {
      if (param != FOG.start) {
         FOG.start = param;
         GL11.glFogf(2915, param);
      }

   }

   public static void fogEnd(float param) {
      if (param != FOG.end) {
         FOG.end = param;
         GL11.glFogf(2916, param);
      }

   }

   public static void fogfv(int pname, FloatBuffer param) {
      GL11.glFogfv(pname, param);
   }

   public static void fogi(int pname, int param) {
      GL11.glFogi(pname, param);
   }

   public static void enableCull() {
      CULL.cullFace.setEnabled();
   }

   public static void disableCull() {
      CULL.cullFace.setDisabled();
   }

   public static void cullFace(GlStateManager.CullFace cullFace) {
      cullFace(cullFace.mode);
   }

   private static void cullFace(int mode) {
      if (mode != CULL.mode) {
         CULL.mode = mode;
         GL11.glCullFace(mode);
      }

   }

   public static void polygonMode(int face, int mode) {
      GL11.glPolygonMode(face, mode);
   }

   public static void enablePolygonOffset() {
      POLYGON_OFFSET.fill.setEnabled();
   }

   public static void disablePolygonOffset() {
      POLYGON_OFFSET.fill.setDisabled();
   }

   public static void polygonOffset(float factor, float units) {
      if (factor != POLYGON_OFFSET.factor || units != POLYGON_OFFSET.units) {
         POLYGON_OFFSET.factor = factor;
         POLYGON_OFFSET.units = units;
         GL11.glPolygonOffset(factor, units);
      }

   }

   public static void enableColorLogic() {
      COLOR_LOGIC.colorLogicOp.setEnabled();
   }

   public static void disableColorLogic() {
      COLOR_LOGIC.colorLogicOp.setDisabled();
   }

   public static void logicOp(GlStateManager.LogicOp logicOperation) {
      logicOp(logicOperation.opcode);
   }

   public static void logicOp(int opcode) {
      if (opcode != COLOR_LOGIC.opcode) {
         COLOR_LOGIC.opcode = opcode;
         GL11.glLogicOp(opcode);
      }

   }

   public static void enableTexGen(GlStateManager.TexGen texGen) {
      texGenCoord(texGen).textureGen.setEnabled();
   }

   public static void disableTexGen(GlStateManager.TexGen texGen) {
      texGenCoord(texGen).textureGen.setDisabled();
   }

   public static void texGenMode(GlStateManager.TexGen texGen, int mode) {
      GlStateManager.TexGenCoord glstatemanager$texgencoord = texGenCoord(texGen);
      if (mode != glstatemanager$texgencoord.mode) {
         glstatemanager$texgencoord.mode = mode;
         GL11.glTexGeni(glstatemanager$texgencoord.coord, 9472, mode);
      }

   }

   public static void texGenParam(GlStateManager.TexGen texGen, int pname, FloatBuffer params) {
      GL11.glTexGenfv(texGenCoord(texGen).coord, pname, params);
   }

   private static GlStateManager.TexGenCoord texGenCoord(GlStateManager.TexGen texGen) {
      switch(texGen) {
      case S:
         return TEX_GEN.s;
      case T:
         return TEX_GEN.t;
      case R:
         return TEX_GEN.r;
      case Q:
         return TEX_GEN.q;
      default:
         return TEX_GEN.s;
      }
   }

   public static void activeTexture(int texture) {
      if (activeTexture != texture - OpenGlHelper.GL_TEXTURE0) {
         activeTexture = texture - OpenGlHelper.GL_TEXTURE0;
         OpenGlHelper.glActiveTexture(texture);
      }

   }

   public static void enableTexture2D() {
      TEXTURES[activeTexture].texture2DState.setEnabled();
   }

   public static void disableTexture2D() {
      TEXTURES[activeTexture].texture2DState.setDisabled();
   }

   public static void texEnvfv(int target, int parameterName, FloatBuffer parameters) {
      GL11.glTexEnvfv(target, parameterName, parameters);
   }

   public static void texEnvi(int target, int parameterName, int parameter) {
      GL11.glTexEnvi(target, parameterName, parameter);
   }

   public static void texEnvf(int target, int parameterName, float parameter) {
      GL11.glTexEnvf(target, parameterName, parameter);
   }

   public static void texParameterf(int target, int parameterName, float parameter) {
      GL11.glTexParameterf(target, parameterName, parameter);
   }

   public static void texParameteri(int target, int parameterName, int parameter) {
      GL11.glTexParameteri(target, parameterName, parameter);
   }

   public static int glGetTexLevelParameteri(int target, int level, int parameterName) {
      return GL11.glGetTexLevelParameteri(target, level, parameterName);
   }

   public static int generateTexture() {
      return GL11.glGenTextures();
   }

   public static void deleteTexture(int texture) {
      GL11.glDeleteTextures(texture);

      for(GlStateManager.TextureState glstatemanager$texturestate : TEXTURES) {
         if (glstatemanager$texturestate.textureName == texture) {
            glstatemanager$texturestate.textureName = -1;
         }
      }

   }

   public static void bindTexture(int texture) {
      if (texture != TEXTURES[activeTexture].textureName) {
         TEXTURES[activeTexture].textureName = texture;
         GL11.glBindTexture(3553, texture);
      }

   }

   public static void texImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, @Nullable IntBuffer pixels) {
      GL11.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
   }

   public static void texSubImage2D(int target, int level, int xOffset, int yOffset, int width, int height, int format, int type, long pixels) {
      GL11.glTexSubImage2D(target, level, xOffset, yOffset, width, height, format, type, pixels);
   }

   public static void getTexImage(int tex, int level, int format, int type, long pixels) {
      GL11.glGetTexImage(tex, level, format, type, pixels);
   }

   public static void enableNormalize() {
      NORMALIZE.setEnabled();
   }

   public static void disableNormalize() {
      NORMALIZE.setDisabled();
   }

   public static void shadeModel(int mode) {
      if (mode != activeShadeModel) {
         activeShadeModel = mode;
         GL11.glShadeModel(mode);
      }

   }

   public static void enableRescaleNormal() {
      RESCALE_NORMAL.setEnabled();
   }

   public static void disableRescaleNormal() {
      RESCALE_NORMAL.setDisabled();
   }

   public static void viewport(int x, int y, int width, int height) {
      GlStateManager.Viewport.INSTANCE.x = x;
      GlStateManager.Viewport.INSTANCE.y = y;
      GlStateManager.Viewport.INSTANCE.width = width;
      GlStateManager.Viewport.INSTANCE.height = height;
      GL11.glViewport(x, y, width, height);
   }

   public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
      if (red != COLOR_MASK.red || green != COLOR_MASK.green || blue != COLOR_MASK.blue || alpha != COLOR_MASK.alpha) {
         COLOR_MASK.red = red;
         COLOR_MASK.green = green;
         COLOR_MASK.blue = blue;
         COLOR_MASK.alpha = alpha;
         GL11.glColorMask(red, green, blue, alpha);
      }

   }

   public static void clearDepth(double depth) {
      if (depth != CLEAR.depth) {
         CLEAR.depth = depth;
         GL11.glClearDepth(depth);
      }

   }

   public static void clearColor(float red, float green, float blue, float alpha) {
      if (red != CLEAR.color.red || green != CLEAR.color.green || blue != CLEAR.color.blue || alpha != CLEAR.color.alpha) {
         CLEAR.color.red = red;
         CLEAR.color.green = green;
         CLEAR.color.blue = blue;
         CLEAR.color.alpha = alpha;
         GL11.glClearColor(red, green, blue, alpha);
      }

   }

   public static void clear(int mask) {
      GL11.glClear(mask);
      if (Minecraft.IS_RUNNING_ON_MAC) {
         getError();
      }

   }

   public static void matrixMode(int mode) {
      GL11.glMatrixMode(mode);
   }

   public static void loadIdentity() {
      GL11.glLoadIdentity();
   }

   public static void pushMatrix() {
      GL11.glPushMatrix();
   }

   public static void popMatrix() {
      GL11.glPopMatrix();
   }

   public static void getFloatv(int pname, FloatBuffer params) {
      GL11.glGetFloatv(pname, params);
   }

   public static void ortho(double left, double right, double bottom, double top, double zNear, double zFar) {
      GL11.glOrtho(left, right, bottom, top, zNear, zFar);
   }

   public static void rotatef(float angle, float x, float y, float z) {
      GL11.glRotatef(angle, x, y, z);
   }

   public static void func_212477_a(double p_212477_0_, double p_212477_2_, double p_212477_4_, double p_212477_6_) {
      GL11.glRotated(p_212477_0_, p_212477_2_, p_212477_4_, p_212477_6_);
   }

   public static void scalef(float x, float y, float z) {
      GL11.glScalef(x, y, z);
   }

   public static void scaled(double x, double y, double z) {
      GL11.glScaled(x, y, z);
   }

   public static void translatef(float x, float y, float z) {
      GL11.glTranslatef(x, y, z);
   }

   public static void translated(double x, double y, double z) {
      GL11.glTranslated(x, y, z);
   }

   public static void multMatrixf(FloatBuffer matrix) {
      GL11.glMultMatrixf(matrix);
   }

   public static void multMatrixf(Matrix4f matrixIn) {
      matrixIn.write(BUF_FLOAT_16);
      BUF_FLOAT_16.rewind();
      GL11.glMultMatrixf(BUF_FLOAT_16);
   }

   public static void color4f(float colorRed, float colorGreen, float colorBlue, float colorAlpha) {
      if (colorRed != COLOR.red || colorGreen != COLOR.green || colorBlue != COLOR.blue || colorAlpha != COLOR.alpha) {
         COLOR.red = colorRed;
         COLOR.green = colorGreen;
         COLOR.blue = colorBlue;
         COLOR.alpha = colorAlpha;
         GL11.glColor4f(colorRed, colorGreen, colorBlue, colorAlpha);
      }

   }

   public static void color3f(float colorRed, float colorGreen, float colorBlue) {
      color4f(colorRed, colorGreen, colorBlue, 1.0F);
   }

   public static void resetColor() {
      COLOR.red = -1.0F;
      COLOR.green = -1.0F;
      COLOR.blue = -1.0F;
      COLOR.alpha = -1.0F;
   }

   public static void normalPointer(int type, int stride, int pointer) {
      GL11.glNormalPointer(type, stride, (long)pointer);
   }

   public static void normalPointer(int type, int stride, ByteBuffer buffer) {
      GL11.glNormalPointer(type, stride, buffer);
   }

   public static void texCoordPointer(int size, int type, int stride, int buffer_offset) {
      GL11.glTexCoordPointer(size, type, stride, (long)buffer_offset);
   }

   public static void texCoordPointer(int size, int type, int stride, ByteBuffer buffer) {
      GL11.glTexCoordPointer(size, type, stride, buffer);
   }

   public static void vertexPointer(int size, int type, int stride, int buffer_offset) {
      GL11.glVertexPointer(size, type, stride, (long)buffer_offset);
   }

   public static void vertexPointer(int size, int type, int stride, ByteBuffer buffer) {
      GL11.glVertexPointer(size, type, stride, buffer);
   }

   public static void colorPointer(int size, int type, int stride, int buffer_offset) {
      GL11.glColorPointer(size, type, stride, (long)buffer_offset);
   }

   public static void colorPointer(int size, int type, int stride, ByteBuffer buffer) {
      GL11.glColorPointer(size, type, stride, buffer);
   }

   public static void disableClientState(int cap) {
      GL11.glDisableClientState(cap);
   }

   public static void enableClientState(int cap) {
      GL11.glEnableClientState(cap);
   }

   public static void drawArrays(int mode, int first, int count) {
      GL11.glDrawArrays(mode, first, count);
   }

   public static void lineWidth(float width) {
      GL11.glLineWidth(width);
   }

   public static void callList(int list) {
      GL11.glCallList(list);
   }

   public static void deleteLists(int list, int range) {
      GL11.glDeleteLists(list, range);
   }

   public static void newList(int list, int mode) {
      GL11.glNewList(list, mode);
   }

   public static void endList() {
      GL11.glEndList();
   }

   public static int genLists(int range) {
      return GL11.glGenLists(range);
   }

   public static void pixelStorei(int parameterName, int param) {
      GL11.glPixelStorei(parameterName, param);
   }

   public static void pixelTransferf(int param, float value) {
      GL11.glPixelTransferf(param, value);
   }

   public static void readPixels(int x, int y, int width, int height, int format, int type, long pixels) {
      GL11.glReadPixels(x, y, width, height, format, type, pixels);
   }

   public static int getError() {
      return GL11.glGetError();
   }

   public static String getString(int name) {
      return GL11.glGetString(name);
   }

   public static void enableBlendProfile(GlStateManager.Profile profile) {
      profile.apply();
   }

   public static void disableBlendProfile(GlStateManager.Profile profile) {
      profile.clean();
   }

   @OnlyIn(Dist.CLIENT)
   static class AlphaState {
      public GlStateManager.BooleanState test = new GlStateManager.BooleanState(3008);
      public int func = 519;
      public float ref = -1.0F;

      private AlphaState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class BlendState {
      public GlStateManager.BooleanState blend = new GlStateManager.BooleanState(3042);
      public int srcFactor = 1;
      public int dstFactor = 0;
      public int srcFactorAlpha = 1;
      public int dstFactorAlpha = 0;

      private BlendState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class BooleanState {
      private final int capability;
      private boolean currentState;

      public BooleanState(int capabilityIn) {
         this.capability = capabilityIn;
      }

      public void setDisabled() {
         this.setState(false);
      }

      public void setEnabled() {
         this.setState(true);
      }

      public void setState(boolean state) {
         if (state != this.currentState) {
            this.currentState = state;
            if (state) {
               GL11.glEnable(this.capability);
            } else {
               GL11.glDisable(this.capability);
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   static class ClearState {
      public double depth = 1.0D;
      public GlStateManager.Color color = new GlStateManager.Color(0.0F, 0.0F, 0.0F, 0.0F);

      private ClearState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Color {
      public float red = 1.0F;
      public float green = 1.0F;
      public float blue = 1.0F;
      public float alpha = 1.0F;

      public Color() {
         this(1.0F, 1.0F, 1.0F, 1.0F);
      }

      public Color(float redIn, float greenIn, float blueIn, float alphaIn) {
         this.red = redIn;
         this.green = greenIn;
         this.blue = blueIn;
         this.alpha = alphaIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class ColorLogicState {
      public GlStateManager.BooleanState colorLogicOp = new GlStateManager.BooleanState(3058);
      public int opcode = 5379;

      private ColorLogicState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class ColorMask {
      public boolean red = true;
      public boolean green = true;
      public boolean blue = true;
      public boolean alpha = true;

      private ColorMask() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class ColorMaterialState {
      public GlStateManager.BooleanState colorMaterial = new GlStateManager.BooleanState(2903);
      public int face = 1032;
      public int mode = 5634;

      private ColorMaterialState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum CullFace {
      FRONT(1028),
      BACK(1029),
      FRONT_AND_BACK(1032);

      public final int mode;

      private CullFace(int modeIn) {
         this.mode = modeIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class CullState {
      public GlStateManager.BooleanState cullFace = new GlStateManager.BooleanState(2884);
      public int mode = 1029;

      private CullState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class DepthState {
      public GlStateManager.BooleanState test = new GlStateManager.BooleanState(2929);
      public boolean mask = true;
      public int func = 513;

      private DepthState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum DestFactor {
      CONSTANT_ALPHA(32771),
      CONSTANT_COLOR(32769),
      DST_ALPHA(772),
      DST_COLOR(774),
      ONE(1),
      ONE_MINUS_CONSTANT_ALPHA(32772),
      ONE_MINUS_CONSTANT_COLOR(32770),
      ONE_MINUS_DST_ALPHA(773),
      ONE_MINUS_DST_COLOR(775),
      ONE_MINUS_SRC_ALPHA(771),
      ONE_MINUS_SRC_COLOR(769),
      SRC_ALPHA(770),
      SRC_COLOR(768),
      ZERO(0);

      public final int factor;

      private DestFactor(int factorIn) {
         this.factor = factorIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum FogMode {
      LINEAR(9729),
      EXP(2048),
      EXP2(2049);

      /** The capability ID of this {@link FogMode} */
      public final int capabilityId;

      private FogMode(int capabilityIn) {
         this.capabilityId = capabilityIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class FogState {
      public GlStateManager.BooleanState fog = new GlStateManager.BooleanState(2912);
      public int mode = 2048;
      public float density = 1.0F;
      public float start;
      public float end = 1.0F;

      private FogState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum LogicOp {
      AND(5377),
      AND_INVERTED(5380),
      AND_REVERSE(5378),
      CLEAR(5376),
      COPY(5379),
      COPY_INVERTED(5388),
      EQUIV(5385),
      INVERT(5386),
      NAND(5390),
      NOOP(5381),
      NOR(5384),
      OR(5383),
      OR_INVERTED(5389),
      OR_REVERSE(5387),
      SET(5391),
      XOR(5382);

      public final int opcode;

      private LogicOp(int opcodeIn) {
         this.opcode = opcodeIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class PolygonOffsetState {
      public GlStateManager.BooleanState fill = new GlStateManager.BooleanState(32823);
      public GlStateManager.BooleanState line = new GlStateManager.BooleanState(10754);
      public float factor;
      public float units;

      private PolygonOffsetState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Profile {
      DEFAULT {
         public void apply() {
            GlStateManager.disableAlphaTest();
            GlStateManager.alphaFunc(519, 0.0F);
            GlStateManager.disableLighting();
            GlStateManager.lightModelfv(2899, RenderHelper.setColorBuffer(0.2F, 0.2F, 0.2F, 1.0F));

            for(int i = 0; i < 8; ++i) {
               GlStateManager.disableLight(i);
               GlStateManager.lightfv(16384 + i, 4608, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
               GlStateManager.lightfv(16384 + i, 4611, RenderHelper.setColorBuffer(0.0F, 0.0F, 1.0F, 0.0F));
               if (i == 0) {
                  GlStateManager.lightfv(16384 + i, 4609, RenderHelper.setColorBuffer(1.0F, 1.0F, 1.0F, 1.0F));
                  GlStateManager.lightfv(16384 + i, 4610, RenderHelper.setColorBuffer(1.0F, 1.0F, 1.0F, 1.0F));
               } else {
                  GlStateManager.lightfv(16384 + i, 4609, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                  GlStateManager.lightfv(16384 + i, 4610, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
               }
            }

            GlStateManager.disableColorMaterial();
            GlStateManager.colorMaterial(1032, 5634);
            GlStateManager.disableDepthTest();
            GlStateManager.depthFunc(513);
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendEquation(32774);
            GlStateManager.disableFog();
            GlStateManager.fogi(2917, 2048);
            GlStateManager.fogDensity(1.0F);
            GlStateManager.fogStart(0.0F);
            GlStateManager.fogEnd(1.0F);
            GlStateManager.fogfv(2918, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            if (GL.getCapabilities().GL_NV_fog_distance) {
               GlStateManager.fogi(2917, 34140);
            }

            GlStateManager.polygonOffset(0.0F, 0.0F);
            GlStateManager.disableColorLogic();
            GlStateManager.logicOp(5379);
            GlStateManager.disableTexGen(GlStateManager.TexGen.S);
            GlStateManager.texGenMode(GlStateManager.TexGen.S, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.S, 9474, RenderHelper.setColorBuffer(1.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.S, 9217, RenderHelper.setColorBuffer(1.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.disableTexGen(GlStateManager.TexGen.T);
            GlStateManager.texGenMode(GlStateManager.TexGen.T, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.T, 9474, RenderHelper.setColorBuffer(0.0F, 1.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.T, 9217, RenderHelper.setColorBuffer(0.0F, 1.0F, 0.0F, 0.0F));
            GlStateManager.disableTexGen(GlStateManager.TexGen.R);
            GlStateManager.texGenMode(GlStateManager.TexGen.R, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.R, 9474, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.R, 9217, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.disableTexGen(GlStateManager.TexGen.Q);
            GlStateManager.texGenMode(GlStateManager.TexGen.Q, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.Q, 9474, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.Q, 9217, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.activeTexture(0);
            GlStateManager.texParameteri(3553, 10240, 9729);
            GlStateManager.texParameteri(3553, 10241, 9986);
            GlStateManager.texParameteri(3553, 10242, 10497);
            GlStateManager.texParameteri(3553, 10243, 10497);
            GlStateManager.texParameteri(3553, 33085, 1000);
            GlStateManager.texParameteri(3553, 33083, 1000);
            GlStateManager.texParameteri(3553, 33082, -1000);
            GlStateManager.texParameterf(3553, 34049, 0.0F);
            GlStateManager.texEnvi(8960, 8704, 8448);
            GlStateManager.texEnvfv(8960, 8705, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texEnvi(8960, 34161, 8448);
            GlStateManager.texEnvi(8960, 34162, 8448);
            GlStateManager.texEnvi(8960, 34176, 5890);
            GlStateManager.texEnvi(8960, 34177, 34168);
            GlStateManager.texEnvi(8960, 34178, 34166);
            GlStateManager.texEnvi(8960, 34184, 5890);
            GlStateManager.texEnvi(8960, 34185, 34168);
            GlStateManager.texEnvi(8960, 34186, 34166);
            GlStateManager.texEnvi(8960, 34192, 768);
            GlStateManager.texEnvi(8960, 34193, 768);
            GlStateManager.texEnvi(8960, 34194, 770);
            GlStateManager.texEnvi(8960, 34200, 770);
            GlStateManager.texEnvi(8960, 34201, 770);
            GlStateManager.texEnvi(8960, 34202, 770);
            GlStateManager.texEnvf(8960, 34163, 1.0F);
            GlStateManager.texEnvf(8960, 3356, 1.0F);
            GlStateManager.disableNormalize();
            GlStateManager.shadeModel(7425);
            GlStateManager.disableRescaleNormal();
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.clearDepth(1.0D);
            GlStateManager.lineWidth(1.0F);
            GlStateManager.normal3f(0.0F, 0.0F, 1.0F);
            GlStateManager.polygonMode(1028, 6914);
            GlStateManager.polygonMode(1029, 6914);
         }

         public void clean() {
         }
      },
      PLAYER_SKIN {
         public void apply() {
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(770, 771, 1, 0);
         }

         public void clean() {
            GlStateManager.disableBlend();
         }
      },
      TRANSPARENT_MODEL {
         public void apply() {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.15F);
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.alphaFunc(516, 0.003921569F);
         }

         public void clean() {
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.depthMask(true);
         }
      };

      private Profile() {
      }

      public abstract void apply();

      public abstract void clean();
   }

   @OnlyIn(Dist.CLIENT)
   public static enum SourceFactor {
      CONSTANT_ALPHA(32771),
      CONSTANT_COLOR(32769),
      DST_ALPHA(772),
      DST_COLOR(774),
      ONE(1),
      ONE_MINUS_CONSTANT_ALPHA(32772),
      ONE_MINUS_CONSTANT_COLOR(32770),
      ONE_MINUS_DST_ALPHA(773),
      ONE_MINUS_DST_COLOR(775),
      ONE_MINUS_SRC_ALPHA(771),
      ONE_MINUS_SRC_COLOR(769),
      SRC_ALPHA(770),
      SRC_ALPHA_SATURATE(776),
      SRC_COLOR(768),
      ZERO(0);

      public final int factor;

      private SourceFactor(int factorIn) {
         this.factor = factorIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class StencilFunc {
      public int func = 519;
      public int mask = -1;

      private StencilFunc() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class StencilState {
      public GlStateManager.StencilFunc func = new GlStateManager.StencilFunc();
      public int mask = -1;
      public int fail = 7680;
      public int zfail = 7680;
      public int zpass = 7680;

      private StencilState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum TexGen {
      S,
      T,
      R,
      Q;
   }

   @OnlyIn(Dist.CLIENT)
   static class TexGenCoord {
      public GlStateManager.BooleanState textureGen;
      public int coord;
      public int mode = -1;

      public TexGenCoord(int coordIn, int capabilityIn) {
         this.coord = coordIn;
         this.textureGen = new GlStateManager.BooleanState(capabilityIn);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class TexGenState {
      public GlStateManager.TexGenCoord s = new GlStateManager.TexGenCoord(8192, 3168);
      public GlStateManager.TexGenCoord t = new GlStateManager.TexGenCoord(8193, 3169);
      public GlStateManager.TexGenCoord r = new GlStateManager.TexGenCoord(8194, 3170);
      public GlStateManager.TexGenCoord q = new GlStateManager.TexGenCoord(8195, 3171);

      private TexGenState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class TextureState {
      public GlStateManager.BooleanState texture2DState = new GlStateManager.BooleanState(3553);
      public int textureName;

      private TextureState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Viewport {
      INSTANCE;

      protected int x;
      protected int y;
      protected int width;
      protected int height;
   }
}