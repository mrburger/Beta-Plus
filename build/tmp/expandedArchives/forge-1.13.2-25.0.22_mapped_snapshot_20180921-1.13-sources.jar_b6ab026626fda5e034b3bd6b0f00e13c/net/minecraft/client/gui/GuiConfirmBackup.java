package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiConfirmBackup extends GuiScreen {
   private final GuiScreen parentScreen;
   protected GuiConfirmBackup.ICallback callback;
   protected String title;
   private final String message;
   /** The content of {@link #message}, word-wrapped */
   private final List<String> wrappedMessage = Lists.newArrayList();
   protected String confirmText;
   protected String skipBackupText;
   protected String cancelText;

   public GuiConfirmBackup(GuiScreen parent, GuiConfirmBackup.ICallback callback, String title, String message) {
      this.parentScreen = parent;
      this.callback = callback;
      this.title = title;
      this.message = message;
      this.confirmText = I18n.format("selectWorld.backupJoinConfirmButton");
      this.skipBackupText = I18n.format("selectWorld.backupJoinSkipButton");
      this.cancelText = I18n.format("gui.cancel");
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      super.initGui();
      this.wrappedMessage.clear();
      this.wrappedMessage.addAll(this.fontRenderer.listFormattedStringToWidth(this.message, this.width - 50));
      this.addButton(new GuiOptionButton(0, this.width / 2 - 155, 100 + (this.wrappedMessage.size() + 1) * this.fontRenderer.FONT_HEIGHT, this.confirmText) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiConfirmBackup.this.callback.proceed(true);
         }
      });
      this.addButton(new GuiOptionButton(1, this.width / 2 - 155 + 160, 100 + (this.wrappedMessage.size() + 1) * this.fontRenderer.FONT_HEIGHT, this.skipBackupText) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiConfirmBackup.this.callback.proceed(false);
         }
      });
      this.addButton(new GuiButton(1, this.width / 2 - 155 + 80, 124 + (this.wrappedMessage.size() + 1) * this.fontRenderer.FONT_HEIGHT, 150, 20, this.cancelText) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiConfirmBackup.this.mc.displayGuiScreen(GuiConfirmBackup.this.parentScreen);
         }
      });
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 70, 16777215);
      int i = 90;

      for(String s : this.wrappedMessage) {
         this.drawCenteredString(this.fontRenderer, s, this.width / 2, i, 16777215);
         i += this.fontRenderer.FONT_HEIGHT;
      }

      super.render(mouseX, mouseY, partialTicks);
   }

   /**
    * Called when escape is pressed in this gui.
    *  
    * @return true if the GUI is allowed to close from this press.
    */
   public boolean allowCloseWithEscape() {
      return false;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         this.mc.displayGuiScreen(this.parentScreen);
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public interface ICallback {
      void proceed(boolean p_proceed_1_);
   }
}