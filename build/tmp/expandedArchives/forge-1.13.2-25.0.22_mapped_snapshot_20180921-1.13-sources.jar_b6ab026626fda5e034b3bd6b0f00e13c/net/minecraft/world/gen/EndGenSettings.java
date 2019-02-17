package net.minecraft.world.gen;

import net.minecraft.util.math.BlockPos;

public class EndGenSettings extends ChunkGenSettings {
   private BlockPos spawnPos;

   public EndGenSettings setSpawnPos(BlockPos pos) {
      this.spawnPos = pos;
      return this;
   }

   public BlockPos getSpawnPos() {
      return this.spawnPos;
   }
}