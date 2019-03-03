package com.mrburgerus.betaplus.world.biome.alpha;

import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.DungeonRoomConfig;
import net.minecraft.world.gen.surfacebuilders.CompositeSurfaceBuilder;

public class BiomeAlphaOcean extends AbstractAlphaBiome
{
	public static final String NAME = "alpha_ocean";
	public BiomeAlphaOcean()
	{
		super((new BiomeBuilder()).surfaceBuilder(new CompositeSurfaceBuilder<>(DEFAULT_SURFACE_BUILDER, GRASS_DIRT_GRAVEL_SURFACE)).precipitation(RainType.RAIN).category(Category.OCEAN).depth(0.0F).scale(0.0F).temperature(0.5F).downfall(0.5F).waterColor(3750089).waterFogColor(329011).parent((String)null));

		//Create Ores
		this.addAllOres();
		// Add structures
		this.addStructureFeatures();
		// Add Ocean Features
		this.addAlphaOceanFeatures();

		// Add Spawns
		this.addPassiveOceanSpawns();
		this.addHostileSpawns();

		// Add Dungeons
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, createCompositeFeature(Feature.DUNGEONS, IFeatureConfig.NO_FEATURE_CONFIG, DUNGEON_ROOM, new DungeonRoomConfig(8)));
	}
}
