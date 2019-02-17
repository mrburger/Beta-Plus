package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GuiWorldSelection extends GuiScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   /** The screen to return to when this closes (always Main Menu). */
   protected GuiScreen prevScreen;
   protected String title = "Select world";
   /** Tooltip displayed a world whose version is different from this client's */
   private String worldVersTooltip;
   private GuiButton deleteButton;
   private GuiButton selectButton;
   private GuiButton renameButton;
   private GuiButton copyButton;
   protected GuiTextField field_212352_g;
   private GuiListWorldSelection selectionList;

   public GuiWorldSelection(GuiScreen screenIn) {
      this.prevScreen = screenIn;
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_) {
      return this.selectionList.mouseScrolled(p_mouseScrolled_1_);
   }

   /**
    * Called from the main game loop to update the screen.
    */
   public void tick() {
      this.field_212352_g.tick();
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      this.mc.keyboardListener.enableRepeatEvents(true);
      this.title = I18n.format("selectWorld.title");
      this.field_212352_g = new GuiTextField(0, this.fontRenderer, this.width / 2 - 100, 22, 200, 20, this.field_212352_g) {
         /**
          * Sets focus to this gui element
          */
         public void setFocused(boolean isFocusedIn) {
            super.setFocused(true);
         }
      };
      this.field_212352_g.setTextAcceptHandler((p_212350_1_, p_212350_2_) -> {
         this.selectionList.func_212330_a(() -> {
            return p_212350_2_;
         }, false);
      });
      this.selectionList = new GuiListWorldSelection(this, this.mc, this.width, this.height, 48, this.height - 64, 36, () -> {
         return this.field_212352_g.getText();
      }, this.selectionList);
      this.selectButton = this.addButton(new GuiButton(1, this.width / 2 - 154, this.height - 52, 150, 20, I18n.format("selectWorld.select")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiListWorldSelectionEntry guilistworldselectionentry = GuiWorldSelection.this.selectionList.getSelectedWorld();
            if (guilistworldselectionentry != null) {
               guilistworldselectionentry.joinWorld();
            }

         }
      });
      this.addButton(new GuiButton(3, this.width / 2 + 4, this.height - 52, 150, 20, I18n.format("selectWorld.create")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiWorldSelection.this.mc.displayGuiScreen(new GuiCreateWorld(GuiWorldSelection.this));
         }
      });
      this.renameButton = this.addButton(new GuiButton(4, this.width / 2 - 154, this.height - 28, 72, 20, I18n.format("selectWorld.edit")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiListWorldSelectionEntry guilistworldselectionentry = GuiWorldSelection.this.selectionList.getSelectedWorld();
            if (guilistworldselectionentry != null) {
               guilistworldselectionentry.editWorld();
            }

         }
      });
      this.deleteButton = this.addButton(new GuiButton(2, this.width / 2 - 76, this.height - 28, 72, 20, I18n.format("selectWorld.delete")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiListWorldSelectionEntry guilistworldselectionentry = GuiWorldSelection.this.selectionList.getSelectedWorld();
            if (guilistworldselectionentry != null) {
               guilistworldselectionentry.deleteWorld();
            }

         }
      });
      this.copyButton = this.addButton(new GuiButton(5, this.width / 2 + 4, this.height - 28, 72, 20, I18n.format("selectWorld.recreate")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiListWorldSelectionEntry guilistworldselectionentry = GuiWorldSelection.this.selectionList.getSelectedWorld();
            if (guilistworldselectionentry != null) {
               guilistworldselectionentry.recreateWorld();
            }

         }
      });
      this.addButton(new GuiButton(0, this.width / 2 + 82, this.height - 28, 72, 20, I18n.format("gui.cancel")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiWorldSelection.this.mc.displayGuiScreen(GuiWorldSelection.this.prevScreen);
         }
      });
      this.selectButton.enabled = false;
      this.deleteButton.enabled = false;
      this.renameButton.enabled = false;
      this.copyButton.enabled = false;
      this.children.add(this.field_212352_g);
      this.children.add(this.selectionList);
      this.field_212352_g.setFocused(true);
      this.field_212352_g.setCanLoseFocus(false);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) ? true : this.field_212352_g.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      return this.field_212352_g.charTyped(p_charTyped_1_, p_charTyped_2_);
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.worldVersTooltip = null;
      this.selectionList.drawScreen(mouseX, mouseY, partialTicks);
      this.field_212352_g.drawTextField(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 8, 16777215);
      super.render(mouseX, mouseY, partialTicks);
      if (this.worldVersTooltip != null) {
         this.drawHoveringText(Lists.newArrayList(Splitter.on("\n").split(this.worldVersTooltip)), mouseX, mouseY);
      }

   }

   /**
    * Called back by selectionList when we call its drawScreen method, from ours.
    */
   public void setVersionTooltip(String p_184861_1_) {
      this.worldVersTooltip = p_184861_1_;
   }

   public void selectWorld(@Nullable GuiListWorldSelectionEntry entry) {
      boolean flag = entry != null;
      this.selectButton.enabled = flag;
      this.deleteButton.enabled = flag;
      this.renameButton.enabled = flag;
      this.copyButton.enabled = flag;
   }

   /**
    * Called when the screen is unloaded. Used to disable keyboard repeat events
    */
   public void onGuiClosed() {
      if (this.selectionList != null) {
         this.selectionList.getChildren().forEach(GuiListWorldSelectionEntry::close);
      }

   }
}