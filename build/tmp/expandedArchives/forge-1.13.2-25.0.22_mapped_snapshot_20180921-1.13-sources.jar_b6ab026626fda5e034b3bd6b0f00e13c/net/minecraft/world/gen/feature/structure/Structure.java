package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Structure<C extends IFeatureConfig> extends Feature<C> {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final StructureStart NO_STRUCTURE = new StructureStart() {
      /**
       * currently only defined for Villages, returns true if Village has more than 2 non-road components
       */
      public boolean isSizeableStructure() {
         return false;
      }
   };

   public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, C p_212245_5_) {
      if (!this.isEnabledIn(p_212245_1_)) {
         return false;
      } else {
         int i = this.getSize();
         int j = p_212245_4_.getX() >> 4;
         int k = p_212245_4_.getZ() >> 4;
         int l = j << 4;
         int i1 = k << 4;
         long j1 = ChunkPos.asLong(j, k);
         boolean flag = false;

         for(int k1 = j - i; k1 <= j + i; ++k1) {
            for(int l1 = k - i; l1 <= k + i; ++l1) {
               long i2 = ChunkPos.asLong(k1, l1);
               StructureStart structurestart = this.getStructureStart(p_212245_1_, p_212245_2_, (SharedSeedRandom)p_212245_3_, i2);
               if (structurestart != NO_STRUCTURE && structurestart.getBoundingBox().intersectsWith(l, i1, l + 15, i1 + 15)) {
                  p_212245_2_.getStructurePositionToReferenceMap(this).computeIfAbsent(j1, (p_208203_0_) -> {
                     return new LongOpenHashSet();
                  }).add(i2);
                  p_212245_1_.getChunkProvider().provideChunkOrPrimer(j, k, true).addStructureReference(this.getStructureName(), i2);
                  structurestart.generateStructure(p_212245_1_, p_212245_3_, new MutableBoundingBox(l, i1, l + 15, i1 + 15), new ChunkPos(j, k));
                  structurestart.notifyPostProcessAt(new ChunkPos(j, k));
                  flag = true;
               }
            }
         }

         return flag;
      }
   }

   protected StructureStart getStart(IWorld worldIn, BlockPos pos) {
      label31:
      for(StructureStart structurestart : this.getStarts(worldIn, pos.getX() >> 4, pos.getZ() >> 4)) {
         if (structurestart.isSizeableStructure() && structurestart.getBoundingBox().isVecInside(pos)) {
            Iterator<StructurePiece> iterator = structurestart.getComponents().iterator();

            while(true) {
               if (!iterator.hasNext()) {
                  continue label31;
               }

               StructurePiece structurepiece = iterator.next();
               if (structurepiece.getBoundingBox().isVecInside(pos)) {
                  break;
               }
            }

            return structurestart;
         }
      }

      return NO_STRUCTURE;
   }

   public boolean isPositionInStructure(IWorld worldIn, BlockPos pos) {
      for(StructureStart structurestart : this.getStarts(worldIn, pos.getX() >> 4, pos.getZ() >> 4)) {
         if (structurestart.isSizeableStructure() && structurestart.getBoundingBox().isVecInside(pos)) {
            return true;
         }
      }

      return false;
   }

   public boolean isPositionInsideStructure(IWorld worldIn, BlockPos pos) {
      return this.getStart(worldIn, pos).isSizeableStructure();
   }

   @Nullable
   public BlockPos findNearest(World worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, BlockPos pos, int radius, boolean p_211405_5_) {
      if (!chunkGenerator.getBiomeProvider().hasStructure(this)) {
         return null;
      } else {
         int i = pos.getX() >> 4;
         int j = pos.getZ() >> 4;
         int k = 0;

         for(SharedSeedRandom sharedseedrandom = new SharedSeedRandom(); k <= radius; ++k) {
            for(int l = -k; l <= k; ++l) {
               boolean flag = l == -k || l == k;

               for(int i1 = -k; i1 <= k; ++i1) {
                  boolean flag1 = i1 == -k || i1 == k;
                  if (flag || flag1) {
                     ChunkPos chunkpos = this.getStartPositionForPosition(chunkGenerator, sharedseedrandom, i, j, l, i1);
                     StructureStart structurestart = this.getStructureStart(worldIn, chunkGenerator, sharedseedrandom, chunkpos.asLong());
                     if (structurestart != NO_STRUCTURE) {
                        if (p_211405_5_ && structurestart.func_212687_g()) {
                           structurestart.func_212685_h();
                           return structurestart.getPos();
                        }

                        if (!p_211405_5_) {
                           return structurestart.getPos();
                        }
                     }

                     if (k == 0) {
                        break;
                     }
                  }
               }

               if (k == 0) {
                  break;
               }
            }
         }

         return null;
      }
   }

   private List<StructureStart> getStarts(IWorld worldIn, int x, int z) {
      List<StructureStart> list = Lists.newArrayList();
      Long2ObjectMap<StructureStart> long2objectmap = worldIn.getChunkProvider().getChunkGenerator().getStructureReferenceToStartMap(this);
      Long2ObjectMap<LongSet> long2objectmap1 = worldIn.getChunkProvider().getChunkGenerator().getStructurePositionToReferenceMap(this);
      long i = ChunkPos.asLong(x, z);
      LongSet longset = long2objectmap1.get(i);
      if (longset == null) {
         longset = worldIn.getChunkProvider().provideChunkOrPrimer(x, z, true).getStructureReferences(this.getStructureName());
         long2objectmap1.put(i, longset);
      }

      for(Long olong : longset) {
         StructureStart structurestart = long2objectmap.get(olong);
         if (structurestart != null) {
            list.add(structurestart);
         } else {
            ChunkPos chunkpos = new ChunkPos(olong);
            IChunk ichunk = worldIn.getChunkProvider().provideChunkOrPrimer(chunkpos.x, chunkpos.z, true);
            structurestart = ichunk.getStructureStart(this.getStructureName());
            if (structurestart != null) {
               long2objectmap.put(olong, structurestart);
               list.add(structurestart);
            }
         }
      }

      return list;
   }

   private StructureStart getStructureStart(IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> generator, SharedSeedRandom rand, long packedChunkPos) {
      if (!generator.getBiomeProvider().hasStructure(this)) {
         return NO_STRUCTURE;
      } else {
         Long2ObjectMap<StructureStart> long2objectmap = generator.getStructureReferenceToStartMap(this);
         StructureStart structurestart = long2objectmap.get(packedChunkPos);
         if (structurestart != null) {
            return structurestart;
         } else {
            ChunkPos chunkpos = new ChunkPos(packedChunkPos);
            IChunk ichunk = worldIn.getChunkProvider().provideChunkOrPrimer(chunkpos.x, chunkpos.z, false);
            if (ichunk != null) {
               structurestart = ichunk.getStructureStart(this.getStructureName());
               if (structurestart != null) {
                  long2objectmap.put(packedChunkPos, structurestart);
                  return structurestart;
               }
            }

            if (this.hasStartAt(generator, rand, chunkpos.x, chunkpos.z)) {
               StructureStart structurestart1 = this.makeStart(worldIn, generator, rand, chunkpos.x, chunkpos.z);
               structurestart = structurestart1.isSizeableStructure() ? structurestart1 : NO_STRUCTURE;
            } else {
               structurestart = NO_STRUCTURE;
            }

            if (structurestart.isSizeableStructure()) {
               worldIn.getChunkProvider().provideChunkOrPrimer(chunkpos.x, chunkpos.z, true).putStructureStart(this.getStructureName(), structurestart);
            }

            long2objectmap.put(packedChunkPos, structurestart);
            return structurestart;
         }
      }
   }

   protected ChunkPos getStartPositionForPosition(IChunkGenerator<?> chunkGenerator, Random random, int x, int z, int spacingOffsetsX, int spacingOffsetsZ) {
      return new ChunkPos(x + spacingOffsetsX, z + spacingOffsetsZ);
   }

   protected abstract boolean hasStartAt(IChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ);

   protected abstract boolean isEnabledIn(IWorld worldIn);

   protected abstract StructureStart makeStart(IWorld worldIn, IChunkGenerator<?> generator, SharedSeedRandom random, int x, int z);

   protected abstract String getStructureName();

   public abstract int getSize();
}