package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class PointyTaigaTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
   private static final IBlockState TRUNK = Blocks.SPRUCE_LOG.getDefaultState();
   private static final IBlockState LEAF = Blocks.SPRUCE_LEAVES.getDefaultState();

   public PointyTaigaTreeFeature() {
      super(false);
   }

   public boolean place(Set<BlockPos> changedBlocks, IWorld worldIn, Random rand, BlockPos position) {
      int i = rand.nextInt(5) + 7;
      int j = i - rand.nextInt(2) - 3;
      int k = i - j;
      int l = 1 + rand.nextInt(k + 1);
      if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
         boolean flag = true;

         for(int i1 = position.getY(); i1 <= position.getY() + 1 + i && flag; ++i1) {
            int j1 = 1;
            if (i1 - position.getY() < j) {
               j1 = 0;
            } else {
               j1 = l;
            }

            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for(int k1 = position.getX() - j1; k1 <= position.getX() + j1 && flag; ++k1) {
               for(int l1 = position.getZ() - j1; l1 <= position.getZ() + j1 && flag; ++l1) {
                  if (i1 >= 0 && i1 < worldIn.getWorld().getHeight()) {
                     if (!this.canGrowInto(worldIn, blockpos$mutableblockpos.setPos(k1, i1, l1))) {
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
            boolean isSoil = worldIn.getBlockState(position.down()).canSustainPlant(worldIn, position.down(), net.minecraft.util.EnumFacing.UP, (net.minecraft.block.BlockSapling)Blocks.SPRUCE_SAPLING);
            if (isSoil && position.getY() < worldIn.getWorld().getHeight() - i - 1) {
               this.setDirtAt(worldIn, position.down(), position);
               int k2 = 0;

               for(int l2 = position.getY() + i; l2 >= position.getY() + j; --l2) {
                  for(int j3 = position.getX() - k2; j3 <= position.getX() + k2; ++j3) {
                     int k3 = j3 - position.getX();

                     for(int i2 = position.getZ() - k2; i2 <= position.getZ() + k2; ++i2) {
                        int j2 = i2 - position.getZ();
                        if (Math.abs(k3) != k2 || Math.abs(j2) != k2 || k2 <= 0) {
                           BlockPos blockpos = new BlockPos(j3, l2, i2);
                           if (worldIn.getBlockState(blockpos).canBeReplacedByLeaves(worldIn, blockpos)) {
                              this.setBlockState(worldIn, blockpos, LEAF);
                           }
                        }
                     }
                  }

                  if (k2 >= 1 && l2 == position.getY() + j + 1) {
                     --k2;
                  } else if (k2 < l) {
                     ++k2;
                  }
               }

               for(int i3 = 0; i3 < i - 1; ++i3) {
                  IBlockState iblockstate = worldIn.getBlockState(position.up(i3));
                  if (iblockstate.isAir(worldIn, position.up(i3)) || iblockstate.isIn(BlockTags.LEAVES)) {
                     this.func_208520_a(changedBlocks, worldIn, position.up(i3), TRUNK);
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