package com.mrburgerus.betaplus.world.biome.alpha;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.feature.RandomDefaultFeatureListConfig;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.DungeonRoomConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.TopSolidWithNoiseConfig;
import net.minecraft.world.gen.surfacebuilders.CompositeSurfaceBuilder;

/* A Necessity to ensure Alpha Snow worlds get some fun stuff. */
public class BiomeAlphaFrozenOcean extends Biome
{
	public static final String name = "frozen_ocean";
	public BiomeAlphaFrozenOcean()
	{
		//super((new Biome.BiomeBuilder()).surfaceBuilder(new CompositeSurfaceBuilder<>(FROZEN_OCEAN_SURFACE_BUILDER, GRASS_DIRT_GRAVEL_SURFACE)).precipitation(Biome.RainType.SNOW).category(Biome.Category.OCEAN).temperature(0.0F).downfall(0.5F).waterColor(3750089).waterFogColor(329011).parent((String)null));
		super((new Biome.BiomeBuilder()).surfaceBuilder(new CompositeSurfaceBuilder<>(DEFAULT_SURFACE_BUILDER, GRASS_DIRT_GRAVEL_SURFACE)).precipitation(Biome.RainType.SNOW).category(Category.OCEAN).depth(-1.0F).scale(0.1F).temperature(0.0F).downfall(0.5F).waterColor(3750089).waterFogColor(329011).parent((String)null));
		this.addStructure(Feature.OCEAN_RUIN, new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.9F));
		this.addStructure(Feature.MINESHAFT, new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL));
		this.addStructure(Feature.SHIPWRECK, new ShipwreckConfig(false));
		this.addCarver(GenerationStage.Carving.AIR, createWorldCarverWrapper(CAVE_WORLD_CARVER, new ProbabilityConfig(0.06666667F)));
		this.addCarver(GenerationStage.Carving.AIR, createWorldCarverWrapper(CANYON_WORLD_CARVER, new ProbabilityConfig(0.02F)));
		this.addCarver(GenerationStage.Carving.LIQUID, createWorldCarverWrapper(UNDERWATER_CANYON_WORLD_CARVER, new ProbabilityConfig(0.02F)));
		this.addCarver(GenerationStage.Carving.LIQUID, createWorldCarverWrapper(UNDERWATER_CAVE_WORLD_CARVER, new ProbabilityConfig(0.06666667F)));
		this.addStructureFeatures();

		this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, createCompositeFeature(Feature.KELP, IFeatureConfig.NO_FEATURE_CONFIG, TOP_SOLID_WITH_NOISE, new TopSolidWithNoiseConfig(120, 80.0D)));
		/* Freezes Over Oceans */
		this.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, createCompositeFeature(Feature.ICE_AND_SNOW, IFeatureConfig.NO_FEATURE_CONFIG, PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));

		this.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, createCompositeFeature(Feature.DUNGEONS, IFeatureConfig.NO_FEATURE_CONFIG, DUNGEON_ROOM, new DungeonRoomConfig(8)));
		this.addSpawn(EnumCreatureType.WATER_CREATURE, new SpawnListEntry(EntityType.SQUID, 1, 1, 4));
		this.addSpawn(EnumCreatureType.WATER_CREATURE, new SpawnListEntry(EntityType.SALMON, 15, 1, 5));
		this.addSpawn(EnumCreatureType.CREATURE, new SpawnListEntry(EntityType.POLAR_BEAR, 1, 1, 2));
		this.addSpawn(EnumCreatureType.AMBIENT, new SpawnListEntry(EntityType.BAT, 10, 8, 8));
		this.addSpawn(EnumCreatureType.MONSTER, new SpawnListEntry(EntityType.SPIDER, 100, 4, 4));
		this.addSpawn(EnumCreatureType.MONSTER, new SpawnListEntry(EntityType.ZOMBIE, 95, 4, 4));
		this.addSpawn(EnumCreatureType.MONSTER, new SpawnListEntry(EntityType.DROWNED, 5, 1, 1));
		this.addSpawn(EnumCreatureType.MONSTER, new SpawnListEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
		this.addSpawn(EnumCreatureType.MONSTER, new SpawnListEntry(EntityType.SKELETON, 100, 4, 4));
		this.addSpawn(EnumCreatureType.MONSTER, new SpawnListEntry(EntityType.CREEPER, 100, 4, 4));
		this.addSpawn(EnumCreatureType.MONSTER, new SpawnListEntry(EntityType.SLIME, 100, 4, 4));
		this.addSpawn(EnumCreatureType.MONSTER, new SpawnListEntry(EntityType.ENDERMAN, 10, 1, 4));
		this.addSpawn(EnumCreatureType.MONSTER, new SpawnListEntry(EntityType.WITCH, 5, 1, 1));

	}

	/* To create perpetual Ice */
	@Override
	public float getTemperature(BlockPos p_180626_1_)
	{
		return this.getDefaultTemperature();
	}
}
