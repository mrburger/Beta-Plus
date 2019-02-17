package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class CoralMushroomFeature extends CoralFeature {
   protected boolean func_204623_a(IWorld p_204623_1_, Random p_204623_2_, BlockPos p_204623_3_, IBlockState p_204623_4_) {
      int i = p_204623_2_.nextInt(3) + 3;
      int j = p_204623_2_.nextInt(3) + 3;
      int k = p_204623_2_.nextInt(3) + 3;
      int l = p_204623_2_.nextInt(3) + 1;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(p_204623_3_);

      for(int i1 = 0; i1 <= j; ++i1) {
         for(int j1 = 0; j1 <= i; ++j1) {
            for(int k1 = 0; k1 <= k; ++k1) {
               blockpos$mutableblockpos.setPos(i1 + p_204623_3_.getX(), j1 + p_204623_3_.getY(), k1 + p_204623_3_.getZ());
               blockpos$mutableblockpos.move(EnumFacing.DOWN, l);
               if ((i1 != 0 && i1 != j || j1 != 0 && j1 != i) && (k1 != 0 && k1 != k || j1 != 0 && j1 != i) && (i1 != 0 && i1 != j || k1 != 0 && k1 != k) && (i1 == 0 || i1 == j || j1 == 0 || j1 == i || k1 == 0 || k1 == k) && !(p_204623_2_.nextFloat() < 0.1F) && !this.func_204624_b(p_204623_1_, p_204623_2_, blockpos$mutableblockpos, p_204623_4_)) {
                  ;
               }
            }
         }
      }

      return true;
   }
}