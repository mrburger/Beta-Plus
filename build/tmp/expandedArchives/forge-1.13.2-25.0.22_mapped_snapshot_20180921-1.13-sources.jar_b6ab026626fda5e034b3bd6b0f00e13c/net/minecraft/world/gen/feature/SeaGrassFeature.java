package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockSeaGrassTall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class SeaGrassFeature extends Feature<SeaGrassConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, SeaGrassConfig p_212245_5_) {
      int i = 0;

      for(int j = 0; j < p_212245_5_.field_203237_a; ++j) {
         int k = p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8);
         int l = p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8);
         int i1 = p_212245_1_.getHeight(Heightmap.Type.OCEAN_FLOOR, p_212245_4_.getX() + k, p_212245_4_.getZ() + l);
         BlockPos blockpos = new BlockPos(p_212245_4_.getX() + k, i1, p_212245_4_.getZ() + l);
         if (p_212245_1_.getBlockState(blockpos).getBlock() == Blocks.WATER) {
            boolean flag = p_212245_3_.nextDouble() < p_212245_5_.field_203238_b;
            IBlockState iblockstate = flag ? Blocks.TALL_SEAGRASS.getDefaultState() : Blocks.SEAGRASS.getDefaultState();
            if (iblockstate.isValidPosition(p_212245_1_, blockpos)) {
               if (flag) {
                  IBlockState iblockstate1 = iblockstate.with(BlockSeaGrassTall.field_208065_c, DoubleBlockHalf.UPPER);
                  BlockPos blockpos1 = blockpos.up();
                  if (p_212245_1_.getBlockState(blockpos1).getBlock() == Blocks.WATER) {
                     p_212245_1_.setBlockState(blockpos, iblockstate, 2);
                     p_212245_1_.setBlockState(blockpos1, iblockstate1, 2);
                  }
               } else {
                  p_212245_1_.setBlockState(blockpos, iblockstate, 2);
               }

               ++i;
            }
         }
      }

      return i > 0;
   }
}