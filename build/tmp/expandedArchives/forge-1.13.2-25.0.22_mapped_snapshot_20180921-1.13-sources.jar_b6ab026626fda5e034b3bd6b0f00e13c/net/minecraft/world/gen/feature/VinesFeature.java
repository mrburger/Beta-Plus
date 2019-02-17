package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class VinesFeature extends Feature<NoFeatureConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(p_212245_4_);

      for(int i = p_212245_4_.getY(); i < p_212245_1_.getWorld().getHeight(); ++i) {
         blockpos$mutableblockpos.setPos(p_212245_4_);
         blockpos$mutableblockpos.move(p_212245_3_.nextInt(4) - p_212245_3_.nextInt(4), 0, p_212245_3_.nextInt(4) - p_212245_3_.nextInt(4));
         blockpos$mutableblockpos.setY(i);
         if (p_212245_1_.isAirBlock(blockpos$mutableblockpos)) {
            for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
               IBlockState iblockstate = Blocks.VINE.getDefaultState().with(BlockVine.getPropertyFor(enumfacing), Boolean.valueOf(true));
               if (iblockstate.isValidPosition(p_212245_1_, blockpos$mutableblockpos)) {
                  p_212245_1_.setBlockState(blockpos$mutableblockpos, iblockstate, 2);
                  break;
               }
            }
         }
      }

      return true;
   }
}