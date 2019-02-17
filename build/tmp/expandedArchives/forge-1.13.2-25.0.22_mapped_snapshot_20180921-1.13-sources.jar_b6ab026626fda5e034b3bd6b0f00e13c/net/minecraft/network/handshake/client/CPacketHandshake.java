package net.minecraft.network.handshake.client;

import java.io.IOException;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketHandshake implements Packet<INetHandlerHandshakeServer> {
   private int protocolVersion;
   private String ip;
   private int port;
   private EnumConnectionState requestedState;
   private String fmlVersion = net.minecraftforge.fml.network.NetworkHooks.NETVERSION;

   public CPacketHandshake() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketHandshake(String p_i47613_1_, int p_i47613_2_, EnumConnectionState p_i47613_3_) {
      this.protocolVersion = 404;
      this.ip = p_i47613_1_;
      this.port = p_i47613_2_;
      this.requestedState = p_i47613_3_;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.protocolVersion = buf.readVarInt();
      this.ip = buf.readString(255);
      this.port = buf.readUnsignedShort();
      this.requestedState = EnumConnectionState.getById(buf.readVarInt());
      this.fmlVersion = net.minecraftforge.fml.network.NetworkHooks.getFMLVersion(this.ip);
      this.ip = this.ip.split("\0")[0];
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.protocolVersion);
      buf.writeString(this.ip + "\0"+net.minecraftforge.fml.network.NetworkHooks.NETVERSION+"\0");
      buf.writeShort(this.port);
      buf.writeVarInt(this.requestedState.getId());
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(INetHandlerHandshakeServer handler) {
      handler.processHandshake(this);
   }

   public EnumConnectionState getRequestedState() {
      return this.requestedState;
   }

   public int getProtocolVersion() {
      return this.protocolVersion;
   }

   public String getFMLVersion() {
      return this.fmlVersion;
   }
}