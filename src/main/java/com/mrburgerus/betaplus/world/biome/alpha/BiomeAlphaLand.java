package com.mrburgerus.betaplus.world.biome.alpha;

import com.google.common.collect.Lists;
import com.mrburgerus.betaplus.world.alpha_plus.generators.BasePlacementAlphaPlus;
import com.mrburgerus.betaplus.world.biome.BiomeHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.placement.*;
import net.minecraft.world.gen.surfacebuilders.CompositeSurfaceBuilder;

public class BiomeAlphaLand extends Biome
{
	public static final String NAME = "alpha_hills";
	public BiomeAlphaLand()
	{
		/* Land of Alpha, how beautiful. */
		super((new Biome.BiomeBuilder()).surfaceBuilder(new CompositeSurfaceBuilder<>(DEFAULT_SURFACE_BUILDER, GRASS_DIRT_SAND_SURFACE)).precipitation(RainType.RAIN).category(Category.PLAINS).depth(0.0F).scale(0.1F).temperature(0.5F).downfall(0.5F).waterColor(3750089).waterFogColor(329011).parent((String)null));

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

		// Add Dungeons
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, createCompositeFeature(Feature.DUNGEONS, IFeatureConfig.NO_FEATURE_CONFIG, DUNGEON_ROOM, new DungeonRoomConfig(8)));
	}
}