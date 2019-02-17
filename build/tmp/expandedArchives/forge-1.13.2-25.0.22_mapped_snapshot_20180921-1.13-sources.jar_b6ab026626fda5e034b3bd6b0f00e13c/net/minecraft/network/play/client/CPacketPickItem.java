package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketPickItem implements Packet<INetHandlerPlayServer> {
   private int pickIndex;

   public CPacketPickItem() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketPickItem(int pickIndexIn) {
      this.pickIndex = pickIndexIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.pickIndex = buf.readVarInt();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.pickIndex);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(INetHandlerPlayServer handler) {
      handler.processPickItem(this);
   }

   public int getPickIndex() {
      return this.pickIndex;
   }
}