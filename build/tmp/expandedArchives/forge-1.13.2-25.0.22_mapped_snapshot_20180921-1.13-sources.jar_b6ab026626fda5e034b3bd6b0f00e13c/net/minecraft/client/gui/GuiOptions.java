package net.minecraft.client.gui;

import net.minecraft.client.GameSettings;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiOptions extends GuiScreen {
   private static final GameSettings.Options[] SCREEN_OPTIONS = new GameSettings.Options[]{GameSettings.Options.FOV};
   private final GuiScreen lastScreen;
   /** Reference to the GameSettings object. */
   private final GameSettings settings;
   private GuiButton difficultyButton;
   private GuiLockIconButton lockButton;
   protected String title = "Options";

   public GuiOptions(GuiScreen p_i1046_1_, GameSettings p_i1046_2_) {
      this.lastScreen = p_i1046_1_;
      this.settings = p_i1046_2_;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      this.title = I18n.format("options.title");
      int i = 0;

      for(GameSettings.Options gamesettings$options : SCREEN_OPTIONS) {
         if (gamesettings$options.isFloat()) {
            this.addButton(new GuiOptionSlider(gamesettings$options.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), gamesettings$options));
         } else {
            GuiOptionButton guioptionbutton = new GuiOptionButton(gamesettings$options.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), gamesettings$options, this.settings.getKeyBinding(gamesettings$options)) {
               /**
                * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
                */
               public void onClick(double mouseX, double mouseY) {
                  GuiOptions.this.settings.setOptionValue(this.getOption(), 1);
                  this.displayString = GuiOptions.this.settings.getKeyBinding(GameSettings.Options.byOrdinal(this.id));
               }
            };
            this.addButton(guioptionbutton);
         }

         ++i;
      }

      if (this.mc.world != null) {
         EnumDifficulty enumdifficulty = this.mc.world.getDifficulty();
         this.difficultyButton = new GuiButton(108, this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), 150, 20, this.getDifficultyText(enumdifficulty)) {
            /**
             * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
             */
            public void onClick(double mouseX, double mouseY) {
               GuiOptions.this.mc.world.getWorldInfo().setDifficulty(EnumDifficulty.byId(GuiOptions.this.mc.world.getDifficulty().getId() + 1));
               GuiOptions.this.difficultyButton.displayString = GuiOptions.this.getDifficultyText(GuiOptions.this.mc.world.getDifficulty());
            }
         };
         this.addButton(this.difficultyButton);
         if (this.mc.isSingleplayer() && !this.mc.world.getWorldInfo().isHardcore()) {
            this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
            this.lockButton = new GuiLockIconButton(109, this.difficultyButton.x + this.difficultyButton.getWidth(), this.difficultyButton.y) {
               /**
                * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
                */
               public void onClick(double mouseX, double mouseY) {
                  GuiOptions.this.mc.displayGuiScreen(new GuiYesNo(GuiOptions.this, (new TextComponentTranslation("difficulty.lock.title")).getFormattedText(), (new TextComponentTranslation("difficulty.lock.question", new TextComponentTranslation(GuiOptions.this.mc.world.getWorldInfo().getDifficulty().getTranslationKey()))).getFormattedText(), 109));
               }
            };
            this.addButton(this.lockButton);
            this.lockButton.setLocked(this.mc.world.getWorldInfo().isDifficultyLocked());
            this.lockButton.enabled = !this.lockButton.isLocked();
            this.difficultyButton.enabled = !this.lockButton.isLocked();
         } else {
            this.difficultyButton.enabled = false;
         }
      } else {
         this.addButton(new GuiOptionButton(GameSettings.Options.REALMS_NOTIFICATIONS.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), GameSettings.Options.REALMS_NOTIFICATIONS, this.settings.getKeyBinding(GameSettings.Options.REALMS_NOTIFICATIONS)) {
            /**
             * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
             */
            public void onClick(double mouseX, double mouseY) {
               GuiOptions.this.settings.setOptionValue(this.getOption(), 1);
               this.displayString = GuiOptions.this.settings.getKeyBinding(GameSettings.Options.byOrdinal(this.id));
            }
         });
      }

      this.addButton(new GuiButton(110, this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, I18n.format("options.skinCustomisation")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiOptions.this.mc.gameSettings.saveOptions();
            GuiOptions.this.mc.displayGuiScreen(new GuiCustomizeSkin(GuiOptions.this));
         }
      });
      this.addButton(new GuiButton(106, this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, I18n.format("options.sounds")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiOptions.this.mc.gameSettings.saveOptions();
            GuiOptions.this.mc.displayGuiScreen(new GuiScreenOptionsSounds(GuiOptions.this, GuiOptions.this.settings));
         }
      });
      this.addButton(new GuiButton(101, this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, I18n.format("options.video")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiOptions.this.mc.gameSettings.saveOptions();
            GuiOptions.this.mc.displayGuiScreen(new GuiVideoSettings(GuiOptions.this, GuiOptions.this.settings));
         }
      });
      this.addButton(new GuiButton(100, this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, I18n.format("options.controls")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiOptions.this.mc.gameSettings.saveOptions();
            GuiOptions.this.mc.displayGuiScreen(new GuiControls(GuiOptions.this, GuiOptions.this.settings));
         }
      });
      this.addButton(new GuiButton(102, this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, I18n.format("options.language")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiOptions.this.mc.gameSettings.saveOptions();
            GuiOptions.this.mc.displayGuiScreen(new GuiLanguage(GuiOptions.this, GuiOptions.this.settings, GuiOptions.this.mc.getLanguageManager()));
         }
      });
      this.addButton(new GuiButton(103, this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, I18n.format("options.chat.title")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiOptions.this.mc.gameSettings.saveOptions();
            GuiOptions.this.mc.displayGuiScreen(new ScreenChatOptions(GuiOptions.this, GuiOptions.this.settings));
         }
      });
      this.addButton(new GuiButton(105, this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, I18n.format("options.resourcepack")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiOptions.this.mc.gameSettings.saveOptions();
            GuiOptions.this.mc.displayGuiScreen(new GuiScreenResourcePacks(GuiOptions.this));
         }
      });
      this.addButton(new GuiButton(104, this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, I18n.format("options.snooper.view")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiOptions.this.mc.gameSettings.saveOptions();
            GuiOptions.this.mc.displayGuiScreen(new GuiSnooper(GuiOptions.this, GuiOptions.this.settings));
         }
      });
      this.addButton(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiOptions.this.mc.gameSettings.saveOptions();
            GuiOptions.this.mc.displayGuiScreen(GuiOptions.this.lastScreen);
         }
      });
   }

   public String getDifficultyText(EnumDifficulty p_175355_1_) {
      return (new TextComponentTranslation("options.difficulty")).appendText(": ").appendSibling(p_175355_1_.getDisplayName()).getFormattedText();
   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      this.mc.displayGuiScreen(this);
      if (p_confirmResult_2_ == 109 && p_confirmResult_1_ && this.mc.world != null) {
         this.mc.world.getWorldInfo().setDifficultyLocked(true);
         this.lockButton.setLocked(true);
         this.lockButton.enabled = false;
         this.difficultyButton.enabled = false;
      }

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
}