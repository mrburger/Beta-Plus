package com.mrburgerus.betaplus.world.biome.alpha;

import com.mrburgerus.betaplus.world.biome.BiomeHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.*;
import net.minecraft.world.gen.surfacebuilders.CompositeSurfaceBuilder;

/* Completed Feb 24, 2019 */
public class BiomeAlphaFrozenLand extends Biome
{
	public static final String NAME = "alpha_frozen_hills";
	public BiomeAlphaFrozenLand()
	{
		/* Woo! Explicit Declarations */
		super((new Biome.BiomeBuilder()).surfaceBuilder(new CompositeSurfaceBuilder<>(DEFAULT_SURFACE_BUILDER, GRASS_DIRT_SAND_SURFACE)).precipitation(RainType.SNOW).category(Category.PLAINS).depth(0.0F).scale(0.1F).temperature(0.0F).downfall(100.0F).waterColor(3750089).waterFogColor(329011).parent((String)null));

		// Create Snow
		this.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, createCompositeFeature(Feature.ICE_AND_SNOW, IFeatureConfig.NO_FEATURE_CONFIG, PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));

		//Create Ores
		BiomeHelper.addAllOres(this);
		// Add structures
		this.addStructureFeatures();
		// Add Trees, Caves, features.
		BiomeHelper.addAlphaLandFeatures(this);
		// Add vegetation
		BiomeHelper.addAllVegetal(this);

		// Standard Spawns
		BiomeHelper.addPassiveLandSpawns(this);
		BiomeHelper.addHostileSpawns(this);
		// Add Polar Bears
		this.addSpawn(EnumCreatureType.CREATURE, new SpawnListEntry(EntityType.POLAR_BEAR, 1, 1, 2));

		// Add Dungeons
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, createCompositeFeature(Feature.DUNGEONS, IFeatureConfig.NO_FEATURE_CONFIG, DUNGEON_ROOM, new DungeonRoomConfig(8)));
	}
}
