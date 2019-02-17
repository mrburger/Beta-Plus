package net.minecraft.world.gen.feature.structure;

import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class ShipwreckStructure extends ScatteredStructure<ShipwreckConfig> {
   protected String getStructureName() {
      return "Shipwreck";
   }

   public int getSize() {
      return 3;
   }

   protected StructureStart makeStart(IWorld worldIn, IChunkGenerator<?> generator, SharedSeedRandom random, int x, int z) {
      Biome biome = generator.getBiomeProvider().getBiome(new BlockPos((x << 4) + 9, 0, (z << 4) + 9), (Biome)null);
      return new ShipwreckStructure.Start(worldIn, generator, random, x, z, biome);
   }

   protected int getSeedModifier() {
      return 165745295;
   }

   protected int getBiomeFeatureDistance(IChunkGenerator<?> chunkGenerator) {
      return chunkGenerator.getSettings().func_204748_h();
   }

   protected int func_211745_b(IChunkGenerator<?> p_211745_1_) {
      return p_211745_1_.getSettings().func_211730_k();
   }

   public static class Start extends StructureStart {
      public Start() {
      }

      public Start(IWorld p_i48901_1_, IChunkGenerator<?> p_i48901_2_, SharedSeedRandom p_i48901_3_, int p_i48901_4_, int p_i48901_5_, Biome p_i48901_6_) {
         super(p_i48901_4_, p_i48901_5_, p_i48901_6_, p_i48901_3_, p_i48901_1_.getSeed());
         ShipwreckConfig shipwreckconfig = (ShipwreckConfig)p_i48901_2_.getStructureConfig(p_i48901_6_, Feature.SHIPWRECK);
         Rotation rotation = Rotation.values()[p_i48901_3_.nextInt(Rotation.values().length)];
         BlockPos blockpos = new BlockPos(p_i48901_4_ * 16, 90, p_i48901_5_ * 16);
         ShipwreckPieces.func_204760_a(p_i48901_1_.getSaveHandler().getStructureTemplateManager(), blockpos, rotation, this.components, p_i48901_3_, shipwreckconfig);
         this.recalculateStructureSize(p_i48901_1_);
      }
   }
}