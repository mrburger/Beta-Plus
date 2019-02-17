package net.minecraft.client.gui;

import net.minecraft.client.GameSettings;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiScreenDemo extends GuiScreen {
   private static final ResourceLocation DEMO_BACKGROUND_LOCATION = new ResourceLocation("textures/gui/demo_background.png");

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      int i = -16;
      this.addButton(new GuiButton(1, this.width / 2 - 116, this.height / 2 + 62 + -16, 114, 20, I18n.format("demo.help.buy")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            this.enabled = false;
            Util.getOSType().openURI("http://www.minecraft.net/store?source=demo");
         }
      });
      this.addButton(new GuiButton(2, this.width / 2 + 2, this.height / 2 + 62 + -16, 114, 20, I18n.format("demo.help.later")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiScreenDemo.this.mc.displayGuiScreen((GuiScreen)null);
            GuiScreenDemo.this.mc.mouseHelper.grabMouse();
         }
      });
   }

   /**
    * Draws either a gradient over the background world (if there is a world), or a dirt screen if there is no world.
    *  
    * This method should usually be called before doing any other rendering; otherwise weird results will occur if there
    * is no world, and the world will not be tinted if there is.
    *  
    * Do not call after having already done other rendering, as it will draw over it.
    */
   public void drawDefaultBackground() {
      super.drawDefaultBackground();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(DEMO_BACKGROUND_LOCATION);
      int i = (this.width - 248) / 2;
      int j = (this.height - 166) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, 248, 166);
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      int i = (this.width - 248) / 2 + 10;
      int j = (this.height - 166) / 2 + 8;
      this.fontRenderer.drawString(I18n.format("demo.help.title"), (float)i, (float)j, 2039583);
      j = j + 12;
      GameSettings gamesettings = this.mc.gameSettings;
      this.fontRenderer.drawString(I18n.format("demo.help.movementShort", gamesettings.keyBindForward.func_197978_k(), gamesettings.keyBindLeft.func_197978_k(), gamesettings.keyBindBack.func_197978_k(), gamesettings.keyBindRight.func_197978_k()), (float)i, (float)j, 5197647);
      this.fontRenderer.drawString(I18n.format("demo.help.movementMouse"), (float)i, (float)(j + 12), 5197647);
      this.fontRenderer.drawString(I18n.format("demo.help.jump", gamesettings.keyBindJump.func_197978_k()), (float)i, (float)(j + 24), 5197647);
      this.fontRenderer.drawString(I18n.format("demo.help.inventory", gamesettings.keyBindInventory.func_197978_k()), (float)i, (float)(j + 36), 5197647);
      this.fontRenderer.drawSplitString(I18n.format("demo.help.fullWrapped"), i, j + 68, 218, 2039583);
      super.render(mouseX, mouseY, partialTicks);
   }
}