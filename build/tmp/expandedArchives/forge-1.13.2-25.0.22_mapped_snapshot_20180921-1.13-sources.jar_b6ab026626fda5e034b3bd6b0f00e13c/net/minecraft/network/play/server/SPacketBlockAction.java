package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketBlockAction implements Packet<INetHandlerPlayClient> {
   private BlockPos blockPosition;
   private int instrument;
   private int pitch;
   private Block block;

   public SPacketBlockAction() {
   }

   public SPacketBlockAction(BlockPos pos, Block blockIn, int instrumentIn, int pitchIn) {
      this.blockPosition = pos;
      this.block = blockIn;
      this.instrument = instrumentIn;
      this.pitch = pitchIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.blockPosition = buf.readBlockPos();
      this.instrument = buf.readUnsignedByte();
      this.pitch = buf.readUnsignedByte();
      this.block = IRegistry.field_212618_g.get(buf.readVarInt());
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeBlockPos(this.blockPosition);
      buf.writeByte(this.instrument);
      buf.writeByte(this.pitch);
      buf.writeVarInt(IRegistry.field_212618_g.getId(this.block));
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleBlockAction(this);
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getBlockPosition() {
      return this.blockPosition;
   }

   /**
    * instrument data for noteblocks
    */
   @OnlyIn(Dist.CLIENT)
   public int getData1() {
      return this.instrument;
   }

   /**
    * pitch data for noteblocks
    */
   @OnlyIn(Dist.CLIENT)
   public int getData2() {
      return this.pitch;
   }

   @OnlyIn(Dist.CLIENT)
   public Block getBlockType() {
      return this.block;
   }
}