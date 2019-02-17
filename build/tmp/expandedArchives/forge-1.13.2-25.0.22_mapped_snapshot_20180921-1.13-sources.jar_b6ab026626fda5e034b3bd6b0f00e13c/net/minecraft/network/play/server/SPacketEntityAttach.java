package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketEntityAttach implements Packet<INetHandlerPlayClient> {
   private int entityId;
   /** The entity that is holding the leash, or -1 to clear the holder. */
   private int vehicleEntityId;

   public SPacketEntityAttach() {
   }

   public SPacketEntityAttach(Entity entityIn, @Nullable Entity vehicleIn) {
      this.entityId = entityIn.getEntityId();
      this.vehicleEntityId = vehicleIn != null ? vehicleIn.getEntityId() : -1;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readInt();
      this.vehicleEntityId = buf.readInt();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeInt(this.entityId);
      buf.writeInt(this.vehicleEntityId);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleEntityAttach(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityId() {
      return this.entityId;
   }

   @OnlyIn(Dist.CLIENT)
   public int getVehicleEntityId() {
      return this.vehicleEntityId;
   }
}