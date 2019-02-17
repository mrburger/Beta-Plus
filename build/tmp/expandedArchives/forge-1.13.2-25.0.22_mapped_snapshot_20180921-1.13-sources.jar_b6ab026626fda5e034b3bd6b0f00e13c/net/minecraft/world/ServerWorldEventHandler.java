package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.particles.IParticleData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

public class ServerWorldEventHandler implements IWorldEventListener {
   /** Reference to the MinecraftServer object. */
   private final MinecraftServer server;
   /** The WorldServer object. */
   private final WorldServer world;

   public ServerWorldEventHandler(MinecraftServer mcServerIn, WorldServer worldServerIn) {
      this.server = mcServerIn;
      this.world = worldServerIn;
   }

   public void addParticle(IParticleData particleData, boolean alwaysRender, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
   }

   public void addParticle(IParticleData particleData, boolean ignoreRange, boolean minimizeLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
   }

   /**
    * Called on all IWorldAccesses when an entity is created or loaded. On client worlds, starts downloading any
    * necessary textures. On server worlds, adds the entity to the entity tracker.
    */
   public void onEntityAdded(Entity entityIn) {
      this.world.getEntityTracker().track(entityIn);
      if (entityIn instanceof EntityPlayerMP) {
         this.world.dimension.onPlayerAdded((EntityPlayerMP)entityIn);
      }

   }

   /**
    * Called on all IWorldAccesses when an entity is unloaded or destroyed. On client worlds, releases any downloaded
    * textures. On server worlds, removes the entity from the entity tracker.
    */
   public void onEntityRemoved(Entity entityIn) {
      this.world.getEntityTracker().untrack(entityIn);
      this.world.getScoreboard().removeEntity(entityIn);
      if (entityIn instanceof EntityPlayerMP) {
         this.world.dimension.onPlayerRemoved((EntityPlayerMP)entityIn);
      }

   }

   public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {
      this.server.getPlayerList().sendToAllNearExcept(player, x, y, z, volume > 1.0F ? (double)(16.0F * volume) : 16.0D, this.world.dimension.getType(), new SPacketSoundEffect(soundIn, category, x, y, z, volume, pitch));
   }

   /**
    * On the client, re-renders all blocks in this range, inclusive. On the server, does nothing.
    */
   public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
   }

   public void notifyBlockUpdate(IBlockReader worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
      this.world.getPlayerChunkMap().markBlockForUpdate(pos);
   }

   public void notifyLightSet(BlockPos pos) {
   }

   public void playRecord(SoundEvent soundIn, BlockPos pos) {
   }

   public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {
      this.server.getPlayerList().sendToAllNearExcept(player, (double)blockPosIn.getX(), (double)blockPosIn.getY(), (double)blockPosIn.getZ(), 64.0D, this.world.dimension.getType(), new SPacketEffect(type, blockPosIn, data, false));
   }

   public void broadcastSound(int soundID, BlockPos pos, int data) {
      this.server.getPlayerList().sendPacketToAllPlayers(new SPacketEffect(soundID, pos, data, true));
   }

   public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
      for(EntityPlayerMP entityplayermp : this.server.getPlayerList().getPlayers()) {
         if (entityplayermp != null && entityplayermp.world == this.world && entityplayermp.getEntityId() != breakerId) {
            double d0 = (double)pos.getX() - entityplayermp.posX;
            double d1 = (double)pos.getY() - entityplayermp.posY;
            double d2 = (double)pos.getZ() - entityplayermp.posZ;
            if (d0 * d0 + d1 * d1 + d2 * d2 < 1024.0D) {
               entityplayermp.connection.sendPacket(new SPacketBlockBreakAnim(breakerId, pos, progress));
            }
         }
      }

   }
}