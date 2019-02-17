package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ServerListEntryLanDetected extends ServerSelectionList.Entry {
   private final GuiMultiplayer screen;
   protected final Minecraft mc;
   protected final LanServerInfo serverData;
   private long lastClickTime;

   protected ServerListEntryLanDetected(GuiMultiplayer p_i47141_1_, LanServerInfo p_i47141_2_) {
      this.screen = p_i47141_1_;
      this.serverData = p_i47141_2_;
      this.mc = Minecraft.getInstance();
   }

   public void drawEntry(int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
      int i = this.getX();
      int j = this.getY();
      this.mc.fontRenderer.drawString(I18n.format("lanServer.title"), (float)(i + 32 + 3), (float)(j + 1), 16777215);
      this.mc.fontRenderer.drawString(this.serverData.getServerMotd(), (float)(i + 32 + 3), (float)(j + 12), 8421504);
      if (this.mc.gameSettings.hideServerAddress) {
         this.mc.fontRenderer.drawString(I18n.format("selectServer.hiddenAddress"), (float)(i + 32 + 3), (float)(j + 12 + 11), 3158064);
      } else {
         this.mc.fontRenderer.drawString(this.serverData.getServerIpPort(), (float)(i + 32 + 3), (float)(j + 12 + 11), 3158064);
      }

   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      this.screen.selectServer(this.getIndex());
      if (Util.milliTime() - this.lastClickTime < 250L) {
         this.screen.connectToSelected();
      }

      this.lastClickTime = Util.milliTime();
      return false;
   }

   public LanServerInfo getServerData() {
      return this.serverData;
   }
}