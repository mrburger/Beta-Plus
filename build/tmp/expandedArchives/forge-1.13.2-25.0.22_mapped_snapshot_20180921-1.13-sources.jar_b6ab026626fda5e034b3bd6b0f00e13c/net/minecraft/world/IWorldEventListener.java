package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

public interface IWorldEventListener {
   void notifyBlockUpdate(IBlockReader worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags);

   void notifyLightSet(BlockPos pos);

   /**
    * On the client, re-renders all blocks in this range, inclusive. On the server, does nothing.
    */
   void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2);

   void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch);

   void playRecord(SoundEvent soundIn, BlockPos pos);

   void addParticle(IParticleData particleData, boolean alwaysRender, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed);

   void addParticle(IParticleData particleData, boolean ignoreRange, boolean minimizeLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed);

   /**
    * Called on all IWorldAccesses when an entity is created or loaded. On client worlds, starts downloading any
    * necessary textures. On server worlds, adds the entity to the entity tracker.
    */
   void onEntityAdded(Entity entityIn);

   /**
    * Called on all IWorldAccesses when an entity is unloaded or destroyed. On client worlds, releases any downloaded
    * textures. On server worlds, removes the entity from the entity tracker.
    */
   void onEntityRemoved(Entity entityIn);

   void broadcastSound(int soundID, BlockPos pos, int data);

   void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data);

   void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress);
}