package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.carver.CanyonWorldCarver;
import net.minecraft.world.gen.carver.CaveWorldCarver;
import net.minecraft.world.gen.carver.IWorldCarver;
import net.minecraft.world.gen.carver.NetherCaveWorldCarver;
import net.minecraft.world.gen.carver.UnderwaterCanyonWorldCarver;
import net.minecraft.world.gen.carver.UnderwaterCaveWorldCarver;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.carver.WorldCarverWrapper;
import net.minecraft.world.gen.feature.AbstractFlowersFeature;
import net.minecraft.world.gen.feature.CompositeFeature;
import net.minecraft.world.gen.feature.CompositeFlowerFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.feature.structure.BuriedTreasureConfig;
import net.minecraft.world.gen.feature.structure.DesertPyramidConfig;
import net.minecraft.world.gen.feature.structure.IglooConfig;
import net.minecraft.world.gen.feature.structure.JunglePyramidConfig;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.OceanMonumentConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinStructure;
import net.minecraft.world.gen.feature.structure.ShipwreckConfig;
import net.minecraft.world.gen.feature.structure.StrongholdConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.SwampHutConfig;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.structure.VillagePieces;
import net.minecraft.world.gen.feature.structure.WoodlandMansionConfig;
import net.minecraft.world.gen.placement.AtHeight64;
import net.minecraft.world.gen.placement.AtSurface;
import net.minecraft.world.gen.placement.AtSurfaceRandomCount;
import net.minecraft.world.gen.placement.AtSurfaceWithChance;
import net.minecraft.world.gen.placement.AtSurfaceWithChanceMultiple;
import net.minecraft.world.gen.placement.AtSurfaceWithExtra;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.BasePlacement;
import net.minecraft.world.gen.placement.CaveEdge;
import net.minecraft.world.gen.placement.CaveEdgeConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.ChanceRange;
import net.minecraft.world.gen.placement.ChanceRangeConfig;
import net.minecraft.world.gen.placement.ChorusPlant;
import net.minecraft.world.gen.placement.CountRange;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.DepthAverage;
import net.minecraft.world.gen.placement.DepthAverageConfig;
import net.minecraft.world.gen.placement.DungeonRoom;
import net.minecraft.world.gen.placement.DungeonRoomConfig;
import net.minecraft.world.gen.placement.EndGateway;
import net.minecraft.world.gen.placement.EndIsland;
import net.minecraft.world.gen.placement.EndSpikes;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.Height4To32;
import net.minecraft.world.gen.placement.HeightBiasedRange;
import net.minecraft.world.gen.placement.HeightVeryBiasedRange;
import net.minecraft.world.gen.placement.HeightWithChanceConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.IcebergPlacement;
import net.minecraft.world.gen.placement.LakeChanceConfig;
import net.minecraft.world.gen.placement.LakeLava;
import net.minecraft.world.gen.placement.LakeWater;
import net.minecraft.world.gen.placement.NetherFire;
import net.minecraft.world.gen.placement.NetherGlowstone;
import net.minecraft.world.gen.placement.NetherMagma;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.NoiseDependant;
import net.minecraft.world.gen.placement.Passthrough;
import net.minecraft.world.gen.placement.RandomCountWithRange;
import net.minecraft.world.gen.placement.RoofedTree;
import net.minecraft.world.gen.placement.SurfacePlus32;
import net.minecraft.world.gen.placement.SurfacePlus32WithNoise;
import net.minecraft.world.gen.placement.TopSolid;
import net.minecraft.world.gen.placement.TopSolidOnce;
import net.minecraft.world.gen.placement.TopSolidRange;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraft.world.gen.placement.TopSolidWithChance;
import net.minecraft.world.gen.placement.TopSolidWithNoise;
import net.minecraft.world.gen.placement.TopSolidWithNoiseConfig;
import net.minecraft.world.gen.placement.TwiceSurface;
import net.minecraft.world.gen.placement.TwiceSurfaceWithChance;
import net.minecraft.world.gen.placement.TwiceSurfaceWithChanceMultiple;
import net.minecraft.world.gen.placement.TwiceSurfaceWithNoise;
import net.minecraft.world.gen.placement.WithChance;
import net.minecraft.world.gen.surfacebuilders.CompositeSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.DefaultSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ExtremeHillsMutatedSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ExtremeHillsSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.FrozenOceanSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.MesaBryceSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.MesaForestSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.MesaSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.NetherSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.NoopSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SavanaMutatedSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SwampSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.TaigaMegaSurfaceBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Biome extends net.minecraftforge.registries.ForgeRegistryEntry<Biome> {
   public static final Logger LOGGER = LogManager.getLogger();
   public static final WorldCarver<ProbabilityConfig> CAVE_WORLD_CARVER = new CaveWorldCarver();
   public static final WorldCarver<ProbabilityConfig> NETHER_CAVE_WORLD_CARVER = new NetherCaveWorldCarver();
   public static final WorldCarver<ProbabilityConfig> CANYON_WORLD_CARVER = new CanyonWorldCarver();
   public static final WorldCarver<ProbabilityConfig> UNDERWATER_CANYON_WORLD_CARVER = new UnderwaterCanyonWorldCarver();
   public static final WorldCarver<ProbabilityConfig> UNDERWATER_CAVE_WORLD_CARVER = new UnderwaterCaveWorldCarver();
   public static final BasePlacement<FrequencyConfig> AT_SURFACE = new AtSurface();
   public static final BasePlacement<FrequencyConfig> TOP_SOLID = new TopSolid();
   public static final BasePlacement<FrequencyConfig> SURFACE_PLUS_32 = new SurfacePlus32();
   public static final BasePlacement<FrequencyConfig> TWICE_SURFACE = new TwiceSurface();
   public static final BasePlacement<FrequencyConfig> AT_HEIGHT_64 = new AtHeight64();
   public static final BasePlacement<NoiseDependant> SURFACE_PLUS_32_WITH_NOISE = new SurfacePlus32WithNoise();
   public static final BasePlacement<NoiseDependant> TWICE_SURFACE_WITH_NOISE = new TwiceSurfaceWithNoise();
   public static final BasePlacement<NoPlacementConfig> PASSTHROUGH = new Passthrough();
   public static final BasePlacement<ChanceConfig> AT_SURFACE_WITH_CHANCE = new AtSurfaceWithChance();
   public static final BasePlacement<ChanceConfig> TWICE_SURFACE_WITH_CHANCE = new TwiceSurfaceWithChance();
   public static final BasePlacement<ChanceConfig> WITH_CHANCE = new WithChance();
   public static final BasePlacement<ChanceConfig> TOP_SURFACE_WITH_CHANCE = new TopSolidWithChance();
   public static final BasePlacement<AtSurfaceWithExtraConfig> AT_SURFACE_WITH_EXTRA = new AtSurfaceWithExtra();
   public static final BasePlacement<CountRangeConfig> COUNT_RANGE = new CountRange();
   public static final BasePlacement<CountRangeConfig> HEIGHT_BIASED_RANGE = new HeightBiasedRange();
   public static final BasePlacement<CountRangeConfig> HEIGHT_VERY_BIASED_RANGE = new HeightVeryBiasedRange();
   public static final BasePlacement<CountRangeConfig> RANDOM_COUNT_WITH_RANGE = new RandomCountWithRange();
   public static final BasePlacement<ChanceRangeConfig> CHANCE_RANGE = new ChanceRange();
   public static final BasePlacement<HeightWithChanceConfig> AT_SUFACE_WITH_CHANCE_MULTIPLE = new AtSurfaceWithChanceMultiple();
   public static final BasePlacement<HeightWithChanceConfig> TWICE_SURFACE_WITH_CHANCE_MULTPLE = new TwiceSurfaceWithChanceMultiple();
   public static final BasePlacement<DepthAverageConfig> DEPTH_AVERAGE = new DepthAverage();
   public static final BasePlacement<NoPlacementConfig> TOP_SOLID_ONCE = new TopSolidOnce();
   public static final BasePlacement<TopSolidRangeConfig> TOP_SOLID_RANGE = new TopSolidRange();
   public static final BasePlacement<TopSolidWithNoiseConfig> TOP_SOLID_WITH_NOISE = new TopSolidWithNoise();
   public static final BasePlacement<CaveEdgeConfig> CAVE_EDGE = new CaveEdge();
   public static final BasePlacement<FrequencyConfig> AT_SURFACE_RANDOM_COUNT = new AtSurfaceRandomCount();
   public static final BasePlacement<FrequencyConfig> NETHER_FIRE = new NetherFire();
   public static final BasePlacement<FrequencyConfig> NETHER_MAGMA = new NetherMagma();
   public static final BasePlacement<NoPlacementConfig> HEIGHT_4_TO_32 = new Height4To32();
   public static final BasePlacement<LakeChanceConfig> LAVA_LAKE = new LakeLava();
   public static final BasePlacement<LakeChanceConfig> LAKE_WATER = new LakeWater();
   public static final BasePlacement<DungeonRoomConfig> DUNGEON_ROOM = new DungeonRoom();
   public static final BasePlacement<NoPlacementConfig> ROOFED_TREE = new RoofedTree();
   public static final BasePlacement<ChanceConfig> ICEBERG_PLACEMENT = new IcebergPlacement();
   public static final BasePlacement<FrequencyConfig> NETHER_GLOWSTONE = new NetherGlowstone();
   public static final BasePlacement<NoPlacementConfig> END_SPIKES = new EndSpikes();
   public static final BasePlacement<NoPlacementConfig> END_ISLAND = new EndIsland();
   public static final BasePlacement<NoPlacementConfig> CHORUS_PLANT = new ChorusPlant();
   public static final BasePlacement<NoPlacementConfig> END_GATEWAY = new EndGateway();
   protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
   protected static final IBlockState DIRT = Blocks.DIRT.getDefaultState();
   protected static final IBlockState GRASS_BLOCK = Blocks.GRASS_BLOCK.getDefaultState();
   protected static final IBlockState PODZOL = Blocks.PODZOL.getDefaultState();
   protected static final IBlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
   protected static final IBlockState STONE = Blocks.STONE.getDefaultState();
   protected static final IBlockState COARSE_DIRT = Blocks.COARSE_DIRT.getDefaultState();
   protected static final IBlockState SAND = Blocks.SAND.getDefaultState();
   protected static final IBlockState RED_SAND = Blocks.RED_SAND.getDefaultState();
   protected static final IBlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
   protected static final IBlockState MYCELIUM = Blocks.MYCELIUM.getDefaultState();
   protected static final IBlockState NETHERRACK = Blocks.NETHERRACK.getDefaultState();
   protected static final IBlockState END_STONE = Blocks.END_STONE.getDefaultState();
   public static final SurfaceBuilderConfig AIR_SURFACE = new SurfaceBuilderConfig(AIR, AIR, AIR);
   public static final SurfaceBuilderConfig DIRT_DIRT_GRAVEL_SURFACE = new SurfaceBuilderConfig(DIRT, DIRT, GRAVEL);
   public static final SurfaceBuilderConfig GRASS_DIRT_GRAVEL_SURFACE = new SurfaceBuilderConfig(GRASS_BLOCK, DIRT, GRAVEL);
   public static final SurfaceBuilderConfig STONE_STONE_GRAVEL_SURFACE = new SurfaceBuilderConfig(STONE, STONE, GRAVEL);
   public static final SurfaceBuilderConfig GRAVEL_SURFACE = new SurfaceBuilderConfig(GRAVEL, GRAVEL, GRAVEL);
   public static final SurfaceBuilderConfig COARSE_DIRT_DIRT_GRAVEL_SURFACE = new SurfaceBuilderConfig(COARSE_DIRT, DIRT, GRAVEL);
   public static final SurfaceBuilderConfig PODZOL_DIRT_GRAVEL_SURFACE = new SurfaceBuilderConfig(PODZOL, DIRT, GRAVEL);
   public static final SurfaceBuilderConfig SAND_SURFACE = new SurfaceBuilderConfig(SAND, SAND, SAND);
   public static final SurfaceBuilderConfig GRASS_DIRT_SAND_SURFACE = new SurfaceBuilderConfig(GRASS_BLOCK, DIRT, SAND);
   public static final SurfaceBuilderConfig SAND_SAND_GRAVEL_SURFACE = new SurfaceBuilderConfig(SAND, SAND, GRAVEL);
   public static final SurfaceBuilderConfig RED_SAND_WHITE_TERRACOTTA_GRAVEL_SURFACE = new SurfaceBuilderConfig(RED_SAND, WHITE_TERRACOTTA, GRAVEL);
   public static final SurfaceBuilderConfig MYCELIUM_DIRT_GRAVEL_SURFACE = new SurfaceBuilderConfig(MYCELIUM, DIRT, GRAVEL);
   public static final SurfaceBuilderConfig NETHERRACK_SURFACE = new SurfaceBuilderConfig(NETHERRACK, NETHERRACK, NETHERRACK);
   public static final SurfaceBuilderConfig END_STONE_SURFACE = new SurfaceBuilderConfig(END_STONE, END_STONE, END_STONE);
   public static final ISurfaceBuilder<SurfaceBuilderConfig> DEFAULT_SURFACE_BUILDER = new DefaultSurfaceBuilder();
   public static final ISurfaceBuilder<SurfaceBuilderConfig> EXTREME_HILL_SURFACE_BUILDER = new ExtremeHillsSurfaceBuilder();
   public static final ISurfaceBuilder<SurfaceBuilderConfig> SAVANA_MUTATED_SURFACE_BUILDER = new SavanaMutatedSurfaceBuilder();
   public static final ISurfaceBuilder<SurfaceBuilderConfig> EXTREME_HILLS_MUTATED_SURFACE_BUILDER = new ExtremeHillsMutatedSurfaceBuilder();
   public static final ISurfaceBuilder<SurfaceBuilderConfig> TAIGA_MEGA_SURFACE_BUILDER = new TaigaMegaSurfaceBuilder();
   public static final ISurfaceBuilder<SurfaceBuilderConfig> SWAMP_SURFACE_BUILDER = new SwampSurfaceBuilder();
   public static final ISurfaceBuilder<SurfaceBuilderConfig> MESA_SURFACE_BUILDER = new MesaSurfaceBuilder();
   public static final ISurfaceBuilder<SurfaceBuilderConfig> MESA_FOREST_SURFACE_BUILDER = new MesaForestSurfaceBuilder();
   public static final ISurfaceBuilder<SurfaceBuilderConfig> MESA_BRYCE_SURACE_BUILDER = new MesaBryceSurfaceBuilder();
   public static final ISurfaceBuilder<SurfaceBuilderConfig> FROZEN_OCEAN_SURFACE_BUILDER = new FrozenOceanSurfaceBuilder();
   public static final ISurfaceBuilder<SurfaceBuilderConfig> NETHER_SURFACE_BUILDER = new NetherSurfaceBuilder();
   public static final ISurfaceBuilder<SurfaceBuilderConfig> NOOP_SURFACE_BUILDER = new NoopSurfaceBuilder();
   public static final Set<Biome> BIOMES = Sets.newHashSet();
   public static final ObjectIntIdentityMap<Biome> MUTATION_TO_BASE_ID_MAP = new ObjectIntIdentityMap<>();
   protected static final NoiseGeneratorPerlin TEMPERATURE_NOISE = new NoiseGeneratorPerlin(new Random(1234L), 1);
   public static final NoiseGeneratorPerlin INFO_NOISE = new NoiseGeneratorPerlin(new Random(2345L), 1);
   @Nullable
   protected String translationKey;
   /** The base height of this biome. Default 0.1. */
   protected final float depth;
   /** The variation from the base height of the biome. Default 0.3. */
   protected final float scale;
   /** The temperature of this biome. */
   protected final float temperature;
   /** The rainfall in this biome. */
   protected final float downfall;
   /** Color tint applied to water depending on biome */
   protected final int waterColor;
   protected final int waterFogColor;
   /** The unique identifier of the biome for which this is a mutation of. */
   @Nullable
   protected final String parent;
   protected final CompositeSurfaceBuilder<?> surfaceBuilder;
   protected final Biome.Category category;
   protected final Biome.RainType precipitation;
   protected final Map<GenerationStage.Carving, List<WorldCarverWrapper<?>>> carvers = Maps.newHashMap();
   protected final Map<GenerationStage.Decoration, List<CompositeFeature<?, ?>>> features = Maps.newHashMap();
   protected final List<CompositeFlowerFeature<?>> flowers = Lists.newArrayList();
   protected final Map<Structure<?>, IFeatureConfig> structures = Maps.newHashMap();
   private final Map<EnumCreatureType, List<Biome.SpawnListEntry>> spawns = Maps.newHashMap();

   @Nullable
   public static Biome getMutationForBiome(Biome biome) {
      return MUTATION_TO_BASE_ID_MAP.getByValue(IRegistry.field_212624_m.getId(biome));
   }

   public static <C extends IFeatureConfig> WorldCarverWrapper<C> createWorldCarverWrapper(IWorldCarver<C> carver, C config) {
      return new WorldCarverWrapper<>(carver, config);
   }

   public static <F extends IFeatureConfig, D extends IPlacementConfig> CompositeFeature<F, D> createCompositeFeature(Feature<F> featureIn, F config, BasePlacement<D> basePlacementIn, D placementConfig) {
      return new CompositeFeature<>(featureIn, config, basePlacementIn, placementConfig);
   }

   public static <D extends IPlacementConfig> CompositeFlowerFeature<D> createCompositeFlowerFeature(AbstractFlowersFeature flowerFeature, BasePlacement<D> placement, D config) {
      return new CompositeFlowerFeature<>(flowerFeature, placement, config);
   }

   public Biome(Biome.BiomeBuilder biomeBuilder) {
      if (biomeBuilder.surfaceBuilder != null && biomeBuilder.precipitation != null && biomeBuilder.category != null && biomeBuilder.depth != null && biomeBuilder.scale != null && biomeBuilder.temperature != null && biomeBuilder.downfall != null && biomeBuilder.waterColor != null && biomeBuilder.waterFogColor != null) {
         this.surfaceBuilder = biomeBuilder.surfaceBuilder;
         this.precipitation = biomeBuilder.precipitation;
         this.category = biomeBuilder.category;
         this.depth = biomeBuilder.depth;
         this.scale = biomeBuilder.scale;
         this.temperature = biomeBuilder.temperature;
         this.downfall = biomeBuilder.downfall;
         this.waterColor = biomeBuilder.waterColor;
         this.waterFogColor = biomeBuilder.waterFogColor;
         this.parent = biomeBuilder.parent;

         for(GenerationStage.Decoration generationstage$decoration : GenerationStage.Decoration.values()) {
            this.features.put(generationstage$decoration, Lists.newArrayList());
         }

         for(EnumCreatureType enumcreaturetype : EnumCreatureType.values()) {
            this.spawns.put(enumcreaturetype, Lists.newArrayList());
         }

      } else {
         throw new IllegalStateException("You are missing parameters to build a proper biome for " + this.getClass().getSimpleName() + "\n" + biomeBuilder);
      }
   }

   public void addStructureFeatures() {
      this.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, createCompositeFeature(Feature.MINESHAFT, new MineshaftConfig((double)0.004F, MineshaftStructure.Type.NORMAL), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
      this.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, createCompositeFeature(Feature.VILLAGE, new VillageConfig(0, VillagePieces.Type.OAK), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
      this.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, createCompositeFeature(Feature.STRONGHOLD, new StrongholdConfig(), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
      this.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, createCompositeFeature(Feature.SWAMP_HUT, new SwampHutConfig(), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
      this.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, createCompositeFeature(Feature.DESERT_PYRAMID, new DesertPyramidConfig(), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
      this.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, createCompositeFeature(Feature.JUNGLE_PYRAMID, new JunglePyramidConfig(), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
      this.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, createCompositeFeature(Feature.IGLOO, new IglooConfig(), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
      this.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, createCompositeFeature(Feature.SHIPWRECK, new ShipwreckConfig(false), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
      this.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, createCompositeFeature(Feature.OCEAN_MONUMENT, new OceanMonumentConfig(), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
      this.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, createCompositeFeature(Feature.WOODLAND_MANSION, new WoodlandMansionConfig(), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
      this.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, createCompositeFeature(Feature.OCEAN_RUIN, new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.9F), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
      this.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, createCompositeFeature(Feature.BURIED_TREASURE, new BuriedTreasureConfig(0.01F), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
   }

   public boolean isMutation() {
      return this.parent != null;
   }

   /**
    * takes temperature, returns color
    */
   @OnlyIn(Dist.CLIENT)
   public int getSkyColorByTemp(float currentTemperature) {
      currentTemperature = currentTemperature / 3.0F;
      currentTemperature = MathHelper.clamp(currentTemperature, -1.0F, 1.0F);
      return MathHelper.hsvToRGB(0.62222224F - currentTemperature * 0.05F, 0.5F + currentTemperature * 0.1F, 1.0F);
   }

   public void addSpawn(EnumCreatureType type, Biome.SpawnListEntry spawnListEntry) {
      this.spawns.computeIfAbsent(type, k -> Lists.newArrayList()).add(spawnListEntry);
   }

   /**
    * Returns the correspondent list of the EnumCreatureType informed.
    */
   public List<Biome.SpawnListEntry> getSpawns(EnumCreatureType creatureType) {
      return this.spawns.computeIfAbsent(creatureType, k -> Lists.newArrayList());
   }

   public Biome.RainType getPrecipitation() {
      return this.precipitation;
   }

   /**
    * Checks to see if the rainfall level of the biome is extremely high
    */
   public boolean isHighHumidity() {
      return this.getDownfall() > 0.85F;
   }

   /**
    * returns the chance a creature has to spawn.
    */
   public float getSpawningChance() {
      return 0.1F;
   }

   /**
    * Gets the current temperature at the given location, based off of the default for this biome, the elevation of the
    * position, and {@linkplain #TEMPERATURE_NOISE} some random perlin noise.
    */
   public float getTemperature(BlockPos pos) {
      if (pos.getY() > 64) {
         float f = (float)(TEMPERATURE_NOISE.getValue((double)((float)pos.getX() / 8.0F), (double)((float)pos.getZ() / 8.0F)) * 4.0D);
         return this.getDefaultTemperature() - (f + (float)pos.getY() - 64.0F) * 0.05F / 30.0F;
      } else {
         return this.getDefaultTemperature();
      }
   }

   public boolean doesWaterFreeze(IWorldReaderBase p_201848_1_, BlockPos pos) {
      return this.doesWaterFreeze(p_201848_1_, pos, true);
   }

   public boolean doesWaterFreeze(IWorldReaderBase worldIn, BlockPos water, boolean mustBeAtEdge) {
      if (this.getTemperature(water) >= 0.15F) {
         return false;
      } else {
         if (water.getY() >= 0 && water.getY() < 256 && worldIn.getLightFor(EnumLightType.BLOCK, water) < 10) {
            IBlockState iblockstate = worldIn.getBlockState(water);
            IFluidState ifluidstate = worldIn.getFluidState(water);
            if (ifluidstate.getFluid() == Fluids.WATER && iblockstate.getBlock() instanceof BlockFlowingFluid) {
               if (!mustBeAtEdge) {
                  return true;
               }

               boolean flag = worldIn.hasWater(water.west()) && worldIn.hasWater(water.east()) && worldIn.hasWater(water.north()) && worldIn.hasWater(water.south());
               if (!flag) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean doesSnowGenerate(IWorldReaderBase worldIn, BlockPos pos) {
      if (this.getTemperature(pos) >= 0.15F) {
         return false;
      } else {
         if (pos.getY() >= 0 && pos.getY() < 256 && worldIn.getLightFor(EnumLightType.BLOCK, pos) < 10) {
            IBlockState iblockstate = worldIn.getBlockState(pos);
            if (iblockstate.isAir(worldIn, pos) && Blocks.SNOW.getDefaultState().isValidPosition(worldIn, pos)) {
               return true;
            }
         }

         return false;
      }
   }

   public void addFeature(GenerationStage.Decoration decorationStage, CompositeFeature<?, ?> featureIn) {
      if (featureIn instanceof CompositeFlowerFeature) {
         this.flowers.add((CompositeFlowerFeature)featureIn);
      }

      this.features.get(decorationStage).add(featureIn);
   }

   public <C extends IFeatureConfig> void addCarver(GenerationStage.Carving stage, WorldCarverWrapper<C> carver) {
      this.carvers.computeIfAbsent(stage, (p_203604_0_) -> {
         return Lists.newArrayList();
      }).add(carver);
   }

   public List<WorldCarverWrapper<?>> getCarvers(GenerationStage.Carving stage) {
      return this.carvers.computeIfAbsent(stage, (p_203610_0_) -> {
         return Lists.newArrayList();
      });
   }

   public <C extends IFeatureConfig> void addStructure(Structure<C> structureIn, C config) {
      this.structures.put(structureIn, config);
   }

   public <C extends IFeatureConfig> boolean hasStructure(Structure<C> structureIn) {
      return this.structures.containsKey(structureIn);
   }

   @Nullable
   public <C extends IFeatureConfig> IFeatureConfig getStructureConfig(Structure<C> structureIn) {
      return this.structures.get(structureIn);
   }

   public List<CompositeFlowerFeature<?>> getFlowers() {
      return this.flowers;
   }

   public List<CompositeFeature<?, ?>> getFeatures(GenerationStage.Decoration decorationStage) {
      return this.features.get(decorationStage);
   }

   public void decorate(GenerationStage.Decoration stage, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, IWorld worldIn, long p_203608_4_, SharedSeedRandom random, BlockPos pos) {
      int i = 0;

      for(CompositeFeature<?, ?> compositefeature : this.features.get(stage)) {
         random.setFeatureSeed(p_203608_4_, i, stage.ordinal());
         compositefeature.func_212245_a(worldIn, chunkGenerator, random, pos, IFeatureConfig.NO_FEATURE_CONFIG);
         ++i;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public int getGrassColor(BlockPos pos) {
      double d0 = (double)MathHelper.clamp(this.getTemperature(pos), 0.0F, 1.0F);
      double d1 = (double)MathHelper.clamp(this.getDownfall(), 0.0F, 1.0F);
      return GrassColors.get(d0, d1);
   }

   @OnlyIn(Dist.CLIENT)
   public int getFoliageColor(BlockPos pos) {
      double d0 = (double)MathHelper.clamp(this.getTemperature(pos), 0.0F, 1.0F);
      double d1 = (double)MathHelper.clamp(this.getDownfall(), 0.0F, 1.0F);
      return FoliageColors.get(d0, d1);
   }

   public void buildSurface(Random random, IChunk chunkIn, int x, int z, int startHeight, double noise, IBlockState defaultBlock, IBlockState defaultFluid, int seaLevel, long seed) {
      this.surfaceBuilder.setSeed(seed);
      this.surfaceBuilder.buildSurface(random, chunkIn, this, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, AIR_SURFACE);
   }

   public Biome.TempCategory getTempCategory() {
      if (this.category == Biome.Category.OCEAN) {
         return Biome.TempCategory.OCEAN;
      } else if ((double)this.getDefaultTemperature() < 0.2D) {
         return Biome.TempCategory.COLD;
      } else {
         return (double)this.getDefaultTemperature() < 1.0D ? Biome.TempCategory.MEDIUM : Biome.TempCategory.WARM;
      }
   }

   public static Biome getBiome(int biomeId, Biome fallback) {
      Biome biome = IRegistry.field_212624_m.get(biomeId);
      return biome == null ? fallback : biome;
   }

   public final float getDepth() {
      return this.depth;
   }

   /**
    * Gets a floating point representation of this biome's rainfall
    */
   public final float getDownfall() {
      return this.downfall;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getDisplayName() {
      return new TextComponentTranslation(this.getTranslationKey());
   }

   public String getTranslationKey() {
      if (this.translationKey == null) {
         this.translationKey = Util.makeTranslationKey("biome", IRegistry.field_212624_m.getKey(this));
      }

      return this.translationKey;
   }

   public final float getScale() {
      return this.scale;
   }

   /**
    * Gets the constant default temperature for this biome.
    */
   public final float getDefaultTemperature() {
      return this.temperature;
   }

   public final int getWaterColor() {
      return this.waterColor;
   }

   public final int getWaterFogColor() {
      return this.waterFogColor;
   }

   public final Biome.Category getCategory() {
      return this.category;
   }

   public CompositeSurfaceBuilder<?> getSurfaceBuilder() {
      return this.surfaceBuilder;
   }

   public ISurfaceBuilderConfig getSurfaceBuilderConfig() {
      return this.surfaceBuilder.getConfig();
   }

   @Nullable
   public String getParent() {
      return this.parent;
   }

   /**
    * Registers all of the vanilla biomes.
    */
   public static void registerBiomes() {
      register(0, "ocean", new OceanBiome());
      register(1, "plains", new PlainsBiome());
      register(2, "desert", new DesertBiome());
      register(3, "mountains", new MountainsBiome());
      register(4, "forest", new ForestBiome());
      register(5, "taiga", new TaigaBiome());
      register(6, "swamp", new SwampBiome());
      register(7, "river", new RiverBiome());
      register(8, "nether", new NetherBiome());
      register(9, "the_end", new TheEndBiome());
      register(10, "frozen_ocean", new FrozenOceanBiome());
      register(11, "frozen_river", new FrozenRiverBiome());
      register(12, "snowy_tundra", new SnowyTundraBiome());
      register(13, "snowy_mountains", new SnowyMountainsBiome());
      register(14, "mushroom_fields", new MushroomFieldsBiome());
      register(15, "mushroom_field_shore", new MushroomFieldShoreBiome());
      register(16, "beach", new BeachBiome());
      register(17, "desert_hills", new DesertHillsBiome());
      register(18, "wooded_hills", new WoodedHillsBiome());
      register(19, "taiga_hills", new TaigaHillsBiome());
      register(20, "mountain_edge", new MountainEdgeBiome());
      register(21, "jungle", new JungleBiome());
      register(22, "jungle_hills", new JungleHillsBiome());
      register(23, "jungle_edge", new JungleEdgeBiome());
      register(24, "deep_ocean", new DeepOceanBiome());
      register(25, "stone_shore", new StoneShoreBiome());
      register(26, "snowy_beach", new SnowyBeachBiome());
      register(27, "birch_forest", new BirchForestBiome());
      register(28, "birch_forest_hills", new BirchForestHillsBiome());
      register(29, "dark_forest", new DarkForestBiome());
      register(30, "snowy_taiga", new SnowyTaigaBiome());
      register(31, "snowy_taiga_hills", new SnowyTaigaHillsBiome());
      register(32, "giant_tree_taiga", new GiantTreeTaigaBiome());
      register(33, "giant_tree_taiga_hills", new GiantTreeTaigaHillsBiome());
      register(34, "wooded_mountains", new WoodedMountainsBiome());
      register(35, "savanna", new SavannaBiome());
      register(36, "savanna_plateau", new SavannaPlateauBiome());
      register(37, "badlands", new BadlandsBiome());
      register(38, "wooded_badlands_plateau", new WoodedBadlandsPlateauBiome());
      register(39, "badlands_plateau", new BadlandsPlateauBiome());
      register(40, "small_end_islands", new SmallEndIslandsBiome());
      register(41, "end_midlands", new EndMidlandsBiome());
      register(42, "end_highlands", new EndHighlandsBiome());
      register(43, "end_barrens", new EndBarrensBiome());
      register(44, "warm_ocean", new WarmOceanBiome());
      register(45, "lukewarm_ocean", new LukewarmOceanBiome());
      register(46, "cold_ocean", new ColdOceanBiome());
      register(47, "deep_warm_ocean", new DeepWarmOceanBiome());
      register(48, "deep_lukewarm_ocean", new DeepLukewarmOceanBiome());
      register(49, "deep_cold_ocean", new DeepColdOceanBiome());
      register(50, "deep_frozen_ocean", new DeepFrozenOceanBiome());
      register(127, "the_void", new TheVoidBiome());
      register(129, "sunflower_plains", new SunflowerPlainsBiome());
      register(130, "desert_lakes", new DesertLakesBiome());
      register(131, "gravelly_mountains", new GravellyMountainsBiome());
      register(132, "flower_forest", new FlowerForestBiome());
      register(133, "taiga_mountains", new TaigaMountainsBiome());
      register(134, "swamp_hills", new SwampHillsBiome());
      register(140, "ice_spikes", new IceSpikesBiome());
      register(149, "modified_jungle", new ModifiedJungleBiome());
      register(151, "modified_jungle_edge", new ModifiedJungleEdgeBiome());
      register(155, "tall_birch_forest", new TallBirchForestBiome());
      register(156, "tall_birch_hills", new TallBirchHillsBiome());
      register(157, "dark_forest_hills", new DarkForestHillsBiome());
      register(158, "snowy_taiga_mountains", new SnowyTaigaMountainsBiome());
      register(160, "giant_spruce_taiga", new GiantSpruceTaigaBiome());
      register(161, "giant_spruce_taiga_hills", new GiantSpruceTaigaHillsBiome());
      register(162, "modified_gravelly_mountains", new ModifiedGravellyMountainsBiome());
      register(163, "shattered_savanna", new ShatteredSavannaBiome());
      register(164, "shattered_savanna_plateau", new ShatteredSavannaPlateauBiome());
      register(165, "eroded_badlands", new ErodedBadlandsBiome());
      register(166, "modified_wooded_badlands_plateau", new ModifiedWoodedBadlandsPlateauBiome());
      register(167, "modified_badlands_plateau", new ModifiedBadlandsPlateauBiome());
      Collections.addAll(BIOMES, Biomes.OCEAN, Biomes.PLAINS, Biomes.DESERT, Biomes.MOUNTAINS, Biomes.FOREST, Biomes.TAIGA, Biomes.SWAMP, Biomes.RIVER, Biomes.FROZEN_RIVER, Biomes.SNOWY_TUNDRA, Biomes.SNOWY_MOUNTAINS, Biomes.MUSHROOM_FIELDS, Biomes.MUSHROOM_FIELD_SHORE, Biomes.BEACH, Biomes.DESERT_HILLS, Biomes.WOODED_HILLS, Biomes.TAIGA_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.DEEP_OCEAN, Biomes.STONE_SHORE, Biomes.SNOWY_BEACH, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.DARK_FOREST, Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.WOODED_MOUNTAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.BADLANDS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.BADLANDS_PLATEAU);
   }

   /**
    * Registers a new biome into the registry.
    */
   public static void register(int id, String name, Biome biome) {
      IRegistry.field_212624_m.register(id, new ResourceLocation(name), biome);
      if (biome.isMutation()) {
         MUTATION_TO_BASE_ID_MAP.put(biome, IRegistry.field_212624_m.getId(IRegistry.field_212624_m.func_212608_b(new ResourceLocation(biome.parent))));
      }

   }

   public static class BiomeBuilder {
      @Nullable
      private CompositeSurfaceBuilder<?> surfaceBuilder;
      @Nullable
      private Biome.RainType precipitation;
      @Nullable
      private Biome.Category category;
      @Nullable
      private Float depth;
      @Nullable
      private Float scale;
      @Nullable
      private Float temperature;
      @Nullable
      private Float downfall;
      @Nullable
      private Integer waterColor;
      @Nullable
      private Integer waterFogColor;
      @Nullable
      private String parent;

      public Biome.BiomeBuilder surfaceBuilder(CompositeSurfaceBuilder<?> surfaceBuilderIn) {
         this.surfaceBuilder = surfaceBuilderIn;
         return this;
      }

      public Biome.BiomeBuilder precipitation(Biome.RainType precipitationIn) {
         this.precipitation = precipitationIn;
         return this;
      }

      public Biome.BiomeBuilder category(Biome.Category biomeCategory) {
         this.category = biomeCategory;
         return this;
      }

      public Biome.BiomeBuilder depth(float depthIn) {
         this.depth = depthIn;
         return this;
      }

      public Biome.BiomeBuilder scale(float scaleIn) {
         this.scale = scaleIn;
         return this;
      }

      public Biome.BiomeBuilder temperature(float temperatureIn) {
         this.temperature = temperatureIn;
         return this;
      }

      public Biome.BiomeBuilder downfall(float downfallIn) {
         this.downfall = downfallIn;
         return this;
      }

      public Biome.BiomeBuilder waterColor(int waterColorIn) {
         this.waterColor = waterColorIn;
         return this;
      }

      public Biome.BiomeBuilder waterFogColor(int waterFogColorIn) {
         this.waterFogColor = waterFogColorIn;
         return this;
      }

      public Biome.BiomeBuilder parent(@Nullable String parentIn) {
         this.parent = parentIn;
         return this;
      }

      public String toString() {
         return "BiomeBuilder{\nsurfaceBuilder=" + this.surfaceBuilder + ",\nprecipitation=" + this.precipitation + ",\nbiomeCategory=" + this.category + ",\ndepth=" + this.depth + ",\nscale=" + this.scale + ",\ntemperature=" + this.temperature + ",\ndownfall=" + this.downfall + ",\nwaterColor=" + this.waterColor + ",\nwaterFogColor=" + this.waterFogColor + ",\nparent='" + this.parent + '\'' + "\n" + '}';
      }
   }

   public static enum Category {
      NONE,
      TAIGA,
      EXTREME_HILLS,
      JUNGLE,
      MESA,
      PLAINS,
      SAVANNA,
      ICY,
      THEEND,
      BEACH,
      FOREST,
      OCEAN,
      DESERT,
      RIVER,
      SWAMP,
      MUSHROOM,
      NETHER;
   }

   public static enum RainType {
      NONE,
      RAIN,
      SNOW;
   }

   public static class SpawnListEntry extends WeightedRandom.Item {
      public EntityType<? extends EntityLiving> entityType;
      public int minGroupCount;
      public int maxGroupCount;

      public SpawnListEntry(EntityType<? extends EntityLiving> entityTypeIn, int weight, int minGroupCountIn, int maxGroupCountIn) {
         super(weight);
         this.entityType = entityTypeIn;
         this.minGroupCount = minGroupCountIn;
         this.maxGroupCount = maxGroupCountIn;
      }

      public String toString() {
         return EntityType.getId(this.entityType) + "*(" + this.minGroupCount + "-" + this.maxGroupCount + "):" + this.itemWeight;
      }
   }

   public static class FlowerEntry extends WeightedRandom.Item {
      private final IBlockState state;
      public FlowerEntry(IBlockState state, int weight) {
         super(weight);
         this.state = state;
      }

      public IBlockState getState() {
         return state;
      }
   }

   public static enum TempCategory {
      OCEAN,
      COLD,
      MEDIUM,
      WARM;
   }
}