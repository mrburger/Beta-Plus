package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import java.util.BitSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.gen.feature.IFeatureConfig;

public abstract class WorldCarver<C extends IFeatureConfig> implements IWorldCarver<C> {
   protected static final IBlockState DEFAULT_AIR = Blocks.AIR.getDefaultState();
   protected static final IBlockState DEFAULT_CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
   protected static final IFluidState WATER_FLUID = Fluids.WATER.getDefaultState();
   protected static final IFluidState LAVA_FLUID = Fluids.LAVA.getDefaultState();
   protected Set<Block> terrainBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.PACKED_ICE);
   protected Set<Fluid> terrainFluids = ImmutableSet.of(Fluids.WATER);

   public int func_202520_b() {
      return 4;
   }

   protected abstract boolean carveAtTarget(IWorld worldIn, long seed, int mainChunkX, int mainChunkZ, double xRange, double yRange, double zRange, double placementXZBound, double placementYBound, BitSet mask);

   protected boolean isTargetAllowed(IBlockState target) {
      return this.terrainBlocks.contains(target.getBlock());
   }

   protected boolean isTargetSafeFromFalling(IBlockState targetState, IBlockState stateAboveTarget) {
      Block block = targetState.getBlock();
      return this.isTargetAllowed(targetState) || (block == Blocks.SAND || block == Blocks.GRAVEL) && !stateAboveTarget.getFluidState().isTagged(FluidTags.WATER);
   }

   protected boolean doesAreaHaveFluids(IWorldReaderBase worldIn, int mainChunkX, int mainChunkZ, int minXPos, int maxXPos, int minYPos, int maxYPos, int minZPos, int maxZPos) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int i = minXPos; i < maxXPos; ++i) {
         for(int j = minZPos; j < maxZPos; ++j) {
            for(int k = minYPos - 1; k <= maxYPos + 1; ++k) {
               if (this.terrainFluids.contains(worldIn.getFluidState(blockpos$mutableblockpos.setPos(i + mainChunkX * 16, k, j + mainChunkZ * 16)).getFluid())) {
                  return true;
               }

               if (k != maxYPos + 1 && !this.isInBounds(minXPos, maxXPos, minZPos, maxZPos, i, j)) {
                  k = maxYPos;
               }
            }
         }
      }

      return false;
   }

   private boolean isInBounds(int minXPos, int maxXPos, int minZPos, int maxZPos, int xPos, int zPos) {
      return xPos == minXPos || xPos == maxXPos - 1 || zPos == minZPos || zPos == maxZPos - 1;
   }

   protected boolean isWithinGenerationDepth(int mainChunkX, int mainChunkZ, double xRange, double rangeZ, int currentDepth, int maxDepth, float p_202515_9_) {
      double d0 = (double)(mainChunkX * 16 + 8);
      double d1 = (double)(mainChunkZ * 16 + 8);
      double d2 = xRange - d0;
      double d3 = rangeZ - d1;
      double d4 = (double)(maxDepth - currentDepth);
      double d5 = (double)(p_202515_9_ + 2.0F + 16.0F);
      return d2 * d2 + d3 * d3 - d4 * d4 <= d5 * d5;
   }
}