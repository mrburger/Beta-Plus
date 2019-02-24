package com.mrburgerus.betaplus.world.biome.alpha;

import com.mrburgerus.betaplus.world.biome.BiomeHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.placement.DungeonRoomConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.TopSolidWithNoiseConfig;
import net.minecraft.world.gen.surfacebuilders.CompositeSurfaceBuilder;

public class BiomeAlphaOcean extends Biome
{
	public static final String NAME = "alpha_ocean";
	public BiomeAlphaOcean()
	{
		super((new BiomeBuilder()).surfaceBuilder(new CompositeSurfaceBuilder<>(DEFAULT_SURFACE_BUILDER, GRASS_DIRT_GRAVEL_SURFACE)).precipitation(RainType.RAIN).category(Category.OCEAN).depth(-1.8F).scale(0.1F).temperature(0.5F).downfall(0.5F).waterColor(4159204).waterFogColor(329011).parent((String)null));

		//Create Ores
		BiomeHelper.addAllOres(this);
		// Add structures
		this.addStructureFeatures();
		// Add Ocean Features
		BiomeHelper.addAlphaOceanFeatures(this);

		// Add Spawns
		BiomeHelper.addPassiveOceanSpawns(this);
		BiomeHelper.addHostileSpawns(this);

		// Add Dungeons
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, createCompositeFeature(Feature.DUNGEONS, IFeatureConfig.NO_FEATURE_CONFIG, DUNGEON_ROOM, new DungeonRoomConfig(8)));

	}
}
