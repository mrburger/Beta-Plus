package net.minecraft.world.gen.layer;

import javax.annotation.Nullable;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;

public class GenLayer {
   private final IAreaFactory<LazyArea> lazyAreaFactory;

   public GenLayer(IAreaFactory<LazyArea> lazyAreaFactoryIn) {
      this.lazyAreaFactory = lazyAreaFactoryIn;
   }

   public Biome[] generateBiomes(int startX, int startZ, int xSize, int zSize, @Nullable Biome defaultBiome) {
      AreaDimension areadimension = new AreaDimension(startX, startZ, xSize, zSize);
      LazyArea lazyarea = this.lazyAreaFactory.make(areadimension);
      Biome[] abiome = new Biome[xSize * zSize];

      for(int i = 0; i < zSize; ++i) {
         for(int j = 0; j < xSize; ++j) {
            abiome[j + i * xSize] = Biome.getBiome(lazyarea.getValue(j, i), defaultBiome);
         }
      }

      return abiome;
   }
}