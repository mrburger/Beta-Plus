package net.minecraft.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Optional;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.VideoMode;
import net.minecraft.client.renderer.VirtualScreen;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public final class MainWindow implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final GLFWErrorCallback loggingErrorCallback = GLFWErrorCallback.create(this::logGlError);
   private final Minecraft mc;
   private final VirtualScreen virtualScreen;
   private Monitor monitor;
   private final long handle;
   private int prevWindowX;
   private int prevWindowY;
   private int prevWindowWidth;
   private int prevWindowHeight;
   private Optional<VideoMode> videoMode;
   private boolean fullscreen;
   private boolean lastFullscreen;
   private int windowX;
   private int windowY;
   private int width;
   private int height;
   private int framebufferWidth;
   private int framebufferHeight;
   private int scaledWidth;
   private int scaledHeight;
   private double guiScaleFactor;
   private String renderPhase = "";
   private boolean videoModeChanged;
   private double field_198139_z = Double.MIN_VALUE;

   public MainWindow(Minecraft mcIn, VirtualScreen virtualScreenIn, GameConfiguration.DisplayInformation displayInfoIn, String fullscreenResolution) {
      this.virtualScreen = virtualScreenIn;
      this.setThrowExceptionOnGlError();
      this.setRenderPhase("Pre startup");
      this.mc = mcIn;
      Optional<VideoMode> optional = VideoMode.parseFromSettings(fullscreenResolution);
      if (optional.isPresent()) {
         this.videoMode = optional;
      } else if (displayInfoIn.fullscreenWidth.isPresent() && displayInfoIn.fullscreenHeight.isPresent()) {
         this.videoMode = Optional.of(new VideoMode(displayInfoIn.fullscreenWidth.get(), displayInfoIn.fullscreenHeight.get(), 8, 8, 8, 60));
      } else {
         this.videoMode = Optional.empty();
      }

      this.lastFullscreen = this.fullscreen = displayInfoIn.fullscreen;
      this.monitor = virtualScreenIn.getMonitor(GLFW.glfwGetPrimaryMonitor());
      VideoMode videomode = this.monitor.getVideoModeOrDefault(this.fullscreen ? this.videoMode : Optional.empty());
      this.prevWindowWidth = this.width = displayInfoIn.width > 0 ? displayInfoIn.width : 1;
      this.prevWindowHeight = this.height = displayInfoIn.height > 0 ? displayInfoIn.height : 1;
      this.prevWindowX = this.windowX = this.monitor.getVirtualPosX() + videomode.getWidth() / 2 - this.width / 2;
      this.prevWindowY = this.windowY = this.monitor.getVirtualPosY() + videomode.getHeight() / 2 - this.height / 2;
      GLFW.glfwDefaultWindowHints();
      this.handle = GLFW.glfwCreateWindow(this.width, this.height, "Minecraft 1.13.2", this.fullscreen ? this.monitor.getMonitorPointer() : 0L, 0L);
      mcIn.isWindowFocused = true;
      this.setMonitorFromVirtualScreen();
      GLFW.glfwMakeContextCurrent(this.handle);
      GL.createCapabilities();
      this.updateVideoMode();
      this.updateFramebufferSize();
      this.loadIcon();
      GLFW.glfwSetFramebufferSizeCallback(this.handle, this::onFramebufferSizeUpdate);
      GLFW.glfwSetWindowPosCallback(this.handle, this::onWindowPosUpdate);
      GLFW.glfwSetWindowSizeCallback(this.handle, this::onWindowSizeUpdate);
      GLFW.glfwSetWindowFocusCallback(this.handle, this::onWindowFocusUpdate);
      mcIn.mouseHelper = new MouseHelper(mcIn);
      mcIn.mouseHelper.registerCallbacks(this.handle);
      mcIn.keyboardListener = new KeyboardListener(mcIn);
      mcIn.keyboardListener.setupCallbacks(this.handle);
   }

   public static void checkGlfwError(BiConsumer<Integer, String> p_211162_0_) {
      try (MemoryStack memorystack = MemoryStack.stackPush()) {
         PointerBuffer pointerbuffer = memorystack.mallocPointer(1);
         int i = GLFW.glfwGetError(pointerbuffer);
         if (i != 0) {
            long j = pointerbuffer.get();
            String s = j != 0L ? MemoryUtil.memUTF8(j) : "";
            p_211162_0_.accept(i, s);
         }
      }

   }

   public void setupOverlayRendering() {
      GlStateManager.clear(256);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.ortho(0.0D, (double)this.getFramebufferWidth() / this.getGuiScaleFactor(), (double)this.getFramebufferHeight() / this.getGuiScaleFactor(), 0.0D, 1000.0D, 3000.0D);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
   }

   private void loadIcon() {
      try (
         MemoryStack memorystack = MemoryStack.stackPush();
         InputStream inputstream = this.mc.getPackFinder().getVanillaPack().getResourceStream(ResourcePackType.CLIENT_RESOURCES, new ResourceLocation("icons/icon_16x16.png"));
         InputStream inputstream1 = this.mc.getPackFinder().getVanillaPack().getResourceStream(ResourcePackType.CLIENT_RESOURCES, new ResourceLocation("icons/icon_32x32.png"));
      ) {
         if (inputstream == null) {
            throw new FileNotFoundException("icons/icon_16x16.png");
         }

         if (inputstream1 == null) {
            throw new FileNotFoundException("icons/icon_32x32.png");
         }

         IntBuffer intbuffer = memorystack.mallocInt(1);
         IntBuffer intbuffer1 = memorystack.mallocInt(1);
         IntBuffer intbuffer2 = memorystack.mallocInt(1);
         Buffer buffer = GLFWImage.mallocStack(2, memorystack);
         ByteBuffer bytebuffer = this.func_198111_a(inputstream, intbuffer, intbuffer1, intbuffer2);
         if (bytebuffer == null) {
            throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
         }

         buffer.position(0);
         buffer.width(intbuffer.get(0));
         buffer.height(intbuffer1.get(0));
         buffer.pixels(bytebuffer);
         ByteBuffer bytebuffer1 = this.func_198111_a(inputstream1, intbuffer, intbuffer1, intbuffer2);
         if (bytebuffer1 == null) {
            throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
         }

         buffer.position(1);
         buffer.width(intbuffer.get(0));
         buffer.height(intbuffer1.get(0));
         buffer.pixels(bytebuffer1);
         buffer.position(0);
         GLFW.glfwSetWindowIcon(this.handle, buffer);
         STBImage.stbi_image_free(bytebuffer);
         STBImage.stbi_image_free(bytebuffer1);
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't set icon", (Throwable)ioexception);
      }

   }

   @Nullable
   private ByteBuffer func_198111_a(InputStream p_198111_1_, IntBuffer p_198111_2_, IntBuffer p_198111_3_, IntBuffer p_198111_4_) throws IOException {
      ByteBuffer bytebuffer = null;

      ByteBuffer bytebuffer1;
      try {
         bytebuffer = TextureUtil.readToNativeBuffer(p_198111_1_);
         bytebuffer.rewind();
         bytebuffer1 = STBImage.stbi_load_from_memory(bytebuffer, p_198111_2_, p_198111_3_, p_198111_4_, 0);
      } finally {
         if (bytebuffer != null) {
            MemoryUtil.memFree(bytebuffer);
         }

      }

      return bytebuffer1;
   }

   void setRenderPhase(String renderPhaseIn) {
      this.renderPhase = renderPhaseIn;
   }

   private void setThrowExceptionOnGlError() {
      GLFW.glfwSetErrorCallback(MainWindow::throwExceptionForGlError);
   }

   private static void throwExceptionForGlError(int error, long description) {
      throw new IllegalStateException("GLFW error " + error + ": " + MemoryUtil.memUTF8(description));
   }

   void logGlError(int error, long description) {
      String s = MemoryUtil.memUTF8(description);
      LOGGER.error("########## GL ERROR ##########");
      LOGGER.error("@ {}", (Object)this.renderPhase);
      LOGGER.error("{}: {}", error, s);
   }

   void setLogOnGlError() {
      GLFW.glfwSetErrorCallback(this.loggingErrorCallback).free();
   }

   public void updateVsyncFromGameSettings() {
      GLFW.glfwSwapInterval(this.mc.gameSettings.enableVsync ? 1 : 0);
   }

   public void close() {
      Util.nanoTimeSupplier = System::nanoTime;
      Callbacks.glfwFreeCallbacks(this.handle);
      this.loggingErrorCallback.close();
      GLFW.glfwDestroyWindow(this.handle);
      GLFW.glfwTerminate();
   }

   private void setMonitorFromVirtualScreen() {
      this.monitor = this.virtualScreen.getMonitor(this);
   }

   private void onWindowPosUpdate(long windowPointer, int windowXIn, int windowYIn) {
      this.windowX = windowXIn;
      this.windowY = windowYIn;
      this.setMonitorFromVirtualScreen();
   }

   private void onFramebufferSizeUpdate(long windowPointer, int framebufferWidth, int framebufferHeight) {
      if (windowPointer == this.handle) {
         int i = this.getFramebufferWidth();
         int j = this.getFramebufferHeight();
         if (framebufferWidth != 0 && framebufferHeight != 0) {
            this.framebufferWidth = framebufferWidth;
            this.framebufferHeight = framebufferHeight;
            if (this.getFramebufferWidth() != i || this.getFramebufferHeight() != j) {
               this.updateSize();
            }

         }
      }
   }

   private void updateFramebufferSize() {
      int[] aint = new int[1];
      int[] aint1 = new int[1];
      GLFW.glfwGetFramebufferSize(this.handle, aint, aint1);
      this.framebufferWidth = aint[0];
      this.framebufferHeight = aint1[0];
   }

   private void onWindowSizeUpdate(long windowPointer, int windowWidthIn, int windowHeightIn) {
      this.width = windowWidthIn;
      this.height = windowHeightIn;
      this.setMonitorFromVirtualScreen();
   }

   private void onWindowFocusUpdate(long windowPointer, boolean hasFocus) {
      if (windowPointer == this.handle) {
         this.mc.isWindowFocused = hasFocus;
      }

   }

   private int getLimitFramerate() {
      return this.mc.world == null && this.mc.currentScreen != null ? 60 : this.mc.gameSettings.limitFramerate;
   }

   public boolean isFramerateLimitBelowMax() {
      return (double)this.getLimitFramerate() < GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
   }

   public void update(boolean p_198086_1_) {
      this.mc.profiler.startSection("display_update");
      GLFW.glfwSwapBuffers(this.handle);
      GLFW.glfwPollEvents();
      if (this.fullscreen != this.lastFullscreen) {
         this.lastFullscreen = this.fullscreen;
         this.updateFullscreenState();
      }

      this.mc.profiler.endSection();
      if (p_198086_1_ && this.isFramerateLimitBelowMax()) {
         this.mc.profiler.startSection("fpslimit_wait");
         double d0 = this.field_198139_z + 1.0D / (double)this.getLimitFramerate();

         double d1;
         for(d1 = GLFW.glfwGetTime(); d1 < d0; d1 = GLFW.glfwGetTime()) {
            GLFW.glfwWaitEventsTimeout(d0 - d1);
         }

         this.field_198139_z = d1;
         this.mc.profiler.endSection();
      }

   }

   public Optional<VideoMode> getVideoMode() {
      return this.videoMode;
   }

   public int getVideoModeIndex() {
      return this.videoMode.isPresent() ? this.monitor.getVideoModeOrDefaultIndex(this.videoMode) + 1 : 0;
   }

   public String getVideoModeString(int index) {
      if (this.monitor.getVideoModeCount() <= index) {
         index = this.monitor.getVideoModeCount() - 1;
      }

      return this.monitor.getVideoModeFromIndex(index).toString();
   }

   public void setFullscreenResolution(int index) {
      Optional<VideoMode> optional = this.videoMode;
      if (index == 0) {
         this.videoMode = Optional.empty();
      } else {
         this.videoMode = Optional.of(this.monitor.getVideoModeFromIndex(index - 1));
      }

      if (!this.videoMode.equals(optional)) {
         this.videoModeChanged = true;
      }

   }

   public void update() {
      if (this.fullscreen && this.videoModeChanged) {
         this.videoModeChanged = false;
         this.updateVideoMode();
         this.updateSize();
      }

   }

   private void updateVideoMode() {
      boolean flag = GLFW.glfwGetWindowMonitor(this.handle) != 0L;
      if (this.fullscreen) {
         VideoMode videomode = this.monitor.getVideoModeOrDefault(this.videoMode);
         if (!flag) {
            this.prevWindowX = this.windowX;
            this.prevWindowY = this.windowY;
            this.prevWindowWidth = this.width;
            this.prevWindowHeight = this.height;
         }

         this.windowX = 0;
         this.windowY = 0;
         this.width = videomode.getWidth();
         this.height = videomode.getHeight();
         GLFW.glfwSetWindowMonitor(this.handle, this.monitor.getMonitorPointer(), this.windowX, this.windowY, this.width, this.height, videomode.getRefreshRate());
      } else {
         VideoMode videomode1 = this.monitor.getDefaultVideoMode();
         this.windowX = this.prevWindowX;
         this.windowY = this.prevWindowY;
         this.width = this.prevWindowWidth;
         this.height = this.prevWindowHeight;
         GLFW.glfwSetWindowMonitor(this.handle, 0L, this.windowX, this.windowY, this.width, this.height, -1);
      }

   }

   public void toggleFullscreen() {
      this.fullscreen = !this.fullscreen;
      this.mc.gameSettings.fullScreen = this.fullscreen;
   }

   private void updateFullscreenState() {
      try {
         this.updateVideoMode();
         this.updateSize();
         this.updateVsyncFromGameSettings();
         this.update(false);
      } catch (Exception exception) {
         LOGGER.error("Couldn't toggle fullscreen", (Throwable)exception);
      }

   }

   public void updateSize() {
      this.guiScaleFactor = (double)this.getScaleFactor(this.mc.gameSettings.guiScale);
      this.scaledWidth = MathHelper.ceil((double)this.framebufferWidth / this.guiScaleFactor);
      this.scaledHeight = MathHelper.ceil((double)this.framebufferHeight / this.guiScaleFactor);
      if (this.mc.currentScreen != null) {
         this.mc.currentScreen.onResize(this.mc, this.scaledWidth, this.scaledHeight);
      }

      Framebuffer framebuffer = this.mc.getFramebuffer();
      if (framebuffer != null) {
         framebuffer.createBindFramebuffer(this.framebufferWidth, this.framebufferHeight);
      }

      if (this.mc.entityRenderer != null) {
         this.mc.entityRenderer.updateShaderGroupSize(this.framebufferWidth, this.framebufferHeight);
      }

      if (this.mc.mouseHelper != null) {
         this.mc.mouseHelper.setIgnoreFirstMove();
      }

   }

   public int getScaleFactor(int guiScale) {
      int i;
      for(i = 1; i != guiScale && i < this.framebufferWidth && i < this.framebufferHeight && this.framebufferWidth / (i + 1) >= 320 && this.framebufferHeight / (i + 1) >= 240; ++i) {
         ;
      }

      if (this.mc.getForceUnicodeFont() && i % 2 != 0) {
         ++i;
      }

      return i;
   }

   /**
    * Gets a pointer to the native window object that is passed to GLFW.
    */
   public long getHandle() {
      return this.handle;
   }

   public boolean isFullscreen() {
      return this.fullscreen;
   }

   public int getFramebufferWidth() {
      return this.framebufferWidth;
   }

   public int getFramebufferHeight() {
      return this.framebufferHeight;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getScaledWidth() {
      return this.scaledWidth;
   }

   public int getScaledHeight() {
      return this.scaledHeight;
   }

   public int getWindowX() {
      return this.windowX;
   }

   public int getWindowY() {
      return this.windowY;
   }

   public double getGuiScaleFactor() {
      return this.guiScaleFactor;
   }
}