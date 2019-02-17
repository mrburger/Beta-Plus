package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketBlockChange implements Packet<INetHandlerPlayClient> {
   private BlockPos pos;
   private IBlockState state;

   public SPacketBlockChange() {
   }

   public SPacketBlockChange(IBlockReader p_i48982_1_, BlockPos pos) {
      this.pos = pos;
      this.state = p_i48982_1_.getBlockState(pos);
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.pos = buf.readBlockPos();
      this.state = Block.BLOCK_STATE_IDS.getByValue(buf.readVarInt());
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeBlockPos(this.pos);
      buf.writeVarInt(Block.getStateId(this.state));
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleBlockChange(this);
   }

   @OnlyIn(Dist.CLIENT)
   public IBlockState getState() {
      return this.state;
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getPos() {
      return this.pos;
   }
}