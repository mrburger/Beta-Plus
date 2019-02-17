package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.init.Biomes;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class MineshaftStructure extends Structure<MineshaftConfig> {
   protected boolean hasStartAt(IChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ) {
      ((SharedSeedRandom)rand).setLargeFeatureSeed(chunkGen.getSeed(), chunkPosX, chunkPosZ);
      Biome biome = chunkGen.getBiomeProvider().getBiome(new BlockPos((chunkPosX << 4) + 9, 0, (chunkPosZ << 4) + 9), Biomes.DEFAULT);
      if (chunkGen.hasStructure(biome, Feature.MINESHAFT)) {
         MineshaftConfig mineshaftconfig = (MineshaftConfig)chunkGen.getStructureConfig(biome, Feature.MINESHAFT);
         double d0 = mineshaftconfig.field_202439_a;
         return rand.nextDouble() < d0;
      } else {
         return false;
      }
   }

   protected boolean isEnabledIn(IWorld worldIn) {
      return worldIn.getWorldInfo().isMapFeaturesEnabled();
   }

   protected StructureStart makeStart(IWorld worldIn, IChunkGenerator<?> generator, SharedSeedRandom random, int x, int z) {
      Biome biome = generator.getBiomeProvider().getBiome(new BlockPos((x << 4) + 9, 0, (z << 4) + 9), Biomes.DEFAULT);
      return new MineshaftStructure.Start(worldIn, generator, random, x, z, biome);
   }

   protected String getStructureName() {
      return "Mineshaft";
   }

   public int getSize() {
      return 8;
   }

   public static class Start extends StructureStart {
      private MineshaftStructure.Type field_202507_c;

      public Start() {
      }

      public Start(IWorld p_i48759_1_, IChunkGenerator<?> p_i48759_2_, SharedSeedRandom p_i48759_3_, int p_i48759_4_, int p_i48759_5_, Biome p_i48759_6_) {
         super(p_i48759_4_, p_i48759_5_, p_i48759_6_, p_i48759_3_, p_i48759_1_.getSeed());
         MineshaftConfig mineshaftconfig = (MineshaftConfig)p_i48759_2_.getStructureConfig(p_i48759_6_, Feature.MINESHAFT);
         this.field_202507_c = mineshaftconfig.type;
         MineshaftPieces.Room mineshaftpieces$room = new MineshaftPieces.Room(0, p_i48759_3_, (p_i48759_4_ << 4) + 2, (p_i48759_5_ << 4) + 2, this.field_202507_c);
         this.components.add(mineshaftpieces$room);
         mineshaftpieces$room.buildComponent(mineshaftpieces$room, this.components, p_i48759_3_);
         this.recalculateStructureSize(p_i48759_1_);
         if (mineshaftconfig.type == MineshaftStructure.Type.MESA) {
            int i = -5;
            int j = p_i48759_1_.getSeaLevel() - this.boundingBox.maxY + this.boundingBox.getYSize() / 2 - -5;
            this.boundingBox.offset(0, j, 0);

            for(StructurePiece structurepiece : this.components) {
               structurepiece.offset(0, j, 0);
            }
         } else {
            this.markAvailableHeight(p_i48759_1_, p_i48759_3_, 10);
         }

      }
   }

   public static enum Type {
      NORMAL,
      MESA;

      public static MineshaftStructure.Type byId(int id) {
         return id >= 0 && id < values().length ? values()[id] : NORMAL;
      }
   }
}