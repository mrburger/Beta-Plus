package net.minecraft.network;

import net.minecraft.network.handshake.INetHandlerHandshakeServer;
import net.minecraft.network.handshake.client.CPacketHandshake;
import net.minecraft.network.login.server.SPacketDisconnectLogin;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class NetHandlerHandshakeTCP implements INetHandlerHandshakeServer {
   private final MinecraftServer server;
   private final NetworkManager networkManager;

   public NetHandlerHandshakeTCP(MinecraftServer serverIn, NetworkManager netManager) {
      this.server = serverIn;
      this.networkManager = netManager;
   }

   /**
    * There are two recognized intentions for initiating a handshake: logging in and acquiring server status. The
    * NetworkManager's protocol will be reconfigured according to the specified intention, although a login-intention
    * must pass a versioncheck or receive a disconnect otherwise
    */
   public void processHandshake(CPacketHandshake packetIn) {
      if (!net.minecraftforge.fml.server.ServerLifecycleHooks.handleServerLogin(packetIn, this.networkManager)) return;
      switch(packetIn.getRequestedState()) {
      case LOGIN:
         this.networkManager.setConnectionState(EnumConnectionState.LOGIN);
         if (packetIn.getProtocolVersion() > 404) {
            ITextComponent itextcomponent = new TextComponentTranslation("multiplayer.disconnect.outdated_server", "1.13.2");
            this.networkManager.sendPacket(new SPacketDisconnectLogin(itextcomponent));
            this.networkManager.closeChannel(itextcomponent);
         } else if (packetIn.getProtocolVersion() < 404) {
            ITextComponent itextcomponent1 = new TextComponentTranslation("multiplayer.disconnect.outdated_client", "1.13.2");
            this.networkManager.sendPacket(new SPacketDisconnectLogin(itextcomponent1));
            this.networkManager.closeChannel(itextcomponent1);
         } else {
            this.networkManager.setNetHandler(new NetHandlerLoginServer(this.server, this.networkManager));
         }
         break;
      case STATUS:
         this.networkManager.setConnectionState(EnumConnectionState.STATUS);
         this.networkManager.setNetHandler(new NetHandlerStatusServer(this.server, this.networkManager));
         break;
      default:
         throw new UnsupportedOperationException("Invalid intention " + packetIn.getRequestedState());
      }

   }

   /**
    * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
    */
   public void onDisconnect(ITextComponent reason) {
   }
}