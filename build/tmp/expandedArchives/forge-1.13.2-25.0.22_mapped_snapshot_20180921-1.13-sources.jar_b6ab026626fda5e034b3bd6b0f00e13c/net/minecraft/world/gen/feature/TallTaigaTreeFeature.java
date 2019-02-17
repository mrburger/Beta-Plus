package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class TallTaigaTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
   private static final IBlockState TRUNK = Blocks.SPRUCE_LOG.getDefaultState();
   private static final IBlockState LEAF = Blocks.SPRUCE_LEAVES.getDefaultState();

   public TallTaigaTreeFeature(boolean p_i2025_1_) {
      super(p_i2025_1_);
   }

   public boolean place(Set<BlockPos> changedBlocks, IWorld worldIn, Random rand, BlockPos position) {
      int i = rand.nextInt(4) + 6;
      int j = 1 + rand.nextInt(2);
      int k = i - j;
      int l = 2 + rand.nextInt(2);
      boolean flag = true;
      if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getWorld().getHeight()) {
         for(int i1 = position.getY(); i1 <= position.getY() + 1 + i && flag; ++i1) {
            int j1;
            if (i1 - position.getY() < j) {
               j1 = 0;
            } else {
               j1 = l;
            }

            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for(int k1 = position.getX() - j1; k1 <= position.getX() + j1 && flag; ++k1) {
               for(int l1 = position.getZ() - j1; l1 <= position.getZ() + j1 && flag; ++l1) {
                  if (i1 >= 0 && i1 < worldIn.getWorld().getHeight()) {
                     IBlockState iblockstate = worldIn.getBlockState(blockpos$mutableblockpos.setPos(k1, i1, l1));
                     if (!iblockstate.isAir(worldIn, blockpos$mutableblockpos) && !iblockstate.isIn(BlockTags.LEAVES)) {
                        flag = false;
                     }
                  } else {
                     flag = false;
                  }
               }
            }
         }

         if (!flag) {
            return false;
         } else {
            if (worldIn.getBlockState(position.down()).canSustainPlant(worldIn, position.down(), net.minecraft.util.EnumFacing.UP, (net.minecraft.block.BlockSapling)Blocks.SPRUCE_SAPLING) && position.getY() < worldIn.getWorld().getHeight() - i - 1) {
               this.setDirtAt(worldIn, position.down(), position);
               int i3 = rand.nextInt(2);
               int j3 = 1;
               int k3 = 0;

               for(int l3 = 0; l3 <= k; ++l3) {
                  int j4 = position.getY() + i - l3;

                  for(int i2 = position.getX() - i3; i2 <= position.getX() + i3; ++i2) {
                     int j2 = i2 - position.getX();

                     for(int k2 = position.getZ() - i3; k2 <= position.getZ() + i3; ++k2) {
                        int l2 = k2 - position.getZ();
                        if (Math.abs(j2) != i3 || Math.abs(l2) != i3 || i3 <= 0) {
                           BlockPos blockpos = new BlockPos(i2, j4, k2);
                           if (worldIn.getBlockState(blockpos).canBeReplacedByLeaves(worldIn, blockpos)) {
                              this.setBlockState(worldIn, blockpos, LEAF);
                           }
                        }
                     }
                  }

                  if (i3 >= j3) {
                     i3 = k3;
                     k3 = 1;
                     ++j3;
                     if (j3 > l) {
                        j3 = l;
                     }
                  } else {
                     ++i3;
                  }
               }

               int i4 = rand.nextInt(3);

               for(int k4 = 0; k4 < i - i4; ++k4) {
                  IBlockState iblockstate1 = worldIn.getBlockState(position.up(k4));
                  if (iblockstate1.isAir(worldIn, position.up(k4)) || iblockstate1.isIn(BlockTags.LEAVES)) {
                     this.func_208520_a(changedBlocks, worldIn, position.up(k4), TRUNK);
                  }
               }

               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }
}