package net.minecraft.world.gen.tasks;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.WorldGenRegion;

public class LiquidCarveChunkTask extends ChunkTask {
   protected ChunkPrimer run(ChunkStatus chunkStatusIn, World worldIn, IChunkGenerator<?> chunkGenerator, ChunkPrimer[] region, int x, int z) {
      chunkGenerator.carve(new WorldGenRegion(region, chunkStatusIn.getTaskRange() * 2 + 1, chunkStatusIn.getTaskRange() * 2 + 1, x, z, worldIn), GenerationStage.Carving.LIQUID);
      ChunkPrimer chunkprimer = region[region.length / 2];
      chunkprimer.createHeightMap(Heightmap.Type.OCEAN_FLOOR_WG, Heightmap.Type.WORLD_SURFACE_WG);
      chunkprimer.setStatus(ChunkStatus.LIQUID_CARVED);
      return chunkprimer;
   }
}