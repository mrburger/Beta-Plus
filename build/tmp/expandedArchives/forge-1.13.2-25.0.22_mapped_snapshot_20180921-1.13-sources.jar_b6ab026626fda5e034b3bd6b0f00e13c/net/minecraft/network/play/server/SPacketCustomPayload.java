package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketCustomPayload implements Packet<INetHandlerPlayClient>, net.minecraftforge.fml.network.ICustomPacket<SPacketCustomPayload> {
   /** Channel that lists trades. */
   public static final ResourceLocation TRADER_LIST = new ResourceLocation("minecraft:trader_list");
   /** Channel for the server brand. */
   public static final ResourceLocation BRAND = new ResourceLocation("minecraft:brand");
   /** Channel to open a book. */
   public static final ResourceLocation BOOK_OPEN = new ResourceLocation("minecraft:book_open");
   /**
    * Debug channel for pathfinding.
    *  
    * @see net.minecraft.client.renderer.debug.DebugRendererPathfinding
    */
   public static final ResourceLocation DEBUG_PATH = new ResourceLocation("minecraft:debug/path");
   /**
    * Debug channel for block updates.
    *  
    * @see net.minecraft.client.renderer.debug.DebugRendererNeighborsUpdate
    */
   public static final ResourceLocation DEBUG_NEIGHBORS_UPDATE = new ResourceLocation("minecraft:debug/neighbors_update");
   /**
    * Debug channel for caves.
    *  
    * @see net.minecraft.client.renderer.debug.DebugRendererCave
    */
   public static final ResourceLocation DEBUG_CAVES = new ResourceLocation("minecraft:debug/caves");
   /**
    * Debug channel for structures.
    *  
    * @see net.minecraft.client.renderer.debug.DebugRendererStructure
    */
   public static final ResourceLocation DEBUG_STRUCTURES = new ResourceLocation("minecraft:debug/structures");
   /**
    * Debug channel for world generation attempts.
    *  
    * @see net.minecraft.client.renderer.debug.DebugRendererWorldGenAttempts
    */
   public static final ResourceLocation DEBUG_WORLDGEN_ATTEMPT = new ResourceLocation("minecraft:debug/worldgen_attempt");
   private ResourceLocation channel;
   private PacketBuffer data;

   public SPacketCustomPayload() {
   }

   public SPacketCustomPayload(ResourceLocation channelIn, PacketBuffer dataIn) {
      this.channel = channelIn;
      this.data = dataIn;
      if (dataIn.writerIndex() > 1048576) {
         throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
      }
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.channel = buf.readResourceLocation();
      int i = buf.readableBytes();
      if (i >= 0 && i <= 1048576) {
         this.data = new PacketBuffer(buf.readBytes(i));
      } else {
         throw new IOException("Payload may not be larger than 1048576 bytes");
      }
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeResourceLocation(this.channel);
      buf.writeBytes(this.data.copy());
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleCustomPayload(this);
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getChannelName() {
      return this.channel;
   }

   @OnlyIn(Dist.CLIENT)
   public PacketBuffer getBufferData() {
      return new PacketBuffer(this.data.copy());
   }
}