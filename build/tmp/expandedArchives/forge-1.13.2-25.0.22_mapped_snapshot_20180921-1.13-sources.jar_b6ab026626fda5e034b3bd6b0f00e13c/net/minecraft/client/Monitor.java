package net.minecraft.client;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.renderer.VideoMode;
import net.minecraft.client.renderer.VirtualScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

@OnlyIn(Dist.CLIENT)
public final class Monitor {
   private final VirtualScreen virtualScreen;
   private final long monitorPointer;
   private final List<VideoMode> videoModes;
   private VideoMode defaultVideoMode;
   private int virtualPosX;
   private int virtualPosY;

   public Monitor(VirtualScreen virtualScreenIn, long monitorPointerIn) {
      this.virtualScreen = virtualScreenIn;
      this.monitorPointer = monitorPointerIn;
      this.videoModes = Lists.newArrayList();
      this.setup();
   }

   public void setup() {
      this.videoModes.clear();
      Buffer buffer = GLFW.glfwGetVideoModes(this.monitorPointer);

      for(int i = 0; i < buffer.limit(); ++i) {
         buffer.position(i);
         VideoMode videomode = new VideoMode(buffer);
         if (videomode.getRedBits() >= 8 && videomode.getGreenBits() >= 8 && videomode.getBlueBits() >= 8) {
            this.videoModes.add(videomode);
         }
      }

      int[] aint = new int[1];
      int[] aint1 = new int[1];
      GLFW.glfwGetMonitorPos(this.monitorPointer, aint, aint1);
      this.virtualPosX = aint[0];
      this.virtualPosY = aint1[0];
      GLFWVidMode glfwvidmode = GLFW.glfwGetVideoMode(this.monitorPointer);
      this.defaultVideoMode = new VideoMode(glfwvidmode);
   }

   VideoMode getVideoModeOrDefault(Optional<VideoMode> optionalVideoMode) {
      if (optionalVideoMode.isPresent()) {
         VideoMode videomode = optionalVideoMode.get();

         for(VideoMode videomode1 : Lists.reverse(this.videoModes)) {
            if (videomode1.equals(videomode)) {
               return videomode1;
            }
         }
      }

      return this.getDefaultVideoMode();
   }

   int getVideoModeOrDefaultIndex(Optional<VideoMode> optionalVideoMode) {
      if (optionalVideoMode.isPresent()) {
         VideoMode videomode = optionalVideoMode.get();

         for(int i = this.videoModes.size() - 1; i >= 0; --i) {
            if (videomode.equals(this.videoModes.get(i))) {
               return i;
            }
         }
      }

      return this.videoModes.indexOf(this.getDefaultVideoMode());
   }

   public VideoMode getDefaultVideoMode() {
      return this.defaultVideoMode;
   }

   public int getVirtualPosX() {
      return this.virtualPosX;
   }

   public int getVirtualPosY() {
      return this.virtualPosY;
   }

   public VideoMode getVideoModeFromIndex(int index) {
      return this.videoModes.get(index);
   }

   public int getVideoModeCount() {
      return this.videoModes.size();
   }

   public long getMonitorPointer() {
      return this.monitorPointer;
   }

   public String toString() {
      return String.format("Monitor[%s %sx%s %s]", this.monitorPointer, this.virtualPosX, this.virtualPosY, this.defaultVideoMode);
   }
}