package net.minecraft.client.gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiDirtMessageScreen extends GuiScreen {
   private final String field_205029_a;

   public GuiDirtMessageScreen(String p_i48952_1_) {
      this.field_205029_a = p_i48952_1_;
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
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawBackground(0);
      this.drawCenteredString(this.fontRenderer, this.field_205029_a, this.width / 2, 70, 16777215);
      super.render(mouseX, mouseY, partialTicks);
   }
}