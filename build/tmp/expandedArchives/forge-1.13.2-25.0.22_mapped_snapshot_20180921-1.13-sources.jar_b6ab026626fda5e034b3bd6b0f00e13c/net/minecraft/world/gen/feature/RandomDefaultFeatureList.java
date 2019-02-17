package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class RandomDefaultFeatureList extends Feature<RandomFeatureListConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, RandomFeatureListConfig p_212245_5_) {
      int i = p_212245_3_.nextInt(5) - 3 + p_212245_5_.field_202456_c;

      for(int j = 0; j < i; ++j) {
         int k = p_212245_3_.nextInt(p_212245_5_.field_202454_a.length);
         this.func_202361_a(p_212245_5_.field_202454_a[k], p_212245_5_.field_202455_b[k], p_212245_1_, p_212245_2_, p_212245_3_, p_212245_4_);
      }

      return true;
   }

   <FC extends IFeatureConfig> boolean func_202361_a(Feature<FC> p_202361_1_, IFeatureConfig p_202361_2_, IWorld p_202361_3_, IChunkGenerator<? extends IChunkGenSettings> p_202361_4_, Random p_202361_5_, BlockPos p_202361_6_) {
      return p_202361_1_.func_212245_a(p_202361_3_, p_202361_4_, p_202361_5_, p_202361_6_, (FC)p_202361_2_);
   }
}