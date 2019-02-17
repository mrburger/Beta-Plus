package net.minecraft.client.gui;

import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiVideoSettings extends GuiScreen {
   private final GuiScreen parentGuiScreen;
   protected String screenTitle = "Video Settings";
   private final GameSettings guiGameSettings;
   private GuiOptionsRowList optionsRowList;
   /** An array of all of GameSettings.Options's video options. */
   private static final GameSettings.Options[] VIDEO_OPTIONS = new GameSettings.Options[]{GameSettings.Options.GRAPHICS, GameSettings.Options.RENDER_DISTANCE, GameSettings.Options.AMBIENT_OCCLUSION, GameSettings.Options.FRAMERATE_LIMIT, GameSettings.Options.ENABLE_VSYNC, GameSettings.Options.VIEW_BOBBING, GameSettings.Options.GUI_SCALE, GameSettings.Options.ATTACK_INDICATOR, GameSettings.Options.GAMMA, GameSettings.Options.RENDER_CLOUDS, GameSettings.Options.USE_FULLSCREEN, GameSettings.Options.PARTICLES, GameSettings.Options.MIPMAP_LEVELS, GameSettings.Options.USE_VBO, GameSettings.Options.ENTITY_SHADOWS, GameSettings.Options.BIOME_BLEND_RADIUS};

   public GuiVideoSettings(GuiScreen parentScreenIn, GameSettings gameSettingsIn) {
      this.parentGuiScreen = parentScreenIn;
      this.guiGameSettings = gameSettingsIn;
   }

   @Nullable
   public IGuiEventListener getFocused() {
      return this.optionsRowList;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      this.screenTitle = I18n.format("options.videoTitle");
      this.addButton(new GuiButton(200, this.width / 2 - 100, this.height - 27, I18n.format("gui.done")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiVideoSettings.this.mc.gameSettings.saveOptions();
            GuiVideoSettings.this.mc.mainWindow.update();
            GuiVideoSettings.this.mc.displayGuiScreen(GuiVideoSettings.this.parentGuiScreen);
         }
      });
      if (OpenGlHelper.vboSupported) {
         this.optionsRowList = new GuiOptionsRowList(this.mc, this.width, this.height, 32, this.height - 32, 25, VIDEO_OPTIONS);
      } else {
         GameSettings.Options[] agamesettings$options = new GameSettings.Options[VIDEO_OPTIONS.length - 1];
         int i = 0;

         for(GameSettings.Options gamesettings$options : VIDEO_OPTIONS) {
            if (gamesettings$options == GameSettings.Options.USE_VBO) {
               break;
            }

            agamesettings$options[i] = gamesettings$options;
            ++i;
         }

         this.optionsRowList = new GuiOptionsRowList(this.mc, this.width, this.height, 32, this.height - 32, 25, agamesettings$options);
      }

      this.children.add(this.optionsRowList);
   }

   public void close() {
      this.mc.gameSettings.saveOptions();
      super.close();
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      int i = this.guiGameSettings.guiScale;
      if (super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         if (this.guiGameSettings.guiScale != i) {
            this.mc.mainWindow.updateSize();
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      int i = this.guiGameSettings.guiScale;
      if (super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)) {
         return true;
      } else if (this.optionsRowList.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)) {
         if (this.guiGameSettings.guiScale != i) {
            this.mc.mainWindow.updateSize();
         }

         return true;
      } else {
         return false;
      }
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.optionsRowList.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 5, 16777215);
      super.render(mouseX, mouseY, partialTicks);
   }

   /**
    * Called when the screen is unloaded. Used to disable keyboard repeat events
    */
   @Override // FORGE: fix for MC-64581 very laggy mipmap slider
   public void onGuiClosed() {
      super.onGuiClosed();
      this.mc.gameSettings.onGuiClosed();
   }
}