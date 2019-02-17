package net.minecraft.client.gui;

import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryStack;

@OnlyIn(Dist.CLIENT)
public class ServerListEntryNormal extends ServerSelectionList.Entry {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(5, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).build());
   private static final ResourceLocation UNKNOWN_SERVER = new ResourceLocation("textures/misc/unknown_server.png");
   private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation("textures/gui/server_selection.png");
   private final GuiMultiplayer owner;
   private final Minecraft mc;
   private final ServerData server;
   private final ResourceLocation serverIcon;
   private String lastIconB64;
   private DynamicTexture icon;
   private long lastClickTime;

   protected ServerListEntryNormal(GuiMultiplayer ownerIn, ServerData serverIn) {
      this.owner = ownerIn;
      this.server = serverIn;
      this.mc = Minecraft.getInstance();
      this.serverIcon = new ResourceLocation("servers/" + Hashing.sha1().hashUnencodedChars(serverIn.serverIP) + "/icon");
      this.icon = (DynamicTexture)this.mc.getTextureManager().getTexture(this.serverIcon);
   }

   public void drawEntry(int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
      int i = this.getY();
      int j = this.getX();
      if (!this.server.pinged) {
         this.server.pinged = true;
         this.server.pingToServer = -2L;
         this.server.serverMOTD = "";
         this.server.populationInfo = "";
         EXECUTOR.submit(() -> {
            try {
               this.owner.getOldServerPinger().ping(this.server);
            } catch (UnknownHostException var2) {
               this.server.pingToServer = -1L;
               this.server.serverMOTD = TextFormatting.DARK_RED + I18n.format("multiplayer.status.cannot_resolve");
            } catch (Exception var3) {
               this.server.pingToServer = -1L;
               this.server.serverMOTD = TextFormatting.DARK_RED + I18n.format("multiplayer.status.cannot_connect");
            }

         });
      }

      boolean flag = this.server.version > 404;
      boolean flag1 = this.server.version < 404;
      boolean flag2 = flag || flag1;
      this.mc.fontRenderer.drawString(this.server.serverName, (float)(j + 32 + 3), (float)(i + 1), 16777215);
      List<String> list = this.mc.fontRenderer.listFormattedStringToWidth(this.server.serverMOTD, entryWidth - 32 - 2);

      for(int k = 0; k < Math.min(list.size(), 2); ++k) {
         this.mc.fontRenderer.drawString(list.get(k), (float)(j + 32 + 3), (float)(i + 12 + this.mc.fontRenderer.FONT_HEIGHT * k), 8421504);
      }

      String s2 = flag2 ? TextFormatting.DARK_RED + this.server.gameVersion : this.server.populationInfo;
      int l = this.mc.fontRenderer.getStringWidth(s2);
      this.mc.fontRenderer.drawString(s2, (float)(j + entryWidth - l - 15 - 2), (float)(i + 1), 8421504);
      int i1 = 0;
      String s = null;
      int j1;
      String s1;
      if (flag2) {
         j1 = 5;
         s1 = I18n.format(flag ? "multiplayer.status.client_out_of_date" : "multiplayer.status.server_out_of_date");
         s = this.server.playerList;
      } else if (this.server.pinged && this.server.pingToServer != -2L) {
         if (this.server.pingToServer < 0L) {
            j1 = 5;
         } else if (this.server.pingToServer < 150L) {
            j1 = 0;
         } else if (this.server.pingToServer < 300L) {
            j1 = 1;
         } else if (this.server.pingToServer < 600L) {
            j1 = 2;
         } else if (this.server.pingToServer < 1000L) {
            j1 = 3;
         } else {
            j1 = 4;
         }

         if (this.server.pingToServer < 0L) {
            s1 = I18n.format("multiplayer.status.no_connection");
         } else {
            s1 = this.server.pingToServer + "ms";
            s = this.server.playerList;
         }
      } else {
         i1 = 1;
         j1 = (int)(Util.milliTime() / 100L + (long)(this.getIndex() * 2) & 7L);
         if (j1 > 4) {
            j1 = 8 - j1;
         }

         s1 = I18n.format("multiplayer.status.pinging");
      }

      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(Gui.ICONS);
      Gui.drawModalRectWithCustomSizedTexture(j + entryWidth - 15, i, (float)(i1 * 10), (float)(176 + j1 * 8), 10, 8, 256.0F, 256.0F);
      if (this.server.getBase64EncodedIconData() != null && !this.server.getBase64EncodedIconData().equals(this.lastIconB64)) {
         this.lastIconB64 = this.server.getBase64EncodedIconData();
         this.prepareServerIcon();
         this.owner.getServerList().saveServerList();
      }

      if (this.icon != null) {
         this.drawTextureAt(j, i, this.serverIcon);
      } else {
         this.drawTextureAt(j, i, UNKNOWN_SERVER);
      }

      int k1 = mouseX - j;
      int l1 = mouseY - i;
      if (k1 >= entryWidth - 15 && k1 <= entryWidth - 5 && l1 >= 0 && l1 <= 8) {
         this.owner.setHoveringText(s1);
      } else if (k1 >= entryWidth - l - 15 - 2 && k1 <= entryWidth - 15 - 2 && l1 >= 0 && l1 <= 8) {
         this.owner.setHoveringText(s);
      }

      if (this.mc.gameSettings.touchscreen || p_194999_5_) {
         this.mc.getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
         Gui.drawRect(j, i, j + 32, i + 32, -1601138544);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         int i2 = mouseX - j;
         int j2 = mouseY - i;
         if (this.canJoin()) {
            if (i2 < 32 && i2 > 16) {
               Gui.drawModalRectWithCustomSizedTexture(j, i, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.drawModalRectWithCustomSizedTexture(j, i, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }

         if (this.owner.canMoveUp(this, this.getIndex())) {
            if (i2 < 16 && j2 < 16) {
               Gui.drawModalRectWithCustomSizedTexture(j, i, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.drawModalRectWithCustomSizedTexture(j, i, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }

         if (this.owner.canMoveDown(this, this.getIndex())) {
            if (i2 < 16 && j2 > 16) {
               Gui.drawModalRectWithCustomSizedTexture(j, i, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.drawModalRectWithCustomSizedTexture(j, i, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }
      }

   }

   protected void drawTextureAt(int p_178012_1_, int p_178012_2_, ResourceLocation p_178012_3_) {
      this.mc.getTextureManager().bindTexture(p_178012_3_);
      GlStateManager.enableBlend();
      Gui.drawModalRectWithCustomSizedTexture(p_178012_1_, p_178012_2_, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
      GlStateManager.disableBlend();
   }

   private boolean canJoin() {
      return true;
   }

   private void prepareServerIcon() {
      if (this.server.getBase64EncodedIconData() == null) {
         this.mc.getTextureManager().deleteTexture(this.serverIcon);
         this.icon.getTextureData().close();
         this.icon = null;
      } else {
         try (MemoryStack memorystack = MemoryStack.stackPush()) {
            ByteBuffer bytebuffer = memorystack.UTF8(this.server.getBase64EncodedIconData(), false);
            ByteBuffer bytebuffer1 = Base64.getDecoder().decode(bytebuffer);
            ByteBuffer bytebuffer2 = memorystack.malloc(bytebuffer1.remaining());
            bytebuffer2.put(bytebuffer1);
            bytebuffer2.rewind();
            NativeImage nativeimage = NativeImage.read(bytebuffer2);
            Validate.validState(nativeimage.getWidth() == 64, "Must be 64 pixels wide");
            Validate.validState(nativeimage.getHeight() == 64, "Must be 64 pixels high");
            if (this.icon == null) {
               this.icon = new DynamicTexture(nativeimage);
            } else {
               this.icon.setTextureData(nativeimage);
               this.icon.updateDynamicTexture();
            }

            this.mc.getTextureManager().loadTexture(this.serverIcon, this.icon);
         } catch (Throwable throwable) {
            LOGGER.error("Invalid icon for server {} ({})", this.server.serverName, this.server.serverIP, throwable);
            this.server.setBase64EncodedIconData((String)null);
         }
      }

   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      double d0 = p_mouseClicked_1_ - (double)this.getX();
      double d1 = p_mouseClicked_3_ - (double)this.getY();
      if (d0 <= 32.0D) {
         if (d0 < 32.0D && d0 > 16.0D && this.canJoin()) {
            this.owner.selectServer(this.getIndex());
            this.owner.connectToSelected();
            return true;
         }

         if (d0 < 16.0D && d1 < 16.0D && this.owner.canMoveUp(this, this.getIndex())) {
            this.owner.moveServerUp(this, this.getIndex(), GuiScreen.isShiftKeyDown());
            return true;
         }

         if (d0 < 16.0D && d1 > 16.0D && this.owner.canMoveDown(this, this.getIndex())) {
            this.owner.moveServerDown(this, this.getIndex(), GuiScreen.isShiftKeyDown());
            return true;
         }
      }

      this.owner.selectServer(this.getIndex());
      if (Util.milliTime() - this.lastClickTime < 250L) {
         this.owner.connectToSelected();
      }

      this.lastClickTime = Util.milliTime();
      return false;
   }

   public ServerData getServerData() {
      return this.server;
   }
}