package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class TreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
   private static final IBlockState DEFAULT_TRUNK = Blocks.OAK_LOG.getDefaultState();
   private static final IBlockState DEFAULT_LEAF = Blocks.OAK_LEAVES.getDefaultState();
   /** The minimum height of a generated tree. */
   protected final int minTreeHeight;
   /** True if this tree should grow Vines. */
   private final boolean vinesGrow;
   /** The metadata value of the wood to use in tree generation. */
   private final IBlockState metaWood;
   /** The metadata value of the leaves to use in tree generation. */
   private final IBlockState metaLeaves;
   protected net.minecraftforge.common.IPlantable sapling = (net.minecraftforge.common.IPlantable)Blocks.OAK_SAPLING;

   public TreeFeature(boolean p_i2027_1_) {
      this(p_i2027_1_, 4, DEFAULT_TRUNK, DEFAULT_LEAF, false);
   }

   public TreeFeature(boolean notify, int minTreeHeightIn, IBlockState woodMeta, IBlockState p_i46446_4_, boolean growVines) {
      super(notify);
      this.minTreeHeight = minTreeHeightIn;
      this.metaWood = woodMeta;
      this.metaLeaves = p_i46446_4_;
      this.vinesGrow = growVines;
   }

   public boolean place(Set<BlockPos> changedBlocks, IWorld worldIn, Random rand, BlockPos position) {
      int i = this.func_208534_a(rand);
      boolean flag = true;
      if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getWorld().getHeight()) {
         for(int j = position.getY(); j <= position.getY() + 1 + i; ++j) {
            int k = 1;
            if (j == position.getY()) {
               k = 0;
            }

            if (j >= position.getY() + 1 + i - 2) {
               k = 2;
            }

            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for(int l = position.getX() - k; l <= position.getX() + k && flag; ++l) {
               for(int i1 = position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
                  if (j >= 0 && j < worldIn.getWorld().getHeight()) {
                     if (!this.canGrowInto(worldIn, blockpos$mutableblockpos.setPos(l, j, i1))) {
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
            if (worldIn.getBlockState(position.down()).canSustainPlant(worldIn, position.down(), net.minecraft.util.EnumFacing.UP, (net.minecraft.block.BlockSapling)Blocks.OAK_SAPLING) && position.getY() < worldIn.getWorld().getHeight() - i - 1) {
               this.setDirtAt(worldIn, position.down(), position);
               int k2 = 3;
               int l2 = 0;

               for(int i3 = position.getY() - 3 + i; i3 <= position.getY() + i; ++i3) {
                  int i4 = i3 - (position.getY() + i);
                  int j1 = 1 - i4 / 2;

                  for(int k1 = position.getX() - j1; k1 <= position.getX() + j1; ++k1) {
                     int l1 = k1 - position.getX();

                     for(int i2 = position.getZ() - j1; i2 <= position.getZ() + j1; ++i2) {
                        int j2 = i2 - position.getZ();
                        if (Math.abs(l1) != j1 || Math.abs(j2) != j1 || rand.nextInt(2) != 0 && i4 != 0) {
                           BlockPos blockpos = new BlockPos(k1, i3, i2);
                           IBlockState iblockstate = worldIn.getBlockState(blockpos);
                           Material material = iblockstate.getMaterial();
                           if (iblockstate.canBeReplacedByLeaves(worldIn, blockpos) || material == Material.VINE) {
                              this.setBlockState(worldIn, blockpos, this.metaLeaves);
                           }
                        }
                     }
                  }
               }

               for(int j3 = 0; j3 < i; ++j3) {
                  IBlockState iblockstate1 = worldIn.getBlockState(position.up(j3));
                  Material material1 = iblockstate1.getMaterial();
                  if (iblockstate1.canBeReplacedByLeaves(worldIn, position.up(j3)) || material1 == Material.VINE) {
                     this.func_208520_a(changedBlocks, worldIn, position.up(j3), this.metaWood);
                     if (this.vinesGrow && j3 > 0) {
                        if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(-1, j3, 0))) {
                           this.addVine(worldIn, position.add(-1, j3, 0), BlockVine.EAST);
                        }

                        if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(1, j3, 0))) {
                           this.addVine(worldIn, position.add(1, j3, 0), BlockVine.WEST);
                        }

                        if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(0, j3, -1))) {
                           this.addVine(worldIn, position.add(0, j3, -1), BlockVine.SOUTH);
                        }

                        if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(0, j3, 1))) {
                           this.addVine(worldIn, position.add(0, j3, 1), BlockVine.NORTH);
                        }
                     }
                  }
               }

               if (this.vinesGrow) {
                  for(int k3 = position.getY() - 3 + i; k3 <= position.getY() + i; ++k3) {
                     int j4 = k3 - (position.getY() + i);
                     int k4 = 2 - j4 / 2;
                     BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();

                     for(int l4 = position.getX() - k4; l4 <= position.getX() + k4; ++l4) {
                        for(int i5 = position.getZ() - k4; i5 <= position.getZ() + k4; ++i5) {
                           blockpos$mutableblockpos1.setPos(l4, k3, i5);
                           if (worldIn.getBlockState(blockpos$mutableblockpos1).isIn(BlockTags.LEAVES)) {
                              BlockPos blockpos1 = blockpos$mutableblockpos1.west();
                              BlockPos blockpos2 = blockpos$mutableblockpos1.east();
                              BlockPos blockpos3 = blockpos$mutableblockpos1.north();
                              BlockPos blockpos4 = blockpos$mutableblockpos1.south();
                              if (rand.nextInt(4) == 0 && worldIn.isAirBlock(blockpos1)) {
                                 this.addHangingVine(worldIn, blockpos1, BlockVine.EAST);
                              }

                              if (rand.nextInt(4) == 0 && worldIn.isAirBlock(blockpos2)) {
                                 this.addHangingVine(worldIn, blockpos2, BlockVine.WEST);
                              }

                              if (rand.nextInt(4) == 0 && worldIn.isAirBlock(blockpos3)) {
                                 this.addHangingVine(worldIn, blockpos3, BlockVine.SOUTH);
                              }

                              if (rand.nextInt(4) == 0 && worldIn.isAirBlock(blockpos4)) {
                                 this.addHangingVine(worldIn, blockpos4, BlockVine.NORTH);
                              }
                           }
                        }
                     }
                  }

                  if (rand.nextInt(5) == 0 && i > 5) {
                     for(int l3 = 0; l3 < 2; ++l3) {
                        for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                           if (rand.nextInt(4 - l3) == 0) {
                              EnumFacing enumfacing1 = enumfacing.getOpposite();
                              this.placeCocoa(worldIn, rand.nextInt(3), position.add(enumfacing1.getXOffset(), i - 5 + l3, enumfacing1.getZOffset()), enumfacing);
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

   protected int func_208534_a(Random p_208534_1_) {
      return this.minTreeHeight + p_208534_1_.nextInt(3);
   }

   private void placeCocoa(IWorld worldIn, int p_181652_2_, BlockPos pos, EnumFacing side) {
      this.setBlockState(worldIn, pos, Blocks.COCOA.getDefaultState().with(BlockCocoa.AGE, Integer.valueOf(p_181652_2_)).with(BlockCocoa.HORIZONTAL_FACING, side));
   }

   private void addVine(IWorld worldIn, BlockPos pos, BooleanProperty prop) {
      this.setBlockState(worldIn, pos, Blocks.VINE.getDefaultState().with(prop, Boolean.valueOf(true)));
   }

   private void addHangingVine(IWorld worldIn, BlockPos pos, BooleanProperty prop) {
      this.addVine(worldIn, pos, prop);
      int i = 4;

      for(BlockPos blockpos = pos.down(); worldIn.isAirBlock(blockpos) && i > 0; --i) {
         this.addVine(worldIn, blockpos, prop);
         blockpos = blockpos.down();
      }

   }

   public TreeFeature setSapling(net.minecraftforge.common.IPlantable sapling) {
      this.sapling = sapling;
      return this;
   }
}