package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class CoralClawFeature extends CoralFeature {
   protected boolean func_204623_a(IWorld p_204623_1_, Random p_204623_2_, BlockPos p_204623_3_, IBlockState p_204623_4_) {
      if (!this.func_204624_b(p_204623_1_, p_204623_2_, p_204623_3_, p_204623_4_)) {
         return false;
      } else {
         EnumFacing enumfacing = EnumFacing.Plane.HORIZONTAL.random(p_204623_2_);
         int i = p_204623_2_.nextInt(2) + 2;
         List<EnumFacing> list = Lists.newArrayList(enumfacing, enumfacing.rotateY(), enumfacing.rotateYCCW());
         Collections.shuffle(list, p_204623_2_);

         for(EnumFacing enumfacing1 : list.subList(0, i)) {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(p_204623_3_);
            int j = p_204623_2_.nextInt(2) + 1;
            blockpos$mutableblockpos.move(enumfacing1);
            int k;
            EnumFacing enumfacing2;
            if (enumfacing1 == enumfacing) {
               enumfacing2 = enumfacing;
               k = p_204623_2_.nextInt(3) + 2;
            } else {
               blockpos$mutableblockpos.move(EnumFacing.UP);
               EnumFacing[] aenumfacing = new EnumFacing[]{enumfacing1, EnumFacing.UP};
               enumfacing2 = aenumfacing[p_204623_2_.nextInt(aenumfacing.length)];
               k = p_204623_2_.nextInt(3) + 3;
            }

            for(int l = 0; l < j && this.func_204624_b(p_204623_1_, p_204623_2_, blockpos$mutableblockpos, p_204623_4_); ++l) {
               blockpos$mutableblockpos.move(enumfacing2);
            }

            blockpos$mutableblockpos.move(enumfacing2.getOpposite());
            blockpos$mutableblockpos.move(EnumFacing.UP);

            for(int i1 = 0; i1 < k; ++i1) {
               blockpos$mutableblockpos.move(enumfacing);
               if (!this.func_204624_b(p_204623_1_, p_204623_2_, blockpos$mutableblockpos, p_204623_4_)) {
                  break;
               }

               if (p_204623_2_.nextFloat() < 0.25F) {
                  blockpos$mutableblockpos.move(EnumFacing.UP);
               }
            }
         }

         return true;
      }
   }
}