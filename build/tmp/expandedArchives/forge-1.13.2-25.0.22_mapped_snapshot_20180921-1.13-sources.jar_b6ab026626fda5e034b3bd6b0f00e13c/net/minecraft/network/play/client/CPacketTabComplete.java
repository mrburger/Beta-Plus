package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketTabComplete implements Packet<INetHandlerPlayServer> {
   private int transactionId;
   private String command;

   public CPacketTabComplete() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketTabComplete(int p_i47928_1_, String p_i47928_2_) {
      this.transactionId = p_i47928_1_;
      this.command = p_i47928_2_;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.transactionId = buf.readVarInt();
      this.command = buf.readString(32500);
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.transactionId);
      buf.writeString(this.command, 32500);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(INetHandlerPlayServer handler) {
      handler.processTabComplete(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }

   public String getCommand() {
      return this.command;
   }
}