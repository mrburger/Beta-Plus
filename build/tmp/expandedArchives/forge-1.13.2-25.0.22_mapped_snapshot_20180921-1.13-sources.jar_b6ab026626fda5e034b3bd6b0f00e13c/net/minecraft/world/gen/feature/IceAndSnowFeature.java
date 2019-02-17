package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockDirtSnowy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class IceAndSnowFeature extends Feature<NoFeatureConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();

      for(int i = 0; i < 16; ++i) {
         for(int j = 0; j < 16; ++j) {
            int k = p_212245_4_.getX() + i;
            int l = p_212245_4_.getZ() + j;
            int i1 = p_212245_1_.getHeight(Heightmap.Type.MOTION_BLOCKING, k, l);
            blockpos$mutableblockpos.setPos(k, i1, l);
            blockpos$mutableblockpos1.setPos(blockpos$mutableblockpos).move(EnumFacing.DOWN, 1);
            Biome biome = p_212245_1_.getBiome(blockpos$mutableblockpos);
            if (biome.doesWaterFreeze(p_212245_1_, blockpos$mutableblockpos1, false)) {
               p_212245_1_.setBlockState(blockpos$mutableblockpos1, Blocks.ICE.getDefaultState(), 2);
            }

            if (biome.doesSnowGenerate(p_212245_1_, blockpos$mutableblockpos)) {
               p_212245_1_.setBlockState(blockpos$mutableblockpos, Blocks.SNOW.getDefaultState(), 2);
               IBlockState iblockstate = p_212245_1_.getBlockState(blockpos$mutableblockpos1);
               if (iblockstate.has(BlockDirtSnowy.SNOWY)) {
                  p_212245_1_.setBlockState(blockpos$mutableblockpos1, iblockstate.with(BlockDirtSnowy.SNOWY, Boolean.valueOf(true)), 2);
               }
            }
         }
      }

      return true;
   }
}