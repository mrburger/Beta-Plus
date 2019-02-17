package net.minecraft.client.gui;

import java.util.Objects;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiScreenWorking extends GuiScreen implements IProgressUpdate {
   private String title = "";
   private String stage = "";
   private int progress;
   private boolean doneWorking;

   /**
    * Called when escape is pressed in this gui.
    *  
    * @return true if the GUI is allowed to close from this press.
    */
   public boolean allowCloseWithEscape() {
      return false;
   }

   public void displaySavingString(ITextComponent component) {
      this.resetProgressAndMessage(component);
   }

   public void resetProgressAndMessage(ITextComponent component) {
      this.title = component.getFormattedText();
      this.displayLoadingString(new TextComponentTranslation("progress.working"));
   }

   public void displayLoadingString(ITextComponent component) {
      this.stage = component.getFormattedText();
      this.setLoadingProgress(0);
   }

   /**
    * Updates the progress bar on the loading screen to the specified amount.
    */
   public void setLoadingProgress(int progress) {
      this.progress = progress;
   }

   public void setDoneWorking() {
      this.doneWorking = true;
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      if (this.doneWorking) {
         if (!this.mc.isConnectedToRealms()) {
            this.mc.displayGuiScreen((GuiScreen)null);
         }

      } else {
         this.drawDefaultBackground();
         this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 70, 16777215);
         if (!Objects.equals(this.stage, "") && this.progress != 0) {
            this.drawCenteredString(this.fontRenderer, this.stage + " " + this.progress + "%", this.width / 2, 90, 16777215);
         }

         super.render(mouseX, mouseY, partialTicks);
      }
   }
}