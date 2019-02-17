package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Biomes;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class OceanMonumentStructure extends Structure<OceanMonumentConfig> {
   private static final List<Biome.SpawnListEntry> MONUMENT_ENEMIES = Lists.newArrayList(new Biome.SpawnListEntry(EntityType.GUARDIAN, 1, 2, 4));

   protected ChunkPos getStartPositionForPosition(IChunkGenerator<?> chunkGenerator, Random random, int x, int z, int spacingOffsetsX, int spacingOffsetsZ) {
      int i = chunkGenerator.getSettings().getOceanMonumentSpacing();
      int j = chunkGenerator.getSettings().getOceanMonumentSeparation();
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
         for(Biome biome : chunkGen.getBiomeProvider().getBiomesInSquare(chunkPosX * 16 + 9, chunkPosZ * 16 + 9, 16)) {
            if (!chunkGen.hasStructure(biome, Feature.OCEAN_MONUMENT)) {
               return false;
            }
         }

         for(Biome biome1 : chunkGen.getBiomeProvider().getBiomesInSquare(chunkPosX * 16 + 9, chunkPosZ * 16 + 9, 29)) {
            if (biome1.getCategory() != Biome.Category.OCEAN && biome1.getCategory() != Biome.Category.RIVER) {
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
      return new OceanMonumentStructure.Start(worldIn, random, x, z, biome);
   }

   protected String getStructureName() {
      return "Monument";
   }

   public int getSize() {
      return 8;
   }

   public List<Biome.SpawnListEntry> getSpawnList() {
      return MONUMENT_ENEMIES;
   }

   public static class Start extends StructureStart {
      private final Set<ChunkPos> processed = Sets.newHashSet();
      private boolean wasCreated;

      public Start() {
      }

      public Start(IWorld p_i48754_1_, SharedSeedRandom p_i48754_2_, int p_i48754_3_, int p_i48754_4_, Biome p_i48754_5_) {
         super(p_i48754_3_, p_i48754_4_, p_i48754_5_, p_i48754_2_, p_i48754_1_.getSeed());
         this.create(p_i48754_1_, p_i48754_2_, p_i48754_3_, p_i48754_4_);
      }

      private void create(IBlockReader worldIn, Random random, int chunkX, int chunkZ) {
         int i = chunkX * 16 - 29;
         int j = chunkZ * 16 - 29;
         EnumFacing enumfacing = EnumFacing.Plane.HORIZONTAL.random(random);
         this.components.add(new OceanMonumentPieces.MonumentBuilding(random, i, j, enumfacing));
         this.recalculateStructureSize(worldIn);
         this.wasCreated = true;
      }

      /**
       * Keeps iterating Structure Pieces and spawning them until the checks tell it to stop
       */
      public void generateStructure(IWorld worldIn, Random rand, MutableBoundingBox structurebb, ChunkPos p_75068_4_) {
         if (!this.wasCreated) {
            this.components.clear();
            this.create(worldIn, rand, this.getChunkPosX(), this.getChunkPosZ());
         }

         super.generateStructure(worldIn, rand, structurebb, p_75068_4_);
      }

      public void notifyPostProcessAt(ChunkPos pair) {
         super.notifyPostProcessAt(pair);
         this.processed.add(pair);
      }

      public void writeAdditional(NBTTagCompound tagCompound) {
         super.writeAdditional(tagCompound);
         NBTTagList nbttaglist = new NBTTagList();

         for(ChunkPos chunkpos : this.processed) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setInt("X", chunkpos.x);
            nbttagcompound.setInt("Z", chunkpos.z);
            nbttaglist.add((INBTBase)nbttagcompound);
         }

         tagCompound.setTag("Processed", nbttaglist);
      }

      public void readAdditional(NBTTagCompound tagCompound) {
         super.readAdditional(tagCompound);
         if (tagCompound.contains("Processed", 9)) {
            NBTTagList nbttaglist = tagCompound.getList("Processed", 10);

            for(int i = 0; i < nbttaglist.size(); ++i) {
               NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
               this.processed.add(new ChunkPos(nbttagcompound.getInt("X"), nbttagcompound.getInt("Z")));
            }
         }

      }
   }
}