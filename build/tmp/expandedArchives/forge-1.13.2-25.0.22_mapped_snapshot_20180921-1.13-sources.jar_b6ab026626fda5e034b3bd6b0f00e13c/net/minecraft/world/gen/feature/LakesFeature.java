package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class LakesFeature extends Feature<LakesConfig> {
   private static final IBlockState field_205188_a = Blocks.CAVE_AIR.getDefaultState();

   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, LakesConfig p_212245_5_) {
      while(p_212245_4_.getY() > 5 && p_212245_1_.isAirBlock(p_212245_4_)) {
         p_212245_4_ = p_212245_4_.down();
      }

      if (p_212245_4_.getY() <= 4) {
         return false;
      } else {
         p_212245_4_ = p_212245_4_.down(4);
         boolean[] aboolean = new boolean[2048];
         int i = p_212245_3_.nextInt(4) + 4;

         for(int j = 0; j < i; ++j) {
            double d0 = p_212245_3_.nextDouble() * 6.0D + 3.0D;
            double d1 = p_212245_3_.nextDouble() * 4.0D + 2.0D;
            double d2 = p_212245_3_.nextDouble() * 6.0D + 3.0D;
            double d3 = p_212245_3_.nextDouble() * (16.0D - d0 - 2.0D) + 1.0D + d0 / 2.0D;
            double d4 = p_212245_3_.nextDouble() * (8.0D - d1 - 4.0D) + 2.0D + d1 / 2.0D;
            double d5 = p_212245_3_.nextDouble() * (16.0D - d2 - 2.0D) + 1.0D + d2 / 2.0D;

            for(int l = 1; l < 15; ++l) {
               for(int i1 = 1; i1 < 15; ++i1) {
                  for(int j1 = 1; j1 < 7; ++j1) {
                     double d6 = ((double)l - d3) / (d0 / 2.0D);
                     double d7 = ((double)j1 - d4) / (d1 / 2.0D);
                     double d8 = ((double)i1 - d5) / (d2 / 2.0D);
                     double d9 = d6 * d6 + d7 * d7 + d8 * d8;
                     if (d9 < 1.0D) {
                        aboolean[(l * 16 + i1) * 8 + j1] = true;
                     }
                  }
               }
            }
         }

         for(int k1 = 0; k1 < 16; ++k1) {
            for(int l2 = 0; l2 < 16; ++l2) {
               for(int k = 0; k < 8; ++k) {
                  boolean flag = !aboolean[(k1 * 16 + l2) * 8 + k] && (k1 < 15 && aboolean[((k1 + 1) * 16 + l2) * 8 + k] || k1 > 0 && aboolean[((k1 - 1) * 16 + l2) * 8 + k] || l2 < 15 && aboolean[(k1 * 16 + l2 + 1) * 8 + k] || l2 > 0 && aboolean[(k1 * 16 + (l2 - 1)) * 8 + k] || k < 7 && aboolean[(k1 * 16 + l2) * 8 + k + 1] || k > 0 && aboolean[(k1 * 16 + l2) * 8 + (k - 1)]);
                  if (flag) {
                     Material material = p_212245_1_.getBlockState(p_212245_4_.add(k1, k, l2)).getMaterial();
                     if (k >= 4 && material.isLiquid()) {
                        return false;
                     }

                     if (k < 4 && !material.isSolid() && p_212245_1_.getBlockState(p_212245_4_.add(k1, k, l2)).getBlock() != p_212245_5_.field_202438_a) {
                        return false;
                     }
                  }
               }
            }
         }

         for(int l1 = 0; l1 < 16; ++l1) {
            for(int i3 = 0; i3 < 16; ++i3) {
               for(int i4 = 0; i4 < 8; ++i4) {
                  if (aboolean[(l1 * 16 + i3) * 8 + i4]) {
                     p_212245_1_.setBlockState(p_212245_4_.add(l1, i4, i3), i4 >= 4 ? field_205188_a : p_212245_5_.field_202438_a.getDefaultState(), 2);
                  }
               }
            }
         }

         for(int i2 = 0; i2 < 16; ++i2) {
            for(int j3 = 0; j3 < 16; ++j3) {
               for(int j4 = 4; j4 < 8; ++j4) {
                  if (aboolean[(i2 * 16 + j3) * 8 + j4]) {
                     BlockPos blockpos = p_212245_4_.add(i2, j4 - 1, j3);
                     if (Block.isDirt(p_212245_1_.getBlockState(blockpos).getBlock()) && p_212245_1_.getLightFor(EnumLightType.SKY, p_212245_4_.add(i2, j4, j3)) > 0) {
                        Biome biome = p_212245_1_.getBiome(blockpos);
                        if (biome.getSurfaceBuilderConfig().getTop().getBlock() == Blocks.MYCELIUM) {
                           p_212245_1_.setBlockState(blockpos, Blocks.MYCELIUM.getDefaultState(), 2);
                        } else {
                           p_212245_1_.setBlockState(blockpos, Blocks.GRASS_BLOCK.getDefaultState(), 2);
                        }
                     }
                  }
               }
            }
         }

         if (p_212245_5_.field_202438_a.getDefaultState().getMaterial() == Material.LAVA) {
            for(int j2 = 0; j2 < 16; ++j2) {
               for(int k3 = 0; k3 < 16; ++k3) {
                  for(int k4 = 0; k4 < 8; ++k4) {
                     boolean flag1 = !aboolean[(j2 * 16 + k3) * 8 + k4] && (j2 < 15 && aboolean[((j2 + 1) * 16 + k3) * 8 + k4] || j2 > 0 && aboolean[((j2 - 1) * 16 + k3) * 8 + k4] || k3 < 15 && aboolean[(j2 * 16 + k3 + 1) * 8 + k4] || k3 > 0 && aboolean[(j2 * 16 + (k3 - 1)) * 8 + k4] || k4 < 7 && aboolean[(j2 * 16 + k3) * 8 + k4 + 1] || k4 > 0 && aboolean[(j2 * 16 + k3) * 8 + (k4 - 1)]);
                     if (flag1 && (k4 < 4 || p_212245_3_.nextInt(2) != 0) && p_212245_1_.getBlockState(p_212245_4_.add(j2, k4, k3)).getMaterial().isSolid()) {
                        p_212245_1_.setBlockState(p_212245_4_.add(j2, k4, k3), Blocks.STONE.getDefaultState(), 2);
                     }
                  }
               }
            }
         }

         if (p_212245_5_.field_202438_a.getDefaultState().getMaterial() == Material.WATER) {
            for(int k2 = 0; k2 < 16; ++k2) {
               for(int l3 = 0; l3 < 16; ++l3) {
                  int l4 = 4;
                  BlockPos blockpos1 = p_212245_4_.add(k2, 4, l3);
                  if (p_212245_1_.getBiome(blockpos1).doesWaterFreeze(p_212245_1_, blockpos1, false)) {
                     p_212245_1_.setBlockState(blockpos1, Blocks.ICE.getDefaultState(), 2);
                  }
               }
            }
         }

         return true;
      }
   }
}