package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.GameSettings;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Monitor;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallback;
import org.lwjgl.glfw.GLFWMonitorCallbackI;

@OnlyIn(Dist.CLIENT)
public final class VirtualScreen implements AutoCloseable {
   private final Minecraft mc;
   private final Map<Long, Monitor> monitorMap = Maps.newHashMap();
   private final Map<Long, MainWindow> unusedMap = Maps.newHashMap();
   private final Map<MainWindow, Monitor> windowToMonitorMap = Maps.newHashMap();

   public VirtualScreen(Minecraft mcIn) {
      this.mc = mcIn;
      GLFW.glfwSetMonitorCallback(this::onMonitorConfigurationChange);
      PointerBuffer pointerbuffer = GLFW.glfwGetMonitors();

      for(int i = 0; i < pointerbuffer.limit(); ++i) {
         long j = pointerbuffer.get(i);
         this.monitorMap.put(j, new Monitor(this, j));
      }

   }

   private void onMonitorConfigurationChange(long monitorIn, int event) {
      if (event == 262145) {
         this.monitorMap.put(monitorIn, new Monitor(this, monitorIn));
      } else if (event == 262146) {
         this.monitorMap.remove(monitorIn);
      }

   }

   public Monitor getMonitor(long monitorPointer) {
      return this.monitorMap.get(monitorPointer);
   }

   public Monitor getMonitor(MainWindow window) {
      long i = GLFW.glfwGetWindowMonitor(window.getHandle());
      if (i != 0L) {
         return this.monitorMap.get(i);
      } else {
         Monitor monitor = this.monitorMap.values().iterator().next();
         int j = -1;
         int k = window.getWindowX();
         int l = k + window.getWidth();
         int i1 = window.getWindowY();
         int j1 = i1 + window.getHeight();

         for(Monitor monitor1 : this.monitorMap.values()) {
            int k1 = monitor1.getVirtualPosX();
            int l1 = k1 + monitor1.getDefaultVideoMode().getWidth();
            int i2 = monitor1.getVirtualPosY();
            int j2 = i2 + monitor1.getDefaultVideoMode().getHeight();
            int k2 = MathHelper.clamp(k, k1, l1);
            int l2 = MathHelper.clamp(l, k1, l1);
            int i3 = MathHelper.clamp(i1, i2, j2);
            int j3 = MathHelper.clamp(j1, i2, j2);
            int k3 = Math.max(0, l2 - k2);
            int l3 = Math.max(0, j3 - i3);
            int i4 = k3 * l3;
            if (i4 > j) {
               monitor = monitor1;
               j = i4;
            }
         }

         if (monitor != this.windowToMonitorMap.get(window)) {
            this.windowToMonitorMap.put(window, monitor);
            GameSettings.Options.FULLSCREEN_RESOLUTION.setValueMax((float)monitor.getVideoModeCount());
         }

         return monitor;
      }
   }

   public MainWindow createMainWindow(GameConfiguration.DisplayInformation displayInfoIn, String fullscreenResolution) {
      return new MainWindow(this.mc, this, displayInfoIn, fullscreenResolution);
   }

   public void close() {
      GLFWMonitorCallback glfwmonitorcallback = GLFW.glfwSetMonitorCallback((GLFWMonitorCallbackI)null);
      if (glfwmonitorcallback != null) {
         glfwmonitorcallback.free();
      }

   }
}