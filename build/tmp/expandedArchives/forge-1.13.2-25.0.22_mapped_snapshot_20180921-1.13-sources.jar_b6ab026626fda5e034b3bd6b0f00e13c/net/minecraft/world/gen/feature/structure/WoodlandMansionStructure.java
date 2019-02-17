package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class WoodlandMansionStructure extends Structure<WoodlandMansionConfig> {
   protected ChunkPos getStartPositionForPosition(IChunkGenerator<?> chunkGenerator, Random random, int x, int z, int spacingOffsetsX, int spacingOffsetsZ) {
      int i = chunkGenerator.getSettings().getMansionDistance();
      int j = chunkGenerator.getSettings().getMansionSeparation();
      int k = x + i * spacingOffsetsX;
      int l = z + i * spacingOffsetsZ;
      int i1 = k < 0 ? k - i + 1 : k;
      int j1 = l < 0 ? l - i + 1 : l;
      int k1 = i1 / i;
      int l1 = j1 / i;
      ((SharedSeedRandom)random).setLargeFeatureSeedWithSalt(chunkGenerator.getSeed(), k1, l1, 10387319);
      k1 = k1 * i;
      l1 = l1 * i;
      k1 = k1 + (random.nextInt(i - j) + random.nextInt(i - j)) / 2;
      l1 = l1 + (random.nextInt(i - j) + random.nextInt(i - j)) / 2;
      return new ChunkPos(k1, l1);
   }

   protected boolean hasStartAt(IChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ) {
      ChunkPos chunkpos = this.getStartPositionForPosition(chunkGen, rand, chunkPosX, chunkPosZ, 0, 0);
      if (chunkPosX == chunkpos.x && chunkPosZ == chunkpos.z) {
         for(Biome biome : chunkGen.getBiomeProvider().getBiomesInSquare(chunkPosX * 16 + 9, chunkPosZ * 16 + 9, 32)) {
            if (!chunkGen.hasStructure(biome, Feature.WOODLAND_MANSION)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean isEnabledIn(IWorld worldIn) {
      return worldIn.getWorldInfo().isMapFeaturesEnabled();
   }

   protected StructureStart makeStart(IWorld worldIn, IChunkGenerator<?> generator, SharedSeedRandom random, int x, int z) {
      Biome biome = generator.getBiomeProvider().getBiome(new BlockPos((x << 4) + 9, 0, (z << 4) + 9), Biomes.DEFAULT);
      return new WoodlandMansionStructure.Start(worldIn, generator, random, x, z, biome);
   }

   protected String getStructureName() {
      return "Mansion";
   }

   public int getSize() {
      return 8;
   }

   public static class Start extends StructureStart {
      private boolean isValid;

      public Start() {
      }

      public Start(IWorld world, IChunkGenerator<?> generator, SharedSeedRandom seed, int x, int z, Biome biome) {
         super(x, z, biome, seed, world.getSeed());
         Rotation rotation = Rotation.values()[seed.nextInt(Rotation.values().length)];
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

         ChunkPrimer chunkprimer = new ChunkPrimer(new ChunkPos(x, z), UpgradeData.EMPTY);
         generator.makeBase(chunkprimer);
         int k = chunkprimer.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7, 7);
         int l = chunkprimer.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7, 7 + j);
         int i1 = chunkprimer.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7 + i, 7);
         int j1 = chunkprimer.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7 + i, 7 + j);
         int k1 = Math.min(Math.min(k, l), Math.min(i1, j1));
         if (k1 < 60) {
            this.isValid = false;
         } else {
            BlockPos blockpos = new BlockPos(x * 16 + 8, k1 + 1, z * 16 + 8);
            List<WoodlandMansionPieces.MansionTemplate> list = Lists.newLinkedList();
            WoodlandMansionPieces.generateMansion(world.getSaveHandler().getStructureTemplateManager(), blockpos, rotation, list, seed);
            this.components.addAll(list);
            this.recalculateStructureSize(world);
            this.isValid = true;
         }
      }

      /**
       * Keeps iterating Structure Pieces and spawning them until the checks tell it to stop
       */
      public void generateStructure(IWorld worldIn, Random rand, MutableBoundingBox structurebb, ChunkPos p_75068_4_) {
         super.generateStructure(worldIn, rand, structurebb, p_75068_4_);
         int i = this.boundingBox.minY;

         for(int j = structurebb.minX; j <= structurebb.maxX; ++j) {
            for(int k = structurebb.minZ; k <= structurebb.maxZ; ++k) {
               BlockPos blockpos = new BlockPos(j, i, k);
               if (!worldIn.isAirBlock(blockpos) && this.boundingBox.isVecInside(blockpos)) {
                  boolean flag = false;

                  for(StructurePiece structurepiece : this.components) {
                     if (structurepiece.getBoundingBox().isVecInside(blockpos)) {
                        flag = true;
                        break;
                     }
                  }

                  if (flag) {
                     for(int l = i - 1; l > 1; --l) {
                        BlockPos blockpos1 = new BlockPos(j, l, k);
                        if (!worldIn.isAirBlock(blockpos1) && !worldIn.getBlockState(blockpos1).getMaterial().isLiquid()) {
                           break;
                        }

                        worldIn.setBlockState(blockpos1, Blocks.COBBLESTONE.getDefaultState(), 2);
                     }
                  }
               }
            }
         }

      }

      /**
       * currently only defined for Villages, returns true if Village has more than 2 non-road components
       */
      public boolean isSizeableStructure() {
         return this.isValid;
      }
   }
}