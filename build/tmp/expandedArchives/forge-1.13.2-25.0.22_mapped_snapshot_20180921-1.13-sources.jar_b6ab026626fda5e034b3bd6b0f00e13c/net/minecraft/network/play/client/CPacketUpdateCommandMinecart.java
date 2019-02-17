package net.minecraft.network.play.client;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketUpdateCommandMinecart implements Packet<INetHandlerPlayServer> {
   private int entityId;
   private String command;
   private boolean trackOutput;

   public CPacketUpdateCommandMinecart() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketUpdateCommandMinecart(int entityIdIn, String commandIn, boolean trackOutputIn) {
      this.entityId = entityIdIn;
      this.command = commandIn;
      this.trackOutput = trackOutputIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarInt();
      this.command = buf.readString(32767);
      this.trackOutput = buf.readBoolean();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.entityId);
      buf.writeString(this.command);
      buf.writeBoolean(this.trackOutput);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(INetHandlerPlayServer handler) {
      handler.processUpdateCommandMinecart(this);
   }

   @Nullable
   public CommandBlockBaseLogic getCommandBlock(World worldIn) {
      Entity entity = worldIn.getEntityByID(this.entityId);
      return entity instanceof EntityMinecartCommandBlock ? ((EntityMinecartCommandBlock)entity).getCommandBlockLogic() : null;
   }

   public String getCommand() {
      return this.command;
   }

   public boolean shouldTrackOutput() {
      return this.trackOutput;
   }
}