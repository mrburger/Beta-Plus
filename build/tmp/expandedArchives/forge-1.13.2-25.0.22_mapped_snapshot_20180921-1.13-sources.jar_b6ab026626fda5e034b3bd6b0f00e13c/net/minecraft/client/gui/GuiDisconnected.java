package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiDisconnected extends GuiScreen {
   private final String reason;
   private final ITextComponent message;
   private List<String> multilineMessage;
   private final GuiScreen parentScreen;
   private int textHeight;

   public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, ITextComponent chatComp) {
      this.parentScreen = screen;
      this.reason = I18n.format(reasonLocalizationKey);
      this.message = chatComp;
   }

   /**
    * Called when escape is pressed in this gui.
    *  
    * @return true if the GUI is allowed to close from this press.
    */
   public boolean allowCloseWithEscape() {
      return false;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      this.multilineMessage = this.fontRenderer.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
      this.textHeight = this.multilineMessage.size() * this.fontRenderer.FONT_HEIGHT;
      this.addButton(new GuiButton(0, this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + this.fontRenderer.FONT_HEIGHT, this.height - 30), I18n.format("gui.toMenu")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiDisconnected.this.mc.displayGuiScreen(GuiDisconnected.this.parentScreen);
         }
      });
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.reason, this.width / 2, this.height / 2 - this.textHeight / 2 - this.fontRenderer.FONT_HEIGHT * 2, 11184810);
      int i = this.height / 2 - this.textHeight / 2;
      if (this.multilineMessage != null) {
         for(String s : this.multilineMessage) {
            this.drawCenteredString(this.fontRenderer, s, this.width / 2, i, 16777215);
            i += this.fontRenderer.FONT_HEIGHT;
         }
      }

      super.render(mouseX, mouseY, partialTicks);
   }
}