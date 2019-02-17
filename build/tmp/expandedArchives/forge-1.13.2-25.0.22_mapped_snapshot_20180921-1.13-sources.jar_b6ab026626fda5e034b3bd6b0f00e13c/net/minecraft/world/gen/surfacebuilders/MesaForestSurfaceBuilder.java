package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class MesaForestSurfaceBuilder extends MesaSurfaceBuilder {
   private static final IBlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
   private static final IBlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getDefaultState();
   private static final IBlockState TERRACOTTA = Blocks.TERRACOTTA.getDefaultState();

   public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, IBlockState defaultBlock, IBlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
      int i = x & 15;
      int j = z & 15;
      IBlockState iblockstate = WHITE_TERRACOTTA;
      IBlockState iblockstate1 = biomeIn.getSurfaceBuilderConfig().getMiddle();
      int k = (int)(noise / 3.0D + 3.0D + random.nextDouble() * 0.25D);
      boolean flag = Math.cos(noise / 3.0D * Math.PI) > 0.0D;
      int l = -1;
      boolean flag1 = false;
      int i1 = 0;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int j1 = startHeight; j1 >= 0; --j1) {
         if (i1 < 15) {
            blockpos$mutableblockpos.setPos(i, j1, j);
            IBlockState iblockstate2 = chunkIn.getBlockState(blockpos$mutableblockpos);
            if (iblockstate2.isAir()) {
               l = -1;
            } else if (iblockstate2.getBlock() == defaultBlock.getBlock()) {
               if (l == -1) {
                  flag1 = false;
                  if (k <= 0) {
                     iblockstate = Blocks.AIR.getDefaultState();
                     iblockstate1 = defaultBlock;
                  } else if (j1 >= seaLevel - 4 && j1 <= seaLevel + 1) {
                     iblockstate = WHITE_TERRACOTTA;
                     iblockstate1 = biomeIn.getSurfaceBuilderConfig().getMiddle();
                  }

                  if (j1 < seaLevel && (iblockstate == null || iblockstate.isAir())) {
                     iblockstate = defaultFluid;
                  }

                  l = k + Math.max(0, j1 - seaLevel);
                  if (j1 >= seaLevel - 1) {
                     if (j1 > 86 + k * 2) {
                        if (flag) {
                           chunkIn.setBlockState(blockpos$mutableblockpos, Blocks.COARSE_DIRT.getDefaultState(), false);
                        } else {
                           chunkIn.setBlockState(blockpos$mutableblockpos, Blocks.GRASS_BLOCK.getDefaultState(), false);
                        }
                     } else if (j1 > seaLevel + 3 + k) {
                        IBlockState iblockstate3;
                        if (j1 >= 64 && j1 <= 127) {
                           if (flag) {
                              iblockstate3 = TERRACOTTA;
                           } else {
                              iblockstate3 = this.func_202614_a(x, j1, z);
                           }
                        } else {
                           iblockstate3 = ORANGE_TERRACOTTA;
                        }

                        chunkIn.setBlockState(blockpos$mutableblockpos, iblockstate3, false);
                     } else {
                        chunkIn.setBlockState(blockpos$mutableblockpos, biomeIn.getSurfaceBuilderConfig().getTop(), false);
                        flag1 = true;
                     }
                  } else {
                     chunkIn.setBlockState(blockpos$mutableblockpos, iblockstate1, false);
                     if (iblockstate1.getBlock() == WHITE_TERRACOTTA) {
                        chunkIn.setBlockState(blockpos$mutableblockpos, ORANGE_TERRACOTTA, false);
                     }
                  }
               } else if (l > 0) {
                  --l;
                  if (flag1) {
                     chunkIn.setBlockState(blockpos$mutableblockpos, ORANGE_TERRACOTTA, false);
                  } else {
                     chunkIn.setBlockState(blockpos$mutableblockpos, this.func_202614_a(x, j1, z), false);
                  }
               }

               ++i1;
            }
         }
      }

   }
}