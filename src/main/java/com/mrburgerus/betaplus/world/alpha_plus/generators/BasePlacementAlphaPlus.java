package com.mrburgerus.betaplus.world.alpha_plus.generators;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.BasePlacement;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;

import java.util.Random;

public class BasePlacementAlphaPlus extends BasePlacement<NoPlacementConfig>
{

	@Override
	public <C extends IFeatureConfig> boolean generate(IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, NoPlacementConfig placementConfig, Feature<C> featureIn, C featureConfig)
	{
		featureIn.func_212245_a(worldIn, chunkGenerator, random, pos, featureConfig);

		return true;
	}
}
