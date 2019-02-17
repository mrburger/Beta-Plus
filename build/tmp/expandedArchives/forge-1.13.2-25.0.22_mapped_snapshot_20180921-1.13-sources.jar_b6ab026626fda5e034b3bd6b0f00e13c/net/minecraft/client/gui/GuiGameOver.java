package net.minecraft.client.gui;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiGameOver extends GuiScreen {
   /** The integer value containing the number of ticks that have passed since the player's death */
   private int enableButtonsTimer;
   private final ITextComponent causeOfDeath;

   public GuiGameOver(@Nullable ITextComponent causeOfDeathIn) {
      this.causeOfDeath = causeOfDeathIn;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      this.enableButtonsTimer = 0;
      String s;
      String s1;
      if (this.mc.world.getWorldInfo().isHardcore()) {
         s = I18n.format("deathScreen.spectate");
         s1 = I18n.format("deathScreen." + (this.mc.isIntegratedServerRunning() ? "deleteWorld" : "leaveServer"));
      } else {
         s = I18n.format("deathScreen.respawn");
         s1 = I18n.format("deathScreen.titleScreen");
      }

      this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 72, s) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiGameOver.this.mc.player.respawnPlayer();
            GuiGameOver.this.mc.displayGuiScreen((GuiScreen)null);
         }
      });
      GuiButton guibutton = this.addButton(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96, s1) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            if (GuiGameOver.this.mc.world.getWorldInfo().isHardcore()) {
               GuiGameOver.this.mc.displayGuiScreen(new GuiMainMenu());
            } else {
               GuiYesNo guiyesno = new GuiYesNo(GuiGameOver.this, I18n.format("deathScreen.quit.confirm"), "", I18n.format("deathScreen.titleScreen"), I18n.format("deathScreen.respawn"), 0);
               GuiGameOver.this.mc.displayGuiScreen(guiyesno);
               guiyesno.setButtonDelay(20);
            }
         }
      });
      if (!this.mc.world.getWorldInfo().isHardcore() && this.mc.getSession() == null) {
         guibutton.enabled = false;
      }

      for(GuiButton guibutton1 : this.buttons) {
         guibutton1.enabled = false;
      }

   }

   /**
    * Called when escape is pressed in this gui.
    *  
    * @return true if the GUI is allowed to close from this press.
    */
   public boolean allowCloseWithEscape() {
      return false;
   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      if (p_confirmResult_2_ == 31102009) {
         super.confirmResult(p_confirmResult_1_, p_confirmResult_2_);
      } else if (p_confirmResult_1_) {
         if (this.mc.world != null) {
            this.mc.world.sendQuittingDisconnectingPacket();
         }

         this.mc.loadWorld((WorldClient)null, new GuiDirtMessageScreen(I18n.format("menu.savingLevel")));
         this.mc.displayGuiScreen(new GuiMainMenu());
      } else {
         this.mc.player.respawnPlayer();
         this.mc.displayGuiScreen((GuiScreen)null);
      }

   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      boolean flag = this.mc.world.getWorldInfo().isHardcore();
      this.drawGradientRect(0, 0, this.width, this.height, 1615855616, -1602211792);
      GlStateManager.pushMatrix();
      GlStateManager.scalef(2.0F, 2.0F, 2.0F);
      this.drawCenteredString(this.fontRenderer, I18n.format(flag ? "deathScreen.title.hardcore" : "deathScreen.title"), this.width / 2 / 2, 30, 16777215);
      GlStateManager.popMatrix();
      if (this.causeOfDeath != null) {
         this.drawCenteredString(this.fontRenderer, this.causeOfDeath.getFormattedText(), this.width / 2, 85, 16777215);
      }

      this.drawCenteredString(this.fontRenderer, I18n.format("deathScreen.score") + ": " + TextFormatting.YELLOW + this.mc.player.getScore(), this.width / 2, 100, 16777215);
      if (this.causeOfDeath != null && mouseY > 85 && mouseY < 85 + this.fontRenderer.FONT_HEIGHT) {
         ITextComponent itextcomponent = this.getClickedComponentAt(mouseX);
         if (itextcomponent != null && itextcomponent.getStyle().getHoverEvent() != null) {
            this.handleComponentHover(itextcomponent, mouseX, mouseY);
         }
      }

      super.render(mouseX, mouseY, partialTicks);
   }

   @Nullable
   public ITextComponent getClickedComponentAt(int p_184870_1_) {
      if (this.causeOfDeath == null) {
         return null;
      } else {
         int i = this.mc.fontRenderer.getStringWidth(this.causeOfDeath.getFormattedText());
         int j = this.width / 2 - i / 2;
         int k = this.width / 2 + i / 2;
         int l = j;
         if (p_184870_1_ >= j && p_184870_1_ <= k) {
            for(ITextComponent itextcomponent : this.causeOfDeath) {
               l += this.mc.fontRenderer.getStringWidth(GuiUtilRenderComponents.removeTextColorsIfConfigured(itextcomponent.getUnformattedComponentText(), false));
               if (l > p_184870_1_) {
                  return itextcomponent;
               }
            }

            return null;
         } else {
            return null;
         }
      }
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.causeOfDeath != null && p_mouseClicked_3_ > 85.0D && p_mouseClicked_3_ < (double)(85 + this.fontRenderer.FONT_HEIGHT)) {
         ITextComponent itextcomponent = this.getClickedComponentAt((int)p_mouseClicked_1_);
         if (itextcomponent != null && itextcomponent.getStyle().getClickEvent() != null && itextcomponent.getStyle().getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
            this.handleComponentClick(itextcomponent);
            return false;
         }
      }

      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   /**
    * Returns true if this GUI should pause the game when it is displayed in single-player
    */
   public boolean doesGuiPauseGame() {
      return false;
   }

   /**
    * Called from the main game loop to update the screen.
    */
   public void tick() {
      super.tick();
      ++this.enableButtonsTimer;
      if (this.enableButtonsTimer == 20) {
         for(GuiButton guibutton : this.buttons) {
            guibutton.enabled = true;
         }
      }

   }
}