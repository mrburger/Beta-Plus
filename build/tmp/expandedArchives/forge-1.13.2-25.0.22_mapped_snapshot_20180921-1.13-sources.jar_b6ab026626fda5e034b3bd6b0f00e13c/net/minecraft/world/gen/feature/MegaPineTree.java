package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;

public class MegaPineTree extends HugeTreesFeature<NoFeatureConfig> {
   private static final IBlockState TRUNK = Blocks.SPRUCE_LOG.getDefaultState();
   private static final IBlockState LEAF = Blocks.SPRUCE_LEAVES.getDefaultState();
   private static final IBlockState PODZOL = Blocks.PODZOL.getDefaultState();
   private final boolean useBaseHeight;

   public MegaPineTree(boolean notify, boolean p_i45457_2_) {
      super(notify, 13, 15, TRUNK, LEAF);
      this.useBaseHeight = p_i45457_2_;
   }

   public boolean place(Set<BlockPos> changedBlocks, IWorld worldIn, Random rand, BlockPos position) {
      int i = this.getHeight(rand);
      if (!this.func_203427_a(worldIn, position, i)) {
         return false;
      } else {
         this.createCrown(worldIn, position.getX(), position.getZ(), position.getY() + i, 0, rand);

         for(int j = 0; j < i; ++j) {
            IBlockState iblockstate = worldIn.getBlockState(position.up(j));
            if (iblockstate.isAir(worldIn, position.up(j)) || iblockstate.isIn(BlockTags.LEAVES)) {
               this.func_208520_a(changedBlocks, worldIn, position.up(j), this.woodMetadata);
            }

            if (j < i - 1) {
               iblockstate = worldIn.getBlockState(position.add(1, j, 0));
               if (iblockstate.isAir(worldIn, position.add(1, j, 0)) || iblockstate.isIn(BlockTags.LEAVES)) {
                  this.func_208520_a(changedBlocks, worldIn, position.add(1, j, 0), this.woodMetadata);
               }

               iblockstate = worldIn.getBlockState(position.add(1, j, 1));
               if (iblockstate.isAir(worldIn, position.add(1, j, 1)) || iblockstate.isIn(BlockTags.LEAVES)) {
                  this.func_208520_a(changedBlocks, worldIn, position.add(1, j, 1), this.woodMetadata);
               }

               iblockstate = worldIn.getBlockState(position.add(0, j, 1));
               if (iblockstate.isAir(worldIn, position.add(0, j, 1)) || iblockstate.isIn(BlockTags.LEAVES)) {
                  this.func_208520_a(changedBlocks, worldIn, position.add(0, j, 1), this.woodMetadata);
               }
            }
         }

         this.generateSaplings(worldIn, rand, position);
         return true;
      }
   }

   private void createCrown(IWorld worldIn, int x, int z, int y, int p_150541_5_, Random rand) {
      int i = rand.nextInt(5) + (this.useBaseHeight ? this.baseHeight : 3);
      int j = 0;

      for(int k = y - i; k <= y; ++k) {
         int l = y - k;
         int i1 = p_150541_5_ + MathHelper.floor((float)l / (float)i * 3.5F);
         this.growLeavesLayerStrict(worldIn, new BlockPos(x, k, z), i1 + (l > 0 && i1 == j && (k & 1) == 0 ? 1 : 0));
         j = i1;
      }

   }

   public void generateSaplings(IWorld worldIn, Random random, BlockPos pos) {
      this.placePodzolCircle(worldIn, pos.west().north());
      this.placePodzolCircle(worldIn, pos.east(2).north());
      this.placePodzolCircle(worldIn, pos.west().south(2));
      this.placePodzolCircle(worldIn, pos.east(2).south(2));

      for(int i = 0; i < 5; ++i) {
         int j = random.nextInt(64);
         int k = j % 8;
         int l = j / 8;
         if (k == 0 || k == 7 || l == 0 || l == 7) {
            this.placePodzolCircle(worldIn, pos.add(-3 + k, 0, -3 + l));
         }
      }

   }

   private void placePodzolCircle(IWorld worldIn, BlockPos center) {
      for(int i = -2; i <= 2; ++i) {
         for(int j = -2; j <= 2; ++j) {
            if (Math.abs(i) != 2 || Math.abs(j) != 2) {
               this.placePodzolAt(worldIn, center.add(i, 0, j));
            }
         }
      }

   }

   private void placePodzolAt(IWorld worldIn, BlockPos pos) {
      for(int i = 2; i >= -3; --i) {
         BlockPos blockpos = pos.up(i);
         IBlockState iblockstate = worldIn.getBlockState(blockpos);
         Block block = iblockstate.getBlock();
         if (block == Blocks.GRASS_BLOCK || Block.isDirt(block)) {
            this.setBlockState(worldIn, blockpos, PODZOL);
            break;
         }

         if (!iblockstate.isAir(worldIn, blockpos) && i < 0) {
            break;
         }
      }

   }
}