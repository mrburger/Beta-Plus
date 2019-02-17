package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiShareToLan extends GuiScreen {
   private final GuiScreen lastScreen;
   private GuiButton allowCheatsButton;
   private GuiButton gameModeButton;
   private String gameMode = "survival";
   private boolean allowCheats;

   public GuiShareToLan(GuiScreen lastScreenIn) {
      this.lastScreen = lastScreenIn;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      this.addButton(new GuiButton(101, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("lanServer.start")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiShareToLan.this.mc.displayGuiScreen((GuiScreen)null);
            int i = HttpUtil.getSuitableLanPort();
            ITextComponent itextcomponent;
            if (GuiShareToLan.this.mc.getIntegratedServer().shareToLAN(GameType.getByName(GuiShareToLan.this.gameMode), GuiShareToLan.this.allowCheats, i)) {
               itextcomponent = new TextComponentTranslation("commands.publish.started", i);
            } else {
               itextcomponent = new TextComponentTranslation("commands.publish.failed");
            }

            GuiShareToLan.this.mc.ingameGUI.getChatGUI().printChatMessage(itextcomponent);
         }
      });
      this.addButton(new GuiButton(102, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiShareToLan.this.mc.displayGuiScreen(GuiShareToLan.this.lastScreen);
         }
      });
      this.gameModeButton = this.addButton(new GuiButton(104, this.width / 2 - 155, 100, 150, 20, I18n.format("selectWorld.gameMode")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            if ("spectator".equals(GuiShareToLan.this.gameMode)) {
               GuiShareToLan.this.gameMode = "creative";
            } else if ("creative".equals(GuiShareToLan.this.gameMode)) {
               GuiShareToLan.this.gameMode = "adventure";
            } else if ("adventure".equals(GuiShareToLan.this.gameMode)) {
               GuiShareToLan.this.gameMode = "survival";
            } else {
               GuiShareToLan.this.gameMode = "spectator";
            }

            GuiShareToLan.this.updateDisplayNames();
         }
      });
      this.allowCheatsButton = this.addButton(new GuiButton(103, this.width / 2 + 5, 100, 150, 20, I18n.format("selectWorld.allowCommands")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiShareToLan.this.allowCheats = !GuiShareToLan.this.allowCheats;
            GuiShareToLan.this.updateDisplayNames();
         }
      });
      this.updateDisplayNames();
   }

   private void updateDisplayNames() {
      this.gameModeButton.displayString = I18n.format("selectWorld.gameMode") + ": " + I18n.format("selectWorld.gameMode." + this.gameMode);
      this.allowCheatsButton.displayString = I18n.format("selectWorld.allowCommands") + " ";
      if (this.allowCheats) {
         this.allowCheatsButton.displayString = this.allowCheatsButton.displayString + I18n.format("options.on");
      } else {
         this.allowCheatsButton.displayString = this.allowCheatsButton.displayString + I18n.format("options.off");
      }

   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, I18n.format("lanServer.title"), this.width / 2, 50, 16777215);
      this.drawCenteredString(this.fontRenderer, I18n.format("lanServer.otherPlayers"), this.width / 2, 82, 16777215);
      super.render(mouseX, mouseY, partialTicks);
   }
}