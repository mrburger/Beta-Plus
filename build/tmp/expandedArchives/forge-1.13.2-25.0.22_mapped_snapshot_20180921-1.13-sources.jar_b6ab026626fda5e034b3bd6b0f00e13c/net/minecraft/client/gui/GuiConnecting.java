package net.minecraft.client.gui;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.CPacketHandshake;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GuiConnecting extends GuiScreen {
   private static final AtomicInteger CONNECTION_ID = new AtomicInteger(0);
   private static final Logger LOGGER = LogManager.getLogger();
   private NetworkManager networkManager;
   private boolean cancel;
   private final GuiScreen previousGuiScreen;
   private ITextComponent field_209515_s = new TextComponentTranslation("connect.connecting");

   public GuiConnecting(GuiScreen parent, Minecraft mcIn, ServerData serverDataIn) {
      this.mc = mcIn;
      this.previousGuiScreen = parent;
      ServerAddress serveraddress = ServerAddress.fromString(serverDataIn.serverIP);
      mcIn.loadWorld((WorldClient)null);
      mcIn.setServerData(serverDataIn);
      this.connect(serveraddress.getIP(), serveraddress.getPort());
   }

   public GuiConnecting(GuiScreen parent, Minecraft mcIn, String hostName, int port) {
      this.mc = mcIn;
      this.previousGuiScreen = parent;
      mcIn.loadWorld((WorldClient)null);
      this.connect(hostName, port);
   }

   private void connect(final String ip, final int port) {
      LOGGER.info("Connecting to {}, {}", ip, port);
      Thread thread = new Thread("Server Connector #" + CONNECTION_ID.incrementAndGet()) {
         public void run() {
            InetAddress inetaddress = null;

            try {
               if (GuiConnecting.this.cancel) {
                  return;
               }

               inetaddress = InetAddress.getByName(ip);
               GuiConnecting.this.networkManager = NetworkManager.createNetworkManagerAndConnect(inetaddress, port, GuiConnecting.this.mc.gameSettings.isUsingNativeTransport());
               GuiConnecting.this.networkManager.setNetHandler(new NetHandlerLoginClient(GuiConnecting.this.networkManager, GuiConnecting.this.mc, GuiConnecting.this.previousGuiScreen, (p_209549_1_) -> {
                  GuiConnecting.this.func_209514_a(p_209549_1_);
               }));
               net.minecraftforge.fml.network.NetworkHooks.registerClientLoginChannel(GuiConnecting.this.networkManager);
               GuiConnecting.this.networkManager.sendPacket(new CPacketHandshake(ip, port, EnumConnectionState.LOGIN));
               GuiConnecting.this.networkManager.sendPacket(new CPacketLoginStart(GuiConnecting.this.mc.getSession().getProfile()));
            } catch (UnknownHostException unknownhostexception) {
               if (GuiConnecting.this.cancel) {
                  return;
               }

               GuiConnecting.LOGGER.error("Couldn't connect to server", (Throwable)unknownhostexception);
               GuiConnecting.this.mc.addScheduledTask(() -> {
                  GuiConnecting.this.mc.displayGuiScreen(new GuiDisconnected(GuiConnecting.this.previousGuiScreen, "connect.failed", new TextComponentTranslation("disconnect.genericReason", "Unknown host")));
               });
            } catch (Exception exception) {
               if (GuiConnecting.this.cancel) {
                  return;
               }

               GuiConnecting.LOGGER.error("Couldn't connect to server", (Throwable)exception);
               String s = inetaddress == null ? exception.toString() : exception.toString().replaceAll(inetaddress + ":" + port, "");
               GuiConnecting.this.mc.addScheduledTask(() -> {
                  GuiConnecting.this.mc.displayGuiScreen(new GuiDisconnected(GuiConnecting.this.previousGuiScreen, "connect.failed", new TextComponentTranslation("disconnect.genericReason", s)));
               });
            }

         }
      };
      thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      thread.start();
   }

   private void func_209514_a(ITextComponent p_209514_1_) {
      this.field_209515_s = p_209514_1_;
   }

   /**
    * Called from the main game loop to update the screen.
    */
   public void tick() {
      if (this.networkManager != null) {
         if (this.networkManager.isChannelOpen()) {
            this.networkManager.tick();
         } else {
            this.networkManager.handleDisconnection();
         }
      }

   }

   /**
    * Called when escape is pressed in this gui.
    *  
    * @return true if the GUI is allowed to close from this press.
    */
   public boolean allowCloseWithEscape() {
      return false;
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiConnecting.this.cancel = true;
            if (GuiConnecting.this.networkManager != null) {
               GuiConnecting.this.networkManager.closeChannel(new TextComponentTranslation("connect.aborted"));
            }

            GuiConnecting.this.mc.displayGuiScreen(GuiConnecting.this.previousGuiScreen);
         }
      });
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.field_209515_s.getFormattedText(), this.width / 2, this.height / 2 - 50, 16777215);
      super.render(mouseX, mouseY, partialTicks);
   }
}