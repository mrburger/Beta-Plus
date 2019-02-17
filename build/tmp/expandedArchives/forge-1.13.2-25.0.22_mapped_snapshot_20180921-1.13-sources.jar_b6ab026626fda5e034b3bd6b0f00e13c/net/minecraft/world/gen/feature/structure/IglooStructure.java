package net.minecraft.world.gen.feature.structure;

import net.minecraft.init.Biomes;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class IglooStructure extends ScatteredStructure<IglooConfig> {
   protected String getStructureName() {
      return "Igloo";
   }

   public int getSize() {
      return 3;
   }

   protected StructureStart makeStart(IWorld worldIn, IChunkGenerator<?> generator, SharedSeedRandom random, int x, int z) {
      Biome biome = generator.getBiomeProvider().getBiome(new BlockPos((x << 4) + 9, 0, (z << 4) + 9), Biomes.PLAINS);
      return new IglooStructure.Start(worldIn, generator, random, x, z, biome);
   }

   protected int getSeedModifier() {
      return 14357618;
   }

   public static class Start extends StructureStart {
      public Start() {
      }

      public Start(IWorld p_i49246_1_, IChunkGenerator<?> p_i49246_2_, SharedSeedRandom p_i49246_3_, int p_i49246_4_, int p_i49246_5_, Biome p_i49246_6_) {
         super(p_i49246_4_, p_i49246_5_, p_i49246_6_, p_i49246_3_, p_i49246_1_.getSeed());
         IglooConfig iglooconfig = (IglooConfig)p_i49246_2_.getStructureConfig(p_i49246_6_, Feature.IGLOO);
         int i = p_i49246_4_ * 16;
         int j = p_i49246_5_ * 16;
         BlockPos blockpos = new BlockPos(i, 90, j);
         Rotation rotation = Rotation.values()[p_i49246_3_.nextInt(Rotation.values().length)];
         TemplateManager templatemanager = p_i49246_1_.getSaveHandler().getStructureTemplateManager();
         IglooPieces.func_207617_a(templatemanager, blockpos, rotation, this.components, p_i49246_3_, iglooconfig);
         this.recalculateStructureSize(p_i49246_1_);
      }
   }
}