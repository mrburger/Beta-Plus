package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Biomes;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class FortressStructure extends Structure<FortressConfig> {
   private static final List<Biome.SpawnListEntry> field_202381_d = Lists.newArrayList(new Biome.SpawnListEntry(EntityType.BLAZE, 10, 2, 3), new Biome.SpawnListEntry(EntityType.ZOMBIE_PIGMAN, 5, 4, 4), new Biome.SpawnListEntry(EntityType.WITHER_SKELETON, 8, 5, 5), new Biome.SpawnListEntry(EntityType.SKELETON, 2, 5, 5), new Biome.SpawnListEntry(EntityType.MAGMA_CUBE, 3, 4, 4));

   protected boolean hasStartAt(IChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ) {
      int i = chunkPosX >> 4;
      int j = chunkPosZ >> 4;
      rand.setSeed((long)(i ^ j << 4) ^ chunkGen.getSeed());
      rand.nextInt();
      if (rand.nextInt(3) != 0) {
         return false;
      } else if (chunkPosX != (i << 4) + 4 + rand.nextInt(8)) {
         return false;
      } else if (chunkPosZ != (j << 4) + 4 + rand.nextInt(8)) {
         return false;
      } else {
         Biome biome = chunkGen.getBiomeProvider().getBiome(new BlockPos((chunkPosX << 4) + 9, 0, (chunkPosZ << 4) + 9), Biomes.DEFAULT);
         return chunkGen.hasStructure(biome, Feature.FORTRESS);
      }
   }

   protected boolean isEnabledIn(IWorld worldIn) {
      return worldIn.getWorldInfo().isMapFeaturesEnabled();
   }

   protected StructureStart makeStart(IWorld worldIn, IChunkGenerator<?> generator, SharedSeedRandom random, int x, int z) {
      Biome biome = generator.getBiomeProvider().getBiome(new BlockPos((x << 4) + 9, 0, (z << 4) + 9), Biomes.DEFAULT);
      return new FortressStructure.Start(worldIn, random, x, z, biome);
   }

   protected String getStructureName() {
      return "Fortress";
   }

   public int getSize() {
      return 8;
   }

   public List<Biome.SpawnListEntry> getSpawnList() {
      return field_202381_d;
   }

   public static class Start extends StructureStart {
      public Start() {
      }

      public Start(IWorld p_i48727_1_, SharedSeedRandom p_i48727_2_, int p_i48727_3_, int p_i48727_4_, Biome p_i48727_5_) {
         super(p_i48727_3_, p_i48727_4_, p_i48727_5_, p_i48727_2_, p_i48727_1_.getSeed());
         FortressPieces.Start fortresspieces$start = new FortressPieces.Start(p_i48727_2_, (p_i48727_3_ << 4) + 2, (p_i48727_4_ << 4) + 2);
         this.components.add(fortresspieces$start);
         fortresspieces$start.buildComponent(fortresspieces$start, this.components, p_i48727_2_);
         List<StructurePiece> list = fortresspieces$start.pendingChildren;

         while(!list.isEmpty()) {
            int i = p_i48727_2_.nextInt(list.size());
            StructurePiece structurepiece = list.remove(i);
            structurepiece.buildComponent(fortresspieces$start, this.components, p_i48727_2_);
         }

         this.recalculateStructureSize(p_i48727_1_);
         this.setRandomHeight(p_i48727_1_, p_i48727_2_, 48, 70);
      }
   }
}