package net.minecraft.world.gen.surfacebuilders;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

public class MesaSurfaceBuilder implements ISurfaceBuilder<SurfaceBuilderConfig> {
   private static final IBlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
   private static final IBlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getDefaultState();
   private static final IBlockState TERRACOTTA = Blocks.TERRACOTTA.getDefaultState();
   private static final IBlockState YELLOW_TERRACOTTA = Blocks.YELLOW_TERRACOTTA.getDefaultState();
   private static final IBlockState BROWN_TERRACOTTA = Blocks.BROWN_TERRACOTTA.getDefaultState();
   private static final IBlockState RED_TERRACOTTA = Blocks.RED_TERRACOTTA.getDefaultState();
   private static final IBlockState LIGHT_GRAY_TERRACOTTA = Blocks.LIGHT_GRAY_TERRACOTTA.getDefaultState();
   protected IBlockState[] field_202615_a;
   protected long field_202616_b;
   protected NoiseGeneratorPerlin field_202617_c;
   protected NoiseGeneratorPerlin field_202618_d;
   protected NoiseGeneratorPerlin field_202619_e;

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
                     if (j1 > seaLevel + 3 + k) {
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
                     Block block = iblockstate1.getBlock();
                     if (block == Blocks.WHITE_TERRACOTTA || block == Blocks.ORANGE_TERRACOTTA || block == Blocks.MAGENTA_TERRACOTTA || block == Blocks.LIGHT_BLUE_TERRACOTTA || block == Blocks.YELLOW_TERRACOTTA || block == Blocks.LIME_TERRACOTTA || block == Blocks.PINK_TERRACOTTA || block == Blocks.GRAY_TERRACOTTA || block == Blocks.LIGHT_GRAY_TERRACOTTA || block == Blocks.CYAN_TERRACOTTA || block == Blocks.PURPLE_TERRACOTTA || block == Blocks.BLUE_TERRACOTTA || block == Blocks.BROWN_TERRACOTTA || block == Blocks.GREEN_TERRACOTTA || block == Blocks.RED_TERRACOTTA || block == Blocks.BLACK_TERRACOTTA) {
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

   public void setSeed(long seed) {
      if (this.field_202616_b != seed || this.field_202615_a == null) {
         this.func_202613_a(seed);
      }

      if (this.field_202616_b != seed || this.field_202617_c == null || this.field_202618_d == null) {
         Random random = new SharedSeedRandom(seed);
         this.field_202617_c = new NoiseGeneratorPerlin(random, 4);
         this.field_202618_d = new NoiseGeneratorPerlin(random, 1);
      }

      this.field_202616_b = seed;
   }

   protected void func_202613_a(long p_202613_1_) {
      this.field_202615_a = new IBlockState[64];
      Arrays.fill(this.field_202615_a, TERRACOTTA);
      Random random = new SharedSeedRandom(p_202613_1_);
      this.field_202619_e = new NoiseGeneratorPerlin(random, 1);

      for(int l1 = 0; l1 < 64; ++l1) {
         l1 += random.nextInt(5) + 1;
         if (l1 < 64) {
            this.field_202615_a[l1] = ORANGE_TERRACOTTA;
         }
      }

      int i2 = random.nextInt(4) + 2;

      for(int i = 0; i < i2; ++i) {
         int j = random.nextInt(3) + 1;
         int k = random.nextInt(64);

         for(int l = 0; k + l < 64 && l < j; ++l) {
            this.field_202615_a[k + l] = YELLOW_TERRACOTTA;
         }
      }

      int j2 = random.nextInt(4) + 2;

      for(int k2 = 0; k2 < j2; ++k2) {
         int i3 = random.nextInt(3) + 2;
         int l3 = random.nextInt(64);

         for(int i1 = 0; l3 + i1 < 64 && i1 < i3; ++i1) {
            this.field_202615_a[l3 + i1] = BROWN_TERRACOTTA;
         }
      }

      int l2 = random.nextInt(4) + 2;

      for(int j3 = 0; j3 < l2; ++j3) {
         int i4 = random.nextInt(3) + 1;
         int k4 = random.nextInt(64);

         for(int j1 = 0; k4 + j1 < 64 && j1 < i4; ++j1) {
            this.field_202615_a[k4 + j1] = RED_TERRACOTTA;
         }
      }

      int k3 = random.nextInt(3) + 3;
      int j4 = 0;

      for(int l4 = 0; l4 < k3; ++l4) {
         int i5 = 1;
         j4 += random.nextInt(16) + 4;

         for(int k1 = 0; j4 + k1 < 64 && k1 < 1; ++k1) {
            this.field_202615_a[j4 + k1] = WHITE_TERRACOTTA;
            if (j4 + k1 > 1 && random.nextBoolean()) {
               this.field_202615_a[j4 + k1 - 1] = LIGHT_GRAY_TERRACOTTA;
            }

            if (j4 + k1 < 63 && random.nextBoolean()) {
               this.field_202615_a[j4 + k1 + 1] = LIGHT_GRAY_TERRACOTTA;
            }
         }
      }

   }

   protected IBlockState func_202614_a(int p_202614_1_, int p_202614_2_, int p_202614_3_) {
      int i = (int)Math.round(this.field_202619_e.getValue((double)p_202614_1_ / 512.0D, (double)p_202614_3_ / 512.0D) * 2.0D);
      return this.field_202615_a[(p_202614_2_ + i + 64) % 64];
   }
}