package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketEditBook implements Packet<INetHandlerPlayServer> {
   private ItemStack field_210347_a;
   private boolean field_210348_b;
   private EnumHand field_212645_c;

   public CPacketEditBook() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketEditBook(ItemStack p_i49823_1_, boolean p_i49823_2_, EnumHand p_i49823_3_) {
      this.field_210347_a = p_i49823_1_.copy();
      this.field_210348_b = p_i49823_2_;
      this.field_212645_c = p_i49823_3_;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.field_210347_a = buf.readItemStack();
      this.field_210348_b = buf.readBoolean();
      this.field_212645_c = buf.readEnumValue(EnumHand.class);
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeItemStack(this.field_210347_a);
      buf.writeBoolean(this.field_210348_b);
      buf.writeEnumValue(this.field_212645_c);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(INetHandlerPlayServer handler) {
      handler.processEditBook(this);
   }

   public ItemStack func_210346_a() {
      return this.field_210347_a;
   }

   public boolean func_210345_b() {
      return this.field_210348_b;
   }

   public EnumHand func_212644_d() {
      return this.field_212645_c;
   }
}