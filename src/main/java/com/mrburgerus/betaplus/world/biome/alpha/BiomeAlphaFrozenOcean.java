package com.mrburgerus.betaplus.world.biome.alpha;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.DungeonRoomConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.surfacebuilders.CompositeSurfaceBuilder;

/* A Necessity to ensure Alpha Snow worlds get some fun stuff. */
public class BiomeAlphaFrozenOcean extends AbstractAlphaBiome
{
	public static final String NAME = "alpha_frozen_ocean";
	public BiomeAlphaFrozenOcean()
	{
		super((new Biome.BiomeBuilder()).surfaceBuilder(new CompositeSurfaceBuilder<>(DEFAULT_SURFACE_BUILDER, GRASS_DIRT_GRAVEL_SURFACE)).precipitation(Biome.RainType.SNOW).category(Category.OCEAN).depth(0.0F).scale(0.0F).temperature(0.0F).downfall(10.0F).waterColor(3750089).waterFogColor(329011).parent((String)null));

		// Create Frozen Feature
		this.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, createCompositeFeature(Feature.ICE_AND_SNOW, IFeatureConfig.NO_FEATURE_CONFIG, PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));

		//Create Ores
		this.addAllOres();
		// Add structures
		this.addStructureFeatures();
		// Add Ocean Features
		this.addAlphaOceanFeatures();


		// Add Spawns
		this.addPassiveOceanSpawns();
		this.addHostileSpawns();
		// Add Polar Bears
		this.addSpawn(EnumCreatureType.CREATURE, new SpawnListEntry(EntityType.POLAR_BEAR, 1, 1, 2));

		// Add Dungeons
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, createCompositeFeature(Feature.DUNGEONS, IFeatureConfig.NO_FEATURE_CONFIG, DUNGEON_ROOM, new DungeonRoomConfig(8)));
	}

	/* To create perpetual Ice */
	@Override
	public float getTemperature(BlockPos p_180626_1_)
	{
		return this.getDefaultTemperature();
	}
}
