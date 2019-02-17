package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.network.login.client.CPacketCustomPayloadLogin;
import net.minecraft.network.login.client.CPacketEncryptionResponse;
import net.minecraft.network.login.server.SPacketCustomPayloadLogin;
import net.minecraft.network.login.server.SPacketDisconnectLogin;
import net.minecraft.network.login.server.SPacketEnableCompression;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.util.CryptManager;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class NetHandlerLoginClient implements INetHandlerLoginClient {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft mc;
   @Nullable
   private final GuiScreen previousGuiScreen;
   private final Consumer<ITextComponent> field_209525_d;
   private final NetworkManager networkManager;
   private GameProfile gameProfile;

   public NetHandlerLoginClient(NetworkManager p_i49527_1_, Minecraft p_i49527_2_, @Nullable GuiScreen p_i49527_3_, Consumer<ITextComponent> p_i49527_4_) {
      this.networkManager = p_i49527_1_;
      this.mc = p_i49527_2_;
      this.previousGuiScreen = p_i49527_3_;
      this.field_209525_d = p_i49527_4_;
   }

   public void handleEncryptionRequest(SPacketEncryptionRequest packetIn) {
      SecretKey secretkey = CryptManager.createNewSharedKey();
      PublicKey publickey = packetIn.getPublicKey();
      String s = (new BigInteger(CryptManager.getServerIdHash(packetIn.getServerId(), publickey, secretkey))).toString(16);
      CPacketEncryptionResponse cpacketencryptionresponse = new CPacketEncryptionResponse(secretkey, publickey, packetIn.getVerifyToken());
      this.field_209525_d.accept(new TextComponentTranslation("connect.authorizing"));
      HttpUtil.DOWNLOADER_EXECUTOR.submit(() -> {
         ITextComponent itextcomponent = this.func_209522_a(s);
         if (itextcomponent != null) {
            if (this.mc.getCurrentServerData() == null || !this.mc.getCurrentServerData().isOnLAN()) {
               this.networkManager.closeChannel(itextcomponent);
               return;
            }

            LOGGER.warn(itextcomponent.getString());
         }

         this.field_209525_d.accept(new TextComponentTranslation("connect.encrypting"));
         this.networkManager.sendPacket(cpacketencryptionresponse, (p_209523_2_) -> {
            this.networkManager.enableEncryption(secretkey);
         });
      });
   }

   @Nullable
   private ITextComponent func_209522_a(String p_209522_1_) {
      try {
         this.getSessionService().joinServer(this.mc.getSession().getProfile(), this.mc.getSession().getToken(), p_209522_1_);
         return null;
      } catch (AuthenticationUnavailableException var3) {
         return new TextComponentTranslation("disconnect.loginFailedInfo", new TextComponentTranslation("disconnect.loginFailedInfo.serversUnavailable"));
      } catch (InvalidCredentialsException var4) {
         return new TextComponentTranslation("disconnect.loginFailedInfo", new TextComponentTranslation("disconnect.loginFailedInfo.invalidSession"));
      } catch (AuthenticationException authenticationexception) {
         return new TextComponentTranslation("disconnect.loginFailedInfo", authenticationexception.getMessage());
      }
   }

   private MinecraftSessionService getSessionService() {
      return this.mc.getSessionService();
   }

   public void handleLoginSuccess(SPacketLoginSuccess packetIn) {
      this.field_209525_d.accept(new TextComponentTranslation("connect.joining"));
      this.gameProfile = packetIn.getProfile();
      this.networkManager.setConnectionState(EnumConnectionState.PLAY);
      this.networkManager.setNetHandler(new NetHandlerPlayClient(this.mc, this.previousGuiScreen, this.networkManager, this.gameProfile));
   }

   /**
    * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
    */
   public void onDisconnect(ITextComponent reason) {
      if (this.previousGuiScreen != null && this.previousGuiScreen instanceof GuiScreenRealmsProxy) {
         this.mc.displayGuiScreen((new DisconnectedRealmsScreen(((GuiScreenRealmsProxy)this.previousGuiScreen).getProxy(), "connect.failed", reason)).getProxy());
      } else {
         this.mc.displayGuiScreen(new GuiDisconnected(this.previousGuiScreen, "connect.failed", reason));
      }

   }

   public void handleDisconnect(SPacketDisconnectLogin packetIn) {
      this.networkManager.closeChannel(packetIn.getReason());
   }

   public void handleEnableCompression(SPacketEnableCompression packetIn) {
      if (!this.networkManager.isLocalChannel()) {
         this.networkManager.setCompressionThreshold(packetIn.getCompressionThreshold());
      }

   }

   public void func_209521_a(SPacketCustomPayloadLogin p_209521_1_) {
      if (net.minecraftforge.fml.network.NetworkHooks.onCustomPayload(p_209521_1_, this.networkManager)) return;
      this.field_209525_d.accept(new TextComponentTranslation("connect.negotiating"));
      this.networkManager.sendPacket(new CPacketCustomPayloadLogin(p_209521_1_.getTransaction(), (PacketBuffer)null));
   }
}