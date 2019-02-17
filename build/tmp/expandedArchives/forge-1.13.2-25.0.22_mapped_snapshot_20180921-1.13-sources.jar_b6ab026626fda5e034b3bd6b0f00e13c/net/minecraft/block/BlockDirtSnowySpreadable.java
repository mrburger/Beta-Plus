package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class BlockDirtSnowySpreadable extends BlockDirtSnowy {
   protected BlockDirtSnowySpreadable(Block.Properties builder) {
      super(builder);
   }

   private static boolean func_196383_a(IWorldReaderBase p_196383_0_, BlockPos p_196383_1_) {
      BlockPos blockpos = p_196383_1_.up();
      return p_196383_0_.getLight(blockpos) >= 4 || p_196383_0_.getBlockState(blockpos).getOpacity(p_196383_0_, blockpos) < p_196383_0_.getMaxLightLevel();
   }

   private static boolean func_196384_b(IWorldReaderBase p_196384_0_, BlockPos p_196384_1_) {
      BlockPos blockpos = p_196384_1_.up();
      return p_196384_0_.getLight(blockpos) >= 4 && p_196384_0_.getBlockState(blockpos).getOpacity(p_196384_0_, blockpos) < p_196384_0_.getMaxLightLevel() && !p_196384_0_.getFluidState(blockpos).isTagged(FluidTags.WATER);
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (!worldIn.isRemote) {
         if (!worldIn.isAreaLoaded(pos, 3)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
         if (!func_196383_a(worldIn, pos)) {
            worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState());
         } else {
            if (worldIn.getLight(pos.up()) >= 9) {
               for(int i = 0; i < 4; ++i) {
                  BlockPos blockpos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                  if (!worldIn.isBlockPresent(blockpos)) {
                     return;
                  }

                  if (worldIn.getBlockState(blockpos).getBlock() == Blocks.DIRT && func_196384_b(worldIn, blockpos)) {
                     worldIn.setBlockState(blockpos, this.getDefaultState());
                  }
               }
            }

         }
      }
   }
}