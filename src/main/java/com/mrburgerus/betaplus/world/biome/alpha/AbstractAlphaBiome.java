package com.mrburgerus.betaplus.world.biome.alpha;

import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.placement.*;

public class AbstractAlphaBiome extends Biome
{

	// Pass it across.
	AbstractAlphaBiome(BiomeBuilder builder)
	{
		super(builder);
	}

	public void addAllOres()
	{
		// Underground Dirt
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.MINABLE, new MinableConfig(MinableConfig.IS_ROCK, Blocks.DIRT.getDefaultState(), 33), Biome.COUNT_RANGE, new CountRangeConfig(10, 0, 0, 256)));
		// Underground Gravel
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.MINABLE, new MinableConfig(MinableConfig.IS_ROCK, Blocks.GRAVEL.getDefaultState(), 33), Biome.COUNT_RANGE, new CountRangeConfig(8, 0, 0, 256)));
		// Underground Granite
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.MINABLE, new MinableConfig(MinableConfig.IS_ROCK, Blocks.GRANITE.getDefaultState(), 33), Biome.COUNT_RANGE, new CountRangeConfig(10, 0, 0, 80)));
		// Underground Diorite
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.MINABLE, new MinableConfig(MinableConfig.IS_ROCK, Blocks.DIORITE.getDefaultState(), 33), Biome.COUNT_RANGE, new CountRangeConfig(10, 0, 0, 80)));
		// Underground Andesite
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.MINABLE, new MinableConfig(MinableConfig.IS_ROCK, Blocks.ANDESITE.getDefaultState(), 33), Biome.COUNT_RANGE, new CountRangeConfig(10, 0, 0, 80)));
		// Coal Ore
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.MINABLE, new MinableConfig(MinableConfig.IS_ROCK, Blocks.COAL_ORE.getDefaultState(), 17), Biome.COUNT_RANGE, new CountRangeConfig(20, 0, 0, 128)));
		// Iron ore
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.MINABLE, new MinableConfig(MinableConfig.IS_ROCK, Blocks.IRON_ORE.getDefaultState(), 9), Biome.COUNT_RANGE, new CountRangeConfig(20, 0, 0, 64)));
		// Gold Ore
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.MINABLE, new MinableConfig(MinableConfig.IS_ROCK, Blocks.GOLD_ORE.getDefaultState(), 9), Biome.COUNT_RANGE, new CountRangeConfig(2, 0, 0, 32)));
		// Redstone Ore
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.MINABLE, new MinableConfig(MinableConfig.IS_ROCK, Blocks.REDSTONE_ORE.getDefaultState(), 8), Biome.COUNT_RANGE, new CountRangeConfig(8, 0, 0, 16)));
		// Diamond Ore
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.MINABLE, new MinableConfig(MinableConfig.IS_ROCK, Blocks.DIAMOND_ORE.getDefaultState(), 8), Biome.COUNT_RANGE, new CountRangeConfig(1, 0, 0, 16)));
		// Lapis Ore
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.MINABLE, new MinableConfig(MinableConfig.IS_ROCK, Blocks.LAPIS_ORE.getDefaultState(), 7), Biome.DEPTH_AVERAGE, new DepthAverageConfig(1, 16, 16)));
		// Emerald Ore (From MountainsBiome.class)
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.REPLACE_BLOCK, new ReplaceBlockConfig(BlockMatcher.forBlock(Blocks.STONE), Blocks.EMERALD_ORE.getDefaultState()), Biome.HEIGHT_4_TO_32, IPlacementConfig.NO_PLACEMENT_CONFIG));


		// Replacement
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.SPHERE_REPLACE, new SphereReplaceConfig(Blocks.SAND, 7, 2, Lists.newArrayList(Blocks.DIRT, Blocks.GRASS_BLOCK)), Biome.TOP_SOLID, new FrequencyConfig(3)));
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.SPHERE_REPLACE, new SphereReplaceConfig(Blocks.CLAY, 4, 1, Lists.newArrayList(Blocks.DIRT, Blocks.CLAY)), Biome.TOP_SOLID, new FrequencyConfig(1)));
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.SPHERE_REPLACE, new SphereReplaceConfig(Blocks.GRAVEL, 6, 2, Lists.newArrayList(Blocks.DIRT, Blocks.GRASS_BLOCK)), Biome.TOP_SOLID, new FrequencyConfig(1)));
	}

	public void addAllVegetal()
	{
		// Reeds
		this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createCompositeFeature(Feature.REED, IFeatureConfig.NO_FEATURE_CONFIG, Biome.TWICE_SURFACE, new FrequencyConfig(10)));
		// Pumpkins
		this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createCompositeFeature(Feature.PUMPKIN, IFeatureConfig.NO_FEATURE_CONFIG, Biome.TWICE_SURFACE_WITH_CHANCE, new ChanceConfig(32)));
		// Tall Grass (Increase Rarity)
		this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createCompositeFeature(Feature.TALL_GRASS, new TallGrassConfig(Blocks.GRASS.getDefaultState()), Biome.SURFACE_PLUS_32, new FrequencyConfig(2)));
		// Flowers
		this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createCompositeFlowerFeature(Feature.DEFAULT_FLOWERS, Biome.SURFACE_PLUS_32, new FrequencyConfig(2)));
		// Trees
		this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createCompositeFeature(Feature.TREE, FeatureRadiusConfig.NO_FEATURE_CONFIG,  Biome.AT_SURFACE_WITH_EXTRA, new AtSurfaceWithExtraConfig(3, 0.05F, 1)));

		//Melon
		this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, createCompositeFeature(Feature.MELON, IFeatureConfig.NO_FEATURE_CONFIG, TWICE_SURFACE_WITH_CHANCE, new ChanceConfig(48))); // chance was 32
		//this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, createCompositeFeature(Feature.MELON, IFeatureConfig.NO_FEATURE_CONFIG, SURFACE_PLUS_32, new FrequencyConfig(2))); // chance was 32
	}

	void addPassiveLandSpawns()
	{
		/* THEY ARENT SPAWNING MUCH */
		int passivePackSize = 5; // from 7
		this.addSpawn(EnumCreatureType.CREATURE, new SpawnListEntry(EntityType.SHEEP, 12, passivePackSize, passivePackSize));
		this.addSpawn(EnumCreatureType.CREATURE, new SpawnListEntry(EntityType.PIG, 10, 4, 4));
		this.addSpawn(EnumCreatureType.CREATURE, new SpawnListEntry(EntityType.CHICKEN, 10, 4, 4));
		this.addSpawn(EnumCreatureType.CREATURE, new SpawnListEntry(EntityType.COW, 8, 4, 4));
		this.addSpawn(EnumCreatureType.CREATURE, new SpawnListEntry(EntityType.HORSE, 5, 2, 6));
		this.addSpawn(EnumCreatureType.CREATURE, new SpawnListEntry(EntityType.DONKEY, 1, 1, 3));
		this.addSpawn(EnumCreatureType.AMBIENT, new Biome.SpawnListEntry(EntityType.BAT, 10, 8, 8));
	}

	void addPassiveOceanSpawns()
	{
		this.addSpawn(EnumCreatureType.WATER_CREATURE, new Biome.SpawnListEntry(EntityType.SQUID, 1, 1, 4));
		this.addSpawn(EnumCreatureType.WATER_CREATURE, new Biome.SpawnListEntry(EntityType.COD, 10, 3, 6));
		this.addSpawn(EnumCreatureType.WATER_CREATURE, new Biome.SpawnListEntry(EntityType.DOLPHIN, 1, 1, 2));
	}

	void addHostileSpawns()
	{
		this.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.SPIDER, 100, 4, 4));
		this.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.ZOMBIE, 95, 4, 4));
		this.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
		this.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.SKELETON, 100, 4, 4));
		this.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.CREEPER, 100, 4, 4));
		this.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.SLIME, 100, 4, 4));
		this.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.ENDERMAN, 10, 1, 4));
		this.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.WITCH, 5, 1, 1));
	}

	public void addAlphaOceanFeatures()
	{
		// Shipwreck
		this.addStructure(Feature.SHIPWRECK, new ShipwreckConfig(false));
		// Ruins
		this.addStructure(Feature.OCEAN_RUIN, new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.9F));
		// Add Ocean Monuments (Works?)
		this.addStructure(Feature.OCEAN_MONUMENT, new OceanMonumentConfig());

		// Underwater Ravine
		this.addCarver(GenerationStage.Carving.LIQUID, Biome.createWorldCarverWrapper(Biome.UNDERWATER_CANYON_WORLD_CARVER, new ProbabilityConfig(0.02F)));

		// Drowned
		this.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.DROWNED, 100, 4, 4));

		// Underwater Plants
		this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, createCompositeFeature(Feature.SEA_GRASS, new SeaGrassConfig(48, 0.8D), TOP_SOLID_ONCE, IPlacementConfig.NO_PLACEMENT_CONFIG));
		this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, createCompositeFeature(Feature.BLOCK_WITH_CONTEXT, new BlockWithContextConfig(Blocks.SEAGRASS.getDefaultState(), new IBlockState[]{Blocks.STONE.getDefaultState()}, new IBlockState[]{Blocks.WATER.getDefaultState()}, new IBlockState[]{Blocks.WATER.getDefaultState()}), CAVE_EDGE, new CaveEdgeConfig(GenerationStage.Carving.LIQUID, 0.1F)));
		this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, createCompositeFeature(Feature.KELP, IFeatureConfig.NO_FEATURE_CONFIG, TOP_SOLID_WITH_NOISE, new TopSolidWithNoiseConfig(120, 80.0D)));


		addDefaultStructures();
	}

	/* Adds All the features we need to Alpha Biomes */
	void addAlphaLandFeatures()
	{
		// Caves & Ravines
		this.addCarver(GenerationStage.Carving.AIR, Biome.createWorldCarverWrapper(Biome.CAVE_WORLD_CARVER, new ProbabilityConfig(0.06666667F)));
		this.addCarver(GenerationStage.Carving.AIR, Biome.createWorldCarverWrapper(Biome.CANYON_WORLD_CARVER, new ProbabilityConfig(0.02F)));

		// Mushrooms
		this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createCompositeFeature(Feature.BUSH, new BushConfig(Blocks.BROWN_MUSHROOM), Biome.TWICE_SURFACE_WITH_CHANCE, new ChanceConfig(4)));
		this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createCompositeFeature(Feature.BUSH, new BushConfig(Blocks.RED_MUSHROOM), Biome.TWICE_SURFACE_WITH_CHANCE, new ChanceConfig(8)));

		// Surface Water & Lava
		this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, createCompositeFeature(Feature.LIQUIDS, new LiquidsConfig(Fluids.WATER), HEIGHT_BIASED_RANGE, new CountRangeConfig(50, 8, 8, 256)));
		this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, createCompositeFeature(Feature.LIQUIDS, new LiquidsConfig(Fluids.LAVA), HEIGHT_VERY_BIASED_RANGE, new CountRangeConfig(20, 8, 16, 256)));


		// Structures
		addDefaultStructures();
		this.addStructure(Feature.WOODLAND_MANSION, new WoodlandMansionConfig());
	}

	private void addDefaultStructures()
	{

		this.addStructure(Feature.STRONGHOLD, new StrongholdConfig());
		this.addStructure(Feature.MINESHAFT, new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL));
	}
}
