package net.minecraft.world.gen.placement;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.EndCrystalTowerFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class EndSpikes extends BasePlacement<NoPlacementConfig> {
   private static final LoadingCache<Long, EndCrystalTowerFeature.EndSpike[]> CACHE = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build(new EndSpikes.CacheLoader());

   public <C extends IFeatureConfig> boolean generate(IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, NoPlacementConfig placementConfig, Feature<C> featureIn, C featureConfig) {
      EndCrystalTowerFeature.EndSpike[] aendcrystaltowerfeature$endspike = getSpikes(worldIn);
      boolean flag = false;

      for(EndCrystalTowerFeature.EndSpike endcrystaltowerfeature$endspike : aendcrystaltowerfeature$endspike) {
         if (endcrystaltowerfeature$endspike.doesStartInChunk(pos)) {
            ((EndCrystalTowerFeature)featureIn).setSpike(endcrystaltowerfeature$endspike);
            flag |= ((EndCrystalTowerFeature)featureIn).func_212245_a(worldIn, chunkGenerator, random, new BlockPos(endcrystaltowerfeature$endspike.getCenterX(), 45, endcrystaltowerfeature$endspike.getCenterZ()), IFeatureConfig.NO_FEATURE_CONFIG);
         }
      }

      return flag;
   }

   public static EndCrystalTowerFeature.EndSpike[] getSpikes(IWorld worldIn) {
      Random random = new Random(worldIn.getSeed());
      long i = random.nextLong() & 65535L;
      return CACHE.getUnchecked(i);
   }

   static class CacheLoader extends com.google.common.cache.CacheLoader<Long, EndCrystalTowerFeature.EndSpike[]> {
      private CacheLoader() {
      }

      public EndCrystalTowerFeature.EndSpike[] load(Long p_load_1_) throws Exception {
         List<Integer> list = Lists.newArrayList(ContiguousSet.create(Range.closedOpen(0, 10), DiscreteDomain.integers()));
         Collections.shuffle(list, new Random(p_load_1_));
         EndCrystalTowerFeature.EndSpike[] aendcrystaltowerfeature$endspike = new EndCrystalTowerFeature.EndSpike[10];

         for(int i = 0; i < 10; ++i) {
            int j = (int)(42.0D * Math.cos(2.0D * (-Math.PI + (Math.PI / 10D) * (double)i)));
            int k = (int)(42.0D * Math.sin(2.0D * (-Math.PI + (Math.PI / 10D) * (double)i)));
            int l = list.get(i);
            int i1 = 2 + l / 3;
            int j1 = 76 + l * 3;
            boolean flag = l == 1 || l == 2;
            aendcrystaltowerfeature$endspike[i] = new EndCrystalTowerFeature.EndSpike(j, k, i1, j1, flag);
         }

         return aendcrystaltowerfeature$endspike;
      }
   }
}