package net.minecraft.client.gui;

import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ArrayUtils;

@OnlyIn(Dist.CLIENT)
public class GuiKeyBindingList extends GuiListExtended<GuiKeyBindingList.Entry> {
   private final GuiControls controlsScreen;
   private final Minecraft mc;
   private int maxListLabelWidth;

   public GuiKeyBindingList(GuiControls controls, Minecraft mcIn) {
      super(mcIn, controls.width + 45, controls.height, 63, controls.height - 32, 20);
      this.controlsScreen = controls;
      this.mc = mcIn;
      KeyBinding[] akeybinding = ArrayUtils.clone(mcIn.gameSettings.keyBindings);
      Arrays.sort((Object[])akeybinding);
      String s = null;

      for(KeyBinding keybinding : akeybinding) {
         String s1 = keybinding.getKeyCategory();
         if (!s1.equals(s)) {
            s = s1;
            this.addEntry(new GuiKeyBindingList.CategoryEntry(s1));
         }

         int i = mcIn.fontRenderer.getStringWidth(I18n.format(keybinding.getKeyDescription()));
         if (i > this.maxListLabelWidth) {
            this.maxListLabelWidth = i;
         }

         this.addEntry(new GuiKeyBindingList.KeyEntry(keybinding));
      }

   }

   protected int getScrollBarX() {
      return super.getScrollBarX() + 35;
   }

   /**
    * Gets the width of the list
    */
   public int getListWidth() {
      return super.getListWidth() + 32;
   }

   @OnlyIn(Dist.CLIENT)
   public class CategoryEntry extends GuiKeyBindingList.Entry {
      private final String labelText;
      private final int labelWidth;

      public CategoryEntry(String name) {
         this.labelText = I18n.format(name);
         this.labelWidth = GuiKeyBindingList.this.mc.fontRenderer.getStringWidth(this.labelText);
      }

      public void drawEntry(int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
         GuiKeyBindingList.this.mc.fontRenderer.drawString(this.labelText, (float)(GuiKeyBindingList.this.mc.currentScreen.width / 2 - this.labelWidth / 2), (float)(this.getY() + entryHeight - GuiKeyBindingList.this.mc.fontRenderer.FONT_HEIGHT - 1), 16777215);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class Entry extends GuiListExtended.IGuiListEntry<GuiKeyBindingList.Entry> {
   }

   @OnlyIn(Dist.CLIENT)
   public class KeyEntry extends GuiKeyBindingList.Entry {
      /** The keybinding specified for this KeyEntry */
      private final KeyBinding keybinding;
      /** The localized key description for this KeyEntry */
      private final String keyDesc;
      private final GuiButton btnChangeKeyBinding;
      private final GuiButton btnReset;

      private KeyEntry(final KeyBinding name) {
         this.keybinding = name;
         this.keyDesc = I18n.format(name.getKeyDescription());
         this.btnChangeKeyBinding = new GuiButton(0, 0, 0, 95, 20, I18n.format(name.func_197978_k())) {
            /**
             * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
             */
            public void onClick(double mouseX, double mouseY) {
               GuiKeyBindingList.this.controlsScreen.buttonId = name;
            }
         };
         this.btnReset = new GuiButton(0, 0, 0, 50, 20, I18n.format("controls.reset")) {
            /**
             * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
             */
            public void onClick(double mouseX, double mouseY) {
               keybinding.setToDefault();
               GuiKeyBindingList.this.mc.gameSettings.setKeyBindingCode(name, name.getDefault());
               KeyBinding.resetKeyBindingArrayAndHash();
            }
         };
      }

      public void drawEntry(int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
         int i = this.getY();
         int j = this.getX();
         boolean flag = GuiKeyBindingList.this.controlsScreen.buttonId == this.keybinding;
         GuiKeyBindingList.this.mc.fontRenderer.drawString(this.keyDesc, (float)(j + 90 - GuiKeyBindingList.this.maxListLabelWidth), (float)(i + entryHeight / 2 - GuiKeyBindingList.this.mc.fontRenderer.FONT_HEIGHT / 2), 16777215);
         this.btnReset.x = j + 210;
         this.btnReset.y = i;
         this.btnReset.enabled = !this.keybinding.func_197985_l();
         this.btnReset.render(mouseX, mouseY, partialTicks);
         this.btnChangeKeyBinding.x = j + 105;
         this.btnChangeKeyBinding.y = i;
         this.btnChangeKeyBinding.displayString = this.keybinding.func_197978_k();
         boolean flag1 = false;
         boolean keyCodeModifierConflict = true; // less severe form of conflict, like SHIFT conflicting with SHIFT+G
         if (!this.keybinding.isInvalid()) {
            for(KeyBinding keybinding : GuiKeyBindingList.this.mc.gameSettings.keyBindings) {
               if (keybinding != this.keybinding && this.keybinding.func_197983_b(keybinding)) {
                  flag1 = true;
                  keyCodeModifierConflict &= keybinding.hasKeyCodeModifierConflict(this.keybinding);
               }
            }
         }

         if (flag) {
            this.btnChangeKeyBinding.displayString = TextFormatting.WHITE + "> " + TextFormatting.YELLOW + this.btnChangeKeyBinding.displayString + TextFormatting.WHITE + " <";
         } else if (flag1) {
            this.btnChangeKeyBinding.displayString = (keyCodeModifierConflict ? TextFormatting.GOLD : TextFormatting.RED) + this.btnChangeKeyBinding.displayString;
         }

         this.btnChangeKeyBinding.render(mouseX, mouseY, partialTicks);
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         if (this.btnChangeKeyBinding.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
            return true;
         } else {
            return this.btnReset.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
         }
      }

      public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
         return this.btnChangeKeyBinding.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_) || this.btnReset.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
      }
   }
}