package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class CompositeSurfaceBuilder<C extends ISurfaceBuilderConfig> implements ISurfaceBuilder<SurfaceBuilderConfig> {
   private final ISurfaceBuilder<C> surfaceBuilder;
   private final C config;

   public CompositeSurfaceBuilder(ISurfaceBuilder<C> surfaceBuilderIn, C configIn) {
      this.surfaceBuilder = surfaceBuilderIn;
      this.config = configIn;
   }

   public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, IBlockState defaultBlock, IBlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
      this.surfaceBuilder.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, this.config);
   }

   public void setSeed(long seed) {
      this.surfaceBuilder.setSeed(seed);
   }

   public C getConfig() {
      return this.config;
   }
}