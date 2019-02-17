package net.minecraft.world.gen.carver;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class WorldCarverWrapper<C extends IFeatureConfig> implements IWorldCarver<NoFeatureConfig> {
   private final IWorldCarver<C> carver;
   private final C config;

   public WorldCarverWrapper(IWorldCarver<C> carverIn, C configIn) {
      this.carver = carverIn;
      this.config = configIn;
   }

   public boolean func_212246_a(IBlockReader p_212246_1_, Random p_212246_2_, int p_212246_3_, int p_212246_4_, NoFeatureConfig p_212246_5_) {
      return this.carver.func_212246_a(p_212246_1_, p_212246_2_, p_212246_3_, p_212246_4_, this.config);
   }

   public boolean carve(IWorld region, Random random, int chunkX, int chunkZ, int originalX, int originalZ, BitSet mask, NoFeatureConfig config) {
      return this.carver.carve(region, random, chunkX, chunkZ, originalX, originalZ, mask, this.config);
   }
}