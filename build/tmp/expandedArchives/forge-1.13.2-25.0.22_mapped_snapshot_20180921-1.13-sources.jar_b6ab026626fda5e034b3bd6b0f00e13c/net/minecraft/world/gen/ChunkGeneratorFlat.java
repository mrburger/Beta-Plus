package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.PhantomSpawner;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.WorldCarverWrapper;
import net.minecraft.world.gen.feature.CompositeFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.surfacebuilders.CompositeSurfaceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkGeneratorFlat extends AbstractChunkGenerator<FlatGenSettings> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final FlatGenSettings settings;
   private final Biome field_202103_f;
   private final PhantomSpawner phantomSpawner = new PhantomSpawner();

   public ChunkGeneratorFlat(IWorld p_i48958_1_, BiomeProvider p_i48958_2_, FlatGenSettings p_i48958_3_) {
      super(p_i48958_1_, p_i48958_2_);
      this.settings = p_i48958_3_;
      this.field_202103_f = this.func_202099_e();
   }

   private Biome func_202099_e() {
      Biome biome = this.settings.getBiome();
      ChunkGeneratorFlat.BiomeWrapper chunkgeneratorflat$biomewrapper = new ChunkGeneratorFlat.BiomeWrapper(biome.getSurfaceBuilder(), biome.getPrecipitation(), biome.getCategory(), biome.getDepth(), biome.getScale(), biome.getDefaultTemperature(), biome.getDownfall(), biome.getWaterColor(), biome.getWaterFogColor(), biome.getParent());
      Map<String, Map<String, String>> map = this.settings.getWorldFeatures();

      for(String s : map.keySet()) {
         CompositeFeature<?, ?>[] compositefeature = FlatGenSettings.field_202247_j.get(s);
         if (compositefeature != null) {
            for(CompositeFeature<?, ?> compositefeature1 : compositefeature) {
               chunkgeneratorflat$biomewrapper.addFeature(FlatGenSettings.field_202248_k.get(compositefeature1), compositefeature1);
               Feature<?> feature = compositefeature1.getFeature();
               if (feature instanceof Structure) {
                  IFeatureConfig ifeatureconfig = biome.getStructureConfig((Structure)feature);
                  chunkgeneratorflat$biomewrapper.addStructure((Structure)feature, ifeatureconfig != null ? ifeatureconfig : FlatGenSettings.field_202249_l.get(compositefeature1));
               }
            }
         }
      }

      boolean flag = (!this.settings.isAllAir() || biome == Biomes.THE_VOID) && map.containsKey("decoration");
      if (flag) {
         List<GenerationStage.Decoration> list = Lists.newArrayList();
         list.add(GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
         list.add(GenerationStage.Decoration.SURFACE_STRUCTURES);

         for(GenerationStage.Decoration generationstage$decoration : GenerationStage.Decoration.values()) {
            if (!list.contains(generationstage$decoration)) {
               for(CompositeFeature<?, ?> compositefeature2 : biome.getFeatures(generationstage$decoration)) {
                  chunkgeneratorflat$biomewrapper.addFeature(generationstage$decoration, compositefeature2);
               }
            }
         }
      }

      return chunkgeneratorflat$biomewrapper;
   }

   public void makeBase(IChunk chunkIn) {
      ChunkPos chunkpos = chunkIn.getPos();
      int i = chunkpos.x;
      int j = chunkpos.z;
      Biome[] abiome = this.biomeProvider.getBiomeBlock(i * 16, j * 16, 16, 16);
      chunkIn.setBiomes(abiome);
      this.func_202100_a(i, j, chunkIn);
      chunkIn.createHeightMap(Heightmap.Type.WORLD_SURFACE_WG, Heightmap.Type.OCEAN_FLOOR_WG);
      chunkIn.setStatus(ChunkStatus.BASE);
   }

   public void carve(WorldGenRegion region, GenerationStage.Carving carvingStage) {
      int i = 8;
      int j = region.getMainChunkX();
      int k = region.getMainChunkZ();
      BitSet bitset = new BitSet(65536);
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom();

      for(int l = j - 8; l <= j + 8; ++l) {
         for(int i1 = k - 8; i1 <= k + 8; ++i1) {
            List<WorldCarverWrapper<?>> list = this.field_202103_f.getCarvers(GenerationStage.Carving.AIR);
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

   public FlatGenSettings getSettings() {
      return this.settings;
   }

   public double[] generateNoiseRegion(int x, int z) {
      return new double[0];
   }

   public int getGroundHeight() {
      IChunk ichunk = this.world.getChunk(0, 0);
      return ichunk.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 8, 8);
   }

   public void decorate(WorldGenRegion region) {
      int i = region.getMainChunkX();
      int j = region.getMainChunkZ();
      int k = i * 16;
      int l = j * 16;
      BlockPos blockpos = new BlockPos(k, 0, l);
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
      long i1 = sharedseedrandom.setDecorationSeed(region.getSeed(), k, l);

      for(GenerationStage.Decoration generationstage$decoration : GenerationStage.Decoration.values()) {
         this.field_202103_f.decorate(generationstage$decoration, this, region, i1, sharedseedrandom, blockpos);
      }

   }

   public void spawnMobs(WorldGenRegion region) {
   }

   public void func_202100_a(int p_202100_1_, int p_202100_2_, IChunk p_202100_3_) {
      IBlockState[] aiblockstate = this.settings.getStates();
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int i = 0; i < aiblockstate.length; ++i) {
         IBlockState iblockstate = aiblockstate[i];
         if (iblockstate != null) {
            for(int j = 0; j < 16; ++j) {
               for(int k = 0; k < 16; ++k) {
                  p_202100_3_.setBlockState(blockpos$mutableblockpos.setPos(j, i, k), iblockstate, false);
               }
            }
         }
      }

   }

   public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      Biome biome = this.world.getBiome(pos);
      return biome.getSpawns(creatureType);
   }

   public int spawnMobs(World worldIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs) {
      int i = 0;
      i = i + this.phantomSpawner.spawnMobs(worldIn, spawnHostileMobs, spawnPeacefulMobs);
      return i;
   }

   public boolean hasStructure(Biome biomeIn, Structure<? extends IFeatureConfig> structureIn) {
      return this.field_202103_f.hasStructure(structureIn);
   }

   @Nullable
   public IFeatureConfig getStructureConfig(Biome biomeIn, Structure<? extends IFeatureConfig> structureIn) {
      return this.field_202103_f.getStructureConfig(structureIn);
   }

   @Nullable
   public BlockPos findNearestStructure(World worldIn, String name, BlockPos pos, int radius, boolean p_211403_5_) {
      return !this.settings.getWorldFeatures().keySet().contains(name) ? null : super.findNearestStructure(worldIn, name, pos, radius, p_211403_5_);
   }

   class BiomeWrapper extends Biome {
      protected BiomeWrapper(CompositeSurfaceBuilder<?> surfaceBuilder, Biome.RainType precipitation, Biome.Category category, float depth, float scale, float temperature, float downfall, int waterColor, int waterFogColor, @Nullable String parent) {
         super((new Biome.BiomeBuilder()).surfaceBuilder(surfaceBuilder).precipitation(precipitation).category(category).depth(depth).scale(scale).temperature(temperature).downfall(downfall).waterColor(waterColor).waterFogColor(waterFogColor).parent(parent));
      }
   }
}