package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketPlaceGhostRecipe implements Packet<INetHandlerPlayClient> {
   private int field_194314_a;
   private ResourceLocation recipe;

   public SPacketPlaceGhostRecipe() {
   }

   public SPacketPlaceGhostRecipe(int p_i47615_1_, IRecipe p_i47615_2_) {
      this.field_194314_a = p_i47615_1_;
      this.recipe = p_i47615_2_.getId();
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation func_199615_a() {
      return this.recipe;
   }

   @OnlyIn(Dist.CLIENT)
   public int func_194313_b() {
      return this.field_194314_a;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.field_194314_a = buf.readByte();
      this.recipe = buf.readResourceLocation();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.field_194314_a);
      buf.writeResourceLocation(this.recipe);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(INetHandlerPlayClient handler) {
      handler.handlePlaceGhostRecipe(this);
   }
}