package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class SwampTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
   private static final IBlockState TRUNK = Blocks.OAK_LOG.getDefaultState();
   private static final IBlockState LEAF = Blocks.OAK_LEAVES.getDefaultState();

   public SwampTreeFeature() {
      super(false);
   }

   public boolean place(Set<BlockPos> changedBlocks, IWorld worldIn, Random rand, BlockPos position) {
      int i;
      for(i = rand.nextInt(4) + 5; worldIn.getFluidState(position.down()).isTagged(FluidTags.WATER); position = position.down()) {
         ;
      }

      boolean flag = true;
      if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getWorld().getHeight()) {
         for(int j = position.getY(); j <= position.getY() + 1 + i; ++j) {
            int k = 1;
            if (j == position.getY()) {
               k = 0;
            }

            if (j >= position.getY() + 1 + i - 2) {
               k = 3;
            }

            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for(int l = position.getX() - k; l <= position.getX() + k && flag; ++l) {
               for(int i1 = position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
                  if (j >= 0 && j < worldIn.getWorld().getHeight()) {
                     IBlockState iblockstate = worldIn.getBlockState(blockpos$mutableblockpos.setPos(l, j, i1));
                     Block block = iblockstate.getBlock();
                     if (!iblockstate.isAir(worldIn, blockpos$mutableblockpos) && !iblockstate.isIn(BlockTags.LEAVES)) {
                        if (block == Blocks.WATER) {
                           if (j > position.getY()) {
                              flag = false;
                           }
                        } else {
                           flag = false;
                        }
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
            if (worldIn.getBlockState(position.down()).canSustainPlant(worldIn, position.down(), net.minecraft.util.EnumFacing.UP, (net.minecraft.block.BlockSapling)Blocks.OAK_SAPLING)  && position.getY() < worldIn.getWorld().getHeight() - i - 1) {
               this.setDirtAt(worldIn, position.down(), position);

               for(int k1 = position.getY() - 3 + i; k1 <= position.getY() + i; ++k1) {
                  int j2 = k1 - (position.getY() + i);
                  int l2 = 2 - j2 / 2;

                  for(int j3 = position.getX() - l2; j3 <= position.getX() + l2; ++j3) {
                     int k3 = j3 - position.getX();

                     for(int i4 = position.getZ() - l2; i4 <= position.getZ() + l2; ++i4) {
                        int j1 = i4 - position.getZ();
                        if (Math.abs(k3) != l2 || Math.abs(j1) != l2 || rand.nextInt(2) != 0 && j2 != 0) {
                           BlockPos blockpos = new BlockPos(j3, k1, i4);
                           if (worldIn.getBlockState(blockpos).canBeReplacedByLeaves(worldIn, blockpos)) {
                              this.setBlockState(worldIn, blockpos, LEAF);
                           }
                        }
                     }
                  }
               }

               for(int l1 = 0; l1 < i; ++l1) {
                  IBlockState iblockstate1 = worldIn.getBlockState(position.up(l1));
                  Block block2 = iblockstate1.getBlock();
                  if (iblockstate1.isAir(worldIn, position.up(l1)) || iblockstate1.isIn(BlockTags.LEAVES) || block2 == Blocks.WATER) {
                     this.func_208520_a(changedBlocks, worldIn, position.up(l1), TRUNK);
                  }
               }

               for(int i2 = position.getY() - 3 + i; i2 <= position.getY() + i; ++i2) {
                  int k2 = i2 - (position.getY() + i);
                  int i3 = 2 - k2 / 2;
                  BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();

                  for(int l3 = position.getX() - i3; l3 <= position.getX() + i3; ++l3) {
                     for(int j4 = position.getZ() - i3; j4 <= position.getZ() + i3; ++j4) {
                        blockpos$mutableblockpos1.setPos(l3, i2, j4);
                        if (worldIn.getBlockState(blockpos$mutableblockpos1).isIn(BlockTags.LEAVES)) {
                           BlockPos blockpos3 = blockpos$mutableblockpos1.west();
                           BlockPos blockpos4 = blockpos$mutableblockpos1.east();
                           BlockPos blockpos1 = blockpos$mutableblockpos1.north();
                           BlockPos blockpos2 = blockpos$mutableblockpos1.south();
                           if (rand.nextInt(4) == 0 && worldIn.isAirBlock(blockpos3)) {
                              this.addVine(worldIn, blockpos3, BlockVine.EAST);
                           }

                           if (rand.nextInt(4) == 0 && worldIn.isAirBlock(blockpos4)) {
                              this.addVine(worldIn, blockpos4, BlockVine.WEST);
                           }

                           if (rand.nextInt(4) == 0 && worldIn.isAirBlock(blockpos1)) {
                              this.addVine(worldIn, blockpos1, BlockVine.SOUTH);
                           }

                           if (rand.nextInt(4) == 0 && worldIn.isAirBlock(blockpos2)) {
                              this.addVine(worldIn, blockpos2, BlockVine.NORTH);
                           }
                        }
                     }
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

   private void addVine(IWorld worldIn, BlockPos pos, BooleanProperty prop) {
      IBlockState iblockstate = Blocks.VINE.getDefaultState().with(prop, Boolean.valueOf(true));
      this.setBlockState(worldIn, pos, iblockstate);
      int i = 4;

      for(BlockPos blockpos = pos.down(); worldIn.isAirBlock(blockpos) && i > 0; --i) {
         this.setBlockState(worldIn, blockpos, iblockstate);
         blockpos = blockpos.down();
      }

   }
}