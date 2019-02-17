package net.minecraft.server.management;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerChunkMapEntry {
   private static final Logger LOGGER = LogManager.getLogger();
   private final PlayerChunkMap playerChunkMap;
   private final List<EntityPlayerMP> players = Lists.newArrayList();
   private final ChunkPos pos;
   private short[] changedBlocks = new short[64];
   @Nullable
   private Chunk chunk;
   private int changes;
   private int changedSectionFilter;
   private long lastUpdateInhabitedTime;
   private boolean sentToPlayers;
   /*private boolean loading = true;
   private final Runnable loadedRunnable;*/

   public PlayerChunkMapEntry(PlayerChunkMap mapIn, int chunkX, int chunkZ) {
      this.playerChunkMap = mapIn;
      this.pos = new ChunkPos(chunkX, chunkZ);
      ChunkProviderServer chunkproviderserver = mapIn.getWorld().getChunkProvider();
      chunkproviderserver.func_212469_a(chunkX, chunkZ);
      //this.loadedRunnable = () -> {
      this.chunk = chunkproviderserver.provideChunk(chunkX, chunkZ, true, false);
      /*this.loading = false;
      }; Forge: Pending if we find a better way to async load/gen chunks.
      mapIn.getWorld().getChunkProvider().loadChunk(chunkX, chunkZ, this.loadedRunnable);*/
   }

   public ChunkPos getPos() {
      return this.pos;
   }

   public void addPlayer(EntityPlayerMP player) {
      if (this.players.contains(player)) {
         LOGGER.debug("Failed to add player. {} already is in chunk {}, {}", player, this.pos.x, this.pos.z);
      } else {
         if (this.players.isEmpty()) {
            this.lastUpdateInhabitedTime = this.playerChunkMap.getWorld().getGameTime();
         }

         this.players.add(player);
         if (this.sentToPlayers) {
            this.sendToPlayer(player);
            // chunk watch event - the chunk is ready
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkWatchEvent.Watch(this.chunk, player));
         }

      }
   }

   public void removePlayer(EntityPlayerMP player) {
      if (this.players.contains(player)) {
         // If we haven't loaded yet don't load the chunk just so we can clean it up
         if (this.chunk == null) {
            this.players.remove(player);

            if (this.players.isEmpty()) {
               /*if (this.loading)
                  net.minecraftforge.common.chunkio.ChunkIOExecutor.dropQueuedChunkLoad(this.playerChunkMap.getWorld(), this.pos.x, this.pos.z, this.loadedRunnable);
               */
               this.playerChunkMap.removeEntry(this);
            }

            return;
         }

         if (this.sentToPlayers) {
            player.connection.sendPacket(new SPacketUnloadChunk(this.pos.x, this.pos.z));
         }

         this.players.remove(player);
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkWatchEvent.UnWatch(this.chunk, player));
         if (this.players.isEmpty()) {
            this.playerChunkMap.removeEntry(this);
         }

      }
   }

   /**
    * Provide the chunk at the player's location. Can fail, returning false, if the player is a spectator floating
    * outside of any pre-existing chunks, and the server is not configured to allow chunk generation for spectators.
    */
   public boolean providePlayerChunk(boolean canGenerate) {
      //if (this.loading) return false;
      if (this.chunk != null) {
         return true;
      } else {
         this.chunk = this.playerChunkMap.getWorld().getChunkProvider().provideChunk(this.pos.x, this.pos.z, true, canGenerate);
         return this.chunk != null;
      }
   }

   public boolean sendToPlayers() {
      if (this.sentToPlayers) {
         return true;
      } else if (this.chunk == null) {
         return false;
      } else if (!this.chunk.isPopulated()) {
         return false;
      } else {
         this.changes = 0;
         this.changedSectionFilter = 0;
         this.sentToPlayers = true;
         if (!this.players.isEmpty()) {
            Packet<?> packet = new SPacketChunkData(this.chunk, 65535);

            for(EntityPlayerMP entityplayermp : this.players) {
               entityplayermp.connection.sendPacket(packet);
               this.playerChunkMap.getWorld().getEntityTracker().sendLeashedEntitiesInChunk(entityplayermp, this.chunk);
               // chunk watch event - delayed to here as the chunk wasn't ready in addPlayer
               net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkWatchEvent.Watch(this.chunk, entityplayermp));
            }
         }

         return true;
      }
   }

   /**
    * Fully resyncs this chunk's blocks, tile entities, and entity attachments (passengers and leashes) to all tracking
    * players
    */
   public void sendToPlayer(EntityPlayerMP player) {
      if (this.sentToPlayers) {
         player.connection.sendPacket(new SPacketChunkData(this.chunk, 65535));
         this.playerChunkMap.getWorld().getEntityTracker().sendLeashedEntitiesInChunk(player, this.chunk);
      }
   }

   public void updateChunkInhabitedTime() {
      long i = this.playerChunkMap.getWorld().getGameTime();
      if (this.chunk != null) {
         this.chunk.setInhabitedTime(this.chunk.getInhabitedTime() + i - this.lastUpdateInhabitedTime);
      }

      this.lastUpdateInhabitedTime = i;
   }

   public void blockChanged(int x, int y, int z) {
      if (this.sentToPlayers) {
         if (this.changes == 0) {
            this.playerChunkMap.entryChanged(this);
         }

         this.changedSectionFilter |= 1 << (y >> 4);
         { //Forge; Cache everything, so always run
            short short1 = (short)(x << 12 | z << 8 | y);

            for(int i = 0; i < this.changes; ++i) {
               if (this.changedBlocks[i] == short1) {
                  return;
               }
            }

            if (this.changes == this.changedBlocks.length)
                this.changedBlocks = java.util.Arrays.copyOf(this.changedBlocks, this.changedBlocks.length << 1);
            this.changedBlocks[this.changes++] = short1;
         }

      }
   }

   public void sendPacket(Packet<?> packetIn) {
      if (this.sentToPlayers) {
         for(int i = 0; i < this.players.size(); ++i) {
            (this.players.get(i)).connection.sendPacket(packetIn);
         }

      }
   }

   public void tick() {
      if (this.sentToPlayers && this.chunk != null) {
         if (this.changes != 0) {
            if (this.changes == 1) {
               int i = (this.changedBlocks[0] >> 12 & 15) + this.pos.x * 16;
               int j = this.changedBlocks[0] & 255;
               int k = (this.changedBlocks[0] >> 8 & 15) + this.pos.z * 16;
               BlockPos blockpos = new BlockPos(i, j, k);
               this.sendPacket(new SPacketBlockChange(this.playerChunkMap.getWorld(), blockpos));
               if (this.playerChunkMap.getWorld().getBlockState(blockpos).hasTileEntity()) {
                  this.sendBlockEntity(this.playerChunkMap.getWorld().getTileEntity(blockpos));
               }
            } else if (this.changes >= net.minecraftforge.common.ForgeConfig.SERVER.clumpingThreshold.get()) {
               this.sendPacket(new SPacketChunkData(this.chunk, this.changedSectionFilter));
               //TODO: Fix Mojang's fuckup to modded by combining all TE data into the chunk data packet... seriously... packet size explosion!
            } else {
               this.sendPacket(new SPacketMultiBlockChange(this.changes, this.changedBlocks, this.chunk));
            //} Keep this in the else until we figure out a fix for mojang's derpitude on the data packet so we don't double send crap.
            //{// Forge: Send only the tile entities that are updated, Adding this brace lets us keep the indent and the patch small
               for(int l = 0; l < this.changes; ++l) {
                  int i1 = (this.changedBlocks[l] >> 12 & 15) + this.pos.x * 16;
                  int j1 = this.changedBlocks[l] & 255;
                  int k1 = (this.changedBlocks[l] >> 8 & 15) + this.pos.z * 16;
                  BlockPos blockpos1 = new BlockPos(i1, j1, k1);
                  if (this.playerChunkMap.getWorld().getBlockState(blockpos1).hasTileEntity()) {
                     this.sendBlockEntity(this.playerChunkMap.getWorld().getTileEntity(blockpos1));
                  }
               }
            }

            this.changes = 0;
            this.changedSectionFilter = 0;
         }
      }
   }

   private void sendBlockEntity(@Nullable TileEntity be) {
      if (be != null) {
         SPacketUpdateTileEntity spacketupdatetileentity = be.getUpdatePacket();
         if (spacketupdatetileentity != null) {
            this.sendPacket(spacketupdatetileentity);
         }
      }

   }

   public boolean containsPlayer(EntityPlayerMP player) {
      return this.players.contains(player);
   }

   public boolean hasPlayerMatching(Predicate<EntityPlayerMP> predicate) {
      return this.players.stream().anyMatch(predicate);
   }

   public boolean hasPlayerMatchingInRange(double range, Predicate<EntityPlayerMP> predicate) {
      int i = 0;

      for(int j = this.players.size(); i < j; ++i) {
         EntityPlayerMP entityplayermp = this.players.get(i);
         if (predicate.test(entityplayermp) && this.pos.getDistanceSq(entityplayermp) < range * range) {
            return true;
         }
      }

      return false;
   }

   public boolean isSentToPlayers() {
      return this.sentToPlayers;
   }

   @Nullable
   public Chunk getChunk() {
      return this.chunk;
   }

   public double getClosestPlayerDistance() {
      double d0 = Double.MAX_VALUE;

      for(EntityPlayerMP entityplayermp : this.players) {
         double d1 = this.pos.getDistanceSq(entityplayermp);
         if (d1 < d0) {
            d0 = d1;
         }
      }

      return d0;
   }

   public List<EntityPlayerMP> getWatchingPlayers() {
      return isSentToPlayers() ? java.util.Collections.unmodifiableList(players) : java.util.Collections.emptyList();
   }
}