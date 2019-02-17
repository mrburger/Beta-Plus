package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class RandomFeatureList extends Feature<RandomDefaultFeatureListConfig> {
   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, RandomDefaultFeatureListConfig p_212245_5_) {
      for(int i = 0; i < p_212245_5_.field_202449_a.length; ++i) {
         if (p_212245_3_.nextFloat() < p_212245_5_.field_202451_c[i]) {
            return this.func_202362_a(p_212245_5_.field_202449_a[i], p_212245_5_.field_202450_b[i], p_212245_1_, p_212245_2_, p_212245_3_, p_212245_4_);
         }
      }

      return this.func_202362_a(p_212245_5_.field_202452_d, p_212245_5_.field_202453_f, p_212245_1_, p_212245_2_, p_212245_3_, p_212245_4_);
   }

   <FC extends IFeatureConfig> boolean func_202362_a(Feature<FC> featureIn, IFeatureConfig config, IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> generator, Random rand, BlockPos pos) {
      return featureIn.func_212245_a(worldIn, generator, rand, pos, (FC)config);
   }
}