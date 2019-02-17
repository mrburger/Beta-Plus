package net.minecraft.client.gui;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiScreenOptionsSounds extends GuiScreen {
   private final GuiScreen parent;
   /** Reference to the GameSettings object. */
   private final GameSettings game_settings_4;
   protected String title = "Options";
   private String offDisplayString;

   public GuiScreenOptionsSounds(GuiScreen parentIn, GameSettings settingsIn) {
      this.parent = parentIn;
      this.game_settings_4 = settingsIn;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      this.title = I18n.format("options.sounds.title");
      this.offDisplayString = I18n.format("options.off");
      int i = 0;
      this.addButton(new GuiScreenOptionsSounds.Button(SoundCategory.MASTER.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), SoundCategory.MASTER, true));
      i = i + 2;

      for(SoundCategory soundcategory : SoundCategory.values()) {
         if (soundcategory != SoundCategory.MASTER) {
            this.addButton(new GuiScreenOptionsSounds.Button(soundcategory.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), soundcategory, false));
            ++i;
         }
      }

      int j = this.width / 2 - 75;
      int k = this.height / 6 - 12;
      ++i;
      this.addButton(new GuiOptionButton(201, j, k + 24 * (i >> 1), GameSettings.Options.SHOW_SUBTITLES, this.game_settings_4.getKeyBinding(GameSettings.Options.SHOW_SUBTITLES)) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiScreenOptionsSounds.this.mc.gameSettings.setOptionValue(GameSettings.Options.SHOW_SUBTITLES, 1);
            this.displayString = GuiScreenOptionsSounds.this.mc.gameSettings.getKeyBinding(GameSettings.Options.SHOW_SUBTITLES);
            GuiScreenOptionsSounds.this.mc.gameSettings.saveOptions();
         }
      });
      this.addButton(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiScreenOptionsSounds.this.mc.gameSettings.saveOptions();
            GuiScreenOptionsSounds.this.mc.displayGuiScreen(GuiScreenOptionsSounds.this.parent);
         }
      });
   }

   public void close() {
      this.mc.gameSettings.saveOptions();
      super.close();
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 15, 16777215);
      super.render(mouseX, mouseY, partialTicks);
   }

   protected String getDisplayString(SoundCategory category) {
      float f = this.game_settings_4.getSoundLevel(category);
      return f == 0.0F ? this.offDisplayString : (int)(f * 100.0F) + "%";
   }

   @OnlyIn(Dist.CLIENT)
   class Button extends GuiButton {
      private final SoundCategory category;
      private final String categoryName;
      public double volume;
      public boolean pressed;

      public Button(int buttonId, int x, int y, SoundCategory categoryIn, boolean master) {
         super(buttonId, x, y, master ? 310 : 150, 20, "");
         this.category = categoryIn;
         this.categoryName = I18n.format("soundCategory." + categoryIn.getName());
         this.displayString = this.categoryName + ": " + GuiScreenOptionsSounds.this.getDisplayString(categoryIn);
         this.volume = (double)GuiScreenOptionsSounds.this.game_settings_4.getSoundLevel(categoryIn);
      }

      /**
       * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering
       * over this button.
       */
      protected int getHoverState(boolean mouseOver) {
         return 0;
      }

      /**
       * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
       */
      protected void renderBg(Minecraft mc, int mouseX, int mouseY) {
         if (this.visible) {
            if (this.pressed) {
               this.volume = (double)((float)(mouseX - (this.x + 4)) / (float)(this.width - 8));
               this.volume = MathHelper.clamp(this.volume, 0.0D, 1.0D);
               mc.gameSettings.setSoundLevel(this.category, (float)this.volume);
               mc.gameSettings.saveOptions();
               this.displayString = this.categoryName + ": " + GuiScreenOptionsSounds.this.getDisplayString(this.category);
            }

            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.x + (int)(this.volume * (double)(this.width - 8)), this.y, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.x + (int)(this.volume * (double)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
         }
      }

      /**
       * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
       */
      public void onClick(double mouseX, double mouseY) {
         this.volume = (mouseX - (double)(this.x + 4)) / (double)(this.width - 8);
         this.volume = MathHelper.clamp(this.volume, 0.0D, 1.0D);
         GuiScreenOptionsSounds.this.mc.gameSettings.setSoundLevel(this.category, (float)this.volume);
         GuiScreenOptionsSounds.this.mc.gameSettings.saveOptions();
         this.displayString = this.categoryName + ": " + GuiScreenOptionsSounds.this.getDisplayString(this.category);
         this.pressed = true;
      }

      public void playPressSound(SoundHandler soundHandlerIn) {
      }

      /**
       * Called when the left mouse button is released. This method is specific to GuiButton.
       */
      public void onRelease(double mouseX, double mouseY) {
         if (this.pressed) {
            GuiScreenOptionsSounds.this.mc.getSoundHandler().play(SimpleSound.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         }

         this.pressed = false;
      }
   }
}