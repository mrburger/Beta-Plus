package com.mrburgerus.betaplus.world.biome.alpha;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.DungeonRoomConfig;
import net.minecraft.world.gen.surfacebuilders.CompositeSurfaceBuilder;

public class BiomeAlphaLand extends AbstractAlphaBiome
{
	public static final String NAME = "alpha_hills";
	public BiomeAlphaLand()
	{
		/* Land of Alpha, how beautiful. */
		super((new Biome.BiomeBuilder()).surfaceBuilder(new CompositeSurfaceBuilder<>(DEFAULT_SURFACE_BUILDER, GRASS_DIRT_SAND_SURFACE)).precipitation(RainType.RAIN).category(Category.PLAINS).depth(0.0F).scale(0.1F).temperature(0.5F).downfall(0.5F).waterColor(WATER_COLOR).waterFogColor(329011).parent((String)null));

		//Create Ores
		this.addAllOres();
		// Add structures
		this.addStructureFeatures();
		// Add Trees, Caves, features.
		this.addAlphaLandFeatures();
		// Add vegetation
		this.addAllVegetal();

		// Standard Spawns
		this.addPassiveLandSpawns();
		this.addHostileSpawns();

		// Add Dungeons
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, createCompositeFeature(Feature.DUNGEONS, IFeatureConfig.NO_FEATURE_CONFIG, DUNGEON_ROOM, new DungeonRoomConfig(8)));

	}
}
