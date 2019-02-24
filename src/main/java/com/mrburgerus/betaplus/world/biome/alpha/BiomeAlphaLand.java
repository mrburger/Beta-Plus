package com.mrburgerus.betaplus.world.biome.alpha;

import com.google.common.collect.Lists;
import com.mrburgerus.betaplus.world.alpha_plus.generators.BasePlacementAlphaPlus;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.placement.AtSurfaceRandomCount;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.surfacebuilders.CompositeSurfaceBuilder;

public class BiomeAlphaLand extends Biome
{
	public static final String name = "alpha_hills";
	public BiomeAlphaLand()
	{
		/* Copied from Alpha Frozen Ocean */
		super((new Biome.BiomeBuilder()).surfaceBuilder(new CompositeSurfaceBuilder<>(DEFAULT_SURFACE_BUILDER, GRASS_DIRT_SAND_SURFACE)).precipitation(RainType.RAIN).category(Category.PLAINS).depth(0.0F).scale(0.1F).temperature(0.5F).downfall(0.5F).waterColor(3750089).waterFogColor(329011).parent((String)null));
		this.addStructure(Feature.MINESHAFT, new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL));
		this.addCarver(GenerationStage.Carving.AIR, createWorldCarverWrapper(CAVE_WORLD_CARVER, new ProbabilityConfig(0.06666667F)));
		this.addCarver(GenerationStage.Carving.AIR, createWorldCarverWrapper(CANYON_WORLD_CARVER, new ProbabilityConfig(0.02F)));
		this.addStructureFeatures();
		this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, createCompositeFeature(Feature.TREE, FeatureRadiusConfig.NO_FEATURE_CONFIG,  AT_SURFACE_WITH_EXTRA, new AtSurfaceWithExtraConfig(3, 0.05F, 1)));
		// Create a good amount of clay.
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, createCompositeFeature(Feature.SPHERE_REPLACE, new SphereReplaceConfig(Blocks.CLAY, 4, 1, Lists.newArrayList(Blocks.DIRT, Blocks.CLAY)), TOP_SOLID, new FrequencyConfig(1)));

		this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, createCompositeFeature(Feature.REED, IFeatureConfig.NO_FEATURE_CONFIG, TWICE_SURFACE, new FrequencyConfig(10)));


	}
}
