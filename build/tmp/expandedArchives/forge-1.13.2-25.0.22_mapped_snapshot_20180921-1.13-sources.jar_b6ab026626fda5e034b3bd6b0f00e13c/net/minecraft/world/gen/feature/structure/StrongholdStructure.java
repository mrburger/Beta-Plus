package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.init.Biomes;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class StrongholdStructure extends Structure<StrongholdConfig> {
   /** is spawned false and set true once the defined BiomeGenBases were compared with the present ones */
   private boolean ranBiomeCheck;
   private ChunkPos[] structureCoords;
   private long seed;

   protected boolean hasStartAt(IChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ) {
      if (this.seed != chunkGen.getSeed()) {
         this.resetData();
      }

      if (!this.ranBiomeCheck) {
         this.reinitializeData(chunkGen);
         this.ranBiomeCheck = true;
      }

      for(ChunkPos chunkpos : this.structureCoords) {
         if (chunkPosX == chunkpos.x && chunkPosZ == chunkpos.z) {
            return true;
         }
      }

      return false;
   }

   /**
    * Resets the current available data on the stronghold structure, since biome checks and existing structure
    * coordinates are needed to properly generate strongholds.
    */
   private void resetData() {
      this.ranBiomeCheck = false;
      this.structureCoords = null;
   }

   protected boolean isEnabledIn(IWorld worldIn) {
      return worldIn.getWorldInfo().isMapFeaturesEnabled();
   }

   protected StructureStart makeStart(IWorld worldIn, IChunkGenerator<?> generator, SharedSeedRandom random, int x, int z) {
      Biome biome = generator.getBiomeProvider().getBiome(new BlockPos((x << 4) + 9, 0, (z << 4) + 9), Biomes.DEFAULT);
      int i = 0;

      StrongholdStructure.Start strongholdstructure$start;
      for(strongholdstructure$start = new StrongholdStructure.Start(worldIn, random, x, z, biome, i++); strongholdstructure$start.getComponents().isEmpty() || ((StrongholdPieces.Stairs2)strongholdstructure$start.getComponents().get(0)).strongholdPortalRoom == null; strongholdstructure$start = new StrongholdStructure.Start(worldIn, random, x, z, biome, i++)) {
         ;
      }

      return strongholdstructure$start;
   }

   protected String getStructureName() {
      return "Stronghold";
   }

   public int getSize() {
      return 8;
   }

   @Nullable
   public BlockPos findNearest(World worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, BlockPos pos, int radius, boolean p_211405_5_) {
      if (!chunkGenerator.getBiomeProvider().hasStructure(this)) {
         return null;
      } else {
         if (this.seed != worldIn.getSeed()) {
            this.resetData();
         }

         if (!this.ranBiomeCheck) {
            this.reinitializeData(chunkGenerator);
            this.ranBiomeCheck = true;
         }

         BlockPos blockpos = null;
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(0, 0, 0);
         double d0 = Double.MAX_VALUE;

         for(ChunkPos chunkpos : this.structureCoords) {
            blockpos$mutableblockpos.setPos((chunkpos.x << 4) + 8, 32, (chunkpos.z << 4) + 8);
            double d1 = blockpos$mutableblockpos.distanceSq(pos);
            if (blockpos == null) {
               blockpos = new BlockPos(blockpos$mutableblockpos);
               d0 = d1;
            } else if (d1 < d0) {
               blockpos = new BlockPos(blockpos$mutableblockpos);
               d0 = d1;
            }
         }

         return blockpos;
      }
   }

   /**
    * Re-initializes the stronghold information needed to generate strongholds. Due to the requirement to rely on seeds
    * and other settings provided by the chunk generator, each time the structure is used on a different seed, this can
    * be called multiple times during the game lifecycle.
    */
   private void reinitializeData(IChunkGenerator<?> generator) {
      this.seed = generator.getSeed();
      List<Biome> list = Lists.newArrayList();

      for(Biome biome : IRegistry.field_212624_m) {
         if (biome != null && generator.hasStructure(biome, Feature.STRONGHOLD)) {
            list.add(biome);
         }
      }

      int i2 = generator.getSettings().getStrongholdDistance();
      int j2 = generator.getSettings().getStrongholdCount();
      int i = generator.getSettings().getStrongholdSpread();
      this.structureCoords = new ChunkPos[j2];
      int j = 0;
      Long2ObjectMap<StructureStart> long2objectmap = generator.getStructureReferenceToStartMap(this);
      synchronized(long2objectmap) {
         for(StructureStart structurestart : long2objectmap.values()) {
            if (j < this.structureCoords.length) {
               this.structureCoords[j++] = new ChunkPos(structurestart.getChunkPosX(), structurestart.getChunkPosZ());
            }
         }
      }

      Random random = new Random();
      random.setSeed(generator.getSeed());
      double d1 = random.nextDouble() * Math.PI * 2.0D;
      int k = long2objectmap.size();
      if (k < this.structureCoords.length) {
         int l = 0;
         int i1 = 0;

         for(int j1 = 0; j1 < this.structureCoords.length; ++j1) {
            double d0 = (double)(4 * i2 + i2 * i1 * 6) + (random.nextDouble() - 0.5D) * (double)i2 * 2.5D;
            int k1 = (int)Math.round(Math.cos(d1) * d0);
            int l1 = (int)Math.round(Math.sin(d1) * d0);
            BlockPos blockpos = generator.getBiomeProvider().findBiomePosition((k1 << 4) + 8, (l1 << 4) + 8, 112, list, random);
            if (blockpos != null) {
               k1 = blockpos.getX() >> 4;
               l1 = blockpos.getZ() >> 4;
            }

            if (j1 >= k) {
               this.structureCoords[j1] = new ChunkPos(k1, l1);
            }

            d1 += (Math.PI * 2D) / (double)i;
            ++l;
            if (l == i) {
               ++i1;
               l = 0;
               i = i + 2 * i / (i1 + 1);
               i = Math.min(i, this.structureCoords.length - j1);
               d1 += random.nextDouble() * Math.PI * 2.0D;
            }
         }
      }

   }

   public static class Start extends StructureStart {
      public Start() {
      }

      public Start(IWorld worldIn, SharedSeedRandom random, int chunkX, int chunkZ, Biome p_i48716_5_, int seed) {
         super(chunkX, chunkZ, p_i48716_5_, random, worldIn.getSeed() + (long)seed);
         StrongholdPieces.prepareStructurePieces();
         StrongholdPieces.Stairs2 strongholdpieces$stairs2 = new StrongholdPieces.Stairs2(0, random, (chunkX << 4) + 2, (chunkZ << 4) + 2);
         this.components.add(strongholdpieces$stairs2);
         strongholdpieces$stairs2.buildComponent(strongholdpieces$stairs2, this.components, random);
         List<StructurePiece> list = strongholdpieces$stairs2.pendingChildren;

         while(!list.isEmpty()) {
            int i = random.nextInt(list.size());
            StructurePiece structurepiece = list.remove(i);
            structurepiece.buildComponent(strongholdpieces$stairs2, this.components, random);
         }

         this.recalculateStructureSize(worldIn);
         this.markAvailableHeight(worldIn, random, 10);
      }
   }
}