package net.minecraft.world;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IWorld extends IWorldReaderBase, ISaveDataAccess, IWorldWriter {
   /**
    * gets the random world seed
    */
   long getSeed();

   /**
    * gets the current fullness of the moon expressed as a float between 1.0 and 0.0, in steps of .25
    */
   default float getCurrentMoonPhaseFactor() {
      return this.getDimension().getCurrentMoonPhaseFactor(this.getWorld().getDayTime());
   }

   /**
    * calls calculateCelestialAngle
    */
   default float getCelestialAngle(float partialTicks) {
      return this.getDimension().calculateCelestialAngle(this.getWorld().getDayTime(), partialTicks);
   }

   @OnlyIn(Dist.CLIENT)
   default int getMoonPhase() {
      return this.getDimension().getMoonPhase(this.getWorld().getDayTime());
   }

   ITickList<Block> getPendingBlockTicks();

   ITickList<Fluid> getPendingFluidTicks();

   default IChunk getChunkDefault(BlockPos pos) {
      return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
   }

   /**
    * Gets the chunk at the specified location.
    */
   IChunk getChunk(int chunkX, int chunkZ);

   World getWorld();

   /**
    * Returns the world's WorldInfo object
    */
   WorldInfo getWorldInfo();

   DifficultyInstance getDifficultyForLocation(BlockPos pos);

   default EnumDifficulty getDifficulty() {
      return this.getWorldInfo().getDifficulty();
   }

   /**
    * gets the world's chunk provider
    */
   IChunkProvider getChunkProvider();

   /**
    * Returns this world's current save handler
    */
   ISaveHandler getSaveHandler();

   Random getRandom();

   void notifyNeighbors(BlockPos pos, Block blockIn);

   /**
    * Gets the spawn point in the world
    */
   BlockPos getSpawnPoint();

   /**
    * Plays the specified sound for a player at the center of the given block position.
    */
   void playSound(@Nullable EntityPlayer player, BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch);

   void spawnParticle(IParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed);
}