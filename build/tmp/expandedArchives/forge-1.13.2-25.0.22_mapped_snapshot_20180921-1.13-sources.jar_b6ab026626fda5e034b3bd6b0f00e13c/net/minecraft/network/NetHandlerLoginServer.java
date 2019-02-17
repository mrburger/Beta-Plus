package net.minecraft.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.network.login.client.CPacketCustomPayloadLogin;
import net.minecraft.network.login.client.CPacketEncryptionResponse;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.login.server.SPacketDisconnectLogin;
import net.minecraft.network.login.server.SPacketEnableCompression;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.CryptManager;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerLoginServer implements INetHandlerLoginServer, ITickable {
   private static final AtomicInteger AUTHENTICATOR_THREAD_ID = new AtomicInteger(0);
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Random RANDOM = new Random();
   private final byte[] verifyToken = new byte[4];
   private final MinecraftServer server;
   public final NetworkManager networkManager;
   private NetHandlerLoginServer.LoginState currentLoginState = NetHandlerLoginServer.LoginState.HELLO;
   /** How long has player been trying to login into the server. */
   private int connectionTimer;
   private GameProfile loginGameProfile;
   private final String serverId = "";
   private SecretKey secretKey;
   private EntityPlayerMP player;

   public NetHandlerLoginServer(MinecraftServer serverIn, NetworkManager networkManagerIn) {
      this.server = serverIn;
      this.networkManager = networkManagerIn;
      RANDOM.nextBytes(this.verifyToken);
   }

   public void tick() {
      if (this.currentLoginState == LoginState.NEGOTIATING) {
         // We force the state into "NEGOTIATING" which is otherwise unused. Once we're completed we move the negotiation onto "READY_TO_ACCEPT"
         // Might want to promote player object creation to here as well..
         boolean negotiationComplete = net.minecraftforge.fml.network.NetworkHooks.tickNegotiation(this, this.networkManager, this.player);
         if (negotiationComplete)
            this.currentLoginState = LoginState.READY_TO_ACCEPT;
      } else if (this.currentLoginState == NetHandlerLoginServer.LoginState.READY_TO_ACCEPT) {
         this.tryAcceptPlayer();
      } else if (this.currentLoginState == NetHandlerLoginServer.LoginState.DELAY_ACCEPT) {
         EntityPlayerMP entityplayermp = this.server.getPlayerList().getPlayerByUUID(this.loginGameProfile.getId());
         if (entityplayermp == null) {
            this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
            this.server.getPlayerList().initializeConnectionToPlayer(this.networkManager, this.player);
            this.player = null;
         }
      }

      if (this.connectionTimer++ == 600) {
         this.disconnect(new TextComponentTranslation("multiplayer.disconnect.slow_login"));
      }

   }

   public void disconnect(ITextComponent reason) {
      try {
         LOGGER.info("Disconnecting {}: {}", this.getConnectionInfo(), reason.getString());
         this.networkManager.sendPacket(new SPacketDisconnectLogin(reason));
         this.networkManager.closeChannel(reason);
      } catch (Exception exception) {
         LOGGER.error("Error whilst disconnecting player", (Throwable)exception);
      }

   }

   public void tryAcceptPlayer() {
      if (!this.loginGameProfile.isComplete()) {
         this.loginGameProfile = this.getOfflineProfile(this.loginGameProfile);
      }

      ITextComponent itextcomponent = this.server.getPlayerList().canPlayerLogin(this.networkManager.getRemoteAddress(), this.loginGameProfile);
      if (itextcomponent != null) {
         this.disconnect(itextcomponent);
      } else {
         this.currentLoginState = NetHandlerLoginServer.LoginState.ACCEPTED;
         if (this.server.getNetworkCompressionThreshold() >= 0 && !this.networkManager.isLocalChannel()) {
            this.networkManager.sendPacket(new SPacketEnableCompression(this.server.getNetworkCompressionThreshold()), (p_210149_1_) -> {
               this.networkManager.setCompressionThreshold(this.server.getNetworkCompressionThreshold());
            });
         }

         this.networkManager.sendPacket(new SPacketLoginSuccess(this.loginGameProfile));
         EntityPlayerMP entityplayermp = this.server.getPlayerList().getPlayerByUUID(this.loginGameProfile.getId());
         if (entityplayermp != null) {
            this.currentLoginState = NetHandlerLoginServer.LoginState.DELAY_ACCEPT;
            this.player = this.server.getPlayerList().createPlayerForUser(this.loginGameProfile);
         } else {
            this.server.getPlayerList().initializeConnectionToPlayer(this.networkManager, this.server.getPlayerList().createPlayerForUser(this.loginGameProfile));
         }
      }

   }

   /**
    * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
    */
   public void onDisconnect(ITextComponent reason) {
      LOGGER.info("{} lost connection: {}", this.getConnectionInfo(), reason.getString());
   }

   public String getConnectionInfo() {
      return this.loginGameProfile != null ? this.loginGameProfile + " (" + this.networkManager.getRemoteAddress() + ")" : String.valueOf((Object)this.networkManager.getRemoteAddress());
   }

   public void processLoginStart(CPacketLoginStart packetIn) {
      Validate.validState(this.currentLoginState == NetHandlerLoginServer.LoginState.HELLO, "Unexpected hello packet");
      this.loginGameProfile = packetIn.getProfile();
      if (this.server.isServerInOnlineMode() && !this.networkManager.isLocalChannel()) {
         this.currentLoginState = NetHandlerLoginServer.LoginState.KEY;
         this.networkManager.sendPacket(new SPacketEncryptionRequest("", this.server.getKeyPair().getPublic(), this.verifyToken));
      } else {
         this.currentLoginState = NetHandlerLoginServer.LoginState.NEGOTIATING;
      }

   }

   public void processEncryptionResponse(CPacketEncryptionResponse packetIn) {
      Validate.validState(this.currentLoginState == NetHandlerLoginServer.LoginState.KEY, "Unexpected key packet");
      PrivateKey privatekey = this.server.getKeyPair().getPrivate();
      if (!Arrays.equals(this.verifyToken, packetIn.getVerifyToken(privatekey))) {
         throw new IllegalStateException("Invalid nonce!");
      } else {
         this.secretKey = packetIn.getSecretKey(privatekey);
         this.currentLoginState = NetHandlerLoginServer.LoginState.AUTHENTICATING;
         this.networkManager.enableEncryption(this.secretKey);
         Thread thread = new Thread(net.minecraftforge.fml.common.thread.SidedThreadGroups.SERVER, "User Authenticator #" + AUTHENTICATOR_THREAD_ID.incrementAndGet()) {
            public void run() {
               GameProfile gameprofile = NetHandlerLoginServer.this.loginGameProfile;

               try {
                  String s = (new BigInteger(CryptManager.getServerIdHash("", NetHandlerLoginServer.this.server.getKeyPair().getPublic(), NetHandlerLoginServer.this.secretKey))).toString(16);
                  NetHandlerLoginServer.this.loginGameProfile = NetHandlerLoginServer.this.server.getMinecraftSessionService().hasJoinedServer(new GameProfile((UUID)null, gameprofile.getName()), s, this.getAddress());
                  if (NetHandlerLoginServer.this.loginGameProfile != null) {
                     NetHandlerLoginServer.LOGGER.info("UUID of player {} is {}", NetHandlerLoginServer.this.loginGameProfile.getName(), NetHandlerLoginServer.this.loginGameProfile.getId());
                     NetHandlerLoginServer.this.currentLoginState = NetHandlerLoginServer.LoginState.NEGOTIATING;
                  } else if (NetHandlerLoginServer.this.server.isSinglePlayer()) {
                     NetHandlerLoginServer.LOGGER.warn("Failed to verify username but will let them in anyway!");
                     NetHandlerLoginServer.this.loginGameProfile = NetHandlerLoginServer.this.getOfflineProfile(gameprofile);
                     NetHandlerLoginServer.this.currentLoginState = NetHandlerLoginServer.LoginState.NEGOTIATING;
                  } else {
                     NetHandlerLoginServer.this.disconnect(new TextComponentTranslation("multiplayer.disconnect.unverified_username"));
                     NetHandlerLoginServer.LOGGER.error("Username '{}' tried to join with an invalid session", (Object)gameprofile.getName());
                  }
               } catch (AuthenticationUnavailableException var3) {
                  if (NetHandlerLoginServer.this.server.isSinglePlayer()) {
                     NetHandlerLoginServer.LOGGER.warn("Authentication servers are down but will let them in anyway!");
                     NetHandlerLoginServer.this.loginGameProfile = NetHandlerLoginServer.this.getOfflineProfile(gameprofile);
                     NetHandlerLoginServer.this.currentLoginState = NetHandlerLoginServer.LoginState.NEGOTIATING;
                  } else {
                     NetHandlerLoginServer.this.disconnect(new TextComponentTranslation("multiplayer.disconnect.authservers_down"));
                     NetHandlerLoginServer.LOGGER.error("Couldn't verify username because servers are unavailable");
                  }
               }

            }

            @Nullable
            private InetAddress getAddress() {
               SocketAddress socketaddress = NetHandlerLoginServer.this.networkManager.getRemoteAddress();
               return NetHandlerLoginServer.this.server.getPreventProxyConnections() && socketaddress instanceof InetSocketAddress ? ((InetSocketAddress)socketaddress).getAddress() : null;
            }
         };
         thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
         thread.start();
      }
   }

   public void processCustomPayloadLogin(CPacketCustomPayloadLogin p_209526_1_) {
      if (!net.minecraftforge.fml.network.NetworkHooks.onCustomPayload(p_209526_1_, this.networkManager))
      this.disconnect(new TextComponentTranslation("multiplayer.disconnect.unexpected_query_response"));
   }

   protected GameProfile getOfflineProfile(GameProfile original) {
      UUID uuid = EntityPlayer.getOfflineUUID(original.getName());
      return new GameProfile(uuid, original.getName());
   }

   static enum LoginState {
      HELLO,
      KEY,
      AUTHENTICATING,
      NEGOTIATING,
      READY_TO_ACCEPT,
      DELAY_ACCEPT,
      ACCEPTED;
   }
}