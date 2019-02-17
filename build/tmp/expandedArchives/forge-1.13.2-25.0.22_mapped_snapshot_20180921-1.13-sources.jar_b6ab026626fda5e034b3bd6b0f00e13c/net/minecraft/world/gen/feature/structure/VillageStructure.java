package net.minecraft.world.gen.feature.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Biomes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class VillageStructure extends Structure<VillageConfig> {
   public String getStructureName() {
      return "Village";
   }

   public int getSize() {
      return 8;
   }

   protected boolean isEnabledIn(IWorld worldIn) {
      return worldIn.getWorldInfo().isMapFeaturesEnabled();
   }

   protected ChunkPos getStartPositionForPosition(IChunkGenerator<?> chunkGenerator, Random random, int x, int z, int spacingOffsetsX, int spacingOffsetsZ) {
      int i = chunkGenerator.getSettings().getVillageDistance();
      int j = chunkGenerator.getSettings().getVillageSeparation();
      int k = x + i * spacingOffsetsX;
      int l = z + i * spacingOffsetsZ;
      int i1 = k < 0 ? k - i + 1 : k;
      int j1 = l < 0 ? l - i + 1 : l;
      int k1 = i1 / i;
      int l1 = j1 / i;
      ((SharedSeedRandom)random).setLargeFeatureSeedWithSalt(chunkGenerator.getSeed(), k1, l1, 10387312);
      k1 = k1 * i;
      l1 = l1 * i;
      k1 = k1 + random.nextInt(i - j);
      l1 = l1 + random.nextInt(i - j);
      return new ChunkPos(k1, l1);
   }

   protected boolean hasStartAt(IChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ) {
      ChunkPos chunkpos = this.getStartPositionForPosition(chunkGen, rand, chunkPosX, chunkPosZ, 0, 0);
      if (chunkPosX == chunkpos.x && chunkPosZ == chunkpos.z) {
         Biome biome = chunkGen.getBiomeProvider().getBiome(new BlockPos((chunkPosX << 4) + 9, 0, (chunkPosZ << 4) + 9), Biomes.DEFAULT);
         return chunkGen.hasStructure(biome, Feature.VILLAGE);
      } else {
         return false;
      }
   }

   protected StructureStart makeStart(IWorld worldIn, IChunkGenerator<?> generator, SharedSeedRandom random, int x, int z) {
      Biome biome = generator.getBiomeProvider().getBiome(new BlockPos((x << 4) + 9, 0, (z << 4) + 9), Biomes.DEFAULT);
      return new VillageStructure.Start(worldIn, generator, random, x, z, biome);
   }

   public static class Start extends StructureStart {
      /** well ... thats what it does */
      private boolean hasMoreThanTwoComponents;

      public Start() {
      }

      public Start(IWorld p_i48753_1_, IChunkGenerator<?> p_i48753_2_, SharedSeedRandom p_i48753_3_, int p_i48753_4_, int p_i48753_5_, Biome p_i48753_6_) {
         super(p_i48753_4_, p_i48753_5_, p_i48753_6_, p_i48753_3_, p_i48753_1_.getSeed());
         VillageConfig villageconfig = (VillageConfig)p_i48753_2_.getStructureConfig(p_i48753_6_, Feature.VILLAGE);
         List<VillagePieces.PieceWeight> list = VillagePieces.getStructureVillageWeightedPieceList(p_i48753_3_, villageconfig.field_202461_a);
         VillagePieces.Start villagepieces$start = new VillagePieces.Start(0, p_i48753_3_, (p_i48753_4_ << 4) + 2, (p_i48753_5_ << 4) + 2, list, villageconfig, p_i48753_6_);
         this.components.add(villagepieces$start);
         villagepieces$start.buildComponent(villagepieces$start, this.components, p_i48753_3_);
         List<StructurePiece> list1 = villagepieces$start.pendingRoads;
         List<StructurePiece> list2 = villagepieces$start.pendingHouses;

         while(!list1.isEmpty() || !list2.isEmpty()) {
            if (list1.isEmpty()) {
               int i = p_i48753_3_.nextInt(list2.size());
               StructurePiece structurepiece = list2.remove(i);
               structurepiece.buildComponent(villagepieces$start, this.components, p_i48753_3_);
            } else {
               int j = p_i48753_3_.nextInt(list1.size());
               StructurePiece structurepiece2 = list1.remove(j);
               structurepiece2.buildComponent(villagepieces$start, this.components, p_i48753_3_);
            }
         }

         this.recalculateStructureSize(p_i48753_1_);
         int k = 0;

         for(StructurePiece structurepiece1 : this.components) {
            if (!(structurepiece1 instanceof VillagePieces.Road)) {
               ++k;
            }
         }

         this.hasMoreThanTwoComponents = k > 2;
      }

      /**
       * currently only defined for Villages, returns true if Village has more than 2 non-road components
       */
      public boolean isSizeableStructure() {
         return this.hasMoreThanTwoComponents;
      }

      public void writeAdditional(NBTTagCompound tagCompound) {
         super.writeAdditional(tagCompound);
         tagCompound.setBoolean("Valid", this.hasMoreThanTwoComponents);
      }

      public void readAdditional(NBTTagCompound tagCompound) {
         super.readAdditional(tagCompound);
         this.hasMoreThanTwoComponents = tagCompound.getBoolean("Valid");
      }
   }
}