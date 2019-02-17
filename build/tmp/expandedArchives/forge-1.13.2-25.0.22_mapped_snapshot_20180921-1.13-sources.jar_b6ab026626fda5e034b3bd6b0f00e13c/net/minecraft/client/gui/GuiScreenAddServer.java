package net.minecraft.client.gui;

import java.net.IDN;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.StringUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiScreenAddServer extends GuiScreen {
   private GuiButton field_195179_a;
   private final GuiScreen parentScreen;
   private final ServerData serverData;
   private GuiTextField serverIPField;
   private GuiTextField serverNameField;
   private GuiButton serverResourcePacks;
   private final Predicate<String> addressFilter = (p_210141_0_) -> {
      if (StringUtils.isNullOrEmpty(p_210141_0_)) {
         return true;
      } else {
         String[] astring = p_210141_0_.split(":");
         if (astring.length == 0) {
            return true;
         } else {
            try {
               String s = IDN.toASCII(astring[0]);
               return true;
            } catch (IllegalArgumentException var3) {
               return false;
            }
         }
      }
   };

   public GuiScreenAddServer(GuiScreen parentScreenIn, ServerData serverDataIn) {
      this.parentScreen = parentScreenIn;
      this.serverData = serverDataIn;
   }

   /**
    * Called from the main game loop to update the screen.
    */
   public void tick() {
      this.serverNameField.tick();
      this.serverIPField.tick();
   }

   public IGuiEventListener getFocused() {
      return this.serverIPField.isFocused() ? this.serverIPField : this.serverNameField;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      this.mc.keyboardListener.enableRepeatEvents(true);
      this.field_195179_a = this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 18, I18n.format("addServer.add")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiScreenAddServer.this.func_195172_h();
         }
      });
      this.addButton(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 18, I18n.format("gui.cancel")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiScreenAddServer.this.parentScreen.confirmResult(false, 0);
         }
      });
      this.serverResourcePacks = this.addButton(new GuiButton(2, this.width / 2 - 100, this.height / 4 + 72, I18n.format("addServer.resourcePack") + ": " + this.serverData.getResourceMode().getMotd().getFormattedText()) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiScreenAddServer.this.serverData.setResourceMode(ServerData.ServerResourceMode.values()[(GuiScreenAddServer.this.serverData.getResourceMode().ordinal() + 1) % ServerData.ServerResourceMode.values().length]);
            GuiScreenAddServer.this.serverResourcePacks.displayString = I18n.format("addServer.resourcePack") + ": " + GuiScreenAddServer.this.serverData.getResourceMode().getMotd().getFormattedText();
         }
      });
      this.serverIPField = new GuiTextField(1, this.fontRenderer, this.width / 2 - 100, 106, 200, 20) {
         /**
          * Sets focus to this gui element
          */
         public void setFocused(boolean isFocusedIn) {
            super.setFocused(isFocusedIn);
            if (isFocusedIn) {
               GuiScreenAddServer.this.serverNameField.setFocused(false);
            }

         }
      };
      this.serverIPField.setMaxStringLength(128);
      this.serverIPField.setText(this.serverData.serverIP);
      this.serverIPField.setValidator(this.addressFilter);
      this.serverIPField.setTextAcceptHandler(this::func_195171_a);
      this.children.add(this.serverIPField);
      this.serverNameField = new GuiTextField(0, this.fontRenderer, this.width / 2 - 100, 66, 200, 20) {
         /**
          * Sets focus to this gui element
          */
         public void setFocused(boolean isFocusedIn) {
            super.setFocused(isFocusedIn);
            if (isFocusedIn) {
               GuiScreenAddServer.this.serverIPField.setFocused(false);
            }

         }
      };
      this.serverNameField.setFocused(true);
      this.serverNameField.setText(this.serverData.serverName);
      this.serverNameField.setTextAcceptHandler(this::func_195171_a);
      this.children.add(this.serverNameField);
      this.close();
   }

   /**
    * Called when the GUI is resized in order to update the world and the resolution
    */
   public void onResize(Minecraft mcIn, int w, int h) {
      String s = this.serverIPField.getText();
      String s1 = this.serverNameField.getText();
      this.setWorldAndResolution(mcIn, w, h);
      this.serverIPField.setText(s);
      this.serverNameField.setText(s1);
   }

   private void func_195171_a(int p_195171_1_, String p_195171_2_) {
      this.close();
   }

   /**
    * Called when the screen is unloaded. Used to disable keyboard repeat events
    */
   public void onGuiClosed() {
      this.mc.keyboardListener.enableRepeatEvents(false);
   }

   private void func_195172_h() {
      this.serverData.serverName = this.serverNameField.getText();
      this.serverData.serverIP = this.serverIPField.getText();
      this.parentScreen.confirmResult(true, 0);
   }

   public void close() {
      this.field_195179_a.enabled = !this.serverIPField.getText().isEmpty() && this.serverIPField.getText().split(":").length > 0 && !this.serverNameField.getText().isEmpty();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 258) {
         if (this.serverNameField.isFocused()) {
            this.serverIPField.setFocused(true);
         } else {
            this.serverNameField.setFocused(true);
         }

         return true;
      } else if ((p_keyPressed_1_ == 257 || p_keyPressed_1_ == 335) && this.field_195179_a.enabled) {
         this.func_195172_h();
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
      this.drawCenteredString(this.fontRenderer, I18n.format("addServer.title"), this.width / 2, 17, 16777215);
      this.drawString(this.fontRenderer, I18n.format("addServer.enterName"), this.width / 2 - 100, 53, 10526880);
      this.drawString(this.fontRenderer, I18n.format("addServer.enterIp"), this.width / 2 - 100, 94, 10526880);
      this.serverNameField.drawTextField(mouseX, mouseY, partialTicks);
      this.serverIPField.drawTextField(mouseX, mouseY, partialTicks);
      super.render(mouseX, mouseY, partialTicks);
   }
}