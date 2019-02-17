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

public class SPacketJoinGame implements Packet<INetHandlerPlayClient> {
   private int playerId;
   private boolean hardcoreMode;
   private GameType gameType;
   private DimensionType dimension;
   private EnumDifficulty difficulty;
   private int maxPlayers;
   private WorldType worldType;
   private boolean reducedDebugInfo;

   public SPacketJoinGame() {
   }

   public SPacketJoinGame(int p_i49826_1_, GameType p_i49826_2_, boolean p_i49826_3_, DimensionType p_i49826_4_, EnumDifficulty p_i49826_5_, int p_i49826_6_, WorldType p_i49826_7_, boolean p_i49826_8_) {
      this.playerId = p_i49826_1_;
      this.dimension = p_i49826_4_;
      this.difficulty = p_i49826_5_;
      this.gameType = p_i49826_2_;
      this.maxPlayers = p_i49826_6_;
      this.hardcoreMode = p_i49826_3_;
      this.worldType = p_i49826_7_;
      this.reducedDebugInfo = p_i49826_8_;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.playerId = buf.readInt();
      int i = buf.readUnsignedByte();
      this.hardcoreMode = (i & 8) == 8;
      i = i & -9;
      this.gameType = GameType.getByID(i);
      this.dimension = DimensionType.getById(buf.readInt());
      this.difficulty = EnumDifficulty.byId(buf.readUnsignedByte());
      this.maxPlayers = buf.readUnsignedByte();
      this.worldType = WorldType.byName(buf.readString(16));
      if (this.worldType == null) {
         this.worldType = WorldType.DEFAULT;
      }

      this.reducedDebugInfo = buf.readBoolean();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeInt(this.playerId);
      int i = this.gameType.getID();
      if (this.hardcoreMode) {
         i |= 8;
      }

      buf.writeByte(i);
      buf.writeInt(this.dimension.getId());
      buf.writeByte(this.difficulty.getId());
      buf.writeByte(this.maxPlayers);
      buf.writeString(this.worldType.getName());
      buf.writeBoolean(this.reducedDebugInfo);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleJoinGame(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getPlayerId() {
      return this.playerId;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isHardcoreMode() {
      return this.hardcoreMode;
   }

   @OnlyIn(Dist.CLIENT)
   public GameType getGameType() {
      return this.gameType;
   }

   @OnlyIn(Dist.CLIENT)
   public DimensionType func_212642_e() {
      return this.dimension;
   }

   @OnlyIn(Dist.CLIENT)
   public EnumDifficulty getDifficulty() {
      return this.difficulty;
   }

   @OnlyIn(Dist.CLIENT)
   public WorldType getWorldType() {
      return this.worldType;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isReducedDebugInfo() {
      return this.reducedDebugInfo;
   }
}