package net.minecraft.network.login;

import net.minecraft.network.INetHandler;
import net.minecraft.network.login.server.SPacketCustomPayloadLogin;
import net.minecraft.network.login.server.SPacketDisconnectLogin;
import net.minecraft.network.login.server.SPacketEnableCompression;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.network.login.server.SPacketLoginSuccess;

public interface INetHandlerLoginClient extends INetHandler {
   void handleEncryptionRequest(SPacketEncryptionRequest packetIn);

   void handleLoginSuccess(SPacketLoginSuccess packetIn);

   void handleDisconnect(SPacketDisconnectLogin packetIn);

   void handleEnableCompression(SPacketEnableCompression packetIn);

   void func_209521_a(SPacketCustomPayloadLogin p_209521_1_);
}