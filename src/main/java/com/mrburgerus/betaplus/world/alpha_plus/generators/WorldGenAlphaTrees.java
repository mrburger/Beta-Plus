package com.mrburgerus.betaplus.world.alpha_plus.generators;

import net.minecraft.block.trees.AbstractTree;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.BigTreeFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;

import javax.annotation.Nullable;
import java.util.Random;


public class WorldGenAlphaTrees extends AbstractTree
{
	//Fields
	private int minTreeHeight = 4;

	public WorldGenAlphaTrees()
	{
		super();
	}

	protected int getHeight(Random p_208534_1_) {
		return this.minTreeHeight + p_208534_1_.nextInt(3);
	}

	@Nullable
	@Override
	protected AbstractTreeFeature<NoFeatureConfig> getTreeFeature(Random random)
	{
		return (AbstractTreeFeature<NoFeatureConfig>)(random.nextInt(10) == 0 ? new BigTreeFeature(true) : new TreeFeature(true));
	}
}
