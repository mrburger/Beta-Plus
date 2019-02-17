package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public abstract class HugeTreesFeature<T extends IFeatureConfig> extends AbstractTreeFeature<T> {
   /** The base height of the tree */
   protected final int baseHeight;
   /** Sets the metadata for the wood blocks used */
   protected final IBlockState woodMetadata;
   /** Sets the metadata for the leaves used in huge trees */
   protected final IBlockState leavesMetadata;
   protected int extraRandomHeight;
   protected net.minecraftforge.common.IPlantable sapling = (net.minecraftforge.common.IPlantable)Blocks.OAK_SAPLING;

   public HugeTreesFeature(boolean notify, int baseHeightIn, int extraRandomHeightIn, IBlockState woodMetadataIn, IBlockState leavesMetadataIn) {
      super(notify);
      this.baseHeight = baseHeightIn;
      this.extraRandomHeight = extraRandomHeightIn;
      this.woodMetadata = woodMetadataIn;
      this.leavesMetadata = leavesMetadataIn;
   }

   /**
    * calculates the height based on this trees base height and its extra random height
    */
   protected int getHeight(Random rand) {
      int i = rand.nextInt(3) + this.baseHeight;
      if (this.extraRandomHeight > 1) {
         i += rand.nextInt(this.extraRandomHeight);
      }

      return i;
   }

   /**
    * returns whether or not there is space for a tree to grow at a certain position
    */
   private boolean isSpaceAt(IBlockReader worldIn, BlockPos leavesPos, int height) {
      boolean flag = true;
      int worldHeight  = worldIn instanceof IWorld ? ((IWorld)worldIn).getWorld().getHeight() : 256;
      if (leavesPos.getY() >= 1 && leavesPos.getY() + height + 1 <= worldHeight) {
         for(int i = 0; i <= 1 + height; ++i) {
            int j = 2;
            if (i == 0) {
               j = 1;
            } else if (i >= 1 + height - 2) {
               j = 2;
            }

            for(int k = -j; k <= j && flag; ++k) {
               for(int l = -j; l <= j && flag; ++l) {
                  if (leavesPos.getY() + i < 0 || leavesPos.getY() + i >= 256 || !this.canGrowInto(worldIn, leavesPos.add(k, i, l))) {
                     flag = false;
                  }
               }
            }
         }

         return flag;
      } else {
         return false;
      }
   }

   private boolean func_202405_b(IWorld p_202405_1_, BlockPos p_202405_2_) {
      BlockPos blockpos = p_202405_2_.down();
      boolean isSoil = p_202405_1_.getBlockState(blockpos).canSustainPlant(p_202405_1_, blockpos, net.minecraft.util.EnumFacing.UP, sapling);
      if (isSoil && p_202405_2_.getY() >= 2) {
         this.setDirtAt(p_202405_1_, blockpos, p_202405_2_);
         this.setDirtAt(p_202405_1_, blockpos.east(), p_202405_2_);
         this.setDirtAt(p_202405_1_, blockpos.south(), p_202405_2_);
         this.setDirtAt(p_202405_1_, blockpos.south().east(), p_202405_2_);
         return true;
      } else {
         return false;
      }
   }

   protected boolean func_203427_a(IWorld p_203427_1_, BlockPos p_203427_2_, int p_203427_3_) {
      return this.isSpaceAt(p_203427_1_, p_203427_2_, p_203427_3_) && this.func_202405_b(p_203427_1_, p_203427_2_);
   }

   /**
    * grow leaves in a circle with the outsides being within the circle
    */
   protected void growLeavesLayerStrict(IWorld worldIn, BlockPos layerCenter, int width) {
      int i = width * width;

      for(int j = -width; j <= width + 1; ++j) {
         for(int k = -width; k <= width + 1; ++k) {
            int l = Math.min(Math.abs(j), Math.abs(j - 1));
            int i1 = Math.min(Math.abs(k), Math.abs(k - 1));
            if (l + i1 < 7 && l * l + i1 * i1 <= i) {
               BlockPos blockpos = layerCenter.add(j, 0, k);
               IBlockState iblockstate = worldIn.getBlockState(blockpos);
               if (iblockstate.isAir(worldIn, blockpos) || iblockstate.isIn(BlockTags.LEAVES)) {
                  this.setBlockState(worldIn, blockpos, this.leavesMetadata);
               }
            }
         }
      }

   }

   /**
    * grow leaves in a circle
    */
   protected void growLeavesLayer(IWorld worldIn, BlockPos layerCenter, int width) {
      int i = width * width;

      for(int j = -width; j <= width; ++j) {
         for(int k = -width; k <= width; ++k) {
            if (j * j + k * k <= i) {
               BlockPos blockpos = layerCenter.add(j, 0, k);
               IBlockState iblockstate = worldIn.getBlockState(blockpos);
               if (iblockstate.isAir(worldIn, blockpos) || iblockstate.isIn(BlockTags.LEAVES)) {
                  this.setBlockState(worldIn, blockpos, this.leavesMetadata);
               }
            }
         }
      }

   }

   public HugeTreesFeature<T> setSapling(net.minecraftforge.common.IPlantable sapling) {
      this.sapling = sapling;
      return this;
   }
}