package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import java.util.BitSet;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public class UnderwaterCanyonWorldCarver extends CanyonWorldCarver {
   private final float[] field_203628_i = new float[1024];

   public UnderwaterCanyonWorldCarver() {
      this.terrainBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.SAND, Blocks.GRAVEL, Blocks.WATER, Blocks.LAVA, Blocks.OBSIDIAN, Blocks.AIR, Blocks.CAVE_AIR);
   }

   public boolean func_212246_a(IBlockReader p_212246_1_, Random p_212246_2_, int p_212246_3_, int p_212246_4_, ProbabilityConfig p_212246_5_) {
      return p_212246_2_.nextFloat() <= p_212246_5_.probability;
   }

   protected boolean carveAtTarget(IWorld worldIn, long seed, int mainChunkX, int mainChunkZ, double xRange, double yRange, double zRange, double placementXZBound, double placementYBound, BitSet mask) {
      Random random = new Random(seed + (long)mainChunkX + (long)mainChunkZ);
      double d0 = (double)(mainChunkX * 16 + 8);
      double d1 = (double)(mainChunkZ * 16 + 8);
      if (!(xRange < d0 - 16.0D - placementXZBound * 2.0D) && !(zRange < d1 - 16.0D - placementXZBound * 2.0D) && !(xRange > d0 + 16.0D + placementXZBound * 2.0D) && !(zRange > d1 + 16.0D + placementXZBound * 2.0D)) {
         int i = Math.max(MathHelper.floor(xRange - placementXZBound) - mainChunkX * 16 - 1, 0);
         int j = Math.min(MathHelper.floor(xRange + placementXZBound) - mainChunkX * 16 + 1, 16);
         int k = Math.max(MathHelper.floor(yRange - placementYBound) - 1, 1);
         int l = Math.min(MathHelper.floor(yRange + placementYBound) + 1, 248);
         int i1 = Math.max(MathHelper.floor(zRange - placementXZBound) - mainChunkZ * 16 - 1, 0);
         int j1 = Math.min(MathHelper.floor(zRange + placementXZBound) - mainChunkZ * 16 + 1, 16);
         if (i <= j && k <= l && i1 <= j1) {
            boolean flag = false;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for(int k1 = i; k1 < j; ++k1) {
               int l1 = k1 + mainChunkX * 16;
               double d2 = ((double)l1 + 0.5D - xRange) / placementXZBound;

               for(int i2 = i1; i2 < j1; ++i2) {
                  int j2 = i2 + mainChunkZ * 16;
                  double d3 = ((double)j2 + 0.5D - zRange) / placementXZBound;
                  if (d2 * d2 + d3 * d3 < 1.0D) {
                     for(int k2 = l; k2 > k; --k2) {
                        double d4 = ((double)(k2 - 1) + 0.5D - yRange) / placementYBound;
                        if ((d2 * d2 + d3 * d3) * (double)this.field_203628_i[k2 - 1] + d4 * d4 / 6.0D < 1.0D && k2 < worldIn.getSeaLevel()) {
                           int l2 = k1 | i2 << 4 | k2 << 8;
                           if (!mask.get(l2)) {
                              mask.set(l2);
                              blockpos$mutableblockpos.setPos(l1, k2, j2);
                              IBlockState iblockstate = worldIn.getBlockState(blockpos$mutableblockpos);
                              if (this.isTargetAllowed(iblockstate)) {
                                 if (k2 == 10) {
                                    float f = random.nextFloat();
                                    if ((double)f < 0.25D) {
                                       worldIn.setBlockState(blockpos$mutableblockpos, Blocks.MAGMA_BLOCK.getDefaultState(), 2);
                                       worldIn.getPendingBlockTicks().scheduleTick(blockpos$mutableblockpos, Blocks.MAGMA_BLOCK, 0);
                                       flag = true;
                                    } else {
                                       worldIn.setBlockState(blockpos$mutableblockpos, Blocks.OBSIDIAN.getDefaultState(), 2);
                                       flag = true;
                                    }
                                 } else if (k2 < 10) {
                                    worldIn.setBlockState(blockpos$mutableblockpos, Blocks.LAVA.getDefaultState(), 2);
                                 } else {
                                    boolean flag1 = false;

                                    for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                                       IBlockState iblockstate1 = worldIn.getBlockState(blockpos$mutableblockpos.setPos(l1 + enumfacing.getXOffset(), k2, j2 + enumfacing.getZOffset()));
                                       if (iblockstate1.isAir()) {
                                          worldIn.setBlockState(blockpos$mutableblockpos, WATER_FLUID.getBlockState(), 2);
                                          worldIn.getPendingFluidTicks().scheduleTick(blockpos$mutableblockpos, WATER_FLUID.getFluid(), 0);
                                          flag = true;
                                          flag1 = true;
                                          break;
                                       }
                                    }

                                    blockpos$mutableblockpos.setPos(l1, k2, j2);
                                    if (!flag1) {
                                       worldIn.setBlockState(blockpos$mutableblockpos, WATER_FLUID.getBlockState(), 2);
                                       flag = true;
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            return flag;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}