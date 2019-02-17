package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.NoiseGeneratorOctaves;

public class NetherSurfaceBuilder implements ISurfaceBuilder<SurfaceBuilderConfig> {
   private static final IBlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
   private static final IBlockState NETHERRACK = Blocks.NETHERRACK.getDefaultState();
   private static final IBlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
   private static final IBlockState SOUL_SAND = Blocks.SOUL_SAND.getDefaultState();
   protected long field_205552_a;
   protected NoiseGeneratorOctaves field_205553_b;

   public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, IBlockState defaultBlock, IBlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
      int i = seaLevel + 1;
      int j = x & 15;
      int k = z & 15;
      double d0 = 0.03125D;
      boolean flag = this.field_205553_b.func_205563_a((double)x * 0.03125D, (double)z * 0.03125D, 0.0D) + random.nextDouble() * 0.2D > 0.0D;
      boolean flag1 = this.field_205553_b.func_205563_a((double)x * 0.03125D, 109.0D, (double)z * 0.03125D) + random.nextDouble() * 0.2D > 0.0D;
      int l = (int)(noise / 3.0D + 3.0D + random.nextDouble() * 0.25D);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      int i1 = -1;
      IBlockState iblockstate = NETHERRACK;
      IBlockState iblockstate1 = NETHERRACK;

      for(int j1 = 127; j1 >= 0; --j1) {
         blockpos$mutableblockpos.setPos(j, j1, k);
         IBlockState iblockstate2 = chunkIn.getBlockState(blockpos$mutableblockpos);
         if (iblockstate2.getBlock() != null && !iblockstate2.isAir()) {
            if (iblockstate2.getBlock() == defaultBlock.getBlock()) {
               if (i1 == -1) {
                  if (l <= 0) {
                     iblockstate = CAVE_AIR;
                     iblockstate1 = NETHERRACK;
                  } else if (j1 >= i - 4 && j1 <= i + 1) {
                     iblockstate = NETHERRACK;
                     iblockstate1 = NETHERRACK;
                     if (flag1) {
                        iblockstate = GRAVEL;
                        iblockstate1 = NETHERRACK;
                     }

                     if (flag) {
                        iblockstate = SOUL_SAND;
                        iblockstate1 = SOUL_SAND;
                     }
                  }

                  if (j1 < i && (iblockstate == null || iblockstate.isAir())) {
                     iblockstate = defaultFluid;
                  }

                  i1 = l;
                  if (j1 >= i - 1) {
                     chunkIn.setBlockState(blockpos$mutableblockpos, iblockstate, false);
                  } else {
                     chunkIn.setBlockState(blockpos$mutableblockpos, iblockstate1, false);
                  }
               } else if (i1 > 0) {
                  --i1;
                  chunkIn.setBlockState(blockpos$mutableblockpos, iblockstate1, false);
               }
            }
         } else {
            i1 = -1;
         }
      }

   }

   public void setSeed(long seed) {
      if (this.field_205552_a != seed || this.field_205553_b == null) {
         this.field_205553_b = new NoiseGeneratorOctaves(new SharedSeedRandom(seed), 4);
      }

      this.field_205552_a = seed;
   }
}