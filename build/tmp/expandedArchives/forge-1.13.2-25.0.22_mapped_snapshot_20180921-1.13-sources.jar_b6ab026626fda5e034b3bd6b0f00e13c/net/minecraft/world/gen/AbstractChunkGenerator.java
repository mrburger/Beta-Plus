package net.minecraft.world.gen;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFalling;
import net.minecraft.init.Blocks;
import net.minecraft.util.ExpiringMap;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.WorldCarverWrapper;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;

public abstract class AbstractChunkGenerator<C extends IChunkGenSettings> implements IChunkGenerator<C> {
   protected final IWorld world;
   protected final long seed;
   protected final BiomeProvider biomeProvider;
   protected final Map<Structure<? extends IFeatureConfig>, Long2ObjectMap<StructureStart>> structureStartCache = Maps.newHashMap();
   protected final Map<Structure<? extends IFeatureConfig>, Long2ObjectMap<LongSet>> structureReferenceCache = Maps.newHashMap();

   public AbstractChunkGenerator(IWorld worldIn, BiomeProvider biomeProviderIn) {
      this.world = worldIn;
      this.seed = worldIn.getSeed();
      this.biomeProvider = biomeProviderIn;
   }

   public void carve(WorldGenRegion region, GenerationStage.Carving carvingStage) {
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom(this.seed);
      int i = 8;
      int j = region.getMainChunkX();
      int k = region.getMainChunkZ();
      BitSet bitset = region.getChunk(j, k).getCarvingMask(carvingStage);

      for(int l = j - 8; l <= j + 8; ++l) {
         for(int i1 = k - 8; i1 <= k + 8; ++i1) {
            List<WorldCarverWrapper<?>> list = region.getChunkProvider().getChunkGenerator().getBiomeProvider().getBiome(new BlockPos(l * 16, 0, i1 * 16), (Biome)null).getCarvers(carvingStage);
            ListIterator<WorldCarverWrapper<?>> listiterator = list.listIterator();

            while(listiterator.hasNext()) {
               int j1 = listiterator.nextIndex();
               WorldCarverWrapper<?> worldcarverwrapper = listiterator.next();
               sharedseedrandom.setLargeFeatureSeed(region.getWorld().getSeed() + (long)j1, l, i1);
               if (worldcarverwrapper.func_212246_a(region, sharedseedrandom, l, i1, IFeatureConfig.NO_FEATURE_CONFIG)) {
                  worldcarverwrapper.carve(region, sharedseedrandom, l, i1, j, k, bitset, IFeatureConfig.NO_FEATURE_CONFIG);
               }
            }
         }
      }

   }

   @Nullable
   public BlockPos findNearestStructure(World worldIn, String name, BlockPos pos, int radius, boolean p_211403_5_) {
      Structure<?> structure = Feature.STRUCTURES.get(name.toLowerCase(Locale.ROOT));
      return structure != null ? structure.findNearest(worldIn, this, pos, radius, p_211403_5_) : null;
   }

   protected void makeBedrock(IChunk chunkIn, Random random) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      int i = chunkIn.getPos().getXStart();
      int j = chunkIn.getPos().getZStart();

      for(BlockPos blockpos : BlockPos.getAllInBox(i, 0, j, i + 16, 0, j + 16)) {
         for(int k = 4; k >= 0; --k) {
            if (k <= random.nextInt(5)) {
               chunkIn.setBlockState(blockpos$mutableblockpos.setPos(blockpos.getX(), k, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
            }
         }
      }

   }

   public void decorate(WorldGenRegion region) {
      BlockFalling.fallInstantly = true;
      int i = region.getMainChunkX();
      int j = region.getMainChunkZ();
      int k = i * 16;
      int l = j * 16;
      BlockPos blockpos = new BlockPos(k, 0, l);
      Biome biome = region.getChunk(i + 1, j + 1).getBiomes()[0];
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
      long i1 = sharedseedrandom.setDecorationSeed(region.getSeed(), k, l);

      for(GenerationStage.Decoration generationstage$decoration : GenerationStage.Decoration.values()) {
         biome.decorate(generationstage$decoration, this, region, i1, sharedseedrandom, blockpos);
      }

      BlockFalling.fallInstantly = false;
   }

   public void buildSurface(IChunk chunkIn, Biome[] biomesIn, SharedSeedRandom random, int seaLevel) {
      if (!net.minecraftforge.event.ForgeEventFactory.onReplaceBiomeBlocks(this, chunkIn, this.world)) return;
      double d0 = 0.03125D;
      ChunkPos chunkpos = chunkIn.getPos();
      int i = chunkpos.getXStart();
      int j = chunkpos.getZStart();
      double[] adouble = this.generateNoiseRegion(chunkpos.x, chunkpos.z);

      for(int k = 0; k < 16; ++k) {
         for(int l = 0; l < 16; ++l) {
            int i1 = i + k;
            int j1 = j + l;
            int k1 = chunkIn.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, k, l) + 1;
            biomesIn[l * 16 + k].buildSurface(random, chunkIn, i1, j1, k1, adouble[l * 16 + k], this.getSettings().getDefaultBlock(), this.getSettings().getDefaultFluid(), seaLevel, this.world.getSeed());
         }
      }

   }

   public abstract C getSettings();

   public abstract double[] generateNoiseRegion(int x, int z);

   public boolean hasStructure(Biome biomeIn, Structure<? extends IFeatureConfig> structureIn) {
      return biomeIn.hasStructure(structureIn);
   }

   @Nullable
   public IFeatureConfig getStructureConfig(Biome biomeIn, Structure<? extends IFeatureConfig> structureIn) {
      return biomeIn.getStructureConfig(structureIn);
   }

   public BiomeProvider getBiomeProvider() {
      return this.biomeProvider;
   }

   public long getSeed() {
      return this.seed;
   }

   public Long2ObjectMap<StructureStart> getStructureReferenceToStartMap(Structure<? extends IFeatureConfig> structureIn) {
      return this.structureStartCache.computeIfAbsent(structureIn, (p_203225_0_) -> {
         return Long2ObjectMaps.synchronize(new ExpiringMap<>(8192, 10000));
      });
   }

   public Long2ObjectMap<LongSet> getStructurePositionToReferenceMap(Structure<? extends IFeatureConfig> structureIn) {
      return this.structureReferenceCache.computeIfAbsent(structureIn, (p_203226_0_) -> {
         return Long2ObjectMaps.synchronize(new ExpiringMap<>(8192, 10000));
      });
   }

   public int getMaxHeight() {
      return 256;
   }
}