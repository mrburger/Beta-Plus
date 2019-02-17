package net.minecraft.client.gui;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiOptionSlider extends GuiButton {
   private double sliderValue = 1.0D;
   public boolean dragging;
   private final GameSettings.Options options;
   private final double minValue;
   private final double maxValue;

   public GuiOptionSlider(int buttonId, int x, int y, GameSettings.Options optionIn) {
      this(buttonId, x, y, optionIn, 0.0D, 1.0D);
   }

   public GuiOptionSlider(int p_i47662_1_, int p_i47662_2_, int p_i47662_3_, GameSettings.Options p_i47662_4_, double p_i47662_5_, double p_i47662_7_) {
      this(p_i47662_1_, p_i47662_2_, p_i47662_3_, 150, 20, p_i47662_4_, p_i47662_5_, p_i47662_7_);
   }

   public GuiOptionSlider(int p_i47663_1_, int p_i47663_2_, int p_i47663_3_, int p_i47663_4_, int p_i47663_5_, GameSettings.Options p_i47663_6_, double p_i47663_7_, double p_i47663_9_) {
      super(p_i47663_1_, p_i47663_2_, p_i47663_3_, p_i47663_4_, p_i47663_5_, "");
      this.options = p_i47663_6_;
      this.minValue = p_i47663_7_;
      this.maxValue = p_i47663_9_;
      Minecraft minecraft = Minecraft.getInstance();
      this.sliderValue = p_i47663_6_.normalizeValue(minecraft.gameSettings.getOptionFloatValue(p_i47663_6_));
      this.displayString = minecraft.gameSettings.getKeyBinding(p_i47663_6_);
   }

   /**
    * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
    * this button.
    */
   protected int getHoverState(boolean mouseOver) {
      return 0;
   }

   /**
    * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
    */
   protected void renderBg(Minecraft mc, int mouseX, int mouseY) {
      if (this.visible) {
         if (this.dragging) {
            this.sliderValue = (double)((float)(mouseX - (this.x + 4)) / (float)(this.width - 8));
            this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0D, 1.0D);
         }

         if (this.dragging || this.options == GameSettings.Options.FULLSCREEN_RESOLUTION) {
            double d0 = this.options.denormalizeValue(this.sliderValue);
            mc.gameSettings.setOptionFloatValue(this.options, d0);
            this.sliderValue = this.options.normalizeValue(d0);
            this.displayString = mc.gameSettings.getKeyBinding(this.options);
         }

         mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.drawTexturedModalRect(this.x + (int)(this.sliderValue * (double)(this.width - 8)), this.y, 0, 66, 4, 20);
         this.drawTexturedModalRect(this.x + (int)(this.sliderValue * (double)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
      }
   }

   /**
    * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
    */
   public final void onClick(double mouseX, double mouseY) {
      this.sliderValue = (mouseX - (double)(this.x + 4)) / (double)(this.width - 8);
      this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0D, 1.0D);
      Minecraft minecraft = Minecraft.getInstance();
      minecraft.gameSettings.setOptionFloatValue(this.options, this.options.denormalizeValue(this.sliderValue));
      this.displayString = minecraft.gameSettings.getKeyBinding(this.options);
      this.dragging = true;
   }

   /**
    * Called when the left mouse button is released. This method is specific to GuiButton.
    */
   public void onRelease(double mouseX, double mouseY) {
      this.dragging = false;
   }
}