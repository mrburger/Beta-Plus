package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockStateMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class DesertWellsFeature extends Feature<NoFeatureConfig> {
   private static final BlockStateMatcher IS_SAND = BlockStateMatcher.forBlock(Blocks.SAND);
   private final IBlockState sandSlab = Blocks.SANDSTONE_SLAB.getDefaultState();
   private final IBlockState sandstone = Blocks.SANDSTONE.getDefaultState();
   private final IBlockState water = Blocks.WATER.getDefaultState();

   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      for(p_212245_4_ = p_212245_4_.up(); p_212245_1_.isAirBlock(p_212245_4_) && p_212245_4_.getY() > 2; p_212245_4_ = p_212245_4_.down()) {
         ;
      }

      if (!IS_SAND.test(p_212245_1_.getBlockState(p_212245_4_))) {
         return false;
      } else {
         for(int i = -2; i <= 2; ++i) {
            for(int j = -2; j <= 2; ++j) {
               if (p_212245_1_.isAirBlock(p_212245_4_.add(i, -1, j)) && p_212245_1_.isAirBlock(p_212245_4_.add(i, -2, j))) {
                  return false;
               }
            }
         }

         for(int l = -1; l <= 0; ++l) {
            for(int l1 = -2; l1 <= 2; ++l1) {
               for(int k = -2; k <= 2; ++k) {
                  p_212245_1_.setBlockState(p_212245_4_.add(l1, l, k), this.sandstone, 2);
               }
            }
         }

         p_212245_1_.setBlockState(p_212245_4_, this.water, 2);

         for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            p_212245_1_.setBlockState(p_212245_4_.offset(enumfacing), this.water, 2);
         }

         for(int i1 = -2; i1 <= 2; ++i1) {
            for(int i2 = -2; i2 <= 2; ++i2) {
               if (i1 == -2 || i1 == 2 || i2 == -2 || i2 == 2) {
                  p_212245_1_.setBlockState(p_212245_4_.add(i1, 1, i2), this.sandstone, 2);
               }
            }
         }

         p_212245_1_.setBlockState(p_212245_4_.add(2, 1, 0), this.sandSlab, 2);
         p_212245_1_.setBlockState(p_212245_4_.add(-2, 1, 0), this.sandSlab, 2);
         p_212245_1_.setBlockState(p_212245_4_.add(0, 1, 2), this.sandSlab, 2);
         p_212245_1_.setBlockState(p_212245_4_.add(0, 1, -2), this.sandSlab, 2);

         for(int j1 = -1; j1 <= 1; ++j1) {
            for(int j2 = -1; j2 <= 1; ++j2) {
               if (j1 == 0 && j2 == 0) {
                  p_212245_1_.setBlockState(p_212245_4_.add(j1, 4, j2), this.sandstone, 2);
               } else {
                  p_212245_1_.setBlockState(p_212245_4_.add(j1, 4, j2), this.sandSlab, 2);
               }
            }
         }

         for(int k1 = 1; k1 <= 3; ++k1) {
            p_212245_1_.setBlockState(p_212245_4_.add(-1, k1, -1), this.sandstone, 2);
            p_212245_1_.setBlockState(p_212245_4_.add(-1, k1, 1), this.sandstone, 2);
            p_212245_1_.setBlockState(p_212245_4_.add(1, k1, -1), this.sandstone, 2);
            p_212245_1_.setBlockState(p_212245_4_.add(1, k1, 1), this.sandstone, 2);
         }

         return true;
      }
   }
}