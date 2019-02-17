package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.init.Biomes;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class EndCityStructure extends Structure<EndCityConfig> {
   protected ChunkPos getStartPositionForPosition(IChunkGenerator<?> chunkGenerator, Random random, int x, int z, int spacingOffsetsX, int spacingOffsetsZ) {
      int i = chunkGenerator.getSettings().getEndCityDistance();
      int j = chunkGenerator.getSettings().getEndCitySeparation();
      int k = x + i * spacingOffsetsX;
      int l = z + i * spacingOffsetsZ;
      int i1 = k < 0 ? k - i + 1 : k;
      int j1 = l < 0 ? l - i + 1 : l;
      int k1 = i1 / i;
      int l1 = j1 / i;
      ((SharedSeedRandom)random).setLargeFeatureSeedWithSalt(chunkGenerator.getSeed(), k1, l1, 10387313);
      k1 = k1 * i;
      l1 = l1 * i;
      k1 = k1 + (random.nextInt(i - j) + random.nextInt(i - j)) / 2;
      l1 = l1 + (random.nextInt(i - j) + random.nextInt(i - j)) / 2;
      return new ChunkPos(k1, l1);
   }

   protected boolean hasStartAt(IChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ) {
      ChunkPos chunkpos = this.getStartPositionForPosition(chunkGen, rand, chunkPosX, chunkPosZ, 0, 0);
      if (chunkPosX == chunkpos.x && chunkPosZ == chunkpos.z) {
         Biome biome = chunkGen.getBiomeProvider().getBiome(new BlockPos((chunkPosX << 4) + 9, 0, (chunkPosZ << 4) + 9), Biomes.DEFAULT);
         if (!chunkGen.hasStructure(biome, Feature.END_CITY)) {
            return false;
         } else {
            int i = getYPosForStructure(chunkPosX, chunkPosZ, chunkGen);
            return i >= 60;
         }
      } else {
         return false;
      }
   }

   protected boolean isEnabledIn(IWorld worldIn) {
      return worldIn.getWorldInfo().isMapFeaturesEnabled();
   }

   protected StructureStart makeStart(IWorld worldIn, IChunkGenerator<?> generator, SharedSeedRandom random, int x, int z) {
      Biome biome = generator.getBiomeProvider().getBiome(new BlockPos((x << 4) + 9, 0, (z << 4) + 9), Biomes.DEFAULT);
      return new EndCityStructure.Start(worldIn, generator, random, x, z, biome);
   }

   protected String getStructureName() {
      return "EndCity";
   }

   public int getSize() {
      return 9;
   }

   private static int getYPosForStructure(int chunkX, int chunkY, IChunkGenerator<?> generatorIn) {
      Random random = new Random((long)(chunkX + chunkY * 10387313));
      Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
      ChunkPrimer chunkprimer = new ChunkPrimer(new ChunkPos(chunkX, chunkY), UpgradeData.EMPTY);
      generatorIn.makeBase(chunkprimer);
      int i = 5;
      int j = 5;
      if (rotation == Rotation.CLOCKWISE_90) {
         i = -5;
      } else if (rotation == Rotation.CLOCKWISE_180) {
         i = -5;
         j = -5;
      } else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
         j = -5;
      }

      int k = chunkprimer.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7, 7);
      int l = chunkprimer.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7, 7 + j);
      int i1 = chunkprimer.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7 + i, 7);
      int j1 = chunkprimer.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7 + i, 7 + j);
      return Math.min(Math.min(k, l), Math.min(i1, j1));
   }

   public static class Start extends StructureStart {
      private boolean isSizeable;

      public Start() {
      }

      public Start(IWorld worldIn, IChunkGenerator<?> chunkGenerator, SharedSeedRandom sharedSeed, int chunkX, int chunkZ, Biome biomeIn) {
         super(chunkX, chunkZ, biomeIn, sharedSeed, worldIn.getSeed());
         Rotation rotation = Rotation.values()[sharedSeed.nextInt(Rotation.values().length)];
         int i = EndCityStructure.getYPosForStructure(chunkX, chunkZ, chunkGenerator);
         if (i < 60) {
            this.isSizeable = false;
         } else {
            BlockPos blockpos = new BlockPos(chunkX * 16 + 8, i, chunkZ * 16 + 8);
            EndCityPieces.startHouseTower(worldIn.getSaveHandler().getStructureTemplateManager(), blockpos, rotation, this.components, sharedSeed);
            this.recalculateStructureSize(worldIn);
            this.isSizeable = true;
         }
      }

      /**
       * currently only defined for Villages, returns true if Village has more than 2 non-road components
       */
      public boolean isSizeableStructure() {
         return this.isSizeable;
      }
   }
}