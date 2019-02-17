package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.structure.BuriedTreasureConfig;
import net.minecraft.world.gen.feature.structure.BuriedTreasureStructure;
import net.minecraft.world.gen.feature.structure.DesertPyramidConfig;
import net.minecraft.world.gen.feature.structure.DesertPyramidStructure;
import net.minecraft.world.gen.feature.structure.EndCityConfig;
import net.minecraft.world.gen.feature.structure.EndCityStructure;
import net.minecraft.world.gen.feature.structure.FortressConfig;
import net.minecraft.world.gen.feature.structure.FortressStructure;
import net.minecraft.world.gen.feature.structure.IglooConfig;
import net.minecraft.world.gen.feature.structure.IglooStructure;
import net.minecraft.world.gen.feature.structure.JunglePyramidConfig;
import net.minecraft.world.gen.feature.structure.JunglePyramidStructure;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.OceanMonumentConfig;
import net.minecraft.world.gen.feature.structure.OceanMonumentStructure;
import net.minecraft.world.gen.feature.structure.OceanRuinConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinStructure;
import net.minecraft.world.gen.feature.structure.ShipwreckConfig;
import net.minecraft.world.gen.feature.structure.ShipwreckStructure;
import net.minecraft.world.gen.feature.structure.StrongholdConfig;
import net.minecraft.world.gen.feature.structure.StrongholdStructure;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.SwampHutConfig;
import net.minecraft.world.gen.feature.structure.SwampHutStructure;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.structure.VillageStructure;
import net.minecraft.world.gen.feature.structure.WoodlandMansionConfig;
import net.minecraft.world.gen.feature.structure.WoodlandMansionStructure;
import net.minecraft.world.gen.placement.CountConfig;

public abstract class Feature<C extends IFeatureConfig> {
   private static final List<Biome.SpawnListEntry> EMPTY_SPAWN_LIST = Lists.newArrayList();
   public static final Structure<VillageConfig> VILLAGE = new VillageStructure();
   public static final Structure<MineshaftConfig> MINESHAFT = new MineshaftStructure();
   public static final Structure<WoodlandMansionConfig> WOODLAND_MANSION = new WoodlandMansionStructure();
   public static final Structure<JunglePyramidConfig> JUNGLE_PYRAMID = new JunglePyramidStructure();
   public static final Structure<DesertPyramidConfig> DESERT_PYRAMID = new DesertPyramidStructure();
   public static final Structure<IglooConfig> IGLOO = new IglooStructure();
   public static final Structure<ShipwreckConfig> SHIPWRECK = new ShipwreckStructure();
   public static final Structure<SwampHutConfig> SWAMP_HUT = new SwampHutStructure();
   public static final Structure<StrongholdConfig> STRONGHOLD = new StrongholdStructure();
   public static final Structure<OceanMonumentConfig> OCEAN_MONUMENT = new OceanMonumentStructure();
   public static final Structure<OceanRuinConfig> OCEAN_RUIN = new OceanRuinStructure();
   public static final Structure<FortressConfig> FORTRESS = new FortressStructure();
   public static final Structure<EndCityConfig> END_CITY = new EndCityStructure();
   public static final Structure<BuriedTreasureConfig> BURIED_TREASURE = new BuriedTreasureStructure();
   public static final AbstractTreeFeature<NoFeatureConfig> BIG_TREE = new BigTreeFeature(false);
   public static final AbstractTreeFeature<NoFeatureConfig> BIRCH_TREE = new BirchTreeFeature(false, false);
   public static final AbstractTreeFeature<NoFeatureConfig> TALL_BIRCH_TREE = new BirchTreeFeature(false, true);
   public static final AbstractTreeFeature<NoFeatureConfig> SHRUB = new ShrubFeature(Blocks.JUNGLE_LOG.getDefaultState(), Blocks.OAK_LEAVES.getDefaultState()).setSapling((net.minecraftforge.common.IPlantable)Blocks.JUNGLE_SAPLING);
   public static final AbstractTreeFeature<NoFeatureConfig> JUNGLE_TREE = new JungleTreeFeature(false, 4, Blocks.JUNGLE_LOG.getDefaultState(), Blocks.JUNGLE_LEAVES.getDefaultState(), true).setSapling((net.minecraftforge.common.IPlantable)Blocks.JUNGLE_SAPLING);
   public static final AbstractTreeFeature<NoFeatureConfig> POINTY_TAIGA_TREE = new PointyTaigaTreeFeature();
   public static final AbstractTreeFeature<NoFeatureConfig> CANOPY_TREE = new CanopyTreeFeature(false);
   public static final AbstractTreeFeature<NoFeatureConfig> SAVANNA_TREE = new SavannaTreeFeature(false);
   public static final AbstractTreeFeature<NoFeatureConfig> TALL_TAIGA_TREE = new TallTaigaTreeFeature(false);
   public static final AbstractTreeFeature<NoFeatureConfig> SWAMP_TREE = new SwampTreeFeature();
   public static final AbstractTreeFeature<NoFeatureConfig> TREE = new TreeFeature(false);
   public static final HugeTreesFeature<NoFeatureConfig> MESA_JUNGLE = new MegaJungleFeature(false, 10, 20, Blocks.JUNGLE_LOG.getDefaultState(), Blocks.JUNGLE_LEAVES.getDefaultState()).setSapling((net.minecraftforge.common.IPlantable)Blocks.JUNGLE_SAPLING);
   public static final HugeTreesFeature<NoFeatureConfig> MEGA_PINE_TREE_1 = new MegaPineTree(false, false);
   public static final HugeTreesFeature<NoFeatureConfig> MEGA_PINE_TREE_2 = new MegaPineTree(false, true);
   public static final AbstractFlowersFeature DEFAULT_FLOWERS = new DefaultFlowersFeature();
   public static final AbstractFlowersFeature FOREST_FLOWERS = new ForestFlowersFeature();
   public static final AbstractFlowersFeature PLAINS_FLOWERS = new PlainsFlowersFeature();
   public static final AbstractFlowersFeature SWAMP_FLOWERS = new SwampFlowersFeature();
   public static final Feature<NoFeatureConfig> JUNGLE_GRASS = new JungleGrassFeature();
   public static final Feature<NoFeatureConfig> TAIGA_GRASS = new TaigaGrassFeature();
   public static final Feature<TallGrassConfig> TALL_GRASS = new TallGrassFeature();
   public static final Feature<NoFeatureConfig> VOID_START_PLATFORM = new VoidStartPlatformFeature();
   public static final Feature<NoFeatureConfig> CACTUS = new CactusFeature();
   public static final Feature<NoFeatureConfig> DEAD_BUSH = new DeadBushFeature();
   public static final Feature<NoFeatureConfig> DESERT_WELLS = new DesertWellsFeature();
   public static final Feature<NoFeatureConfig> FOSSILS = new FossilsFeature();
   public static final Feature<NoFeatureConfig> FIRE = new FireFeature();
   public static final Feature<NoFeatureConfig> BIG_RED_MUSHROOM = new BigRedMushroomFeature();
   public static final Feature<NoFeatureConfig> BIG_BROWN_MUSHROOM = new BigBrownMushroomFeature();
   public static final Feature<NoFeatureConfig> ICE_SPIKE = new IceSpikeFeature();
   public static final Feature<NoFeatureConfig> GLOWSTONE = new GlowstoneFeature();
   public static final Feature<NoFeatureConfig> MELON = new MelonFeature();
   public static final Feature<NoFeatureConfig> PUMPKIN = new PumpkinFeature();
   public static final Feature<NoFeatureConfig> REED = new ReedFeature();
   public static final Feature<NoFeatureConfig> ICE_AND_SNOW = new IceAndSnowFeature();
   public static final Feature<NoFeatureConfig> VINES = new VinesFeature();
   public static final Feature<NoFeatureConfig> WATERLILY = new WaterlilyFeature();
   public static final Feature<NoFeatureConfig> DUNGEONS = new DungeonsFeature();
   public static final Feature<NoFeatureConfig> BLUE_ICE = new BlueIceFeature();
   public static final Feature<IcebergConfig> ICEBERG = new IcebergFeature();
   public static final Feature<BlockBlobConfig> BLOCK_BLOB = new BlockBlobFeature();
   public static final Feature<BushConfig> BUSH = new BushFeature();
   public static final Feature<SphereReplaceConfig> SPHERE_REPLACE = new SphereReplaceFeature();
   public static final Feature<DoublePlantConfig> DOUBLE_PLANT = new DoublePlantFeature();
   public static final Feature<HellLavaConfig> HELL_LAVA = new HellLavaFeature();
   public static final Feature<FeatureRadiusConfig> ICE_PATH = new IcePathFeature();
   public static final Feature<LakesConfig> LAKES = new LakesFeature();
   public static final Feature<MinableConfig> MINABLE = new MinableFeature();
   public static final Feature<RandomFeatureListConfig> DEFAULT_RANDOM_FEATURE_LIST = new RandomDefaultFeatureList();
   public static final Feature<RandomDefaultFeatureListConfig> RANDOM_FEATURE_LIST = new RandomFeatureList();
   public static final Feature<RandomFeatureWithConfigConfig> RANDOM_FEATURE_WITH_CONFIG = new RandomFeatureWithConfigFeature();
   public static final Feature<TwoFeatureChoiceConfig> TWO_FEATURE_CHOICE = new TwoFeatureChoiceFeature();
   public static final Feature<ReplaceBlockConfig> REPLACE_BLOCK = new ReplaceBlockFeature();
   public static final Feature<LiquidsConfig> LIQUIDS = new LiquidsFeature();
   public static final Feature<NoFeatureConfig> END_CRYSTAL_TOWER = new EndCrystalTowerFeature();
   public static final Feature<NoFeatureConfig> END_ISLAND = new EndIslandFeature();
   public static final Feature<NoFeatureConfig> CHORUS_PLANT = new ChorusPlantFeature();
   public static final Feature<EndGatewayConfig> END_GATEWAY = new EndGatewayFeature();
   public static final Feature<SeaGrassConfig> SEA_GRASS = new SeaGrassFeature();
   public static final Feature<NoFeatureConfig> KELP = new KelpFeature();
   public static final Feature<NoFeatureConfig> CORAL_TREE = new CoralTreeFeature();
   public static final Feature<NoFeatureConfig> CORAL_MUSHROOM = new CoralMushroomFeature();
   public static final Feature<NoFeatureConfig> CORAL_CLAW = new CoralClawFeature();
   public static final Feature<CountConfig> SEA_PICKLE = new SeaPickleFeature();
   public static final Feature<BlockWithContextConfig> BLOCK_WITH_CONTEXT = new BlockWithContextFeature();
   public static final Map<String, Structure<?>> STRUCTURES = Util.make(Maps.newHashMap(), (p_205170_0_) -> {
      p_205170_0_.put("Village".toLowerCase(Locale.ROOT), VILLAGE);
      p_205170_0_.put("Mineshaft".toLowerCase(Locale.ROOT), MINESHAFT);
      p_205170_0_.put("Mansion".toLowerCase(Locale.ROOT), WOODLAND_MANSION);
      p_205170_0_.put("Jungle_Pyramid".toLowerCase(Locale.ROOT), JUNGLE_PYRAMID);
      p_205170_0_.put("Desert_Pyramid".toLowerCase(Locale.ROOT), DESERT_PYRAMID);
      p_205170_0_.put("Igloo".toLowerCase(Locale.ROOT), IGLOO);
      p_205170_0_.put("Shipwreck".toLowerCase(Locale.ROOT), SHIPWRECK);
      p_205170_0_.put("Swamp_Hut".toLowerCase(Locale.ROOT), SWAMP_HUT);
      p_205170_0_.put("Stronghold".toLowerCase(Locale.ROOT), STRONGHOLD);
      p_205170_0_.put("Monument".toLowerCase(Locale.ROOT), OCEAN_MONUMENT);
      p_205170_0_.put("Ocean_Ruin".toLowerCase(Locale.ROOT), OCEAN_RUIN);
      p_205170_0_.put("Fortress".toLowerCase(Locale.ROOT), FORTRESS);
      p_205170_0_.put("EndCity".toLowerCase(Locale.ROOT), END_CITY);
      p_205170_0_.put("Buried_Treasure".toLowerCase(Locale.ROOT), BURIED_TREASURE);
   });
   /**
    * Sets wither or not the generator should notify blocks of blocks it changes. When the world is first generated,
    * this is false, when saplings grow, this is true.
    */
   protected final boolean doBlockNotify;

   public Feature() {
      this(false);
   }

   public Feature(boolean notify) {
      this.doBlockNotify = notify;
   }

   public abstract boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, C p_212245_5_);

   protected void setBlockState(IWorld worldIn, BlockPos pos, IBlockState state) {
      if (this.doBlockNotify) {
         worldIn.setBlockState(pos, state, 3);
      } else {
         worldIn.setBlockState(pos, state, 2);
      }

   }

   public List<Biome.SpawnListEntry> getSpawnList() {
      return EMPTY_SPAWN_LIST;
   }

   public static boolean isPositionInStructureExact(IWorld worldIn, String structureIn, BlockPos pos) {
      return STRUCTURES.get(structureIn.toLowerCase(Locale.ROOT)).isPositionInsideStructure(worldIn, pos);
   }
}