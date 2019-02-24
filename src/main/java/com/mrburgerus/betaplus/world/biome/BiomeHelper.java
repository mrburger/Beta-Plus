package com.mrburgerus.betaplus.world.biome;

import com.google.common.collect.Lists;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.placement.*;

public class BiomeHelper
{
	public static void addAllOres(Biome biome)
	{
		// Underground Dirt
		biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.MINABLE, new MinableConfig(MinableConfig.IS_ROCK, Blocks.DIRT.getDefaultState(), 33), Biome.COUNT_RANGE, new CountRangeConfig(10, 0, 0, 256)));
		// Underground Gravel
		biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.MINABLE, new MinableConfig(MinableConfig.IS_ROCK, Blocks.GRAVEL.getDefaultState(), 33), Biome.COUNT_RANGE, new CountRangeConfig(8, 0, 0, 256)));
		// Underground Granite
		biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.MINABLE, new MinableConfig(MinableConfig.IS_ROCK, Blocks.GRANITE.getDefaultState(), 33), Biome.COUNT_RANGE, new CountRangeConfig(10, 0, 0, 80)));
		// Underground Diorite
		biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.MINABLE, new MinableConfig(MinableConfig.IS_ROCK, Blocks.DIORITE.getDefaultState(), 33), Biome.COUNT_RANGE, new CountRangeConfig(10, 0, 0, 80)));
		// Underground Andesite
		biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.MINABLE, new MinableConfig(MinableConfig.IS_ROCK, Blocks.ANDESITE.getDefaultState(), 33), Biome.COUNT_RANGE, new CountRangeConfig(10, 0, 0, 80)));
		// Coal Ore
		biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.MINABLE, new MinableConfig(MinableConfig.IS_ROCK, Blocks.COAL_ORE.getDefaultState(), 17), Biome.COUNT_RANGE, new CountRangeConfig(20, 0, 0, 128)));
		// Iron ore
		biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.MINABLE, new MinableConfig(MinableConfig.IS_ROCK, Blocks.IRON_ORE.getDefaultState(), 9), Biome.COUNT_RANGE, new CountRangeConfig(20, 0, 0, 64)));
		// Gold Ore
		biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.MINABLE, new MinableConfig(MinableConfig.IS_ROCK, Blocks.GOLD_ORE.getDefaultState(), 9), Biome.COUNT_RANGE, new CountRangeConfig(2, 0, 0, 32)));
		// Redstone Ore
		biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.MINABLE, new MinableConfig(MinableConfig.IS_ROCK, Blocks.REDSTONE_ORE.getDefaultState(), 8), Biome.COUNT_RANGE, new CountRangeConfig(8, 0, 0, 16)));
		// Diamond Ore
		biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.MINABLE, new MinableConfig(MinableConfig.IS_ROCK, Blocks.DIAMOND_ORE.getDefaultState(), 8), Biome.COUNT_RANGE, new CountRangeConfig(1, 0, 0, 16)));
		// Lapis Ore
		biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.MINABLE, new MinableConfig(MinableConfig.IS_ROCK, Blocks.LAPIS_ORE.getDefaultState(), 7), Biome.DEPTH_AVERAGE, new DepthAverageConfig(1, 16, 16)));
		// Emerald Ore (From MountainsBiome.class)
		biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.REPLACE_BLOCK, new ReplaceBlockConfig(BlockMatcher.forBlock(Blocks.STONE), Blocks.EMERALD_ORE.getDefaultState()), Biome.HEIGHT_4_TO_32, IPlacementConfig.NO_PLACEMENT_CONFIG));


		// Replacement
		biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.SPHERE_REPLACE, new SphereReplaceConfig(Blocks.SAND, 7, 2, Lists.newArrayList(Blocks.DIRT, Blocks.GRASS_BLOCK)), Biome.TOP_SOLID, new FrequencyConfig(3)));
		biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.SPHERE_REPLACE, new SphereReplaceConfig(Blocks.CLAY, 4, 1, Lists.newArrayList(Blocks.DIRT, Blocks.CLAY)), Biome.TOP_SOLID, new FrequencyConfig(1)));
		biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createCompositeFeature(Feature.SPHERE_REPLACE, new SphereReplaceConfig(Blocks.GRAVEL, 6, 2, Lists.newArrayList(Blocks.DIRT, Blocks.GRASS_BLOCK)), Biome.TOP_SOLID, new FrequencyConfig(1)));
	}

	public static void addAllVegetal(Biome biome)
	{
		// Reeds
		biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createCompositeFeature(Feature.REED, IFeatureConfig.NO_FEATURE_CONFIG, Biome.TWICE_SURFACE, new FrequencyConfig(10)));
		// Pumpkins
		biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createCompositeFeature(Feature.PUMPKIN, IFeatureConfig.NO_FEATURE_CONFIG, Biome.TWICE_SURFACE_WITH_CHANCE, new ChanceConfig(32)));
		// Tall Grass (Increase Rarity)
		biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createCompositeFeature(Feature.TALL_GRASS, new TallGrassConfig(Blocks.GRASS.getDefaultState()), Biome.SURFACE_PLUS_32, new FrequencyConfig(2)));
		// Flowers
		biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createCompositeFlowerFeature(Feature.DEFAULT_FLOWERS, Biome.SURFACE_PLUS_32, new FrequencyConfig(2)));
		// Trees
		biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createCompositeFeature(Feature.TREE, FeatureRadiusConfig.NO_FEATURE_CONFIG,  Biome.AT_SURFACE_WITH_EXTRA, new AtSurfaceWithExtraConfig(3, 0.05F, 1)));

	}

	public static void addPassiveLandSpawns(Biome biome)
	{
		int passivePackSize = 7;
		biome.addSpawn(EnumCreatureType.CREATURE, new Biome.SpawnListEntry(EntityType.SHEEP, 12, passivePackSize, passivePackSize));
		biome.addSpawn(EnumCreatureType.CREATURE, new Biome.SpawnListEntry(EntityType.PIG, 10, passivePackSize, passivePackSize));
		biome.addSpawn(EnumCreatureType.CREATURE, new Biome.SpawnListEntry(EntityType.CHICKEN, 10, passivePackSize, passivePackSize));
		biome.addSpawn(EnumCreatureType.CREATURE, new Biome.SpawnListEntry(EntityType.COW, 8, passivePackSize, passivePackSize));
		biome.addSpawn(EnumCreatureType.CREATURE, new Biome.SpawnListEntry(EntityType.WOLF, 5, passivePackSize, passivePackSize));
		biome.addSpawn(EnumCreatureType.AMBIENT, new Biome.SpawnListEntry(EntityType.BAT, 10, 8, 8));
	}

	public static void addPassiveOceanSpawns(Biome biome)
	{
		biome.addSpawn(EnumCreatureType.WATER_CREATURE, new Biome.SpawnListEntry(EntityType.SQUID, 1, 1, 4));
		biome.addSpawn(EnumCreatureType.WATER_CREATURE, new Biome.SpawnListEntry(EntityType.COD, 10, 3, 6));
		biome.addSpawn(EnumCreatureType.WATER_CREATURE, new Biome.SpawnListEntry(EntityType.DOLPHIN, 1, 1, 2));
	}

	public static void addHostileSpawns(Biome biome)
	{
		biome.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.SPIDER, 100, 4, 4));
		biome.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.ZOMBIE, 95, 4, 4));
		biome.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
		biome.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.SKELETON, 100, 4, 4));
		biome.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.CREEPER, 100, 4, 4));
		biome.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.SLIME, 100, 4, 4));
		biome.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.ENDERMAN, 10, 1, 4));
		biome.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.WITCH, 5, 1, 1));
	}

	public static void addAlphaOceanFeatures(Biome biome)
	{
		// Shipwreck
		biome.addStructure(Feature.SHIPWRECK, new ShipwreckConfig(false));
		biome.addStructure(Feature.OCEAN_RUIN, new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.9F));

		// Underwater Ravine
		biome.addCarver(GenerationStage.Carving.LIQUID, Biome.createWorldCarverWrapper(Biome.UNDERWATER_CANYON_WORLD_CARVER, new ProbabilityConfig(0.02F)));

	}

	/* Adds All the features we need to Alpha Biomes */
	public static void addAlphaLandFeatures(Biome biome)
	{
		// Caves & Ravines
		biome.addCarver(GenerationStage.Carving.AIR, Biome.createWorldCarverWrapper(Biome.CAVE_WORLD_CARVER, new ProbabilityConfig(0.06666667F)));
		biome.addCarver(GenerationStage.Carving.AIR, Biome.createWorldCarverWrapper(Biome.CANYON_WORLD_CARVER, new ProbabilityConfig(0.02F)));

		// Mushrooms
		biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createCompositeFeature(Feature.BUSH, new BushConfig(Blocks.BROWN_MUSHROOM), Biome.TWICE_SURFACE_WITH_CHANCE, new ChanceConfig(4)));
		biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createCompositeFeature(Feature.BUSH, new BushConfig(Blocks.RED_MUSHROOM), Biome.TWICE_SURFACE_WITH_CHANCE, new ChanceConfig(8)));

	}
}
