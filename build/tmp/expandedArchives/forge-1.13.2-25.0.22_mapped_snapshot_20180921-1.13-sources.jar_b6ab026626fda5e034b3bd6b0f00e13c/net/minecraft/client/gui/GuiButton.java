package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class GuiButton extends Gui implements IGuiEventListener {
   protected static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("textures/gui/widgets.png");
   /** Button width in pixels */
   public int width = 200;
   /** Button height in pixels */
   public int height = 20;
   /** The x position of this control. */
   public int x;
   /** The y position of this control. */
   public int y;
   /** The string displayed on this control. */
   public String displayString;
   public int id;
   /** True if this control is enabled, false to disable. */
   public boolean enabled = true;
   /** Hides the button completely if false. */
   public boolean visible = true;
   protected boolean hovered;
   private boolean isDragging;
   public int packedFGColor; // FML

   public GuiButton(int buttonId, int x, int y, String buttonText) {
      this(buttonId, x, y, 200, 20, buttonText);
   }

   public GuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
      this.id = buttonId;
      this.x = x;
      this.y = y;
      this.width = widthIn;
      this.height = heightIn;
      this.displayString = buttonText;
   }

   /**
    * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
    * this button.
    */
   protected int getHoverState(boolean mouseOver) {
      int i = 1;
      if (!this.enabled) {
         i = 0;
      } else if (mouseOver) {
         i = 2;
      }

      return i;
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      if (this.visible) {
         Minecraft minecraft = Minecraft.getInstance();
         FontRenderer fontrenderer = minecraft.fontRenderer;
         minecraft.getTextureManager().bindTexture(BUTTON_TEXTURES);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
         int i = this.getHoverState(this.hovered);
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
         this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
         this.renderBg(minecraft, mouseX, mouseY);
         int j = 14737632;
         if (packedFGColor != 0)
         {
            j = packedFGColor;
         }
         else
         if (!this.enabled) {
            j = 10526880;
         } else if (this.hovered) {
            j = 16777120;
         }

         this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
      }
   }

   /**
    * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
    */
   protected void renderBg(Minecraft mc, int mouseX, int mouseY) {
   }

   /**
    * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
    */
   public void onClick(double mouseX, double mouseY) {
      this.isDragging = true;
   }

   /**
    * Called when the left mouse button is released. This method is specific to GuiButton.
    */
   public void onRelease(double mouseX, double mouseY) {
      this.isDragging = false;
   }

   protected void onDrag(double mouseX, double mouseY, double mouseDX, double mouseDY) {
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (p_mouseClicked_5_ == 0) {
         boolean flag = this.isPressable(p_mouseClicked_1_, p_mouseClicked_3_);
         if (flag) {
            this.playPressSound(Minecraft.getInstance().getSoundHandler());
            this.onClick(p_mouseClicked_1_, p_mouseClicked_3_);
            return true;
         }
      }

      return false;
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      if (p_mouseReleased_5_ == 0) {
         this.onRelease(p_mouseReleased_1_, p_mouseReleased_3_);
         return true;
      } else {
         return false;
      }
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      if (p_mouseDragged_5_ == 0) {
         this.onDrag(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_6_, p_mouseDragged_8_);
         return true;
      } else {
         return false;
      }
   }

   /**
    * True if this button can be interacted with (enabled, visible, and with the mouse in the right place)
    */
   protected boolean isPressable(double mouseX, double mouseY) {
      return this.enabled && this.visible && mouseX >= (double)this.x && mouseY >= (double)this.y && mouseX < (double)(this.x + this.width) && mouseY < (double)(this.y + this.height);
   }

   /**
    * Whether the mouse cursor is currently over the button.
    */
   public boolean isMouseOver() {
      return this.hovered;
   }

   public void drawButtonForegroundLayer(int mouseX, int mouseY) {
   }

   public void playPressSound(SoundHandler soundHandlerIn) {
      soundHandlerIn.play(SimpleSound.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int width) {
      this.width = width;
   }
}