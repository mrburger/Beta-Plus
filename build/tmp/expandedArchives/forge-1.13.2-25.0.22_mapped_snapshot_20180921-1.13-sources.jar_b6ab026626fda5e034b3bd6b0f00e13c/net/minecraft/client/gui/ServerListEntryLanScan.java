package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ServerListEntryLanScan extends ServerSelectionList.Entry {
   private final Minecraft mc = Minecraft.getInstance();

   public void drawEntry(int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
      int i = this.getY() + entryHeight / 2 - this.mc.fontRenderer.FONT_HEIGHT / 2;
      this.mc.fontRenderer.drawString(I18n.format("lanServer.scanning"), (float)(this.mc.currentScreen.width / 2 - this.mc.fontRenderer.getStringWidth(I18n.format("lanServer.scanning")) / 2), (float)i, 16777215);
      String s;
      switch((int)(Util.milliTime() / 300L % 4L)) {
      case 0:
      default:
         s = "O o o";
         break;
      case 1:
      case 3:
         s = "o O o";
         break;
      case 2:
         s = "o o O";
      }

      this.mc.fontRenderer.drawString(s, (float)(this.mc.currentScreen.width / 2 - this.mc.fontRenderer.getStringWidth(s) / 2), (float)(i + this.mc.fontRenderer.FONT_HEIGHT), 8421504);
   }
}