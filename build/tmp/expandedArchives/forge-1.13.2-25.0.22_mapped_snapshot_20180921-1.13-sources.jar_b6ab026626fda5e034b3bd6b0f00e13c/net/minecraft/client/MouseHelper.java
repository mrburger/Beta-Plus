package net.minecraft.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.MouseSmoother;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class MouseHelper {
   private final Minecraft minecraft;
   /** True if the left mouse button is down */
   private boolean leftDown;
   /** True if the middle mouse button is down (unused) */
   private boolean middleDown;
   /** True if the right mouse button is down */
   private boolean rightDown;
   private double mouseX;
   private double mouseY;
   private int field_212148_g;
   /**
    * The mouse button being dragged. Values are -1 (none) or a GLFW mouse button, usually {@link
    * GLFW#GLFW_MOUSE_BUTTON_LEFT} (0) / {@link GLFW#GLFW_MOUSE_BUTTON_RIGHT} (1) / {@link GLFW_MOUSE_BUTTON_MIDDLE}
    * (2).
    */
   private int activeButton = -1;
   private boolean ignoreFirstMove = true;
   private int touchScreenCounter;
   private double eventTime;
   private final MouseSmoother xSmoother = new MouseSmoother();
   private final MouseSmoother ySmoother = new MouseSmoother();
   private double xVelocity;
   private double yVelocity;
   private double accumulatedScrollDelta;
   private double field_198050_o = Double.MIN_VALUE;
   /** True if the mouse is grabbed. */
   private boolean mouseGrabbed;

   public MouseHelper(Minecraft minecraftIn) {
      this.minecraft = minecraftIn;
   }

   /**
    * Will be called when a mouse button is pressed or released.
    *  
    * @see GLFWMouseButtonCallbackI
    *  
    * @param handle the window that received the event
    * @param button the mouse button that was pressed or released
    * @param action the button action. One of:<br><table><tr><td>{@link GLFW#GLFW_PRESS PRESS}</td><td>{@link
    * GLFW#GLFW_RELEASE RELEASE}</td><td>{@link GLFW#GLFW_REPEAT REPEAT}</td></tr></table>
    * @param mods bitfield describing which modifiers keys were held down
    */
   private void mouseButtonCallback(long handle, int button, int action, int mods) {
      if (handle == this.minecraft.mainWindow.getHandle()) {
         boolean flag = action == 1;
         if (Minecraft.IS_RUNNING_ON_MAC && button == 0) {
            if (flag) {
               if ((mods & 2) == 2) {
                  button = 1;
                  ++this.field_212148_g;
               }
            } else if (this.field_212148_g > 0) {
               button = 1;
               --this.field_212148_g;
            }
         }

         if (flag) {
            if (this.minecraft.gameSettings.touchscreen && this.touchScreenCounter++ > 0) {
               return;
            }

            this.activeButton = button;
            this.eventTime = GLFW.glfwGetTime();
         } else if (this.activeButton != -1) {
            if (this.minecraft.gameSettings.touchscreen && --this.touchScreenCounter > 0) {
               return;
            }

            this.activeButton = -1;
         }

         boolean[] aboolean = new boolean[]{false};
         if (this.minecraft.currentScreen == null) {
            if (!this.mouseGrabbed && flag) {
               this.grabMouse();
            }
         } else {
            double d0 = this.mouseX * (double)this.minecraft.mainWindow.getScaledWidth() / (double)this.minecraft.mainWindow.getWidth();
            double d1 = this.mouseY * (double)this.minecraft.mainWindow.getScaledHeight() / (double)this.minecraft.mainWindow.getHeight();
            final int pFinal =  button;
            if (flag) {
               GuiScreen.runOrMakeCrashReport(() -> {
                  aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onGuiMouseClickedPre(this.minecraft.currentScreen, d0, d1, pFinal);
                  if (!aboolean[0]) aboolean[0] = this.minecraft.currentScreen.mouseClicked(d0, d1, pFinal);
                  if (!aboolean[0]) aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onGuiMouseClickedPost(this.minecraft.currentScreen, d0, d1, pFinal);
               }, "mouseClicked event handler", this.minecraft.currentScreen.getClass().getCanonicalName());
            } else {
               GuiScreen.runOrMakeCrashReport(() -> {
                  aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onGuiMouseReleasedPre(this.minecraft.currentScreen, d0, d1, pFinal);
                  if (!aboolean[0]) aboolean[0] = this.minecraft.currentScreen.mouseReleased(d0, d1, pFinal);
                  if (!aboolean[0]) aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onGuiMouseReleasedPost(this.minecraft.currentScreen, d0, d1, pFinal);
               }, "mouseReleased event handler", this.minecraft.currentScreen.getClass().getCanonicalName());
            }
         }

         if (!aboolean[0] && (this.minecraft.currentScreen == null || this.minecraft.currentScreen.allowUserInput)) {
            if (button == 0) {
               this.leftDown = flag;
            } else if (button == 2) {
               this.middleDown = flag;
            } else if (button == 1) {
               this.rightDown = flag;
            }

            KeyBinding.setKeyBindState(InputMappings.Type.MOUSE.getOrMakeInput(button), flag);
            if (flag) {
               if (this.minecraft.player.isSpectator() && button == 2) {
                  this.minecraft.ingameGUI.getSpectatorGui().onMiddleClick();
               } else {
                  KeyBinding.onTick(InputMappings.Type.MOUSE.getOrMakeInput(button));
               }
            }
         }

      }
   }

   /**
    * Will be called when a scrolling device is used, such as a mouse wheel or scrolling area of a touchpad.
    *  
    * @see GLFWScrollCallbackI
    *  
    * @param handle the window that received the event
    * @param xoffset the scroll offset along the x-axis
    * @param yoffset the scroll offset along the y-axis
    */
   private void scrollCallback(long handle, double xoffset, double yoffset) {
      if (handle == Minecraft.getInstance().mainWindow.getHandle()) {
         double d0 = yoffset * this.minecraft.gameSettings.mouseWheelSensitivity;
         if (this.minecraft.currentScreen != null) {
            if (net.minecraftforge.client.ForgeHooksClient.onGuiMouseScrollPre(this, this.minecraft.currentScreen, d0)) return;
            if (this.minecraft.currentScreen.mouseScrolled(d0)) return;
            net.minecraftforge.client.ForgeHooksClient.onGuiMouseScrollPost(this, this.minecraft.currentScreen, d0);
         } else if (this.minecraft.player != null) {
            if (this.accumulatedScrollDelta != 0.0D && Math.signum(d0) != Math.signum(this.accumulatedScrollDelta)) {
               this.accumulatedScrollDelta = 0.0D;
            }

            this.accumulatedScrollDelta += d0;
            double d1 = (double)((int)this.accumulatedScrollDelta);
            if (d1 == 0.0D) {
               return;
            }

            this.accumulatedScrollDelta -= d1;
            if (this.minecraft.player.isSpectator()) {
               if (this.minecraft.ingameGUI.getSpectatorGui().isMenuActive()) {
                  this.minecraft.ingameGUI.getSpectatorGui().func_195621_a(-d1);
               } else {
                  double d2 = MathHelper.clamp((double)this.minecraft.player.abilities.getFlySpeed() + d1 * (double)0.005F, 0.0D, (double)0.2F);
                  this.minecraft.player.abilities.setFlySpeed(d2);
               }
            } else {
               this.minecraft.player.inventory.changeCurrentItem(d1);
            }
         }
      }

   }

   public void registerCallbacks(long handle) {
      GLFW.glfwSetCursorPosCallback(handle, this::cursorPosCallback);
      GLFW.glfwSetMouseButtonCallback(handle, this::mouseButtonCallback);
      GLFW.glfwSetScrollCallback(handle, this::scrollCallback);
   }

   /**
    * Will be called when the cursor is moved.
    *  
    * <p>The callback function receives the cursor position, measured in screen coordinates but relative to the top-left
    * corner of the window client area. On platforms that provide it, the full sub-pixel cursor position is passed
    * on.</p>
    *  
    * @see GLFWCursorPosCallbackI
    *  
    * @param handle the window that received the event
    * @param xpos the new cursor x-coordinate, relative to the left edge of the client area
    * @param ypos the new cursor y-coordinate, relative to the top edge of the client area
    */
   private void cursorPosCallback(long handle, double xpos, double ypos) {
      if (handle == Minecraft.getInstance().mainWindow.getHandle()) {
         if (this.ignoreFirstMove) {
            this.mouseX = xpos;
            this.mouseY = ypos;
            this.ignoreFirstMove = false;
         }

         IGuiEventListener iguieventlistener = this.minecraft.currentScreen;
         if (this.activeButton != -1 && this.eventTime > 0.0D && iguieventlistener != null) {
            double d0 = xpos * (double)this.minecraft.mainWindow.getScaledWidth() / (double)this.minecraft.mainWindow.getWidth();
            double d1 = ypos * (double)this.minecraft.mainWindow.getScaledHeight() / (double)this.minecraft.mainWindow.getHeight();
            double d2 = (xpos - this.mouseX) * (double)this.minecraft.mainWindow.getScaledWidth() / (double)this.minecraft.mainWindow.getWidth();
            double d3 = (ypos - this.mouseY) * (double)this.minecraft.mainWindow.getScaledHeight() / (double)this.minecraft.mainWindow.getHeight();
            GuiScreen.runOrMakeCrashReport(() -> {
               if (net.minecraftforge.client.ForgeHooksClient.onGuiMouseDragPre(this.minecraft.currentScreen, d0, d1, this.activeButton, d2, d3)) return;
               if (iguieventlistener.mouseDragged(d0, d1, this.activeButton, d2, d3)) return;
               net.minecraftforge.client.ForgeHooksClient.onGuiMouseDragPost(this.minecraft.currentScreen, d0, d1, this.activeButton, d2, d3);
            }, "mouseDragged event handler", iguieventlistener.getClass().getCanonicalName());
         }

         this.minecraft.profiler.startSection("mouse");
         if (this.isMouseGrabbed() && this.minecraft.isGameFocused()) {
            this.xVelocity += xpos - this.mouseX;
            this.yVelocity += ypos - this.mouseY;
         }

         this.updatePlayerLook();
         this.mouseX = xpos;
         this.mouseY = ypos;
         this.minecraft.profiler.endSection();
      }
   }

   public void updatePlayerLook() {
      double d0 = GLFW.glfwGetTime();
      double d1 = d0 - this.field_198050_o;
      this.field_198050_o = d0;
      if (this.isMouseGrabbed() && this.minecraft.isGameFocused()) {
         double d4 = this.minecraft.gameSettings.mouseSensitivity * (double)0.6F + (double)0.2F;
         double d5 = d4 * d4 * d4 * 8.0D;
         double d2;
         double d3;
         if (this.minecraft.gameSettings.smoothCamera) {
            double d6 = this.xSmoother.smooth(this.xVelocity * d5, d1 * d5);
            double d7 = this.ySmoother.smooth(this.yVelocity * d5, d1 * d5);
            d2 = d6;
            d3 = d7;
         } else {
            this.xSmoother.reset();
            this.ySmoother.reset();
            d2 = this.xVelocity * d5;
            d3 = this.yVelocity * d5;
         }

         this.xVelocity = 0.0D;
         this.yVelocity = 0.0D;
         int i = 1;
         if (this.minecraft.gameSettings.invertMouse) {
            i = -1;
         }

         this.minecraft.getTutorial().onMouseMove(d2, d3);
         if (this.minecraft.player != null) {
            this.minecraft.player.rotateTowards(d2, d3 * (double)i);
         }

      } else {
         this.xVelocity = 0.0D;
         this.yVelocity = 0.0D;
      }
   }

   public boolean isLeftDown() {
      return this.leftDown;
   }

   public boolean isRightDown() {
      return this.rightDown;
   }

   public double getMouseX() {
      return this.mouseX;
   }

   public double getMouseY() {
      return this.mouseY;
   }

   public void setIgnoreFirstMove() {
      this.ignoreFirstMove = true;
   }

   /**
    * Returns true if the mouse is grabbed.
    */
   public boolean isMouseGrabbed() {
      return this.mouseGrabbed;
   }

   /**
    * Will set the focus to ingame if the Minecraft window is the active with focus. Also clears any GUI screen
    * currently displayed
    */
   public void grabMouse() {
      if (this.minecraft.isGameFocused()) {
         if (!this.mouseGrabbed) {
            if (!Minecraft.IS_RUNNING_ON_MAC) {
               KeyBinding.updateKeyBindState();
            }

            this.mouseGrabbed = true;
            this.mouseX = (double)(this.minecraft.mainWindow.getWidth() / 2);
            this.mouseY = (double)(this.minecraft.mainWindow.getHeight() / 2);
            GLFW.glfwSetCursorPos(this.minecraft.mainWindow.getHandle(), this.mouseX, this.mouseY);
            GLFW.glfwSetInputMode(this.minecraft.mainWindow.getHandle(), 208897, 212995);
            this.minecraft.displayGuiScreen((GuiScreen)null);
            this.minecraft.leftClickCounter = 10000;
         }
      }
   }

   /**
    * Resets the player keystate, disables the ingame focus, and ungrabs the mouse cursor.
    */
   public void ungrabMouse() {
      if (this.mouseGrabbed) {
         this.mouseGrabbed = false;
         GLFW.glfwSetInputMode(this.minecraft.mainWindow.getHandle(), 208897, 212993);
         this.mouseX = (double)(this.minecraft.mainWindow.getWidth() / 2);
         this.mouseY = (double)(this.minecraft.mainWindow.getHeight() / 2);
         GLFW.glfwSetCursorPos(this.minecraft.mainWindow.getHandle(), this.mouseX, this.mouseY);
      }
   }
}