package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class BlockWithContextFeature extends Feature<BlockWithContextConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, BlockWithContextConfig p_212245_5_) {
      if (p_212245_5_.placeOn.contains(p_212245_1_.getBlockState(p_212245_4_.down())) && p_212245_5_.placeIn.contains(p_212245_1_.getBlockState(p_212245_4_)) && p_212245_5_.placeUnder.contains(p_212245_1_.getBlockState(p_212245_4_.up()))) {
         p_212245_1_.setBlockState(p_212245_4_, p_212245_5_.state, 2);
         return true;
      } else {
         return false;
      }
   }
}