package net.minecraft.realms;

import java.lang.reflect.Constructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenAlert;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsBridge extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private GuiScreen previousScreen;

   public void switchToRealms(GuiScreen p_switchToRealms_1_) {
      this.previousScreen = p_switchToRealms_1_;

      try {
         Class<?> oclass = Class.forName("com.mojang.realmsclient.RealmsMainScreen");
         Constructor<?> constructor = oclass.getDeclaredConstructor(RealmsScreen.class);
         constructor.setAccessible(true);
         Object object = constructor.newInstance(this);
         Minecraft.getInstance().displayGuiScreen(((RealmsScreen)object).getProxy());
      } catch (ClassNotFoundException var5) {
         LOGGER.error("Realms module missing");
         this.showMissingRealmsErrorScreen();
      } catch (Exception exception) {
         LOGGER.error("Failed to load Realms module", (Throwable)exception);
         this.showMissingRealmsErrorScreen();
      }

   }

   public GuiScreenRealmsProxy getNotificationScreen(GuiScreen p_getNotificationScreen_1_) {
      try {
         this.previousScreen = p_getNotificationScreen_1_;
         Class<?> oclass = Class.forName("com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen");
         Constructor<?> constructor = oclass.getDeclaredConstructor(RealmsScreen.class);
         constructor.setAccessible(true);
         Object object = constructor.newInstance(this);
         return ((RealmsScreen)object).getProxy();
      } catch (ClassNotFoundException var5) {
         LOGGER.error("Realms module missing");
      } catch (Exception exception) {
         LOGGER.error("Failed to load Realms module", (Throwable)exception);
      }

      return null;
   }

   public void init() {
      Minecraft.getInstance().displayGuiScreen(this.previousScreen);
   }

   public static void openUri(String p_openUri_0_) {
      Util.getOSType().openURI(p_openUri_0_);
   }

   public static void setClipboard(String p_setClipboard_0_) {
      Minecraft.getInstance().keyboardListener.setClipboardString(p_setClipboard_0_);
   }

   private void showMissingRealmsErrorScreen() {
      Minecraft.getInstance().displayGuiScreen(new GuiScreenAlert(() -> {
         Minecraft.getInstance().displayGuiScreen(this.previousScreen);
      }, new TextComponentString(""), new TextComponentTranslation("realms.missing.module.error.text")));
   }
}