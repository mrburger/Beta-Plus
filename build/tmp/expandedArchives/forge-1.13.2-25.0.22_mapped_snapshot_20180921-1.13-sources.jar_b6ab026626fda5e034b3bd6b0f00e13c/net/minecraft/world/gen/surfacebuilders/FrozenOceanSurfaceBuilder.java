package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

public class FrozenOceanSurfaceBuilder implements ISurfaceBuilder<SurfaceBuilderConfig> {
   protected static final IBlockState PACKED_ICE = Blocks.PACKED_ICE.getDefaultState();
   protected static final IBlockState SNOW_BLOCK = Blocks.SNOW_BLOCK.getDefaultState();
   private static final IBlockState AIR = Blocks.AIR.getDefaultState();
   private static final IBlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
   private static final IBlockState ICE = Blocks.ICE.getDefaultState();
   private NoiseGeneratorPerlin field_205199_h;
   private NoiseGeneratorPerlin field_205200_i;
   private long field_205201_j;

   public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, IBlockState defaultBlock, IBlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
      double d0 = 0.0D;
      double d1 = 0.0D;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      float f = biomeIn.getTemperature(blockpos$mutableblockpos.setPos(x, 63, z));
      double d2 = Math.min(Math.abs(noise), this.field_205199_h.getValue((double)x * 0.1D, (double)z * 0.1D));
      if (d2 > 1.8D) {
         double d3 = 0.09765625D;
         double d4 = Math.abs(this.field_205200_i.getValue((double)x * 0.09765625D, (double)z * 0.09765625D));
         d0 = d2 * d2 * 1.2D;
         double d5 = Math.ceil(d4 * 40.0D) + 14.0D;
         if (d0 > d5) {
            d0 = d5;
         }

         if (f > 0.1F) {
            d0 -= 2.0D;
         }

         if (d0 > 2.0D) {
            d1 = (double)seaLevel - d0 - 7.0D;
            d0 = d0 + (double)seaLevel;
         } else {
            d0 = 0.0D;
         }
      }

      int k1 = x & 15;
      int i = z & 15;
      IBlockState iblockstate2 = biomeIn.getSurfaceBuilderConfig().getMiddle();
      IBlockState iblockstate = biomeIn.getSurfaceBuilderConfig().getTop();
      int l1 = (int)(noise / 3.0D + 3.0D + random.nextDouble() * 0.25D);
      int j = -1;
      int k = 0;
      int l = 2 + random.nextInt(4);
      int i1 = seaLevel + 18 + random.nextInt(10);

      for(int j1 = Math.max(startHeight, (int)d0 + 1); j1 >= 0; --j1) {
         blockpos$mutableblockpos.setPos(k1, j1, i);
         if (chunkIn.getBlockState(blockpos$mutableblockpos).isAir() && j1 < (int)d0 && random.nextDouble() > 0.01D) {
            chunkIn.setBlockState(blockpos$mutableblockpos, PACKED_ICE, false);
         } else if (chunkIn.getBlockState(blockpos$mutableblockpos).getMaterial() == Material.WATER && j1 > (int)d1 && j1 < seaLevel && d1 != 0.0D && random.nextDouble() > 0.15D) {
            chunkIn.setBlockState(blockpos$mutableblockpos, PACKED_ICE, false);
         }

         IBlockState iblockstate1 = chunkIn.getBlockState(blockpos$mutableblockpos);
         if (iblockstate1.isAir()) {
            j = -1;
         } else if (iblockstate1.getBlock() != defaultBlock.getBlock()) {
            if (iblockstate1.getBlock() == Blocks.PACKED_ICE && k <= l && j1 > i1) {
               chunkIn.setBlockState(blockpos$mutableblockpos, SNOW_BLOCK, false);
               ++k;
            }
         } else if (j == -1) {
            if (l1 <= 0) {
               iblockstate = AIR;
               iblockstate2 = defaultBlock;
            } else if (j1 >= seaLevel - 4 && j1 <= seaLevel + 1) {
               iblockstate = biomeIn.getSurfaceBuilderConfig().getTop();
               iblockstate2 = biomeIn.getSurfaceBuilderConfig().getMiddle();
            }

            if (j1 < seaLevel && (iblockstate == null || iblockstate.isAir())) {
               if (biomeIn.getTemperature(blockpos$mutableblockpos.setPos(x, j1, z)) < 0.15F) {
                  iblockstate = ICE;
               } else {
                  iblockstate = defaultFluid;
               }
            }

            j = l1;
            if (j1 >= seaLevel - 1) {
               chunkIn.setBlockState(blockpos$mutableblockpos, iblockstate, false);
            } else if (j1 < seaLevel - 7 - l1) {
               iblockstate = AIR;
               iblockstate2 = defaultBlock;
               chunkIn.setBlockState(blockpos$mutableblockpos, GRAVEL, false);
            } else {
               chunkIn.setBlockState(blockpos$mutableblockpos, iblockstate2, false);
            }
         } else if (j > 0) {
            --j;
            chunkIn.setBlockState(blockpos$mutableblockpos, iblockstate2, false);
            if (j == 0 && iblockstate2.getBlock() == Blocks.SAND && l1 > 1) {
               j = random.nextInt(4) + Math.max(0, j1 - 63);
               iblockstate2 = iblockstate2.getBlock() == Blocks.RED_SAND ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
            }
         }
      }

   }

   public void setSeed(long seed) {
      if (this.field_205201_j != seed || this.field_205199_h == null || this.field_205200_i == null) {
         Random random = new SharedSeedRandom(seed);
         this.field_205199_h = new NoiseGeneratorPerlin(random, 4);
         this.field_205200_i = new NoiseGeneratorPerlin(random, 1);
      }

      this.field_205201_j = seed;
   }
}