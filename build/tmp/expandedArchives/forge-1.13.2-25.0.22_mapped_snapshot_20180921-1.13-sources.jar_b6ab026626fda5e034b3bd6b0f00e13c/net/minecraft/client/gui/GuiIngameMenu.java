package net.minecraft.client.gui;

import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.gui.advancements.GuiScreenAdvancements;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiIngameMenu extends GuiScreen {
   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      int i = -16;
      int j = 98;
      GuiButton guibutton = this.addButton(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + -16, I18n.format("menu.returnToMenu")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            boolean flag = GuiIngameMenu.this.mc.isIntegratedServerRunning();
            boolean flag1 = GuiIngameMenu.this.mc.isConnectedToRealms();
            this.enabled = false;
            GuiIngameMenu.this.mc.world.sendQuittingDisconnectingPacket();
            if (flag) {
               GuiIngameMenu.this.mc.loadWorld((WorldClient)null, new GuiDirtMessageScreen(I18n.format("menu.savingLevel")));
            } else {
               GuiIngameMenu.this.mc.loadWorld((WorldClient)null);
            }

            if (flag) {
               GuiIngameMenu.this.mc.displayGuiScreen(new GuiMainMenu());
            } else if (flag1) {
               RealmsBridge realmsbridge = new RealmsBridge();
               realmsbridge.switchToRealms(new GuiMainMenu());
            } else {
               GuiIngameMenu.this.mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
            }

         }
      });
      if (!this.mc.isIntegratedServerRunning()) {
         guibutton.displayString = I18n.format("menu.disconnect");
      }

      this.addButton(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 24 + -16, I18n.format("menu.returnToGame")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiIngameMenu.this.mc.displayGuiScreen((GuiScreen)null);
            GuiIngameMenu.this.mc.mouseHelper.grabMouse();
         }
      });
      this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + -16, 98, 20, I18n.format("menu.options")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiIngameMenu.this.mc.displayGuiScreen(new GuiOptions(GuiIngameMenu.this, GuiIngameMenu.this.mc.gameSettings));
         }
      });
      GuiButton guibutton1 = this.addButton(new GuiButton(7, this.width / 2 + 2, this.height / 4 + 96 + -16, 98, 20, I18n.format("menu.shareToLan")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiIngameMenu.this.mc.displayGuiScreen(new GuiShareToLan(GuiIngameMenu.this));
         }
      });
      guibutton1.enabled = this.mc.isSingleplayer() && !this.mc.getIntegratedServer().getPublic();
      this.addButton(new GuiButton(5, this.width / 2 - 100, this.height / 4 + 48 + -16, 98, 20, I18n.format("gui.advancements")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiIngameMenu.this.mc.displayGuiScreen(new GuiScreenAdvancements(GuiIngameMenu.this.mc.player.connection.getAdvancementManager()));
         }
      });
      this.addButton(new GuiButton(6, this.width / 2 + 2, this.height / 4 + 48 + -16, 98, 20, I18n.format("gui.stats")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiIngameMenu.this.mc.displayGuiScreen(new GuiStats(GuiIngameMenu.this, GuiIngameMenu.this.mc.player.getStats()));
         }
      });
   }

   /**
    * Called from the main game loop to update the screen.
    */
   public void tick() {
      super.tick();
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, I18n.format("menu.game"), this.width / 2, 40, 16777215);
      super.render(mouseX, mouseY, partialTicks);
   }
}