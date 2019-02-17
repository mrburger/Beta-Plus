package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.client.GameSettings;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiSnooper extends GuiScreen {
   private final GuiScreen lastScreen;
   /** Reference to the GameSettings object. */
   private final GameSettings game_settings_2;
   private final java.util.List<String> keys = Lists.newArrayList();
   private final java.util.List<String> values = Lists.newArrayList();
   private String title;
   private String[] desc;
   private GuiSnooper.List list;
   private GuiButton toggleButton;

   public GuiSnooper(GuiScreen p_i1061_1_, GameSettings p_i1061_2_) {
      this.lastScreen = p_i1061_1_;
      this.game_settings_2 = p_i1061_2_;
   }

   public IGuiEventListener getFocused() {
      return this.list;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      this.title = I18n.format("options.snooper.title");
      String s = I18n.format("options.snooper.desc");
      java.util.List<String> list = Lists.newArrayList();

      for(String s1 : this.fontRenderer.listFormattedStringToWidth(s, this.width - 30)) {
         list.add(s1);
      }

      this.desc = list.toArray(new String[list.size()]);
      this.keys.clear();
      this.values.clear();
      GuiButton guibutton = new GuiButton(1, this.width / 2 - 152, this.height - 30, 150, 20, this.game_settings_2.getKeyBinding(GameSettings.Options.SNOOPER_ENABLED)) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiSnooper.this.game_settings_2.setOptionValue(GameSettings.Options.SNOOPER_ENABLED, 1);
            GuiSnooper.this.toggleButton.displayString = GuiSnooper.this.game_settings_2.getKeyBinding(GameSettings.Options.SNOOPER_ENABLED);
         }
      };
      guibutton.enabled = false;
      this.toggleButton = this.addButton(guibutton);
      this.addButton(new GuiButton(2, this.width / 2 + 2, this.height - 30, 150, 20, I18n.format("gui.done")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiSnooper.this.game_settings_2.saveOptions();
            GuiSnooper.this.game_settings_2.saveOptions();
            GuiSnooper.this.mc.displayGuiScreen(GuiSnooper.this.lastScreen);
         }
      });
      boolean flag = this.mc.getIntegratedServer() != null && this.mc.getIntegratedServer().getSnooper() != null;

      for(Entry<String, String> entry : (new TreeMap<>(this.mc.getSnooper().getCurrentStats())).entrySet()) {
         this.keys.add((flag ? "C " : "") + (String)entry.getKey());
         this.values.add(this.fontRenderer.trimStringToWidth(entry.getValue(), this.width - 220));
      }

      if (flag) {
         for(Entry<String, String> entry1 : (new TreeMap<>(this.mc.getIntegratedServer().getSnooper().getCurrentStats())).entrySet()) {
            this.keys.add("S " + (String)entry1.getKey());
            this.values.add(this.fontRenderer.trimStringToWidth(entry1.getValue(), this.width - 220));
         }
      }

      this.list = new GuiSnooper.List();
      this.children.add(this.list);
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.list.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 8, 16777215);
      int i = 22;

      for(String s : this.desc) {
         this.drawCenteredString(this.fontRenderer, s, this.width / 2, i, 8421504);
         i += this.fontRenderer.FONT_HEIGHT;
      }

      super.render(mouseX, mouseY, partialTicks);
   }

   @OnlyIn(Dist.CLIENT)
   class List extends GuiSlot {
      public List() {
         super(GuiSnooper.this.mc, GuiSnooper.this.width, GuiSnooper.this.height, 80, GuiSnooper.this.height - 40, GuiSnooper.this.fontRenderer.FONT_HEIGHT + 1);
      }

      protected int getSize() {
         return GuiSnooper.this.keys.size();
      }

      /**
       * Returns true if the element passed in is currently selected
       */
      protected boolean isSelected(int slotIndex) {
         return false;
      }

      protected void drawBackground() {
      }

      protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks) {
         GuiSnooper.this.fontRenderer.drawString(GuiSnooper.this.keys.get(slotIndex), 10.0F, (float)yPos, 16777215);
         GuiSnooper.this.fontRenderer.drawString(GuiSnooper.this.values.get(slotIndex), 230.0F, (float)yPos, 16777215);
      }

      protected int getScrollBarX() {
         return this.width - 10;
      }
   }
}