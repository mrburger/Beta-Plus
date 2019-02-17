package net.minecraft.client.gui;

import net.minecraft.client.GameSettings;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiControls extends GuiScreen {
   private static final GameSettings.Options[] OPTIONS_ARR = new GameSettings.Options[]{GameSettings.Options.INVERT_MOUSE, GameSettings.Options.SENSITIVITY, GameSettings.Options.TOUCHSCREEN, GameSettings.Options.AUTO_JUMP};
   /** A reference to the screen object that created this. Used for navigating between screens. */
   private final GuiScreen parentScreen;
   protected String screenTitle = "Controls";
   /** Reference to the GameSettings object. */
   private final GameSettings options;
   /** The ID of the button that has been pressed. */
   public KeyBinding buttonId;
   public long time;
   private GuiKeyBindingList keyBindingList;
   private GuiButton buttonReset;

   public GuiControls(GuiScreen screen, GameSettings settings) {
      this.parentScreen = screen;
      this.options = settings;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      this.keyBindingList = new GuiKeyBindingList(this, this.mc);
      this.children.add(this.keyBindingList);
      this.setFocused(this.keyBindingList);
      this.addButton(new GuiButton(200, this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.format("gui.done")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiControls.this.mc.displayGuiScreen(GuiControls.this.parentScreen);
         }
      });
      this.buttonReset = this.addButton(new GuiButton(201, this.width / 2 - 155, this.height - 29, 150, 20, I18n.format("controls.resetAll")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            for(KeyBinding keybinding : GuiControls.this.mc.gameSettings.keyBindings) {
               keybinding.setToDefault();
            }

            KeyBinding.resetKeyBindingArrayAndHash();
         }
      });
      this.screenTitle = I18n.format("controls.title");
      int i = 0;

      for(GameSettings.Options gamesettings$options : OPTIONS_ARR) {
         if (gamesettings$options.isFloat()) {
            this.addButton(new GuiOptionSlider(gamesettings$options.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), gamesettings$options));
         } else {
            this.addButton(new GuiOptionButton(gamesettings$options.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), gamesettings$options, this.options.getKeyBinding(gamesettings$options)) {
               /**
                * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
                */
               public void onClick(double mouseX, double mouseY) {
                  GuiControls.this.options.setOptionValue(this.getOption(), 1);
                  this.displayString = GuiControls.this.options.getKeyBinding(GameSettings.Options.byOrdinal(this.id));
               }
            });
         }

         ++i;
      }

   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.buttonId != null) {
         this.options.setKeyBindingCode(this.buttonId, InputMappings.Type.MOUSE.getOrMakeInput(p_mouseClicked_5_));
         this.buttonId = null;
         KeyBinding.resetKeyBindingArrayAndHash();
         return true;
      } else if (p_mouseClicked_5_ == 0 && this.keyBindingList.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         this.setDragging(true);
         this.setFocused(this.keyBindingList);
         return true;
      } else {
         return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      }
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      if (p_mouseReleased_5_ == 0 && this.keyBindingList.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)) {
         this.setDragging(false);
         return true;
      } else {
         return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
      }
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (this.buttonId != null) {
         if (p_keyPressed_1_ == 256) {
            this.buttonId.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), InputMappings.INPUT_INVALID);
            this.options.setKeyBindingCode(this.buttonId, InputMappings.INPUT_INVALID);
         } else {
            this.buttonId.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), InputMappings.getInputByCode(p_keyPressed_1_, p_keyPressed_2_));
            this.options.setKeyBindingCode(this.buttonId, InputMappings.getInputByCode(p_keyPressed_1_, p_keyPressed_2_));
         }

         if (!net.minecraftforge.client.settings.KeyModifier.isKeyCodeModifier(this.buttonId.getKey()))
         this.buttonId = null;
         this.time = Util.milliTime();
         KeyBinding.resetKeyBindingArrayAndHash();
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.keyBindingList.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 8, 16777215);
      boolean flag = false;

      for(KeyBinding keybinding : this.options.keyBindings) {
         if (!keybinding.func_197985_l()) {
            flag = true;
            break;
         }
      }

      this.buttonReset.enabled = flag;
      super.render(mouseX, mouseY, partialTicks);
   }
}