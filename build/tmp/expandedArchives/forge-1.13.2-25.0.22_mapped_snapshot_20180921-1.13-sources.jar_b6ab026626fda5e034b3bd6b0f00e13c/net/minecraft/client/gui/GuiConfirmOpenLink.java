package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiConfirmOpenLink extends GuiYesNo {
   /** Text to warn players from opening unsafe links. */
   private final String openLinkWarning;
   /** Label for the Copy to Clipboard button. */
   private final String copyLinkButtonText;
   private final String linkText;
   private boolean showSecurityWarning = true;

   public GuiConfirmOpenLink(GuiYesNoCallback parentScreenIn, String linkTextIn, int parentButtonClickedIdIn, boolean trusted) {
      super(parentScreenIn, I18n.format(trusted ? "chat.link.confirmTrusted" : "chat.link.confirm"), linkTextIn, parentButtonClickedIdIn);
      this.confirmButtonText = I18n.format(trusted ? "chat.link.open" : "gui.yes");
      this.cancelButtonText = I18n.format(trusted ? "gui.cancel" : "gui.no");
      this.copyLinkButtonText = I18n.format("chat.copy");
      this.openLinkWarning = I18n.format("chat.link.warning");
      this.linkText = linkTextIn;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      super.initGui();
      this.buttons.clear();
      this.children.clear();
      this.addButton(new GuiButton(0, this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.confirmButtonText) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiConfirmOpenLink.this.parentScreen.confirmResult(true, GuiConfirmOpenLink.this.parentButtonClickedId);
         }
      });
      this.addButton(new GuiButton(2, this.width / 2 - 50, this.height / 6 + 96, 100, 20, this.copyLinkButtonText) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiConfirmOpenLink.this.copyLinkToClipboard();
            GuiConfirmOpenLink.this.parentScreen.confirmResult(false, GuiConfirmOpenLink.this.parentButtonClickedId);
         }
      });
      this.addButton(new GuiButton(1, this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, this.cancelButtonText) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiConfirmOpenLink.this.parentScreen.confirmResult(false, GuiConfirmOpenLink.this.parentButtonClickedId);
         }
      });
   }

   /**
    * Copies the link to the system clipboard.
    */
   public void copyLinkToClipboard() {
      this.mc.keyboardListener.setClipboardString(this.linkText);
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      super.render(mouseX, mouseY, partialTicks);
      if (this.showSecurityWarning) {
         this.drawCenteredString(this.fontRenderer, this.openLinkWarning, this.width / 2, 110, 16764108);
      }

   }

   public void disableSecurityWarning() {
      this.showSecurityWarning = false;
   }
}