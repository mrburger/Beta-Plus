package net.minecraft.world.gen.feature.structure;

import net.minecraft.init.Biomes;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;

public class DesertPyramidStructure extends ScatteredStructure<DesertPyramidConfig> {
   protected String getStructureName() {
      return "Desert_Pyramid";
   }

   public int getSize() {
      return 3;
   }

   protected StructureStart makeStart(IWorld worldIn, IChunkGenerator<?> generator, SharedSeedRandom random, int x, int z) {
      Biome biome = generator.getBiomeProvider().getBiome(new BlockPos((x << 4) + 9, 0, (z << 4) + 9), Biomes.PLAINS);
      return new DesertPyramidStructure.Start(worldIn, random, x, z, biome);
   }

   protected int getSeedModifier() {
      return 14357617;
   }

   public static class Start extends StructureStart {
      public Start() {
      }

      public Start(IWorld p_i48628_1_, SharedSeedRandom p_i48628_2_, int p_i48628_3_, int p_i48628_4_, Biome p_i48628_5_) {
         super(p_i48628_3_, p_i48628_4_, p_i48628_5_, p_i48628_2_, p_i48628_1_.getSeed());
         DesertPyramidPiece desertpyramidpiece = new DesertPyramidPiece(p_i48628_2_, p_i48628_3_ * 16, p_i48628_4_ * 16);
         this.components.add(desertpyramidpiece);
         this.recalculateStructureSize(p_i48628_1_);
      }
   }
}