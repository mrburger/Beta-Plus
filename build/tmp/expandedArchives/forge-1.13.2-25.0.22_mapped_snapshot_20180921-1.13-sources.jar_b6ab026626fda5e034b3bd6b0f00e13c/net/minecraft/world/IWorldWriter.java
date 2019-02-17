package net.minecraft.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public interface IWorldWriter {
   /**
    * Sets a block state into this world.Flags are as follows:
    * 1 will cause a block update.
    * 2 will send the change to clients.
    * 4 will prevent the block from being re-rendered.
    * 8 will force any re-renders to run on the main thread instead
    * 16 will prevent neighbor reactions (e.g. fences connecting, observers pulsing).
    * 32 will prevent neighbor reactions from spawning drops.
    * 64 will signify the block is being moved.
    * Flags can be OR-ed
    */
   boolean setBlockState(BlockPos pos, IBlockState newState, int flags);

   /**
    * Called when an entity is spawned in the world. This includes players.
    */
   boolean spawnEntity(Entity entityIn);

   boolean removeBlock(BlockPos pos);

   void setLightFor(EnumLightType type, BlockPos pos, int lightValue);

   /**
    * Sets a block to air, but also plays the sound and particles and can spawn drops
    */
   boolean destroyBlock(BlockPos pos, boolean dropBlock);
}