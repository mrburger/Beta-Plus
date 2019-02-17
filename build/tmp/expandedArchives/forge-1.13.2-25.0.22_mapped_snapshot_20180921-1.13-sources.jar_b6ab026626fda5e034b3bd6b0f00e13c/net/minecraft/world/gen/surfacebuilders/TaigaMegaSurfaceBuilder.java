package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class TaigaMegaSurfaceBuilder implements ISurfaceBuilder<SurfaceBuilderConfig> {
   public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, IBlockState defaultBlock, IBlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
      if (noise > 1.75D) {
         Biome.DEFAULT_SURFACE_BUILDER.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, Biome.COARSE_DIRT_DIRT_GRAVEL_SURFACE);
      } else if (noise > -0.95D) {
         Biome.DEFAULT_SURFACE_BUILDER.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, Biome.PODZOL_DIRT_GRAVEL_SURFACE);
      } else {
         Biome.DEFAULT_SURFACE_BUILDER.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, Biome.GRASS_DIRT_GRAVEL_SURFACE);
      }

   }
}