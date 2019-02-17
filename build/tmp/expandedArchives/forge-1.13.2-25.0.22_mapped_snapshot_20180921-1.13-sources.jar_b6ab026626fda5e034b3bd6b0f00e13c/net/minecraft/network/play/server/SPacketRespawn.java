package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketRespawn implements Packet<INetHandlerPlayClient> {
   private DimensionType dimensionID;
   private EnumDifficulty difficulty;
   private GameType gameType;
   private WorldType worldType;

   public SPacketRespawn() {
   }

   public SPacketRespawn(DimensionType p_i49824_1_, EnumDifficulty p_i49824_2_, WorldType p_i49824_3_, GameType p_i49824_4_) {
      this.dimensionID = p_i49824_1_;
      this.difficulty = p_i49824_2_;
      this.gameType = p_i49824_4_;
      this.worldType = p_i49824_3_;
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleRespawn(this);
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.dimensionID = DimensionType.getById(buf.readInt());
      this.difficulty = EnumDifficulty.byId(buf.readUnsignedByte());
      this.gameType = GameType.getByID(buf.readUnsignedByte());
      this.worldType = WorldType.byName(buf.readString(16));
      if (this.worldType == null) {
         this.worldType = WorldType.DEFAULT;
      }

   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeInt(this.dimensionID.getId());
      buf.writeByte(this.difficulty.getId());
      buf.writeByte(this.gameType.getID());
      buf.writeString(this.worldType.getName());
   }

   @OnlyIn(Dist.CLIENT)
   public DimensionType func_212643_b() {
      return this.dimensionID;
   }

   @OnlyIn(Dist.CLIENT)
   public EnumDifficulty getDifficulty() {
      return this.difficulty;
   }

   @OnlyIn(Dist.CLIENT)
   public GameType getGameType() {
      return this.gameType;
   }

   @OnlyIn(Dist.CLIENT)
   public WorldType getWorldType() {
      return this.worldType;
   }
}