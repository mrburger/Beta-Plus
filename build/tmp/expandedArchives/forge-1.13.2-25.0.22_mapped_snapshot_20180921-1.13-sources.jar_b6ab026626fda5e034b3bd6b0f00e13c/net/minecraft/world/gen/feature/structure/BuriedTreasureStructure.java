package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class BuriedTreasureStructure extends Structure<BuriedTreasureConfig> {
   protected boolean hasStartAt(IChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ) {
      Biome biome = chunkGen.getBiomeProvider().getBiome(new BlockPos((chunkPosX << 4) + 9, 0, (chunkPosZ << 4) + 9), (Biome)null);
      if (chunkGen.hasStructure(biome, Feature.BURIED_TREASURE)) {
         ((SharedSeedRandom)rand).setLargeFeatureSeedWithSalt(chunkGen.getSeed(), chunkPosX, chunkPosZ, 10387320);
         BuriedTreasureConfig buriedtreasureconfig = (BuriedTreasureConfig)chunkGen.getStructureConfig(biome, Feature.BURIED_TREASURE);
         return rand.nextFloat() < buriedtreasureconfig.chance;
      } else {
         return false;
      }
   }

   protected boolean isEnabledIn(IWorld worldIn) {
      return worldIn.getWorldInfo().isMapFeaturesEnabled();
   }

   protected StructureStart makeStart(IWorld worldIn, IChunkGenerator<?> generator, SharedSeedRandom random, int x, int z) {
      Biome biome = generator.getBiomeProvider().getBiome(new BlockPos((x << 4) + 9, 0, (z << 4) + 9), (Biome)null);
      return new BuriedTreasureStructure.Start(worldIn, generator, random, x, z, biome);
   }

   protected String getStructureName() {
      return "Buried_Treasure";
   }

   public int getSize() {
      return 1;
   }

   public static class Start extends StructureStart {
      public Start() {
      }

      public Start(IWorld p_i48890_1_, IChunkGenerator<?> p_i48890_2_, SharedSeedRandom p_i48890_3_, int p_i48890_4_, int p_i48890_5_, Biome p_i48890_6_) {
         super(p_i48890_4_, p_i48890_5_, p_i48890_6_, p_i48890_3_, p_i48890_1_.getSeed());
         int i = p_i48890_4_ * 16;
         int j = p_i48890_5_ * 16;
         BlockPos blockpos = new BlockPos(i + 9, 90, j + 9);
         this.components.add(new BuriedTreasurePieces.Piece(blockpos));
         this.recalculateStructureSize(p_i48890_1_);
      }

      public BlockPos getPos() {
         return new BlockPos((this.chunkPosX << 4) + 9, 0, (this.chunkPosZ << 4) + 9);
      }
   }
}