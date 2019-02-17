package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.CompositeFlowerFeature;

public class BlockGrass extends BlockDirtSnowySpreadable implements IGrowable {
   public BlockGrass(Block.Properties builder) {
      super(builder);
   }

   /**
    * Whether this IGrowable can grow
    */
   public boolean canGrow(IBlockReader worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return worldIn.getBlockState(pos.up()).isAir();
   }

   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return true;
   }

   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      BlockPos blockpos = pos.up();
      IBlockState iblockstate = Blocks.GRASS.getDefaultState();

      for(int i = 0; i < 128; ++i) {
         BlockPos blockpos1 = blockpos;
         int j = 0;

         while(true) {
            if (j >= i / 16) {
               IBlockState iblockstate2 = worldIn.getBlockState(blockpos1);
               if (iblockstate2.getBlock() == iblockstate.getBlock() && rand.nextInt(10) == 0) {
                  ((IGrowable)iblockstate.getBlock()).grow(worldIn, rand, blockpos1, iblockstate2);
               }

               if (!iblockstate2.isAir()) {
                  break;
               }

               IBlockState iblockstate1;
               if (rand.nextInt(8) == 0) {
                  List<CompositeFlowerFeature<?>> list = worldIn.getBiome(blockpos1).getFlowers();
                  if (list.isEmpty()) {
                     break;
                  }

                  iblockstate1 = list.get(0).getRandomFlower(rand, blockpos1);
               } else {
                  iblockstate1 = iblockstate;
               }

               if (iblockstate1.isValidPosition(worldIn, blockpos1)) {
                  worldIn.setBlockState(blockpos1, iblockstate1, 3);
               }
               break;
            }

            blockpos1 = blockpos1.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);
            if (worldIn.getBlockState(blockpos1.down()).getBlock() != this || worldIn.getBlockState(blockpos1).isBlockNormalCube()) {
               break;
            }

            ++j;
         }
      }

   }

   public boolean isSolid(IBlockState state) {
      return true;
   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT_MIPPED;
   }
}